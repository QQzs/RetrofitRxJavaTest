package com.zs.demo.retrofitrxjavatest;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zs.demo.retrofitrxjavatest.bean.RequestBean;
import com.zs.demo.retrofitrxjavatest.bean.MyZBBean;
import com.zs.demo.retrofitrxjavatest.request.DefaultObserver;
import com.zs.demo.retrofitrxjavatest.request.RequestApi;
import com.zs.demo.retrofitrxjavatest.request.RequestBaseParams;
import com.zs.demo.retrofitrxjavatest.util.NewAES;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {


    private Button mBtnGet;
    private Button mBtnPost;
    private Button mBtnRxjavaPost;
    private Button mBtnUtilPost;
    private TextView mTv;

    private JSONObject mObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mBtnGet = (Button) findViewById(R.id.btn_get_main);
        mBtnPost = (Button) findViewById(R.id.btn_post_main);
        mBtnRxjavaPost = (Button) findViewById(R.id.btn_rxjava_main);
        mBtnUtilPost = (Button) findViewById(R.id.btn_util_main);
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

                Call<ResponseBody> responseBodyCall = RequestApi.getInstance().getBaseService().getMzDatas();
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
                Call<ResponseBody> responseBodyCall = RequestApi.getInstance().getRequestService(RequestApi.REQUEST_URL1).getZBDatas(bean.getVersion(),
                        bean.getOptioncode(),bean.getTimestamp(),bean.getNonce(),bean.getOption(),bean.getSignature()
                );
                responseBodyCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String back = response.body().string();
                            mTv.setText(NewAES.decrypt(back, RequestBaseParams.IMEncodingAESKey));
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
        mBtnRxjavaPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RequestBean bean = new RequestBean("live-21",mObj.toString());

                RequestApi.getInstance().getRequestService(RequestApi.REQUEST_URL1).getTestData(
                        bean.getOptioncode(), bean.getOption())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new DefaultObserver<MyZBBean>() {
                            @Override
                            public void onSuccess(MyZBBean response) {
                                mTv.setText(response.getBackgroundpic() + "\n" + response.getBegintime());
                            }
                        });

//                RequestApi.getInstance().getBaseService().getRJData()
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(new DefaultObserver<BaseResponse>() {
//                            @Override
//                            public void onSuccess(BaseResponse response) {
//                                mTv.setText(response.getError());
//                            }
//                        });
            }
        });

//        mBtnUtilPost.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                RequestUtil.request("live-21",mObj.toString()).subscribe(new DefaultObserver<BaseResponse>() {
//                    @Override
//                    public void onSuccess(BaseResponse response) {
//
//                    }
//                });
//            }
//        });


    }

    @Override
    public void onClick(View view) {

    }

}
