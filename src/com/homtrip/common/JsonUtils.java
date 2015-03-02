package com.homtrip.common;


import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

/**
 * 
 */

public class JsonUtils {
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").disableHtmlEscaping().create();

    /**
     * @param json
     * @param clazz
     * @return
     * @throws
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return (T)gson.fromJson(json, clazz);
    }

    /**
     * @param obj
     * @return
     * @throws
     */
    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    /**
     * @param seccess
     * @param obj
     * @return
     */
    public static String objToJson(boolean seccess, Object... obj) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("success", seccess);
        if (null != obj && obj.length > 0) {
            if (obj.length > 1) {
                m.put("data", obj);
            } else {
                m.put("data", obj[0]);
            }
        }
        return gson.toJson(m);
    }

    /**
     * @param json
     * @param t
     * @return
     */
    @SuppressWarnings("unchecked")
	public static <T> T fromJson(String json, Type t) {
        return (T)gson.fromJson(json, t);
    }

    /**
     * @param json
     * @return
     */
    public static JsonElement fromJsonAsJsonElement(String json) {
    	return gson.fromJson(json, JsonElement.class);
    }
}
