package com.seleniumui.core.actions;

import com.seleniumui.executors.JsExecutor;
import com.seleniumui.executors.RetryExecutor;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

import java.util.Objects;

public class SmartActions {

    public static String getAttribute(WebDriver driver, By locator, String attribute) {
        return RetryExecutor.getWithRetry(() -> {
            WebElement element = driver.findElement(locator);
            return Objects.requireNonNull(element.getAttribute(attribute));
        });
    }

    public static void scrollToElement(WebDriver driver, By locator) {
        RetryExecutor.runWithRetry(() -> JsExecutor.scrollToElement(driver, locator));
    }

    public static void click(WebDriver driver, By locator) {
        click(driver.findElement(locator));
    }

    public static void click(WebElement element) {
        RetryExecutor.runWithRetry(element::click);
    }

    public static void doubleClick(WebDriver driver, By locator) {
        RetryExecutor.runWithRetry(() -> {
            WebElement element = driver.findElement(locator);
            Actions actions = new Actions(driver);
            actions.doubleClick(element).perform();
        });
    }

    public static void rightClick(WebDriver driver, By locator) {
        RetryExecutor.runWithRetry(() -> {
            WebElement element = driver.findElement(locator);
            Actions actions = new Actions(driver);
            actions.contextClick(element).perform();
        });
    }

    public static void jsClick(WebDriver driver, By locator) {
        RetryExecutor.runWithRetry(() -> JsExecutor.jsClick(driver, locator));
    }

    public static void clear(WebDriver driver, By locator) {
        RetryExecutor.runWithRetry(() -> driver.findElement(locator).clear());
    }

    public static void type(WebDriver driver, By locator, String text) {
        RetryExecutor.runWithRetry(() -> driver.findElement(locator).sendKeys(text));
    }

    public static void pressEnter(WebDriver driver, By locator) {
        RetryExecutor.runWithRetry(() -> driver.findElement(locator).sendKeys(Keys.ENTER));
    }

    public static void pressTab(WebDriver driver, By locator) {
        RetryExecutor.runWithRetry(() -> driver.findElement(locator).sendKeys(Keys.TAB));
    }

    public static void pressKey(WebDriver driver, By locator, CharSequence key) {
        RetryExecutor.runWithRetry(() -> driver.findElement(locator).sendKeys(key));
    }

    public static String getText(WebDriver driver, By locator) {
        return RetryExecutor.getWithRetry(() -> driver.findElement(locator).getText());
    }

    public static void hoverOverElement(WebDriver driver, By locator) {
        RetryExecutor.runWithRetry(() -> {
            Actions actions = new Actions(driver);
            actions.moveToElement(driver.findElement(locator)).perform();
        });
    }

    public static void focusElement(WebDriver driver, By locator) {
        RetryExecutor.runWithRetry(() -> JsExecutor.focusElement(driver, locator));
    }
}
