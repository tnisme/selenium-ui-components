package com.seleniumui.actions;

import com.seleniumui.utils.JsHelper;
import com.seleniumui.utils.RetryExecutor;
import com.seleniumui.utils.SmartWait;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class SmartActions {

    enum ActionType {
        CLICK,
        DOUBLE_CLICK,
        RIGHT_CLICK,
        JS_CLICK,
        SCROLL_TO_ELEMENT,
        SEND_KEYS,
        CLEAR,
        GET_ATTRIBUTE
    }

    static void executeWithWaitAndRetry(WebDriver driver, By locator, Consumer<WebElement> action, ActionType actionType) {
        RetryExecutor.runWithRetry(() -> {
            WebElement element;
            if (actionType == ActionType.CLICK || actionType == ActionType.DOUBLE_CLICK || actionType == ActionType.RIGHT_CLICK) {
                element = SmartWait.forClickable(driver, locator);
            } else {
                element = SmartWait.forVisible(driver, locator);
            }
            if (!element.isEnabled()) throw new ElementNotInteractableException(actionType.toString() + " failed: Element is not enabled");
            action.accept(element);
        });
    }

    static <T> T executeWithWaitAndRetry(WebDriver driver, By locator, Function<WebElement, T> action) {
        return RetryExecutor.getWithRetry(() -> {
            WebElement element = SmartWait.forVisible(driver, locator);
            if (!element.isEnabled()) throw new ElementNotInteractableException("Action failed: Element is not enabled");
            return action.apply(element);
        });
    }

    public static String getAttribute(WebDriver driver, By locator, String attribute) {
        return executeWithWaitAndRetry(driver, locator, el -> Objects.requireNonNull(el.getAttribute(attribute)));
    }

    public static void click(WebDriver driver, By locator) {
        executeWithWaitAndRetry(driver, locator, el -> {
            JsHelper.scrollToElement(driver, locator);
            el.click();
        }, ActionType.CLICK);
    }

    public static void click(WebDriver driver, WebElement element) {
        RetryExecutor.runWithRetry(() -> {
            SmartWait.forClickable(driver, element);
            if (!element.isEnabled()) throw new ElementNotInteractableException("CLICK failed: Element is not enabled");
            JsHelper.scrollToElement(driver, element);
            element.click();
        });
    }

    public static void doubleClick(WebDriver driver, By locator) {
        executeWithWaitAndRetry(driver, locator, el -> {
            Actions actions = new Actions(driver);
            JsHelper.scrollToElement(driver, locator);
            actions.doubleClick(el).perform();
        }, ActionType.DOUBLE_CLICK);
    }

    public static void rightClick(WebDriver driver, By locator) {
        executeWithWaitAndRetry(driver, locator, el -> {
            Actions actions = new Actions(driver);
            JsHelper.scrollToElement(driver, locator);
            actions.contextClick(el).perform();
        }, ActionType.RIGHT_CLICK);
    }

    public static void jsClick(WebDriver driver, By locator) {
        executeWithWaitAndRetry(driver, locator, el -> {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            JsHelper.scrollToElement(driver, locator);
            js.executeScript("arguments[0].click();", el);
        }, ActionType.JS_CLICK);
    }

    public static void clear(WebDriver driver, By locator) {
        executeWithWaitAndRetry(driver, locator, WebElement::clear, ActionType.CLEAR);
    }

    public static void type(WebDriver driver, By locator, String text) {
        executeWithWaitAndRetry(driver, locator, el -> el.sendKeys(text), ActionType.SEND_KEYS);
    }

    public static void pressEnter(WebDriver driver, By locator) {
        executeWithWaitAndRetry(driver, locator, el -> el.sendKeys(Keys.ENTER), ActionType.SEND_KEYS);
    }

    public static void pressTab(WebDriver driver, By locator) {
        executeWithWaitAndRetry(driver, locator, el -> el.sendKeys(Keys.TAB), ActionType.SEND_KEYS);
    }

    public static void pressKey(WebDriver driver, By locator, CharSequence key) {
        executeWithWaitAndRetry(driver, locator, el -> el.sendKeys(key), ActionType.SEND_KEYS);
    }

    public static String getText(WebDriver driver, By locator) {
        return executeWithWaitAndRetry(driver, locator, WebElement::getText);
    }
}
