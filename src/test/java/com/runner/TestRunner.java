package com.runner;


import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.BeforeSuite;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.stepdefs", "com.hooks"},
        tags = "",
        monochrome = true,
        plugin = {"pretty", "html:results/cucumber-report.html"}
)
public class TestRunner extends AbstractTestNGCucumberTests {


    public static boolean reuseBrowser;
    public static int reuseBrowserCount;

    @BeforeSuite
    @Parameters(value = {"reuseBrowser", "reuseBrowserCount"})
    public void setup(String reuseBrowserParam, String reuseBrowserCountParam) {
        if (reuseBrowserParam != null && reuseBrowserCountParam != null) {
            // Use TestNG parameters if present
            reuseBrowser = Boolean.parseBoolean(reuseBrowserParam);
            reuseBrowserCount = Integer.parseInt(reuseBrowserCountParam);
        } else {
            // Fall back to system properties (Maven properties)
            reuseBrowser = Boolean.parseBoolean(System.getProperty("reuseBrowser", "false"));
            reuseBrowserCount = Integer.parseInt(System.getProperty("reuseBrowserCount", "3"));
        }
    }

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
