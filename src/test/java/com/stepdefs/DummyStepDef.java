package com.stepdefs;

import com.pages.DummyPage;
import io.cucumber.java.en.Given;

public class DummyStepDef {
private DummyPage dummyP;

    @Given("^Browser is launched$")
    public void launchBrowser(){
        dummyP = new DummyPage();
        dummyP.openBrowser();
    }

}
