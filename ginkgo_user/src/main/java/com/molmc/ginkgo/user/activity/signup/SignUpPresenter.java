package com.molmc.ginkgo.user.activity.signup;

import android.content.Context;
import android.text.TextUtils;

import com.molmc.ginkgo.basic.constants.SPKey;
import com.molmc.ginkgo.basic.data.SPDataSource;
import com.molmc.ginkgo.user.constants.SaveErrorCode;
import com.molmc.ginkgo.user.constants.UrlRegular;

/**
 * Created by wyl on 2017/3/28.
 * 登录配置Presenter
 */

class SignUpPresenter implements SignUpContract.Presenter {

    private final SignUpContract.View mView;
    private final Context mContext;

    SignUpPresenter(SignUpContract.View view) {
        this.mView = view;
        this.mContext = view.getContext();
    }

    @Override
    public void start() {
        String ip = (String) SPDataSource.get(mContext, SPKey.KEY_IP, "");
        String port = (String) SPDataSource.get(mContext, SPKey.KEY_PORT, "");
        mView.redisplayHost(ip, port);
    }

    @Override
    public void saveHost(String ip, String port, SaveListener saveListener) {
        if (TextUtils.isEmpty(ip) || TextUtils.isEmpty(port)) {
            saveListener.saveFailed(SaveErrorCode.CAN_NOT_BE_NULL);
        } else if (!ip.matches(UrlRegular.IP_REGULAR) || !port.matches(UrlRegular.PORT_REGULAR)) {
            saveListener.saveFailed(SaveErrorCode.FORMAT_ERROR);
        } else {
            SPDataSource.put(mContext, SPKey.KEY_IP, ip);
            SPDataSource.put(mContext, SPKey.KEY_PORT, port);
            saveListener.saveSuccess();
        }
    }

    @Override
    public void exit() {

    }

    interface SaveListener {
        void saveSuccess();

        void saveFailed(int saveErrorCode);
    }
}
