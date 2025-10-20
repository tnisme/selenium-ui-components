package com.seleniumui.components;

import com.seleniumui.actions.SmartActions;
import com.seleniumui.core.BaseComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class Button extends BaseComponent {

    public Button(WebDriver driver, By locator) {
        super(driver, locator);
    }

    public void click() {
        SmartActions.click(driver, locator);
    }

    public void doubleClick() {
        SmartActions.doubleClick(driver, locator);
    }

    public void rightClick() {
        SmartActions.rightClick(driver, locator);
    }

    public void jsClick() {
        SmartActions.jsClick(driver, locator);
    }
}
