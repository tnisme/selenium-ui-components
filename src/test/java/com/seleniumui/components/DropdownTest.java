package com.seleniumui.components;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DropdownTest extends BaseTest{

    @Test
    public void sampleTest() {
        driver.get("https://demoqa.com/select-menu");
        Dropdown dropdown = seleniumUI.dropdown(By.id("oldSelectMenu"));
        Dropdown dropdown2 = seleniumUI.dropdown(By.id("salutation"));
        Dropdown dropdownMulti = seleniumUI.dropdown(By.id("cars"));
        Dropdown dropdownMulti2 = seleniumUI.dropdown(By.xpath("//b[text()='Multiselect drop down']/ancestor::p/following-sibling::div"));

        dropdown.selectByText("Blue");
        Assert.assertEquals(dropdown.getSelectedText(), "Blue", "Selected text should match the expected value.");

        dropdown2.selectByText("Mr.");
        Assert.assertEquals(dropdown2.getSelectedText(), "Mr.", "Selected text should match the expected value.");

        dropdownMulti.selectByText("Volvo");
        Assert.assertEquals(dropdownMulti.getSelectedTexts().get(0), "Volvo", "Selected text should match the expected value.");
        dropdownMulti.addSelectionByText("Saab");
        Assert.assertEquals(dropdownMulti.getSelectedTexts().get(1), "Saab", "Selected text should match the expected value.");
        dropdownMulti.deselectAll();
        Assert.assertTrue(dropdownMulti.getSelectedTexts().isEmpty(), "All selections should be cleared.");

        dropdownMulti2.selectByText("Green");
        Assert.assertEquals(dropdownMulti2.getSelectedTexts().get(0), "Green", "Selected text should match the expected value.");
        dropdownMulti2.addSelectionByText("Black");
        Assert.assertEquals(dropdownMulti2.getSelectedTexts().get(1), "Black", "Selected text should match the expected value.");
    }
}
