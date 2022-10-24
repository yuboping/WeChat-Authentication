package com.ailk.wxserver.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.axis.MessageContext;
import org.apache.axis.transport.http.HTTPConstants;

import com.ailk.lcims.support.mp.client.MPClientUtils;

public class RequestParamUtil {

	private final static String KEY = "A001024E0405D607C0D1151314F51617202E222F2F2526C7";
	private final static byte[] keybyte=EncodDecodeUtil.getKeyByStr(KEY);
	
	private static final String STYLE_DEFAULT_PREFIX = "/style/default";
	
	private static final String SPLIT_SIGN = "|||";
	
	private static final String SPLIT_SIGN_KV = "=";
	
	private static final String URL_ENC = "UTF-8"; //字符编码名称
	
	/**
	 * Map值域转成String , 字符串des加密、url编码
	 * href末尾带参数时，需要加密
	 * 例如：Map(a=A,b=B)
	 * String : a=A|||b=B
	 * @param paramMap
	 * @return
	 */
	public static String MapValueToString(Map<String,String> paramMap){
		String retStr = MapValueToStringWithoutUrlEnc(paramMap);
		//url编码
		return getUrlEncStr(retStr);
	}
	
	/**
	 * Map值域转成String , 字符串des加密,不使用url编码
	 * 设置隐藏域是不需要urlencode
	 * 例如：Map(a=A,b=B)
	 * String : a=A|||b=B
	 * @param paramMap
	 * @return
	 */
	public static String MapValueToStringWithoutUrlEnc(Map<String,String> paramMap){
		String retStr = null;
		//map to string
		String str = getStringFromMap(paramMap);
		if(str != null && !str.trim().equals("")){
			//DES加密
			retStr = getEncoderStr(str);
		}
		return retStr;
	}
	
	/**
	 * String 转成HashMap 不需要url解码
	 * String : a=A|||b=B  ===》Map(a=A,b=B)
	 * @param paramStr
	 * @return
	 */
	public static Map<String,String> StringValueToMap(String paramStr){
		String decStr = null;
		if(paramStr !=null && !paramStr.trim().equals("")){
			//des解密
			decStr = getDecoderStr(paramStr);		
		}
		//string to map
		return getMapFromString(decStr);
	}
	
	/**
	 * String 转成HashMap 需要URLDecoder解码
	 * String : a=A|||b=B  ===》Map(a=A,b=B)
	 * @param paramStr
	 * @return
	 */
	public static Map<String,String> StringValueToMapOnUrlDec(String paramStr){
		//url解码
		String str = getUrlDecStr(paramStr);
		return  StringValueToMap(str);
	}
	
	/**
	 * 根据提供的keys 移除Map中的key-value域值，并转成加密经编码的String串
	 * @param paramMap
	 * @param keys  需移除的Key集合
	 * @return
	 */
	public static String removeParamStrByMapKey(Map<String,String> paramMap,String[] keys){
		String str="";
		if(keys !=null && keys.length!=0){
			for(int i=0;i<keys.length;i++){
				String key = keys[i];
				if(key !=null && !key.equals("")){
					paramMap.remove(key);
				}
			}
		}
		str = MapValueToString(paramMap);
		return str;
	}
	
	/**
	 * 根据配置 保留需要的参数
	 * @param paramMap
	 * @param keyName 指定保留哪些参数:preindex_key|auth_key|logout_key
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Map<String,String> removeParamStrUnusedByKeyName(Map<String,String> paramMap,String keyName){
		List<String> tempList = MPClientUtils.callService("/mp/portal/config/business/getConfigValues",
				new Object[]{"output_paraminfo",keyName},List.class);
		Map<String,String> returnMap=new HashMap<String,String>();
		for (String str:tempList) {
			String propValue = paramMap.get(str);
			if(propValue !=null && !propValue.equals("")){
				returnMap.put(str, propValue);
			}
		}
		return returnMap;
	}
	
	/**
	 * 获取指定方案下 指定的页面路径，并可带url参数
	 * @param styleUrl 参数中保存的路径 不包含最后的jsp页面 例如/style/default
	 * @param appiontedStr
	 * @param urlParams
	 * @return  以“/”开头的路径
	 */
	public static String getAppointedUrlByStyle(String styleUrl,String appiontedStr,String urlParam){
		String url="";
		if(urlParam!=null){
			urlParam="?"+urlParam;
		}else{
			urlParam="";
		}
		if(styleUrl==null || styleUrl.equals("")){
			styleUrl = STYLE_DEFAULT_PREFIX;
		}
		url = styleUrl+"/"+appiontedStr+urlParam;
		return url;
	}
	
