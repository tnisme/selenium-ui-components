package com.seleniumui.core;

import com.seleniumui.actions.SmartActions;
import com.seleniumui.utils.SmartWait;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class BaseComponent {
    protected WebDriver driver;
    protected By locator;

    protected BaseComponent(WebDriver driver, By locator) {
        this.locator = locator;
        this.driver = driver;
    }

    protected WebElement find() {
        return SmartWait.forVisible(driver, locator);
    }

    public boolean isDisplayed() {
        return find().isDisplayed();
    }

    public String getAttribute(String attribute) {
        return SmartActions.getAttribute(driver, locator, attribute);
    }

    public String getText() {
        return SmartActions.getText(driver, locator);
    }

    public boolean isVisible() {
        try {
            SmartWait.forVisible(driver, locator);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isEnabled() {
        return find().isEnabled();
    }

}
