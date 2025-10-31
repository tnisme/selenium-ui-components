package com.seleniumui.components;

import com.seleniumui.core.BaseComponent;
import com.seleniumui.core.actions.SmartActions;
import com.seleniumui.core.waits.SmartWait;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class Input extends BaseComponent {

    public Input(WebDriver driver, By locator, SmartWait smartWait, SmartActions smartActions) {
        super(driver, locator, smartWait, smartActions);
    }

    public void clear() {
        smartWait.forVisible(locator);
        smartActions.clear(locator);
    }

    public void type(String text) {
        smartWait.forVisible(locator);
        smartActions.type(locator, text);
    }

    public void pressEnter() {
        smartWait.forVisible(locator);
        smartActions.pressEnter(locator);
    }

    public void pressTab() {
        smartWait.forVisible(locator);
        smartActions.pressTab(locator);
    }

    public void clearAndType(String text) {
        clear();
        type(text);
    }

    public void typeAndPressEnter(String text) {
        type(text);
        pressEnter();
    }

    public String getValue() {
        return smartActions.getAttribute(locator, "value");
    }

    public void pressKeys(CharSequence... keys) {
        StringBuilder combinedKeys = new StringBuilder();
        for (CharSequence key : keys) {
            combinedKeys.append(key);
        }
        smartWait.forVisible(locator);
        smartActions.pressKey(locator, combinedKeys);
    }
}
