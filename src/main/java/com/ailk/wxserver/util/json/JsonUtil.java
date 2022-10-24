package com.ailk.wxserver.util.json;

import java.util.Map;

import net.sf.json.JSONObject;

public class JsonUtil {

    /**
     * map转为json字符串
     * 
     * @param map
     * @return
     */
    public static String map2json(Map<String, String> map) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.putAll(map);
        return jsonObject.toString();
    }

    /**
     * 
     * @Title: json2map
     * @Description: TODO(json字符串转为map)
     * @param @param jsonStr
     * @param @return 参数
     * @return Map<String,Object> 返回类型
     * @throws
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> json2map(String jsonStr) {
        JSONObject jsonObject = JSONObject.fromObject(jsonStr);
        return jsonObject;
    }
}
