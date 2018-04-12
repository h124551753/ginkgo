package com.molmc.ginkgo.message.fragment;

import android.view.View;

import com.molmc.ginkgo.basic.base.BaseFragment;
import com.molmc.ginkgo.message.R;

public class MessageRootFragment extends BaseFragment {

    @Override
    protected View setContentView() {
        return getLayoutInflater().inflate(R.layout.fragment_message, null);
    }

    @Override
    protected void init() {

    }
}
