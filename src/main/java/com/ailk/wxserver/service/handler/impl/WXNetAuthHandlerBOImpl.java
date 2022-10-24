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
import com.ailk.wxserver.service.base.interfaces.WXNetAuthBO;
import com.ailk.wxserver.service.constant.ParamConstant;
import com.ailk.wxserver.service.constant.UserConstant;
import com.ailk.wxserver.service.constant.WXMsgCodeConstant;
import com.ailk.wxserver.service.constant.WXResultConstant;
import com.ailk.wxserver.service.handler.interfaces.WXNetAuthHandlerBO;
import com.ailk.wxserver.util.LogUtil;
import com.ailk.wxserver.util.RequestParamUtil;
import com.ailk.wxserver.util.StringUtils;
import com.ailk.wxserver.util.exception.DAOException;
import com.ailk.wxserver.util.log.LogFactory;
import com.ailk.wxserver.util.log.LogObj;
import com.ailk.wxserver.util.qq.wx.WXUtil;

public class WXNetAuthHandlerBOImpl implements WXNetAuthHandlerBO {

	
	private Logger log = LogFactory.getLogger("wxnetauthhandler");
	private Logger error = LogFactory.getLogger("error");

	private WXNetAuthBO wxNetAuthBO;
	private SpecialUserBO specialUserBO;
	
	@Override
	public Map<String, String> getRequestParam(HttpServletRequest request) {
		Map<String, String> paramMap = RequestParamUtil.getRequestParameters(request, false);
		return paramMap;
	}
	
	@Override
	public void response(HttpServletResponse response, String info,
			String redirectUrl) throws IOException {
		if(!StringUtils.isEmpty(info)){
			response.getWriter().print(info);
		}else if(!StringUtils.isEmpty(redirectUrl)){
			response.sendRedirect(redirectUrl);
		}
	}
	
