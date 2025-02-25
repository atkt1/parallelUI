package com.utils;

import io.cucumber.java.Scenario;
import java.util.HashMap;
import java.util.Map;

public class ScenarioContextManager {

    private static final ThreadLocal<Map<String, Object>> scenarioContext = ThreadLocal.withInitial(HashMap::new);
    private static final ThreadLocal<Scenario> currentScenario = new ThreadLocal<>();

    public static void setScenario(Scenario scenario) {
        currentScenario.set(scenario);
    }

    public static void storeData(String key, Object value) {
        scenarioContext.get().put(key, value);
    }

    public static Object getData(String key) {
        return scenarioContext.get().get(key);
    }

    public static void attachDataToReport(String key, Object value) {
        if (currentScenario.get() != null) {
            currentScenario.get().attach(value.toString().getBytes(), "text/plain", key);
        }
    }

    public static void clearContext() {
        scenarioContext.remove();
        currentScenario.remove();
    }
}
