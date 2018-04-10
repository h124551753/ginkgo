package com.molmc.dispatch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.billy.cc.core.component.CC;
import com.billy.cc.core.component.CCResult;
import com.billy.cc.core.component.IComponent;
import com.molmc.dispatch.activity.main.MainActivity;

/**
 * Created by 10295 on 2018/3/9.
 * APP组件功能实现
 */

public class ComponentApp implements IComponent {
    @Override
    public String getName() {
        return "ComponentApp";
    }

    @Override
    public boolean onCall(CC cc) {
        String actionName = cc.getActionName();
        Context context = cc.getContext();
        switch (actionName) {
            case "toMainActivity":
                Intent intent = new Intent(context, MainActivity.class);
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
            case "notifyUnreadSysRemindCountChanged":
                Activity activity = cc.getParamItem("activity");
                int unreadCount = cc.getParamItem("unreadCount");
                if (activity != null && activity instanceof MainActivity) {
                    ((MainActivity) activity).notifyUnreadSysRemindCountChanged(unreadCount);
                }
                CC.sendCCResult(cc.getCallId(), CCResult.success());
                break;
        }
        return false;
    }
}
