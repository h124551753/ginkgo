package com.molmc.ginkgo.user.activity.signup;

import com.molmc.ginkgo.basic.base.BasePresenter;
import com.molmc.ginkgo.basic.base.BaseView;

/**
 * Created by wyl on 2017/3/28.
 * 登录配置Contract
 */

class SignUpContract {
    interface View extends BaseView {
        void redisplayHost(String ip, String port);
    }

    interface Presenter extends BasePresenter {
        void saveHost(String ip, String port, SignUpPresenter.SaveListener saveListener);
    }
}
