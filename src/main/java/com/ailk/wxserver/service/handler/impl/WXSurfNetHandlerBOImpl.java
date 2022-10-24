package com.ailk.wxserver.service.handler.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ailk.lcims.support.mp.client.MPClientUtils;
import com.ailk.wxserver.ci.client.http.HttpsClient;
import com.ailk.wxserver.model.WXMsgModel;
import com.ailk.wxserver.po.BSpecialUser;
import com.ailk.wxserver.po.DEnterpriseRegister;
import com.ailk.wxserver.service.base.interfaces.SpecialUserBO;
import com.ailk.wxserver.service.base.interfaces.WXMsgBO;
import com.ailk.wxserver.service.base.interfaces.WXWifiBO;
import com.ailk.wxserver.service.constant.ParamConstant;
import com.ailk.wxserver.service.constant.WXAuthContant;
import com.ailk.wxserver.service.constant.WXMsgCodeConstant;
import com.ailk.wxserver.service.constant.WXResultConstant;
import com.ailk.wxserver.service.handler.interfaces.WXSurfNetHandlerBO;
import com.ailk.wxserver.util.LogUtil;
import com.ailk.wxserver.util.ResponseResult;
import com.ailk.wxserver.util.StringUtils;
import com.ailk.wxserver.util.exception.DAOException;
import com.ailk.wxserver.util.json.JsonUtil;
import com.ailk.wxserver.util.log.LogFactory;
import com.ailk.wxserver.util.log.LogObj;

public class WXSurfNetHandlerBOImpl implements WXSurfNetHandlerBO {

    private Logger log = LogFactory.getLogger("wxmsghandler");
    private Logger error = LogFactory.getLogger("error");

    private WXMsgBO wxMsgBO;
    private WXWifiBO wxWifiBO;
    private SpecialUserBO specialUserBO;

    @Override
    public Map<String, String> getRequestParam(HttpServletRequest request) throws IOException {
        // 获取post消息体
        String message = wxMsgBO.getRequestPostMsg(request);
        // 获取url中参数
        String code = request.getParameter(WXAuthContant.WX_SURFNET_CODE);
        String state = request.getParameter(WXAuthContant.WX_SURFNET_STATE);
        String registerid = request.getParameter(WXAuthContant.KEY_REGISTER_ID);
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put(WXAuthContant.WX_SURFNET_CODE, code);
        paramMap.put(WXAuthContant.WX_SURFNET_STATE, state);
        paramMap.put(WXAuthContant.KEY_REGISTER_ID, registerid);
        paramMap.put(WXAuthContant.KEY_MESSAGE, message);
        return paramMap;
    }

    @Override
    public void response(HttpServletResponse response, String desc, String redirectUrl)
            throws IOException {
        if (!StringUtils.isEmpty(redirectUrl)) {
            response.sendRedirect(redirectUrl);
        } else {
            response.getWriter().print(desc);
        }
    }

