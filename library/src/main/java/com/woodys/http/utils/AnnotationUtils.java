package com.woodys.http.utils;

import android.support.annotation.Nullable;

import java.lang.annotation.Annotation;

/**
 * 创建时间：2018/4/8
 * 编写人： yeutao
 * 功能描述：retrofit2工具类
 */
public class AnnotationUtils {

    @Nullable
    public static <T extends Annotation> T findAnnotation(Annotation[] annotations, Class<T> cls) {
        //just in case
        if (annotations == null) {
            return null;
        }
        for (Annotation annotation : annotations) {
            if (cls.isInstance(annotation)) {
                //noinspection unchecked
                return (T) annotation;
            }
        }
        return null;
    }

    /**
     * Returns true if {@code annotations} contains an instance of {@code cls}.
     */
    public static boolean isAnnotationPresent(Annotation[] annotations,
                                              Class<? extends Annotation> cls) {
        for (Annotation annotation : annotations) {
            if (cls.isInstance(annotation)) {
                return true;
            }
        }
        return false;
    }
}
