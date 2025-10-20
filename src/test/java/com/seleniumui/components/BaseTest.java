package com.seleniumui.components;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

public class BaseTest {

    @BeforeTest
    public void setup() {
        // Common setup code for all tests can be placed here
    }

    @AfterTest
    public void teardown() {
        // Common teardown code for all tests can be placed here
    }
}
