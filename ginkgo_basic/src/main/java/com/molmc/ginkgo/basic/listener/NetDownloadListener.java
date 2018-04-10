package com.molmc.ginkgo.basic.listener;

import java.io.File;

/**
 * Created by 10295 on 2018/1/9.
 * 上传文件事件监听
 */

public interface NetDownloadListener {
    void downloadProgress(String serverUri, int progress);

    void downloadSuccess(File file, String serverUri);

    void downloadFailed(String serverUri);
}
