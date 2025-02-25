package com.driver;

import com.runner.TestRunner;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;


public class DriverManager {

    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Integer> reuseCounterThreadLocal = ThreadLocal.withInitial(() -> 0);


    private DriverManager() {
        // Private constructor to prevent instantiation
    }

    public static WebDriver getDriver(String browserName) {

        WebDriver driver = driverThreadLocal.get();
        int reuseCounter = reuseCounterThreadLocal.get();

        if (TestRunner.reuseBrowser && reuseCounter < TestRunner.reuseBrowserCount && driver != null) {
            reuseCounterThreadLocal.set(reuseCounter + 1);
            return driver; // Reuse existing driver
        } else {
            if (driver != null) {
                quitDriver(); // Quit existing driver if reuse count reached or reuse is false
            }
            switch (browserName.toLowerCase()) {
                case "chrome":
                    WebDriverManager.chromedriver().setup();
                    driver = new ChromeDriver();
                    break;
                case "firefox":
                    WebDriverManager.firefoxdriver().setup();
                    driver = new FirefoxDriver();
                    break;
                case "edge":
                    WebDriverManager.edgedriver().setup();
                    driver = new EdgeDriver();
                    break;
                case "safari":
                    WebDriverManager.safaridriver().setup();
                    driver = new SafariDriver();
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported browser: " + browserName);
            }
            driver.manage().window().maximize();
            driverThreadLocal.set(driver);
            reuseCounterThreadLocal.set(1); // Start reuse counter
            return driver;
        }
    }

    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            driver.quit();
            driverThreadLocal.remove();
            reuseCounterThreadLocal.set(0);

        }
    }

    public static void resetReuseCounter(){
        reuseCounterThreadLocal.set(0);
    }

    public static int getResetReuseCounter(){
        return reuseCounterThreadLocal.get();
    }

    public static void closeAllDriverInstances() {
    }
}