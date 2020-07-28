package com.simple.net.impls;

import com.woodys.http.core.callback.DynamicBaseUrlCallback;

import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * 动态设置BaseUrl，设置配置规则
 * @author Created by woodys on 2020/6/16.
 * @email yuetao.315@qq.com
 */
public class SimpleDynamicBaseUrlCallback implements DynamicBaseUrlCallback {
    @Override
    public HttpUrl transform(String baseUrlName, Request request) {
        if (baseUrlName.equals("baidu")) {
            String oldUrl = request.url().toString();
            String newUrl = oldUrl.replace("https://wanandroid.com/", "https://www.baidu.com/");
            return HttpUrl.get(newUrl);
        }
        return null;
    }
}
