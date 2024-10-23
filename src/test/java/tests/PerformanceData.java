package tests;

public class PerformanceData {
    private String actionName;
    private long responseTime; // in milliseconds
    private boolean success;
    private String errorMessage;
    private long timestamp;

    public PerformanceData(String actionName, long responseTime, boolean success, String errorMessage, long timestamp) {
        this.actionName = actionName;
        this.responseTime = responseTime;
        this.success = success;
        this.errorMessage = errorMessage;
        this.timestamp = timestamp;
    }

    // Getters
    public String getActionName() {
        return actionName;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
