package com.hooks;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.driver.DriverManager;
import com.reports.ExtentReportManager;
import com.utils.ScenarioContextManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Hooks {
    Logger logger = LogManager.getLogger(Hooks.class);
    @Before
    public void beforeScenario(Scenario scenario) {
        logger.info(scenario.getName() + " : Test started...!!");
        ScenarioContextManager.setScenario(scenario);
        ExtentReportManager.setupExtentReport();
        ExtentReportManager.createTest(scenario);
    }

    @After
    public void afterScenario(Scenario scenario) {
        logger.info(scenario.getName() + " : Test Finished...!!");
        ScenarioContextManager.clearContext();
        if (scenario.isFailed()) {
            WebDriver driver = DriverManager.getDriver(System.getProperty("browser", "chrome"));
            String screenshotPath = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
            ExtentReportManager.getExtentTest().log(Status.FAIL, "Scenario Failed: " + scenario.getName(), MediaEntityBuilder.createScreenCaptureFromBase64String(screenshotPath).build());
        } else if (scenario.getStatus().toString().equalsIgnoreCase("SKIPPED")) {
            ExtentReportManager.getExtentTest().log(Status.SKIP, "Scenario Skipped: " + scenario.getName());
        } else {
            ExtentReportManager.getExtentTest().log(Status.PASS, "Scenario Passed: " + scenario.getName());
        }
        ExtentReportManager.flushExtentReport();
    }
}
