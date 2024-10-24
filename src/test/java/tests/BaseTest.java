package tests;
import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
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
 
    // Initialize ChromeDriver
    driver =  setUpRemoteChromeDriver();

    // Initialize WebDriverWait
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
    options.setAcceptInsecureCerts(true); // Updated for Selenium 4.22  

    // Set SSL properties   
    System.setProperty("javax.net.ssl.trustStore", "src" + File.separator + "pvcp-intermidate.jks");    
    System.setProperty("javax.net.ssl.trustStorePassword", "97460480");    

    // Define and load properties
    Properties props = new Properties();
    InputStream input = getClass().getClassLoader().getResourceAsStream("seleniumHub.properties");
    if (input == null) {
        throw new IOException("Property file 'seleniumHub.properties' not found in the classpath");
    }
    props.load(input);

    // Load the URL property
    URL remoteUrl = new URL(props.getProperty("URL_Remote"));  
    return new RemoteWebDriver(remoteUrl, options);   
}

    
}
