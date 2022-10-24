package com.ailk.wxserver.service.main.interfaces;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author dengliugao
 *
 */
public interface WXOperateBO {

	/**
	 * 微信合法校验、关注、取消
	 * 
	 * @param request
	 * @param response
	 */
	public void doWeixinOperation(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException;
	
	
	/**
	 * 验证URL真实性
	 * 
	 * @param request
	 * @param response
	 */
	public void doSignatureCheck(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException;
	
}
