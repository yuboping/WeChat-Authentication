package com.ailk.wxserver.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ailk.wxserver.service.main.interfaces.WXNetAuthOperateBO;
import com.ailk.wxserver.util.spring.BeanUtil;

/**
 * 微信上网认证
 * @author zhoutj
 *
 */
public class WeiXinNetAuthServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6659378783119736038L;
	
	private WXNetAuthOperateBO wxNetAuthOperateBO;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		wxNetAuthOperateBO.doWXNetAuth(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doGet(request, response);
	}

	@Override
	public void init() {
		this.wxNetAuthOperateBO = (WXNetAuthOperateBO) BeanUtil.getBean("wxNetAuthOperateBO");
	}

}
