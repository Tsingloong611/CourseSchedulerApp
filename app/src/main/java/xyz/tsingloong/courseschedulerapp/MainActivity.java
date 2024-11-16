package xyz.tsingloong.courseschedulerapp;
import xyz.tsingloong.courseschedulerapp.BuildConfig;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import xyz.tsingloong.courseschedulerapp.models.Course;
import xyz.tsingloong.courseschedulerapp.models.CourseOption;
import xyz.tsingloong.courseschedulerapp.models.LogEntry;
import xyz.tsingloong.courseschedulerapp.utils.LogManager;
import xyz.tsingloong.courseschedulerapp.utils.Scheduler;
import xyz.tsingloong.courseschedulerapp.utils.ToastUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String UPDATE_URL = "https://<your_username>.github.io/AppUpdateInfo/version.json";

    private LinearLayout courseInputContainer;
    private Button btnAddCourse, btnSubmit, btnViewLogs, btnAboutApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        courseInputContainer = findViewById(R.id.courseInputContainer);
        btnAddCourse = findViewById(R.id.btnAddCourse);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnViewLogs = findViewById(R.id.btnViewLogs);

        btnAboutApp = findViewById(R.id.btnAboutApp);
        btnAboutApp.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        });


        // 添加初始数据
        addInitialData();

        Button btnCheckUpdates = findViewById(R.id.btnCheckUpdates);
        btnCheckUpdates.setOnClickListener(v -> checkForUpdates());


        // 动态添加课程输入行
        btnAddCourse.setOnClickListener(v -> addCourseInputRow(courseInputContainer));

        // 查看日志按钮逻辑
        btnViewLogs.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LogsActivity.class);
            startActivity(intent);
        });

        // 提交按钮逻辑
        btnSubmit.setOnClickListener(v -> {
            if (courseInputContainer.getChildCount() == 0) {
                // 当没有任何课程时，提示用户添加课程
                ToastUtils.showCustomToast(this, "You have not joined Class C3!", R.drawable.ic_toast_icon);
                return;
            }
            boolean hasError = false;

            for (int i = 0; i < courseInputContainer.getChildCount(); i++) {
                View row = courseInputContainer.getChildAt(i);
                EditText etCourseName = row.findViewById(R.id.etCourseName);
                EditText etSection = row.findViewById(R.id.etSection);
                EditText etTimeSlots = row.findViewById(R.id.etTimeSlots);

                if (etCourseName.getText().toString().trim().isEmpty()) {
                    etCourseName.setError("课程名称不能为空");
                    hasError = true;
                }
                if (etSection.getText().toString().trim().isEmpty()) {
                    etSection.setError("班级不能为空");
                    hasError = true;
                }
                if (etTimeSlots.getText().toString().trim().isEmpty()) {
                    etTimeSlots.setError("时间段不能为空");
                    hasError = true;
                }
            }

            if (hasError) {
                ToastUtils.showCustomToast(this, "请检测输入错误并重试！", R.drawable.ic_toast_icon);

                return;
            }

            try {
                List<Course> courses = parseDynamicInput(courseInputContainer);
                if (courses.isEmpty()) return;

                // 调用 Scheduler 排课
                Scheduler scheduler = new Scheduler();
                List<CourseOption> bestSchedule = scheduler.selectOptimalCourses(courses);

                // 生成结果文本
                String resultText = generateResultText(bestSchedule, scheduler.getMinEarlyClasses());

                // 记录日志
                recordLog(courses, resultText);

                // 跳转到结果页面
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                intent.putExtra("result", resultText);
                startActivity(intent);
            } catch (Exception e) {
                List<String> errorData = new ArrayList<>();
                errorData.add("Error occurred during operation");

                LogManager logManager = new LogManager(this); // 传入当前 Activity 的上下文


                logManager.addLog(new LogEntry(
                        java.text.DateFormat.getDateTimeInstance().format(new java.util.Date()),
                        errorData,
                        "Error: " + e.getMessage()
                ));
            }
        });
    }

    /**
     * 添加初始数据
     */
    private void addInitialData() {
        addCourseInputRow(courseInputContainer);
        View firstRow = courseInputContainer.getChildAt(0);
        ((EditText) firstRow.findViewById(R.id.etCourseName)).setText("数学");
        ((EditText) firstRow.findViewById(R.id.etSection)).setText("101");
        ((EditText) firstRow.findViewById(R.id.etTimeSlots)).setText("一1-2&三3-4");
    }

    /**
     * 动态添加课程输入行
     */
    private void addCourseInputRow(LinearLayout container) {
        View row = LayoutInflater.from(this).inflate(R.layout.course_input_row, container, false);

        // 更新行号
        TextView tvRowNumber = row.findViewById(R.id.tvRowNumber);
        tvRowNumber.setText(String.valueOf(container.getChildCount() + 1) + ".");

        // 获取输入框
        EditText etCourseName = row.findViewById(R.id.etCourseName);
        EditText etSection = row.findViewById(R.id.etSection);
        EditText etTimeSlots = row.findViewById(R.id.etTimeSlots);

        // 添加实时校验
        addInputValidation(etCourseName, "课程名称不能为空");
        addInputValidation(etSection, "班级不能为空");
        addInputValidation(etTimeSlots, "时间段不能为空");

        // 删除按钮逻辑
        row.findViewById(R.id.btnDeleteRow).setOnClickListener(v -> {
            container.removeView(row);
            updateRowNumbers(container); // 删除后更新行号
        });

        container.addView(row);
    }

    /**
     * 添加实时输入验证
     */
    private void addInputValidation(EditText editText, String errorMessage) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty()) {
                    editText.setError(errorMessage);
                } else if (editText.getId() == R.id.etTimeSlots &&
                        !s.toString().matches("[一二三四五六日]\\d+-\\d+(&[一二三四五六日]\\d+-\\d+)*")) {
                    editText.setError("时间段格式不正确");
                } else {
                    editText.setError(null);
                }
            }
        });
    }





    /**
     * 删除行后更新行号
     */
    private void updateRowNumbers(LinearLayout container) {
        for (int i = 0; i < container.getChildCount(); i++) {
            View row = container.getChildAt(i);
            TextView tvRowNumber = row.findViewById(R.id.tvRowNumber);
            tvRowNumber.setText(String.valueOf(i + 1) + ".");
        }
    }

    /**
     * 动态解析输入数据
     */
    private List<Course> parseDynamicInput(LinearLayout container) {
        List<Course> courses = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();
        boolean hasError = false;

        for (int i = 0; i < container.getChildCount(); i++) {
            View row = container.getChildAt(i);
            EditText etCourseName = row.findViewById(R.id.etCourseName);
            EditText etSection = row.findViewById(R.id.etSection);
            EditText etTimeSlots = row.findViewById(R.id.etTimeSlots);

            String courseName = etCourseName.getText().toString().trim();
            String section = etSection.getText().toString().trim();
            String timeSlots = etTimeSlots.getText().toString().trim();

            if (courseName.isEmpty() || section.isEmpty() || timeSlots.isEmpty()) {
                errorMessages.add("第 " + (i + 1) + " 行: 请填写所有字段");
                hasError = true;
                continue;
            }

            Course course = findCourseByName(courses, courseName);
            if (course == null) {
                course = new Course(courseName);
                courses.add(course);
            }

            List<String> timeSlotList = Arrays.asList(timeSlots.split("&"));
            course.options.add(new CourseOption(courseName, section, timeSlotList));
        }

        if (hasError) {
            StringBuilder errorString = new StringBuilder();
            for (String error : errorMessages) {
                errorString.append(error).append("\n");
            }
            ToastUtils.showCustomToast(this, errorString.toString(), R.drawable.ic_toast_icon);

            return new ArrayList<>();
        }

        return courses;
    }

    /**
     * 查找是否已有同名课程
     */
    private Course findCourseByName(List<Course> courses, String name) {
        for (Course course : courses) {
            if (course.name.equals(name)) {
                return course;
            }
        }
        return null;
    }

    /**
     * 生成结果文本
     */
    /**
     * 生成优化后的排课结果文本
     */
    private String generateResultText(List<CourseOption> bestSchedule, int minEarlyClasses) {
        StringBuilder result = new StringBuilder();

        if (bestSchedule == null || bestSchedule.isEmpty()) {
            result.append("优化失败！未能找到合适的课程安排方案。");
        } else {
            result.append("优化后的课程安排：\n\n");

            for (CourseOption option : bestSchedule) {
                result.append("课程名称: ").append(option.courseName).append("\n")
                        .append("班级: ").append(option.section).append("\n")
                        .append("时间段: ").append(String.join(", ", option.timeSlots)).append("\n\n");
            }

            result.append("最少的早八课程数: ").append(minEarlyClasses);
        }

        return result.toString();
    }


    /**
     * 记录日志
     */
    private void recordLog(List<Course> courses, String resultText) {
        List<String> inputData = new ArrayList<>();
        for (Course course : courses) {
            for (CourseOption option : course.options) {
                inputData.add("课程名称: " + course.name +
                        ", 班级: " + option.section +
                        ", 时间段: " + option.timeSlots);
            }
        }

        LogManager logManager = new LogManager(this);
        logManager.addLog(new LogEntry(
                java.text.DateFormat.getDateTimeInstance().format(new java.util.Date()),
                inputData,  // 记录完整输入
                resultText  // 记录优化结果
        ));
    }
    private void checkForUpdates() {
        new Thread(() -> {
            try {
                // 获取 version.json
                URL url = new URL(UPDATE_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder jsonBuilder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    jsonBuilder.append(line);
                }

                reader.close();
                inputStream.close();

                // 解析 JSON
                JSONObject jsonResponse = new JSONObject(jsonBuilder.toString());
                String latestVersion = jsonResponse.getString("latest_version");
                String downloadUrl = jsonResponse.getString("download_url");
                String releaseNotes = jsonResponse.getString("release_notes");

                // 比较版本号
                runOnUiThread(() -> {
                    if (!BuildConfig.VERSION_NAME.equals(latestVersion)) {
                        // 显示更新对话框
                        showUpdateDialog(latestVersion, downloadUrl, releaseNotes);
                    } else {
                        Toast.makeText(this, "已是最新版本", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "检查更新失败", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void showUpdateDialog(String version, String downloadUrl, String releaseNotes) {
        new AlertDialog.Builder(this)
                .setTitle("新版本：" + version)
                .setMessage("更新内容：\n" + releaseNotes)
                .setPositiveButton("立即更新", (dialog, which) -> {
                    // 打开浏览器下载 APK
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl));
                    startActivity(browserIntent);
                })
                .setNegativeButton("稍后再说", null)
                .show();
    }

}
