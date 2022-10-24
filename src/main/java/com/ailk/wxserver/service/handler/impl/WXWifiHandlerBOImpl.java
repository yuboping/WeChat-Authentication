package com.ailk.wxserver.service.handler.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ailk.lcims.support.mp.client.MPClientUtils;
import com.ailk.wxserver.po.BSpecialUser;
import com.ailk.wxserver.po.DEnterpriseRegister;
import com.ailk.wxserver.service.base.interfaces.MemCachedBO;
import com.ailk.wxserver.service.base.interfaces.SpecialUserBO;
import com.ailk.wxserver.service.base.interfaces.WXWifiBO;
import com.ailk.wxserver.service.constant.ParamConstant;
import com.ailk.wxserver.service.constant.WXAuthContant;
import com.ailk.wxserver.service.constant.WXMsgCodeConstant;
import com.ailk.wxserver.service.constant.WXReqTypeConstant;
import com.ailk.wxserver.service.constant.WXResultConstant;
import com.ailk.wxserver.service.handler.interfaces.WXWifiHandlerBO;
import com.ailk.wxserver.util.LogUtil;
import com.ailk.wxserver.util.RequestParamUtil;
import com.ailk.wxserver.util.StringUtils;
import com.ailk.wxserver.util.SystemUtil;
import com.ailk.wxserver.util.exception.DAOException;
import com.ailk.wxserver.util.json.JsonUtil;
import com.ailk.wxserver.util.log.LogFactory;
import com.ailk.wxserver.util.log.LogObj;
import com.ailk.wxserver.util.qq.wx.WXUtil;

public class WXWifiHandlerBOImpl implements WXWifiHandlerBO{

	private Logger log = LogFactory.getLogger("wxwifihandler");
	private Logger error = LogFactory.getLogger("error");
	
	private WXWifiBO wxWifiBO = null;
	private MemCachedBO memCachedBO = null;
	private SpecialUserBO specialUserBO = null;
	
	@Override
	public Map<String, String> getRequestParam(HttpServletRequest request){
		Map<String, String> paramMap = RequestParamUtil.getRequestParameters(request, false);
		boolean cookieflag = wxWifiBO.isReadCookie(paramMap);
		if(cookieflag){
			Cookie[] cookies = request.getCookies();
			if(cookies != null){
				for(Cookie cookie : cookies){
					String name = cookie.getName();
					String value = cookie.getValue();
					//获取cookie数据，保存到map中，key值设置特殊的，与url传的参数值区分开来，为后面处理获取
					if(WXAuthContant.KEY_WIFI_OPENID.equals(name)){
						paramMap.put(WXAuthContant.KEY_WIFI_C_OPENID, value);
					}else if(WXAuthContant.KEY_WIFI_TID.equals(name)){
						paramMap.put(WXAuthContant.KEY_WIFI_C_TID, value);
					}else if(WXAuthContant.KEY_WIFI_EXTEND.equals(name)){
						paramMap.put(WXAuthContant.KEY_WIFI_C_EXTEND, value);
					}
				}
			}
		}
		return paramMap;
	}

	@Override
	public void response(HttpServletResponse response, Map<String, String>paramMap,String info,
			String redirectUrl) throws IOException{
		boolean cookieflag = wxWifiBO.isWriteCookie(paramMap);
		if(cookieflag){
			try {
				String openid = paramMap.get(WXAuthContant.KEY_WIFI_OPENID);
				String tid = paramMap.get(WXAuthContant.KEY_WIFI_TID);
				String extend = paramMap.get(WXAuthContant.KEY_WIFI_EXTEND);
				if(!StringUtils.isEmpty(openid)){
					Cookie cookie = new Cookie(WXAuthContant.KEY_WIFI_OPENID,openid);
					response.addCookie(cookie);
				}
				if(!StringUtils.isEmpty(tid)){
					Cookie cookie = new Cookie(WXAuthContant.KEY_WIFI_TID, tid);
					response.addCookie(cookie);
				}
				if(!StringUtils.isEmpty(extend)){
					Cookie cookie = new Cookie(WXAuthContant.KEY_WIFI_EXTEND, extend);
					response.addCookie(cookie);
				}
			} catch (Exception e) {
				LogUtil.printErrorStackTrace(error, e);
			}
		}
		
		if(!StringUtils.isEmpty(info)){
			response.getWriter().print(info);
		}else if(!StringUtils.isEmpty(redirectUrl)){
			response.sendRedirect(redirectUrl);
		}
	}

