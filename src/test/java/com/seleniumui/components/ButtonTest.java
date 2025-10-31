package com.seleniumui.components;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.*;

public class ButtonTest extends BaseTest {

    @Test
    public void testClickButtonOnRealPage() {
        driver.get("https://demoqa.com/buttons");
        seleniumUI.button(By.id("doubleClickBtn")).doubleClick();
        seleniumUI.button(By.id("rightClickBtn")).rightClick();
        seleniumUI.button(By.xpath("//button[text()='Click Me']")).click();

        Assert.assertTrue(seleniumUI.label(By.id("doubleClickMessage")).isDisplayed(), "doubleClickMessage should be visible after click");
        Assert.assertTrue(seleniumUI.label(By.id("rightClickMessage")).isDisplayed(), "rightClickMessage should be visible after click");
        Assert.assertTrue(seleniumUI.label(By.id("dynamicClickMessage")).isDisplayed(), "dynamicClickMessage should be visible after click");
    }

    @Test
    public void testButtonWithDynamicElement() {
        driver.get("https://demoqa.com/dynamic-properties");
        seleniumUI.button(By.id("enableAfter")).click();
        seleniumUI.button(By.id("visibleAfter")).click();
    }
}
