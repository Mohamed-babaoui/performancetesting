import config.utils.Methods;
import io.gatling.app.Gatling;
import io.gatling.core.config.GatlingPropertiesBuilder;
import org.knowm.xchart.*;
import simulations.PoloWSSimulation;
import simulations.PoloWeb1Simulation;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import org.knowm.xchart.style.Styler;

import javax.mail.MessagingException;

class RequestData {
    String requestName;
    double averageMs;

    public RequestData(String requestName, double averageMs) {
        this.requestName = requestName;
        this.averageMs = averageMs;
    }
}

class ComparisonData {
    String requestName;
    double oldAverage;
    double newAverage;
    double difference;

    public ComparisonData(String requestName, double oldAverage, double newAverage) {
        this.requestName = requestName;
        this.oldAverage = oldAverage;
        this.newAverage = newAverage;
        this.difference = calculatePercentageDifference(oldAverage, newAverage);

    }

    private double calculatePercentageDifference(double oldAverage, double newAverage) {
        if (oldAverage == 0) return 0; // Avoid division by zero
        double difference = ((oldAverage - newAverage) / oldAverage) * 100;
        return difference;
    }

    @Override
    public String toString() {
        return String.format("%-20s %-15.2f %-15.2f %+,13.2f%%", requestName, oldAverage, newAverage, difference);
    }
}

public class PoloWSEngine {

    public String Conclusion = null;
    public static void main(String[] args) throws IOException, MessagingException {

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy '-' HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        Date simulationStart = new Date(System.currentTimeMillis());

        GatlingPropertiesBuilder props = new GatlingPropertiesBuilder()
                .resourcesDirectory(IDEPathHelper.mavenResourcesDirectory.toString())
                .resultsDirectory(IDEPathHelper.resultsDirectory.toString())
                .binariesDirectory(IDEPathHelper.mavenBinariesDirectory.toString())
                .simulationClass(PoloWSSimulation.class.getName());

        Gatling.fromMap(props.build());

        Date simulationEnd = new Date(System.currentTimeMillis());

        Path resultsDir = Paths.get("target/gatling");
        String latestExecutionFolder = getLatestExecutionFolder(resultsDir.toFile());
        String logFilePath = "target/gatling/" + latestExecutionFolder + "/simulation.log";
        String currentSimulationDir = "target/gatling/" + latestExecutionFolder;

        if (latestExecutionFolder == null) {
            System.out.println("No simulation results found.");
            return;
        }
        System.out.println("Latest simulation result folder: " + latestExecutionFolder);

        Map<String, RequestMetrics> metricsMap = new HashMap<>();
        long[] timeBounds = {Long.MAX_VALUE, Long.MIN_VALUE}; // For start and end time

        try (BufferedReader reader = new BufferedReader(new FileReader(logFilePath))) {
            reader.lines().filter(line -> line.startsWith("REQUEST")).forEach(line -> processLogLine(line, metricsMap, timeBounds));
        } catch (IOException e) {
            e.printStackTrace();
        }

        printMetricsSummary(metricsMap, timeBounds, currentSimulationDir);

        System.out.println("1111111111111111111111111111111111111111111111111111111111111");

        String oldFilePath = "src/test/oldWS/summary.txt";
        String newFilePath = currentSimulationDir+"/output.txt";
        String outputFilePath = currentSimulationDir+"/compare.txt";


        // Call the comparison function
        String conclusion = "";
        conclusion = compareAverages(oldFilePath, newFilePath, outputFilePath).replaceAll("\\s", "");

        Map<String, List<Long>> elapsedTimesByRequestType = new HashMap<>();
        Map<String, List<Long>> responseTimesByRequestType = new HashMap<>();

        parseRequestDurations(currentSimulationDir+"/simulation.log", elapsedTimesByRequestType, responseTimesByRequestType);
        adjustElapsedTimesToStartFromZero(elapsedTimesByRequestType);
        // Create and save the chart as an image
        if (elapsedTimesByRequestType.isEmpty() || responseTimesByRequestType.isEmpty()) {
            System.err.println("No valid request durations found. Please check the input file.");
        } else {
            createAndSaveLineChart(elapsedTimesByRequestType, responseTimesByRequestType, currentSimulationDir+"/graph.png");
        }

        // Create and show the line chart
        /*createAndSaveLineChart(requestDurations, currentSimulationDir+"/grapgh.png");*/
        /*System.exit(0);*/


        String content = Files.readString(Path.of("src/test/oldWS/info.txt"));
        String oldStartDate = content.split(",")[0];
        String oldVersion = content.split(",")[1];
        Methods.sendMail(currentSimulationDir+"/graph.png", newFilePath, oldFilePath, outputFilePath, simulationStart, simulationEnd, conclusion, oldStartDate, oldVersion);
    }

