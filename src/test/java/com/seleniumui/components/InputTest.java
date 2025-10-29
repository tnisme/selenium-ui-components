package com.seleniumui.components;

import com.seleniumui.core.ComponentFactory;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

public class InputTest extends BaseTest {

    @Test
    public void sampleTest() {
        driver.get("https://demoqa.com/text-box");
        Input username = ComponentFactory.createComponent(Input.class, By.id("userName"));
        Input email = ComponentFactory.createComponent(Input.class, By.id("userEmail"));
        Input currentAddress = ComponentFactory.createComponent(Input.class, By.id("currentAddress"));
        Input permanentAddress = ComponentFactory.createComponent(Input.class, By.id("permanentAddress"));

        username.clearAndType("John Doe");
        Assert.assertEquals(username.getValue(), "John Doe", "Username input value should match the typed text.");

        email.clearAndType("johndoe@example.com");
        Assert.assertEquals(email.getValue(), "johndoe@example.com", "Email input value should match the typed text.");

        currentAddress.clearAndType("123 Main Street");
        Assert.assertEquals(currentAddress.getValue(), "123 Main Street", "Current address input value should match the typed text.");

        permanentAddress.clearAndType("456 Elm Street");
        Assert.assertEquals(permanentAddress.getValue(), "456 Elm Street", "Permanent address input value should match the typed text.");
    }
}
