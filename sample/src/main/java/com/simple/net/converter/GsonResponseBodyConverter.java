package com.simple.net.converter;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.stream.JsonReader;
import com.woodys.http.core.converter.BaseGsonConverter;
import com.woodys.http.core.exception.HttpError;
import com.simple.entity.BaseResult;
import com.simple.entity.BaseResultWrapper;
import com.simple.entity.Result;
import com.simple.json.GsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;

/**
 * 创建时间：2018/4/3
 * 编写人： yeutao
 * 功能描述：json解析相关
 */
public final class GsonResponseBodyConverter<T> extends BaseGsonConverter<T> {
    private final Gson gson;

    public GsonResponseBodyConverter(Gson gson, Type type) {
        super(type, $Gson$Types.getRawType(type));
        this.gson = gson;
    }

    @Override
    public T convert(@NonNull ResponseBody value) throws IOException {
        try {
            return convertResponse(value);
        } catch (Exception e) {
            throw new IOException(e);
        } finally {
            value.close();
        }
    }

    /**
     * 该方法是子线程处理，不能做ui相关的工作
     * 主要作用是解析网络返回的 body 对象，生成onSuccess回调中需要的数据对象
     * 这里的解析工作不同的业务逻辑基本都不一样,所以需要自己实现,以下给出的时模板代码,实际使用根据需要修改
     */
    public T convertResponse(ResponseBody body) throws Exception {
       if (type == null) {
            if (clazz == null) {
                // 如果没有通过构造函数传进来，就自动解析父类泛型的真实类型（有局限性，继承后就无法解析到）
                Type genType = getClass().getGenericSuperclass();
                type = ((ParameterizedType) genType).getActualTypeArguments()[0];
            } else {
                return parseClass(body, clazz);
            }
        }

        if (type instanceof ParameterizedType) {
            return parseParameterizedType(body, (ParameterizedType) type);
        } else if (type instanceof Class) {
            return parseClass(body, (Class<?>) type);
        } else {
            return parseType(body, type);
        }
    }

    private T parseClass(ResponseBody body, Class<?> rawType) throws Exception {
        if (rawType == null) {return null;}
        if (body == null) {return null;}
        JsonReader jsonReader = new JsonReader(body.charStream());

        if (rawType == String.class) {
            //noinspection unchecked
            return (T) body.string();
        } else if (rawType == JSONObject.class) {
            //noinspection unchecked
            return (T) new JSONObject(body.string());
        } else if (rawType == JSONArray.class) {
            //noinspection unchecked
            return (T) new JSONArray(body.string());
        } else {
            T t = GsonUtils.fromJson(jsonReader, rawType);
            return t;
        }
    }

    private T parseType(ResponseBody body, Type type) throws Exception {
        if (type == null) {return null;}
        if (body == null) {return null;}
        JsonReader jsonReader = new JsonReader(body.charStream());

        // 泛型格式如下： new JsonCallback<任意JavaBean>(this)
        T t = GsonUtils.fromJson(jsonReader, type);
        return t;
    }

    private T parseParameterizedType(ResponseBody body, ParameterizedType type) throws Exception {
        if (type == null) {return null;}
        if (body == null) {return null;}
        JsonReader jsonReader = new JsonReader(body.charStream());

        // 泛型的实际类型
        Type rawType = type.getRawType();
        // 泛型的参数
        Type typeArgument = type.getActualTypeArguments()[0];
        if (rawType != BaseResultWrapper.class) {
            // 泛型格式如下： new JsonCallback<外层BaseBean<内层JavaBean>>(this)
            T t = GsonUtils.fromJson(jsonReader, type);
            return t;
        } else {
            if (typeArgument == Void.class) {
                // 泛型格式如下： new JsonCallback<BaseResultWrapper<Void>>(this)
                BaseResult simpleResponse = GsonUtils.fromJson(jsonReader, BaseResult.class);
                // 转化下格式
                BaseResultWrapper responseWrapper = new BaseResultWrapper();
                if(null != simpleResponse) {
                    responseWrapper.setCode(simpleResponse.getCode());
                    responseWrapper.setMessage(simpleResponse.getMessage());
                }
                //noinspection unchecked
                return (T) responseWrapper;
            } else {
                // 泛型格式如下： new JsonCallback<BaseResultWrapper<内层JavaBean>>(this)
                BaseResultWrapper baseResultWrapper = GsonUtils.fromJson(jsonReader, type);
                int code = baseResultWrapper.getCode();
                //这里的0是以下意思
                //一般来说服务器会和客户端约定一个数表示成功，其余的表示失败，这里根据实际情况修改
                if (baseResultWrapper.isSuccess()) {
                    //noinspection unchecked
                    return (T) baseResultWrapper;
                } else {
                    //直接将服务端的错误信息抛出，onError中可以获取
                    throw new HttpError("数据异常",new Result(code,baseResultWrapper.getMessage()));
                }
            }
        }
    }
}