	@Override
	public Map<String, String> doWifiReq(Map<String, String> paramMap) {
		Map<String, String> result = null;
		String wxtype = paramMap.get(WXAuthContant.WX_WIFI_REQNAME);
		if(WXReqTypeConstant.WX_REQTYPE_WIFISIGN.equals(wxtype)){
			result = wifisign(paramMap);
		}else if(WXReqTypeConstant.WX_REQTYPE_WIFIAUTH.equals(wxtype)){
			result = wifiauth(paramMap);
		}else if(WXReqTypeConstant.WX_REQTYPE_WIFICHECK.equals(wxtype)){
			result = wificheck(paramMap);
		}else if(WXReqTypeConstant.WX_REQTYPE_WIFIPC.equals(wxtype)){
			result = wifipc(paramMap);
		}else{
			result = new HashMap<String, String>();
			result.put(ParamConstant.KEY_INFO, "不存在该请求类型");
			result.put(ParamConstant.KEY_RESULTCODE, String.valueOf(WXResultConstant.RESULT_REQTYPE_NOTEXIST));
			result.put(ParamConstant.KEY_DESC, "not exist reqname:"+wxtype);
		}
		return result;
	}
	
	/**
	 * wifi签名
	 * @param paramMap
	 * @return
	 */
	private Map<String, String> wifisign(Map<String, String> paramMap){
		Map<String, String> result = new HashMap<String, String>();
		String eccode = paramMap.get(ParamConstant.KEY_ECCODE);
		String usermac = paramMap.get(ParamConstant.KEY_USERMAC);
		String apmac = paramMap.get(ParamConstant.KEY_APMAC);
		String ssid = paramMap.get(ParamConstant.KEY_SSID);
		
		LogObj logObj = new LogObj();
		logObj.putSysKey(LogObj.COLLECT, "y")
				.putSysKey(LogObj.SID, paramMap.get(ParamConstant.KEY_SESSIONID))
				.putSysKey(LogObj.MODULE, "wifisign")
				.putSysKey(LogObj.STEP, "request");
		logObj.putData(ParamConstant.KEY_ECCODE, eccode).putData(ParamConstant.KEY_USERMAC,usermac)
			.putData(ParamConstant.KEY_APMAC,apmac).putData(ParamConstant.KEY_SSID,ssid);
		log.info(logObj);
		
		Map<String, String> resultInfoMap = new HashMap<String, String>();
		int resultcode = 0;
		String desc = "";
		try {
			DEnterpriseRegister der = MPClientUtils.callService(
					"/mp/portal/dEnterpriseRegister/getByEccode",
					new Object[] { eccode },
					DEnterpriseRegister.class);
			if(der == null){
				resultcode = WXResultConstant.RESULT_ENTERPRISE_REGISTER_NOEXIST;
				desc = "DEenterpriseRegister is not exist";
			}else{
                int status = der.getStatus();
                if (status == ParamConstant.KEY_STATUS_PAUSE) {
                    resultcode = WXResultConstant.RESULT_ENTERPRISE_REGISTER_STATUS_PAUSE;
                    desc = "DEenterpriseRegister status is pause";
                } else {
                    Map<String, String> signMap = wxWifiBO.wifisign(paramMap, der);
                    resultInfoMap.putAll(signMap);
                    resultcode = WXResultConstant.RESULT_SUCCESS;
                    desc = "wifi sign success";
                }
			}
		} catch (Exception e) {
			LogUtil.printErrorStackTrace(error, e);
			resultcode = WXResultConstant.RESULT_SYSTEM_EXCEPTION;
			desc = "wifi sign exception";
		}
		resultInfoMap.put("resultcode", String.valueOf(resultcode));
		resultInfoMap.put("desc", desc);
		
		
		String info = JsonUtil.map2json(resultInfoMap);
		result.put(ParamConstant.KEY_INFO, info);
		result.put(ParamConstant.KEY_RESULTCODE, String.valueOf(resultcode));
		result.put(ParamConstant.KEY_DESC, desc);
		logObj.putSysKey(LogObj.STEP, "response");
		logObj.putData("resultcode", resultcode)
		.putData("desc", desc)
		.putData("info", info);
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
	 * 微信连wifi auth
	 * @param paramMap
	 * @return
	 */
	private Map<String, String> wifiauth(Map<String, String> paramMap) {
		Map<String, String> result = new HashMap<String, String>();
		String openid = paramMap.get(WXAuthContant.KEY_WIFI_OPENID);
		String tid = paramMap.get(WXAuthContant.KEY_WIFI_TID);
		String extend = paramMap.get(WXAuthContant.KEY_WIFI_EXTEND);
		
		LogObj logObj = new LogObj();
		logObj.putSysKey(LogObj.COLLECT, "y")
				.putSysKey(LogObj.SID, paramMap.get(ParamConstant.KEY_SESSIONID))
				.putSysKey(LogObj.MODULE, "wifiauth")
				.putSysKey(LogObj.STEP, "request");
		logObj.putData(WXAuthContant.KEY_WIFI_OPENID, openid).putData(WXAuthContant.KEY_WIFI_TID,tid).putData(WXAuthContant.KEY_WIFI_EXTEND,extend);
		log.info(logObj);
		Map<String, String> extendMap = wxWifiBO.parseExtend(extend);
		logObj.putSysKey(LogObj.STEP, "parseExtend");
		logObj.putData("extendMap",extendMap);
		log.info(logObj);
		paramMap.putAll(extendMap);
		int resultcode = 0;
		String desc = "wifi auth success";
		String info = "wifi auth success";
		
		saveWifiDataToMem(paramMap,logObj);
		
		result.put(ParamConstant.KEY_INFO, info);
		result.put(ParamConstant.KEY_RESULTCODE, String.valueOf(resultcode));
		result.put(ParamConstant.KEY_DESC, desc);
		
		logObj.putSysKey(LogObj.STEP, "response");
		logObj.putData("resultcode", resultcode)
		.putData("desc", desc)
		.putData("info", info);
		if (WXResultConstant.RESULT_SUCCESS == resultcode) {
			// 成功
			log.info(logObj);
		} else {
			// 失败
			log.error(logObj);
		}
		return result;
	}

	private Map<String, String> wificheck(Map<String, String> paramMap) {
		Map<String, String> result = new HashMap<String, String>();
		String openid = paramMap.get(WXAuthContant.KEY_WIFI_OPENID);
		String tid = paramMap.get(WXAuthContant.KEY_WIFI_TID);
		String extend = paramMap.get(WXAuthContant.KEY_WIFI_EXTEND);
		
		String c_openid = paramMap.get(WXAuthContant.KEY_WIFI_C_OPENID);
		String c_tid = paramMap.get(WXAuthContant.KEY_WIFI_C_TID);
		String c_extend = paramMap.get(WXAuthContant.KEY_WIFI_C_EXTEND);
		
		LogObj logObj = new LogObj();
		logObj.putSysKey(LogObj.COLLECT, "y")
				.putSysKey(LogObj.SID, paramMap.get(ParamConstant.KEY_SESSIONID))
				.putSysKey(LogObj.MODULE, "wificheck")
				.putSysKey(LogObj.STEP, "request");
		logObj.putData(WXAuthContant.KEY_WIFI_OPENID, openid)
			.putData(WXAuthContant.KEY_WIFI_TID,tid)
			.putData(WXAuthContant.KEY_WIFI_EXTEND,extend)
			.putData(WXAuthContant.KEY_WIFI_C_OPENID, c_openid)
			.putData(WXAuthContant.KEY_WIFI_C_TID,c_tid)
			.putData(WXAuthContant.KEY_WIFI_C_EXTEND,c_extend);
		log.info(logObj);
		
		preReqparam(paramMap, logObj);
		
		openid = paramMap.get(WXAuthContant.KEY_WIFI_OPENID);
		tid = paramMap.get(WXAuthContant.KEY_WIFI_TID);
		extend = paramMap.get(WXAuthContant.KEY_WIFI_EXTEND);
		String eccode = paramMap.get(ParamConstant.KEY_ECCODE);
		
		logObj.putSysKey(LogObj.STEP, "prereqparam");
		logObj.putData(WXAuthContant.KEY_WIFI_OPENID, openid)
			.putData(WXAuthContant.KEY_WIFI_TID,tid)
			.putData(WXAuthContant.KEY_WIFI_EXTEND,extend)
			.putData(ParamConstant.KEY_ECCODE,eccode);
		log.info(logObj);
		
		int resultcode = 0;
		String desc = "";
		
		int replyMsg_code = 0;
		String redirectUrl = "";
		String wxwifi_fail_jsp_url = "";
		String wxverify_verify_jsp_url = "";
		String wxauth_netauth_url = "";
		try {
			wxwifi_fail_jsp_url = MPClientUtils.callService(
					"/mp/portal/config/business/getConfig", new Object[] {
							"wxwifi", "wxwifi_fail_jsp_url" }, String.class);
			wxverify_verify_jsp_url = MPClientUtils.callService(
					"/mp/portal/config/business/getConfig", new Object[] {
							"wxverify", "verify_jsp_url" }, String.class);
			wxauth_netauth_url = MPClientUtils.callService(
					"/mp/portal/config/business/getConfig", new Object[] {
							"wxauth", "netauth_url" }, String.class);
			if(!wxWifiBO.validWificheckreq(paramMap)){
				resultcode = WXResultConstant.RESULT_REQPARAM_ERROR;
				desc = "wifi check req param is error";
				replyMsg_code = WXMsgCodeConstant.WX_WIFI_CODE_PARAMERROR;
				redirectUrl = wxwifi_fail_jsp_url;
			}else{
				DEnterpriseRegister der = MPClientUtils.callService(
						"/mp/portal/dEnterpriseRegister/getByEccode",
						new Object[] { eccode },
						DEnterpriseRegister.class);
				if(der == null){
					resultcode = WXResultConstant.RESULT_ENTERPRISE_REGISTER_NOEXIST;
					desc = "DEenterpriseRegister is not exist";
					replyMsg_code = WXMsgCodeConstant.WX_WIFI_CODE_WECHAT_NOTCONFIGURE;
					redirectUrl = wxwifi_fail_jsp_url;
				}else{
                    int status = der.getStatus();
                    if (status == ParamConstant.KEY_STATUS_PAUSE) {
                        resultcode = WXResultConstant.RESULT_ENTERPRISE_REGISTER_STATUS_PAUSE;
                        desc = "DEenterpriseRegister status is pause";
                        replyMsg_code = WXMsgCodeConstant.WX_WIFI_CODE_STATUS_PAUSE;
                        redirectUrl = wxwifi_fail_jsp_url;
                    } else {
                        BSpecialUser specialUser = specialUserBO.querySpecialUser(openid, eccode);
                        if (specialUser == null) {
                            resultcode = WXResultConstant.RESULT_SPECIALUSER_NOTEXIST;
                            desc = "special user is not exist";
                            replyMsg_code = WXMsgCodeConstant.WX_WIFI_CODE_NOTATTENTION;
                            redirectUrl = wxwifi_fail_jsp_url;
                        } else {
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
				}

			}
		} catch (DAOException e) {
			LogUtil.printErrorStackTrace(error, e);
			resultcode = WXResultConstant.RESULT_OPERATEDB_EXCEPTION;
			desc = "operate db exception:" + e.getMessage();
			replyMsg_code = WXMsgCodeConstant.WX_VERIFY_CODE_SYSTEM_BUSY;
			redirectUrl = wxwifi_fail_jsp_url;
		}  catch (Exception e) {
			LogUtil.printErrorStackTrace(error, e);
			resultcode = WXResultConstant.RESULT_SYSTEM_EXCEPTION;
			desc = "wifi check exception";
			replyMsg_code = WXMsgCodeConstant.WX_WIFI_CODE_EXCEPTION;
			redirectUrl = wxwifi_fail_jsp_url;
		}
		paramMap.put(ParamConstant.KEY_REPLYMSG_CODE,String.valueOf(replyMsg_code));
		
		Map<String,String> finalParamMap = new HashMap<String, String>();
		redirectUrl = wxWifiBO.formRedirectUrl(paramMap, redirectUrl, finalParamMap);
		
		result.put(ParamConstant.KEY_REDIRCTURL, redirectUrl);
		result.put(ParamConstant.KEY_RESULTCODE, String.valueOf(resultcode));
		result.put(ParamConstant.KEY_DESC, desc);
		
		logObj.putSysKey(LogObj.STEP, "response");
		logObj.putData(ParamConstant.KEY_RESULTCODE, resultcode)
		.putData(ParamConstant.KEY_DESC, desc)
		.putData(ParamConstant.KEY_REDIRCTURL, redirectUrl).putData(ParamConstant.KEY_FINALPARAMMAP,finalParamMap);
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
	 * 保存wifidate到内存库中
	 * @param paramMap
	 * @param logObj
	 * @return
	 */
	private boolean saveWifiDataToMem(Map<String, String> paramMap,LogObj logObj){
		String logid = paramMap.get(ParamConstant.KEY_SESSIONID);
		
		String mem_key = wxWifiBO.formWifiMemKey(paramMap.get(ParamConstant.KEY_UNIQUEID));
		if(!StringUtils.isEmpty(mem_key)){
			String mem_value = wxWifiBO.formWifiMemData(paramMap);
			String mem_encry_value = WXUtil.encry(mem_value);
			String mem_expired = MPClientUtils.callService(
					"/mp/portal/config/business/getConfig", new Object[] {
							"wxwifi", "mem_expired" }, String.class);
			long expired_msec = Long.parseLong(mem_expired);
			
			logObj.putSysKey(LogObj.STEP, "memcachedset");
			logObj.putData("mem_value",mem_value)
				.putData("mem_encry_value", mem_encry_value)
				.putData("mem_key",mem_key)
				.putData("expired_msec",expired_msec);
			boolean memcached_result = memCachedBO.set(logid, mem_key, mem_encry_value, expired_msec);
			
			logObj.putData("memcached_result",memcached_result);
			if(memcached_result){
				log.info(logObj);
			}else{
				log.error(logObj);
			}
			return memcached_result;
		}
		return false;
	}
	
	
	/**
	 * 处理请求数据
	 * 1.openid，按照以下3个步骤读取
	 * (1)从url中读取
	 * (2)从cookie读取
	 * (3)从extend中获取唯一码，从内存库中读取
	 * eccode按照以下步骤获取
	 * 1.从url中读取
	 * 2.从extend中读取唯一码，从内存库中读取
	 * 
	 * 另，由于extend数据传递可能有问题，所以从url和cookie中读取extend需要比较进行处理。
	 * @param paramMap
	 * @param logObj
	 */
	private void preReqparam(Map<String, String> paramMap, LogObj logObj) {
		String logid = paramMap.get(ParamConstant.KEY_SESSIONID);

		String openid = paramMap.get(WXAuthContant.KEY_WIFI_OPENID);
		String tid = paramMap.get(WXAuthContant.KEY_WIFI_TID);
		String extend = paramMap.get(WXAuthContant.KEY_WIFI_EXTEND);

		String c_openid = paramMap.get(WXAuthContant.KEY_WIFI_C_OPENID);
		String c_tid = paramMap.get(WXAuthContant.KEY_WIFI_C_TID);
		String c_extend = paramMap.get(WXAuthContant.KEY_WIFI_C_EXTEND);

		if (StringUtils.isEmpty(openid)) {
			openid = c_openid;
			paramMap.put(WXAuthContant.KEY_WIFI_OPENID, openid);
		}

		if (StringUtils.isEmpty(tid)) {
			tid = c_tid;
			paramMap.put(WXAuthContant.KEY_WIFI_TID, tid);
		}

		Map<String, String> extendMap = wxWifiBO.parseExtend(extend);
		if (extendMap == null || extendMap.size() == 0) {
			extendMap = wxWifiBO.parseExtend(c_extend);
			if (extendMap != null && extendMap.size() > 0) {
				paramMap.put(WXAuthContant.KEY_WIFI_EXTEND, c_extend);
			}
		}
		if (extendMap != null && extendMap.size() > 0) {
			SystemUtil.map_put(paramMap, extendMap);
			String uniqueid = extendMap.get(ParamConstant.KEY_UNIQUEID);
			Map<String, String> wifiDataMap = getWifiDataFromMem(logid,
					uniqueid, logObj);
			SystemUtil.map_put(paramMap, wifiDataMap);
		}
	}
	
	/**
	 * 从内存库读取wifi 数据
	 * @param paramMap
	 * @param logObj
	 * @return
	 */
	private Map<String, String> getWifiDataFromMem(String logid,String uniqueid,LogObj logObj){
		Map<String, String> wifiDataMap = null;
		String mem_key = wxWifiBO.formWifiMemKey(uniqueid);
		if(!StringUtils.isEmpty(mem_key)){
			String mem_value = memCachedBO.get(logid, mem_key);
			logObj.putSysKey(LogObj.STEP, "memcachedget");
			logObj.putData("mem_key", mem_key).putData("mem_value",
					mem_value);
			log.info(logObj);
			if(!StringUtils.isEmpty(mem_value)){
				String wifidata = "";
				try {
					wifidata = WXUtil.decry(mem_value);
				} catch (UnsupportedEncodingException e) {
					LogUtil.printErrorStackTrace(error, e);
				}
				wifiDataMap = wxWifiBO.parseMemWifiData(wifidata);
				logObj.putData("mem_decry_value",wifidata).putData("wifiDataMap",wifiDataMap);
				log.info(logObj);
			}
		}else{
			wifiDataMap = new HashMap<String, String>();
		}
		return wifiDataMap;
	}

	/**
	 * 
	 * @Title: wifipc  
	 * @Description: PC端使用微信连Wi-Fi 实现了auth和check
	 * @param @param paramMap
	 * @param @return    参数  
	 * @return Map<String,String>    返回类型  
	 * @throws
	 */
	private Map<String, String> wifipc(Map<String, String> paramMap){
		Map<String, String> result = new HashMap<String, String>();
		String eccode = paramMap.get(ParamConstant.KEY_ECCODE);
		String usermac = paramMap.get(ParamConstant.KEY_USERMAC);
		String apmac = paramMap.get(ParamConstant.KEY_APMAC);
		String ssid = paramMap.get(ParamConstant.KEY_SSID);
		
		LogObj logObj = new LogObj();
		logObj.putSysKey(LogObj.COLLECT, "y")
				.putSysKey(LogObj.SID, paramMap.get(ParamConstant.KEY_SESSIONID))
				.putSysKey(LogObj.MODULE, "wifipc")
				.putSysKey(LogObj.STEP, "request");
		logObj.putData(ParamConstant.KEY_ECCODE, eccode).putData(ParamConstant.KEY_USERMAC,usermac)
			.putData(ParamConstant.KEY_APMAC,apmac).putData(ParamConstant.KEY_SSID,ssid);
		log.info(logObj);
		
		Map<String, String> resultInfoMap = new HashMap<String, String>();
		int resultcode = 0;
		String desc = "";
		try {
			DEnterpriseRegister der = MPClientUtils.callService(
					"/mp/portal/dEnterpriseRegister/getByEccode",
					new Object[] { eccode },
					DEnterpriseRegister.class);
			if(der == null){
				resultcode = WXResultConstant.RESULT_ENTERPRISE_REGISTER_NOEXIST;
				desc = "DEenterpriseRegister is not exist";
			}else{
                int status = der.getStatus();
                if (status == ParamConstant.KEY_STATUS_PAUSE) {
                    resultcode = WXResultConstant.RESULT_ENTERPRISE_REGISTER_STATUS_PAUSE;
                    desc = "DEenterpriseRegister status is pause";
                } else {
                    Map<String, String> pcMap = wxWifiBO.wifiPc(paramMap, der);
                    resultInfoMap.putAll(pcMap);
                    resultcode = WXResultConstant.RESULT_SUCCESS;
                    desc = "wifi pc success";
                }
			}
		} catch (Exception e) {
			LogUtil.printErrorStackTrace(error, e);
			resultcode = WXResultConstant.RESULT_SYSTEM_EXCEPTION;
			desc = "wifi pc exception";
		}
		resultInfoMap.put("resultcode", String.valueOf(resultcode));
		resultInfoMap.put("desc", desc);
		
		
		String info = JsonUtil.map2json(resultInfoMap);
		result.put(ParamConstant.KEY_INFO, info);
		result.put(ParamConstant.KEY_RESULTCODE, String.valueOf(resultcode));
		result.put(ParamConstant.KEY_DESC, desc);
		logObj.putSysKey(LogObj.STEP, "response");
		logObj.putData("resultcode", resultcode)
		.putData("desc", desc)
		.putData("info", info);
		if (WXResultConstant.RESULT_SUCCESS == resultcode) {
			// 成功
			log.info(logObj);
		} else {
			// 失败
			log.error(logObj);
		}
		return result;
	}
	
	public WXWifiBO getWxWifiBO() {
		return wxWifiBO;
	}

	public MemCachedBO getMemCachedBO() {
		return memCachedBO;
	}

	public SpecialUserBO getSpecialUserBO() {
		return specialUserBO;
	}

	public void setWxWifiBO(WXWifiBO wxWifiBO) {
		this.wxWifiBO = wxWifiBO;
	}

	public void setMemCachedBO(MemCachedBO memCachedBO) {
		this.memCachedBO = memCachedBO;
	}

	public void setSpecialUserBO(SpecialUserBO specialUserBO) {
		this.specialUserBO = specialUserBO;
	}
}
