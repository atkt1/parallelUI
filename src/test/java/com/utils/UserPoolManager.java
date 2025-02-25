package com.utils;
import com.hooks.Hooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class UserPoolManager {
    private static final Logger logger = LogManager.getLogger(UserPoolManager.class);
    private static final ThreadLocal<String> currentUser = new ThreadLocal<>();
    private static final List<String> availableUsers = new ArrayList<>();
    private static final List<String> consumedUsers = new ArrayList<>();
    private static boolean isInitialized = false;

    static {
        initializeUserPool("uat1"); // Initialize once when the class is loaded
    }

    private static synchronized void initializeUserPool(String environment) {
        if (!isInitialized) {
            try (FileInputStream input = new FileInputStream("src/test/resources/testdata/" + environment + "-users.properties")) {
                Properties prop = new Properties();
                prop.load(input);
                prop.forEach((key, value) -> availableUsers.add(key + "=" + value));
                isInitialized = true;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static synchronized String getUser() throws Exception {
        if (availableUsers.isEmpty()) {
            throw new Exception("UserNotAvailableInThePool");
        }
        String user = availableUsers.remove(0);
        consumedUsers.add(user);
        currentUser.set(user);
        logger.info(user + " : User picked up from the POOL for execution..!!");
        return user;
    }

    public static synchronized void releaseUser() {
        String user = currentUser.get();
        if (user != null) {
            consumedUsers.remove(user);
            availableUsers.add(user);
            currentUser.remove();
            logger.info(user + " : User removed from the POOL..!!");
        }
    }
}