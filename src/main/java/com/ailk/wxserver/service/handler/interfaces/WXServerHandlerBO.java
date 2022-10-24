package com.ailk.wxserver.service.handler.interfaces;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ailk.wxserver.util.ResponseResult;

public interface WXServerHandlerBO {

	/**
	 * 获取请求参数
	 * 
	 * @param request
	 * @return
	 */
	public Map<String, String> getRequestParam(HttpServletRequest request)
			throws IOException;

	/**
	 * 回应结果
	 * 
	 * @param response
	 * @param info
	 * @param redirectUrl
	 * @throws IOException
	 */
	public void response(HttpServletResponse response, String reply_msg)
			throws IOException;

	/**
	 * 处理微信认证上网第三方接入请求
	 * 
	 * @param paramMap
	 * @return
	 */
	public ResponseResult doWXServerReq(Map<String, String> paramMap);

}
