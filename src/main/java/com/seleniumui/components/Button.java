package com.seleniumui.components;

import com.seleniumui.core.BaseComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class Button extends BaseComponent {

    public Button(WebDriver driver, By locator) {
        super(driver, locator);
    }

    public void click() {
        smartWait.forClickable(locator);
        smartActions.click(locator);
    }

    public void doubleClick() {
        smartWait.forClickable(locator);
        smartActions.doubleClick(locator);
    }

    public void rightClick() {
        smartWait.forClickable(locator);
        smartActions.rightClick(locator);
    }

    public void jsClick() {
        smartWait.forClickable(locator);
        smartActions.jsClick(locator);
    }
}