	@Override
	public Map<String, String> doNetAuthReq(Map<String, String> paramMap) {

		Map<String, String> result = new HashMap<String, String>();
		String eccode = paramMap.get(ParamConstant.KEY_ECCODE);
		String openid = paramMap.get(ParamConstant.KEY_WX_OPENID);
		String timestamp = paramMap.get(ParamConstant.KEY_TIMESTAMP);
		String sessionid = paramMap.get(ParamConstant.KEY_SESSIONID);
		String registerid = paramMap.get(ParamConstant.KEY_REGISTERID);
		
		LogObj logObj = new LogObj();

		logObj.putSysKey(LogObj.COLLECT, "y").putSysKey(LogObj.SID, sessionid)
				.putSysKey(LogObj.MODULE, "netauth")
				.putSysKey(LogObj.STEP, "request");
		logObj.putData(ParamConstant.KEY_TIMESTAMP, timestamp)
				.putData(ParamConstant.KEY_ECCODE, eccode)
				.putData(ParamConstant.KEY_WX_OPENID, openid);
		log.info(logObj);

		int resultcode = 0;
		String desc = "";
		int replyMsg_code = 0;
		String redirectUrl = "";

		String authcode = "";
		
		String wxauth_netauth_fail_jsp_url = "";
		String wxauth_authcode_url = "";

		try {
			wxauth_netauth_fail_jsp_url = MPClientUtils.callService(
					"/mp/portal/config/business/getConfig", new Object[] {
							"wxauth", "netauth_fail_jsp_url" }, String.class);
			wxauth_authcode_url = MPClientUtils.callService(
					"/mp/portal/config/business/getConfig", new Object[] {
							"wxauth", "authcode_url" }, String.class);
			if (!wxNetAuthBO.validNetAuthReq(paramMap)) {
				resultcode = WXResultConstant.RESULT_REQPARAM_ERROR;
				desc = "netauth req param is error";
				replyMsg_code = WXMsgCodeConstant.WX_NETAUTH_CODE_PARAMERROR;
				redirectUrl = wxauth_netauth_fail_jsp_url;
			} else {
				DEnterpriseRegister der = MPClientUtils.callService(
						"/mp/portal/dEnterpriseRegister/getByEccode",
						new Object[] { eccode }, DEnterpriseRegister.class);
				if (der == null) {
					resultcode = WXResultConstant.RESULT_ENTERPRISE_REGISTER_NOEXIST;
					desc = "DEenterpriseRegister is not exist";
					replyMsg_code = WXMsgCodeConstant.WX_NETAUTH_CODE_WECHAT_NOTCONFIGURE;
					redirectUrl = wxauth_netauth_fail_jsp_url;
				} else {
                    int status = der.getStatus();
                    if (status == ParamConstant.KEY_STATUS_PAUSE) {
                        resultcode = WXResultConstant.RESULT_ENTERPRISE_REGISTER_STATUS_PAUSE;
                        desc = "DEenterpriseRegister status is pause";
                        replyMsg_code = WXMsgCodeConstant.WX_NETAUTH_CODE_STATUS_PAUSE;
                        redirectUrl = wxauth_netauth_fail_jsp_url;
                    } else {
                        BSpecialUser specialUser = specialUserBO.querySpecialUser(openid, eccode);
                        int wxuser_status = specialUserBO.validSpeicalUserStatus(specialUser);
                        if (wxuser_status == UserConstant.WXUSER_SPECIALUSER_NOTEXIST) {
                            resultcode = WXResultConstant.RESULT_SPECIALUSER_NOTEXIST;
                            desc = "special user is not exist";
                            replyMsg_code = WXMsgCodeConstant.WX_NETAUTH_CODE_NOTATTENTION;
                            redirectUrl = wxauth_netauth_fail_jsp_url;
                        } else if (wxuser_status == UserConstant.WXUSER_BROADUSER_NOORFAIL) {
                            resultcode = WXResultConstant.RESULT_BROADUSER_NOTEXIST;
                            desc = "broad user is not exist";
                            replyMsg_code = WXMsgCodeConstant.WX_NETAUTH_CODE_NOVERIFY;
                            redirectUrl = wxauth_netauth_fail_jsp_url;
                        } else if (wxuser_status == UserConstant.WXUSER_VERIFY_NOORFAIL) {
                            resultcode = WXResultConstant.RESULT_VERIFY_NO;
                            desc = "no verify or verify fail";
                            replyMsg_code = WXMsgCodeConstant.WX_NETAUTH_CODE_NOVERIFY;
                            redirectUrl = wxauth_netauth_fail_jsp_url;
                        } else if (wxuser_status == UserConstant.WXUSER_VERIFY_EXPIRED) {
                            resultcode = WXResultConstant.RESULT_VERIFY_EXPIRED;
                            desc = "verify expired";
                            replyMsg_code = WXMsgCodeConstant.WX_NETAUTH_CODE_VERIFYEXPIRED;
                            redirectUrl = wxauth_netauth_fail_jsp_url;
                        } else if (wxuser_status == UserConstant.WXUSER_NORMAIL) {
                            if (wxNetAuthBO.checkNetAuthTimestamp(timestamp)) {
                                redirectUrl = wxauth_authcode_url;
                                authcode = WXUtil.formAuthParam(openid, registerid);
                            } else {
                                resultcode = WXResultConstant.RESULT_NETLINK_EXPIRED;
                                desc = "netauth url is expired";
                                replyMsg_code = WXMsgCodeConstant.WX_NETAUTH_CODE_LINKEXPIRED;
                                redirectUrl = wxauth_netauth_fail_jsp_url;
                            }
                        }
                    }
				}

			}

		} catch (DAOException e) {
			LogUtil.printErrorStackTrace(error, e);
			resultcode = WXResultConstant.RESULT_OPERATEDB_EXCEPTION;
			desc = "operate db exception:" + e.getMessage();
			replyMsg_code = WXMsgCodeConstant.WX_NETAUTH_CODE_SYSTEMBUSY;
			redirectUrl = wxauth_netauth_fail_jsp_url;
		} catch (Exception e) {
			LogUtil.printErrorStackTrace(error, e);
			resultcode = WXResultConstant.RESULT_SYSTEM_EXCEPTION;
			desc = "netauth exception:" + e.getMessage();
			replyMsg_code = WXMsgCodeConstant.WX_NETAUTH_CODE_SYSTEMBUSY;
			redirectUrl = wxauth_netauth_fail_jsp_url;
		}
		paramMap.put(ParamConstant.KEY_REPLYMSG_CODE,String.valueOf(replyMsg_code));
		
		logObj.putData("source_authcode",authcode);
		authcode = WXUtil.encry(authcode);
		logObj.putData("encry_authcode",authcode);
		paramMap.put(ParamConstant.KEY_AUTHCODE, authcode);
		
		Map<String, String> finalParamMap = new HashMap<String, String>();
		redirectUrl = wxNetAuthBO.formRedirectUrl(paramMap, redirectUrl, finalParamMap);

		
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

	public WXNetAuthBO getWxNetAuthBO() {
		return wxNetAuthBO;
	}

	public SpecialUserBO getSpecialUserBO() {
		return specialUserBO;
	}

	public void setWxNetAuthBO(WXNetAuthBO wxNetAuthBO) {
		this.wxNetAuthBO = wxNetAuthBO;
	}

	public void setSpecialUserBO(SpecialUserBO specialUserBO) {
		this.specialUserBO = specialUserBO;
	}
}
