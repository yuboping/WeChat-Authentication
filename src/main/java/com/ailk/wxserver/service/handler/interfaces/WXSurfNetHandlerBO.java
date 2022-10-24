package com.ailk.wxserver.service.handler.interfaces;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ailk.wxserver.util.ResponseResult;

public interface WXSurfNetHandlerBO {
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
    public void response(HttpServletResponse response, String desc, String redirectUrl)
            throws IOException;

    /**
     * 处理一键上网请求
     * 
     * @param paramMap
     * @return
     */
    public ResponseResult doSurfNetReq(Map<String, String> paramMap);
}
