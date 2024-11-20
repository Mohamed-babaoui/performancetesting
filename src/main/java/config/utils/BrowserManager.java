package config.utils;

import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;


import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import java.io.File;
import java.net.URL;
import java.security.cert.X509Certificate;
import org.apache.hc.core5.ssl.SSLContextBuilder;

public class BrowserManager {
    
    private static Map<String, WebDriver> webDriverMap = new HashMap<>();
    private static Properties props = new Properties();

    private static WebDriver setUpRemoteChromeDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-plugins");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-blink-features");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-software-rasterizer");
        options.addArguments("--no-default-browser-check");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--incognito");
        options.addArguments("--disable-search-engine-choice-screen");
        options.setAcceptInsecureCerts(true);

        System.setProperty("javax.net.ssl.trustStore", "src" + File.separator + "pvcp-intermidate.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "97460480");

        try {
            props.load(BrowserManager.class.getClassLoader().getResourceAsStream("seleniumHub.properties"));
            System.out.println("11111111111111111111111111111");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        URL remoteUrl = null;
        try {
            System.out.println("22222222222222222222222222222");
            remoteUrl = new URL(props.getProperty("URL_Remote"));
            System.out.println(remoteUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        System.out.println("7777777777777777777777777777777777");
        return new RemoteWebDriver(remoteUrl, options);
    }

    private static WebDriver setUpLocalChromeDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-plugins");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--disable-blink-features");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--incognito");
        options.addArguments("--disable-search-engine-choice-screen");
        options.setAcceptInsecureCerts(true);

        try {
            props.load(BrowserManager.class.getClassLoader().getResourceAsStream("seleniumHub.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new ChromeDriver(options);
    }


    public static String createWebDriver(String browser) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-plugins");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-blink-features");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-software-rasterizer");
        options.addArguments("--no-default-browser-check");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--incognito");
        options.addArguments("--disable-search-engine-choice-screen");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--ignore-ssl-errors");
        options.setAcceptInsecureCerts(true);

          // Step 3: Use RemoteWebDriver with the desired capabilities

        System.setProperty("javax.net.ssl.trustStore", "src" + File.separator + "pvcp-intermidate.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "97460480");

        try {
            props.load(BrowserManager.class.getClassLoader().getResourceAsStream("seleniumHub.properties"));
            System.out.println("11111111111111111111111111111");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        URL remoteUrl = null;
        try {
            System.out.println("22222222222222222222222222222");
            remoteUrl = new URL(props.getProperty("URL_Remote"));
            System.out.println(remoteUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        System.out.println("7777777777777777777777777777777777");
        WebDriver driver = null;
        try {
            driver = new RemoteWebDriver(remoteUrl, options);
        } catch (Exception e){
            e.printStackTrace();
        }
        String uniqueId = UUID.randomUUID().toString();

        webDriverMap.put(uniqueId, driver);
        System.out.println("5555555555555555555555555555555555");
        return uniqueId;
    }

    public static WebDriver getWebDriver(String uniqueId) {
        return webDriverMap.get(uniqueId);
    }

    public static void closeWebDriver(String uniqueId) {
        WebDriver driver = webDriverMap.get(uniqueId);
        if (driver != null) {
            try {
                driver.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
