package com.zs.demo.retrofitrxjavatest.request;

import com.zs.demo.retrofitrxjavatest.bean.BaseResponse;
import com.zs.demo.retrofitrxjavatest.bean.MyZBBean;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by zs
 * Date：2017年 09月 22日
 * Time：10:34
 * —————————————————————————————————————
 * About: 接口
 * —————————————————————————————————————
 */

public interface RequestService {

    @GET("touch/reconstruct/article/list/BBM54PGAwangning/0-10.html")
    Call<ResponseBody> getWYData();

    @FormUrlEncoded
    @POST("android")
    Call<ResponseBody> getZBData(@Field("version") String version,
                                  @Field("optioncode") String optioncode,
                                  @Field("timestamp") String timestamp,
                                  @Field("nonce") String nonce,
                                  @Field("option") String option,
                                  @Field("signature") String signature);

    @GET("touch/reconstruct/article/list/BBM54PGAwangning/0-10.html")
    Observable<BaseResponse> getRJData();

    @FormUrlEncoded
    @POST("android")
    Observable<MyZBBean> getTestData(@Field("version") String version,
                                     @Field("optioncode") String optioncode,
                                     @Field("timestamp") String timestamp,
                                     @Field("nonce") String nonce,
                                     @Field("option") String option,
                                     @Field("signature") String signature);

    @FormUrlEncoded
    @POST("android")
    Observable<MyZBBean> getTestData(@Field("optioncode") String optioncode,
                                     @Field("option") String option);

    @FormUrlEncoded
    @POST("android")
    Observable<BaseResponse> getRequestData(@Field("optioncode") String optioncode,
                                     @Field("option") String option);

}
