package com.ailk.wxserver.service.handler.interfaces;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface WXVerifyHandlerBO {

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
	public void response(HttpServletResponse response, String info,
			String redirectUrl) throws IOException;
	
	/**
	 * 微信验证
	 * @param paramMap
	 * @return
	 */
	public Map<String, String> doVerifyReq(Map<String, String> paramMap);
}
