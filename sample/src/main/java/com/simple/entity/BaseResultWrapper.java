package com.simple.entity;

/**
 * 通用响应体格式
 * {
 * 	"code":0,
 * 	"message":"请求成功",
 * 	"data":{
 * 		"id":123456,
 * 		"name":"张三",
 * 		"age":18
 *   }
 * }
 * @author Created by woodys on 2020-03-03.
 * @email yuetao.315@qq.com
 */
public class BaseResultWrapper<T> extends BaseResult {
    private T data;

    public void setData(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }
}
