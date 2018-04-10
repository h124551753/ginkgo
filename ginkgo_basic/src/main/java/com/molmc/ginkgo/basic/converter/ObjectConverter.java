package com.molmc.ginkgo.basic.converter;

import com.google.gson.Gson;
import com.lzy.okgo.convert.Converter;
import com.molmc.ginkgo.basic.entity.NetBaseResponse;
import com.molmc.ginkgo.basic.listener.ResponseListener;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by 10295 on 2017/12/18 0018
 */

public class ObjectConverter<T> implements Converter<T> {
    private final ResponseListener<T> mListener;

    public ObjectConverter(ResponseListener<T> listener) {
        this.mListener = listener;
    }

    @Override
    public T convertResponse(Response response) throws Throwable {
        int code = response.code();
        if (code == 200){
            ResponseBody body = response.body();
            return mListener.convert(body.string());
        } else if (code == 201){
            return mListener.convert(null);
        } else {
            ResponseBody body = response.body();
            NetBaseResponse errResponse = new Gson().fromJson(body.string(), NetBaseResponse.class);
            Observable.empty()
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete(() -> mListener.onFailed(errResponse.getCode(), errResponse.getMsg()))
                    .subscribe();
            return null;
        }
    }
}
