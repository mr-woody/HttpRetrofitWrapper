package com.woodys.http.core.converter;

import android.support.annotation.Nullable;

import com.woodys.http.utils.Preconditions;

import org.json.JSONObject;

import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * 功能描述：基础json解析相关
 * <p>解析流程如下
 * <li>1、构造JSONObject jsonObject = new JSONObject(value.string());</li>
 * <li>2、获取code msg</li>
 * <li>3、判断code是否为成功码
 * <pre><code>
 *      Tip tip = new Tip(code, msg, cacheStr);
 *      if (code != 0) {
 *          throw new HttpError(msg, tip);
 *      }
 *      if (Tip.class == rawType) {
 *          return (T) tip;
 *      }
 * </code></pre>
 * <li>4、判断data是否为NULL
 * <pre><code>
 *      //这样判断能防止服务端忽略data字段导致jsonObject.get("data")方法奔溃
 *      //且能判断为null或JSONObject#NULL的情况
 *      if (jsonObject.isNull("data")) {
 *          throw new HttpError("数据为空", tip);
 *      }
 * </code></pre>
 * <li>5、判断是否为空JSONObject
 * <pre><code>
 *
 *      Object data = jsonObject.get("data");
 *      if (isEmptyJSON(data)) {
 *         throw new HttpError("暂无数据", tip);
 *      }
 * </code></pre>
 * <li>6、检测是否为基础类型数据
 * <pre><code>
 *      T t = convertBaseType(data, rawType);
 *      if (t != null) {
 *         return t;
 *      }
 * </code></pre>
 * <li>7、解析框架反序列化json
 * <pre><code>
 *      t = gson.fromJson(data.toString(), type);
 *      if (t != null) {
 *         return t;
 *      }
 * </code></pre>
 *
 * @author Created by woodys on 2020/6/09.
 * @email yuetao.315@qq.com
 */
public abstract class BaseGsonConverter<T> implements Converter<ResponseBody, T> {
    protected Type type;
    protected Class<?> clazz;

    protected BaseGsonConverter(Type type, Class<?> clazz) {
        this.type = type;
        this.clazz = clazz;
    }

    /**
     * 是否为空的jsonObject对象 {}
     **/
    protected static boolean isEmptyJSON(@Nullable Object data) {
        return data instanceof JSONObject && ((JSONObject) data).length() == 0;
    }

    /**
     * data 如{"msg": "xxx","code": xxx,"data": xxx}
     * 解析基础装箱类型的参数:
     * String|Boolean|Integer|Long|Short|Double|Float|Byte
     * <p>
     * 子类可重载扩展
     * <p>
     * if (data == null) return null;
     * <p>
     * 重新定义泛型V ，不限制其必须为T，更灵活。
     */
    @Nullable
    @SuppressWarnings("unchecked")
    protected static <V> V convertBaseType(@Nullable Object data, Class<?> baseType) {
        Preconditions.checkNotNull(baseType == null, "baseType==null");
        if (data == null) {
            return null;
        }
        //如果是String 直接返回
        if (String.class == baseType) {
            return (V) String.valueOf(data);
        }
        if (Boolean.class == baseType && data instanceof Boolean) {
            return (V) data;
        }
        //检测是否为装箱类型
        if (!(data instanceof Number)) {
            return null;
        }
        //防止JSON不是引用我们想要的类型
        Number number = (Number) data;
        //赋值时自动装箱
        Number value = null;
        if (Integer.class == baseType) {
            value = number.intValue();
        } else if (Long.class == baseType) {
            value = number.longValue();
        } else if (Short.class == baseType) {
            value = number.shortValue();
        } else if (Double.class == baseType) {
            value = number.doubleValue();
        } else if (Float.class == baseType) {
            value = number.floatValue();
        } else if (Byte.class == baseType) {
            value = number.byteValue();
        }
        return (V) value;
    }
}
