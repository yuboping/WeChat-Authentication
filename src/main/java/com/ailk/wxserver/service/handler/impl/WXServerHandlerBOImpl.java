package com.ailk.wxserver.service.handler.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ailk.lcims.support.mp.client.MPClientUtils;
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
import com.ailk.wxserver.service.handler.interfaces.WXServerHandlerBO;
import com.ailk.wxserver.util.LogUtil;
import com.ailk.wxserver.util.ResponseResult;
import com.ailk.wxserver.util.StringUtils;
import com.ailk.wxserver.util.des3.Des3;
import com.ailk.wxserver.util.exception.DAOException;
import com.ailk.wxserver.util.log.LogFactory;
import com.ailk.wxserver.util.log.LogObj;

public class WXServerHandlerBOImpl implements WXServerHandlerBO {

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
        String signature = request.getParameter(WXAuthContant.KEY_URL_SIGNATURE);
        String echostr = request.getParameter(WXAuthContant.KEY_URL_ECHOSTR);
        String timestamp = request.getParameter(WXAuthContant.KEY_URL_TIMESTAMP);
        String nonce = request.getParameter(WXAuthContant.KEY_URL_NONCE);
        String encrypt_type = request.getParameter(WXAuthContant.KEY_URL_ENCRYPT_TYPE);
        String msg_signature = request.getParameter(WXAuthContant.KEY_URL_MSG_SIGNATURE);
        String uncode = request.getParameter(WXAuthContant.KEY_URL_UNCODE);
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put(WXAuthContant.KEY_URL_SIGNATURE, signature);
        paramMap.put(WXAuthContant.KEY_URL_ECHOSTR, echostr);
        paramMap.put(WXAuthContant.KEY_URL_TIMESTAMP, timestamp);
        paramMap.put(WXAuthContant.KEY_URL_NONCE, nonce);
        paramMap.put(WXAuthContant.KEY_URL_ENCRYPT_TYPE, encrypt_type);
        paramMap.put(WXAuthContant.KEY_URL_MSG_SIGNATURE, msg_signature);
        paramMap.put(WXAuthContant.KEY_URL_UNCODE, uncode);
        paramMap.put(WXAuthContant.KEY_MESSAGE, message);
        return paramMap;
    }

    @Override
    public void response(HttpServletResponse response, String reply_msg) throws IOException {
        response.getWriter().print(reply_msg);
    }

    @Override
    public ResponseResult doWXServerReq(Map<String, String> paramMap) {
        ResponseResult result = new ResponseResult();
        result.setResult(String.valueOf(WXResultConstant.RESULT_SUCCESS));
        LogObj logObj = new LogObj();
        logObj.putSysKey(LogObj.COLLECT, "y").putSysKey(LogObj.SID, paramMap.get("sessionid"))
                .putSysKey(LogObj.MODULE, "msgoperate").putSysKey(LogObj.STEP, "request");
        logObj.putData("paramMap", paramMap);
        log.info(logObj);

        result = prepareMsg(paramMap, logObj);
        if (String.valueOf(WXResultConstant.RESULT_SUCCESS).equals(result.getResult())) {
            WXMsgModel wxMsgModel = (WXMsgModel) result.getObject();
            logObj.putSysKey(LogObj.STEP, "parparemsg");
            logObj.putData("toUserName", wxMsgModel.getToUserName())
                    .putData("fromUserName", wxMsgModel.getFromUserName())
                    .putData("createTime", wxMsgModel.getCreateTime())
                    .putData("content", wxMsgModel.getContent())
                    .putData("msgType", wxMsgModel.getMsgType())
                    .putData("event", wxMsgModel.getEvent());
            log.info(logObj);

            result = handleMsg(paramMap, wxMsgModel, logObj);
            String replymsg = result.getInfo();
            result = formReplyMsg(paramMap, replymsg, wxMsgModel, logObj);
        }
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

    /**
     * 预处理消息，先解密，然后再解析成WXMsgModel对象
     * 
     * @param paramMap
     * @param logObj
     * @return
     */
    public ResponseResult prepareMsg(Map<String, String> paramMap, LogObj logObj) {
        ResponseResult result;
        result = decryptMsg(paramMap);
        if (String.valueOf(WXResultConstant.RESULT_SUCCESS).equals(result.getResult())) {
            logObj.putSysKey(LogObj.STEP, "decryptmsg");
            String message = paramMap.get(WXAuthContant.KEY_MESSAGE);
            logObj.putData("message", message);
            log.info(logObj);
            try {
                WXMsgModel wxMsgModel = wxMsgBO.parseMessage(message);
                result.setObject(wxMsgModel);
            } catch (IOException e) {
                LogUtil.printErrorStackTrace(error, e);
                result.setResult(String.valueOf(WXResultConstant.RESULT_INVALID_MSG));
                result.setDescription("parse message to model exception");
                result.setInfo(WXResultConstant
                        .getMsgByResultCode(WXResultConstant.RESULT_INVALID_MSG));
            }
        }
        return result;
    }

    /**
     * 
     * @Title: decryptMsg
     * @Description: TODO(Des3解密)
     * @param @param paramMap
     * @param @return 参数
     * @return ResponseResult 返回类型
     * @throws
     */
    public ResponseResult decryptMsg(Map<String, String> paramMap) {
        ResponseResult result = new ResponseResult();
        String message = paramMap.get(WXAuthContant.KEY_MESSAGE);
        String uncode = paramMap.get(WXAuthContant.KEY_URL_UNCODE);
        DEnterpriseRegister der = MPClientUtils.callService(
                "/mp/portal/dEnterpriseRegister/getByUncode", new Object[] { uncode },
                DEnterpriseRegister.class);
        if (der == null) {
            result.setResult(String.valueOf(WXResultConstant.RESULT_ENTERPRISE_REGISTER_NOEXIST));
            result.setDescription("no exist DEenterpriseRegister by uncode:" + uncode);
            result.setInfo(WXResultConstant
                    .getMsgByResultCode(WXResultConstant.RESULT_ENTERPRISE_REGISTER_NOEXIST));
            return result;
        }
        int status = der.getStatus();
        if (status == ParamConstant.KEY_STATUS_PAUSE) {
            result.setResult(String
                    .valueOf(WXResultConstant.RESULT_ENTERPRISE_REGISTER_STATUS_PAUSE));
            result.setDescription("DEenterpriseRegister status is pause:" + status);
            result.setInfo(WXResultConstant
                    .getMsgByResultCode(WXResultConstant.RESULT_ENTERPRISE_REGISTER_STATUS_PAUSE));
            return result;
        }

        try {
            String plainMsgXML = Des3.Des3DecCode(message, uncode);
            if (null != plainMsgXML && !"".equals(plainMsgXML)) {
                paramMap.put(WXAuthContant.KEY_MESSAGE, plainMsgXML);
                result.setResult(String.valueOf(WXResultConstant.RESULT_SUCCESS));
                result.setDescription("decript message success");
            } else {
                result.setResult("XML is null");
                result.setDescription("XML is null");
                result.setInfo("XML is null");
            }
        } catch (Exception e) {
            result.setResult("Des3DecCode fail");
            result.setDescription(e.getMessage());
            result.setInfo(e.getMessage());
        }
        return result;
    }

    /**
     * 具体处理消息
     * 
     * @param paramMap
     * @param wxMsgModel
     * @param logObj
     * @return
     */
    private ResponseResult handleMsg(Map<String, String> paramMap, WXMsgModel wxMsgModel,
            LogObj logObj) {
        ResponseResult result = new ResponseResult();
        String verify_cmd = MPClientUtils.callService("/mp/portal/config/business/getConfig",
                new Object[] { "wxauth", "verify_cmd" }, String.class);
        String link_Surf = MPClientUtils.callService("/mp/portal/config/business/getConfig",
                new Object[] { "wxauth", "link_Surf" }, String.class);
        if (WXAuthContant.MSGTYPE_TEXT.equals(wxMsgModel.getMsgType())) {
            // 文本消息
            String content = wxMsgModel.getContent();
            if (content.matches(verify_cmd)) {
                String phone = wxMsgBO.getPhone(content, verify_cmd);
                paramMap.put(ParamConstant.KEY_PHONE, phone);
                // 手机号验证消息
                result = wxMsgBO.msg_bindphone(paramMap, wxMsgModel);
            } else if (content.equals(link_Surf)) {
                // 获取上网链接消息
                result = wxMsgBO.msg_getlink(paramMap, wxMsgModel);
            } else {
                // 其他文本消息
                result = wxMsgBO.msg_other(paramMap, wxMsgModel);
            }
        } else if (WXAuthContant.MSGTYPE_EVENT.equals(wxMsgModel.getMsgType())) {
            // 事件推送
            String event = wxMsgModel.getEvent();
            if (WXAuthContant.EVENT_SUBSCRIBE.equals(event)) {
                // 订阅,包括微信扫描
                result = wxMsgBO.event_subscribe(paramMap, wxMsgModel);
            } else if (WXAuthContant.EVENT_UNSUBSCRIBE.equals(event)) {
                // 取消订阅
                result = wxMsgBO.event_unsubscribe(paramMap, wxMsgModel);
            } else if (WXAuthContant.EVENT_CLICK.equals(event)) {
                // 按钮菜单点击事件
                String eventkey = wxMsgModel.getEventKey();
                DEnterpriseRegister der = MPClientUtils.callService(
                        "/mp/portal/dEnterpriseRegister/getByRegisterid",
                        new Object[] { wxMsgModel.getToUserName() }, DEnterpriseRegister.class);
                boolean isMenuAuth = false;
                if (der != null) {
                    int status = der.getStatus();
                    if (status != ParamConstant.KEY_STATUS_PAUSE) {
                        String eccode = der.getEccode();
                        String menuKey = MPClientUtils.callService(
                                "/mp/portal/config/business/getConfig", new Object[] {
                                        "auth_from_menu", eccode }, String.class);
                        if (menuKey != null && menuKey.equals(eventkey)) {
                            isMenuAuth = true;
                        }
                    }
                }
                // 如果匹配到从菜单点击来认证上网的事件,或者key值就是shangwang，那么做认证操作
                if (eventkey.equals(link_Surf) || isMenuAuth) {
                    result = wxverify(paramMap, wxMsgModel);
                } else {
                    result = wxMsgBO.event_other(paramMap, wxMsgModel);
                }
            } else if (WXAuthContant.EVENT_WIFICONNECTED.equals(event)) {
                result = wxMsgBO.event_wifiConnected(paramMap, wxMsgModel);
            } else {
                // 其他事件
                result = wxMsgBO.event_other(paramMap, wxMsgModel);
            }
        }
        logObj.putSysKey(LogObj.STEP, "msghandle");
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

    /**
     * 构造回应消息体 先构造明文消息，再加密
     * 
     * @param paramMap
     * @param replymsg
     * @param logObj
     * @param wxMsgModel
     * @return
     */
    public ResponseResult formReplyMsg(Map<String, String> paramMap, String replymsg,
            WXMsgModel wxMsgModel, LogObj logObj) {
        String reply_plaintxt = "";
        if (StringUtils.isEmpty(replymsg)) {
            reply_plaintxt = "success";
        } else {
            reply_plaintxt = formReplyPlainMsg(wxMsgModel, replymsg);
        }
        logObj.putSysKey(LogObj.STEP, "replymsg");
        logObj.putData("reply_plaintxt", reply_plaintxt);
        log.info(logObj);
        ResponseResult result = null;
        if (StringUtils.isEmpty(reply_plaintxt) || "success".equalsIgnoreCase(reply_plaintxt)) {
            result = new ResponseResult();
            result.setResult(String.valueOf(WXResultConstant.RESULT_SUCCESS));
            result.setDescription("reply msg is empty,direct reply");
            result.setInfo(reply_plaintxt);
        } else {
            result = encryptReplyMsg(paramMap, wxMsgModel, reply_plaintxt);
        }
        return result;
    }

    /**
     * 
     * @Title: encryptReplyMsg
     * @Description: TODO(Des3加密)
     * @param @param paramMap
     * @param @param wxMsgModel
     * @param @param replymsg
     * @param @return 参数
     * @return ResponseResult 返回类型
     * @throws
     */
    public ResponseResult encryptReplyMsg(Map<String, String> paramMap, WXMsgModel wxMsgModel,
            String replymsg) {
        ResponseResult result = new ResponseResult();
        DEnterpriseRegister der = MPClientUtils.callService(
                "/mp/portal/dEnterpriseRegister/getByRegisterid",
                new Object[] { wxMsgModel.getToUserName() }, DEnterpriseRegister.class);
        try {
            String encryReplyMsg = Des3.Des3EnCode(replymsg, der.getUncode());
            result.setResult(String.valueOf(WXResultConstant.RESULT_SUCCESS));
            result.setDescription("encrypt reply message success");
            result.setInfo(encryReplyMsg);
        } catch (Exception e) {
            result.setResult("Des3EnCode fail");
            result.setDescription(e.getMessage());
            result.setInfo(e.getMessage());
        }
        return result;
    }

    public String formReplyPlainMsg(WXMsgModel wxMsgModel, String content) {
        StringBuffer msg = new StringBuffer();
        String OperateType = "";
        if (WXAuthContant.EVENT_UNSUBSCRIBE.equals(wxMsgModel.getEvent())) {
            msg.append("<xml>");
            msg.append("<ToUserName><![CDATA[");
            msg.append(wxMsgModel.getFromUserName());
            msg.append("]]></ToUserName>");
            msg.append("<FromUserName><![CDATA[");
            msg.append(wxMsgModel.getToUserName());
            msg.append("]]></FromUserName>");
            msg.append("<CreateTime>");
            msg.append(wxMsgModel.getCreateTime());
            msg.append("</CreateTime>");
            msg.append("<MsgType><![CDATA[");
            msg.append(WXAuthContant.MSGTYPE_TEXT);
            msg.append("]]></MsgType>");
            msg.append("<Content><![CDATA[");
            msg.append(content);
            msg.append("]]></Content>");
            msg.append("</xml>");
        } else {
            if (WXAuthContant.EVENT_SUBSCRIBE.equals(wxMsgModel.getEvent())) {
                OperateType = "99";
            } else {
                OperateType = "0";
            }
            msg.append("<xml>");
            msg.append("<ToUserName><![CDATA[");
            msg.append(wxMsgModel.getFromUserName());
            msg.append("]]></ToUserName>");
            msg.append("<FromUserName><![CDATA[");
            msg.append(wxMsgModel.getToUserName());
            msg.append("]]></FromUserName>");
            msg.append("<CreateTime>");
            msg.append(wxMsgModel.getCreateTime());
            msg.append("</CreateTime>");
            msg.append("<MsgType><![CDATA[");
            msg.append(WXAuthContant.MSGTYPE_TEXT);
            msg.append("]]></MsgType>");
            msg.append("<OperateType><![CDATA[");
            msg.append(OperateType);
            msg.append("]]></OperateType>");
            msg.append("<Content><![CDATA[");
            msg.append(content);
            msg.append("]]></Content>");
            msg.append("</xml>");
        }
        return msg.toString();
    }

    private ResponseResult wxverify(Map<String, String> paramMap, WXMsgModel wxMsgModel) {
        ResponseResult result = new ResponseResult();
        String fromUserName = wxMsgModel.getFromUserName();
        String toUserName = wxMsgModel.getToUserName();
        String sessionid = paramMap.get(ParamConstant.KEY_SESSIONID);
        LogObj logObj = new LogObj();
        logObj.putSysKey(LogObj.COLLECT, "y").putSysKey(LogObj.SID, sessionid)
                .putSysKey(LogObj.MODULE, "wxverify").putSysKey(LogObj.STEP, "request");
        logObj.putData("toUserName", toUserName).putData("fromUserName", fromUserName)
                .putData("content", wxMsgModel.getContent());
        log.info(logObj);
        String openid = fromUserName;
        paramMap.put(WXAuthContant.KEY_WIFI_OPENID, openid);
        int replyMsg_code = 0;
        String eccode = "";

        int resultcode = 0;
        String desc = "";

        String redirectUrl = "";
        String wxwifi_fail_jsp_url = "";
        String wxverify_verify_jsp_url = "";
        String wxauth_netauth_url = "";
        try {
            wxwifi_fail_jsp_url = MPClientUtils.callService("/mp/portal/config/business/getConfig",
                    new Object[] { "wxwifi", "wxwifi_fail_jsp_url" }, String.class);
            wxverify_verify_jsp_url = MPClientUtils.callService(
                    "/mp/portal/config/business/getConfig", new Object[] { "wxverify",
                            "verify_jsp_url" }, String.class);
            wxauth_netauth_url = MPClientUtils.callService("/mp/portal/config/business/getConfig",
                    new Object[] { "wxauth", "netauth_url" }, String.class);

            DEnterpriseRegister der = MPClientUtils.callService(
                    "/mp/portal/dEnterpriseRegister/getByRegisterid",
                    new Object[] { wxMsgModel.getToUserName() }, DEnterpriseRegister.class);
            if (der == null) {
                resultcode = WXResultConstant.RESULT_ENTERPRISE_REGISTER_NOEXIST;
                desc = "DEenterpriseRegister is not exist";
                replyMsg_code = WXMsgCodeConstant.WX_WIFI_CODE_WECHAT_NOTCONFIGURE;
                redirectUrl = wxwifi_fail_jsp_url;
            } else {
                int status = der.getStatus();
                if (status == ParamConstant.KEY_STATUS_PAUSE) {
                    resultcode = WXResultConstant.RESULT_ENTERPRISE_REGISTER_STATUS_PAUSE;
                    desc = "DEenterpriseRegister is not exist";
                    replyMsg_code = WXMsgCodeConstant.WX_WIFI_CODE_STATUS_PAUSE;
                    redirectUrl = wxwifi_fail_jsp_url;
                } else {
                    eccode = der.getEccode();
                    paramMap.put(ParamConstant.KEY_ECCODE, eccode);
                    BSpecialUser specialUser = new BSpecialUser();
                    specialUser = specialUserBO.querySpecialUser(openid, eccode);
                    if (specialUser == null) {
                        wxMsgBO.event_subscribe(paramMap, wxMsgModel);
                        specialUser = specialUserBO.querySpecialUser(openid, eccode);
                    }
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
                }
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
        paramMap.put(ParamConstant.KEY_REPLYMSG_CODE, String.valueOf(replyMsg_code));

        Map<String, String> finalParamMap = new HashMap<String, String>();
        redirectUrl = wxWifiBO.formRedirectUrl(paramMap, redirectUrl, finalParamMap);

        result.setInfo(redirectUrl);
        result.setResult(String.valueOf(resultcode));
        result.setDescription(desc);

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
