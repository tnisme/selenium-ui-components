package com.seleniumui.core;

import com.seleniumui.utils.DriverListener;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.events.WebDriverListener;

public class ComponentFactory {

    private static WebDriver webDriver;

    private ComponentFactory() {}

    public static void initialize(WebDriver driver) {
        if (driver == null)
            throw new IllegalArgumentException("WebDriver cannot be null");

        DriverListener listener = new DriverListener();

        webDriver = new EventFiringDecorator<>(listener).decorate(driver);

        System.out.println("[ComponentFactory] WebDriver initialized with listener: "
                + listener.getClass().getSimpleName());
    }

    public static void initialize(WebDriver driver, WebDriverListener... listeners) {
        if (driver == null)
            throw new IllegalArgumentException("WebDriver cannot be null");

        WebDriver decorated = driver;
        for (WebDriverListener listener : listeners) {
            decorated = new EventFiringDecorator<>(listener).decorate(decorated);
        }

        webDriver = decorated;
    }

    public static <T extends BaseComponent> T createComponent(Class<T> componentClass, By locator) {
        try {
            return componentClass.getDeclaredConstructor(WebDriver.class, By.class)
                    .newInstance(webDriver, locator);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create component: " + componentClass.getSimpleName(), e);
        }
    }
}
