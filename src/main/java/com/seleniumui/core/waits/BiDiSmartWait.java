package com.seleniumui.core.waits;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.bidi.BiDi;
import org.openqa.selenium.bidi.Command;
import org.openqa.selenium.bidi.Event;
import org.openqa.selenium.bidi.log.ConsoleLogEntry;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class BiDiSmartWait implements AutoCloseable {
    private final BiDi biDi;
    private final WebDriver driver;
    private final long defaultTimeoutSeconds;
    private final ScheduledExecutorService scheduler;
    private final Map<String, CompletableFuture<?>> pendingFutures;
    private final Map<Long, Event<?>> eventListeners;

    // DOM stability tracking
    private final AtomicReference<String> lastDomHash = new AtomicReference<>("");
    private final AtomicLong lastDomChangeTime = new AtomicLong(System.currentTimeMillis());
    private final AtomicInteger domChangeCount = new AtomicInteger(0);
    private ScheduledFuture<?> domStabilityChecker;

    // Network tracking
    private final AtomicInteger activeRequestCount = new AtomicInteger(0);
    private final AtomicLong lastNetworkActivityTime = new AtomicLong(System.currentTimeMillis());
    private boolean networkMonitoringEnabled = false;
    private final AtomicBoolean isShutdown = new AtomicBoolean(false);

    // Console message filtering
    private final Map<String, Predicate<ConsoleLogEntry>> consoleFilters;

    // Exception tracking
    private final List<Map<String, Object>> caughtExceptions;

    public BiDiSmartWait(WebDriver driver, BiDi biDi, long defaultTimeoutSeconds) {
        this.driver = driver;
        this.biDi = biDi;
        this.defaultTimeoutSeconds = defaultTimeoutSeconds;
        this.scheduler = Executors.newScheduledThreadPool(2, new ThreadFactory() {
            private final AtomicInteger threadCount = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, "bidi-smartwait-" + threadCount.incrementAndGet());
                thread.setDaemon(true);
                return thread;
            }
        });
        this.pendingFutures = new ConcurrentHashMap<>();
        this.eventListeners = new ConcurrentHashMap<>();
        this.consoleFilters = new ConcurrentHashMap<>();
        this.caughtExceptions = new CopyOnWriteArrayList<>();

        initializeEventListeners();
    }

    private void initializeEventListeners() {
        try {
            // Subscribe to console events with proper typing
//            Event<Map<String, Object>> consoleEvent = new Event<>("log.entryAdded", input -> input);
//            long consoleListenerId = biDi.addListener(consoleEvent, this::handleConsoleEvent);
//            eventListeners.put(consoleListenerId, consoleEvent);

            // Subscribe to DOM content loaded for resetting states
            Event<Map<String, Object>> domContentEvent = new Event<>("browsingContext.domContentLoaded", input -> input);
            long domContentListenerId = biDi.addListener(domContentEvent, this::handleDomContentLoaded);
            eventListeners.put(domContentListenerId, domContentEvent);

            // Start DOM stability monitoring
            startDomStabilityMonitoring();

            // Enable network monitoring if available
            enableNetworkMonitoring();

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize BiDi event listeners", e);
        }
    }

    private void handleConsoleEvent(Map<String, Object> consoleData) {
        String type = (String) consoleData.get("type");
        String text = (String) consoleData.get("text");

        System.out.println("Console " + type + ": " + text);

        // Notify console wait conditions
        pendingFutures.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("console:"))
                .forEach(entry -> {
                    @SuppressWarnings("unchecked")
                    CompletableFuture<Map<String, Object>> future =
                            (CompletableFuture<Map<String, Object>>) entry.getValue();
                    if (!future.isDone()) {
                        future.complete(consoleData);
                    }
                });
    }

    private void handleJsException(Map<String, Object> exceptionData) {
        String exceptionText = extractExceptionDetails(exceptionData);
        System.err.println("JavaScript Exception: " + exceptionText);
        caughtExceptions.add(exceptionData);

        // Notify exception wait conditions
        pendingFutures.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("exception:"))
                .forEach(entry -> {
                    @SuppressWarnings("unchecked")
                    CompletableFuture<Map<String, Object>> future =
                            (CompletableFuture<Map<String, Object>>) entry.getValue();
                    if (!future.isDone()) {
                        future.complete(exceptionData);
                    }
                });
    }

    private void handleDomContentLoaded(Map<String, Object> domData) {
        // Reset DOM monitoring state on navigation
        lastDomHash.set("");
        domChangeCount.set(0);
        resetNetworkMonitoring();
    }

    private String extractExceptionDetails(Map<String, Object> exceptionData) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> exceptionDetails = (Map<String, Object>) exceptionData.get("exceptionDetails");
            if (exceptionDetails != null) {
                return String.valueOf(exceptionDetails.get("text"));
            }
            return String.valueOf(exceptionData.get("text"));
        } catch (Exception e) {
            return "Unable to extract exception details";
        }
    }

    private void startDomStabilityMonitoring() {
        domStabilityChecker = scheduler.scheduleAtFixedRate(() -> {
            try {
                String currentHash = calculateDomHash();
                String previousHash = lastDomHash.get();

                if (!currentHash.equals(previousHash)) {
                    lastDomHash.set(currentHash);
                    lastDomChangeTime.set(System.currentTimeMillis());
                    domChangeCount.incrementAndGet();

                    // Notify DOM change listeners
                    notifyDomChangeListeners();
                }
            } catch (Exception e) {
                // Log but don't break the monitoring
                System.err.println("DOM monitoring error: " + e.getMessage());
            }
        }, 0, 200, TimeUnit.MILLISECONDS); // Reduced frequency for better performance
    }

    private void notifyDomChangeListeners() {
        pendingFutures.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("domchange:"))
                .forEach(entry -> {
                    @SuppressWarnings("unchecked")
                    CompletableFuture<Long> future = (CompletableFuture<Long>) entry.getValue();
                    if (!future.isDone()) {
                        future.complete(lastDomChangeTime.get());
                    }
                });
    }

    private String calculateDomHash() {
        try {
            // More efficient DOM state calculation
            Object result = ((JavascriptExecutor) driver).executeScript(
                    "return document.readyState + '|' + document.body.children.length + '|' + " +
                            "Array.from(document.querySelectorAll('*')).filter(el => el.tagName.match(/^[A-Z]/)).length"
            );
            return String.valueOf(result).hashCode() + "";
        } catch (Exception e) {
            return "error_" + System.currentTimeMillis();
        }
    }

    private void enableNetworkMonitoring() {
        try {
            // Try to use BiDi network events if available
            Event<Map<String, Object>> networkRequestEvent = new Event<>("network.beforeRequestSent", input -> input);
            Event<Map<String, Object>> networkResponseEvent = new Event<>("network.responseCompleted", input -> input);

            long requestListenerId = biDi.addListener(networkRequestEvent,
                    event -> activeRequestCount.incrementAndGet());
            long responseListenerId = biDi.addListener(networkResponseEvent,
                    event -> activeRequestCount.decrementAndGet());

            eventListeners.put(requestListenerId, networkRequestEvent);
            eventListeners.put(responseListenerId, networkResponseEvent);
            networkMonitoringEnabled = true;
            System.out.println("BiDi network monitoring enabled");
        } catch (Exception e) {
            System.out.println("BiDi network events not available, falling back to JavaScript monitoring");
            injectNetworkMonitoringScript();
        }
    }

    private void resetNetworkMonitoring() {
        if (!networkMonitoringEnabled) {
            injectNetworkMonitoringScript();
        }
        activeRequestCount.set(0);
    }

    private void injectNetworkMonitoringScript() {
        String script =
                "if (typeof window.__smartWaitActiveRequests === 'undefined') {" +
                        "  window.__smartWaitActiveRequests = 0;" +
                        "  const originalFetch = window.fetch;" +
                        "  window.fetch = function(...args) {" +
                        "    window.__smartWaitActiveRequests++;" +
                        "    const startTime = Date.now();" +
                        "    return originalFetch.apply(this, args).finally(() => {" +
                        "      window.__smartWaitActiveRequests--;" +
                        "    });" +
                        "  };" +
                        "  const OriginalXHR = window.XMLHttpRequest;" +
                        "  class MonitoredXHR extends OriginalXHR {" +
                        "    open(...args) {" +
                        "      window.__smartWaitActiveRequests++;" +
                        "      this.addEventListener('loadend', () => {" +
                        "        window.__smartWaitActiveRequests--;" +
                        "      });" +
                        "      super.open(...args);" +
                        "    }" +
                        "  }" +
                        "  window.XMLHttpRequest = MonitoredXHR;" +
                        "  console.log('Network monitoring injected');" +
                        "}" +
                        "return window.__smartWaitActiveRequests;";

        try {
            ((JavascriptExecutor) driver).executeScript(script);
        } catch (Exception e) {
            System.err.println("Failed to inject network monitoring: " + e.getMessage());
        }
    }

    public enum WaitCondition {
        CONSOLE_MESSAGE,
        JAVASCRIPT_EXCEPTION,
        DOM_STABLE,
        DOM_READY,
        NETWORK_IDLE,
        DOM_CHANGE,
        CUSTOM
    }

    // Enhanced console message waiting with filtering
    public CompletableFuture<ConsoleLogEntry> waitForConsoleMessage(
            Predicate<ConsoleLogEntry> filter) {
        return waitForConsoleMessage(filter, defaultTimeoutSeconds, TimeUnit.SECONDS);
    }

    public CompletableFuture<ConsoleLogEntry> waitForConsoleMessage(
            Predicate<ConsoleLogEntry> filter, long timeout, TimeUnit unit) {

        String key = "console:" + System.currentTimeMillis() + ":" + UUID.randomUUID();
        CompletableFuture<ConsoleLogEntry> future = new CompletableFuture<>();

        consoleFilters.put(key, filter);
        pendingFutures.put(key, future);

        return future.orTimeout(timeout, unit)
                .whenComplete((result, error) -> {
                    pendingFutures.remove(key);
                    consoleFilters.remove(key);
                    if (error instanceof TimeoutException) {
                        System.out.println("Timeout waiting for console message matching filter");
                    }
                });
    }

    // Overload for simple text matching
    public CompletableFuture<ConsoleLogEntry> waitForConsoleMessage(String expectedText) {
        return waitForConsoleMessage(entry ->
                entry.getText() != null && entry.getText().contains(expectedText));
    }

    public CompletableFuture<ConsoleLogEntry> waitForConsoleMessage(
            String expectedText, long timeout, TimeUnit unit) {
        return waitForConsoleMessage(entry ->
                entry.getText() != null && entry.getText().contains(expectedText), timeout, unit);
    }

    // Wait for JavaScript exception with optional filtering
    public CompletableFuture<Map<String, Object>> waitForJavascriptException() {
        return waitForJavascriptException(defaultTimeoutSeconds, TimeUnit.SECONDS);
    }

    public CompletableFuture<Map<String, Object>> waitForJavascriptException(
            long timeout, TimeUnit unit) {

        String key = "exception:" + System.currentTimeMillis() + ":" + UUID.randomUUID();
        CompletableFuture<Map<String, Object>> future = new CompletableFuture<>();

        pendingFutures.put(key, future);

        return future.orTimeout(timeout, unit)
                .whenComplete((result, error) -> pendingFutures.remove(key));
    }

    // Fixed DOM stability waiting
    public CompletableFuture<Void> waitForDomStable(long stabilityThresholdMs) {
        return waitForDomStable(stabilityThresholdMs, defaultTimeoutSeconds, TimeUnit.SECONDS);
    }

    public CompletableFuture<Void> waitForDomStable(
            long stabilityThresholdMs, long timeout, TimeUnit unit) {

        return waitForCondition(
                () -> {
                    long timeSinceLastChange = System.currentTimeMillis() - lastDomChangeTime.get();
                    return timeSinceLastChange >= stabilityThresholdMs;
                },
                "DOM stable for " + stabilityThresholdMs + "ms",
                timeout,
                unit
        );
    }

    // Wait for DOM change
    public CompletableFuture<Long> waitForDomChange() {
        return waitForDomChange(defaultTimeoutSeconds, TimeUnit.SECONDS);
    }

    public CompletableFuture<Long> waitForDomChange(long timeout, TimeUnit unit) {
        String key = "domchange:" + System.currentTimeMillis() + ":" + UUID.randomUUID();
        CompletableFuture<Long> future = new CompletableFuture<>();

        pendingFutures.put(key, future);

        return future.orTimeout(timeout, unit)
                .whenComplete((result, error) -> pendingFutures.remove(key));
    }

    // DOM ready state
    public CompletableFuture<Void> waitForDomReady() {
        return waitForCondition(
                () -> {
                    try {
                        Object result = ((JavascriptExecutor) driver)
                                .executeScript("return document.readyState");
                        return "complete".equals(result);
                    } catch (Exception e) {
                        return false;
                    }
                },
                "DOM ready state complete",
                defaultTimeoutSeconds,
                TimeUnit.SECONDS
        );
    }

    // Enhanced network idle monitoring
    public CompletableFuture<Void> waitForNetworkIdle(long idleThresholdMs) {
        return waitForNetworkIdle(idleThresholdMs, defaultTimeoutSeconds, TimeUnit.SECONDS);
    }

    public CompletableFuture<Void> waitForNetworkIdle(
            long idleThresholdMs, long timeout, TimeUnit unit) {

        if (!networkMonitoringEnabled) {
            injectNetworkMonitoringScript();
        }

        final AtomicLong lastRequestTime = new AtomicLong(System.currentTimeMillis());

        return waitForCondition(
                () -> {
                    try {
                        int activeRequests;
                        if (networkMonitoringEnabled) {
                            activeRequests = activeRequestCount.get();
                        } else {
                            Object result = ((JavascriptExecutor) driver)
                                    .executeScript("return window.__smartWaitActiveRequests || 0");
                            activeRequests = ((Number) result).intValue();
                        }

                        if (activeRequests > 0) {
                            lastRequestTime.set(System.currentTimeMillis());
                            return false;
                        }

                        return System.currentTimeMillis() - lastRequestTime.get() >= idleThresholdMs;
                    } catch (Exception e) {
                        return false;
                    }
                },
                "Network idle for " + idleThresholdMs + "ms",
                timeout,
                unit
        );
    }

    // Enhanced generic condition waiter with better error handling
    public CompletableFuture<Void> waitForCondition(
            Callable<Boolean> condition,
            String description,
            long timeout,
            TimeUnit unit) {

        CompletableFuture<Void> future = new CompletableFuture<>();
        long startTime = System.currentTimeMillis();
        long timeoutMs = unit.toMillis(timeout);
        AtomicInteger checkCount = new AtomicInteger(0);

        ScheduledFuture<?> scheduledTask = scheduler.scheduleAtFixedRate(() -> {
            try {
                checkCount.incrementAndGet();

                if (condition.call()) {
                    future.complete(null);
                } else if (System.currentTimeMillis() - startTime > timeoutMs) {
                    future.completeExceptionally(
                            new TimeoutException("Condition not met within " + timeoutMs + "ms: " + description +
                                    " (checked " + checkCount.get() + " times)"));
                }
            } catch (Exception e) {
                future.completeExceptionally(new RuntimeException("Condition check failed: " + e.getMessage(), e));
            }
        }, 0, 100, TimeUnit.MILLISECONDS);

        future.whenComplete((result, error) -> scheduledTask.cancel(true));
        return future;
    }

    // Composite wait methods
    @SafeVarargs
    public final CompletableFuture<Void> waitForAll(CompletableFuture<?>... futures) {
        return CompletableFuture.allOf(futures);
    }

    @SafeVarargs
    public final CompletableFuture<Object> waitForAny(CompletableFuture<?>... futures) {
        return CompletableFuture.anyOf(futures);
    }

    // Enhanced high-level wait method with type safety
    public CompletableFuture<Void> waitFor(WaitCondition condition, Object... params) {
        switch (condition) {
            case CONSOLE_MESSAGE:
                if (params.length > 0 && params[0] instanceof String) {
                    return waitForConsoleMessage((String) params[0]).thenApply(result -> null);
                } else if (params.length > 0 && params[0] instanceof Predicate) {
                    @SuppressWarnings("unchecked")
                    Predicate<ConsoleLogEntry> filter = (Predicate<ConsoleLogEntry>) params[0];
                    return waitForConsoleMessage(filter).thenApply(result -> null);
                }
                throw new IllegalArgumentException("Console message condition requires String or Predicate parameter");

            case DOM_STABLE:
                if (params.length > 0 && params[0] instanceof Long) {
                    return waitForDomStable((Long) params[0]);
                }
                return waitForDomStable(1000);

            case DOM_READY:
                return waitForDomReady();

            case NETWORK_IDLE:
                if (params.length > 0 && params[0] instanceof Long) {
                    return waitForNetworkIdle((Long) params[0]);
                }
                return waitForNetworkIdle(500);

            case JAVASCRIPT_EXCEPTION:
                return waitForJavascriptException().thenApply(result -> null);

            case DOM_CHANGE:
                return waitForDomChange().thenApply(result -> null);

            default:
                throw new IllegalArgumentException("Unsupported condition: " + condition);
        }
    }

    // Smart page load wait with enhanced logic
    public CompletableFuture<Void> waitForPageReady() {
        return waitForAll(
                waitForDomReady(),
                waitForNetworkIdle(1000),
                waitForDomStable(500)
        ).exceptionally(throwable -> {
            System.err.println("Page ready wait completed with errors: " + throwable.getMessage());
            return null;
        });
    }

    // Enhanced state debugging
    public Map<String, Object> getCurrentState() {
        Map<String, Object> state = new HashMap<>();
        state.put("lastDomChangeMs", System.currentTimeMillis() - lastDomChangeTime.get());
        state.put("domChangeCount", domChangeCount.get());
        state.put("activeRequests", activeRequestCount.get());
        state.put("pendingFutures", pendingFutures.size());
        state.put("eventListeners", eventListeners.size());
        state.put("caughtExceptions", caughtExceptions.size());
        state.put("networkMonitoringEnabled", networkMonitoringEnabled);
        return state;
    }

    // Get caught exceptions for analysis
    public List<Map<String, Object>> getCaughtExceptions() {
        return new ArrayList<>(caughtExceptions);
    }

    // Clear caught exceptions
    public void clearCaughtExceptions() {
        caughtExceptions.clear();
    }

    // Send custom BiDi command
    public <T> T sendCommand(String method, Map<String, Object> params, Class<T> responseType) {
        Command<T> command = new Command<>(method, params, responseType);
        return biDi.send(command);
    }

    // Add custom event listener with better typing
    public <T> long addEventListener(String eventName, Consumer<Map<String, Object>> handler) {
        Event<Map<String, Object>> event = new Event<>(eventName, input -> input);
        long listenerId = biDi.addListener(event, handler);
        eventListeners.put(listenerId, event);
        return listenerId;
    }

    // Remove event listener
    public void removeEventListener(long listenerId) {
        try {
            biDi.removeListener(listenerId);
            eventListeners.remove(listenerId);
        } catch (Exception e) {
            System.err.println("Failed to remove event listener " + listenerId + ": " + e.getMessage());
        }
    }

    // Enhanced cleanup with better resource management
    public void shutdown() {
        if (!isShutdown.compareAndSet(false, true)) {
            return;
        }

        pendingFutures.values().forEach(future -> {
            if (!future.isDone()) {
                future.cancel(true);
            }
        });
        pendingFutures.clear();
        consoleFilters.clear();

        if (domStabilityChecker != null) {
            domStabilityChecker.cancel(false); // interruptIfRunning = false để tránh InterruptedException không cần thiết
        }

        eventListeners.keySet().forEach(listenerId -> {
            try {
                biDi.removeListener(listenerId);
            } catch (Exception e) {
                System.err.println("Failed to remove event listener " + listenerId + ": " + e.getMessage());
            }
        });
        eventListeners.clear();

        shutdownScheduler();
    }

    private void shutdownScheduler() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(3, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
                if (!scheduler.awaitTermination(2, TimeUnit.SECONDS)) {
                    System.err.println("Scheduler did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void close() {
        shutdown();
    }
}
