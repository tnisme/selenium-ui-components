package com.seleniumui.utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Function;

public final class SmartWait {

    public static final long DEFAULT_POLL_INTERVAL_MS = 200;

    public static final long DEFAULT_AJAX_TIMEOUT_MS = 10000;

    // Private constructor to prevent instantiation
    private SmartWait() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static FluentWait<WebDriver> createWait(WebDriver driver) {
        return createWait(driver, DEFAULT_AJAX_TIMEOUT_MS, DEFAULT_POLL_INTERVAL_MS);
    }

    public static FluentWait<WebDriver> createWait(WebDriver driver, long timeoutMs) {
        return createWait(driver, timeoutMs, DEFAULT_POLL_INTERVAL_MS);
    }

    public static FluentWait<WebDriver> createWait(WebDriver driver, long timeoutMs, long pollIntervalMs) {
        Objects.requireNonNull(driver, "WebDriver cannot be null");
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

    public static <T> T until(WebDriver driver, Function<? super WebDriver, T> condition, long timeoutMs, long pollIntervalMs) {
        Objects.requireNonNull(driver, "WebDriver cannot be null");
        Objects.requireNonNull(condition, "Condition cannot be null");

        FluentWait<WebDriver> wait = createWait(driver, timeoutMs, pollIntervalMs);
        try {
            waitForPageLoadComplete(driver, timeoutMs, pollIntervalMs);
            waitUntilAjaxDone(driver, timeoutMs, pollIntervalMs);
            waitForJsCondition(driver, JsHelper.isGlobalOverlayPresent(driver), timeoutMs, pollIntervalMs);
            return wait.until(condition);
        } catch (TimeoutException e) {
            throw new TimeoutException("Timed out after " + timeoutMs + "ms waiting for: " +
                condition, e.getCause());
        }
    }

    public static <T> T until(WebDriver driver, Function<? super WebDriver, T> condition) {
        return until(driver, condition, DEFAULT_AJAX_TIMEOUT_MS, DEFAULT_POLL_INTERVAL_MS);
    }

    public static <T> T until(WebDriver driver, Function<? super WebDriver, T> condition, long timeoutMs) {
        return until(driver, condition, timeoutMs, DEFAULT_POLL_INTERVAL_MS);
    }

    public static void waitForPageLoadComplete(WebDriver driver, long timeoutMs, long pollIntervalMs) {
        Objects.requireNonNull(driver, "WebDriver cannot be null");

        createWait(driver, timeoutMs, pollIntervalMs).until(driver1 ->
            "complete".equals(((JavascriptExecutor) driver1)
                .executeScript("return document.readyState"))
        );
    }

    public static void waitForPageLoadComplete(WebDriver driver) {
        waitForPageLoadComplete(driver, DEFAULT_AJAX_TIMEOUT_MS, DEFAULT_POLL_INTERVAL_MS);
    }

    public static void waitUntilAjaxDone(WebDriver driver, long timeoutMs, long pollIntervalMs) {
        Objects.requireNonNull(driver, "WebDriver cannot be null");
        FluentWait<WebDriver> wait = createWait(driver, timeoutMs, pollIntervalMs);
        try {
            wait.until(d -> {
                JavascriptExecutor js = (JavascriptExecutor) d;
                boolean jqueryComplete = (Boolean) js.executeScript(
                    "if (window.jQuery) { return jQuery.active === 0; } return true;");
                boolean fetchComplete = (Boolean) js.executeScript(
                    "if (window._fetchActiveRequests) { return _fetchActiveRequests === 0; } return true;");
                return jqueryComplete && fetchComplete;
            });
        } catch (TimeoutException e) {
            throw new TimeoutException("Timed out waiting for AJAX/Fetch requests to complete", e);
        } catch (WebDriverException e) {
            throw new WebDriverException("Failed to check AJAX status: " + e.getMessage(), e);
        }
    }

    public static void waitUntilAjaxDone(WebDriver driver) {
        waitUntilAjaxDone(driver, DEFAULT_AJAX_TIMEOUT_MS, DEFAULT_POLL_INTERVAL_MS);
    }

    public static void waitForJsCondition(WebDriver driver, boolean jsScript, long timeoutMs, long pollIntervalMs) {
        Objects.requireNonNull(driver, "WebDriver cannot be null");
        FluentWait<WebDriver> wait = createWait(driver, timeoutMs, pollIntervalMs);
        try {
            wait.until(d -> {
                JavascriptExecutor js = (JavascriptExecutor) d;
                return !(Boolean) js.executeScript(
                    "return " + jsScript + ";"
                );
            });
        } catch (TimeoutException e) {
            throw new TimeoutException("Timed out waiting for overlays to disappear", e);
        } catch (WebDriverException e) {
            throw new WebDriverException("Failed to check overlay status: " + e.getMessage(), e);
        }
    }

    public static WebElement forVisible(WebDriver driver, By locator) {
        Objects.requireNonNull(locator, "Locator cannot be null");
        return until(driver, ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement forVisible(WebDriver driver, WebElement element) {
        Objects.requireNonNull(element, "Element cannot be null");
        return until(driver, ExpectedConditions.visibilityOf(element));
    }

    public static WebElement forClickable(WebDriver driver, By locator) {
        Objects.requireNonNull(locator, "Locator cannot be null");
        return until(driver, ExpectedConditions.elementToBeClickable(locator));
    }

    public static WebElement forClickable(WebDriver driver, WebElement element) {
        Objects.requireNonNull(element, "Element cannot be null");
        return until(driver, ExpectedConditions.elementToBeClickable(element));
    }

    public static boolean forInvisibility(WebDriver driver, By locator) {
        Objects.requireNonNull(locator, "Locator cannot be null");
        try {
            return until(driver, ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            return false;
        }
    }
}
