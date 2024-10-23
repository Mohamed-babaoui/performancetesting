package tests;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v113.network.Network;
import org.openqa.selenium.devtools.v113.network.model.Request;
import org.openqa.selenium.devtools.v113.network.model.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import pages.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Advanced version of Scenario2TestSamp that includes performance sampling and reporting.
 */
public class Scenario2TestSampAdv extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(Scenario2TestSampAdv.class);

    // Instantiate the PerformanceSamplerAdv
    private PerformanceSamplerAdv sampler = new PerformanceSamplerAdv();

    // DevTools and network data collections
    private DevTools devTools;
    private Map<String, Long> requestSizes = new HashMap<>();
    private Map<String, Long> responseSizes = new HashMap<>();
    private final By usernameField = By.id("inputLoginUserName");


    @Parameters({"duration"})
    @Test
    public void testScenario2Adv(long duration) {
        test = extent.createTest("Scenario2TestAdv");
        long testStartTime = System.currentTimeMillis();
        long endTime = testStartTime + duration * 1000; // Convert duration to milliseconds

        // Initialize DevTools for network monitoring
        devTools = ((ChromeDriver) driver).getDevTools();
        devTools.createSession();

        // Enable Network tracking
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

        // Set up listeners to capture request and response sizes
        devTools.addListener(Network.requestWillBeSent(), requestSent -> {
            String requestId = requestSent.getRequestId().toString();
            Request request = requestSent.getRequest();
            long requestSize = estimateRequestSize(request);
            synchronized (requestSizes) {
                requestSizes.put(requestId, requestSize);
            }
        });

        devTools.addListener(Network.responseReceived(), responseReceived -> {
            String requestId = responseReceived.getRequestId().toString();
            Response response = responseReceived.getResponse();
            long responseSize = (long) response.getEncodedDataLength();
            synchronized (responseSizes) {
                responseSizes.put(requestId, responseSize);
            }
        });

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
                sampler.sampleStart(currentAction);
                try {
                    loginPage.goToApp(baseUrl);
                    long bytesSent = getTotalBytesSent();
                    long bytesReceived = getTotalBytesReceived();
                    sampler.sampleEnd(currentAction, true, bytesSent, bytesReceived);
                    logger.info("Navigated to {}", baseUrl);
                    actionNode.pass("Navigated to " + baseUrl);
                } catch (Exception e) {
                    long bytesSent = getTotalBytesSent();
                    long bytesReceived = getTotalBytesReceived();
                    sampler.sampleEnd(currentAction, false, bytesSent, bytesReceived);
                    logger.error("Error during {}: {}", currentAction, e.getMessage());
                    actionNode.fail("An error occurred: " + e.getMessage());
                }

                // Step 1: Authorization
                currentAction = "Authorization";
                actionNode = test.createNode(currentAction);
                loginPage.loginCred(username, password);
                sampler.sampleStart(currentAction);
                try {
                    loginPage.login();
                    long bytesSent = getTotalBytesSent();
                    long bytesReceived = getTotalBytesReceived();
                    sampler.sampleEnd(currentAction, true, bytesSent, bytesReceived);
                    actionNode.pass("Authorization successful.");
                } catch (Exception e) {
                    long bytesSent = getTotalBytesSent();
                    long bytesReceived = getTotalBytesReceived();
                    sampler.sampleEnd(currentAction, false, bytesSent, bytesReceived);
                    logger.error("Error during {}: {}", currentAction, e.getMessage());
                    actionNode.fail("An error occurred: " + e.getMessage());
                }

                // Step 2: Search Customer by Email
                currentAction = "Fill Client Data";
                actionNode = test.createNode(currentAction);
                customerSearchPage.searchCustomerByEmail("cjmsantier@orange.fr");
                sampler.sampleStart(currentAction);
                try {
                    customerSearchPage.selectCustomer();
                    long bytesSent = getTotalBytesSent();
                    long bytesReceived = getTotalBytesReceived();
                    sampler.sampleEnd(currentAction, true, bytesSent, bytesReceived);
                    actionNode.pass("Client data filled and customer selected.");
                } catch (Exception e) {
                    long bytesSent = getTotalBytesSent();
                    long bytesReceived = getTotalBytesReceived();
                    sampler.sampleEnd(currentAction, false, bytesSent, bytesReceived);
                    logger.error("Error during {}: {}", currentAction, e.getMessage());
                    actionNode.fail("An error occurred: " + e.getMessage());
                }

                // Step 3: Go to Availability page
                currentAction = "Client Found";
                actionNode = test.createNode(currentAction);
                sampler.sampleStart(currentAction);
                try {
                    customerSearchPage.goToAvailability();
                    long bytesSent = getTotalBytesSent();
                    long bytesReceived = getTotalBytesReceived();
                    sampler.sampleEnd(currentAction, true, bytesSent, bytesReceived);
                    actionNode.pass("Navigated to Availability page.");
                } catch (Exception e) {
                    long bytesSent = getTotalBytesSent();
                    long bytesReceived = getTotalBytesReceived();
                    sampler.sampleEnd(currentAction, false, bytesSent, bytesReceived);
                    logger.error("Error during {}: {}", currentAction, e.getMessage());
                    actionNode.fail("An error occurred: " + e.getMessage());
                }

                // Step 4: Check Availability with End Date
                currentAction = "Search Availability";
                actionNode = test.createNode(currentAction);
                availabilityPage.enterStartAndEndDate(dateDebut, dateFin);
                availabilityPage.selectSite(site);
                sampler.sampleStart(currentAction);
                try {
                    availabilityPage.searchAvailability();
                    long bytesSent = getTotalBytesSent();
                    long bytesReceived = getTotalBytesReceived();
                    sampler.sampleEnd(currentAction, true, bytesSent, bytesReceived);
                    actionNode.pass("Availability search performed.");
                } catch (Exception e) {
                    long bytesSent = getTotalBytesSent();
                    long bytesReceived = getTotalBytesReceived();
                    sampler.sampleEnd(currentAction, false, bytesSent, bytesReceived);
                    logger.error("Error during {}: {}", currentAction, e.getMessage());
                    actionNode.fail("An error occurred: " + e.getMessage());
                }

                // Step 5: Go to Reservation Details
                currentAction = "GoToReservationDetail";
                actionNode = test.createNode(currentAction);
                sampler.sampleStart(currentAction);
                try {
                    availabilityPage.selectDestination();
                    long bytesSent = getTotalBytesSent();
                    long bytesReceived = getTotalBytesReceived();
                    sampler.sampleEnd(currentAction, true, bytesSent, bytesReceived);
                    actionNode.pass("Navigated to Reservation Details.");
                } catch (Exception e) {
                    long bytesSent = getTotalBytesSent();
                    long bytesReceived = getTotalBytesReceived();
                    sampler.sampleEnd(currentAction, false, bytesSent, bytesReceived);
                    logger.error("Error during {}: {}", currentAction, e.getMessage());
                    actionNode.fail("An error occurred: " + e.getMessage());
                }

                // Step 6: Fill Reservation Data
                currentAction = "Fill Reservation Data";
                actionNode = test.createNode(currentAction);
                sampler.sampleStart(currentAction);
                try {
                    reservationPage.fillReservationDetails(commentaire);
                    long bytesSent = getTotalBytesSent();
                    long bytesReceived = getTotalBytesReceived();
                    sampler.sampleEnd(currentAction, true, bytesSent, bytesReceived);
                    actionNode.pass("Reservation data filled.");
                } catch (Exception e) {
                    long bytesSent = getTotalBytesSent();
                    long bytesReceived = getTotalBytesReceived();
                    sampler.sampleEnd(currentAction, false, bytesSent, bytesReceived);
                    logger.error("Error during {}: {}", currentAction, e.getMessage());
                    actionNode.fail("An error occurred: " + e.getMessage());
                }

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
                    logger.error("Error getting booking number: {}", e.getMessage());
                    test.fail("Error getting booking number: " + e.getMessage());
                }

                // Step 7: Confirm Reservation
                currentAction = "Confirmation";
                actionNode = test.createNode(currentAction);
                sampler.sampleStart(currentAction);
                try {
                    reservationPage.confirmBooking();
                    long bytesSent = getTotalBytesSent();
                    long bytesReceived = getTotalBytesReceived();
                    sampler.sampleEnd(currentAction, true, bytesSent, bytesReceived);
                    actionNode.pass("Reservation confirmed.");
                } catch (Exception e) {
                    long bytesSent = getTotalBytesSent();
                    long bytesReceived = getTotalBytesReceived();
                    sampler.sampleEnd(currentAction, false, bytesSent, bytesReceived);
                    logger.error("Error during {}: {}", currentAction, e.getMessage());
                    actionNode.fail("An error occurred: " + e.getMessage());
                }

                // Post-confirmation actions
                try {
                    Thread.sleep(1000);
                    confirmationPage.cancelBooking();
                    Thread.sleep(500);
                } catch (Exception e) {
                    logger.error("Error during post-confirmation actions: {}", e.getMessage());
                    test.fail("Error during post-confirmation actions: " + e.getMessage());
                }

            } catch (Exception e) {
                logger.error("An unexpected error occurred during the test: {}", e.getMessage());
                test.fail("An unexpected error occurred: " + e.getMessage());
            } finally {
                // Clean up or reset if necessary
                // driver.manage().deleteAllCookies();
            }
        }

        // After the loop, generate the performance reports
        try {
            PerformanceReportAdv report = new PerformanceReportAdv(sampler.getSamples());

            // Set the test start time in the report for accurate elapsed time calculation
            report.setTestStartTime(testStartTime);

            // Generate the reports
            report.generateSummaryReport("test-output/summary_report_adv_scenario2.csv");
            report.generateAggregateReport("test-output/aggregate_report_adv_scenario2.csv");
            report.generateResponseTimesOverTimeChart("test-output/response_times_over_time_adv_scenario2.png");

            // Add the summary table to ExtentReports
            test.info("Summary Report (Advanced):");
            test.info(MarkupHelper.createTable(report.getSummaryTableData()));

            // Add the aggregate table to ExtentReports
            test.info("Aggregate Report (Advanced):");
            test.info(MarkupHelper.createTable(report.getAggregateTableData()));

            // Add the response times over time chart to ExtentReports
            if (new File("test-output/response_times_over_time_adv_scenario2.png").exists()) {
                test.addScreenCaptureFromPath("test-output/response_times_over_time_adv_scenario2.png", "Response Times Over Time (Scenario 2)");
            }
        } catch (IOException e) {
            logger.error("Error generating reports: {}", e.getMessage());
            test.fail("Error generating reports: " + e.getMessage());
        }
    }

    /**
     * Helper method to estimate the size of the request.
     *
     * @param request The request object.
     * @return The estimated size of the request in bytes.
     */
    private long estimateRequestSize(Request request) {
        long size = 0;
        // Estimate headers size
        if (request.getHeaders() != null) {
            size += request.getHeaders().toJson().toString().getBytes().length;
        }
        // Estimate body size if available
        if (request.getHasPostData().isPresent() && request.getHasPostData().get()) {
            size += request.getPostData().orElse("").getBytes().length;
        }
        return size;
    }

    /**
     * Calculates the total bytes sent since the last read and resets the counter.
     *
     * @return The total bytes sent.
     */
    private long getTotalBytesSent() {
        synchronized (requestSizes) {
            long total = requestSizes.values().stream().mapToLong(Long::longValue).sum();
            requestSizes.clear(); // Clear after reading
            return total;
        }
    }

    /**
     * Calculates the total bytes received since the last read and resets the counter.
     *
     * @return The total bytes received.
     */
    private long getTotalBytesReceived() {
        synchronized (responseSizes) {
            long total = responseSizes.values().stream().mapToLong(Long::longValue).sum();
            responseSizes.clear(); // Clear after reading
            return total;
        }
    }
}
