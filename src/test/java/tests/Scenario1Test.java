package tests;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.openqa.selenium.WebDriver;
import pages.*;
import org.apache.commons.lang3.time.StopWatch;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.aventstack.extentreports.*;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.*;

public class Scenario1Test extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(Scenario1Test.class);
    private List<PerformanceData> performanceDataList = new ArrayList<>();
    private long testStartTime;

    @Parameters({"duration"})
    @Test
    public void testScenario1(long duration) {
        test = extent.createTest("Scenario1Test");
        testStartTime = System.currentTimeMillis();
        long endTime = testStartTime + duration * 1000; // Convert to milliseconds

        while (System.currentTimeMillis() < endTime) {
            StopWatch overallTestTime = new StopWatch();
            overallTestTime.start();

            try {
                // Load data
                String baseUrl = testData.getProperty("URL");
                String username = config.getProperty("username");
                String password = config.getProperty("password");
                String site = testData.getProperty("Site");
                String dateDebut = testData.getProperty("DateDebut");
                String commentaire = testData.getProperty("Commentaire");

                // Step 0: Login
                long loadingTime = pageLoading(driver,baseUrl);
                performanceDataList.add(new PerformanceData("LoginPage", loadingTime, true, null, System.currentTimeMillis()));
                logger.info("Navigated to {}", baseUrl);
                test.info("Navigated to " + baseUrl);

                // Page Objects
                LoginPage loginPage = new LoginPage(driver);
                CustomerSearchPage customerSearchPage = new CustomerSearchPage(driver);
                AvailabilityPage availabilityPage = new AvailabilityPage(driver);
                ReservationPage reservationPage = new ReservationPage(driver);
                ConfirmationPage confirmationPage = new ConfirmationPage(driver);


                // Step 1: Login
                long loginTime = performLogin(loginPage,username,password);
                performanceDataList.add(new PerformanceData("Authorisation", loginTime, true, null, System.currentTimeMillis()));

                // Step 2: Search Customer by Name
                long searchTime = searchCustomerByName(customerSearchPage, "SANTIER", "JEAN-MARC", "50350");
                performanceDataList.add(new PerformanceData("Fill Client Data", searchTime, true, null, System.currentTimeMillis()));

                // Step 3: Go to Availability page
                long goToAvailabilityPageTime = goToAvailabilityPage(customerSearchPage);
                performanceDataList.add(new PerformanceData(" Client Found", goToAvailabilityPageTime, true, null, System.currentTimeMillis()));


                // Step 4: Check Availability without End Date
                long availabilityTime = checkAvailabilityWithoutEndDate(availabilityPage, dateDebut, site);
                performanceDataList.add(new PerformanceData("Search Avai", availabilityTime, true, null, System.currentTimeMillis()));

                // Step 5: Go to reservations details
                long goToReservationDetailsTime = goToReservationDetails(availabilityPage);
                performanceDataList.add(new PerformanceData("GoToReservationDetail", goToReservationDetailsTime, true, null, System.currentTimeMillis()));
                // Step 6: Fill Reseravation Data
                long reservationDataTime= fillReservationData(reservationPage,commentaire);
                performanceDataList.add(new PerformanceData("Fill Data", reservationDataTime, true, null, System.currentTimeMillis()));

                // Get Booking Number

                String bookingNumber=getBookingNumber(reservationPage);
                logger.info("Booking Number: {}", bookingNumber);
                test.info("Booking Number: " + bookingNumber);

                // Step 7   : Fill Reservation And Confirmation
                long reservationTime = completeReservation(reservationPage);
                performanceDataList.add(new PerformanceData("Confirmation", reservationTime, true, null, System.currentTimeMillis()));

                confirmationPage.cancelBooking();
                Thread.sleep(500);

                // Assertions
                Assert.assertNotNull(bookingNumber, "Booking number should not be null");
                test.pass("Booking number is not null");

            } catch (Exception e) {
                logger.error("An error occurred during the test: {}", e.getMessage());
                test.fail("An error occurred: " + e.getMessage());

                // Record failure
                performanceDataList.add(new PerformanceData("Error", 0, false, e.getMessage(), System.currentTimeMillis()));
            } finally {
                overallTestTime.stop();
                logger.info("Total test iteration time: {} ms", overallTestTime.getTime());
                test.info("Total test iteration time: " + overallTestTime.getTime() + " ms");
                // Clean up or reset if necessary
               // driver.manage().deleteAllCookies();
            }
        }

        // After the loop, generate the reports
        try {
            generateReports();
        } catch (IOException e) {
            logger.error("Error generating reports: {}", e.getMessage());
            test.fail("Error generating reports: " + e.getMessage());
        }
    }

    private void generateReports() throws IOException {
        // Generate Summary and Aggregate Reports
        String[][] summaryTableData = generateSummaryAndAggregateReports();

        // Generate Response Times Over Time Chart
        File chartFile = generateResponseTimesChart();

        // Add the summary table and chart to ExtentReports
        test.info("Summary and Aggregate Reports:");
        test.info(MarkupHelper.createTable(summaryTableData));

        if (chartFile.exists()) {
            test.addScreenCaptureFromPath(chartFile.getAbsolutePath(), "Response Times Over Time");
        }
    }

    private String[][] generateSummaryAndAggregateReports() {
        List<String[]> rows = new ArrayList<>();

        // Header row
        String[] header = {"Action", "Samples", "Average (ms)", "Min (ms)", "Max (ms)", "Std Dev (ms)"};
        rows.add(header);

        Map<String, List<Long>> actionDurations = new LinkedHashMap<>();

        for (PerformanceData data : performanceDataList) {
            if (data.isSuccess() && !data.getActionName().equals("Error")) {
                actionDurations.computeIfAbsent(data.getActionName(), k -> new ArrayList<>()).add(data.getResponseTime());
            }
        }

        for (Map.Entry<String, List<Long>> entry : actionDurations.entrySet()) {
            String actionName = entry.getKey();
            List<Long> durations = entry.getValue();

            double average = durations.stream().mapToLong(Long::longValue).average().orElse(0);
            long min = durations.stream().mapToLong(Long::longValue).min().orElse(0);
            long max = durations.stream().mapToLong(Long::longValue).max().orElse(0);
            double stdDev = calculateStandardDeviation(durations, average);
            int samples = durations.size();

            String[] row = {
                    actionName,
                    String.valueOf(samples),
                    String.format("%.2f", average),
                    String.valueOf(min),
                    String.valueOf(max),
                    String.format("%.2f", stdDev)
            };
            rows.add(row);
        }

        // Convert the list to a 2D array
        String[][] data = rows.toArray(new String[0][]);
        return data;
    }

    private double calculateStandardDeviation(List<Long> durations, double mean) {
        double variance = durations.stream()
                .mapToDouble(d -> Math.pow(d - mean, 2))
                .average()
                .orElse(0);
        return Math.sqrt(variance);
    }

 /*   private File generateResponseTimesChart() throws IOException {
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        Map<String, TimeSeries> timeSeriesMap = new HashMap<>();

        for (PerformanceData data : performanceDataList) {
            if (data.isSuccess() && !data.getActionName().equals("Error")) {
                String actionName = data.getActionName();
                TimeSeries series = timeSeriesMap.computeIfAbsent(actionName, k -> new TimeSeries(actionName));
                series.addOrUpdate(new Millisecond(new Date(data.getTimestamp())), data.getResponseTime());
            }
        }

        for (TimeSeries series : timeSeriesMap.values()) {
            dataset.addSeries(series);
        }

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Response Times Over Time",
                "Time",
                "Response Time (ms)",
                dataset,
                true,
                true,
                false
        );

        File chartFile = new File("test-output/ResponseTimesOverTime.png");
        ChartUtils.saveChartAsPNG(chartFile, chart, 800, 600);
        return chartFile;
    }*/
 private File generateResponseTimesChart() throws IOException {
     XYSeriesCollection dataset = new XYSeriesCollection();
     Map<String, XYSeries> seriesMap = new HashMap<>();

     for (PerformanceData data : performanceDataList) {
         if (data.isSuccess() && !data.getActionName().equals("Error")) {
             String actionName = data.getActionName();
             // Initialize XYSeries with allowDuplicateXValues = true
             XYSeries series = seriesMap.computeIfAbsent(actionName, k -> new XYSeries(actionName, true, true));
             long elapsedMillis = data.getTimestamp() - testStartTime; // Calculate elapsed time
             series.add(elapsedMillis, data.getResponseTime());
         }
     }

     for (XYSeries series : seriesMap.values()) {
         dataset.addSeries(series);
     }

     JFreeChart chart = ChartFactory.createXYLineChart(
             "Response Times Over Time",
             "Elapsed Time (hh:mm:ss)",
             "Response Time (ms)",
             dataset,
             PlotOrientation.VERTICAL,
             true,
             true,
             false
     );

     // Format the x-axis to display hh:mm:ss
     NumberAxis domainAxis = (NumberAxis) chart.getXYPlot().getDomainAxis();
     domainAxis.setNumberFormatOverride(new NumberFormat() {
         @Override
         public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
             long millis = (long) number;
             long hours = TimeUnit.MILLISECONDS.toHours(millis);
             long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
             long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
             String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
             return toAppendTo.append(time);
         }

         @Override
         public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
             return format((double) number, toAppendTo, pos);
         }



         @Override
         public Number parse(String source, ParsePosition parsePosition) {
             return null; // Parsing not needed
         }
     });

     File chartFile = new File("test-output/ResponseTimesOverTime.png");
     ChartUtils.saveChartAsPNG(chartFile, chart, 800, 600);
     return chartFile;
 }

    private long pageLoading(WebDriver driver,String url) {
        StopWatch actionTime = new StopWatch();
        actionTime.start();
        driver.get(url);
        actionTime.stop();
        long duration = actionTime.getTime();
        test.info("Page Loading action took: " + duration + " ms");
        return duration;
    }

    private long performLogin(LoginPage loginPage,String username,String password) {
        StopWatch actionTime = new StopWatch();
        loginPage.loginCred(username,password);

        actionTime.start();
        loginPage.login();
        actionTime.stop();
        long duration = actionTime.getTime();
        test.info("Authorisation Action took: " + duration + " ms");
        return duration;
    }

    private long searchCustomerByName(CustomerSearchPage customerSearchPage, String lastName, String firstName, String postalCode) throws InterruptedException {
        StopWatch actionTime = new StopWatch();

        customerSearchPage.searchCustomerByNameData(lastName, firstName, postalCode);
        actionTime.start();
        customerSearchPage.selectCustomer();
        actionTime.stop();
        long duration = actionTime.getTime();
        test.info("Customer search by name action took: " + duration + " ms");
        return duration;
    }
    private long goToAvailabilityPage(CustomerSearchPage customerSearchPage) {
        StopWatch actionTime = new StopWatch();

        actionTime.start();
        customerSearchPage.goToAvailability();
        actionTime.stop();
        long duration = actionTime.getTime();
        test.info("Go to Availability page action took: " + duration + " ms");
        return duration;
    }

    private long checkAvailabilityWithoutEndDate(AvailabilityPage availabilityPage, String dateDebut, String site) throws InterruptedException {
        StopWatch actionTime = new StopWatch();

        availabilityPage.enterStartDate(dateDebut);
        availabilityPage.selectSite(site);
        actionTime.start();
        availabilityPage.searchAvailability();
        actionTime.stop();
        long duration = actionTime.getTime();
        test.info("Availability check without end date action took: " + duration + " ms");
        return duration;
    }
    private long goToReservationDetails(AvailabilityPage availabilityPage) throws InterruptedException {
        StopWatch actionTime = new StopWatch();


        actionTime.start();
        availabilityPage.selectDestination();
        actionTime.stop();
        long duration = actionTime.getTime();
        test.info("Go to Reservation details action took: " + duration + " ms");
        return duration;
    }


    private long completeReservation(ReservationPage reservationPage) throws InterruptedException {
        StopWatch actionTime = new StopWatch();

        actionTime.start();
        reservationPage.confirmBooking();
        actionTime.stop();
        long duration = actionTime.getTime();
        test.info("Reservation completion action took: " + duration + " ms");
        return duration;
    }

    private long  fillReservationData(ReservationPage reservationPage, String commentaire) throws InterruptedException {
        StopWatch actionTime = new StopWatch();
        actionTime.start();
        reservationPage.fillReservationDetails(commentaire);
        actionTime.stop();
        long duration = actionTime.getTime();
        test.info("Fill Reservation Data action took: " + duration + " ms");
        return duration;

    }
    private String  getBookingNumber(ReservationPage reservationPage) throws InterruptedException {
        String toReturn =reservationPage.getBookingNumber();
        reservationPage.clickOkOnBookingConfirmation();
        Thread.sleep(3000);
        return toReturn;
    }
}
