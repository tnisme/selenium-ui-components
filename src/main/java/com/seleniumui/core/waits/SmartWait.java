package com.seleniumui.core.waits;

import org.openqa.selenium.*;
import org.openqa.selenium.bidi.BiDi;
import org.openqa.selenium.bidi.HasBiDi;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Function;

public final class SmartWait {

    private final WebDriver driver;
    private final BiDiSmartWait biDiSmartWait;
    private final long defaultTimeoutMs;
    private final long defaultPollIntervalMs;

    public SmartWait(WebDriver driver) {
        this(driver, DEFAULT_AJAX_TIMEOUT_MS, DEFAULT_POLL_INTERVAL_MS);
    }

    public SmartWait(WebDriver driver, long defaultTimeoutMs, long defaultPollIntervalMs) {
        this.driver = Objects.requireNonNull(driver, "WebDriver cannot be null");
        this.defaultTimeoutMs = defaultTimeoutMs;
        this.defaultPollIntervalMs = defaultPollIntervalMs;

        // Initialize BiDiSmartWait
        BiDi biDi = ((HasBiDi) driver).getBiDi();
        this.biDiSmartWait = new BiDiSmartWait(driver, biDi, 30);
    }

    // Constants
    public static final long DEFAULT_POLL_INTERVAL_MS = 200;
    public static final long DEFAULT_AJAX_TIMEOUT_MS = 10000;

    public FluentWait<WebDriver> createWait() {
        return createWait(defaultTimeoutMs, defaultPollIntervalMs);
    }

    public FluentWait<WebDriver> createWait(long timeoutMs) {
        return createWait(timeoutMs, defaultPollIntervalMs);
    }

    public FluentWait<WebDriver> createWait(long timeoutMs, long pollIntervalMs) {
        if (timeoutMs <= 0 || pollIntervalMs <= 0) {
            throw new IllegalArgumentException("Timeout and poll interval must be greater than 0");
        }

        return new WebDriverWait(driver, Duration.ofMillis(timeoutMs))
                .pollingEvery(Duration.ofMillis(pollIntervalMs))
                .ignoring(
                        StaleElementReferenceException.class,
                        NoSuchElementException.class
                )
                .withMessage("Timed out waiting for condition after " + timeoutMs + "ms");
    }

    public <T> T until(Function<? super WebDriver, T> condition, long timeoutMs, long pollIntervalMs) {
        Objects.requireNonNull(condition, "Condition cannot be null");

        FluentWait<WebDriver> wait = createWait(timeoutMs, pollIntervalMs);
        try {
            biDiSmartWait.waitForPageReady();
            return wait.until(condition);
        } catch (TimeoutException e) {
            throw new TimeoutException("Timed out after " + timeoutMs + "ms waiting for: " +
                    condition, e.getCause());
        }
    }

    public <T> T until(Function<? super WebDriver, T> condition) {
        return until(condition, defaultTimeoutMs, defaultPollIntervalMs);
    }

    public <T> T until(Function<? super WebDriver, T> condition, long timeoutMs) {
        return until(condition, timeoutMs, defaultPollIntervalMs);
    }

    public void waitForPageLoadComplete(long timeoutMs, long pollIntervalMs) {
        biDiSmartWait.waitFor(BiDiSmartWait.WaitCondition.DOM_READY);
    }

    public void waitForPageLoadComplete() {
        waitForPageLoadComplete(defaultTimeoutMs, defaultPollIntervalMs);
    }

    public void waitUntilAjaxDone(long timeoutMs, long pollIntervalMs) {
        biDiSmartWait.waitFor(BiDiSmartWait.WaitCondition.NETWORK_IDLE);
    }

    public void waitUntilAjaxDone() {
        waitUntilAjaxDone(defaultTimeoutMs, defaultPollIntervalMs);
    }

    public WebElement forVisible(By locator) {
        Objects.requireNonNull(locator, "Locator cannot be null");
        return until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement forVisible(WebElement element) {
        Objects.requireNonNull(element, "Element cannot be null");
        return until(ExpectedConditions.visibilityOf(element));
    }

    public WebElement forClickable(By locator) {
        Objects.requireNonNull(locator, "Locator cannot be null");
        return until(ExpectedConditions.elementToBeClickable(locator));
    }

    public WebElement forClickable(WebElement element) {
        Objects.requireNonNull(element, "Element cannot be null");
        return until(ExpectedConditions.elementToBeClickable(element));
    }

    public void forInvisible(By locator) {
        Objects.requireNonNull(locator, "Locator cannot be null");
        until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public void forPresence(By locator) {
        Objects.requireNonNull(locator, "Locator cannot be null");
        until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    // Getter methods for configuration (optional)
    public long getDefaultTimeoutMs() {
        return defaultTimeoutMs;
    }

    public long getDefaultPollIntervalMs() {
        return defaultPollIntervalMs;
    }
}
