package com.seleniumui.components;

import com.seleniumui.core.BaseComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class Label extends BaseComponent {

    public Label(WebDriver driver, By locator) {
        super(driver, locator);
    }
}
