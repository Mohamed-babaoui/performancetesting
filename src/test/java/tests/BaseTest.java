package tests;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class BaseTest {

    protected WebDriver driver;
    protected Properties config;
    protected Properties testData;
    protected static ExtentReports extent;
    protected ExtentTest test;
    protected WebDriverWait wait=new WebDriverWait(driver, java.time.Duration.ofSeconds(100));


    @BeforeSuite
    public void setupSuite() {
        // Initialize ExtentReports
        ExtentSparkReporter htmlReporter = new ExtentSparkReporter  ("test-output/ExtentReports.html");
        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);

        // Optional: Set system info or configuration details
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Browser", "Chrome");
        extent.setSystemInfo("Tester", "Automation Team ");
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
    public void setUp() {
        // Set up ChromeOptions
        ChromeOptions options = new ChromeOptions();
        options.setAcceptInsecureCerts(true); // Accept insecure certificates

        // Initialize WebDriver with the options
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(options);

        // Maximize the browser window
        driver.manage().window().maximize();
    }

    @AfterMethod
    public void tearDown() {
        System.out.println("Tear down");
        // Quit driver
        if (driver != null) {
            driver.quit();
        }
    }
}