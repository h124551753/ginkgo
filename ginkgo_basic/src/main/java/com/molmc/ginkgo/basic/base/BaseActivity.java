package com.molmc.ginkgo.basic.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.molmc.ginkgo.basic.data.CacheDataSource;
import com.molmc.ginkgo.basic.utils.HandlerUtils;
import com.molmc.ginkgo.basic.utils.LogUtils;
import com.molmc.ginkgo.basic.utils.ToastUtils;

import java.util.List;

/**
 * Created by admin on 2016/11/18.
 * 基类-Activity
 */

public abstract class BaseActivity extends AppCompatActivity implements HandlerUtils.OnReceiveMessageListener, View.OnClickListener {
    /**
     * 默认的REQUEST_CODE
     */
    protected static final int DEFAULT_REQUEST_CODE = 0;

    protected Context mContext;
    protected HandlerUtils.HandlerHolder mHandler;

    @Override
    public void handlerMessage(Message msg) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            CacheDataSource.restoreData(savedInstanceState);
        }
        mContext = this;
        mHandler = new HandlerUtils.HandlerHolder(this);
        if (setContentView() != 0) {
            setContentView(setContentView());
        }
        start();
    }

    /**
     * 设置显示布局
     *
     * @return layout资源ID
     */
    protected abstract int setContentView();

    /**
     * 入口
     */
    protected abstract void start();

    protected void showToast(@StringRes int resId) {
        ToastUtils.showToast(mContext, resId);
    }

    protected void showToast(String msg) {
        ToastUtils.showToast(mContext, msg);
    }

    protected void showSuccess(@StringRes int resId) {
        ToastUtils.showSuccess(mContext, resId);
    }
    protected void showSuccess(String msg) {
        ToastUtils.showSuccess(mContext, msg);
    }

    protected void showFailed(@StringRes int resId) {
        ToastUtils.showFailed(mContext, resId);
    }
    protected void showFailed(String msg) {
        ToastUtils.showFailed(mContext, msg);
    }

    /**
     * 显示LOG，TAG为当前类名
     */
    protected void logI(String msg) {
        LogUtils.i(getClass().getSimpleName(), msg);
    }

    /**
     * 显示LOG，TAG为当前类名
     */
    protected void logE(String msg) {
        LogUtils.e(getClass().getSimpleName(), msg);
    }

    /**
     * 开启activity，默认不关闭当前activity
     *
     * @param cls 要打开的activity
     */
    public void startActivity(Class<? extends Activity> cls) {
        startActivity(cls, false);
    }

    /**
     * 开启activity
     *
     * @param cls                  要打开的activity
     * @param closeCurrentActivity 是否需要关闭当前页面
     */
    protected void startActivity(Class<? extends Activity> cls, boolean closeCurrentActivity) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
        if (closeCurrentActivity) {
            finish();
        }
    }

    /**
     * 不需要requestCode时调用，会传入默认的code
     *
     * @param intent 要跳转的Activity
     */
    public void startActivityForResult(Intent intent) {
        startActivityForResult(intent, DEFAULT_REQUEST_CODE);
    }

    /**
     * 不需要requestCode时调用，会传入默认的code
     *
     * @param cls 要跳转的Activity
     */
    public void startActivityForResult(Class<? extends Activity> cls) {
        startActivityForResult(cls, DEFAULT_REQUEST_CODE);
    }

    protected void startActivityForResult(Class<? extends Activity> cls, int requestCode) {
        Intent intent = new Intent(this, cls);
        startActivityForResult(intent, requestCode);
    }

    protected void showView(ViewGroup container, View v) {
        v.setVisibility(View.VISIBLE);
        int childCount = container.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = container.getChildAt(i);
            if (childAt.getId() != v.getId()) {
                childAt.setVisibility(View.GONE);
            }
        }
    }

    protected String getText(TextView textView) {
        return textView.getText().toString().trim();
    }

    /**
     * 判断两个字符串是否相等
     */
    protected boolean equals(CharSequence a, CharSequence b) {
        return TextUtils.equals(a, b);
    }

    protected boolean isEmpty(CharSequence str) {
        return TextUtils.isEmpty(str);
    }

    protected int getMyColor(@ColorRes int colorResId) {
        return getResources().getColor(colorResId);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermissionsSafely(String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean hasPermission(String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 实现fragment拦截该事件，fragment不做处理再交给activity
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            List<Fragment> childFragments = fragment.getChildFragmentManager().getFragments();
            for (Fragment childFragment : childFragments) {
                if (childFragment.isVisible() &&
                        childFragment.getUserVisibleHint() &&
                        childFragment instanceof BaseFragment &&
                        ((BaseFragment) childFragment).onKeyDown(keyCode)) {
                    return true;
                }
            }
            if (fragment.isVisible() &&
                    fragment.getUserVisibleHint() &&
                    fragment instanceof BaseFragment &&
                    ((BaseFragment) fragment).onKeyDown(keyCode)) {
                return true;
            }
        }
        // 按返回键不退出程序,当且仅当当前activity为根activity才会生效
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            moveTaskToBack(false);
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void addOnClickListeners(@IdRes int... ids) {
        if (ids != null) {
            for (@IdRes int id : ids) {
                findViewById(id).setOnClickListener(this);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        CacheDataSource.saveData(outState);
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
