package com.seleniumui.core;

import com.seleniumui.components.Button;
import com.seleniumui.components.Dropdown;
import com.seleniumui.components.Input;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ComponentFactory {

    private static WebDriver webDriver;

    private ComponentFactory() {}

    public static void initialize(WebDriver driver) {
        webDriver = driver;
    }

    public static Button button(By locator) {
        return new Button(webDriver, locator);
    }

    public static Dropdown dropdown(By locator) {
        return new Dropdown(webDriver, locator);
    }

    public static Input input(By locator) {
        return new Input(webDriver, locator);
    }
}
