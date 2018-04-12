package com.molmc.dispatch.fragment;

import android.view.View;
import android.widget.Button;

import com.billy.cc.core.component.CC;
import com.molmc.dispatch.R;
import com.molmc.ginkgo.basic.base.BaseFragment;

public class MineFragment extends BaseFragment {

    private Button btnToStepCount;

    @Override
    protected View setContentView() {
        return getLayoutInflater().inflate(R.layout.fragment_mine, null);
    }

    @Override
    protected void init() {
        initView();
        initListener();
    }

    private void initView() {
        btnToStepCount = findViewById(R.id.btn_goto_step);
    }


    private void initListener(){
        addOnClickListeners(R.id.btn_goto_step);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view == btnToStepCount) {
            CC.obtainBuilder("ComponentStepCount")
                    .setActionName("toStepCountActivity")
                    .build()
                    .call();
        }
    }
}
