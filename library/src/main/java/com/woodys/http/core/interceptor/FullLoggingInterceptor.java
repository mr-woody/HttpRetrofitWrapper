package com.woodys.http.core.interceptor;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.woodys.http.log.Debugger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import okhttp3.logging.HttpLoggingInterceptor.Logger;

/**
 * 打印完整的日志，防止多线程情况下导致的日志分散错乱的问题
 * @author Created by woodys on 2020/6/09.
 * @email yuetao.315@qq.com
 */
public final class FullLoggingInterceptor implements Interceptor {
    private static final int JSON_INDENT = 2;
    private static final String LOG_LEVEL = "LogLevel";
    private volatile Level level = Level.NONE;

    public FullLoggingInterceptor(Level level) {
        this.level = level;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if(!Debugger.isEnableLog()){ return chain.proceed(chain.request()); }
        final StringBuilder builder = new StringBuilder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new Logger() {
            @Override
            public void log(String message) {
                append(builder, message);
            }
        });
        //可以单独为某个请求设置日志的级别，避免全局设置的局限性
        httpLoggingInterceptor.setLevel(findLevel(chain.request()));
        Response response = httpLoggingInterceptor.intercept(chain);
        Debugger.d(null,builder.toString());
        return response;
    }

    @NonNull
    private Level findLevel(Request request) {
        //可以单独为某个请求设置日志的级别，避免全局设置的局限性
        String logLevel = request.header(LOG_LEVEL);
        if (logLevel != null) {
            if (logLevel.equalsIgnoreCase("NONE")) {
                return Level.NONE;
            } else if (logLevel.equalsIgnoreCase("BASIC")) {
                return Level.BASIC;
            } else if (logLevel.equalsIgnoreCase("HEADERS")) {
                return Level.HEADERS;
            } else if (logLevel.equalsIgnoreCase("BODY")) {
                return Level.BODY;
            }
        }
        return level;
    }

    private static void append(StringBuilder builder, String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        try {
            // 以{}或者[]形式的说明是响应结果的json数据，需要进行格式化
            if (message.startsWith("{") && message.endsWith("}")) {
                JSONObject jsonObject = new JSONObject(message);
                message = jsonObject.toString(JSON_INDENT);
            } else if (message.startsWith("[") && message.endsWith("]")) {
                JSONArray jsonArray = new JSONArray(message);
                message = jsonArray.toString(JSON_INDENT);
            }
        } catch (JSONException ignored) {
        }
        builder.append(message).append('\n');
    }
}
