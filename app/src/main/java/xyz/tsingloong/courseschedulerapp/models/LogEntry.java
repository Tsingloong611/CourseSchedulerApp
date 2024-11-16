package xyz.tsingloong.courseschedulerapp.models;

import java.util.List;

public class LogEntry {
    private String timestamp; // 时间戳
    private List<String> inputData; // 输入数据
    private String resultText; // 优化结果

    public LogEntry(String timestamp, List<String> inputData, String resultText) {
        this.timestamp = timestamp;
        this.inputData = inputData;
        this.resultText = resultText;
    }

    // Getters and Setters
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getInputData() {
        return inputData;
    }

    public void setInputData(List<String> inputData) {
        this.inputData = inputData;
    }

    public String getResultText() {
        return resultText;
    }

    public void setResultText(String resultText) {
        this.resultText = resultText;
    }

    // Override toString for better logging/debugging
    @Override
    public String toString() {
        StringBuilder logString = new StringBuilder();
        logString.append("Timestamp: ").append(timestamp).append("\n");
        logString.append("Input Data:\n");
        if (inputData != null) {
            for (String input : inputData) {
                logString.append("- ").append(input).append("\n");
            }
        } else {
            logString.append("None\n");
        }
        logString.append("Result: ").append(resultText).append("\n");
        return logString.toString();
    }
}
