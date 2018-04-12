package com.molmc.ginkgo.user.activity.login;

import android.support.annotation.NonNull;

import com.molmc.ginkgo.basic.base.BasePresenter;
import com.molmc.ginkgo.basic.base.BaseView;
import com.molmc.ginkgo.basic.entity.UserEntity;

import java.util.List;

/**
 * Created by wyl on 2017/3/22.
 * 登录界面Contract
 */

class LoginContract {
    interface View extends BaseView {
        void displayRecentlyLoginUserInfo(@NonNull UserEntity userEntity);
    }

    interface Presenter extends BasePresenter {
        UserEntity getUserByUsername(String username);

        void login(String username, String password, LoginPresenter.LoginListener loginListener);

        List<UserEntity> getAllSavedUser();

        void deleteUser(UserEntity userEntity);
    }
}
