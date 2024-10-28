package config.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BrowserManager {
    
    private static Map<String, WebDriver> webDriverMap = new HashMap<>();

    public static String createWebDriver() {
        String uniqueId = UUID.randomUUID().toString();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--disable-extensions");
        options.addArguments("--incognito");
        options.addArguments("--disable-search-engine-choice-screen");
        options.setAcceptInsecureCerts(true); // Updated for Selenium 4.22
        WebDriver driver = new ChromeDriver(options);
        webDriverMap.put(uniqueId, driver);
        return uniqueId;
    }

    public static WebDriver getWebDriver(String uniqueId) {
        return webDriverMap.get(uniqueId);
    }

    public static void deleteWebDriver(String uniqueId) {
        WebDriver driver = webDriverMap.get(uniqueId);
        if (driver != null) {
            try {
                driver.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                driver.quit();
            } catch (Exception e) {
                e.printStackTrace();
            }
            driver=null;
            webDriverMap.remove(uniqueId);
        }
    }

    public static boolean exists(String uniqueId) {
        return webDriverMap.containsKey(uniqueId);
    }

    public static void cleanUpAll() {
        for (WebDriver driver : webDriverMap.values()) {
            if (driver != null) {
                driver.close();
                driver.quit();
            }
        }
        webDriverMap.clear();
    }
}
