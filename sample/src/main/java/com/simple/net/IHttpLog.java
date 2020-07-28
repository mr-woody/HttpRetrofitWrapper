package com.simple.net;

/**
 * 网络日志回调接口
 * @author Created by woodys on 2020-03-03.
 * @email yuetao.315@qq.com
 */
public interface IHttpLog {
    void e(String msg);

    void e(String tag, String msg);

    void d(String tag, String msg);
}
