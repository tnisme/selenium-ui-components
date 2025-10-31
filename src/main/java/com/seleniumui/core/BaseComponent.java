package com.seleniumui.core;

import com.seleniumui.core.actions.SmartActions;
import com.seleniumui.core.waits.SmartWait;
import org.openqa.selenium.*;

public class BaseComponent {

    protected WebDriver driver;
    protected By locator;
    protected SmartWait smartWait;
    protected SmartActions smartActions;

    protected BaseComponent(WebDriver driver, By locator) {
        this.locator = locator;
        this.driver = driver;
        this.smartWait = new SmartWait(driver);
        this.smartActions = new SmartActions(driver);
    }

    protected WebElement find() {
        return smartWait.forVisible(locator);
    }

    public boolean isDisplayed() {
        return find().isDisplayed();
    }

    public String getAttribute(String attribute) {
        return smartActions.getAttribute(locator, attribute);
    }

    public String getText() {
        return smartActions.getText(locator);
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
        smartWait.forInvisible(locator);
    }

    public void waitForClickable() {
        smartWait.forClickable(locator);
    }

    public void waitForPresence() {
        smartWait.forPresence(locator);
    }

    public void scrollIntoView() {
        smartActions.scrollToElement(locator);
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
        smartActions.hoverOverElement(locator);
    }

    public void focus() {
        smartActions.focusElement(locator);
    }

    public String getLocatorDescription() {
        return locator.toString();
    }

    public By getLocator() {
        return locator;
    }
}
