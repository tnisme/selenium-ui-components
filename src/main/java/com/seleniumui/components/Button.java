package com.seleniumui.components;

import com.seleniumui.core.BaseComponent;
import com.seleniumui.core.actions.SmartActions;
import com.seleniumui.core.waits.SmartWait;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class Button extends BaseComponent {

    public Button(WebDriver driver, By locator, SmartWait smartWait, SmartActions smartActions) {
        super(driver, locator, smartWait, smartActions);
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
