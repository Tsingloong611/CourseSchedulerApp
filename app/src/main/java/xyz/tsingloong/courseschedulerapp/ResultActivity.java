package xyz.tsingloong.courseschedulerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    private TextView tvResult;
    private Button btnBackToHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // 绑定视图
        tvResult = findViewById(R.id.tvResult);
        btnBackToHome = findViewById(R.id.btnBackToHome);

        // 获取传递的结果并显示
        String result = getIntent().getStringExtra("result");
        System.out.println("接收到的结果" + result);
        if (result != null) {
            tvResult.setText(result);
        } else {
            tvResult.setText("未找到结果！");
        }

        // 返回首页按钮点击事件
        btnBackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // 关闭当前活动，避免用户返回结果页
            }
        });
    }
}
