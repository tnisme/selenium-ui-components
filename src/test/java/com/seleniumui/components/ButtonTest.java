package com.seleniumui.components;

import com.seleniumui.core.ComponentFactory;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.*;

public class ButtonTest extends BaseTest {

    @Test
    public void testClickButtonOnRealPage() {
        driver.get("https://demoqa.com/buttons");
        ComponentFactory.createComponent(Button.class, By.id("doubleClickBtn")).doubleClick();
        ComponentFactory.createComponent(Button.class, By.id("rightClickBtn")).rightClick();
        ComponentFactory.createComponent(Button.class, By.xpath("//button[text()='Click Me']")).click();
        Label doubleClickMessage = ComponentFactory.createComponent(Label.class, By.id("doubleClickMessage"));
        Label rightClickMessage = ComponentFactory.createComponent(Label.class, By.id("rightClickMessage"));
        Label dynamicClickMessage = ComponentFactory.createComponent(Label.class, By.id("dynamicClickMessage"));
        Assert.assertTrue(doubleClickMessage.isDisplayed(), "doubleClickMessage should be visible after click");
        Assert.assertTrue(rightClickMessage.isDisplayed(), "rightClickMessage should be visible after click");
        Assert.assertTrue(dynamicClickMessage.isDisplayed(), "dynamicClickMessage should be visible after click");
    }

    @Test
    public void testButtonWithDynamicElement() {
        driver.get("https://demoqa.com/dynamic-properties");
        Button dynamicButton1 = ComponentFactory.createComponent(Button.class, By.id("enableAfter"));
        Button dynamicButton2 = ComponentFactory.createComponent(Button.class, By.id("visibleAfter"));
        dynamicButton1.click();
        dynamicButton2.click();
    }
}