    private static void parseRequestDurations(String filePath,
                                              Map<String, List<Long>> elapsedTimesByRequestType,
                                              Map<String, List<Long>> responseTimesByRequestType) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("REQUEST")) {
                    String[] tokens = line.split("\\s+");

                    if (tokens.length >= 5) {
                        String requestType = tokens[1];
                        long startTime = Long.parseLong(tokens[2]);
                        long endTime = Long.parseLong(tokens[3]);
                        long duration = endTime - startTime;

                        elapsedTimesByRequestType
                                .computeIfAbsent(requestType, k -> new ArrayList<>())
                                .add(startTime);
                        responseTimesByRequestType
                                .computeIfAbsent(requestType, k -> new ArrayList<>())
                                .add(duration);
                    } else {
                        System.err.println("Skipping line (not enough tokens): " + line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void adjustElapsedTimesToStartFromZero(Map<String, List<Long>> elapsedTimesByRequestType) {
        // Find the minimum start time across all request types
        long minStartTime = Long.MAX_VALUE;
        for (List<Long> times : elapsedTimesByRequestType.values()) {
            if (!times.isEmpty()) {
                minStartTime = Math.min(minStartTime, times.get(0));
            }
        }

        // Adjust each start time by subtracting the minimum start time
        for (List<Long> times : elapsedTimesByRequestType.values()) {
            for (int i = 0; i < times.size(); i++) {
                times.set(i, times.get(i) - minStartTime);
            }
        }
    }

    private static void createAndSaveLineChart(Map<String, List<Long>> elapsedTimesByRequestType,
                                               Map<String, List<Long>> responseTimesByRequestType,
                                               String outputImagePath) {
        XYChart chart = new XYChartBuilder()
                .width(1024)
                .height(600)
                .title("Response Times Over Time")
                .xAxisTitle("Elapsed Time (ms)")
                .yAxisTitle("Response Time (ms)")
                .build();

        chart.getStyler().setLegendPosition(Styler.LegendPosition.OutsideE);

        for (String requestType : elapsedTimesByRequestType.keySet()) {
            List<Long> elapsedTimes = elapsedTimesByRequestType.get(requestType);
            List<Long> responseTimes = responseTimesByRequestType.get(requestType);
            chart.addSeries(requestType, elapsedTimes, responseTimes);
        }

        try {
            if (chart.getSeriesMap().isEmpty()) {
                System.err.println("Error: No valid data to plot on the chart.");
                return;
            }

            BitmapEncoder.saveBitmap(chart, outputImagePath, BitmapEncoder.BitmapFormat.PNG);
            System.out.println("Chart saved as: " + outputImagePath);
        } catch (IOException e) {
            System.err.println("Error saving chart: " + e.getMessage());
        }
    }

    public static String compareAverages(String oldFilePath, String newFilePath, String outputFilePath) {
        Map<String, Double> oldAverages = parseAverages(oldFilePath);
        Map<String, Double> newAverages = parseAverages(newFilePath);

        String totalConclusion = "";

        List<ComparisonData> comparisons = new ArrayList<>();
        double oldTotal = 0, newTotal = 0;

        for (String requestName : newAverages.keySet()) {
            if (requestName.equals("Total"))
                continue;
            double oldAverage = oldAverages.getOrDefault(requestName, 0.0);
            double newAverage = newAverages.get(requestName);
            comparisons.add(new ComparisonData(requestName, oldAverage, newAverage));
            oldTotal += oldAverage;
            newTotal += newAverage;
        }

        for (String requestName : newAverages.keySet()) {
            if (!requestName.equals("Total"))
                continue;
            double oldAverage = oldAverages.getOrDefault(requestName, 0.0);
            double newAverage = newAverages.get(requestName);
            comparisons.add(new ComparisonData(requestName, oldAverage, newAverage));
            if (oldAverage == 0)
                totalConclusion = "0%"; // Avoid division by zero
            else
                totalConclusion = String.format("%+,13.2f%%", ((oldAverage - newAverage) / oldAverage) * 100);
            oldTotal += oldAverage;
            newTotal += newAverage;
        }

        // Add total comparison
        /*comparisons.add(new ComparisonData("Total", oldTotal, newTotal));*/

        // Prepare the output content
        StringBuilder outputContent = new StringBuilder();
        outputContent.append(String.format("%-20s %-15s %-15s %-15s%n", "Request Name", "Old Average (ms)", "New Average (ms)", "Difference"));
        comparisons.forEach(data -> outputContent.append(data.toString()).append("\n"));

        // Write to the output file
        try {
            Files.writeString(Path.of(outputFilePath), outputContent.toString());
            System.out.println("Comparison results written to: " + outputFilePath);
        } catch (IOException e) {
            System.err.println("Failed to write to the output file: " + e.getMessage());
        }
        return totalConclusion;
    }

    private static Map<String, Double> parseAverages(String filePath) {
        Map<String, Double> averages = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Skip header line
            while ((line = br.readLine()) != null) {
                String[] tokens = line.trim().split("\\s+");
                if (tokens.length < 3) continue;
                String requestName = tokens[0];
                double averageMs = Double.parseDouble(tokens[2]);
                averages.put(requestName, averageMs);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return averages;
    }

    private static void processLogLine(String line, Map<String, RequestMetrics> metricsMap, long[] timeBounds) {
        String[] parts = line.split("\\s+");
        if (parts.length >= 5) {
            String requestName = parts[1];
            long requestStartTime = Long.parseLong(parts[2]);
            long requestEndTime = Long.parseLong(parts[3]);
            boolean isError = "KO".equals(parts[4]);
            long responseTime = requestEndTime - requestStartTime;

            timeBounds[0] = Math.min(timeBounds[0], requestStartTime); // start time
            timeBounds[1] = Math.max(timeBounds[1], requestEndTime);   // end time

            metricsMap.computeIfAbsent(requestName, RequestMetrics::new)
                    .addResponseTime(responseTime, isError);
        }
    }

    private static void printMetricsSummary(Map<String, RequestMetrics> metricsMap, long[] timeBounds, String currentSimulationDir) throws IOException {
        long totalDurationMillis = timeBounds[1] - timeBounds[0];
        double totalCount = 0, totalResponseTime = 0, totalErrorCount = 0, totalThroughput = 0;
        List<Long> allResponseTimes = new ArrayList<>();

        String outputFilePath = currentSimulationDir+"/output.txt";

        PrintWriter writer = new PrintWriter(new FileWriter(outputFilePath));

        writer.printf("%-20s %-15s %-15s %-15s %-15s %-15s %-15s %-15s%n",
                "Request Name", "Count", "Average (ms)", "Min (ms)", "Max (ms)", "Stdev (ms)", "Error (%)", "Throughput (rq/s)");

        for (RequestMetrics metrics : metricsMap.values()) {
            writer.printf("%-20s %-15d %-15.2f %-15d %-15d %-15.2f %-15.2f %-15.2f%n",
                    metrics.name, metrics.requestCount, metrics.getAverageResponseTime(),
                    metrics.minResponseTime, metrics.maxResponseTime, metrics.getStandardDeviation(),
                    metrics.getErrorPercentage(), metrics.getThroughput(20));

            totalCount += metrics.requestCount;
            totalResponseTime += metrics.totalResponseTime;
            totalErrorCount += metrics.errorCount;
            totalThroughput += metrics.getThroughput(20);
            allResponseTimes.addAll(metrics.responseTimes);
        }


        double totalStdev = allResponseTimes.size() > 1 ? calculateStandardDeviation(allResponseTimes) : 0;
        writer.printf("%-20s %-15d %-15.2f %-15d %-15d %-15.2f %-15.2f %-15.2f%n", "Total", (int) totalCount,
                totalResponseTime / totalCount, metricsMap.values().stream().mapToLong(m -> m.minResponseTime).min().orElse(0),
                metricsMap.values().stream().mapToLong(m -> m.maxResponseTime).max().orElse(0), totalStdev,
                (totalCount > 0 ? (totalErrorCount / totalCount * 100) : 0), totalThroughput);

        writer.flush();
    }

    private static double calculateStandardDeviation(List<Long> responseTimes) {
        double mean = responseTimes.stream().mapToDouble(Long::doubleValue).average().orElse(0);
        return Math.sqrt(responseTimes.stream().mapToDouble(rt -> Math.pow(rt - mean, 2)).sum() / responseTimes.size());
    }

    private static String getLatestExecutionFolder(File resultsDirectory) {
        return Optional.ofNullable(resultsDirectory.listFiles(File::isDirectory))
                .map(dirs -> Arrays.stream(dirs).max(Comparator.comparingLong(File::lastModified)).map(File::getName).orElse(null))
                .orElse(null);
    }

    static class RequestMetrics {
        String name;
        long totalResponseTime = 0, requestCount = 0, minResponseTime = Long.MAX_VALUE, maxResponseTime = Long.MIN_VALUE;
        int errorCount = 0;
        List<Long> responseTimes = new ArrayList<>();

        RequestMetrics(String name) {
            this.name = name;
        }

        void addResponseTime(long responseTime, boolean isError) {
            requestCount++;
            totalResponseTime += responseTime;
            minResponseTime = Math.min(minResponseTime, responseTime);
            maxResponseTime = Math.max(maxResponseTime, responseTime);
            responseTimes.add(responseTime);
            if (isError) errorCount++;
        }

        double getAverageResponseTime() {
            return requestCount > 0 ? (double) totalResponseTime / requestCount : 0;
        }

        double getThroughput(int totalSimulationTime) {
            return totalResponseTime > 0 ? (double) requestCount / ((double) totalSimulationTime) : 0;
        }

        double getStandardDeviation() {
            double mean = getAverageResponseTime();
            return Math.sqrt(responseTimes.stream().mapToDouble(rt -> Math.pow(rt - mean, 2)).sum() / responseTimes.size());
        }

        double getErrorPercentage() {
            return requestCount > 0 ? (double) errorCount / requestCount * 100 : 0;
        }
    }
}
