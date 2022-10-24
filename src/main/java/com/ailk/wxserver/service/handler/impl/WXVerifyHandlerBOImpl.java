package com.ailk.wxserver.service.handler.impl;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ailk.lcims.support.mp.client.MPClientUtils;
import com.ailk.wxserver.ci.client.http.HttpsClient;
import com.ailk.wxserver.po.BSpecialUser;
import com.ailk.wxserver.po.DEnterpriseRegister;
import com.ailk.wxserver.service.base.interfaces.SpecialUserBO;
import com.ailk.wxserver.service.base.interfaces.WXVerifyBO;
import com.ailk.wxserver.service.constant.CommonConstant;
import com.ailk.wxserver.service.constant.ParamConstant;
import com.ailk.wxserver.service.constant.SpecialAuthConstant;
import com.ailk.wxserver.service.constant.UserConstant;
import com.ailk.wxserver.service.constant.WXAuthContant;
import com.ailk.wxserver.service.constant.WXMsgCodeConstant;
import com.ailk.wxserver.service.constant.WXReqTypeConstant;
import com.ailk.wxserver.service.constant.WXResultConstant;
import com.ailk.wxserver.service.handler.interfaces.WXVerifyHandlerBO;
import com.ailk.wxserver.util.LogUtil;
import com.ailk.wxserver.util.MessageUtil;
import com.ailk.wxserver.util.RequestParamUtil;
import com.ailk.wxserver.util.ResponseResult;
import com.ailk.wxserver.util.StringUtils;
import com.ailk.wxserver.util.Validate;
import com.ailk.wxserver.util.exception.DAOException;
import com.ailk.wxserver.util.json.JsonUtil;
import com.ailk.wxserver.util.log.LogFactory;
import com.ailk.wxserver.util.log.LogObj;
import com.ailk.wxserver.util.qq.wx.WXUtil;

public class WXVerifyHandlerBOImpl implements WXVerifyHandlerBO {

    private Logger log = LogFactory.getLogger("wxverifyhandler");
    private Logger error = LogFactory.getLogger("error");

    private WXVerifyBO wxVerifyBO;
    private SpecialUserBO specialUserBO;

    @Override
    public Map<String, String> getRequestParam(HttpServletRequest request) {
        Map<String, String> paramMap = RequestParamUtil.getRequestParameters(request, false);
        return paramMap;
    }

    @Override
    public void response(HttpServletResponse response, String info, String redirectUrl)
            throws IOException {
        if (!StringUtils.isEmpty(info)) {
            response.getWriter().print(info);
        } else if (!StringUtils.isEmpty(redirectUrl)) {
            response.sendRedirect(redirectUrl);
        }

    }

    @Override
    public Map<String, String> doVerifyReq(Map<String, String> paramMap) {
        Map<String, String> result = null;
        String wxtype = paramMap.get(WXAuthContant.WX_VERIFY_REQNAME);
        if (WXReqTypeConstant.WX_REQTYPE_VERIFY_TYPE_GETVFCODE.equals(wxtype)) {
            // 获取验证码
            result = getVerifyCode(paramMap);
        } else if (WXReqTypeConstant.WX_REQTYPE_VERIFY_TYPE_LINK.equals(wxtype)) {
            // 链接验证
            result = verify(paramMap);
        } else if (WXReqTypeConstant.WX_REQTYPE_VERIFY_TYPE_VERIFYCODE.equals(wxtype)) {
            // 验证码验证
            result = verify(paramMap);
        } else {
            result = new HashMap<String, String>();
            result.put(ParamConstant.KEY_INFO, "不存在该请求类型");
            result.put(ParamConstant.KEY_RESULTCODE,
                    String.valueOf(WXResultConstant.RESULT_REQTYPE_NOTEXIST));
            result.put(ParamConstant.KEY_DESC, "not exist reqname:" + wxtype);
        }
        return result;
    }

