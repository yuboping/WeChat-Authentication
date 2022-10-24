package com.ailk.wxserver.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ailk.wxserver.service.main.interfaces.WXManagerOperateBO;
import com.ailk.wxserver.util.spring.BeanUtil;

/**
 * 
 * @ClassName: WeiXinManagerServlet
 * @Description: TODO(微信营销接口)
 * @author yubp@asiainfo-sec.com
 * @date 2017年8月23日 上午10:09:55
 *
 */
@SuppressWarnings("serial")
public class WeiXinManagerServlet extends HttpServlet {
    private WXManagerOperateBO wxManagerOperateBO;

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        wxManagerOperateBO.doManagerOperation(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        this.doGet(request, response);
    }

    @Override
    public void init() {
        this.wxManagerOperateBO = (WXManagerOperateBO) BeanUtil.getBean("wxManagerOperateBO");
    }
}
