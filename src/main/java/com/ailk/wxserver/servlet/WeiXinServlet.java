package com.ailk.wxserver.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ailk.wxserver.service.main.interfaces.WXOperateBO;
import com.ailk.wxserver.util.spring.BeanUtil;

/**
 * 微信平台消息处理
 * @author zhoutj
 *
 */
@SuppressWarnings("serial")
public class WeiXinServlet extends HttpServlet{
	
	private WXOperateBO wxOperateBO;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		wxOperateBO.doSignatureCheck(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		wxOperateBO.doWeixinOperation(request, response);
	}

	@Override
	public void init() {
		this.wxOperateBO = (WXOperateBO) BeanUtil.getBean("wxOperateBO");
	}
}
