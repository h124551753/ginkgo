package com.molmc.ginkgo.discovery;

import com.billy.cc.core.component.CC;
import com.billy.cc.core.component.CCResult;
import com.billy.cc.core.component.IComponent;
import com.molmc.ginkgo.discovery.fragment.DiscoveryRootFragment;

/**
 * Created by 10295 on 2018/3/23.
 * 到发组件功能实现
 */

public class ComponentDiscovery implements IComponent {
    @Override
    public String getName() {
        return "ComponentDiscovery";
    }

    @Override
    public boolean onCall(CC cc) {
        switch (cc.getActionName()) {
            case "getDiscoveryRootFragment":
                CC.sendCCResult(cc.getCallId(), CCResult.success("fragment", new DiscoveryRootFragment()));
                break;
        }
        return false;
    }
}
