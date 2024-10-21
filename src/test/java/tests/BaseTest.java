package tests;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public abstract class BaseTest {

    protected WebDriver driver;
    protected Properties config;
    protected Properties testData;
    protected static ExtentReports extent;
    protected ExtentTest test;
    protected WebDriverWait wait;

    @BeforeSuite
    public void setupSuite() {
        // Initialize ExtentReports
        ExtentSparkReporter htmlReporter = new ExtentSparkReporter("test-output/ExtentReports.html");
        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);

        // Optional: Set system info or configuration details
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Browser", "Chrome");
        extent.setSystemInfo("Tester", "Automation Team");
    }

    @AfterSuite
    public void tearDownSuite() {
        // Flush reports
        if (extent != null) {
            extent.flush();
        }
    }

    @BeforeClass
    public void loadConfig() throws IOException {
        // Load configuration
        config = new Properties();
        InputStream configInput = getClass().getClassLoader().getResourceAsStream("config.properties");
        if (configInput == null) {
            throw new IOException("Configuration file 'config.properties' not found in classpath");
        }
        config.load(configInput);

        // Load test data
        testData = new Properties();
        InputStream testDataInput = getClass().getClassLoader().getResourceAsStream("testdata.properties");
        if (testDataInput == null) {
            throw new IOException("Test data file 'testdata.properties' not found in classpath");
        }
        testData.load(testDataInput);
    }

    @BeforeMethod
    public void setUp() throws IOException {
        driver = setUpRemoteChromeDriver();
        // Initialize WebDriverWait after WebDriver is initialized
        wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(100));
    }

    @AfterMethod
    public void tearDown() {
        System.out.println("Tear down");
        // Quit driver
        if (driver != null) {
            driver.quit();
        }
    }

    private WebDriver setUpRemoteChromeDriver() throws IOException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--disable-extensions");
        options.addArguments("--incognito");
        options.addArguments("--disable-search-engine-choice-screen");
        options.setAcceptInsecureCerts(true); // Accept insecure certificates

        // Set SSL properties, ensure the trustStore file exists
        String trustStorePath = "src" + File.separator + "pvcp-intermidate.jks";
        File trustStoreFile = new File(trustStorePath);
        if (trustStoreFile.exists()) {
            System.setProperty("javax.net.ssl.trustStore", trustStoreFile.getAbsolutePath());
            System.setProperty("javax.net.ssl.trustStorePassword", "97460480");
        } else {
            throw new IOException("Trust store file not found: " + trustStoreFile.getAbsolutePath());
        }

        // Load Selenium Grid properties
        Properties props = new Properties();
        InputStream seleniumHubProps = getClass().getClassLoader().getResourceAsStream("seleniumHub.properties");
        if (seleniumHubProps == null) {
            throw new IOException("Selenium hub properties file 'seleniumHub.properties' not found");
        }
        props.load(seleniumHubProps);

        // Get remote URL for Selenium Grid
        String remoteUrlString = props.getProperty("URL_Remote");
        if (remoteUrlString == null || remoteUrlString.isEmpty()) {
            throw new IOException("URL_Remote is not configured in seleniumHub.properties");
        }
        URL remoteUrl = new URL(remoteUrlString);

        // Initialize and return RemoteWebDriver
        return new RemoteWebDriver(remoteUrl, options);
    }
}
