package com.molmc.ginkgo.stepcount;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.billy.cc.core.component.CC;
import com.billy.cc.core.component.CCResult;
import com.billy.cc.core.component.IComponent;
import com.molmc.ginkgo.stepcount.activity.StepPanelActivity;

/**
 * Created by 10295 on 2018/3/23.
 * 到发组件功能实现
 */

public class ComponentStepCount implements IComponent {
    @Override
    public String getName() {
        return "ComponentStepCount";
    }

    @Override
    public boolean onCall(CC cc) {
        switch (cc.getActionName()) {
            case "toStepCountActivity":
            Context context = cc.getContext();
            Intent intent = new Intent(context, StepPanelActivity.class);
            intent.putExtra("callId", cc.getCallId());
            if (!(context instanceof Activity)) {
                //调用方没有设置context或app间组件跳转，context为application
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
            CC.sendCCResult(cc.getCallId(), CCResult.success());
            break;
        }
        return false;
    }
}
