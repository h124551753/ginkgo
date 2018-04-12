package com.molmc.ginkgo.user.activity.signup;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.molmc.ginkgo.basic.base.BaseActivity;
import com.molmc.ginkgo.basic.views.TitleView;
import com.molmc.ginkgo.user.R;
import com.molmc.ginkgo.user.constants.SaveErrorCode;

/**
 * Created by hhe on 2018/4/11
 * 登录配置界面
 */

public class SignUpActivity extends BaseActivity implements SignUpContract.View, SignUpPresenter.SaveListener {
    private TitleView tvTitle;
    private EditText etAddress;
    private EditText etPort;

    private SignUpContract.Presenter mPresenter;

    @Override
    public int setContentView() {
        return R.layout.user_activity_login_config;
    }

    @Override
    public void start() {
        initView();
        initPresenter();
        initListener();
    }

    private void initPresenter() {
        mPresenter = new SignUpPresenter(this);
        mPresenter.start();
    }

    private void initView() {
        tvTitle = findViewById(R.id.tv_title);
        etAddress = findViewById(R.id.et_address);
        etPort = findViewById(R.id.et_port);
    }

    private void initListener() {
        tvTitle.setOnBackClickListener((view) -> finish());
        addOnClickListeners(R.id.btn_confirm);
    }

    @Override
    public void redisplayHost(String ip, String port) {
        if (TextUtils.isEmpty(ip) || TextUtils.isEmpty(port)) {
            return;
        }
        etAddress.setText(ip);
        etPort.setText(port);
        // 设置光标在最后
        etPort.requestFocus();
        etPort.setSelection(port.length());
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public void onClick(View view) {
        mPresenter.saveHost(getText(etAddress), getText(etPort), this);
    }

    @Override
    public void saveSuccess() {
        finish();
    }

    @Override
    public void saveFailed(int saveErrorCode) {
        switch (saveErrorCode) {
            case SaveErrorCode.CAN_NOT_BE_NULL:
                showToast(R.string.toast_complete_ip_port);
                break;
            case SaveErrorCode.FORMAT_ERROR:
                showToast(R.string.toast_format_error);
                break;
        }
    }
}