    @Override
    public ResponseResult doSurfNetReq(Map<String, String> paramMap) {
        ResponseResult result = new ResponseResult();
        int resultcode = 0;
        String redirectUrl = "";
        String desc = "";
        result.setResult(String.valueOf(WXResultConstant.RESULT_SUCCESS));
        LogObj logObj = new LogObj();
        logObj.putSysKey(LogObj.COLLECT, "y").putSysKey(LogObj.SID, paramMap.get("sessionid"))
                .putSysKey(LogObj.MODULE, "wxsurfnetoperation").putSysKey(LogObj.STEP, "request");
        logObj.putData("paramMap", paramMap);
        log.info(logObj);
        String code = paramMap.get(WXAuthContant.WX_SURFNET_CODE);
        String state = paramMap.get(WXAuthContant.WX_SURFNET_STATE);
        String registerid = paramMap.get(WXAuthContant.KEY_REGISTER_ID);
        if (StringUtils.isEmpty(registerid)) {
            if (!StringUtils.isEmpty(code) && !StringUtils.isEmpty(state)) {
                DEnterpriseRegister der = MPClientUtils.callService(
                        "/mp/portal/dEnterpriseRegister/getByRegisterid", new Object[] { state },
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
                        String oauth2_access_token_url = MPClientUtils.callService(
                                "/mp/portal/config/business/getConfig", new Object[] { "wxauth",
                                        "oauth2_access_token_url" }, String.class);
                        if (oauth2_access_token_url == null) {
                            resultcode = WXResultConstant.RESULT_SYSTEM_EXCEPTION;
                            desc = "oauth2_access_token_url is not exist";
                        } else {
                            String appid = der.getAppid();
                            String uncode = der.getUncode();
                            String appsecret = MPClientUtils.callService(
                                    "/mp/portal/config/business/getConfig", new Object[] {
                                            "thirdparty" + uncode, "appsecret" }, String.class);
                            if (appsecret == null) {
                                resultcode = WXResultConstant.RESULT_SYSTEM_EXCEPTION;
                                desc = "appsecret is not exist";
                            } else {
                                Map<String, String> placeMap = new HashMap<String, String>();
                                placeMap.put("APPID", appid);
                                placeMap.put("SECRET", appsecret);
                                placeMap.put("CODE", code);
                                try {
                                    oauth2_access_token_url = StringUtils.placeHolderReform(1,
                                            oauth2_access_token_url, placeMap);
                                } catch (Exception e) {
                                    LogUtil.printErrorStackTrace(error, e);
                                    error.error(e.getMessage());
                                }
                                String jsonStr = HttpsClient.post(oauth2_access_token_url, "",
                                        "UTF-8");
                                Map<String, Object> m = JsonUtil.json2map(jsonStr);
                                if (m.get("openid") == null) {
                                    resultcode = WXResultConstant.RESULT_SYSTEM_EXCEPTION;
                                    desc = "openid is not exist";
                                } else {
                                    String openid = m.get("openid").toString();
                                    paramMap.put(WXAuthContant.KEY_WIFI_OPENID, openid);
                                    int replyMsg_code = 0;
                                    String eccode = der.getEccode();
                                    String wxwifi_fail_jsp_url = "";
                                    String wxverify_verify_jsp_url = "";
                                    String wxauth_netauth_url = "";
                                    try {
                                        wxwifi_fail_jsp_url = MPClientUtils.callService(
                                                "/mp/portal/config/business/getConfig",
                                                new Object[] { "wxwifi", "wxwifi_fail_jsp_url" },
                                                String.class);
                                        wxverify_verify_jsp_url = MPClientUtils.callService(
                                                "/mp/portal/config/business/getConfig",
                                                new Object[] { "wxverify", "verify_jsp_url" },
                                                String.class);
                                        wxauth_netauth_url = MPClientUtils.callService(
                                                "/mp/portal/config/business/getConfig",
                                                new Object[] { "wxauth", "netauth_url" },
                                                String.class);
                                        paramMap.put(ParamConstant.KEY_ECCODE, eccode);
                                        BSpecialUser specialUser = specialUserBO.querySpecialUser(
                                                openid, eccode);
                                        ResponseResult result_subscribe = new ResponseResult();
                                        if (specialUser == null) {
                                            WXMsgModel wxMsgModel = new WXMsgModel();
                                            wxMsgModel.setFromUserName(openid);
                                            wxMsgModel.setToUserName(registerid);
                                            result_subscribe = wxMsgBO.event_subscribe(paramMap,
                                                    wxMsgModel);
                                        }
                                        if (String.valueOf(WXResultConstant.RESULT_SUCCESS).equals(
                                                result_subscribe.getResult())) {
                                            if (specialUserBO.isNeedVerifyPhone(specialUser) == 0) {
                                                // 验证通过，跳转到微信认证
                                                redirectUrl = wxauth_netauth_url;
                                                resultcode = WXResultConstant.RESULT_SUCCESS;
                                                desc = "phone verify is ok,redirect to netauthurl";
                                            } else {
                                                // 需要验证，跳转到微信验证页面
                                                redirectUrl = wxverify_verify_jsp_url;
                                                resultcode = WXResultConstant.RESULT_VERIFY_NEED;
                                                desc = "need verify phone,redirect to verifyurl";
                                            }
                                        } else {
                                            result = result_subscribe;
                                        }
                                    } catch (DAOException e) {
                                        LogUtil.printErrorStackTrace(error, e);
                                        resultcode = WXResultConstant.RESULT_OPERATEDB_EXCEPTION;
                                        desc = "operate db exception:" + e.getMessage();
                                        replyMsg_code = WXMsgCodeConstant.WX_VERIFY_CODE_SYSTEM_BUSY;
                                        redirectUrl = wxwifi_fail_jsp_url;
                                    } catch (Exception e) {
                                        LogUtil.printErrorStackTrace(error, e);
                                        resultcode = WXResultConstant.RESULT_SYSTEM_EXCEPTION;
                                        desc = "wifi check exception";
                                        replyMsg_code = WXMsgCodeConstant.WX_WIFI_CODE_EXCEPTION;
                                        redirectUrl = wxwifi_fail_jsp_url;
                                    }
                                    paramMap.put(ParamConstant.KEY_REPLYMSG_CODE,
                                            String.valueOf(replyMsg_code));
                                    Map<String, String> finalParamMap = new HashMap<String, String>();
                                    redirectUrl = wxWifiBO.formRedirectUrl(paramMap, redirectUrl,
                                            finalParamMap);
                                }
                            }
                        }
                    }
                }
            }
        } else {
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
                    String connect_oauth2_authorize_url = MPClientUtils.callService(
                            "/mp/portal/config/business/getConfig", new Object[] { "wxauth",
                                    "connect_oauth2_authorize_url" }, String.class);
                    if (connect_oauth2_authorize_url == null) {
                        resultcode = WXResultConstant.RESULT_SYSTEM_EXCEPTION;
                        desc = "connect_oauth2_authorize_url is not exist";
                    } else {
                        String appid = der.getAppid();
                        state = der.getRegisterid();
                        Map<String, String> placeMap = new HashMap<String, String>();
                        placeMap.put("APPID", appid);
                        placeMap.put("STATE", state);
                        try {
                            connect_oauth2_authorize_url = StringUtils.placeHolderReform(1,
                                    connect_oauth2_authorize_url, placeMap);
                        } catch (Exception e) {
                            LogUtil.printErrorStackTrace(error, e);
                            error.error(e.getMessage());
                        }
                        redirectUrl = connect_oauth2_authorize_url;
                        resultcode = WXResultConstant.RESULT_SUCCESS;
                        desc = "redirect to connect_oauth2_authorize_url";
                    }
                }
            }
        }

        result.setInfo(redirectUrl);
        result.setResult(String.valueOf(resultcode));
        result.setDescription(desc);

        logObj.putSysKey(LogObj.STEP, "response");
        logObj.putData("result", result.getResult());
        logObj.putData("desc", result.getDescription());
        logObj.putData("info", result.getInfo());
        if (String.valueOf(WXResultConstant.RESULT_SUCCESS).equals(result.getResult())) {
            log.info(logObj);
        } else {
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

    public WXWifiBO getWxWifiBO() {
        return wxWifiBO;
    }

    public void setWxWifiBO(WXWifiBO wxWifiBO) {
        this.wxWifiBO = wxWifiBO;
    }

    public SpecialUserBO getSpecialUserBO() {
        return specialUserBO;
    }

    public void setSpecialUserBO(SpecialUserBO specialUserBO) {
        this.specialUserBO = specialUserBO;
    }
}
