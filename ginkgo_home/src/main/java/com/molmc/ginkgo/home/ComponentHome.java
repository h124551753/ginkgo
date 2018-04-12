package com.molmc.ginkgo.home;

import com.billy.cc.core.component.CC;
import com.billy.cc.core.component.CCResult;
import com.billy.cc.core.component.IComponent;
import com.molmc.ginkgo.home.fragment.HomeRootFragment;

/**
 * Created by 10295 on 2018/3/23.
 * 到发组件功能实现
 */

public class ComponentHome implements IComponent {
    @Override
    public String getName() {
        return "ComponentHome";
    }

    @Override
    public boolean onCall(CC cc) {
        switch (cc.getActionName()) {
            case "getHomeRootFragment":
                CC.sendCCResult(cc.getCallId(), CCResult.success("fragment", new HomeRootFragment()));
                break;
        }
        return false;
    }
}
