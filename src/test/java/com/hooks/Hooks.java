package com.hooks;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.driver.DriverManager;
import com.reports.ExtentReportManager;
import com.runner.TestRunner;
import com.utils.ScenarioContextManager;
import com.utils.UserPoolManager;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Hooks {
    private static final Logger logger = LogManager.getLogger(Hooks.class);
    private static final ThreadLocal<String[]> userCredentialsThreadLocal = new ThreadLocal<>();
    @Before
    public void beforeScenario(Scenario scenario) {
        logger.info("Thread ID: " + Thread.currentThread().getId() + ", Thread Name: " + Thread.currentThread().getName() + " - Scenario: " + scenario.getName() + " : Test started...!!"); // Add this log
        ScenarioContextManager.setScenario(scenario);
        ExtentReportManager.setupExtentReport();
        ExtentReportManager.createTest(scenario);

        // Check if a user is already present in ThreadLocal
        if (userCredentialsThreadLocal.get() == null) {
            String user = null;
            try {
                user = UserPoolManager.getUser();
            } catch (Exception e) {
                e.printStackTrace();
            }
            String[] credentials = user.split("=")[1].split(",");
            userCredentialsThreadLocal.set(credentials);
        }
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
        if (!TestRunner.reuseBrowser || DriverManager.getResetReuseCounter() >= TestRunner.reuseBrowserCount){
            DriverManager.quitDriver();
            UserPoolManager.releaseUser();
            userCredentialsThreadLocal.remove();
            logger.info("Thread ID: " + Thread.currentThread().getId() + ", Thread Name: " + Thread.currentThread().getName() + " - Scenario: " + scenario.getName() + " : Quiting browser instance...!!"); // Add this log
        }else {
            logger.info("Thread ID: " + Thread.currentThread().getId() + ", Thread Name: " + Thread.currentThread().getName() + " - Scenario: " + scenario.getName() + " : Browser will be REUSED...!!");
        }
    }

   @AfterAll
    public static void afterAll() {
        DriverManager.closeAllDriverInstances();
        System.out.println("Suite Completed: Quitting browser Instance...!");
    }

    public static String[] getUserCredentials() {
        return userCredentialsThreadLocal.get();
    }
}
