package com.zs.demo.retrofitrxjavatest.util;

import com.zs.demo.retrofitrxjavatest.bean.BaseResponse;
import com.zs.demo.retrofitrxjavatest.request.RequestApi;
import com.zs.demo.retrofitrxjavatest.request.signature.SignatureParams;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zs
 * Date：2017年 09月 26日
 * Time：14:34
 * —————————————————————————————————————
 * About:
 * —————————————————————————————————————
 */

public class RequestUtil {

    public static Observable<BaseResponse> request(String type , String option){

        return RequestApi.getInstance().getRequestService(RequestApi.REQUEST_URL1).getRequestData(
                type, NewAES.encrypt(option, SignatureParams.IMEncodingAESKey))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    public static Observable getObservable(Observable request){
        return request.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }



}
