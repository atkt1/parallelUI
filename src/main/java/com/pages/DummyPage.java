package com.pages;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class DummyPage {
    WebDriver webDriver;

    public void openBrowser(){
        WebDriverManager.chromedriver().setup();
        webDriver = new ChromeDriver();
        System.out.println(Thread.currentThread().getId() +":Test started..!!");
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
