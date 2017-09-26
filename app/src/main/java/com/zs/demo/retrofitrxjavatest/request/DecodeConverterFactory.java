package com.zs.demo.retrofitrxjavatest.request;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;
import com.zs.demo.retrofitrxjavatest.bean.RequestBean;
import com.zs.demo.retrofitrxjavatest.util.NewAES;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by zs
 * Date：2017年 09月 25日
 * Time：15:40
 * —————————————————————————————————————
 * About: 自定义转换器 对返回的数据进行解密
 * —————————————————————————————————————
 */

public class DecodeConverterFactory extends Converter.Factory {

    private final Gson gson;

    public static DecodeConverterFactory create() {
        return create(new Gson());
    }

    public static DecodeConverterFactory create(Gson gson) {
        return new DecodeConverterFactory(gson);
    }

    private DecodeConverterFactory(Gson gson) {
        if (gson == null) throw new NullPointerException("gson == null");
        this.gson = gson;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new DecodeResponseBodyConverter<>(adapter);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new DecodeRequestBodyConverter<>(gson, adapter);
    }

    public class DecodeResponseBodyConverter<T> implements Converter<ResponseBody, T> {
        private final TypeAdapter<T> adapter;

        DecodeResponseBodyConverter(TypeAdapter<T> adapter) {
            this.adapter = adapter;
        }

        @Override
        public T convert(ResponseBody value) throws IOException {
            //解密字符串
            return adapter.fromJson(NewAES.decrypt(value.string(), RequestBaseParams.IMEncodingAESKey));
        }
    }

    public class DecodeRequestBodyConverter<T> implements Converter<T, RequestBody> {
        private  final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");
        private  final Charset UTF_8 = Charset.forName("UTF-8");

        private final Gson gson;
        private final TypeAdapter<T> adapter;
        DecodeRequestBodyConverter(Gson gson,TypeAdapter<T> adapter){
            this.gson = gson;
            this.adapter = adapter;
        }
        @Override
        public RequestBody convert(T value) throws IOException {
            Buffer buffer = new Buffer();
            Writer writer = new OutputStreamWriter(buffer.outputStream(),UTF_8);
            JsonWriter jsonWriter = gson.newJsonWriter(writer);
            adapter.write(jsonWriter,value);
            jsonWriter.flush();
            return RequestBody.create(MEDIA_TYPE,buffer.readByteString());
        }
    }
}


