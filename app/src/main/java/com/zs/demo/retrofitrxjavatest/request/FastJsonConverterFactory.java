package com.zs.demo.retrofitrxjavatest.request;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.zs.demo.retrofitrxjavatest.util.NewAES;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by zs
 * Date：2017年 12月 08日
 * Time：14:18
 * —————————————————————————————————————
 * About:
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
            return JSON.parseObject(NewAES.decrypt(value.string(), RequestBaseParams.IMEncodingAESKey), mType);
            // 如果没有加密处理
//            return JSON.parseObject(value.string(), mType);
        }

    }

}
