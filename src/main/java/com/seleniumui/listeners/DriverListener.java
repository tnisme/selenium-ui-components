package com.seleniumui.listeners;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DriverListener implements WebDriverListener {

    @Override
    public void beforeClick(WebElement element) {
        System.out.println("Clicking element: " + describe(element));
    }

    @Override
    public void afterClick(WebElement element) {
        System.out.println("Clicked element successfully: " + describe(element));
    }

    @Override
    public void beforeSendKeys(WebElement element, CharSequence... keysToSend) {
        System.out.println("Sending keys to element " + describe(element) + ": " + String.join("", keysToSend));
    }

    @Override
    public void afterSendKeys(WebElement element, CharSequence... keysToSend) {
        System.out.println("Sent keys to element " + describe(element) + ": " + String.join("", keysToSend));
    }

    @Override
    public void beforeClear(WebElement element) {
        System.out.println("Clearing element: " + describe(element));
    }

    @Override
    public void afterClear(WebElement element) {
        System.out.println("Cleared element successfully: " + describe(element));
    }

    @Override
    public void beforeFindElement(WebDriver driver, By locator) {
        System.out.println("Finding element by: " + locator);
    }

    @Override
    public void afterFindElement(WebDriver driver, By locator, WebElement result) {
        System.out.println("Found element by " + locator + ": " + describe(result));
    }

    @Override
    public void onError(Object target, Method method, Object[] args, InvocationTargetException e) {
        System.err.println("Error occurred in method " + method.getName() + ": " + e.getCause());
    }

    private String describe(WebElement element) {
        try {
            return element.getTagName() + (element.getAttribute("id") != null
                    ? "#" + element.getAttribute("id")
                    : element.getAttribute("class") != null
                    ? "." + element.getAttribute("class")
                    : "");
        } catch (StaleElementReferenceException e) {
            return "<stale element>";
        }
    }
}
