package com.ailk.wxserver.service.handler.interfaces;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ailk.wxserver.util.ResponseResult;

/**
 * 微信接入url校验
 * @author zhoutj
 *
 */
public interface WXSignatureCheckHandlerBO {

	/**
	 * 获取请求参数
	 * @param request
	 * @return
	 */
	public Map<String, String> getRequestParam(HttpServletRequest request);
	
	/**
	 * 微信接入校验
	 * 包括明文校验和加密校验
	 * @param paramMap
	 * @return
	 */
	public ResponseResult signaturecheck(Map<String, String> paramMap);
	
	/**
	 * 回应消息
	 * @param response
	 * @param reply_msg
	 * @throws IOException
	 */
	public void responseMsg(HttpServletResponse response,String reply_msg) throws IOException;
	
	
}