	/**
	 * 从request和param中获取语言，当没有语言时，设置默认cn；当语言改变时，修改参数的值
	 */
	public static String getLanguage(HttpServletRequest request,Map<String,String> paramMap){
		String language=paramMap.get("language");
		
		String tempLanguage=request.getParameter("language");
		if(tempLanguage!=null){
			language=tempLanguage;
			paramMap.put("language", tempLanguage);
			return language;
		}
		
		if(language == null){
			language="cn";
			paramMap.put("language", language);
		}
		return language;
		
	}
	/**
	 * 获取方案路径 authtype处理特殊方案
	 */
	public static String getStyleUrl(Map<String,String> paramMap){
		String styleUrl=(String)paramMap.get("StyleUrl");
		String authtype=(String)paramMap.get("authtype");
		if(authtype!=null){
			try {
			styleUrl = MPClientUtils.callService("/mp/portal/config/business/getConfig",	new Object[]{"authtype","roam"}, String.class);
			} catch (Exception e) {
				styleUrl="/internationalroam";
			}
			return styleUrl;
		}
		
		if(styleUrl == null){
			styleUrl = STYLE_DEFAULT_PREFIX;
			paramMap.put("StyleUrl", styleUrl);
		}
		return styleUrl;
		
	}
	
	
	/**
	 * 给字符串URLEncoder编码
	 * @param str
	 * @return
	 */
	public static String getUrlEncStr(String str){
		try {
			if(str==null){
				str="";
			}else{			
				str =URLEncoder.encode(str, URL_ENC);
			}
		} catch (UnsupportedEncodingException e) {
			str="";
		}
		return str;
	}
	
	/**
	 * 给字符串URLDecoder解码
	 * @param str
	 * @return
	 */
	public static String getUrlDecStr(String str){
		try {
			if(str==null){
				str="";
			}else{
				str = URLDecoder.decode(str, URL_ENC);
			}
		} catch (UnsupportedEncodingException e) {
			str="";
		}
		return str;
	}
	
	/**
	 * 国际漫游 获取认证路径
	 * @param request
	 * @return
	 */
	public static String getReturnURL(HttpServletRequest request){
		String ReturnURL = request.getParameter("ReturnURL");
		if(ReturnURL==null||ReturnURL.equals("")){
			try {
				ReturnURL=MPClientUtils.callService("/mp/portal/config/business/geConfig",	new Object[]{"send_param","auth_url"}, String.class);
			} catch (Exception e) {
				ReturnURL="/authServlet";
			}
			//ReturnURL = ReturnURL + "?authtype=roam";
		}
		
		return ReturnURL;
	}
	
	/**
	 * 根据str获取配置URL路径
	 * @param str auth_url/logout_url/index_url
	 * @return
	 */
	public static String getSendURLByKey(String str){
		String url = "";
		if(str !=null){
			try {
				url = MPClientUtils.callService("/mp/portal/config/business/getConfig",	new Object[]{"send_param",str}, String.class);
			} catch (Exception e) {
				url ="";
			}
		}
		return url;
	}
	
	/**
	 * 判断路径是否以“/”开头
	 * @param str
	 * @return  true:以"/"开头，false:非"/"开头
	 */
	public static boolean isLocalUrl(String str){
		
		if(str!=null&&str.startsWith("/")){
			return true;
		}
		return false;
	}
	
	/**
	 * 从request请求中获取所有参数装入hashMap中
	 * 需要特殊处理的参数：paramStr/language
	 * @param request  
	 * @param urlDecFlag true:需要URLDecoder ; false:不需要URLDecoder
	 * @return HashMap
	 */
	public static Map<String,String> getRequestParameters(HttpServletRequest request,boolean urlDecFlag){
		Map<String,String> reqMaps = new HashMap<String,String>();
		Enumeration<String> enu = request.getParameterNames();
		String paramStr = request.getParameter("paramStr");
		if(paramStr != null){
			if(!urlDecFlag){//未编码
				StringValueToMap(paramStr,reqMaps);
			}else{//已编码
				StringValueToMapWithURLDecoder(paramStr,reqMaps);
			}
		}
		String language = request.getParameter("language");
		if(language != null){
			reqMaps.put("language", language);
		}else{
			language = reqMaps.get("language");
			if(language == null){
				reqMaps.put("language", "cn");
			}
		}
		while (enu.hasMoreElements()) 
		{   //先处理paramStr
			String pName = (String)enu.nextElement();
			if(pName.equals("paramStr")){
				
			}else if(pName.equals("language")){
				
			}else{
				//最后处理其他参数
				String value = request.getParameter(pName);
				if(value !=null && !value.equals("")){
					reqMaps.put(pName, value);
				}
			}
		} 
		
		//accessIp
		String accessIp = (String)reqMaps.get("accessIp");
		String reqip=request.getRemoteAddr();
		if(accessIp ==null || !accessIp.equals(reqip)){
			reqMaps.put("accessIp", reqip);
		}
		
		//sessionid
		String sessionid = (String)reqMaps.get("sessionid");
		if(sessionid==null||"".equals(sessionid)){
			int sid = (int) (Math.random() * Integer.MAX_VALUE);
			reqMaps.put("sessionid", String.valueOf(sid));
		}
		
		//User-Agent
		String userAgent = (String) request.getHeader("User-Agent");
		reqMaps.put("useragent", userAgent);
		
			
		
		return reqMaps;
	}
	
