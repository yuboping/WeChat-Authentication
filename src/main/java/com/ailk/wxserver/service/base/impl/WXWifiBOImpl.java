package com.ailk.wxserver.service.base.impl;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ailk.lcims.support.mp.client.MPClientUtils;
import com.ailk.lcims.support.util.password.Password;
import com.ailk.wxserver.po.DEnterpriseRegister;
import com.ailk.wxserver.service.base.interfaces.WXWifiBO;
import com.ailk.wxserver.service.constant.CommonConstant;
import com.ailk.wxserver.service.constant.ParamConstant;
import com.ailk.wxserver.service.constant.WXAuthContant;
import com.ailk.wxserver.service.constant.WXReqTypeConstant;
import com.ailk.wxserver.util.FormatUtil;
import com.ailk.wxserver.util.LogUtil;
import com.ailk.wxserver.util.RequestParamUtil;
import com.ailk.wxserver.util.StringUtils;
import com.ailk.wxserver.util.log.LogFactory;
import com.ailk.wxserver.util.md5.MD5;

public class WXWifiBOImpl implements WXWifiBO {

	private Logger error = LogFactory.getLogger("error");
	
	private String SPLIT = ",";
	
	@Override
	public String formExtend(Map<String, String> paramMap) {
		String eccode = paramMap.get(ParamConstant.KEY_ECCODE);
		String usermac = paramMap.get(ParamConstant.KEY_USERMAC);
		if(StringUtils.isEmpty(eccode)){
			eccode = "";
		}
		if(StringUtils.isEmpty(usermac)){
			usermac = "";
		}
		usermac = usermac.replaceAll("[:-]", "");
		usermac = usermac.toLowerCase();
		
		String uniqueid = formUniqueID(eccode, usermac);
		
		paramMap.put(ParamConstant.KEY_UNIQUEID, uniqueid);
		
		//用逗号分隔
		String extend = eccode + SPLIT +uniqueid;
		
		return extend;
	}

	/**
	 * 生成
	 * @param eccode
	 * @param usermac
	 * @return
	 */
	private String formUniqueID(String eccode, String usermac) {
		String uniqueid = eccode + "."+ usermac + "." +FormatUtil.formatDate("yyyyMMddHHmmssSSS")+"."+ StringUtils.getRandomString(6);
		uniqueid = Password.encryptPassword(uniqueid, 5);
		return uniqueid;
	}

	@Override
	public Map<String, String> parseExtend(String extend) {
		Map<String, String> extendMap = new HashMap<String, String>();
		if(!StringUtils.isEmpty(extend)){
			String[] fields = extend.split(SPLIT);
			if(fields != null && fields.length >= 2){
				String e_eccode = fields[0].trim();
				String e_uniqueid = fields[1].trim();
				
				extendMap.put(ParamConstant.KEY_ECCODE, e_eccode);
				extendMap.put(ParamConstant.KEY_UNIQUEID, e_uniqueid);
			}
		}
		return extendMap;
	}
	
	@Override
	public boolean isReadCookie(Map<String, String> paramMap) {
		boolean readCookieFlag = false;
		String wxtype = paramMap.get(WXAuthContant.WX_WIFI_REQNAME);
		if(WXReqTypeConstant.WX_REQTYPE_WIFISIGN.equals(wxtype)){
		}else if(WXReqTypeConstant.WX_REQTYPE_WIFIAUTH.equals(wxtype)){
		}else if(WXReqTypeConstant.WX_REQTYPE_WIFICHECK.equals(wxtype)){
			readCookieFlag = true;
		}else{
		}
		return readCookieFlag;
	}

	@Override
	public boolean isWriteCookie(Map<String, String> paramMap) {
		boolean writeCookieFlag = false;
		String wxtype = paramMap.get(WXAuthContant.WX_WIFI_REQNAME);
		if(WXReqTypeConstant.WX_REQTYPE_WIFISIGN.equals(wxtype)){
		}else if(WXReqTypeConstant.WX_REQTYPE_WIFIAUTH.equals(wxtype)){
			writeCookieFlag = true;
		}else if(WXReqTypeConstant.WX_REQTYPE_WIFICHECK.equals(wxtype)){
		}else{
		}
		return writeCookieFlag;
	}

	@Override
	public Map<String, String> wifisign(Map<String, String> paramMap,DEnterpriseRegister der) throws NoSuchAlgorithmException{
		Map<String, String> signMap = new HashMap<String, String>();
		String usermac = paramMap.get(ParamConstant.KEY_USERMAC);
		String apmac = paramMap.get(ParamConstant.KEY_APMAC);
		String ssid = paramMap.get(ParamConstant.KEY_SSID);
		
		String sign = "";
		String appid = der.getAppid();
		if(StringUtils.isEmpty(appid)){
			appid = "";
		}
		String extend = formExtend(paramMap);
		long timestamp = new Date().getTime();
		String shopId = der.getShopid();
		if(StringUtils.isEmpty(shopId)){
			shopId = "";
		}
		if(StringUtils.isEmpty(ssid)){
			ssid = der.getSsid();
		}
		if(StringUtils.isEmpty(ssid)){
			ssid = "";
		}
		
		String bssid = apmac;
		if(StringUtils.isEmpty(bssid)){
			bssid = der.getBssid();
		}
		if(StringUtils.isEmpty(bssid)){
			bssid = "";
		}
		bssid = bssid.replaceAll("-", ":");
		bssid = bssid.toLowerCase();
		
		String secretKey = der.getSecretkey();
		if(StringUtils.isEmpty(secretKey)){
			secretKey = "";
		}
		if(StringUtils.isEmpty(usermac)){
			usermac="";
		}
		usermac = usermac.replaceAll("-", ":");
		usermac = usermac.toLowerCase();
		
		String authUrl = MPClientUtils.callService(
				"/mp/portal/config/business/getConfig", new Object[] {
						"wxwifi", "wxwifi_auth_url" }, String.class);
		
		String soruce = appid + extend + timestamp + shopId + authUrl + usermac
				+ ssid + bssid + secretKey;
		sign = MD5.sign(soruce);
		signMap.put("appid", appid);
		signMap.put("extend", extend);
		signMap.put("timestamp", String.valueOf(timestamp));
		signMap.put("shopId", shopId);
		signMap.put("authUrl", authUrl);
		signMap.put("usermac", usermac);
		signMap.put("ssid", ssid);
		signMap.put("bssid", bssid);
		signMap.put("sign", sign);
		return signMap;
	}

