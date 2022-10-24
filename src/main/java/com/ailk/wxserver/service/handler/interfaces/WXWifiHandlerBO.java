package com.ailk.wxserver.service.handler.interfaces;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface WXWifiHandlerBO {

	/**
	 * 获取请求参数
	 * @param request
	 * @return
	 */
	public Map<String, String> getRequestParam(HttpServletRequest request);
	
	/**
	 * 回应结果
	 * @param response
	 * @param info
	 * @param redirectUrl
	 * @throws IOException
	 */
	public void response(HttpServletResponse response, Map<String, String>paramMap,String info,
			String redirectUrl) throws IOException;
	/**
	 * 处理微信wifi请求
	 * @param paramMap
	 * @return
	 */
	public Map<String, String> doWifiReq(Map<String, String> paramMap);
}
