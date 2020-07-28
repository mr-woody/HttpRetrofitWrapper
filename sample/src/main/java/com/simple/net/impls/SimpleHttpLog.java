package com.simple.net.impls;

import com.orhanobut.logger.Logger;
import com.simple.net.IHttpLog;

/**
 * 配置网络日志输出源
 * @author Created by woodys on 2020/6/16.
 * @email yuetao.315@qq.com
 */
public class SimpleHttpLog implements IHttpLog {
    @Override
    public void e(String msg) {
        Logger.e(msg);
    }

    @Override
    public void e(String tag, String msg) {
        Logger.t(tag).e(msg, msg);
    }

    @Override
    public void d(String tag, String msg) {
        Logger.t(tag).d(msg);
    }
}
