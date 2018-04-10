package com.molmc.ginkgo.basic.data;

import android.annotation.SuppressLint;
import android.app.Application;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.MemoryCookieStore;
import com.lzy.okgo.exception.OkGoException;
import com.lzy.okgo.exception.StorageException;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okgo.request.PostRequest;
import com.lzy.okrx2.adapter.ObservableBody;
import com.lzy.okserver.OkDownload;
import com.lzy.okserver.OkUpload;
import com.lzy.okserver.download.DownloadListener;
import com.lzy.okserver.download.DownloadTask;
import com.lzy.okserver.upload.UploadListener;
import com.lzy.okserver.upload.UploadTask;
import com.molmc.ginkgo.basic.constants.Urls;
import com.molmc.ginkgo.basic.converter.ObjectConverter;
import com.molmc.ginkgo.basic.listener.NetDownloadListener;
import com.molmc.ginkgo.basic.listener.NetUploadListener;
import com.molmc.ginkgo.basic.listener.ResponseListener;
import com.molmc.ginkgo.basic.utils.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;

/**
 * Created by wyl on 2017/6/8
 * 网络数据
 */

public class NetDataSource {
    private static final int PAGE_START = 1;    // 查询起始页
    private static final int PAGE_SIZE = 999;   // 查询结束页
    private static final int READ_TIMEOUT = 10; // 读取超时时间
    private static final int WRITE_TIMEOUT = 10;    // 写入超时时间
    private static final int CONNECT_TIMEOUT = 10;  // 连接超时时间
    private static final int RETRY_COUNT = 0;   // 请求重试次数

    private static final HashMap<Object, CompositeDisposable> requestMap = new HashMap<>();
    private static final HashMap<Object, ArrayList<Runnable>> tasksMap = new HashMap<>();

