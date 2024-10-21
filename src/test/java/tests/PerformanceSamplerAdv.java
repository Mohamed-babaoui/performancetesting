package tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PerformanceSamplerAdv {

    private Map<String, List<SampleResult>> samples = new ConcurrentHashMap<>();

    private ThreadLocal<Sample> currentSample = ThreadLocal.withInitial(() -> null);

    /**
     * Starts a new sample with the given name.
     *
     * @param sampleName The name of the sample.
     */
    public void sampleStart(String sampleName) {
        Sample sample = new Sample(sampleName, System.currentTimeMillis());
        currentSample.set(sample);
    }

    /**
     * Ends the current sample and records the result.
     *
     * @param sampleName     The name of the sample.
     * @param success        Whether the sample was successful.
     * @param bytesSent      The number of bytes sent during the sample.
     * @param bytesReceived  The number of bytes received during the sample.
     */
    public void sampleEnd(String sampleName, boolean success, long bytesSent, long bytesReceived) {
        Sample sample = currentSample.get();
        if (sample != null && sample.getSampleName().equals(sampleName)) {
            sample.setEndTime(System.currentTimeMillis());
            sample.setSuccess(success);

            SampleResult result = new SampleResult(
                    sample.getStartTime(),
                    sample.getEndTime(),
                    sample.getEndTime() - sample.getStartTime(),
                    success
            );

            result.setBytesSent(bytesSent);
            result.setBytesReceived(bytesReceived);

            samples.computeIfAbsent(sampleName, k -> new ArrayList<>()).add(result);
            currentSample.remove();
        }
    }

    /**
     * Retrieves all recorded samples.
     *
     * @return A map of sample names to lists of sample results.
     */
    public Map<String, List<SampleResult>> getSamples() {
        return samples;
    }

    /**
     * Inner class representing a sample in progress.
     */
    private static class Sample {
        private String sampleName;
        private long startTime;
        private long endTime;
        private boolean success;

        public Sample(String sampleName, long startTime) {
            this.sampleName = sampleName;
            this.startTime = startTime;
        }

        public String getSampleName() {
            return sampleName;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public long getEndTime() {
            return endTime;
        }

        public boolean isSuccess() {
            return success;
        }
    }

    /**
     * Public static class representing the result of a sample.
     */
    public static class SampleResult {
        private long startTime;
        private long endTime;
        private long duration;
        private boolean success;
        private long bytesReceived; // In bytes
        private long bytesSent;     // In bytes

        public SampleResult(long startTime, long endTime, long duration, boolean success) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.duration = duration;
            this.success = success;
            this.bytesReceived = 0; // Default value
            this.bytesSent = 0;     // Default value
        }

        // Getters and setters
        public long getStartTime() {
            return startTime;
        }

        public long getEndTime() {
            return endTime;
        }

        public long getDuration() {
            return duration;
        }

        public boolean isSuccess() {
            return success;
        }

        public long getBytesReceived() {
            return bytesReceived;
        }

        public void setBytesReceived(long bytesReceived) {
            this.bytesReceived = bytesReceived;
        }

        public long getBytesSent() {
            return bytesSent;
        }

        public void setBytesSent(long bytesSent) {
            this.bytesSent = bytesSent;
        }
    }
}
