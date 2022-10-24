package com.ailk.wxserver.service.handler.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ailk.lcims.support.mp.client.MPClientUtils;
import com.ailk.wxserver.po.BSpecialUser;
import com.ailk.wxserver.po.DEnterpriseRegister;
import com.ailk.wxserver.service.base.interfaces.SpecialUserBO;
import com.ailk.wxserver.service.base.interfaces.WXMsgBO;
import com.ailk.wxserver.service.constant.ParamConstant;
import com.ailk.wxserver.service.constant.WXAuthContant;
import com.ailk.wxserver.service.constant.WXResultConstant;
import com.ailk.wxserver.service.handler.interfaces.WXManagerHandlerBO;
import com.ailk.wxserver.util.LogUtil;
import com.ailk.wxserver.util.exception.DAOException;
import com.ailk.wxserver.util.json.JsonUtil;
import com.ailk.wxserver.util.log.LogFactory;
import com.ailk.wxserver.util.log.LogObj;

public class WXManagerHandlerBOImpl implements WXManagerHandlerBO {

    private Logger log = LogFactory.getLogger("wxmsghandler");
    private Logger error = LogFactory.getLogger("error");

    private WXMsgBO wxMsgBO;
    private SpecialUserBO specialUserBO;

    @Override
    public Map<String, String> getRequestParam(HttpServletRequest request) throws IOException {
        // 获取post消息体
        String message = wxMsgBO.getRequestPostMsg(request);
        // 获取url中参数
        String wxManagerType = request.getParameter(WXAuthContant.WX_MANAGER_TYPE);
        String openid = request.getParameter(ParamConstant.KEY_WX_OPENID);
        String registerid = request.getParameter(WXAuthContant.KEY_REGISTER_ID);
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put(WXAuthContant.WX_MANAGER_TYPE, wxManagerType);
        paramMap.put(ParamConstant.KEY_WX_OPENID, openid);
        paramMap.put(WXAuthContant.KEY_REGISTER_ID, registerid);
        paramMap.put(WXAuthContant.KEY_MESSAGE, message);
        return paramMap;
    }

    @Override
    public void response(HttpServletResponse response, String reply_msg) throws IOException {
        response.getWriter().print(reply_msg);
    }

    @Override
    public Map<String, String> doManagerReq(Map<String, String> paramMap) {
        Map<String, String> result = null;
        String wxManagerType = paramMap.get(WXAuthContant.WX_MANAGER_TYPE);
        if (WXAuthContant.WX_MANAGER_FANSPHONE.equals(wxManagerType)) {
            // 获取微信营销获取手机号请求
            result = getFansPhone(paramMap);
        } else {
            result = new HashMap<String, String>();
            result.put(ParamConstant.KEY_INFO, "不存在该请求类型");
            result.put(ParamConstant.KEY_RESULTCODE,
                    String.valueOf(WXResultConstant.RESULT_REQTYPE_NOTEXIST));
            result.put(ParamConstant.KEY_DESC, "not exist reqname:" + wxManagerType);
        }
        return result;
    }

    private Map<String, String> getFansPhone(Map<String, String> paramMap) {
        Map<String, String> result = new HashMap<String, String>();
        String registerid = paramMap.get(WXAuthContant.KEY_REGISTER_ID);
        String openid = paramMap.get(ParamConstant.KEY_WX_OPENID);
        String sessionid = paramMap.get(ParamConstant.KEY_SESSIONID);
        LogObj logObj = new LogObj();
        logObj.putSysKey(LogObj.COLLECT, "y").putSysKey(LogObj.SID, sessionid)
                .putSysKey(LogObj.MODULE, "getFansPhone").putSysKey(LogObj.STEP, "request");
        logObj.putData(WXAuthContant.KEY_REGISTER_ID, registerid)
                .putData(ParamConstant.KEY_WX_OPENID, openid)
                .putData(ParamConstant.KEY_SESSIONID, sessionid);
        log.info(logObj);
        // resultInfoMap是返回给浏览器的信息
        Map<String, String> resultInfoMap = new HashMap<String, String>();
        int resultcode = WXResultConstant.RESULT_SUCCESS;
        String desc = "";
        String phone = "";
        DEnterpriseRegister der = MPClientUtils.callService(
                "/mp/portal/dEnterpriseRegister/getByRegisterid", new Object[] { registerid },
                DEnterpriseRegister.class);
        if (der == null) {
            resultcode = WXResultConstant.RESULT_ENTERPRISE_REGISTER_NOEXIST;
            desc = "DEenterpriseRegister is not exist";
        } else {
            int status = der.getStatus();
            if (status == ParamConstant.KEY_STATUS_PAUSE) {
                resultcode = WXResultConstant.RESULT_ENTERPRISE_REGISTER_STATUS_PAUSE;
                desc = "DEenterpriseRegister status is pause";
            } else {
                String eccode = der.getEccode();
                try {
                    BSpecialUser specialUser = specialUserBO.querySpecialUser(openid, eccode);
                    if (specialUser == null) {
                        resultcode = WXResultConstant.RESULT_SPECIALUSER_NOTEXIST;
                        desc = "special user is not exist";
                    } else {
                        resultcode = WXResultConstant.RESULT_SUCCESS;
                        desc = "getFansPhone success";
                        phone = specialUser.getPhone();
                    }
                } catch (DAOException e) {
                    LogUtil.printErrorStackTrace(error, e);
                    resultcode = WXResultConstant.RESULT_OPERATEDB_EXCEPTION;
                    desc = "operate db exception:" + e.getMessage();
                } catch (Exception e) {
                    LogUtil.printErrorStackTrace(error, e);
                    resultcode = WXResultConstant.RESULT_SYSTEM_EXCEPTION;
                    desc = "getFansPhone exception";
                }
            }
        }
        resultInfoMap.put(ParamConstant.KEY_RESULTCODE, String.valueOf(resultcode));
        resultInfoMap.put(ParamConstant.KEY_DESC, desc);
        resultInfoMap.put(ParamConstant.KEY_PHONE, phone);
        String info = JsonUtil.map2json(resultInfoMap);
        result.put(ParamConstant.KEY_INFO, info);
        logObj.putSysKey(LogObj.STEP, "response");
        logObj.putData("info", info);
        if (WXResultConstant.RESULT_SUCCESS == resultcode) {
            // 成功
            log.info(logObj);
        } else {
            // 失败
            log.error(logObj);
        }
        return result;
    }

    public WXMsgBO getWxMsgBO() {
        return wxMsgBO;
    }

    public void setWxMsgBO(WXMsgBO wxMsgBO) {
        this.wxMsgBO = wxMsgBO;
    }

    public SpecialUserBO getSpecialUserBO() {
        return specialUserBO;
    }

    public void setSpecialUserBO(SpecialUserBO specialUserBO) {
        this.specialUserBO = specialUserBO;
    }
}