	/**
	 * Str串解析成Map 装入参数returnMap中，不需要url解码
	 * @param str
	 * @param reqMap
	 * @return
	 */
	public static void StringValueToMap(String paramStr,Map<String,String> reqMap){
		if(paramStr !=null && !paramStr.trim().equals("")){
			//des解密
			String decStr=getDecoderStr(paramStr);
			
			String[] arr = StringUtils.split(decStr, SPLIT_SIGN);
			for(int i=0;i<arr.length;i++){
				String[] arrTmp = arr[i].split(SPLIT_SIGN_KV);
				String key =arrTmp[0];
				String value ="";
                if(arrTmp.length == 2){
                	value = arrTmp[1];
				}
                reqMap.put(key, value);
			}
		}
	}
	/**
	 * Str串解析成Map 装入参数returnMap中,需要url解码
	 * @param str
	 * @param reqMap
	 * @return
	 */
	public static void StringValueToMapWithURLDecoder(String paramStr,Map<String,String> reqMap){
		//url解码
		String str = getUrlDecStr(paramStr);
		if(str !=null && !str.trim().equals("")){
			//des解密
			String decStr= getDecoderStr(str);
			
			String[] arr = StringUtils.split(decStr, SPLIT_SIGN);
			for(int i=0;i<arr.length;i++){
				String[] arrTmp = arr[i].split(SPLIT_SIGN_KV);
				String key =arrTmp[0];
				String value ="";
                if(arrTmp.length == 2){
                	value = arrTmp[1];
				}
                reqMap.put(key, value);
			}
		}
	}
	
	/**
	 * Map{A=a,B=b}解析成字符串A=a|||B=b
	 * @param paramMap
	 * @return
	 */
	public static String getStringFromMap(Map<String,String> paramMap){
		String str = null;
		StringBuffer tmpSb = new StringBuffer();
		for(Entry<String,String> entry:paramMap.entrySet()){
			String key = entry.getKey();
			String value = entry.getValue();
			tmpSb.append(key).append(SPLIT_SIGN_KV).append(value).append(SPLIT_SIGN);
		}
		str = tmpSb.toString();
		if(str !=null && !str.trim().equals("")){
			str=str.substring(0, str.lastIndexOf(SPLIT_SIGN));
		}
		return str;
	}
	
	/**
	 * 字符串A=a|||B=b转成Map{A=a,B=b}
	 * @param str
	 * @return
	 */
	public static Map<String,String> getMapFromString(String str){
		Map<String,String> map =new HashMap<String,String>();
		if(str != null && !str.trim().equals("")){
			String[] arr = StringUtils.split(str, SPLIT_SIGN);
			for(int i=0;i<arr.length;i++){
				String[] arrTmp = arr[i].split(SPLIT_SIGN_KV);
				String key =arrTmp[0];
				String value ="";
	            if(arrTmp.length == 2){
	            	value = arrTmp[1];
				}
				map.put(key, value);
			}
		}
		return map;
	}
	
	/**
	 * 字符串DES加密 不判空
	 * @param str
	 * @return
	 */
	public static String getEncoderStr(String str){
//		ThreeDes des = new ThreeDes();
//		des.getKey(KEY);
		
		String encStr=EncodDecodeUtil.Base64EncodeNoEnter(EncodDecodeUtil.encryptMode(keybyte, str.getBytes()));
		return encStr;
	}
	
	/**
	 * 字符串DES解密 不判空
	 * @param str
	 * @return
	 */
	public static String getDecoderStr(String str){
//		ThreeDes des = new ThreeDes();
//		des.getKey(KEY);
		
		String decStr=new String(EncodDecodeUtil.decryptMode(keybyte, EncodDecodeUtil.Base64DecodeNoEnter(str)));
		return decStr;
	}
	
	 /**
     * 获取请求ip
     * @return
     */
    public static String getRequestIp() {
		MessageContext mc = MessageContext.getCurrentContext();
		HttpServletRequest httpReq = (HttpServletRequest) mc
				.getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST);
		String requestIP = httpReq.getRemoteAddr();
		return requestIP;
	}
    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	}

}
