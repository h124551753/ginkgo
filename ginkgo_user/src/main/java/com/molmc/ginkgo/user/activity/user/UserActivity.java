package com.molmc.ginkgo.user.activity.user;

import android.view.View;
import android.widget.TextView;

import com.molmc.ginkgo.basic.base.BaseActivity;
import com.molmc.ginkgo.basic.biz.BasicBiz;
import com.molmc.ginkgo.basic.data.CacheDataSource;
import com.molmc.ginkgo.basic.data.NetDataSource;
import com.molmc.ginkgo.basic.listener.StateListener;
import com.molmc.ginkgo.basic.utils.ImageLoader;
import com.molmc.ginkgo.basic.views.CircleImageView;
import com.molmc.ginkgo.basic.views.CustomDialog;
import com.molmc.ginkgo.basic.views.TitleView;
import com.molmc.ginkgo.user.ComponentUser;
import com.molmc.ginkgo.user.R;

/**
 * Created by 10295 on 2017/12/19 0019
 * 用户信息界面
 */

public class UserActivity extends BaseActivity {
    private TitleView tvTitle;
    private CircleImageView ivHead;
    private TextView tvUsername;
    private TextView tvRoleName;

    private CustomDialog customDialog;
    private String callId;

    @Override
    protected int setContentView() {
        return R.layout.user_activity_user;
    }

    @Override
    protected void start() {
        initView();
        initData();
        initListener();
    }

    private void initView() {
        tvTitle = findViewById(R.id.tv_title);
        ivHead = findViewById(R.id.iv_head);
        tvUsername = findViewById(R.id.tv_username);
        tvRoleName = findViewById(R.id.tv_role_name);
    }

    private void initData() {
        callId = getIntent().getStringExtra("callId");
        if (isEmpty(callId)) {
            tvTitle.showBackButton(false);
        }
        tvUsername.setText(CacheDataSource.getRealName());
        tvRoleName.setText(String.format("(%s)", CacheDataSource.getTeamName()));
        ImageLoader.loadAddress(ivHead, CacheDataSource.getUserHeadAddress(), R.mipmap.basic_ic_head_single, R.mipmap.basic_ic_head_single);
    }

    private void initListener() {
        tvTitle.setOnBackClickListener(v -> finish());
        addOnClickListeners(R.id.rl_check_update, R.id.btn_logout);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.rl_check_update) {
            BasicBiz.checkVersionUpdate(mContext, this, true);
        } else if (id == R.id.btn_logout) {
            showDialog();
            ComponentUser.logout(mContext, callId, new StateListener() {
                @Override
                public void onSuccess() {
                    if (customDialog != null) {
                        customDialog.dismiss();
                    }
                }

                @Override
                public void onFailed() {

                }
            });
        }
    }

    private void showDialog() {
        // 注销中，显示dialog
        if (customDialog == null) {
            customDialog = new CustomDialog.StateBuilder(mContext)
                    .setStateText(getString(R.string.dialog_on_logout))
                    .setIrrevocable()
                    .create();
        }
        customDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetDataSource.unSubscribe(this);
        NetDataSource.unRegister(this);
    }
}
