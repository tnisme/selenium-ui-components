package com.seleniumui.components;

import com.seleniumui.core.SeleniumUI;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;

public class BaseTest {

    protected WebDriver driver;
    protected SeleniumUI seleniumUI;
    private long startTime;
    private long totalTime;

    @BeforeTest
    public void setupTestRun() {
        startTime = System.currentTimeMillis();
    }

    @AfterTest
    public void afterTestRun() {
        totalTime = System.currentTimeMillis() - startTime;
        System.out.printf("Total test execution time: %.2f seconds%n", totalTime / 1000.0);
    }

    @BeforeClass
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.setCapability("webSocketUrl", true);
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        driver = new ChromeDriver(options);
        seleniumUI = SeleniumUI.initialize(driver);
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) driver.quit();
        if (seleniumUI != null) seleniumUI.close();
    }
}