    /**
     * 调用其他方法的前提
     *
     * @param application application
     * @param logSwitch   LOG开关
     * @param logTag      LOG标签
     */
    public static void init(Application application, boolean logSwitch, String logTag) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if (logSwitch) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(logTag);
            // log打印级别，决定了log显示的详细程度
            loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
            // log颜色级别，决定了log在控制台显示的颜色
            loggingInterceptor.setColorLevel(Level.INFO);
            builder.addInterceptor(loggingInterceptor);
        }

        // 配置超时时间
        // 全局的读取超时时间
        builder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
        // 全局的写入超时时间
        builder.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);
        // 全局的连接超时时间
        builder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);

        // 配置Cookie
        // 使用内存保持cookie，app退出后，cookie消失
        builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));

        // 配置Https
        // 信任所有证书
        // HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();
        // builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
        // 配置https的域名匹配规则，详细看demo的初始化介绍，不需要就不要加入，使用不当会导致https握手失败
        // builder.hostnameVerifier(new SafeHostnameVerifier());

        //必须调用初始化
        OkGo.getInstance()
                .init(application)
                //设置OkHttpClient
                .setOkHttpClient(builder.build())
                //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheMode(CacheMode.NO_CACHE)
                //全局统一缓存时间，默认永不过期，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)
                //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
                .setRetryCount(RETRY_COUNT);
    }


    public static <T> void post(Object tag, String url, HttpParams httpParams, ResponseListener<T> listener) {
        url = Urls.BASE_URL + url;
        OkGo.<T>post(url)
                .headers(getHeaderParams())
                .params(httpParams)
                .converter(new ObjectConverter<>(listener))
                .adapt(new ObservableBody<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObserver(tag, url, listener));
    }

    public static <T> void post(Object tag, String url, Object data, ResponseListener<T> listener) {
        url = Urls.BASE_URL + url;
        OkGo.<T>post(url)
                .headers(getHeaderParams())
                .upJson(new Gson().toJson(data))
                .converter(new ObjectConverter<>(listener))
                .adapt(new ObservableBody<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObserver(tag, url, listener));
    }

    public static <T> void get(Object tag, String url, HttpParams httpParams, ResponseListener<T> listener) {
        url = Urls.BASE_URL + url;
        OkGo.<T>get(url)
                .headers(getHeaderParams())
                .params(httpParams)
                .converter(new ObjectConverter<>(listener))
                .adapt(new ObservableBody<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObserver(tag, url, listener));
    }

    /**
     * 不需要添加data参数的上传(eg发布命令时上传的文件不需要跟任务关联)
     *
     * @param tag              用来取消监听，页面销毁时一定要调用NetDataSource.unRegister()取消监听，否则会产生内存泄漏
     * @param file             要上传的文件
     * @param myUploadListener 上传状态回调
     * @return 上传任务对象，开始任务需调用start()
     */
    @SuppressWarnings("unchecked")
    public static UploadTask<String> uploadFileNoData(Object tag, File file, NetUploadListener myUploadListener) {

        UploadListener<String> uploadListener = new UploadListener<String>(tag) {
            @Override
            public void onStart(Progress progress) {

            }

            @Override
            public void onProgress(Progress progress) {
                LogUtils.i("上传文件", file.getName() + "上传进度：" + progress.fraction + "%");
                myUploadListener.uploadProgress(file, (int) (progress.fraction * 100));
            }

            @Override
            public void onError(Progress progress) {
                LogUtils.i("上传文件", file.getName() + "上传失败");
                myUploadListener.uploadFailed(file);
                OkUpload.getInstance().removeTask(progress.tag);
            }

            @Override
            public void onFinish(String s, Progress progress) {
                LogUtils.i("上传文件", file.getName() + "上传成功");
                myUploadListener.uploadSuccess(file);
                OkUpload.getInstance().removeTask(progress.tag);
            }

            @Override
            public void onRemove(Progress progress) {

            }
        };

        UploadTask<String> uploadTask = (UploadTask<String>) OkUpload.getInstance().getTask(file.getName());

        if (uploadTask == null) {

            PostRequest<String> fileUploadRequest = OkGo.<String>post(Urls.BASE_URL + Urls.UPLOAD_SINGLE)
                    .headers(getHeaderParams())
                    .upFile(file)
                    .converter(new StringConvert());

            uploadTask = OkUpload.request(file.getName(), fileUploadRequest).register(uploadListener);
        } else {
            uploadTask.register(uploadListener);
        }

        register(tag, uploadTask);

        return uploadTask;
    }

    /**
     * 添加data参数的上传(eg作业进行中上传的附件需要跟作业关联)
     *
     * @param tag              用来取消监听，页面销毁时一定要调用NetDataSource.unRegister()取消监听，否则会产生内存泄漏
     * @param dateObject       需要关联的数据
     * @param file             要上传的文件
     * @param myUploadListener 上传状态回调
     * @return 上传任务对象，开始任务需调用start()
     */
    @SuppressWarnings("unchecked")
    public static UploadTask<?> uploadFile(Object tag, Object dateObject, File file, NetUploadListener myUploadListener) {
        UploadListener<String> uploadListener = new UploadListener<String>(tag) {
            @Override
            public void onStart(Progress progress) {

            }

            @Override
            public void onProgress(Progress progress) {
                LogUtils.i("上传文件", file.getName() + "上传进度：" + progress.fraction + "%");
                myUploadListener.uploadProgress(file, (int) (progress.fraction * 100));
            }

            @Override
            public void onError(Progress progress) {
                LogUtils.i("上传文件", file.getName() + "上传失败");
                myUploadListener.uploadFailed(file);
                OkUpload.getInstance().removeTask(progress.tag);
            }

            @Override
            public void onFinish(String s, Progress progress) {
                LogUtils.i("上传文件", file.getName() + "上传成功");
                myUploadListener.uploadSuccess(file);
                OkUpload.getInstance().removeTask(progress.tag);
            }

            @Override
            public void onRemove(Progress progress) {

            }
        };

        UploadTask<String> uploadTask = (UploadTask<String>) OkUpload.getInstance().getTask(file.getName());

        if (uploadTask == null) {

            PostRequest<String> fileUploadRequest = OkGo.<String>post(Urls.BASE_URL + Urls.UPLOAD)
                    .headers(getHeaderParams())
                    .upFile(file)
                    .converter(new StringConvert());

            uploadTask = OkUpload.request(file.getName(), fileUploadRequest).register(uploadListener);
        } else {
            uploadTask.register(uploadListener);
        }

        register(tag, uploadTask);

        return uploadTask;
    }

    /**
     * 下载文件
     *
     * @param serverUri          文件服务器地址
     * @param targetDir          文件目标存储目录
     * @param myDownloadListener 下载状态监听
     */
    public static DownloadTask downloadFile(Object tag, String serverUri, String targetDir, NetDownloadListener myDownloadListener) {
        DownloadListener downloadListener = new DownloadListener(tag) {
            @Override
            public void onStart(Progress progress) {

            }

            @Override
            public void onProgress(Progress progress) {
                myDownloadListener.downloadProgress(serverUri, (int) (progress.fraction * 100));
            }

            @Override
            public void onError(Progress progress) {
                if (progress.exception instanceof OkGoException) {
                    OkDownload.getInstance().getTask(serverUri).restart();
                } else if (progress.exception instanceof StorageException) {
                    OkDownload.getInstance().getTask(serverUri).restart();
                } else {
                    myDownloadListener.downloadFailed(serverUri);
                    OkDownload.getInstance().removeTask(progress.tag);
                }
            }

            @Override
            public void onFinish(File file, Progress progress) {
                myDownloadListener.downloadSuccess(file, serverUri);
                OkDownload.getInstance().removeTask(progress.tag);
            }

            @Override
            public void onRemove(Progress progress) {

            }
        };

        DownloadTask downloadTask = OkDownload.getInstance().getTask(serverUri);

        if (downloadTask == null) {
            GetRequest<File> fileGetRequest = OkGo.get(serverUri);
            downloadTask = OkDownload.request(serverUri, fileGetRequest)
                    .folder(targetDir)
                    .register(downloadListener);
        } else {
            downloadTask.register(downloadListener);
        }

        register(tag, downloadTask);

        return downloadTask;
    }

    private static <T> Observer<T> getObserver(Object tag, String url, ResponseListener<T> listener) {
        return new Observer<T>() {
            @Override
            public void onSubscribe(Disposable d) {
                subscribe(tag, d);
            }

            @Override
            public void onNext(T t) {
                if (listener != null) {
                    listener.onSuccess(t);
                }
            }

            @Override
            public void onError(Throwable e) {
                LogUtils.e("接口调用失败", "onError:::" + url + ":::" + e.toString());
                if (listener != null) {
                    listener.onFailed(0, e.getMessage());
                }
            }

            @Override
            public void onComplete() {

            }
        };
    }

    private static HttpHeaders getHeaderParams(){
        HttpHeaders httpHeaders = new HttpHeaders();
        return httpHeaders;
    }

    private static void subscribe(Object tag, Disposable d) {
        if (tag != null) {
            CompositeDisposable subscription;
            if ((subscription = requestMap.get(tag)) == null) {
                subscription = new CompositeDisposable();
                requestMap.put(tag, subscription);
            }
            subscription.add(d);
        }
    }

    public static void unSubscribe(Object tag) {
        if (requestMap.containsKey(tag)) {
            requestMap.get(tag).dispose();
            requestMap.remove(tag);
        }
    }

    private static void register(Object tag, Runnable task) {
        ArrayList<Runnable> tasks = tasksMap.get(tag);
        if (tasks == null) {
            tasks = new ArrayList<>();
            tasksMap.put(tag, tasks);
        }
        tasks.add(task);
    }

    public static void unRegister(Object tag) {
        ArrayList<Runnable> tasks = tasksMap.get(tag);
        if (tasks != null) {
            for (Runnable task : tasks) {
                if (task instanceof DownloadTask) {
                    ((DownloadTask) task).listeners.remove(tag);
                }
                if (task instanceof UploadTask) {
                    ((UploadTask) task).listeners.remove(tag);
                }
            }
            tasksMap.remove(tag);
        }
    }

    @SuppressLint("BadHostnameVerifier")
    private static class SafeHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
}
