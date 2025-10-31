package com.seleniumui.components;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.*;

public class ButtonTest extends BaseTest {

    @Test
    public void testClickButtonOnRealPage() {
        driver.get("https://demoqa.com/buttons");
        Button button1 = new Button(driver, By.id("doubleClickBtn"));
        Button button2 = new Button(driver, By.id("rightClickBtn"));
        Button button3 = new Button(driver, By.xpath("//button[text()='Click Me']"));
        button1.doubleClick();
        button2.rightClick();
        button3.click();

        Label doubleClickMessage = new Label(driver, By.id("doubleClickMessage"));
        Label rightClickMessage = new Label(driver, By.id("rightClickMessage"));
        Label dynamicClickMessage = new Label(driver, By.id("dynamicClickMessage"));
        Assert.assertTrue(doubleClickMessage.isDisplayed(), "doubleClickMessage should be visible after click");
        Assert.assertTrue(rightClickMessage.isDisplayed(), "rightClickMessage should be visible after click");
        Assert.assertTrue(dynamicClickMessage.isDisplayed(), "dynamicClickMessage should be visible after click");
    }

    @Test
    public void testButtonWithDynamicElement() {
        driver.get("https://demoqa.com/dynamic-properties");
        Button dynamicButton1 = new Button(driver, By.id("enableAfter"));
        Button dynamicButton2 = new Button(driver, By.id("visibleAfter"));
        dynamicButton1.click();
        dynamicButton2.click();
    }
}