    /**
     * 获取验证码
     * 
     * @param paramMap
     * @return
     */
    private Map<String, String> getVerifyCode(Map<String, String> paramMap) {
        Map<String, String> result = new HashMap<String, String>();
        String eccode = paramMap.get(ParamConstant.KEY_ECCODE);
        String phone = paramMap.get(ParamConstant.KEY_PHONE);
        String openid = paramMap.get(ParamConstant.KEY_WX_OPENID);

        String sessionid = paramMap.get(ParamConstant.KEY_SESSIONID);
        LogObj logObj = new LogObj();
        logObj.putSysKey(LogObj.COLLECT, "y").putSysKey(LogObj.SID, sessionid)
                .putSysKey(LogObj.MODULE, "getverifycode").putSysKey(LogObj.STEP, "request");
        logObj.putData(ParamConstant.KEY_ECCODE, eccode).putData(ParamConstant.KEY_PHONE, phone)
                .putData(ParamConstant.KEY_WX_OPENID, openid);
        log.info(logObj);
        // 回应用户消息码
        int replyMsg_code = 0;
        // resultInfoMap是返回给浏览器的信息
        Map<String, String> resultInfoMap = new HashMap<String, String>();
        int resultcode = 0;
        String desc = "";
        if (!wxVerifyBO.validGetVerifyCodeReq(paramMap)) {
            resultcode = WXResultConstant.RESULT_REQPARAM_ERROR;
            desc = "getverifycode req param is error";
            replyMsg_code = WXMsgCodeConstant.WX_VF_GETTOKEN_CODE_PARAMERROR;
        } else {
            try {
                DEnterpriseRegister der = MPClientUtils.callService(
                        "/mp/portal/dEnterpriseRegister/getByEccode", new Object[] { eccode },
                        DEnterpriseRegister.class);
                if (der == null) {
                    resultcode = WXResultConstant.RESULT_ENTERPRISE_REGISTER_NOEXIST;
                    desc = "DEenterpriseRegister is not exist";
                    replyMsg_code = WXMsgCodeConstant.WX_VF_GETTOKEN_CODE_WECHAT_NOTCONFIGURE;
                } else {
                    int status = der.getStatus();
                    if (status == ParamConstant.KEY_STATUS_PAUSE) {
                        resultcode = WXResultConstant.RESULT_ENTERPRISE_REGISTER_STATUS_PAUSE;
                        desc = "DEenterpriseRegister status is pause";
                        replyMsg_code = WXMsgCodeConstant.WX_VF_GETTOKEN_CODE_STATUS_PAUSE;
                    } else {
                        boolean sendVerifyCodeSMSFlag = false;
                        String registerid = der.getRegisterid();
                        BSpecialUser specialUser = specialUserBO.querySpecialUser(openid, eccode);
                        int wxuser_status = specialUserBO.validSpeicalUserStatus(specialUser);
                        if (wxuser_status == UserConstant.WXUSER_SPECIALUSER_NOTEXIST) {
                            resultcode = WXResultConstant.RESULT_SPECIALUSER_NOTEXIST;
                            desc = "special user is not exist";
                            replyMsg_code = WXMsgCodeConstant.WX_VF_GETTOKEN_CODE_NOTATTENTION;
                        } else if (wxuser_status == UserConstant.WXUSER_BROADUSER_NOORFAIL) {
                            Map<String, String> userMap = new HashMap<String, String>();
                            userMap.put(ParamConstant.KEY_REGISTERID, registerid);
                            userMap.put(ParamConstant.KEY_SESSIONID, sessionid);
                            ResponseResult responseResult = specialUserBO
                                    .openBroadUserAndUpdateSpecialUser(specialUser, userMap);
                            logObj.putData("result", responseResult.getResult()).putData("desc",
                                    responseResult.getDescription());
                            logObj.putSysKey(LogObj.STEP, "openbroaduser");
                            if (String.valueOf(WXResultConstant.RESULT_SUCCESS).equals(
                                    responseResult.getResult())) {
                                log.info(logObj);
                                sendVerifyCodeSMSFlag = true;
                            } else {
                                log.error(logObj);
                                resultcode = WXResultConstant.RESULT_AAAREQ_FAIL;
                                desc = "aaa openuser fail";
                                replyMsg_code = WXMsgCodeConstant.WX_VF_GETTOKEN_CODE_SYSTEMBUSY;
                            }
                        } else if (wxuser_status == UserConstant.WXUSER_VERIFY_NOORFAIL
                                || wxuser_status == UserConstant.WXUSER_VERIFY_EXPIRED) {
                            sendVerifyCodeSMSFlag = true;
                        } else if (wxuser_status == UserConstant.WXUSER_NORMAIL) {
                            resultcode = WXResultConstant.RESULT_HAS_VERIFIED;
                            desc = "special user has verfied and not expired";
                            replyMsg_code = WXMsgCodeConstant.WX_VF_GETTOKEN_CODE_VERFIED;
                        }

                        if (sendVerifyCodeSMSFlag) {
                            paramMap.put(ParamConstant.KEY_VERIFY_TYPE,
                                    WXReqTypeConstant.WX_REQTYPE_VERIFY_TYPE_VERIFYCODE);
                            ResponseResult responseResult = wxVerifyBO.sendVerifySms(paramMap);
                            resultcode = Integer.parseInt(responseResult.getResult());
                            desc = responseResult.getDescription();
                            if (String.valueOf(WXResultConstant.RESULT_SUCCESS).equals(
                                    responseResult.getResult())) {
                                replyMsg_code = WXMsgCodeConstant.WX_VF_GETTOKEN_CODE_SUCCESS;
                            } else if (String.valueOf(WXResultConstant.RESULT_SENDSMS_FAIL).equals(
                                    responseResult.getResult())) {
                                replyMsg_code = WXMsgCodeConstant.WX_VF_GETTOKEN_CODE_SMSFAIL;
                            } else {
                                replyMsg_code = WXMsgCodeConstant.WX_VF_GETTOKEN_CODE_SYSTEMBUSY;
                            }
                        }
                    }
                }
            } catch (DAOException e) {
                LogUtil.printErrorStackTrace(error, e);
                resultcode = WXResultConstant.RESULT_OPERATEDB_EXCEPTION;
                desc = "operate db exception:" + e.getMessage();
                replyMsg_code = WXMsgCodeConstant.WX_VF_GETTOKEN_CODE_SYSTEMBUSY;
            } catch (Exception e) {
                LogUtil.printErrorStackTrace(error, e);
                resultcode = WXResultConstant.RESULT_SYSTEM_EXCEPTION;
                desc = "verify gettoken exception";
                replyMsg_code = WXMsgCodeConstant.WX_VF_GETTOKEN_CODE_SYSTEMBUSY;
            }
        }

        Map<String, String> placeMap = new HashMap<String, String>();
        String verifycode = paramMap.get(ParamConstant.KEY_VERIFY_VERIFYCODE);
        if (StringUtils.isEmpty(verifycode)) {
            verifycode = "";
        }
        placeMap.put(CommonConstant.PLACE_VERIFYCODE, verifycode);

        String replyMsg = formReplyMsg(placeMap, eccode, replyMsg_code);

        resultInfoMap.put(ParamConstant.KEY_RESULTCODE, String.valueOf(resultcode));
        resultInfoMap.put(ParamConstant.KEY_DESC, desc);
        resultInfoMap.put(ParamConstant.KEY_REPLYMSG, replyMsg);
        resultInfoMap.put(ParamConstant.KEY_REPLYMSG_CODE, String.valueOf(replyMsg_code));
        String info = JsonUtil.map2json(resultInfoMap);

        result.put(ParamConstant.KEY_INFO, info);
        result.put(ParamConstant.KEY_RESULTCODE, String.valueOf(resultcode));
        result.put(ParamConstant.KEY_DESC, desc);

        logObj.putSysKey(LogObj.STEP, "response");
        logObj.putData("resultcode", resultcode).putData("desc", desc).putData("info", info);
        if (WXResultConstant.RESULT_SUCCESS == resultcode) {
            // 成功
            log.info(logObj);
        } else {
            // 失败
            log.error(logObj);
        }
        return result;
    }

