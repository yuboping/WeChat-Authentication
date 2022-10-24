package com.ailk.wxserver.service.handler.interfaces;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface WXManagerHandlerBO {
    /**
     * 获取请求参数
     * 
     * @param request
     * @return
     */
    public Map<String, String> getRequestParam(HttpServletRequest request) throws IOException;

    /**
     * 回应结果
     * 
     * @param response
     * @param info
     * @param redirectUrl
     * @throws IOException
     */
    public void response(HttpServletResponse response, String reply_msg) throws IOException;

    /**
     * 处理微信营销接口
     * 
     * @param paramMap
     * @return
     */
    public Map<String, String> doManagerReq(Map<String, String> paramMap);
}
