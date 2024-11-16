package xyz.tsingloong.courseschedulerapp.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import xyz.tsingloong.courseschedulerapp.models.LogEntry;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LogManager {
    private static final String LOG_FILE_NAME = "logs.json";
    private final File logFile;

    public LogManager(Context context) {
        logFile = new File(context.getFilesDir(), LOG_FILE_NAME);
    }

    public List<LogEntry> getLogs() {
        if (!logFile.exists()) {
            return new ArrayList<>();
        }

        try (FileReader reader = new FileReader(logFile)) {
            Type listType = new TypeToken<List<LogEntry>>() {}.getType();
            List<LogEntry> logs = new Gson().fromJson(reader, listType);
            return logs != null ? logs : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void addLog(LogEntry logEntry) {
        List<LogEntry> logs = getLogs();
        logs.add(logEntry);

        try (FileWriter writer = new FileWriter(logFile)) {
            new Gson().toJson(logs, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<LogEntry> searchLogs(String keyword) {
        List<LogEntry> allLogs = getLogs();
        List<LogEntry> filteredLogs = new ArrayList<>();

        for (LogEntry log : allLogs) {
            if (log.getInputData().toString().contains(keyword) || log.getResultText().contains(keyword)) {
                filteredLogs.add(log);
            }
        }
        return filteredLogs;
    }

    public boolean exportLogs(File file) {
        List<LogEntry> logs = getLogs();

        // 如果日志为空，则返回 false
        if (logs.isEmpty()) {
            return false;
        }

        try (FileWriter writer = new FileWriter(file)) {
            new Gson().toJson(logs, writer);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public void clearLogs() {
        if (logFile.exists()) {
            boolean deleted = logFile.delete(); // 删除文件
            if (!deleted) {
                System.err.println("Failed to delete the log file.");
            }
        }
    }


}
