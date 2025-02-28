package com.driver;

import com.runner.TestRunner;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class DriverManager {

    private static final ThreadLocal<Integer> reuseCounterThreadLocal = ThreadLocal.withInitial(() -> 0);
    private static final Map<Thread, WebDriver> driverMap = new ConcurrentHashMap<>();
    private static final Logger logger = LogManager.getLogger(DriverManager.class);

    private DriverManager() {
        // Private constructor to prevent instantiation
    }

    public static WebDriver getDriver(String browserName) {

        WebDriver driver = driverMap.get(Thread.currentThread());
        int reuseCounter = reuseCounterThreadLocal.get();

        if (TestRunner.reuseBrowser && reuseCounter < TestRunner.reuseBrowserCount && driver != null) {
            reuseCounterThreadLocal.set(reuseCounter + 1);
            logger.info("Thread ID: " + Thread.currentThread().getId() + ", Thread Name: " + Thread.currentThread().getName() + " - Reusing driver, reuseCounter: " + reuseCounterThreadLocal.get());
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
            driverMap.put(Thread.currentThread(), driver);
            reuseCounterThreadLocal.set(1); // Start reuse counter
            //logger.info("Thread ID: " + Thread.currentThread().getId() + ", Thread Name: " + Thread.currentThread().getName() + " - Created new driver, reuseCounter: " + reuseCounterThreadLocal.get() + ", driverMap size: " + driverMap.size() + ", driverMap keys: " + driverMap.keySet()); // Add this log
            return driver;
        }
    }

    public static void quitDriver() {
        WebDriver driver = driverMap.remove(Thread.currentThread());
        if (driver != null) {
            driver.quit();
            logger.info(Thread.currentThread().getId() + " : Session Reused " + reuseCounterThreadLocal.get() + " Times..!!");
            //logger.info("Thread ID: " + Thread.currentThread().getId() + ", Thread Name: " + Thread.currentThread().getName() + " - Quitting driver, reuseCounter: " + reuseCounterThreadLocal.get() + ", driverMap size: " + driverMap.size() + ", driverMap keys: " + driverMap.keySet()); // Add this log
            reuseCounterThreadLocal.set(0);
        }
    }

    public static void resetReuseCounter() {
        reuseCounterThreadLocal.set(0);
    }

    public static int getResetReuseCounter() {
        return reuseCounterThreadLocal.get();
    }

    public static void closeAllDriverInstances() {
        logger.info("Thread ID: " + Thread.currentThread().getId() + ", Thread Name: " + Thread.currentThread().getName() + " - CLOSING LAST INSTANCE..!"); // Add this log
        driverMap.values().forEach(driver -> {
            if (driver != null) {
                driver.quit();
                logger.info("Thread ID: " + Thread.currentThread().getId() + ", Thread Name: " + Thread.currentThread().getName() + " - Quitting driver in closeAllDriverInstances, reuseCounter: " + reuseCounterThreadLocal.get()); // Add this log
            }

        });
        driverMap.clear();
        logger.info("Thread ID: " + Thread.currentThread().getId() + ", Thread Name: " + Thread.currentThread().getName() + " - driverMap cleared"); // Add this log
    }
}