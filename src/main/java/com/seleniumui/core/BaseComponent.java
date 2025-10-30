package com.seleniumui.core;

import com.seleniumui.actions.SmartActions;
import com.seleniumui.utils.SmartWait;
import org.openqa.selenium.*;

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

    public boolean isEnabled() {
        return find().isEnabled();
    }

    public boolean isPresent() {
        try {
            driver.findElement(locator);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public void waitForInvisibility() {
        SmartWait.forInvisible(driver, locator);
    }

    public void waitForClickable() {
        SmartWait.forClickable(driver, locator);
    }

    public void waitForPresence() {
        SmartWait.forPresence(driver, locator);
    }

    public void scrollIntoView() {
        SmartActions.scrollToElement(driver, locator);
    }

    public Point getLocation() {
        return find().getLocation();
    }

    public Dimension getSize() {
        return find().getSize();
    }

    public String getCssValue(String propertyName) {
        return find().getCssValue(propertyName);
    }

    public Rectangle getRect() {
        return find().getRect();
    }

    public boolean hasClass(String className) {
        String classAttribute = getAttribute("class");
        return classAttribute != null && classAttribute.contains(className);
    }

    public boolean hasAttribute(String attributeName) {
        return getAttribute(attributeName) != null;
    }

    public void hover() {
        SmartActions.hoverOverElement(driver, locator);
    }

    public void focus() {
        SmartActions.focusElement(driver, locator);
    }

    public String getLocatorDescription() {
        return locator.toString();
    }

    public By getLocator() {
        return locator;
    }
}
