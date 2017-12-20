package com.zs.demo.retrofitrxjavatest.util;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zs
 * Date：2017年 09月 26日
 * Time：14:34
 * —————————————————————————————————————
 * About: 请求工具类
 * —————————————————————————————————————
 * @author Administrator
 */
public class RequestUtil {

    public static Observable getObservable(Observable request){
        return request.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }



}
