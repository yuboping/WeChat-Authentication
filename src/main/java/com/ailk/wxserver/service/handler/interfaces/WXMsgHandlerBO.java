package com.ailk.wxserver.service.handler.interfaces;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ailk.wxserver.util.ResponseResult;

/**
 * 微信事件和消息体处理
 * @author zhoutj
 *
 */
public interface WXMsgHandlerBO {

	/**
	 * 获取请求参数
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public Map<String, String> getRequestParam(HttpServletRequest request) throws IOException;
	
	
	/**
	 * 回应消息
	 * @param response
	 * @param reply_msg
	 * @throws IOException
	 */
	public void responseMsg(HttpServletResponse response,String reply_msg) throws IOException;
	
	
	/**
	 * 处理消息体
	 * @param paramMap
	 * @return
	 */
	public ResponseResult processMsg(Map<String, String> paramMap);
}
