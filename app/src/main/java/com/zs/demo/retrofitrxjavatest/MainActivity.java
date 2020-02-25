package com.zs.demo.retrofitrxjavatest;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zs.demo.retrofitrxjavatest.base.BaseActivity;
import com.zs.demo.retrofitrxjavatest.bean.MyZBBean;
import com.zs.demo.retrofitrxjavatest.bean.RequestBean;
import com.zs.demo.retrofitrxjavatest.request.DefaultObserver;
import com.zs.demo.retrofitrxjavatest.request.RequestApi;
import com.zs.demo.retrofitrxjavatest.request.signature.SignatureParams;
import com.zs.demo.retrofitrxjavatest.util.NewAES;
import com.zs.demo.retrofitrxjavatest.util.RequestUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Administrator
 */
public class MainActivity extends BaseActivity {

    private Button mBtnGet;
    private Button mBtnPost;
    private Button mBtnRxJavaPost;
    private TextView mTv;

    private JSONObject mObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnGet = (Button) findViewById(R.id.btn_get_main);
        mBtnPost = (Button) findViewById(R.id.btn_post_main);
        mBtnRxJavaPost = (Button) findViewById(R.id.btn_rxjava_main);
        mTv = (TextView) findViewById(R.id.tv_main);

        mObj = new JSONObject();
        try {
            mObj.put("userid", "US170814000000000107");
            mObj.put("personid", "US170725000000000074");
            mObj.put("liveuid", "05dfbc3a-b202-4974-98df-3b64c443c80e");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        mBtnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Call<ResponseBody> responseBodyCall = RequestApi.getInstance().getBaseService().getWYData();
                responseBodyCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            mTv.setText(response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
            }
        });

        mBtnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RequestBean bean = new RequestBean("live-21",mObj.toString());
                Call<ResponseBody> responseBodyCall = RequestApi.getInstance().getRequestService(RequestApi.REQUEST_URL1).getZBData(bean.getVersion(),
                        bean.getOptioncode(),bean.getTimestamp(),bean.getNonce(),bean.getOption(),bean.getSignature()
                );
                responseBodyCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String back = response.body().string();
                            mTv.setText(NewAES.decrypt(back, SignatureParams.IMEncodingAESKey));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
            }
        });
        mBtnRxJavaPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                requestData(mRequestApi.getRequestService(RequestApi.REQUEST_URL1)
                        .getTestData("live-21", mObj.toString()),1);

//                mRequestApi.getRequestService(RequestApi.REQUEST_URL1)
//                        .getTestData("live-21", mObj.toString())
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(new DefaultObserver<MyZBBean>(MainActivity.this) {
//                            @Override
//                            public void onSuccess(MyZBBean response) {
//                                mTv.setText(response.getBackgroundpic() + "\n" + response.getBegintime());
//                            }
//                        });




            }
        });

    }

    /**
     * 统一处理返回结果
     * @param request
     * @param type
     */
    @Override
    protected void requestData(Observable request, int type) {
        Observable observable = RequestUtil.getObservable(request);
        switch (type){
            case 1:
                observable.subscribe(new DefaultObserver<MyZBBean>(this) {
                    @Override
                    public void onSuccess(MyZBBean response) {
                        mTv.setText(response.getBackgroundpic() + "\n" + response.getBegintime());
                    }
                });
                break;
        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
