package com.ailk.wxserver.util.qq.wx;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.ailk.lcims.support.mp.client.MPClientUtils;
import com.ailk.lcims.support.util.security.des3.Des3;
import com.ailk.wxserver.service.constant.SpecialAuthConstant;
import com.ailk.wxserver.service.constant.WXAuthContant;
import com.ailk.wxserver.util.EncodDecodeUtil;
import com.ailk.wxserver.util.FormatUtil;

public class WXUtil {

	private static final String SPLIT = "|||";
	
	/**
	 * 构造验证数据
	 * @param eccode
	 * @param openid
	 * @param phone
	 * @return
	 */
	public static String formVerifyData(String eccode,String openid,String registerid,String phone){
		StringBuffer sb = new StringBuffer();
		sb.append(eccode);
		sb.append(SPLIT);
		sb.append(openid);
		sb.append(SPLIT);
		sb.append(registerid);
		sb.append(SPLIT);
		sb.append(SpecialAuthConstant.APPTYPE_WX_VALUE);
		sb.append(SPLIT);
		sb.append(phone);
		sb.append(SPLIT);
		sb.append(FormatUtil.dateToTimestamp(new Date()));
		sb.append(SPLIT);
		sb.append("end");
		return sb.toString();
	}
	
	/**
	 * 从验证数据解析
	 * @param data
	 * @return
	 */
	public static Map<String,String> parseVerifyData(String data){
		Map<String, String> verifyMap = new HashMap<String, String>();
		String[] values = data.split("\\|\\|\\|");
		if(values.length >=5){
			verifyMap.put(WXAuthContant.KEY_VERIFY_ECCODE, values[0]);
			verifyMap.put(WXAuthContant.KEY_VERIFY_OPENID, values[1]);
			verifyMap.put(WXAuthContant.KEY_REGISTER_ID, values[2]);
			verifyMap.put(WXAuthContant.KEY_VERIFY_TYPE, values[3]);
			verifyMap.put(WXAuthContant.KEY_VERIFY_PHONE, values[4]);
			verifyMap.put(WXAuthContant.KEY_VERIFY_TIMESTAMP, values[5]);
		}
		return verifyMap;
	}
	
	public static String encry(String data){
		String encrypt = Des3.encrypt(data);
		encrypt = EncodDecodeUtil.Base64EncodeNoEnter(encrypt
				.getBytes());
		return encrypt;
	}
	
	public static String decry(String data) throws UnsupportedEncodingException{
		if(data == null || data.trim().equals("")){
			return "";
		}
		byte[] c = EncodDecodeUtil.Base64DecodeNoEnter(data);
		String decrypt = Des3.decrypt(new String(c, "ISO-8859-1"));
		return decrypt;
	}
	
	public static String formAuthParam(String openid,
			String registerid) {
		// 用户openid|||商家集客编码|||类型|||时间戳
		//openid|||pubid(registerid)|||type|||timestamp|||end
		StringBuffer sb = new StringBuffer();
		sb.append(openid);
		sb.append(SPLIT);
		sb.append(registerid);
		sb.append(SPLIT);
		sb.append(SpecialAuthConstant.APPTYPE_WX_VALUE);
		sb.append(SPLIT);
		sb.append(FormatUtil.dateToTimestamp(new Date()));
		sb.append(SPLIT);
		sb.append("end");
		return sb.toString();
	}
	
	/**
	 * 校验手机号
	 * @param phone
	 * @return
	 */
	public static boolean isValidPhone(String phone){
		String mobilepattern = MPClientUtils.callService(
				"/mp/portal/config/business/getConfig", new Object[] {
						"wxauth", "mobilepattern" }, String.class);
		if(phone.matches(mobilepattern)){
			return true;
		}else{
			return false;
		}
	}
}
