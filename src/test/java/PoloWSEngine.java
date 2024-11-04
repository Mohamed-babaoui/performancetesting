import io.gatling.app.Gatling;
import io.gatling.core.config.GatlingPropertiesBuilder;
import simulations.PoloWSSimulation;
import simulations.PoloWeb1Simulation;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class PoloWSEngine {
    public static void main(String[] args) throws IOException {
        GatlingPropertiesBuilder props = new GatlingPropertiesBuilder()
                .resourcesDirectory(IDEPathHelper.mavenResourcesDirectory.toString())
                .resultsDirectory(IDEPathHelper.resultsDirectory.toString())
                .binariesDirectory(IDEPathHelper.mavenBinariesDirectory.toString())
                .simulationClass(PoloWSSimulation.class.getName());

        Gatling.fromMap(props.build());

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

        System.exit(0);
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
