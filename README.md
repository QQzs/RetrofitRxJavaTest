# RetrofitRxJavaTest
Retrofit2 + RxJava2 + Okhttp请求框架应该是现在最流行的请求框架了，这篇文章主要讲一下，如何对这套框架进行自定义修改的，
从而符合自己项目的请求框架包括，自定义解析器，自定义拦截器，取消订阅者等等相关的内容，更加方便的使用这套框架。
<br>
## 自定义解析器
```Java
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
```
默认的解析器是GsonConverterFactory，而有些时候，它并不是很方便，我主要是写了两个自定义的解析转换器，DecodeConverterFactory是带解密的，
因为，有的项目，服务器返回的数据是带加密的，需要解密后才能进行Gson解析，FastJsonConverterFactory是用FastJson进行解析的转换器，具体可以
到代码里看看，简单的几步操作。
```Java
/**
 * Created by zs
 * Date：2017年 12月 08日
 * Time：14:18
 * —————————————————————————————————————
 * About: 自定义转换器 FastJson
 * —————————————————————————————————————
 */
public class FastJsonConverterFactory extends Converter.Factory {

    private final SerializeConfig mSerializeConfig;

    private FastJsonConverterFactory(SerializeConfig serializeConfig) {
        this.mSerializeConfig = serializeConfig;
    }

    public static FastJsonConverterFactory create() {
        return create(SerializeConfig.getGlobalInstance());
    }

    public static FastJsonConverterFactory create(SerializeConfig serializeConfig) {
        return new FastJsonConverterFactory(serializeConfig);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return new FastJsonRequestBodyConverter<>(mSerializeConfig);
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return new FastJsonResponseBodyConvert<>(type);
    }

    final class FastJsonRequestBodyConverter<T> implements Converter<T, RequestBody> {

        private final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");

        private SerializeConfig mSerializeConfig;

        public FastJsonRequestBodyConverter(SerializeConfig serializeConfig) {
            this.mSerializeConfig = serializeConfig;
        }

        @Override
        public RequestBody convert(T value) throws IOException {
            return RequestBody.create(MEDIA_TYPE, JSON.toJSONBytes(value, mSerializeConfig));
        }
    }

    final class FastJsonResponseBodyConvert<T> implements Converter<ResponseBody, T> {

        private Type mType;

        public FastJsonResponseBodyConvert(Type type) {
            this.mType = type;
        }

        @Override
        public T convert(ResponseBody value) throws IOException {
            // 解密字符串
            return JSON.parseObject(NewAES.decrypt(value.string(), SignatureParams.IMEncodingAESKey), mType);
            // 如果没有加密处理
//            return JSON.parseObject(value.string(), mType);
        }

    }

}
```
## 自定义拦截器
```Java
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
```
拦截器的作用是，正规的后台接口请求时，都有一些字段的默认必传的，比例 时间戳、版本号等等，当我们请求的时候，每次请求都写一遍，这是很不
合理的，所以，在拦截器里面处理一下就方便了很多。
```Java
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
```
在请求发出之前，拦截一下请求，把公共的参数拼接进去，然后生成重新请求发送出去。

## 取消订阅者
为了使请求更合理，当页面关闭时，当前页面的请求最好全部取消，防止出现内存泄漏，和一些不必要的操作。
```Java

/**
 * Created by zhpan on 2017/4/22.
 * 添加订阅者
 */

public interface BaseImpl {

    boolean addRxStop(Disposable disposable);

    boolean addRxDestroy(Disposable disposable);

    void remove(Disposable disposable);
}

/**
 * @author Administrator
 */
public class BaseRxActivity extends AppCompatActivity implements BaseImpl {

    /**
     * 管理Stop取消订阅者
     */
    private CompositeDisposable disposables2Stop;
    /**
     * 管理Destroy取消订阅者
     */
    private CompositeDisposable disposables2Destroy;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (disposables2Destroy != null) {
            throw new IllegalStateException("onCreate called multiple times");
        }
        disposables2Destroy = new CompositeDisposable();

    }

    @Override
    public boolean addRxStop(Disposable disposable) {
        if (disposables2Stop == null) {
            throw new IllegalStateException(
                    "addUtilStop should be called between onStart and onStop");
        }
        disposables2Stop.add(disposable);
        return true;
    }

    @Override
    public boolean addRxDestroy(Disposable disposable) {
        if (disposables2Destroy == null) {
            throw new IllegalStateException(
                    "addUtilDestroy should be called between onCreate and onDestroy");
        }
        disposables2Destroy.add(disposable);
        return true;
    }

    @Override
    public void remove(Disposable disposable) {
        if (disposables2Stop == null && disposables2Destroy == null) {
            throw new IllegalStateException("remove should not be called after onDestroy");
        }
        if (disposables2Stop != null) {
            disposables2Stop.remove(disposable);
        }
        if (disposables2Destroy != null) {
            disposables2Destroy.remove(disposable);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (disposables2Stop != null) {
            throw new IllegalStateException("onStart called multiple times");
        }
        disposables2Stop = new CompositeDisposable();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (disposables2Stop == null) {
            throw new IllegalStateException("onStop called multiple times or onStart not called");
        }
        disposables2Stop.dispose();
        disposables2Stop = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposables2Destroy == null) {
            throw new IllegalStateException(
                    "onDestroy called multiple times or onCreate not called");
        }
        disposables2Destroy.dispose();
        disposables2Destroy = null;
    }

}
```
## 请求操作
```Java
mRequestApi.getRequestService(RequestApi.REQUEST_URL1)
                        .getTestData("live-21", mObj.toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new DefaultObserver<MyZBBean>(MainActivity.this) {
                            @Override
                            public void onSuccess(MyZBBean response) {
                                mTv.setText(response.getBackgroundpic() + "\n" + response.getBegintime());
                            }
                        });
```
正常的请求是这样的，但是每次发起请求都如此操作，一个复杂的页面，各种接口，有些代码就很冗余，比如中间那两句，此时，可以单独写个类把
冗余的代码提出去，Retrofit + RXJava的其他代码还是必须的。

```Java
public class RequestUtil {

    public static Observable getObservable(Observable request){
        return request.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

}


mBtnRxjavaPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                requestData(mRequestApi.getRequestService(RequestApi.REQUEST_URL1)
                        .getTestData("live-21", mObj.toString()),1);

            }
        });


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

```
当然这只是个人代码风格，望采纳，还有很多需要修改的地方。







