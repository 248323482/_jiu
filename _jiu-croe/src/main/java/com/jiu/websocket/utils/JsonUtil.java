package com.jiu.websocket.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

/**
 */
public class JsonUtil {
    /**
     * 判断是否是JsonObject
     */
    public static boolean isJsonObject(String message) {
        return null != message && message.startsWith("{") && message.endsWith("}");
    }
    /**
     * 判断是否是JsonArray
     */
    public static boolean isJsonArray(String message) {
        return null != message && message.startsWith("[") && message.endsWith("]");
    }
    /**
     * 将JavaBean序列化为字符串
     */
    public static String serializeJavaBean(Object o){
        return JSON.toJSONString(o);
    }

    /**
     * 将Map序列化为字符串
     */
    public static String serializeMap(Map<String , Object> map){
        return new JSONObject(map).toString();
    }
    /**
     * 将Map序列化为字符串
     */
    public static String serializeList(List<Object> list){
        return JSON.toJSONString(list);
    }
}