	@Override
	public String formWifiMemKey(String uniqueid){
		String mem_key = "";
		if(!StringUtils.isEmpty(uniqueid)){
			mem_key = CommonConstant.MEM_KEY_PREFIX_WIFI+"."+uniqueid;
		}
		return mem_key;
	}

	@Override
	public String formWifiMemData(Map<String, String> paramMap) {
		String eccode = paramMap.get(ParamConstant.KEY_ECCODE);
		String openid = paramMap.get(WXAuthContant.KEY_WIFI_OPENID);
		String mem_value = ParamConstant.KEY_ECCODE+"="+eccode+SPLIT+WXAuthContant.KEY_WIFI_OPENID+"="+openid;
		return mem_value;
	}

	@Override
	public Map<String, String> parseMemWifiData(String wifidata) {
		Map<String, String> wifiDataMap = new HashMap<String, String>();
		if (StringUtils.isEmpty(wifidata)) {
			return wifiDataMap;
		}
		String[] fields = wifidata.split(SPLIT);
		for (String field : fields) {
			String key = field.split("=")[0];
			String value = field.split("=")[1];
			wifiDataMap.put(key, value);
		}
		return wifiDataMap;

	}

	@Override
	public boolean validWificheckreq(Map<String, String> paramMap){
		String openid = paramMap.get(WXAuthContant.KEY_WIFI_OPENID);
		String extend = paramMap.get(WXAuthContant.KEY_WIFI_EXTEND);
		if(StringUtils.isEmpty(openid)){
			return false;
		}
		if(StringUtils.isEmpty(extend)){
			return false;
		}
		return true;
	}

	@Override
	public String formRedirectUrl(Map<String, String> paramMap, String redirectUrl,Map<String, String> finalParamMap) {
		String eccode = paramMap.get(ParamConstant.KEY_ECCODE);
		String openid = paramMap.get(WXAuthContant.KEY_WIFI_OPENID);
		String replyMsg_code = paramMap.get(ParamConstant.KEY_REPLYMSG_CODE);
		
		finalParamMap.put(ParamConstant.KEY_REPLYMSG_CODE, String.valueOf(replyMsg_code));
		finalParamMap.put(ParamConstant.KEY_ECCODE, eccode);
		finalParamMap.put(ParamConstant.KEY_WX_OPENID, openid);
		finalParamMap.put(ParamConstant.KEY_SESSIONID, paramMap.get(ParamConstant.KEY_SESSIONID));
		finalParamMap.put(ParamConstant.KEY_TIMESTAMP, FormatUtil.dateToTimestamp(new Date()));
		DEnterpriseRegister der = MPClientUtils.callService(
				"/mp/portal/dEnterpriseRegister/getByEccode",
				new Object[] { eccode },
				DEnterpriseRegister.class);
		if(der != null){
            int status = der.getStatus();
            if (status != ParamConstant.KEY_STATUS_PAUSE) {
                finalParamMap.put(ParamConstant.KEY_REGISTERID, der.getRegisterid());
            }
		}
		
		String paramStr = RequestParamUtil.MapValueToString(finalParamMap);
		Map<String, String> placeMap = new HashMap<String, String>();
		placeMap.put(CommonConstant.PLACE_PARAMSTR, paramStr);
		if(StringUtils.isEmpty(eccode)){
			placeMap.put(CommonConstant.PLACE_ECCODE, "default");
		}else{
			placeMap.put(CommonConstant.PLACE_ECCODE, eccode);
		}
		try {
			redirectUrl = StringUtils.placeHolderReform(1, redirectUrl, placeMap);
		} catch (Exception e) {
			LogUtil.printErrorStackTrace(error, e);
		}
		return redirectUrl;
	}

	@Override
	public Map<String, String> wifiPc(Map<String, String> paramMap,
			DEnterpriseRegister der) throws NoSuchAlgorithmException {
		Map<String, String> pcMap = new HashMap<String, String>();
		String apmac = paramMap.get(ParamConstant.KEY_APMAC);
		
		String appid = der.getAppid();
		if(StringUtils.isEmpty(appid)){
			appid = "";
		}
		String extend = formExtend(paramMap);
		String shopId = der.getShopid();
		if(StringUtils.isEmpty(shopId)){
			shopId = "";
		}
		
		String bssid = apmac;
		if(StringUtils.isEmpty(bssid)){
			bssid = der.getBssid();
		}
		if(StringUtils.isEmpty(bssid)){
			bssid = "";
		}
		bssid = bssid.replaceAll("-", ":");
		bssid = bssid.toLowerCase();
		
		
		String authUrl = MPClientUtils.callService(
				"/mp/portal/config/business/getConfig", new Object[] {
						"wxwifi", "wxwifi_check_url" }, String.class);
		
		pcMap.put("appid", appid);
		pcMap.put("shopId", shopId);
		pcMap.put("extend", extend);
		pcMap.put("authUrl", authUrl);
		pcMap.put("bssid", bssid);
		return pcMap;
	}
}
