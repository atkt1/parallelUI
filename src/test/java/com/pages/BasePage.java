package com.pages;

import com.driver.DriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;


public class BasePage {

    protected WebDriver driver;

    public BasePage() {
        this.driver = DriverManager.getDriver(System.getProperty("browser", "chrome")); //Retrieve driver from managaer
        PageFactory.initElements(driver, this);
    }

    // Common WebDriver methods (e.g., waitForElement, navigateTo)
    // ...
}