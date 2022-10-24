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
import com.ailk.wxserver.service.handler.interfaces.WXSurfNetHandlerBO;
import com.ailk.wxserver.service.main.interfaces.WXSurfNetOperateBO;
import com.ailk.wxserver.util.LogUtil;
import com.ailk.wxserver.util.ResponseResult;
import com.ailk.wxserver.util.StringUtils;
import com.ailk.wxserver.util.log.LogFactory;
import com.ailk.wxserver.util.log.LogObj;

public class WXSurfNetOperateBOImpl implements WXSurfNetOperateBO {

    private static Logger log = LogFactory.getLogger("wxoperate");
    private static Logger error = LogFactory.getLogger("error");

    private WXSurfNetHandlerBO wxSurfNetHandlerBO = null;

    @Override
    public void doSurfNetOperation(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 将请求、响应的编码均设置为UTF-8（防止中文乱码）
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        LogObj logObj = new LogObj();
        try {
            Map<String, String> paramMap = wxSurfNetHandlerBO.getRequestParam(request);
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
                    .putData(WXAuthContant.WX_SURFNET_CODE,
                            paramMap.get(WXAuthContant.WX_SURFNET_CODE))
                    .putData(WXAuthContant.WX_SURFNET_STATE,
                            paramMap.get(WXAuthContant.WX_SURFNET_STATE))
                    .putData(WXAuthContant.KEY_REGISTER_ID,
                            paramMap.get(WXAuthContant.KEY_REGISTER_ID))
                    .putData(WXAuthContant.KEY_MESSAGE, paramMap.get(WXAuthContant.KEY_MESSAGE));
            log.info(logObj);
            String redirectUrl = "";
            String desc = "";
            ResponseResult result = wxSurfNetHandlerBO.doSurfNetReq(paramMap);
            redirectUrl = result.getInfo();
            desc = result.getDescription();
            logObj.putSysKey(LogObj.STEP, "response");
            logObj.putData("result", result.getResult()).putData("desc", result.getDescription())
                    .putData("replymsg", result.getInfo());
            if (String.valueOf(WXResultConstant.RESULT_SUCCESS).equals(result.getResult())) {
                log.info(logObj);
            } else {
                log.error(logObj);
            }
            wxSurfNetHandlerBO.response(response, desc, redirectUrl);
        } catch (IOException e) {
            LogUtil.printErrorStackTrace(error, e);
        }
    }

    public WXSurfNetHandlerBO getWxSurfNetHandlerBO() {
        return wxSurfNetHandlerBO;
    }

    public void setWxSurfNetHandlerBO(WXSurfNetHandlerBO wxSurfNetHandlerBO) {
        this.wxSurfNetHandlerBO = wxSurfNetHandlerBO;
    }
}
