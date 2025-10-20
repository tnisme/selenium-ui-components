package com.seleniumui.components;

import com.seleniumui.actions.SmartActions;
import com.seleniumui.core.BaseComponent;
import com.seleniumui.utils.SmartWait;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

import java.util.Objects;

public class Dropdown extends BaseComponent {

    public Dropdown(WebDriver driver, By locator) {
        super(Objects.requireNonNull(driver, "WebDriver cannot be null"),
                Objects.requireNonNull(locator, "Locator cannot be null"));
    }

    public void selectByText(String text) {
        Objects.requireNonNull(text, "Selection text cannot be null");
        SmartActions.click(driver, locator);
        SmartActions.click(driver, By.xpath(".//option[normalize-space()='" + text + "']"));
    }

    public String getSelectedText() {
        return new Select(SmartWait.forVisible(driver, locator)).getFirstSelectedOption().getText();
    }
}
