package com.seleniumui.core.actions;

import com.seleniumui.executors.JsExecutor;
import com.seleniumui.executors.RetryExecutor;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

import java.util.Objects;

public class SmartActions {

    private final WebDriver driver;

    public SmartActions(WebDriver driver) {
        this.driver = Objects.requireNonNull(driver, "WebDriver cannot be null");
    }

    public String getAttribute(By locator, String attribute) {
        return RetryExecutor.getWithRetry(() -> {
            WebElement element = driver.findElement(locator);
            return Objects.requireNonNull(element.getAttribute(attribute));
        });
    }

    public void scrollToElement(By locator) {
        RetryExecutor.runWithRetry(() -> JsExecutor.scrollToElement(driver, locator));
    }

    public void scrollToElement(WebElement element) {
        RetryExecutor.runWithRetry(() -> JsExecutor.scrollToElement(driver, element));
    }

    public void click(By locator) {
        click(driver.findElement(locator));
    }

    public void click(WebElement element) {
        RetryExecutor.runWithRetry(element::click);
    }

    public void doubleClick(By locator) {
        RetryExecutor.runWithRetry(() -> {
            WebElement element = driver.findElement(locator);
            Actions actions = new Actions(driver);
            actions.doubleClick(element).perform();
        });
    }

    public void rightClick(By locator) {
        RetryExecutor.runWithRetry(() -> {
            WebElement element = driver.findElement(locator);
            Actions actions = new Actions(driver);
            actions.contextClick(element).perform();
        });
    }

    public void jsClick(By locator) {
        RetryExecutor.runWithRetry(() -> JsExecutor.jsClick(driver, locator));
    }

    public void clear(By locator) {
        RetryExecutor.runWithRetry(() -> driver.findElement(locator).clear());
    }

    public void type(By locator, String text) {
        RetryExecutor.runWithRetry(() -> driver.findElement(locator).sendKeys(text));
    }

    public void pressEnter(By locator) {
        RetryExecutor.runWithRetry(() -> driver.findElement(locator).sendKeys(Keys.ENTER));
    }

    public void pressTab(By locator) {
        RetryExecutor.runWithRetry(() -> driver.findElement(locator).sendKeys(Keys.TAB));
    }

    public void pressKey(By locator, CharSequence key) {
        RetryExecutor.runWithRetry(() -> driver.findElement(locator).sendKeys(key));
    }

    public String getText(By locator) {
        return RetryExecutor.getWithRetry(() -> driver.findElement(locator).getText());
    }

    public void hoverOverElement(By locator) {
        RetryExecutor.runWithRetry(() -> {
            Actions actions = new Actions(driver);
            actions.moveToElement(driver.findElement(locator)).perform();
        });
    }

    public void focusElement(By locator) {
        RetryExecutor.runWithRetry(() -> JsExecutor.focusElement(driver, locator));
    }
}
