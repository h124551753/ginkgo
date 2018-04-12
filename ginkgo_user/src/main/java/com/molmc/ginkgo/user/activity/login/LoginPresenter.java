package com.molmc.ginkgo.user.activity.login;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.lzy.okgo.model.HttpParams;
import com.molmc.ginkgo.basic.biz.BasicBiz;
import com.molmc.ginkgo.basic.constants.SPKey;
import com.molmc.ginkgo.basic.constants.Urls;
import com.molmc.ginkgo.basic.data.CacheDataSource;
import com.molmc.ginkgo.basic.data.DBDataSource;
import com.molmc.ginkgo.basic.data.NetDataSource;
import com.molmc.ginkgo.basic.data.SPDataSource;
import com.molmc.ginkgo.basic.entity.UserEntity;
import com.molmc.ginkgo.basic.listener.ResponseListener;
import com.molmc.ginkgo.basic.mqtt.MQTTClient;
import com.molmc.ginkgo.basic.utils.JsonUtils;
import com.molmc.ginkgo.basic.utils.LogUtils;
import com.molmc.ginkgo.user.constants.LoginErrorCode;
import com.molmc.ginkgo.user.entity.AuthEntity;

import java.util.List;

/**
 * Created by wyl on 2017/3/23
 */

class LoginPresenter implements LoginContract.Presenter, MQTTClient.ConnectListener {
    private static final String TAG = "LoginPresenter";

    private final LoginContract.View mView;
    private final Context mContext;
    private final DBDataSource mDbDataSource;
    private LoginListener mLoginListener;
    private AuthEntity mAuthEntity;
    private String mUsername;
    private String mPassword;

    LoginPresenter(LoginContract.View view) {
        this.mView = view;
        this.mContext = view.getContext();
        this.mDbDataSource = DBDataSource.getInstance(mContext.getApplicationContext());
    }

    @Override
    public void start() {
        // 取出本地保存的用户信息进行回显
        UserEntity lastLoginUser = mDbDataSource.getLastLoginUser();
        if (lastLoginUser != null) {
            mView.displayRecentlyLoginUserInfo(lastLoginUser);
        }
    }

    @Override
    public UserEntity getUserByUsername(String username) {
        return mDbDataSource.getUserByUsername(username);
    }

    @Override
    public void login(String username, String password, LoginListener loginListener) {
        this.mLoginListener = loginListener;
        this.mUsername = username;
        this.mPassword = password;

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            loginListener.loginFailed(LoginErrorCode.CAN_NOT_EMPTY);
            return;
        }

        String ip = (String) SPDataSource.get(mContext, SPKey.KEY_IP, "");
        String port = (String) SPDataSource.get(mContext, SPKey.KEY_PORT, "");
        if (TextUtils.isEmpty(ip) || TextUtils.isEmpty(port)) {
            loginListener.loginFailed(LoginErrorCode.NO_IP_AND_PORT);
            return;
        }

        loginListener.onLogin();

        CacheDataSource.setIp(ip);
        CacheDataSource.setPort(port);
        CacheDataSource.setBaseUrl(String.format("http://%s:%s/", ip, port));

        // 测试模式
        CacheDataSource.setTestMode(mUsername.equals("admin") && mPassword.equals("admin"));

        if (CacheDataSource.getTestMode()) {
            mAuthEntity = JsonUtils.getTestEntity(mContext, AuthEntity.class);
            connectSuccess();
            return;
        }

        // 获取当前用户连接的WIFI信息用来定位
        String bssid = BasicBiz.getBSSID(mContext);

        HttpParams httpParams = new HttpParams();
        httpParams.put("account", username);
        httpParams.put("pwd", password);
        httpParams.put("ssId", bssid);
        httpParams.put("device", 1);

        NetDataSource.post(this, Urls.LOGIN, httpParams, new ResponseListener<AuthEntity>() {
            @Override
            public AuthEntity convert(String jsonStr) {
                return new Gson().fromJson(jsonStr, AuthEntity.class);
            }

            @Override
            public void onSuccess(AuthEntity authEntity) {
                mAuthEntity = authEntity;
                // 开启MQ
                new MQTTClient.Connector(mContext.getApplicationContext(), ip)
                        .listener(LoginPresenter.this)
                        .connect();
                LogUtils.i(TAG, "用户名:" + mUsername + ",密码:" + mPassword);
                LogUtils.i(TAG, "IP地址:" + ip + ",端口号:" + port);
                LogUtils.i(TAG, "BSSID地址:" + bssid);
            }

            @Override
            public void onFailed(int errorCode, String errorInfo) {
                loginListener.loginFailed(LoginErrorCode.LOGIN_FAILED);
            }
        });
    }

    @Override
    public List<UserEntity> getAllSavedUser() {
        return mDbDataSource.getAllSavedUser();
    }

    @Override
    public void deleteUser(UserEntity userEntity) {
        mDbDataSource.deleteUser(userEntity);
    }

    @Override
    public void connectSuccess() {
//        saveData();
        mLoginListener.loginSuccess();
    }

    @Override
    public void connectFailed() {
        mLoginListener.loginFailed(LoginErrorCode.MQ_CONNECT_FAILED);
        MQTTClient.destroy();
    }

    /**
     * 存储数据
     */
    private void saveData() {
        AuthEntity.UserDto user = mAuthEntity.getUser();

        CacheDataSource.setLoginState(true);
        CacheDataSource.setHttpKey(mAuthEntity.getHttpKey());
        CacheDataSource.setToken(mAuthEntity.getToken());
        CacheDataSource.setMqKey(mAuthEntity.getMqKey());
        // TODO 记录配置的SSID   CacheDataSource.setSsid("");
        CacheDataSource.setUserId(mAuthEntity.getUserId());
        CacheDataSource.setTeamId(mAuthEntity.getTeamId());
        CacheDataSource.setTeamName(mAuthEntity.getTeamName());
        CacheDataSource.setUsername(mUsername);
        CacheDataSource.setPassword(mPassword);
        CacheDataSource.setSipUsername(user.getSiptelladdress());
        CacheDataSource.setSipPassword(user.getSippassword());
        CacheDataSource.setRealName(user.getUsername());
        CacheDataSource.setUserHeadAddress(user.getPhoto());

        UserEntity userBean = new UserEntity();
        userBean.setUserId(mAuthEntity.getUserId());
        userBean.setUsername(mUsername);
        userBean.setPassword(mPassword);
        userBean.setHeaderUrl(user.getPhoto());
        userBean.setPocUsername(user.getSiptelladdress());
        userBean.setPocPassword(user.getSippassword());
        userBean.setLastLoginTime(System.currentTimeMillis());
        userBean.setLoginIp(CacheDataSource.getIp());
        userBean.setLoginPort(CacheDataSource.getPort());
        userBean.setIsAutoLogin(true);
        mDbDataSource.saveUserInfo(userBean);
    }

    @Override
    public void exit() {
        NetDataSource.unSubscribe(this);
    }

    /**
     * 登录状态回调
     */
    interface LoginListener {
        /**
         * 登录中回调
         */
        void onLogin();

        /**
         * 登录成功回调
         */
        void loginSuccess();

        /**
         * 登录失败回调
         *
         * @param loginErrorCode 错误码
         */
        void loginFailed(int loginErrorCode);
    }
}
