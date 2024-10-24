package tests;

import com.aventstack.extentreports.markuputils.MarkupHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
// Removed DevTools imports
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import pages.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.io.FileUtils;

/**
 * Advanced version of Scenario1TestSamp that includes performance sampling and reporting.
 */
public class Scenario1TestSampAdv extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(Scenario1TestSampAdv.class);

    // Instantiate the PerformanceSamplerAdv
    private PerformanceSamplerAdv sampler = new PerformanceSamplerAdv();

    // Removed DevTools and network data collections
    // private DevTools devTools;
    // private Map<String, Long> requestSizes = new HashMap<>();
    // private Map<String, Long> responseSizes = new HashMap<>();
    private final By usernameField = By.id("inputLoginUserName");

    @Parameters({"duration"})
    @Test
    public void testScenario1Adv(long duration) {
        test = extent.createTest("Scenario1TestAdv");
        long testStartTime = System.currentTimeMillis();
        long endTime = testStartTime + duration * 1000; // Convert duration to milliseconds
        System.out.println("Start Time :" + testStartTime);
        System.out.println("End Time :" + endTime);
        System.out.println("Duration is : " + duration);

        // Removed DevTools initialization and listeners

        String currentAction = "";

        while (System.currentTimeMillis() < endTime) {
            try {
                printCurrentTime();
                // Load test data
                String baseUrl = testData.getProperty("URL");
                String username = config.getProperty("username");
                String password = config.getProperty("password");
                String site = testData.getProperty("Site");
                String dateDebut = testData.getProperty("DateDebut");
                String commentaire = testData.getProperty("Commentaire");

                // Page Objects
                LoginPage loginPage = new LoginPage(driver);
                CustomerSearchPage customerSearchPage = new CustomerSearchPage(driver);
                AvailabilityPage availabilityPage = new AvailabilityPage(driver);
                ReservationPage reservationPage = new ReservationPage(driver);
                ConfirmationPage confirmationPage = new ConfirmationPage(driver);

                // Step 0: Login Page Loading
                currentAction = "LoginPage";
                sampler.sampleStart(currentAction);
                try {
                    loginPage.goToApp(baseUrl);
                    sampler.sampleEnd(currentAction, true);
                    logger.info("Navigated to {}", baseUrl);
                    test.info("Navigated to " + baseUrl);
                } catch (Exception e) {
                    sampler.sampleEnd(currentAction, false);
                    handleError(currentAction, e);
                }

                // Step 1: Authorization
                currentAction = "Authorization";
                loginPage.loginCred(username, password);
                sampler.sampleStart(currentAction);
                try {
                    loginPage.login();
                    sampler.sampleEnd(currentAction, true);
                    test.pass("Authorization successful.");
                } catch (Exception e) {
                    sampler.sampleEnd(currentAction, false);
                    handleError(currentAction, e);
                }

                // Step 2: Fill Client Data
                currentAction = "Fill Client Data";
                customerSearchPage.searchCustomerByNameData("SANTIER", "JEAN-MARC", "50350");
                sampler.sampleStart(currentAction);
                try {
                    customerSearchPage.selectCustomer();
                    sampler.sampleEnd(currentAction, true);
                    test.pass("Client data filled and customer selected.");
                } catch (Exception e) {
                    sampler.sampleEnd(currentAction, false);
                    handleError(currentAction, e);
                }

                // Step 3: Client Found
                currentAction = "Client Found";
                sampler.sampleStart(currentAction);
                try {
                    customerSearchPage.goToAvailability();
                    sampler.sampleEnd(currentAction, true);
                    test.pass("Navigated to Availability page.");
                } catch (Exception e) {
                    sampler.sampleEnd(currentAction, false);
                    handleError(currentAction, e);
                }

                // Step 4: Search Availability
                currentAction = "Search Availability";
                availabilityPage.enterStartDate(dateDebut);
                availabilityPage.selectSite(site);
                sampler.sampleStart(currentAction);
                try {
                    availabilityPage.searchAvailability();
                    sampler.sampleEnd(currentAction, true);
                    test.pass("Availability search performed.");
                } catch (Exception e) {
                    sampler.sampleEnd(currentAction, false);
                    handleError(currentAction, e);
                }

                // Step 5: Go To Reservation Detail
                currentAction = "GoToReservationDetail";
                sampler.sampleStart(currentAction);
                try {
                    availabilityPage.selectDestination();
                    sampler.sampleEnd(currentAction, true);
                    test.pass("Navigated to Reservation Details.");
                } catch (Exception e) {
                    sampler.sampleEnd(currentAction, false);
                    handleError(currentAction, e);
                }

                // Step 6: Fill Reservation Data
                currentAction = "Fill Reservation Data";
                sampler.sampleStart(currentAction);
                try {
                    reservationPage.fillReservationDetails(commentaire);
                    sampler.sampleEnd(currentAction, true);
                    test.pass("Reservation data filled.");
                } catch (Exception e) {
                    sampler.sampleEnd(currentAction, false);
                    handleError(currentAction, e);
                }

                // Get Booking Number (not sampled)
                try {
                    Thread.sleep(3000);
                    String bookingNumber = reservationPage.getBookingNumber();
                    reservationPage.clickOkOnBookingConfirmation();
                    Thread.sleep(3000);

                    logger.info("Booking Number: {}", bookingNumber);
                    test.info("Booking Number: " + bookingNumber);

                    // Assertions
                    Assert.assertNotNull(bookingNumber, "Booking number should not be null");
                    test.pass("Booking number is not null");
                } catch (Exception e) {
                    handleError("Get Booking Number", e);
                }

                // Step 7: Confirmation
                currentAction = "Confirmation";
                sampler.sampleStart(currentAction);
                try {
                    reservationPage.confirmBooking();
                    sampler.sampleEnd(currentAction, true);
                    test.pass("Reservation confirmed.");
                } catch (Exception e) {
                    sampler.sampleEnd(currentAction, false);
                    handleError(currentAction, e);
                }

                // Post-confirmation actions
                try {
                    Thread.sleep(1000);
                    confirmationPage.cancelBooking();
                    Thread.sleep(500);
                } catch (Exception e) {
                    handleError("Post-confirmation actions", e);
                }

            } catch (Exception e) {
                handleError("Unexpected Error", e);
            }
        }

        // After the loop, generate the performance reports
        try {
            PerformanceReportAdv report = new PerformanceReportAdv(sampler.getSamples());

            // Set the test start time in the report for accurate elapsed time calculation
            report.setTestStartTime(testStartTime);

            // Generate the reports
            report.generateSummaryReport("test-output/summary_report_adv.csv");
            report.generateAggregateReport("test-output/aggregate_report_adv.csv");
            report.generateResponseTimesOverTimeChart("test-output/response_times_over_time_adv.png");

            // Add the summary table to ExtentReports
            test.info("Summary Report (Advanced):");
            test.info(MarkupHelper.createTable(report.getSummaryTableData()));

            // Add the aggregate table to ExtentReports
            test.info("Aggregate Report (Advanced):");
            test.info(MarkupHelper.createTable(report.getAggregateTableData()));

            // Add the response times over time chart to ExtentReports
            if (new File("test-output/response_times_over_time_adv.png").exists()) {
                test.addScreenCaptureFromPath("test-output/response_times_over_time_adv.png", "Response Times Over Time (Scenario 1)");
            }
        } catch (IOException e) {
            handleError("Generating Reports", e);
        }
    }

    /**
     * Helper method to capture a screenshot.
     *
     * @param screenshotName The name for the screenshot file.
     * @return The path to the captured screenshot.
     */
    private String captureScreenshot(String screenshotName) {
        String screenshotPath = "test-output/screenshots/" + screenshotName + ".png";
        File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            File targetFile = new File(screenshotPath);
            FileUtils.copyFile(screenshotFile, targetFile);
            return targetFile.getAbsolutePath();
        } catch (IOException e) {
            logger.error("Failed to capture screenshot: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Helper method to handle errors and capture screenshots.
     *
     * @param currentAction The current action being executed.
     * @param e             The exception that occurred.
     */
    private void handleError(String currentAction, Exception e) {
        sampler.sampleEnd(currentAction, false);
        logger.error("Error during {}: {}", currentAction, e.getMessage());
        test.fail("Error during " + currentAction + ": " + e.getMessage());

        // Capture screenshot
        String screenshotPath = captureScreenshot(currentAction + "_failure");
        if (screenshotPath != null) {
            test.addScreenCaptureFromPath(screenshotPath);
        }
    }
    private void printCurrentTime(){
        // Get the current time
        LocalTime currentTime = LocalTime.now();

        // Format the time in HH:mm:ss format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTime = currentTime.format(formatter);

        // Print the formatted time
        System.out.println("Current time: " + formattedTime);
    }
}
