package com.molmc.ginkgo.home.fragment;

import android.view.View;

import com.molmc.ginkgo.basic.base.BaseFragment;
import com.molmc.ginkgo.home.R;

public class HomeRootFragment extends BaseFragment {

    @Override
    protected View setContentView() {
        return getLayoutInflater().inflate(R.layout.fragment_home, null);
    }

    @Override
    protected void init() {

    }
}
