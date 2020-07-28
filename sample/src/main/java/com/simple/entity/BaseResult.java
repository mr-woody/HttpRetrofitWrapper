package com.simple.entity;

/**
 * 普通的结果提示 ，code=0代表成功
 * @author Created by woodys on 2020-03-03.
 * @email yuetao.315@qq.com
 */
public class BaseResult {
    private int code;
    private String message;


    public void setCode(int code) {
        this.code = code;
    }
    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return code == 0;
    }
}
