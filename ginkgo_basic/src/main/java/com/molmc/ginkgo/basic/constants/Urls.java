package com.molmc.ginkgo.basic.constants;

public interface Urls {

    String BASE_URL = "http://ghgylconsole.intoyun.com/";

    /**
     * 用户登录
     */
    String LOGIN = "kingkong/0.01/auth/userLogin";

    /**
     * 用户登出请求
     */
    String USER_LOGOUT = "kingkong/0.01/auth/userLogout";

    /**
     * 用户上传文件
     */
    String UPLOAD = "kingkong/0.01/job/uploadJobOperatorFile";

    /**
     * 上传单个文件
     */
    String UPLOAD_SINGLE = "kingkong/0.01/fileUpload/uploadSingleFile";

    /**
     * 检查更新
     */
    String CHECK_UPDATE = "/kingkong/0.01/file/versionCheck";
}
