package xyz.tsingloong.courseschedulerapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import xyz.tsingloong.courseschedulerapp.adapters.LogAdapter;
import xyz.tsingloong.courseschedulerapp.models.LogEntry;
import xyz.tsingloong.courseschedulerapp.utils.LogManager;
import xyz.tsingloong.courseschedulerapp.utils.ToastUtils;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class LogsActivity extends AppCompatActivity {

    private static final int EXPORT_LOG_REQUEST_CODE = 1;

    private RecyclerView recyclerView;
    private LogAdapter logAdapter;
    private EditText searchInput;
    private FloatingActionButton btnExport, btnSearch, btnClearLogs;
    private LogManager logManager;
    private List<LogEntry> originalLogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);

        recyclerView = findViewById(R.id.recyclerViewLogs);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchInput = findViewById(R.id.searchInput);
        btnExport = findViewById(R.id.btnExportLogs);
        btnSearch = findViewById(R.id.btnSearchLogs);
        btnClearLogs = findViewById(R.id.btnClearLogs);

        logManager = new LogManager(this);
        originalLogs = logManager.getLogs();

        logAdapter = new LogAdapter(originalLogs);
        recyclerView.setAdapter(logAdapter);

        setupListeners();
    }

    private void setupListeners() {
        // 搜索功能
        btnSearch.setOnClickListener(v -> {
            String keyword = searchInput.getText().toString().trim();
            if (keyword.isEmpty()) {
                ToastUtils.showCustomToast(this, "请输入搜索关键词", R.drawable.ic_toast_icon);
                return;
            }

            List<LogEntry> filteredLogs = logManager.searchLogs(keyword);
            if (filteredLogs.isEmpty()) {
                ToastUtils.showCustomToast(this, "No william", R.drawable.ic_toast_icon);

            }

            logAdapter.updateLogs(filteredLogs);
        });

        // 导出功能
        btnExport.setOnClickListener(v -> {
            List<LogEntry> logs = logManager.getLogs();
            if (logs.isEmpty()) {
                ToastUtils.showCustomToast(this, "日志为空，无法导出 Personally, I don't care", R.drawable.ic_toast_icon);
                return;
            }

            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.setType("application/json");
            intent.putExtra(Intent.EXTRA_TITLE, "exported_logs.json");
            startActivityForResult(intent, EXPORT_LOG_REQUEST_CODE);
        });


        // 清空日志
        btnClearLogs.setOnClickListener(v -> {
            logManager.clearLogs();
            originalLogs.clear();
            logAdapter.updateLogs(originalLogs);
            ToastUtils.showCustomToast(this, "删库跑路", R.drawable.ic_toast_icon);

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EXPORT_LOG_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    exportLogsToFile(uri);
                } else {
                    ToastUtils.showCustomToast(this, "未选择文件路径", R.drawable.ic_toast_icon);

                }
            } else {
                ToastUtils.showCustomToast(this, "导出取消", R.drawable.ic_toast_icon);

            }
        }
    }

    private void exportLogsToFile(Uri uri) {
        try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
            if (outputStream != null) {
                List<LogEntry> logs = logManager.getLogs();
                String jsonLogs = new com.google.gson.Gson().toJson(logs);
                outputStream.write(jsonLogs.getBytes());
                Toast.makeText(this, "日志已成功导出", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "无法写入文件", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "导出日志时出错", Toast.LENGTH_SHORT).show();
        }
    }
}
