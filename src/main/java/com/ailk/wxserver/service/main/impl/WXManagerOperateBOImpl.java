package com.ailk.wxserver.service.main.impl;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ailk.wxserver.service.constant.ParamConstant;
import com.ailk.wxserver.service.constant.WXAuthContant;
import com.ailk.wxserver.service.constant.WXResultConstant;
import com.ailk.wxserver.service.handler.interfaces.WXManagerHandlerBO;
import com.ailk.wxserver.service.main.interfaces.WXManagerOperateBO;
import com.ailk.wxserver.util.LogUtil;
import com.ailk.wxserver.util.StringUtils;
import com.ailk.wxserver.util.log.LogFactory;
import com.ailk.wxserver.util.log.LogObj;

public class WXManagerOperateBOImpl implements WXManagerOperateBO {

    private static Logger log = LogFactory.getLogger("wxoperate");
    private static Logger error = LogFactory.getLogger("error");

    private WXManagerHandlerBO wxManagerHandlerBO = null;

    @Override
    public void doManagerOperation(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 将请求、响应的编码均设置为UTF-8（防止中文乱码）
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        LogObj logObj = new LogObj();
        try {
            Map<String, String> paramMap = wxManagerHandlerBO.getRequestParam(request);
            String sessionid = paramMap.get(ParamConstant.KEY_SESSIONID);
            if (StringUtils.isEmpty(sessionid)) {
                int logid = (int) (Math.random() * Integer.MAX_VALUE);
                sessionid = String.valueOf(logid);
                paramMap.put(ParamConstant.KEY_SESSIONID, sessionid);
            }
            // 读取参数
            logObj.putSysKey(LogObj.COLLECT, "y").putSysKey(LogObj.SID, sessionid)
                    .putSysKey(LogObj.MODULE, "wxsurfnetoperation")
                    .putSysKey(LogObj.STEP, "request");
            logObj.putData("submitType", "post")
                    .putData(WXAuthContant.KEY_REGISTER_ID,
                            paramMap.get(WXAuthContant.KEY_REGISTER_ID))
                    .putData(ParamConstant.KEY_WX_OPENID, paramMap.get(ParamConstant.KEY_WX_OPENID))
                    .putData(WXAuthContant.WX_MANAGER_TYPE,
                            paramMap.get(WXAuthContant.WX_MANAGER_TYPE))
                    .putData(WXAuthContant.KEY_MESSAGE, paramMap.get(WXAuthContant.KEY_MESSAGE));
            log.info(logObj);
            String reply_msg = "";
            Map<String, String> result = wxManagerHandlerBO.doManagerReq(paramMap);
            String resultcode = result.get(ParamConstant.KEY_RESULTCODE);
            logObj.putSysKey(LogObj.STEP, "response");
            logObj.putData("result", result);

            if (String.valueOf(WXResultConstant.RESULT_SUCCESS).equals(resultcode)) {
                // 校验成功
                log.info(logObj);
            } else {
                // 校验失败
                log.error(logObj);
            }

            reply_msg = result.get(ParamConstant.KEY_INFO);
            wxManagerHandlerBO.response(response, reply_msg);
        } catch (IOException e) {
            LogUtil.printErrorStackTrace(error, e);
        }
    }

    public WXManagerHandlerBO getWxManagerHandlerBO() {
        return wxManagerHandlerBO;
    }

    public void setWxManagerHandlerBO(WXManagerHandlerBO wxManagerHandlerBO) {
        this.wxManagerHandlerBO = wxManagerHandlerBO;
    }
}
