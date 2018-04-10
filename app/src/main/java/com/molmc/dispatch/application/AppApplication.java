package com.molmc.dispatch.application;

import android.app.Application;

import com.billy.cc.core.component.CC;
import com.molmc.dispatch.BuildConfig;
import com.molmc.dispatch.R;
import com.molmc.ginkgo.basic.data.NetDataSource;
import com.molmc.ginkgo.basic.utils.CrashUtils;
import com.molmc.ginkgo.basic.utils.DirAndFileUtils;
import com.molmc.ginkgo.basic.utils.LogUtils;
import com.squareup.leakcanary.LeakCanary;

import java.io.IOException;

/**
 * Created by hhe on 2018/4/10
 */

public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        // 初始化内存监测
        LeakCanary.install(this);

        // 全局捕获异常并保存异常信息
        CrashUtils.getInstance().init(this);

        // 初始化网络库
        NetDataSource.init(this, BuildConfig.DEBUG, getString(R.string.app_name));

        // 初始化LOG
        initLog();

        // 初始化模块化单元CC
        initCC();

    }

    private void initLog() {
        String logDir;
        try {
            logDir = DirAndFileUtils.getLogDir().toString();
        } catch (IOException e) {
            logDir = getCacheDir().toString();
        }
        String logTag = getString(R.string.app_name);

        LogUtils.init(BuildConfig.DEBUG, false, logDir, LogUtils.FILTER_V, logTag);
    }

    private void initCC() {
        CC.enableVerboseLog(BuildConfig.DEBUG);
        CC.enableDebug(BuildConfig.DEBUG);
        CC.enableRemoteCC(BuildConfig.DEBUG);
    }
}