    /**
     * 构造回应短消息内容
     * 
     * @param paramMap
     * @param eccode
     * @param replyMsg_code
     * @return
     */
    private String formReplyMsg(Map<String, String> placeMap, String eccode, int replyMsg_code) {
        String replyMsg = MessageUtil.getMessage(eccode + "_"
                + WXAuthContant.MSG_FUNCNAME_WX_VERIFY_GETTOKEN,
                WXAuthContant.MSG_FUNCNAME_WX_VERIFY_GETTOKEN, String.valueOf(replyMsg_code));
        try {
            replyMsg = StringUtils.placeHolderReform(1, replyMsg, placeMap);
        } catch (Exception e) {
            LogUtil.printErrorStackTrace(error, e);
        }
        return replyMsg;
    }

    /**
     * 验证
     * 
     * @param paramMap
     * @return
     */
    private Map<String, String> verify(Map<String, String> paramMap) {

        Map<String, String> result = new HashMap<String, String>();
        String verifytype = paramMap.get(ParamConstant.KEY_VERIFY_TYPE);
        String verifycode = paramMap.get(ParamConstant.KEY_VERIFY_VERIFYCODE);
        String phone = paramMap.get(ParamConstant.KEY_PHONE);
        String eccode = paramMap.get(ParamConstant.KEY_ECCODE);
        String openid = paramMap.get(ParamConstant.KEY_WX_OPENID);
        String registerid = paramMap.get(ParamConstant.KEY_REGISTERID);
        String sessionid = paramMap.get(ParamConstant.KEY_SESSIONID);

        LogObj logObj = new LogObj();
        logObj.putSysKey(LogObj.COLLECT, "y").putSysKey(LogObj.SID, sessionid)
                .putSysKey(LogObj.MODULE, "verify").putSysKey(LogObj.STEP, "request");
        logObj.putData(ParamConstant.KEY_VERIFY_TYPE, verifytype)
                .putData(ParamConstant.KEY_VERIFY_VERIFYCODE, verifycode)
                .putData(ParamConstant.KEY_PHONE, phone).putData(ParamConstant.KEY_ECCODE, eccode)
                .putData(ParamConstant.KEY_WX_OPENID, openid);
        log.info(logObj);

        int resultcode = 0;
        String desc = "";
        int replyMsg_code = 0;
        String redirectUrl = "";

        String wxverify_verifyfail_jsp_url = "";
        String wxauth_netauth_url = "";
        try {
            wxverify_verifyfail_jsp_url = MPClientUtils.callService(
                    "/mp/portal/config/business/getConfig", new Object[] { "wxverify",
                            "verifyfail_jsp_url" }, String.class);
            wxauth_netauth_url = MPClientUtils.callService("/mp/portal/config/business/getConfig",
                    new Object[] { "wxauth", "netauth_url" }, String.class);
            if (!wxVerifyBO.validVerifyReq(paramMap)) {
                resultcode = WXResultConstant.RESULT_REQPARAM_ERROR;
                desc = "verify req param is error";
                replyMsg_code = WXMsgCodeConstant.WX_VERIFY_CODE_REQPARAM_ERROR;
                redirectUrl = wxverify_verifyfail_jsp_url;
            } else {
                Map<String, String> memMap = wxVerifyBO.getMemValue(paramMap);
                String mem_key = memMap.get(ParamConstant.KEY_MEM_KEY);
                String mem_value = memMap.get(ParamConstant.KEY_MEM_VALUE);

                logObj.putSysKey(LogObj.STEP, "memcachedget");
                logObj.putData("mem_key", mem_key).putData("mem_value", mem_value);
                log.info(logObj);

                if (StringUtils.isEmpty(mem_value)) {
                    resultcode = WXResultConstant.RESULT_MEMCACHED_NOEXIST;
                    desc = "verifycode is not exist";
                    replyMsg_code = WXMsgCodeConstant.WX_VERIFY_CODE_VERIFYCODE_NOEXIST;
                    redirectUrl = wxverify_verifyfail_jsp_url;
                } else {
                    String mem_decry_value = WXUtil.decry(mem_value);
                    Map<String, String> verifyMap = WXUtil.parseVerifyData(mem_decry_value);
                    logObj.putData("mem_decry_value", mem_decry_value);
                    logObj.putData("verifyMap", verifyMap);
                    log.info(logObj);
                    ResponseResult verifyResult = checkVerifyData(paramMap, verifyMap);
                    if (StringUtils.isEmpty(eccode)) {
                        eccode = verifyMap.get(WXAuthContant.KEY_VERIFY_ECCODE);
                        paramMap.put(ParamConstant.KEY_ECCODE, eccode);
                    }
                    if (StringUtils.isEmpty(openid)) {
                        openid = verifyMap.get(WXAuthContant.KEY_VERIFY_OPENID);
                        paramMap.put(ParamConstant.KEY_WX_OPENID, openid);
                    }
                    if (StringUtils.isEmpty(registerid)) {
                        registerid = verifyMap.get(WXAuthContant.KEY_REGISTER_ID);
                        paramMap.put(ParamConstant.KEY_REGISTERID, registerid);
                    }
                    if (String.valueOf(WXResultConstant.RESULT_SUCCESS).equals(
                            verifyResult.getResult())) {
                        ResponseResult verifyOKResult = verifyok_process(verifyMap);
                        resultcode = Integer.parseInt(verifyOKResult.getResult());
                        desc = verifyOKResult.getDescription();
                        if (String.valueOf(WXResultConstant.RESULT_SUCCESS).equals(
                                verifyOKResult.getResult())) {
                            replyMsg_code = WXMsgCodeConstant.WX_VERIFY_CODE_SUCCESS;
                            redirectUrl = wxauth_netauth_url;
                            // 发送给微信营销手机验证结果，实现微信营销手机自动分组功能。
                            String wechat_manager_phone_url = MPClientUtils.callService(
                                    "/mp/portal/config/business/getConfig", new Object[] {
                                            "wxauth", "wechat_manager_phone_url" }, String.class);
                            if (wechat_manager_phone_url != null) {
                                Map<String, String> placeMap = new HashMap<String, String>();
                                placeMap.put("REGISTERID", registerid);
                                placeMap.put("OPENID", openid);
                                placeMap.put("PHONE", phone);
                                wechat_manager_phone_url = StringUtils.placeHolderReform(1,
                                        wechat_manager_phone_url, placeMap);
                                String res = "";
                                try {
                                    res = HttpsClient.post(wechat_manager_phone_url, "", "UTF-8");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    logObj.putSysKey(LogObj.STEP, "authPhone req");
                                    logObj.putData(ParamConstant.KEY_RESULTCODE,
                                            WXResultConstant.RESULT_SYSTEM_EXCEPTION).putData(
                                            ParamConstant.KEY_DESC, e);
                                    log.error(logObj);
                                }
                                logObj.putSysKey(LogObj.STEP, "authPhone req");
                                logObj.putData(ParamConstant.KEY_RESULTCODE,
                                        WXResultConstant.RESULT_SUCCESS).putData(
                                        ParamConstant.KEY_DESC, res);
                                log.info(logObj);
                            } else {
                                logObj.putSysKey(LogObj.STEP, "authPhone req");
                                logObj.putData(ParamConstant.KEY_RESULTCODE,
                                        WXResultConstant.RESULT_SYSTEM_EXCEPTION).putData(
                                        ParamConstant.KEY_DESC, "wechat_manager_url is not exist");
                                log.error(logObj);
                            }
                        } else {
                            replyMsg_code = WXMsgCodeConstant.WX_VERIFY_CODE_SYSTEM_BUSY;
                            redirectUrl = wxverify_verifyfail_jsp_url;
                        }
                    } else {
                        resultcode = Integer.parseInt(verifyResult.getResult());
                        desc = verifyResult.getDescription();
                        replyMsg_code = WXMsgCodeConstant.WX_VERIFY_CODE_VERIFYCODE_EXPIRE;
                        redirectUrl = wxverify_verifyfail_jsp_url;
                    }

                }
            }
        } catch (DAOException e) {
            LogUtil.printErrorStackTrace(error, e);
            resultcode = WXResultConstant.RESULT_OPERATEDB_EXCEPTION;
            desc = "operate db exception:" + e.getMessage();
            replyMsg_code = WXMsgCodeConstant.WX_VERIFY_CODE_SYSTEM_BUSY;
            redirectUrl = wxverify_verifyfail_jsp_url;
        } catch (Exception e) {
            LogUtil.printErrorStackTrace(error, e);
            resultcode = WXResultConstant.RESULT_SYSTEM_EXCEPTION;
            desc = "verify exception:" + e.getMessage();
            replyMsg_code = WXMsgCodeConstant.WX_VERIFY_CODE_SYSTEM_BUSY;
            redirectUrl = wxverify_verifyfail_jsp_url;
        }

        paramMap.put(ParamConstant.KEY_REPLYMSG_CODE, String.valueOf(replyMsg_code));

        Map<String, String> finalParamMap = new HashMap<String, String>();
        redirectUrl = wxVerifyBO.formRedirectUrl(paramMap, redirectUrl, finalParamMap);

        result.put(ParamConstant.KEY_REDIRCTURL, redirectUrl);
        result.put(ParamConstant.KEY_RESULTCODE, String.valueOf(resultcode));
        result.put(ParamConstant.KEY_DESC, desc);

        logObj.putSysKey(LogObj.STEP, "response");
        logObj.putData(ParamConstant.KEY_RESULTCODE, resultcode)
                .putData(ParamConstant.KEY_DESC, desc)
                .putData(ParamConstant.KEY_REDIRCTURL, redirectUrl)
                .putData(ParamConstant.KEY_FINALPARAMMAP, finalParamMap);
        if (WXResultConstant.RESULT_SUCCESS == resultcode) {
            // 成功
            log.info(logObj);
        } else {
            // 失败
            log.error(logObj);
        }
        return result;
    }

