package tests;

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

public class Scenario1TestSampAdv extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(Scenario1TestSampAdv.class);

    // List to store performance data
    private List<PerformanceData> performanceDataList = new ArrayList<>();

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

        String currentAction = "";

        while (System.currentTimeMillis() < endTime) {
            try {
                System.out.println("Time Now is: " + System.currentTimeMillis());
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
                long actionStartTime = System.currentTimeMillis();
                boolean success = true;
                String errorMessage = null;
                try {
                    loginPage.goToApp(baseUrl);
                    logger.info("Navigated to {}", baseUrl);
                    test.info("Navigated to " + baseUrl);
                } catch (Exception e) {
                    success = false;
                    errorMessage = e.getMessage();
                    handleError(currentAction, e);
                }
                long responseTime = System.currentTimeMillis() - actionStartTime;
                performanceDataList.add(new PerformanceData(currentAction, responseTime, success, errorMessage, actionStartTime));

                // Step 1: Authorization
                currentAction = "Authorization";
                loginPage.loginCred(username, password);
                actionStartTime = System.currentTimeMillis();
                success = true;
                errorMessage = null;
                try {
                    loginPage.login();
                } catch (Exception e) {
                    success = false;
                    errorMessage = e.getMessage();
                    handleError(currentAction, e);
                }
                responseTime = System.currentTimeMillis() - actionStartTime;
                performanceDataList.add(new PerformanceData(currentAction, responseTime, success, errorMessage, actionStartTime));

                // Step 2: Fill Client Data
                currentAction = "Fill Client Data";
                customerSearchPage.searchCustomerByNameData("SANTIER", "JEAN-MARC", "50350");
                actionStartTime = System.currentTimeMillis();
                success = true;
                errorMessage = null;
                try {
                    customerSearchPage.selectCustomer();
                } catch (Exception e) {
                    success = false;
                    errorMessage = e.getMessage();
                    handleError(currentAction, e);
                }
                responseTime = System.currentTimeMillis() - actionStartTime;
                performanceDataList.add(new PerformanceData(currentAction, responseTime, success, errorMessage, actionStartTime));

                // Step 3: Client Found
                currentAction = "Client Found";
                actionStartTime = System.currentTimeMillis();
                success = true;
                errorMessage = null;
                try {
                    customerSearchPage.goToAvailability();
                } catch (Exception e) {
                    success = false;
                    errorMessage = e.getMessage();
                    handleError(currentAction, e);
                }
                responseTime = System.currentTimeMillis() - actionStartTime;
                performanceDataList.add(new PerformanceData(currentAction, responseTime, success, errorMessage, actionStartTime));

                // Step 4: Search Availability
                currentAction = "Search Availability";
                availabilityPage.enterStartDate(dateDebut);
                availabilityPage.selectSite(site);
                actionStartTime = System.currentTimeMillis();
                success = true;
                errorMessage = null;
                try {
                    availabilityPage.searchAvailability();
                } catch (Exception e) {
                    success = false;
                    errorMessage = e.getMessage();
                    handleError(currentAction, e);
                }
                responseTime = System.currentTimeMillis() - actionStartTime;
                performanceDataList.add(new PerformanceData(currentAction, responseTime, success, errorMessage, actionStartTime));

                // Step 5: Go To Reservation Detail
                currentAction = "GoToReservationDetail";
                actionStartTime = System.currentTimeMillis();
                success = true;
                errorMessage = null;
                try {
                    availabilityPage.selectDestination();
                } catch (Exception e) {
                    success = false;
                    errorMessage = e.getMessage();
                    handleError(currentAction, e);
                }
                responseTime = System.currentTimeMillis() - actionStartTime;
                performanceDataList.add(new PerformanceData(currentAction, responseTime, success, errorMessage, actionStartTime));

                // Step 6: Fill Reservation Data
                currentAction = "Fill Reservation Data";
                actionStartTime = System.currentTimeMillis();
                success = true;
                errorMessage = null;
                try {
                    reservationPage.fillReservationDetails(commentaire);
                } catch (Exception e) {
                    success = false;
                    errorMessage = e.getMessage();
                    handleError(currentAction, e);
                }
                responseTime = System.currentTimeMillis() - actionStartTime;
                performanceDataList.add(new PerformanceData(currentAction, responseTime, success, errorMessage, actionStartTime));

                // Get Booking Number (not measured as an action)
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
                actionStartTime = System.currentTimeMillis();
                success = true;
                errorMessage = null;
                try {
                    reservationPage.confirmBooking();
                } catch (Exception e) {
                    success = false;
                    errorMessage = e.getMessage();
                    handleError(currentAction, e);
                }
                responseTime = System.currentTimeMillis() - actionStartTime;
                performanceDataList.add(new PerformanceData(currentAction, responseTime, success, errorMessage, actionStartTime));

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
            generatePerformanceReport();
        } catch (Exception e) {
            handleError("Generating Reports", e);
        }
    }

    /**
     * Helper method to generate performance report.
     */
    private void generatePerformanceReport() {
        // You can implement the logic to generate the report using performanceDataList
        // For example, write to CSV, generate charts, etc.
        // Here's a simple example of writing to a CSV file

        String csvFile = "test-output/performance_data.csv";
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

            // You can also integrate the data into ExtentReports or generate charts as needed
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
     */
    private void handleError(String currentAction, Exception e) {
        logger.error("Error during {}: {}", currentAction, e.getMessage());
        test.fail("Error during " + currentAction + ": " + e.getMessage());

        // Capture screenshot
        String screenshotPath = captureScreenshot(currentAction + "_failure");
        if (screenshotPath != null) {
            test.addScreenCaptureFromPath(screenshotPath);
        }
    }
}
