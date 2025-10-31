package com.seleniumui.core;

import com.seleniumui.components.Button;
import com.seleniumui.components.Dropdown;
import com.seleniumui.components.Input;
import com.seleniumui.components.Label;
import com.seleniumui.core.actions.SmartActions;
import com.seleniumui.core.waits.SmartWait;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SeleniumUI implements AutoCloseable {

    private final WebDriver driver;
    private final SmartWait smartWait;
    private final SmartActions smartActions;

    private static final ThreadLocal<SeleniumUI> INSTANCE = new ThreadLocal<>();

    private SeleniumUI(WebDriver driver) {
        this.driver = driver;
        this.smartWait = new SmartWait(driver);
        this.smartActions = new SmartActions(driver);
    }

    public static SeleniumUI initialize(WebDriver driver) {
        return create(driver);
    }

    public static SeleniumUI create(WebDriver driver) {
        SeleniumUI instance = new SeleniumUI(driver);
        INSTANCE.set(instance);
        return instance;
    }

    public static SeleniumUI getInstance() {
        SeleniumUI instance = INSTANCE.get();
        if (instance == null) {
            throw new IllegalStateException("SeleniumUI not initialized for current thread. Call create() first.");
        }
        return instance;
    }

    public Button button(By locator) {
        return new Button(driver, locator, smartWait, smartActions);
    }

    public Dropdown dropdown(By locator) {
        return new Dropdown(driver, locator, smartWait, smartActions);
    }

    public Label label(By locator) {
        return new Label(driver, locator, smartWait, smartActions);
    }

    public Input input(By locator) {
        return new Input(driver, locator, smartWait, smartActions);
    }

    @Override
    public void close() {
        try {
            ((AutoCloseable) smartWait).close();
        } catch (Exception e) {
            System.err.println("Warning: Error during SmartWait cleanup: " + e.getMessage());
        } finally {
            INSTANCE.remove();
        }
    }
}
