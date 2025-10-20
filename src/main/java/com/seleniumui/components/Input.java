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
        SmartActions.forElement(driver, locator).clear().perform();
    }

    public void type(String text) {
        SmartActions.forElement(driver, locator).type(text).perform();
    }

    public void pressEnter() {
        SmartActions.forElement(driver, locator).pressEnter().perform();
    }

    public void pressTab() {
        SmartActions.forElement(driver, locator).pressTab().perform();
    }

    public void pressKey(CharSequence key) {
        SmartActions.forElement(driver, locator).type(key.toString()).perform();
    }

    public void pressKeys(CharSequence... keys) {
        StringBuilder combinedKeys = new StringBuilder();
        for (CharSequence key : keys) {
            combinedKeys.append(key);
        }
        SmartActions.forElement(driver, locator).type(combinedKeys.toString()).perform();
    }
}
