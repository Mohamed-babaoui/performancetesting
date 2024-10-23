package tests;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import pages.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;

/**
 * Advanced version of Scenario2TestSamp that includes performance sampling and reporting.
 */
public class Scenario2TestSampAdv extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(Scenario2TestSampAdv.class);

    // List to store performance data
    private List<PerformanceData> performanceDataList = new ArrayList<>();

    private final By usernameField = By.id("inputLoginUserName");

    @Parameters({"duration"})
    @Test
    public void testScenario2Adv(long duration) {
        test = extent.createTest("Scenario2TestAdv");
        long testStartTime = System.currentTimeMillis();
        long endTime = testStartTime + duration * 1000; // Convert duration to milliseconds

        String currentAction = "";

        while (System.currentTimeMillis() < endTime) {
            try {
                // Load data
                String baseUrl = testData.getProperty("URL");
                String username = config.getProperty("username");
                String password = config.getProperty("password");
                String site = testData.getProperty("Site");
                String dateDebut = testData.getProperty("DateDebut");
                String dateFin = testData.getProperty("DateFin");
                String commentaire = testData.getProperty("Commentaire");

                // Page Objects
                LoginPage loginPage = new LoginPage(driver);
                CustomerSearchPage customerSearchPage = new CustomerSearchPage(driver);
                AvailabilityPage availabilityPage = new AvailabilityPage(driver);
                ReservationPage reservationPage = new ReservationPage(driver);
                ConfirmationPage confirmationPage = new ConfirmationPage(driver);

                // Step 0: Login Page Loading
                currentAction = "LoginPage";
                ExtentTest actionNode = test.createNode(currentAction);
                long actionStartTime = System.currentTimeMillis();
                boolean success = true;
                String errorMessage = null;
                try {
                    loginPage.goToApp(baseUrl);
                    logger.info("Navigated to {}", baseUrl);
                    actionNode.pass("Navigated to " + baseUrl);
                } catch (Exception e) {
                    success = false;
                    errorMessage = e.getMessage();
                    handleError(currentAction, e, actionNode);
                }
                long responseTime = System.currentTimeMillis() - actionStartTime;
                performanceDataList.add(new PerformanceData(currentAction, responseTime, success, errorMessage, actionStartTime));

                // Step 1: Authorization
                currentAction = "Authorization";
                actionNode = test.createNode(currentAction);
                loginPage.loginCred(username, password);
                actionStartTime = System.currentTimeMillis();
                success = true;
                errorMessage = null;
                try {
                    loginPage.login();
                    actionNode.pass("Authorization successful.");
                } catch (Exception e) {
                    success = false;
                    errorMessage = e.getMessage();
                    handleError(currentAction, e, actionNode);
                }
                responseTime = System.currentTimeMillis() - actionStartTime;
                performanceDataList.add(new PerformanceData(currentAction, responseTime, success, errorMessage, actionStartTime));

                // Step 2: Search Customer by Email
                currentAction = "Fill Client Data";
                actionNode = test.createNode(currentAction);
                customerSearchPage.searchCustomerByEmail("cjmsantier@orange.fr");
                actionStartTime = System.currentTimeMillis();
                success = true;
                errorMessage = null;
                try {
                    customerSearchPage.selectCustomer();
                    actionNode.pass("Client data filled and customer selected.");
                } catch (Exception e) {
                    success = false;
                    errorMessage = e.getMessage();
                    handleError(currentAction, e, actionNode);
                }
                responseTime = System.currentTimeMillis() - actionStartTime;
                performanceDataList.add(new PerformanceData(currentAction, responseTime, success, errorMessage, actionStartTime));

                // Step 3: Go to Availability page
                currentAction = "Client Found";
                actionNode = test.createNode(currentAction);
                actionStartTime = System.currentTimeMillis();
                success = true;
                errorMessage = null;
                try {
                    customerSearchPage.goToAvailability();
                    actionNode.pass("Navigated to Availability page.");
                } catch (Exception e) {
                    success = false;
                    errorMessage = e.getMessage();
                    handleError(currentAction, e, actionNode);
                }
                responseTime = System.currentTimeMillis() - actionStartTime;
                performanceDataList.add(new PerformanceData(currentAction, responseTime, success, errorMessage, actionStartTime));

                // Step 4: Check Availability with End Date
                currentAction = "Search Availability";
                actionNode = test.createNode(currentAction);
                availabilityPage.enterStartAndEndDate(dateDebut, dateFin);
                availabilityPage.selectSite(site);
                actionStartTime = System.currentTimeMillis();
                success = true;
                errorMessage = null;
                try {
                    availabilityPage.searchAvailability();
                    actionNode.pass("Availability search performed.");
                } catch (Exception e) {
                    success = false;
                    errorMessage = e.getMessage();
                    handleError(currentAction, e, actionNode);
                }
                responseTime = System.currentTimeMillis() - actionStartTime;
                performanceDataList.add(new PerformanceData(currentAction, responseTime, success, errorMessage, actionStartTime));

                // Step 5: Go to Reservation Details
                currentAction = "GoToReservationDetail";
                actionNode = test.createNode(currentAction);
                actionStartTime = System.currentTimeMillis();
                success = true;
                errorMessage = null;
                try {
                    availabilityPage.selectDestination();
                    actionNode.pass("Navigated to Reservation Details.");
                } catch (Exception e) {
                    success = false;
                    errorMessage = e.getMessage();
                    handleError(currentAction, e, actionNode);
                }
                responseTime = System.currentTimeMillis() - actionStartTime;
                performanceDataList.add(new PerformanceData(currentAction, responseTime, success, errorMessage, actionStartTime));

                // Step 6: Fill Reservation Data
                currentAction = "Fill Reservation Data";
                actionNode = test.createNode(currentAction);
                actionStartTime = System.currentTimeMillis();
                success = true;
                errorMessage = null;
                try {
                    reservationPage.fillReservationDetails(commentaire);
                    actionNode.pass("Reservation data filled.");
                } catch (Exception e) {
                    success = false;
                    errorMessage = e.getMessage();
                    handleError(currentAction, e, actionNode);
                }
                responseTime = System.currentTimeMillis() - actionStartTime;
                performanceDataList.add(new PerformanceData(currentAction, responseTime, success, errorMessage, actionStartTime));

                // Get Booking Number
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
                    handleError("Get Booking Number", e, test);
                }

                // Step 7: Confirm Reservation
                currentAction = "Confirmation";
                actionNode = test.createNode(currentAction);
                actionStartTime = System.currentTimeMillis();
                success = true;
                errorMessage = null;
                try {
                    reservationPage.confirmBooking();
                    actionNode.pass("Reservation confirmed.");
                } catch (Exception e) {
                    success = false;
                    errorMessage = e.getMessage();
                    handleError(currentAction, e, actionNode);
                }
                responseTime = System.currentTimeMillis() - actionStartTime;
                performanceDataList.add(new PerformanceData(currentAction, responseTime, success, errorMessage, actionStartTime));

                // Post-confirmation actions
                try {
                    Thread.sleep(1000);
                    confirmationPage.cancelBooking();
                    Thread.sleep(500);
                } catch (Exception e) {
                    handleError("Post-confirmation actions", e, test);
                }

            } catch (Exception e) {
                handleError("Unexpected Error", e, test);
            }
        }

        // After the loop, generate the performance reports
        try {
            generatePerformanceReport();
        } catch (Exception e) {
            handleError("Generating Reports", e, test);
        }
    }

    /**
     * Helper method to generate performance report.
     */
    private void generatePerformanceReport() {
        String csvFile = "test-output/performance_data_scenario2.csv";
        try (FileWriter writer = new FileWriter(csvFile)) {
            // Write header
            writer.append("Action Name,Response Time (ms),Success,Error Message,Timestamp\n");
            for (PerformanceData data : performanceDataList) {
                writer.append(data.getActionName()).append(",");
                writer.append(String.valueOf(data.getResponseTime())).append(",");
                writer.append(String.valueOf(data.isSuccess())).append(",");
                writer.append(data.getErrorMessage() != null ? data.getErrorMessage().replace(",", ";") : "").append(",");
                writer.append(String.valueOf(data.getTimestamp())).append("\n");
            }
            writer.flush();
            logger.info("Performance data written to {}", csvFile);
            test.info("Performance data written to " + csvFile);

            // Optionally, add the CSV file as an attachment to the report
            test.addScreenCaptureFromPath(csvFile, "Performance Data CSV");

            // If you have methods to generate charts or tables, you can include them here
        } catch (IOException e) {
            logger.error("Error writing performance data: {}", e.getMessage());
            test.fail("Error writing performance data: " + e.getMessage());
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
     * @param actionNode    The ExtentTest node for logging.
     */
    private void handleError(String currentAction, Exception e, ExtentTest actionNode) {
        logger.error("Error during {}: {}", currentAction, e.getMessage());
        actionNode.fail("An error occurred during " + currentAction + ": " + e.getMessage());

        // Capture screenshot
        String screenshotPath = captureScreenshot(currentAction + "_failure");
        if (screenshotPath != null) {
            actionNode.addScreenCaptureFromPath(screenshotPath);
        }
    }
}
