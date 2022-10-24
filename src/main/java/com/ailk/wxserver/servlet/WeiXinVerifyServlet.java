package com.ailk.wxserver.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ailk.wxserver.service.main.interfaces.WXVerifyOperateBO;
import com.ailk.wxserver.util.spring.BeanUtil;


/**
 * 微信手机号验证
 * @author zhoutj
 *
 */
@SuppressWarnings("serial")
public class WeiXinVerifyServlet extends HttpServlet{
	
	private WXVerifyOperateBO wxVerifyOperateBO;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		wxVerifyOperateBO.doWXVerify(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doGet(request, response);
	}

	@Override
	public void init() {
		this.wxVerifyOperateBO = (WXVerifyOperateBO) BeanUtil.getBean("wxVerifyOperateBO");
	}
}
