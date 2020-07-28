package com.woodys.http.core.factory;

import com.woodys.http.utils.Preconditions;

import okhttp3.Call;
import okhttp3.Request;

/**
 * 代理{@link Call.Factory} 拦截{@link #newCall(Request)}方法
 * @author Created by woodys on 2020/6/09.
 * @email yuetao.315@qq.com
 */
public abstract class CallFactoryProxy implements Call.Factory {

    protected final Call.Factory delegate;

    public CallFactoryProxy(Call.Factory delegate) {
        Preconditions.checkNotNull(delegate, "delegate==null");
        this.delegate = delegate;
    }
}
