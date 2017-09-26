package com.zs.demo.retrofitrxjavatest.listener;

/**
 * Created by zhpan on 2017/4/22.
 */

public interface BaseImpl {



//    boolean addRxStop(Disposable disposable);
//
//    boolean addRxDestroy(Disposable disposable);
//
//    void remove(Disposable disposable);

    /**
     * 显示ProgressDialog
     */
    void showProgress(String msg);

    /**
     * 取消ProgressDialog
     */
    void dismissProgress();

}
