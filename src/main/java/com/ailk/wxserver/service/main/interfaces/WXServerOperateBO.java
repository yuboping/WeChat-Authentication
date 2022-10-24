package com.ailk.wxserver.service.main.interfaces;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface WXServerOperateBO {

	/**
	 * 
	 * @Title: doWXOperation  
	 * @Description: TODO(微信认证上网第三方接入操作)  
	 * @param @param request
	 * @param @param response
	 * @param @throws ServletException
	 * @param @throws IOException    参数  
	 * @return void    返回类型  
	 * @throws
	 */
	public void doWXOperation(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException;
}
