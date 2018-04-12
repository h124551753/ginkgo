package com.molmc.dispatch.activity.splash;

import android.os.Message;

import com.billy.cc.core.component.CC;
import com.molmc.dispatch.activity.main.MainActivity;
import com.molmc.ginkgo.basic.base.BaseActivity;
import com.molmc.ginkgo.basic.utils.LogUtils;

public class SplashActivity extends BaseActivity {
    private static final String TAG = "SplashActivity";

    @Override
    protected int setContentView() {
        return 0;
    }

    @Override
    protected void start() {
        logScreenInfo();
        mHandler.sendEmptyMessageDelayed(0, 1000);
    }

    @Override
    public void handlerMessage(Message msg) {
        super.handlerMessage(msg);
        boolean hasComponentUser = CC.hasComponent("ComponentUser1");
        if (hasComponentUser) {
            CC.obtainBuilder("ComponentUser")
                    .setContext(this)
                    .setActionName("toLoginActivity")
                    .build()
                    .call();
        } else {
            // 如果没有用户组件直接进入主界面
            startActivity(MainActivity.class, true);
        }
    }

    private void logScreenInfo() {
        int heightPixels = getResources().getDisplayMetrics().heightPixels;
        int widthPixels = getResources().getDisplayMetrics().widthPixels;
        float density = getResources().getDisplayMetrics().density;
        int densityDpi = getResources().getDisplayMetrics().densityDpi;
        LogUtils.i(TAG, "heightPixels: " + heightPixels + ",widthPixels：" + widthPixels + ",density：" + density + ",densityDpi：" + densityDpi);
    }
}
