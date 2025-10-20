package com.seleniumui.actions;

import com.seleniumui.utils.JsHelper;
import com.seleniumui.utils.RetryExecutor;
import com.seleniumui.utils.SmartWait;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

import java.util.function.Consumer;

public class SmartActions {

    enum ActionType {
        CLICK,
        DOUBLE_CLICK,
        RIGHT_CLICK,
        JS_CLICK,
        SCROLL_TO_ELEMENT,
        SEND_KEYS,
        CLEAR
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
            System.out.println(actionType.toString() + " succeeded on element: " + locator.toString());
        });
    }

    public static void click(WebDriver driver, By locator) {
        executeWithWaitAndRetry(driver, locator, el -> {
            JsHelper.scrollToElement(driver, locator);
            el.click();
        }, ActionType.CLICK);
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

    public static SmartActions.Builder forElement(WebDriver driver, By locator) {
        return new SmartActions.Builder(driver, locator);
    }

    public static class Builder {
        private final WebDriver driver;
        private final By locator;
        private final StringBuilder inputBuilder = new StringBuilder();

        public Builder(WebDriver driver, By locator) {
            this.driver = driver;
            this.locator = locator;
        }

        public SmartActions.Builder clear() {
            executeWithWaitAndRetry(driver, locator, WebElement::clear, ActionType.CLEAR);
            return this;
        }

        public SmartActions.Builder type(String text) {
            inputBuilder.append(text);
            return this;
        }

        public SmartActions.Builder pressEnter() {
            inputBuilder.append(Keys.ENTER);
            return this;
        }

        public SmartActions.Builder pressTab() {
            inputBuilder.append(Keys.TAB);
            return this;
        }

        public void perform() {
            String text = inputBuilder.toString();
            if (text.isEmpty()) return;

            executeWithWaitAndRetry(driver, locator, el -> el.sendKeys(text), ActionType.SEND_KEYS);
            inputBuilder.setLength(0);
        }
    }
}
