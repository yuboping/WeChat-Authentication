package com.ailk.wxserver.service.base.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.ailk.lcims.support.mp.client.MPClientUtils;
import com.ailk.wxserver.service.base.interfaces.WXNetAuthBO;
import com.ailk.wxserver.service.constant.CommonConstant;
import com.ailk.wxserver.service.constant.ParamConstant;
import com.ailk.wxserver.util.FormatUtil;
import com.ailk.wxserver.util.LogUtil;
import com.ailk.wxserver.util.RequestParamUtil;
import com.ailk.wxserver.util.StringUtils;
import com.ailk.wxserver.util.Validate;
import com.ailk.wxserver.util.log.LogFactory;
import com.ailk.wxserver.util.log.LogObj;

/**
 * @author wj
 *
 */
/**
 * @author wj
 *
 */
public class WXNetAuthBOImpl implements WXNetAuthBO {

	private Logger error = LogFactory.getLogger("error");
	private Logger info = LogFactory.getLogger("wxnetauthoperate");
	
	@Override
	public boolean validNetAuthReq(Map<String, String> paramMap) {
		String eccode = paramMap.get(ParamConstant.KEY_ECCODE);
		String openid = paramMap.get(ParamConstant.KEY_WX_OPENID);
		String timestamp = paramMap.get(ParamConstant.KEY_TIMESTAMP);

		if (StringUtils.isEmpty(eccode)) {
			return false;
		}
		if (StringUtils.isEmpty(openid)) {
			return false;
		}
		if (StringUtils.isEmpty(timestamp)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean checkNetAuthTimestamp(String timestamp) throws Exception {
		String timestamp_valid = MPClientUtils.callService(
				"/mp/portal/config/business/getConfig", new Object[] {
						"wxauth", "timestamp_valid" }, String.class);
		long expired_msec = Long.parseLong(timestamp_valid);

		if (!Validate.validateTimestamp(expired_msec, timestamp)) {
			return false;
		}
		return true;
	}

	@Override
	public String formRedirectUrl(Map<String, String> paramMap,
			String redirectUrl, Map<String, String> finalParamMap) {
		
		LogObj logObj = new LogObj();
		logObj.putSysKey(LogObj.COLLECT, "y")
				.putSysKey(LogObj.MODULE, "wxnetauth formRedirectUrl")
				.putSysKey(LogObj.STEP, "response");
		logObj.putData("paramMap", paramMap);
		info.info(logObj);
		String eccode = paramMap.get(ParamConstant.KEY_ECCODE);
		String openid = paramMap.get(ParamConstant.KEY_WX_OPENID);
		String replyMsg_code = paramMap.get(ParamConstant.KEY_REPLYMSG_CODE);
		String authcode = paramMap.get(ParamConstant.KEY_AUTHCODE);
		finalParamMap.put(ParamConstant.KEY_REPLYMSG_CODE,
				String.valueOf(replyMsg_code));
		finalParamMap.put(ParamConstant.KEY_ECCODE, eccode);
		finalParamMap.put(ParamConstant.KEY_WX_OPENID, openid);
		finalParamMap.put(ParamConstant.KEY_SESSIONID,
				paramMap.get(ParamConstant.KEY_SESSIONID));
		finalParamMap.put(ParamConstant.KEY_TIMESTAMP, FormatUtil.dateToTimestamp(new Date()));
		String paramStr = RequestParamUtil.MapValueToString(finalParamMap);
		Map<String, String> placeMap = new HashMap<String, String>();
		placeMap.put(CommonConstant.PLACE_PARAMSTR, paramStr);
		
		if(StringUtils.isEmpty(eccode)){
			placeMap.put(CommonConstant.PLACE_ECCODE, "default");
		}else{
			placeMap.put(CommonConstant.PLACE_ECCODE, eccode);
		}
		try {
			/**
			 * 通过微信认证参数接口，获得认证跳转url时候，需要的参数信息
			 */
			//post的请求url地址
			String authcode_param_url = MPClientUtils.callService(
					"/mp/portal/config/business/getConfig", new Object[] {
							"wxauth", "authcode_param_url" }, String.class);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			String sernoStr = sdf.format(new Date())+getSixRandomNumber();
			
			NameValuePair wxreqtype=new NameValuePair("wxreqtype", "wxauthparam");
	        NameValuePair serno=new NameValuePair("serno", sernoStr);
	        NameValuePair _authcode=new NameValuePair("authcode", authcode);
	        NameValuePair[] data = {wxreqtype,serno,_authcode}; 
	        
	        info.info("请求微信认证参数接口，post请求调用，参数:authcode_param_url is: "+authcode_param_url
	        		+" sernoStr is: " + sernoStr + " authcode is : " + authcode);
	        
			String paramsStr = methodPost(authcode_param_url,data);
			 info.info("请求微信认证参数接口，post请求调用返回结果："+paramsStr);
			JSONObject returnJson = new JSONObject(paramsStr);
			
			if("0".equals(returnJson.get("resultcode"))){
				String token = returnJson.getString("token");
				placeMap.put(CommonConstant.NEW_PLACE_AUTHCODE, token + "@@@" +sernoStr);
			}
			redirectUrl = StringUtils.placeHolderReform(1, redirectUrl, placeMap);
		} catch (Exception e) {
			LogUtil.printErrorStackTrace(error, e);
			error.error(e.getMessage());
		}
		return redirectUrl;
	}

	@Override
	public String formNetAuthUrl(Map<String, String> finalParamMap) {
		
		String wxauth_netauth_url = MPClientUtils.callService(
				"/mp/portal/config/business/getConfig", new Object[] {
						"wxauth", "netauth_url" }, String.class);
		
		String paramStr = RequestParamUtil.MapValueToString(finalParamMap);
		Map<String, String> placeMap = new HashMap<String, String>();
		placeMap.put(CommonConstant.PLACE_PARAMSTR, paramStr);
		String netauthUrl = wxauth_netauth_url;
		try {
			netauthUrl = StringUtils.placeHolderReform(1, netauthUrl, placeMap);
		} catch (Exception e) {
			LogUtil.printErrorStackTrace(error, e);
		}
		return netauthUrl;
	}
	
	
	
	 /**
	 * @param url
	 * @param data
	 * @return
	 */
	private String methodPost(String url,NameValuePair[] data){  
	        String response= "";//要返回的response信息  
	        HttpClient httpClient = new HttpClient();  
	        PostMethod postMethod = new PostMethod(url);  
	        // 将表单的值放入postMethod中  
	        postMethod.setRequestBody(data);  
	        // 执行postMethod  
	        int statusCode = 0;  
	        try {  
	            statusCode = httpClient.executeMethod(postMethod);  
	        } catch (HttpException e) {  
	            e.printStackTrace();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	        // HttpClient对于要求接受后继服务的请求，象POST和PUT等不能自动处理转发  
	        // 301或者302  
	        if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY  
	                || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {  
	            // 从头中取出转向的地址  
	            Header locationHeader = postMethod.getResponseHeader("location");  
	            String location = null;  
	            if (locationHeader != null) {  
	                location = locationHeader.getValue();  
	                System.out.println("The page was redirected to:" + location);  
	                response= methodPost(location,data);//用跳转后的页面重新请求。  
	            } else {  
	                System.err.println("Location field value is null.");  
	            }  
	        } else {  
	            System.out.println(postMethod.getStatusLine());  
	  
	            try {  
	                response= postMethod.getResponseBodyAsString();  
	            } catch (IOException e) {  
	                e.printStackTrace();  
	            }  
	            postMethod.releaseConnection();  
	        }  
	        return response;  
	    }
	
	
	/**
	 * @description 生产一个六位的随机数返回
	 * @return String
	 */
	private String getSixRandomNumber(){
		String str = "";
		for(int i=0;i<6;i++){
			int j=(int)(Math.random()*10);
			str += j;
		}
		return str;
	}
	
	
//	public static void main(String args[]) {
//		String authcode_param_url = MPClientUtils.callService(
//				"/mp/portal/config/business/getConfig", new Object[] {
//						"wxauth", "authcode_param_url" }, String.class);
//		
//		NameValuePair wxreqtype = new NameValuePair("wxreqtype", "wxauthparam");
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
//		String sernoStr = sdf.format(new Date()) + getSixRandomNumber();
//		NameValuePair serno = new NameValuePair("serno", sernoStr);
//		String authcode = "NjAzN2VhZWYwYzI3NmMzMDM4MjFkMTA0ZDkwMmQ2NzkzMDU4Y2ZhZDVlMmViOGVmOGE0NjJiY2EzODNjYmU5NzQyNjA0ZTYwYTkzMWYzOWMxZGQ5ZWQ4NWZlMWQ0NWY0YmY3ODFkYTk3NWI2Yzc0OTUzNWI0OGU3YmU2MTIwYjIyY2YwNjIwMzVlYTc5YTlmYjNhZTM3NDRiMmE0NDRmNw==";
//		NameValuePair _authcode = new NameValuePair("authcode", authcode);
//		NameValuePair[] data = { wxreqtype, serno, _authcode };
//		System.out.println(authcode_param_url);
//		String paramsStr = methodPost(authcode_param_url, data);
//		JSONObject returnJson = new JSONObject(paramsStr);
//		if ("0".equals(returnJson.get("resultcode"))) {
//			System.out.println(111);
//		}
//	}
	
	


}
