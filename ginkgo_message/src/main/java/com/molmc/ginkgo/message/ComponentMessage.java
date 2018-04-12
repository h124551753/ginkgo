package com.molmc.ginkgo.message;

import com.billy.cc.core.component.CC;
import com.billy.cc.core.component.CCResult;
import com.billy.cc.core.component.IComponent;
import com.molmc.ginkgo.message.fragment.MessageRootFragment;

/**
 * Created by 10295 on 2018/3/23.
 * 到发组件功能实现
 */

public class ComponentMessage implements IComponent {
    @Override
    public String getName() {
        return "ComponentMessage";
    }

    @Override
    public boolean onCall(CC cc) {
        switch (cc.getActionName()) {
            case "getMessageRootFragment":
                CC.sendCCResult(cc.getCallId(), CCResult.success("fragment", new MessageRootFragment()));
                break;
        }
        return false;
    }
}
