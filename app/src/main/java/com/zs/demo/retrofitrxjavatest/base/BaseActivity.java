package com.zs.demo.retrofitrxjavatest.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.zs.demo.retrofitrxjavatest.request.RequestApi;

import io.reactivex.Observable;

/**
 * Created by zs
 * Date：2017年 09月 25日
 * Time：11:24
 * —————————————————————————————————————
 * About:
 * —————————————————————————————————————
 */
public abstract class BaseActivity extends BaseRxActivity implements View.OnClickListener{

    protected RequestApi mRequestApi = null;
    protected Activity mActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestApi = RequestApi.getInstance();
        mActivity = this;
    }

    /**
     * requestData
     * @param request
     * @param type
     */
    protected abstract void requestData(Observable request, int type);
}
