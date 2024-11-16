package xyz.tsingloong.courseschedulerapp.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import xyz.tsingloong.courseschedulerapp.R;

public class ToastUtils {

    public static void showCustomToast(Context context, String message, int iconResId) {
        // 加载自定义布局
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.custom_toast, null);

        // 设置图标
        ImageView icon = layout.findViewById(R.id.toast_icon);
        icon.setImageResource(iconResId);

        // 设置消息
        TextView text = layout.findViewById(R.id.toast_message);
        text.setText(message);

        // 创建 Toast
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);

        // 设置 Toast 的位置（Gravity.BOTTOM，向上偏移 150 像素）
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 150);
        toast.show();
    }
}
