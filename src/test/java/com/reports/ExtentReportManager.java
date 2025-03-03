package com.reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import io.cucumber.java.Scenario;

public class ExtentReportManager {

    private static final ThreadLocal<ExtentTest> extentTestThreadLocal = new ThreadLocal<>();
    private static ExtentReports extentReports;

    public static void setupExtentReport() {
        if (extentReports == null) {
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter("target/ExtentReports/ExtentReport.html");
            sparkReporter.config().setDocumentTitle("UI Automation Report");
            sparkReporter.config().setReportName("Test Results");
            sparkReporter.config().setTheme(Theme.DARK);

            extentReports = new ExtentReports();
            extentReports.attachReporter(sparkReporter);
            extentReports.setSystemInfo("OS", System.getProperty("os.name"));
            extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));
        }
    }

    public static void createTest(Scenario scenario) {
        ExtentTest extentTest = extentReports.createTest(scenario.getName());
        extentTestThreadLocal.set(extentTest);
    }

    public static ExtentTest getExtentTest() {
        return extentTestThreadLocal.get();
    }

    public static void flushExtentReport() {
        if (extentReports != null) {
            extentReports.flush();
        }
    }
}
