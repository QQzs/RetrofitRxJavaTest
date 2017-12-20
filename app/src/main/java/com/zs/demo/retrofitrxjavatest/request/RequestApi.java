package com.zs.demo.retrofitrxjavatest.request;

import android.text.TextUtils;

import com.zs.demo.retrofitrxjavatest.request.factory.FastJsonConverterFactory;
import com.zs.demo.retrofitrxjavatest.request.signature.SignatureParams;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Created by zs
 * Date：2017年 09月 22日
 * Time：10:36
 * —————————————————————————————————————
 * About:
 * —————————————————————————————————————
 */
public class RequestApi {

    private final static int CONNECT_TIMEOUT = 30;
    private final static int READ_TIMEOUT = 30;

    public static final String BASE_URL = "http://gank.io/api/data/";
    public static final String BASE_NEW_URL = "https://s.ibaodian.com/app/group/live/";

    public static final int REQUEST_URL1 = 1001;
    public static final int REQUEST_URL2 = 1002;
    public static final int REQUEST_URL3 = 1003;


    private static RequestApi mRetrofitApi;
    private OkHttpClient mOkHttpClient;

    public RequestApi(){

    }

    public static RequestApi getInstance() {
        if (mRetrofitApi == null) {
            mRetrofitApi = new RequestApi();
        }
        return mRetrofitApi;
    }

    /**
     * retrofit
     * @param baseurl
     * @return
     */
    public Retrofit getRetrofit(String baseurl){

        if (TextUtils.isEmpty(baseurl)) {
            baseurl = BASE_URL;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseurl)
                .client(getOkHttpClient())
//                .addConverterFactory(GsonConverterFactory.create())   // 默认转换器
//                .addConverterFactory(DecodeConverterFactory.create()) // 自定义转换器
                .addConverterFactory(FastJsonConverterFactory.create()) // FastJson解析转换器
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit;
    }

    /**
     * 请求不同的基地址
     * @param type
     * @return
     */
    public RequestService getRequestService(int type) {
        String baseUrl;
        switch (type){
            case REQUEST_URL1:
                baseUrl = BASE_NEW_URL;
                break;
            case REQUEST_URL2:
                baseUrl = BASE_NEW_URL;
                break;
            case REQUEST_URL3:
                baseUrl = BASE_NEW_URL;
                break;
            default:
                baseUrl = BASE_URL;
        }
        return getRetrofit(baseUrl).create(RequestService.class);
    }

    /**
     * 请求默认的基地址
     * @return
     */
    public RequestService getBaseService() {
        return getRetrofit(null).create(RequestService.class);
    }

    /**
     * OKHttp
     * @return
     */
    private OkHttpClient getOkHttpClient() {
        if (mOkHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)//连接失败后是否重新连接
                        .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)//超时时间
                    .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                    .addInterceptor(new HttpBaseParamsInterceptor());//拦截器

//            if (mCookieJar == null) {
//                builder.cookieJar(new OkHttpCookieManager(new MemoryCookieStore()));
//            } else {
//                builder.cookieJar(mCookieJar);
//            }
            mOkHttpClient = builder.build();
        }
        return mOkHttpClient;
    }

    /**
     * 自定义拦截器
     */
    class HttpBaseParamsInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {

            //访问网络之前，处理Request
            String time = "" + System.currentTimeMillis();
            Request request = chain.request();
            // 添加公共的参数
            HttpUrl.Builder authorizedUrlBuilder = request.url()
                    .newBuilder()
                    .scheme(request.url().scheme())
                    .host(request.url().host())
                    .addQueryParameter("version", "2.5.0")
                    .addQueryParameter("timestamp", time)
                    .addQueryParameter("nonce", SignatureParams.NONCE)
                    .addQueryParameter("signature", SignatureParams.getSignature(time, SignatureParams.NONCE));

            // 新的请求
            Request newRequest = request.newBuilder()
                    .method(request.method(), request.body())
                    .url(authorizedUrlBuilder.build())
                    .build();

            return chain.proceed(newRequest);
        }
    }

}
