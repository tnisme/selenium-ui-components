package com.seleniumui.components;

import com.seleniumui.core.ComponentFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ButtonTest {

    private WebDriver driver;

    @BeforeClass
    public void setup() {
        driver = new ChromeDriver();
        ComponentFactory.initialize(driver);
    }

    @Test
    public void testClickButtonOnRealPage() {
        driver.get("https://demoqa.com/buttons");
        ComponentFactory.button(By.id("doubleClickBtn")).doubleClick();
        ComponentFactory.button(By.id("rightClickBtn")).rightClick();
        ComponentFactory.button(By.xpath("//button[text()='Click Me']")).click();
        WebElement doubleClickMessage = driver.findElement(By.id("doubleClickMessage"));
        WebElement rightClickMessage = driver.findElement(By.id("rightClickMessage"));
        WebElement dynamicClickMessage = driver.findElement(By.id("dynamicClickMessage"));
        Assert.assertTrue(doubleClickMessage.isDisplayed(), "doubleClickMessage should be visible after click");
        Assert.assertTrue(rightClickMessage.isDisplayed(), "rightClickMessage should be visible after click");
        Assert.assertTrue(dynamicClickMessage.isDisplayed(), "dynamicClickMessage should be visible after click");
    }

    @Test
    public void testButtonWithDynamicElement() {
        driver.get("https://demoqa.com/dynamic-properties");
        Button dynamicButton1 = ComponentFactory.button(By.id("enableAfter"));
        Button dynamicButton2 = ComponentFactory.button(By.id("visibleAfter"));
        dynamicButton1.click();
        dynamicButton2.click();
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}