    /**
     * 微信URL验证
     * 
     * @param verifyMap
     * @return
     */
    private ResponseResult checkVerifyData(Map<String, String> paramMap,
            Map<String, String> verifyMap) throws Exception {
        ResponseResult result = new ResponseResult();
        if (verifyMap == null || verifyMap.isEmpty()) {
            result.setResult(String.valueOf(WXResultConstant.RESULT_VERIFY_FAIL));
            result.setDescription("mem verifydata is null");
            return result;
        }
        String v_eccode = verifyMap.get(WXAuthContant.KEY_VERIFY_ECCODE);
        String v_openid = verifyMap.get(WXAuthContant.KEY_VERIFY_OPENID);
        String v_apptype = verifyMap.get(WXAuthContant.KEY_VERIFY_TYPE);
        String v_phone = verifyMap.get(WXAuthContant.KEY_VERIFY_PHONE);
        String v_timestamp = verifyMap.get(WXAuthContant.KEY_VERIFY_TIMESTAMP);
        if (StringUtils.isEmpty(v_eccode)) {
            result.setResult(String.valueOf(WXResultConstant.RESULT_VERIFY_FAIL));
            result.setDescription("mem verifydata's eccode is null");
            return result;
        }
        if (StringUtils.isEmpty(v_openid)) {
            result.setResult(String.valueOf(WXResultConstant.RESULT_VERIFY_FAIL));
            result.setDescription("mem verifydata's openid is null");
            return result;
        }
        if (StringUtils.isEmpty(v_apptype)) {
            result.setResult(String.valueOf(WXResultConstant.RESULT_VERIFY_FAIL));
            result.setDescription("mem verifydata's apptype is null");
            return result;
        }
        if (StringUtils.isEmpty(v_phone)) {
            result.setResult(String.valueOf(WXResultConstant.RESULT_VERIFY_FAIL));
            result.setDescription("mem verifydata's phone is null");
            return result;
        }
        if (StringUtils.isEmpty(v_timestamp)) {
            result.setResult(String.valueOf(WXResultConstant.RESULT_VERIFY_FAIL));
            result.setDescription("mem verifydata's timestamp is null");
            return result;
        }

        if (!v_apptype.equals(String.valueOf(SpecialAuthConstant.APPTYPE_WX_VALUE))) {
            result.setResult(String.valueOf(WXResultConstant.RESULT_VERIFY_FAIL));
            result.setDescription("mem verifydata's apptype is not wx");
            return result;
        }

        String timestamp_valid = MPClientUtils.callService("/mp/portal/config/business/getConfig",
                new Object[] { "wxverify", "timestamp_valid" }, String.class);
        long expired_msec = Long.parseLong(timestamp_valid);

        if (!Validate.validateTimestamp(expired_msec, v_timestamp)) {
            result.setResult(String.valueOf(WXResultConstant.RESULT_VERIFY_FAIL));
            result.setDescription("mem verifydata's is expired");
            return result;
        }
        String verifytype = paramMap.get(ParamConstant.KEY_VERIFY_TYPE);
        String p_phone = paramMap.get(ParamConstant.KEY_PHONE);
        String p_eccode = paramMap.get(ParamConstant.KEY_ECCODE);
        String p_openid = paramMap.get(ParamConstant.KEY_WX_OPENID);
        if (WXReqTypeConstant.WX_REQTYPE_VERIFY_TYPE_VERIFYCODE.equals(verifytype)) {
            if (!v_eccode.equals(p_eccode)) {
                result.setResult(String.valueOf(WXResultConstant.RESULT_VERIFY_FAIL));
                result.setDescription("param's eccode and mem verifydata's eccode is not same");
                return result;
            }
            if (!v_openid.equals(p_openid)) {
                result.setResult(String.valueOf(WXResultConstant.RESULT_VERIFY_FAIL));
                result.setDescription("param's openid and mem verifydata's openid is not same");
                return result;
            }
            if (!v_phone.equals(p_phone)) {
                result.setResult(String.valueOf(WXResultConstant.RESULT_VERIFY_FAIL));
                result.setDescription("param's phone and mem verifydata's phone is not same");
                return result;
            }
        }
        result.setResult(String.valueOf(WXResultConstant.RESULT_SUCCESS));
        result.setDescription("cehck verifydata success");
        return result;
    }

