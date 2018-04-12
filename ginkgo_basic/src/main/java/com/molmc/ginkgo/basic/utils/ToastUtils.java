package com.molmc.ginkgo.basic.utils;

import android.content.Context;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.molmc.ginkgo.basic.R;

/**
 * 单例Toast
 * Created by yalong on 2016/6/22.
 */
public class ToastUtils {
    private static String oldMsg;
    private static Toast toast = null;
    private static long oneTime = 0;
    private static long twoTime = 0;

    public static void showToast(Context context, @StringRes int resId) {
        showToast(context, context.getString(resId));
    }

    public static void showToast(Context context,  String msg) {
        showToast(context, msg, 0);
    }

    public static void showToast(Context context, String msg, int imgResId) {
        context = context.getApplicationContext();
        // 获取LayoutInflater对象
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // 由layout文件创建一个View对象
        View layout = inflater.inflate(R.layout.basic_toast_hud, null);

        // 实例化ImageView和TextView对象
        ImageView imageView = (ImageView) layout.findViewById(R.id.ImageView);
        TextView textView = (TextView) layout.findViewById(R.id.message);
        textView.setText(msg);
        //这里我为了给大家展示就使用这个方面既能显示无图也能显示带图的toast
        if (imgResId == 0) {
            imageView.setVisibility(View.GONE);
        } else {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(imgResId);
        }

        if (toast == null) {
            toast = new Toast(context);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            oneTime = System.currentTimeMillis();
        } else {
            twoTime = System.currentTimeMillis();
            if (msg.equals(oldMsg)) {
                if (twoTime - oneTime > Toast.LENGTH_SHORT) {
                    toast.show();
                }
            } else {
                oldMsg = msg;
                toast.setView(layout);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
        oneTime = twoTime;
    }

    public static void showSuccess(Context context, @StringRes int resId){
        showToast(context, context.getString(resId), R.mipmap.basic_toast_success);
    }

    public static void showSuccess(Context context, String msg) {
        showToast(context, msg, R.mipmap.basic_toast_success);
    }

    public static void showFailed(Context context, @StringRes int resId){
        showToast(context, context.getString(resId), R.mipmap.basic_toast_fail);
    }

    public static void showFailed(Context context, String msg) {
        showToast(context, msg, R.mipmap.basic_toast_fail);
    }
}
