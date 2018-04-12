package com.molmc.ginkgo.user;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import com.billy.cc.core.component.CC;
import com.billy.cc.core.component.CCResult;
import com.billy.cc.core.component.IComponent;
import com.lzy.okgo.model.HttpParams;
import com.molmc.ginkgo.basic.constants.Urls;
import com.molmc.ginkgo.basic.data.CacheDataSource;
import com.molmc.ginkgo.basic.data.DBDataSource;
import com.molmc.ginkgo.basic.data.NetDataSource;
import com.molmc.ginkgo.basic.listener.StateListener;
import com.molmc.ginkgo.basic.mqtt.MQTTClient;
import com.molmc.ginkgo.user.activity.login.LoginActivity;
import com.molmc.ginkgo.user.activity.user.UserActivity;

/**
 * Created by hhe on 2018/3/7.
 * 用户组件功能实现
 */

public class ComponentUser implements IComponent {
    @Override
    public String getName() {
        return "ComponentUser";
    }

    @Override
    public boolean onCall(CC cc) {
        Context context = cc.getContext();
        Intent intent;
        switch (cc.getActionName()) {
            case "toLoginActivity":
                intent = new Intent(context, LoginActivity.class);
                intent.putExtra("callId", cc.getCallId());
                if (!(context instanceof Activity)) {
                    //调用方没有设置context或app间组件跳转，context为application
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(intent);
                if (context instanceof Activity) {
                    ((Activity) context).finish();
                }
                CC.sendCCResult(cc.getCallId(), CCResult.success());
                break;
            case "toLoginActivityClearTask":
                intent = new Intent(context, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("callId", cc.getCallId());
                context.startActivity(intent);
                CC.sendCCResult(cc.getCallId(), CCResult.success());
                break;
            case "toUserActivity":
                intent = new Intent(context, UserActivity.class);
                intent.putExtra("callId", cc.getCallId());
                if (!(context instanceof Activity)) {
                    //调用方没有设置context或app间组件跳转，context为application
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(intent);
                CC.sendCCResult(cc.getCallId(), CCResult.success());
                break;
            case "logout":
                logout(cc.getContext(), cc.getCallId(), null);
                break;
        }
        return false;
    }

    /**
     * 用户注销
     */
    public static void logout(Context context, String callId, StateListener successListener) {
        MQTTClient.destroy();
        // 取消自动登录
        DBDataSource.getInstance(context.getApplicationContext()).cancelAutoLogin();
        // 清空所有通知
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert nm != null;
        nm.cancelAll();
        // 用户请求退出接口
        HttpParams httpParams = new HttpParams();
        httpParams.put("userId", CacheDataSource.getUserId());
        NetDataSource.post(null, Urls.USER_LOGOUT, httpParams, null);
        // 清除缓存信息
        CacheDataSource.clearCache();
        // 跳转登录界面
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("callId", callId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        if (successListener != null) {
            successListener.onSuccess();
        } else {
            CC.sendCCResult(callId, CCResult.success());
        }
    }
}