    /**
     * 验证通过处理
     * 
     * @param verifyMap
     * @return
     * @throws DAOException
     */
    private ResponseResult verifyok_process(Map<String, String> verifyMap) throws DAOException {
        ResponseResult result = new ResponseResult();
        String eccode = verifyMap.get(WXAuthContant.KEY_VERIFY_ECCODE);
        String openid = verifyMap.get(WXAuthContant.KEY_VERIFY_OPENID);
        BSpecialUser specialUser = specialUserBO.querySpecialUser(openid, eccode);
        if (specialUser == null) {
            result.setResult(String.valueOf(WXResultConstant.RESULT_SPECIALUSER_NOTEXIST));
            result.setDescription("specail user is not exist");
        } else if (specialUser.getOpenuserStatus() != WXAuthContant.OPENUSERSTATUS_OK) {
            result.setResult(String.valueOf(WXResultConstant.RESULT_BROADUSER_NOTEXIST));
            result.setDescription("broad user is not open");
        } else if (specialUser.getVerifyStatus() == WXAuthContant.VERIFYSTATUS_VERIFYOK) {
            result.setResult(String.valueOf(WXResultConstant.RESULT_HAS_VERIFIED));
            result.setDescription("it has verified");
        } else {
            specialUser.setPhone(verifyMap.get(WXAuthContant.KEY_VERIFY_PHONE));
            specialUser.setVerifyStatus(WXAuthContant.VERIFYSTATUS_VERIFYOK);
            Calendar calendar = Calendar.getInstance();
            String verifyexpire = MPClientUtils.callService("/mp/portal/config/business/getConfig",
                    new Object[] { "wxverify", "verifyexpire" }, String.class);
            int expired_hour = Integer.parseInt(verifyexpire);
            calendar.add(Calendar.HOUR_OF_DAY, expired_hour);
            specialUser.setVerifyexpireDate(calendar.getTime());
            specialUser.setModDate(new Date());
            if (specialUserBO.updateSpecialUser(specialUser)) {
                result.setResult(String.valueOf(WXResultConstant.RESULT_SUCCESS));
                result.setDescription("specailuser update verify success");
            } else {
                result.setResult(String.valueOf(WXResultConstant.RESULT_OPERATEDB_FAIL));
                result.setDescription("specailuser update verify failure");
            }
        }
        return result;
    }

    public WXVerifyBO getWxVerifyBO() {
        return wxVerifyBO;
    }

    public SpecialUserBO getSpecialUserBO() {
        return specialUserBO;
    }

    public void setWxVerifyBO(WXVerifyBO wxVerifyBO) {
        this.wxVerifyBO = wxVerifyBO;
    }

    public void setSpecialUserBO(SpecialUserBO specialUserBO) {
        this.specialUserBO = specialUserBO;
    }
}
