package com.woodys.http.core.callback;

import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * 动态baseurl替换回调
 * @author Created by woodys on 2020/6/08.
 * @email yuetao.315@qq.com
 */
public interface DynamicBaseUrlCallback {
    /**
     * 通过@Headers或者@Header静态或者动态的设置的baseUrlName来进行动态替换
     * @param baseUrlName
     * @param request
     * @return
     */
    HttpUrl transform(String baseUrlName, Request request);
}
