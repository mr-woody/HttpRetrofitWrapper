package com.woodys.http.core.mock;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mock 的注解类
 * @author Created by woodys on 2020/6/08.
 * @email yuetao.315@qq.com
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Mock {
    String value() default "";
    String url() default "";
    String assets() default "";
    boolean enable() default true;
}