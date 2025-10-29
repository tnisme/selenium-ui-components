package com.seleniumui.components;

import com.seleniumui.actions.SmartActions;
import com.seleniumui.core.BaseComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import java.util.Objects;

public class Input extends BaseComponent {

    public Input(WebDriver driver, By locator) {
        super(Objects.requireNonNull(driver, "WebDriver cannot be null"), 
              Objects.requireNonNull(locator, "Locator cannot be null"));
    }

    public void clear() {
        SmartActions.clear(driver, locator);
    }

    public void type(String text) {
        SmartActions.type(driver, locator, text);
    }

    public void pressEnter() {
        SmartActions.pressEnter(driver, locator);
    }

    public void pressTab() {
        SmartActions.pressTab(driver, locator);
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
        return SmartActions.getAttribute(driver, locator, "value");
    }

    public void pressKeys(CharSequence... keys) {
        StringBuilder combinedKeys = new StringBuilder();
        for (CharSequence key : keys) {
            combinedKeys.append(key);
        }
        SmartActions.pressKey(driver, locator, combinedKeys);
    }
}
