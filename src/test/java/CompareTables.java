import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class CompareTables {

    public static void main(String[] args) {
        String previousReportPath = "previousReport/summary.txt";
        String currentReportPath = "currentReport/summary.txt";
        String resultFolderPath = "result";
        String outputPath = resultFolderPath + "/comparison_report.csv";

        // Ensure the result folder exists
        createResultFolder(resultFolderPath);

        Map<String, PerformanceData> previousData = loadReport(previousReportPath);
        Map<String, PerformanceData> currentData = loadReport(currentReportPath);

        if (previousData != null && currentData != null) {
            try {
                writeComparisonToCSV(previousData, currentData, outputPath);
                System.out.println("Comparison report generated: " + outputPath);
            } catch (IOException e) {
                System.out.println("Error writing CSV file: " + e.getMessage());
            }
        }
    }

    private static void createResultFolder(String folderPath) {
        Path path = Paths.get(folderPath);
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
                System.out.println("Created directory: " + folderPath);
            } catch (IOException e) {
                System.out.println("Error creating result folder: " + e.getMessage());
            }
        }
    }

    private static Map<String, PerformanceData> loadReport(String filePath) {
        Map<String, PerformanceData> reportData = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] columns = line.trim().split("\\s+");
                if (columns.length >= 8) {
                    String requestName = columns[0];
                    int count = Integer.parseInt(columns[1]);
                    double average = Double.parseDouble(columns[2].replace(",", "."));
                    double min = Double.parseDouble(columns[3].replace(",", "."));
                    double max = Double.parseDouble(columns[4].replace(",", "."));
                    double stdev = Double.parseDouble(columns[5].replace(",", "."));
                    double error = Double.parseDouble(columns[6].replace(",", "."));
                    double throughput = Double.parseDouble(columns[7].replace(",", "."));

                    reportData.put(requestName, new PerformanceData(count, average, min, max, stdev, error, throughput));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading report file: " + e.getMessage());
            return null;
        }
        return reportData;
    }

    private static void writeComparisonToCSV(Map<String, PerformanceData> previousData, Map<String, PerformanceData> currentData, String outputPath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new File(outputPath))) {
            // Write the header row with samplesDifference as the first column
            writer.println("samplesDifference,Request Name,Count (%),Average (ms) (%),Min (ms) (%),Max (ms) (%),Stdev (ms) (%),Error (%),Throughput (rq/s) (%)");

            // Write comparison data
            for (String requestName : currentData.keySet()) {
                if (previousData.containsKey(requestName)) {
                    PerformanceData previous = previousData.get(requestName);
                    PerformanceData current = currentData.get(requestName);

                    int samplesDifference = current.count - previous.count;
                    String samplesDifferenceStr = (samplesDifference >= 0 ? "+" : "") + samplesDifference;

                    writer.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                            samplesDifferenceStr,
                            requestName,
                            calculatePercentageChange(previous.count, current.count),
                            calculatePercentageChange(previous.average, current.average),
                            calculatePercentageChange(previous.min, current.min),
                            calculatePercentageChange(previous.max, current.max),
                            calculatePercentageChange(previous.stdev, current.stdev),
                            calculatePercentageChange(previous.error, current.error),
                            calculatePercentageChange(previous.throughput, current.throughput)
                    );
                }
            }
        }
    }

    private static String calculatePercentageChange(double previous, double current) {
        if (previous == 0) {
            return "N/A"; // Avoid division by zero
        }
        double change = ((current - previous) / previous) * 100;
        String sign = change >= 0 ? "+" : "";
        return sign + String.format("%.2f", change) + "%";
    }

    static class PerformanceData {
        int count;
        double average;
        double min;
        double max;
        double stdev;
        double error;
        double throughput;

        public PerformanceData(int count, double average, double min, double max, double stdev, double error, double throughput) {
            this.count = count;
            this.average = average;
            this.min = min;
            this.max = max;
            this.stdev = stdev;
            this.error = error;
            this.throughput = throughput;
        }
    }
}
