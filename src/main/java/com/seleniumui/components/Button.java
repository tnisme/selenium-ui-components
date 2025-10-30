package com.seleniumui.components;

import com.seleniumui.core.actions.SmartActions;
import com.seleniumui.core.BaseComponent;
import com.seleniumui.core.waits.SmartWait;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class Button extends BaseComponent {

    public Button(WebDriver driver, By locator) {
        super(driver, locator);
    }

    public void click() {
        SmartWait.forClickable(driver, locator);
        SmartActions.click(driver, locator);
    }

    public void doubleClick() {
        SmartWait.forClickable(driver, locator);
        SmartActions.doubleClick(driver, locator);
    }

    public void rightClick() {
        SmartWait.forClickable(driver, locator);
        SmartActions.rightClick(driver, locator);
    }

    public void jsClick() {
        SmartWait.forClickable(driver, locator);
        SmartActions.jsClick(driver, locator);
    }
}
