package com.simple.net.impls;

import android.util.Log;

import com.woodys.http.log.LogDelegate;

/**
 * 网络库日志输出源控制
 * @author Created by woodys on 2020/6/16.
 * @email yuetao.315@qq.com
 */
public class SimpleLogDelegate implements LogDelegate {
    @Override
    public void v(String tag, String msg) {
        Log.v(tag, msg);
    }

    @Override
    public void d(String tag, String msg) {
        Log.d(tag, msg);
    }

    @Override
    public void i(String tag, String msg) {
        Log.i(tag, msg);
    }

    @Override
    public void w(String tag, String msg) {
        Log.w(tag, msg);
    }

    @Override
    public void e(String tag, String msg, Throwable tr) {
        Log.e(tag, msg, tr);
    }

}
