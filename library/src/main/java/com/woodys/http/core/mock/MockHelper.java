package com.woodys.http.core.mock;

import android.content.Context;
import android.text.TextUtils;

import com.woodys.http.utils.AssetsUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import okhttp3.Request;
import retrofit2.Invocation;

/**
 * mock 相关工具工具
 * @author Created by woodys on 2020/6/08.
 * @email yuetao.315@qq.com
 */
public class MockHelper {

    public static Method getRetrofitMethod(Request request){
        Invocation invocation = request.tag(Invocation.class);
        return invocation!=null ? invocation.method():null;
    }

    public static Mock getMock(Method method) {
        Annotation[] methodAnnotations = method.getAnnotations();
        for (Annotation annotation:methodAnnotations) {
            if (annotation instanceof Mock) {
                return (Mock) annotation;
            }
        }
        return null;
    }

    public static String getMockData(Context context,Mock mock){
        String value = mock.value();
        if (!TextUtils.isEmpty(value)) {
            return value;
        }
        String assetsUrl = mock.assets();

        if (!TextUtils.isEmpty(assetsUrl)) {
            return AssetsUtils.readAssets(context, assetsUrl);
        }
        return null;
    }


    public static String getMockUrl(Mock mock){
        return mock.url();
    }

    public static boolean getMockEnable(Mock mock){
        return mock.enable();
    }
}
