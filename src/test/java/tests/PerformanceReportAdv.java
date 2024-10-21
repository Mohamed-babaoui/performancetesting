package tests;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PerformanceReportAdv {

    private Map<String, List<PerformanceSamplerAdv.SampleResult>> samples;

    public PerformanceReportAdv(Map<String, List<PerformanceSamplerAdv.SampleResult>> samples) {
        this.samples = samples;
    }

    private long testStartTime = System.currentTimeMillis(); // Initialize with current time

    public void setTestStartTime(long testStartTime) {
        this.testStartTime = testStartTime;
    }

    public long getTestStartTime() {
        return testStartTime;
    }

    /**
     * Generates the summary report CSV file.
     *
     * @param filePath The path to the output CSV file.
     * @throws IOException If an I/O error occurs.
     */
    public void generateSummaryReport(String filePath) throws IOException {
        List<String[]> rows = new ArrayList<>();
        String[] header = {
                "Label",
                "# Samples",
                "Average (ms)",
                "Min (ms)",
                "Max (ms)",
                "Std. Dev.",
                "Error %",
                "Throughput (samples/sec)",
                "Received KB/sec",
                "Sent KB/sec",
                "Avg. Bytes"
        };
        rows.add(header);

        for (Map.Entry<String, List<PerformanceSamplerAdv.SampleResult>> entry : samples.entrySet()) {
            String label = entry.getKey();
            List<PerformanceSamplerAdv.SampleResult> results = entry.getValue();

            int totalSamples = results.size();
            int successSamples = (int) results.stream().filter(PerformanceSamplerAdv.SampleResult::isSuccess).count();
            int failedSamples = totalSamples - successSamples;
            double errorPercentage = ((double) failedSamples / totalSamples) * 100;

            List<Long> durations = results.stream()
                    .map(PerformanceSamplerAdv.SampleResult::getDuration)
                    .collect(Collectors.toList());

            double average = durations.stream().mapToLong(Long::longValue).average().orElse(0);
            long min = durations.stream().mapToLong(Long::longValue).min().orElse(0);
            long max = durations.stream().mapToLong(Long::longValue).max().orElse(0);
            double stdDev = calculateStandardDeviation(durations, average);

            // Calculate Throughput
            long firstSampleTime = results.stream().mapToLong(PerformanceSamplerAdv.SampleResult::getStartTime).min().orElse(0);
            long lastSampleTime = results.stream().mapToLong(PerformanceSamplerAdv.SampleResult::getEndTime).max().orElse(0);
            double totalTestTimeSec = (lastSampleTime - firstSampleTime) / 1000.0;
            double throughput = totalTestTimeSec > 0 ? totalSamples / totalTestTimeSec : 0;

            // Calculate Received and Sent KB/sec
            long totalBytesReceived = results.stream().mapToLong(PerformanceSamplerAdv.SampleResult::getBytesReceived).sum();
            long totalBytesSent = results.stream().mapToLong(PerformanceSamplerAdv.SampleResult::getBytesSent).sum();
            double receivedKBPerSec = totalTestTimeSec > 0 ? (totalBytesReceived / 1024.0) / totalTestTimeSec : 0;
            double sentKBPerSec = totalTestTimeSec > 0 ? (totalBytesSent / 1024.0) / totalTestTimeSec : 0;

            // Calculate Avg. Bytes
            double avgBytes = totalSamples > 0 ? totalBytesReceived / (double) totalSamples : 0;

            String[] row = {
                    label,
                    String.valueOf(totalSamples),
                    String.format("%.2f", average),
                    String.valueOf(min),
                    String.valueOf(max),
                    String.format("%.2f", stdDev),
                    String.format("%.2f%%", errorPercentage),
                    String.format("%.2f", throughput),
                    String.format("%.2f", receivedKBPerSec),
                    String.format("%.2f", sentKBPerSec),
                    String.format("%.2f", avgBytes)
            };
            rows.add(row);
        }

        // Write to CSV
        try (FileWriter writer = new FileWriter(filePath)) {
            for (String[] row : rows) {
                writer.append(String.join(",", row)).append("\n");
            }
        }
    }

    /**
     * Provides the summary table data for ExtentReports.
     *
     * @return A 2D array of strings representing the summary table.
     */
    public String[][] getSummaryTableData() {
        List<String[]> rows = new ArrayList<>();
        String[] header = {
                "Label",
                "# Samples",
                "Average (ms)",
                "Min (ms)",
                "Max (ms)",
                "Std. Dev.",
                "Error %",
                "Throughput (samples/sec)",
                "Received KB/sec",
                "Sent KB/sec",
                "Avg. Bytes"
        };
        rows.add(header);

        for (Map.Entry<String, List<PerformanceSamplerAdv.SampleResult>> entry : samples.entrySet()) {
            String label = entry.getKey();
            List<PerformanceSamplerAdv.SampleResult> results = entry.getValue();

            int totalSamples = results.size();
            int successSamples = (int) results.stream().filter(PerformanceSamplerAdv.SampleResult::isSuccess).count();
            int failedSamples = totalSamples - successSamples;
            double errorPercentage = ((double) failedSamples / totalSamples) * 100;

            List<Long> durations = results.stream()
                    .map(PerformanceSamplerAdv.SampleResult::getDuration)
                    .collect(Collectors.toList());

            double average = durations.stream().mapToLong(Long::longValue).average().orElse(0);
            long min = durations.stream().mapToLong(Long::longValue).min().orElse(0);
            long max = durations.stream().mapToLong(Long::longValue).max().orElse(0);
            double stdDev = calculateStandardDeviation(durations, average);

            // Calculate Throughput
            long firstSampleTime = results.stream().mapToLong(PerformanceSamplerAdv.SampleResult::getStartTime).min().orElse(0);
            long lastSampleTime = results.stream().mapToLong(PerformanceSamplerAdv.SampleResult::getEndTime).max().orElse(0);
            double totalTestTimeSec = (lastSampleTime - firstSampleTime) / 1000.0;
            double throughput = totalTestTimeSec > 0 ? totalSamples / totalTestTimeSec : 0;

            // Calculate Received and Sent KB/sec
            long totalBytesReceived = results.stream().mapToLong(PerformanceSamplerAdv.SampleResult::getBytesReceived).sum();
            long totalBytesSent = results.stream().mapToLong(PerformanceSamplerAdv.SampleResult::getBytesSent).sum();
            double receivedKBPerSec = totalTestTimeSec > 0 ? (totalBytesReceived / 1024.0) / totalTestTimeSec : 0;
            double sentKBPerSec = totalTestTimeSec > 0 ? (totalBytesSent / 1024.0) / totalTestTimeSec : 0;

            // Calculate Avg. Bytes
            double avgBytes = totalSamples > 0 ? totalBytesReceived / (double) totalSamples : 0;

            String[] row = {
                    label,
                    String.valueOf(totalSamples),
                    String.format("%.2f", average),
                    String.valueOf(min),
                    String.valueOf(max),
                    String.format("%.2f", stdDev),
                    String.format("%.2f%%", errorPercentage),
                    String.format("%.2f", throughput),
                    String.format("%.2f", receivedKBPerSec),
                    String.format("%.2f", sentKBPerSec),
                    String.format("%.2f", avgBytes)
            };
            rows.add(row);
        }

        return rows.toArray(new String[0][]);
    }

    /**
     * Generates the aggregate report CSV file.
     *
     * @param filePath The path to the output CSV file.
     * @throws IOException If an I/O error occurs.
     */
    public void generateAggregateReport(String filePath) throws IOException {
        List<String[]> rows = new ArrayList<>();
        String[] header = {
                "Label",
                "# Samples",
                "Average (ms)",
                "Min (ms)",
                "Max (ms)",
                "Std. Dev.",
                "Error %",
                "Throughput (samples/sec)",
                "90% Line (ms)",
                "95% Line (ms)",
                "99% Line (ms)",
                "Received KB/sec",
                "Sent KB/sec",
                "Avg. Bytes"
        };
        rows.add(header);

        for (Map.Entry<String, List<PerformanceSamplerAdv.SampleResult>> entry : samples.entrySet()) {
            String label = entry.getKey();
            List<PerformanceSamplerAdv.SampleResult> results = entry.getValue();

            int totalSamples = results.size();
            int successSamples = (int) results.stream().filter(PerformanceSamplerAdv.SampleResult::isSuccess).count();
            int failedSamples = totalSamples - successSamples;
            double errorPercentage = ((double) failedSamples / totalSamples) * 100;

            List<Long> durations = results.stream()
                    .map(PerformanceSamplerAdv.SampleResult::getDuration)
                    .sorted()
                    .collect(Collectors.toList());

            double average = durations.stream().mapToLong(Long::longValue).average().orElse(0);
            long min = durations.get(0);
            long max = durations.get(durations.size() - 1);
            double stdDev = calculateStandardDeviation(durations, average);

            // Calculate Percentiles
            long pct90 = calculatePercentile(durations, 90);
            long pct95 = calculatePercentile(durations, 95);
            long pct99 = calculatePercentile(durations, 99);

            // Calculate Throughput
            long firstSampleTime = results.stream().mapToLong(PerformanceSamplerAdv.SampleResult::getStartTime).min().orElse(0);
            long lastSampleTime = results.stream().mapToLong(PerformanceSamplerAdv.SampleResult::getEndTime).max().orElse(0);
            double totalTestTimeSec = (lastSampleTime - firstSampleTime) / 1000.0;
            double throughput = totalTestTimeSec > 0 ? totalSamples / totalTestTimeSec : 0;

            // Calculate Received and Sent KB/sec
            long totalBytesReceived = results.stream().mapToLong(PerformanceSamplerAdv.SampleResult::getBytesReceived).sum();
            long totalBytesSent = results.stream().mapToLong(PerformanceSamplerAdv.SampleResult::getBytesSent).sum();
            double receivedKBPerSec = totalTestTimeSec > 0 ? (totalBytesReceived / 1024.0) / totalTestTimeSec : 0;
            double sentKBPerSec = totalTestTimeSec > 0 ? (totalBytesSent / 1024.0) / totalTestTimeSec : 0;

            // Calculate Avg. Bytes
            double avgBytes = totalSamples > 0 ? totalBytesReceived / (double) totalSamples : 0;

            String[] row = {
                    label,
                    String.valueOf(totalSamples),
                    String.format("%.2f", average),
                    String.valueOf(min),
                    String.valueOf(max),
                    String.format("%.2f", stdDev),
                    String.format("%.2f%%", errorPercentage),
                    String.format("%.2f", throughput),
                    String.valueOf(pct90),
                    String.valueOf(pct95),
                    String.valueOf(pct99),
                    String.format("%.2f", receivedKBPerSec),
                    String.format("%.2f", sentKBPerSec),
                    String.format("%.2f", avgBytes)
            };
            rows.add(row);
        }

        // Write to CSV
        try (FileWriter writer = new FileWriter(filePath)) {
            for (String[] row : rows) {
                writer.append(String.join(",", row)).append("\n");
            }
        }
    }

    /**
     * Provides the aggregate table data for ExtentReports.
     *
     * @return A 2D array of strings representing the aggregate table.
     */
    public String[][] getAggregateTableData() {
        List<String[]> rows = new ArrayList<>();
        String[] header = {
                "Label",
                "# Samples",
                "Average (ms)",
                "Min (ms)",
                "Max (ms)",
                "Std. Dev.",
                "Error %",
                "Throughput (samples/sec)",
                "90% Line (ms)",
                "95% Line (ms)",
                "99% Line (ms)",
                "Received KB/sec",
                "Sent KB/sec",
                "Avg. Bytes"
        };
        rows.add(header);

        for (Map.Entry<String, List<PerformanceSamplerAdv.SampleResult>> entry : samples.entrySet()) {
            String label = entry.getKey();
            List<PerformanceSamplerAdv.SampleResult> results = entry.getValue();

            int totalSamples = results.size();
            int successSamples = (int) results.stream().filter(PerformanceSamplerAdv.SampleResult::isSuccess).count();
            int failedSamples = totalSamples - successSamples;
            double errorPercentage = ((double) failedSamples / totalSamples) * 100;

            List<Long> durations = results.stream()
                    .map(PerformanceSamplerAdv.SampleResult::getDuration)
                    .sorted()
                    .collect(Collectors.toList());

            double average = durations.stream().mapToLong(Long::longValue).average().orElse(0);
            long min = durations.get(0);
            long max = durations.get(durations.size() - 1);
            double stdDev = calculateStandardDeviation(durations, average);

            // Calculate Percentiles
            long pct90 = calculatePercentile(durations, 90);
            long pct95 = calculatePercentile(durations, 95);
            long pct99 = calculatePercentile(durations, 99);

            // Calculate Throughput
            long firstSampleTime = results.stream().mapToLong(PerformanceSamplerAdv.SampleResult::getStartTime).min().orElse(0);
            long lastSampleTime = results.stream().mapToLong(PerformanceSamplerAdv.SampleResult::getEndTime).max().orElse(0);
            double totalTestTimeSec = (lastSampleTime - firstSampleTime) / 1000.0;
            double throughput = totalTestTimeSec > 0 ? totalSamples / totalTestTimeSec : 0;

            // Calculate Received and Sent KB/sec
            long totalBytesReceived = results.stream().mapToLong(PerformanceSamplerAdv.SampleResult::getBytesReceived).sum();
            long totalBytesSent = results.stream().mapToLong(PerformanceSamplerAdv.SampleResult::getBytesSent).sum();
            double receivedKBPerSec = totalTestTimeSec > 0 ? (totalBytesReceived / 1024.0) / totalTestTimeSec : 0;
            double sentKBPerSec = totalTestTimeSec > 0 ? (totalBytesSent / 1024.0) / totalTestTimeSec : 0;

            // Calculate Avg. Bytes
            double avgBytes = totalSamples > 0 ? totalBytesReceived / (double) totalSamples : 0;

            String[] row = {
                    label,
                    String.valueOf(totalSamples),
                    String.format("%.2f", average),
                    String.valueOf(min),
                    String.valueOf(max),
                    String.format("%.2f", stdDev),
                    String.format("%.2f%%", errorPercentage),
                    String.format("%.2f", throughput),
                    String.valueOf(pct90),
                    String.valueOf(pct95),
                    String.valueOf(pct99),
                    String.format("%.2f", receivedKBPerSec),
                    String.format("%.2f", sentKBPerSec),
                    String.format("%.2f", avgBytes)
            };
            rows.add(row);
        }

        return rows.toArray(new String[0][]);
    }

    /**
     * Calculates the percentile value from a sorted list of durations.
     *
     * @param sortedDurations The sorted list of durations.
     * @param percentile      The percentile to calculate (e.g., 90 for 90% line).
     * @return The duration value at the specified percentile.
     */
    private long calculatePercentile(List<Long> sortedDurations, double percentile) {
        if (sortedDurations.isEmpty()) {
            return 0;
        }
        int index = (int) Math.ceil(percentile / 100.0 * sortedDurations.size()) - 1;
        index = Math.min(index, sortedDurations.size() - 1);
        return sortedDurations.get(index);
    }

    /**
     * Calculates the standard deviation of the durations.
     *
     * @param durations The list of durations.
     * @param mean      The mean of the durations.
     * @return The standard deviation.
     */
    private double calculateStandardDeviation(List<Long> durations, double mean) {
        double variance = durations.stream()
                .mapToDouble(d -> Math.pow(d - mean, 2))
                .average()
                .orElse(0);
        return Math.sqrt(variance);
    }

    /**
     * Generates the response times over time chart.
     *
     * @param filePath The path to the output PNG file.
     * @throws IOException If an I/O error occurs.
     */
    public void generateResponseTimesOverTimeChart(String filePath) throws IOException {
        XYSeriesCollection dataset = new XYSeriesCollection();

        for (Map.Entry<String, List<PerformanceSamplerAdv.SampleResult>> entry : samples.entrySet()) {
            String label = entry.getKey();
            XYSeries series = new XYSeries(label, true, true);

            for (PerformanceSamplerAdv.SampleResult result : entry.getValue()) {
                if (result.isSuccess()) {
                    long elapsedMillis = result.getStartTime() - getTestStartTime();
                    series.add(elapsedMillis, result.getDuration());
                }
            }

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

        // **Add this code to display data points**
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setDefaultShapesVisible(true);
        plot.setRenderer(renderer);

        ChartUtils.saveChartAsPNG(new File(filePath), chart, 800, 600);
    }
}
