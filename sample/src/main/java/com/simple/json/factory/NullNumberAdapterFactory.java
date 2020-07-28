package com.simple.json.factory;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.simple.json.adapter.NullDoubleTypeAdapter;
import com.simple.json.adapter.NullFloatTypeAdapter;
import com.simple.json.adapter.NullIntegerTypeAdapter;
import com.simple.json.adapter.NullLongTypeAdapter;


public class NullNumberAdapterFactory implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<? super T> rawType =  type.getRawType();
        if (rawType == Float.class || rawType == float.class) {
            return (TypeAdapter<T>) new NullFloatTypeAdapter();
        } else if (rawType == Integer.class || rawType == int.class){
            return (TypeAdapter<T>) new NullIntegerTypeAdapter();
        } else if (rawType == Double.class || rawType == double.class){
            return (TypeAdapter<T>) new NullDoubleTypeAdapter();
        } else if (rawType == Long.class || rawType == long.class){
            return (TypeAdapter<T>) new NullLongTypeAdapter();
        }
        return null;
    }

}