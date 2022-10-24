package com.ailk.wxserver.service.constant;

/**
 * 微信请求type
 * @author zhoutj
 *
 */
public class WXReqTypeConstant {

	
	/**
	 * 微信连wifi签名
	 */
	public static final String WX_REQTYPE_WIFISIGN = "wifisign";
	
	/**
	 * 微信连wifi preauth
	 */
	public static final String WX_REQTYPE_WIFIPREAUTH = "wifipreauth";
	
	/**
	 * 微信连wifi auth
	 */
	public static final String WX_REQTYPE_WIFIAUTH = "wifiauth";
	
	/**
	 * 微信连wifi check:校验微信用户是否关注是否验证过手机号
	 */
	public static final String WX_REQTYPE_WIFICHECK = "wificheck";
	
	
	/**
	 * 获取验证码
	 */
	public final static String WX_REQTYPE_VERIFY_TYPE_GETVFCODE = "gvf";
	
	/**
	 * 链接验证
	 */
	public final static String WX_REQTYPE_VERIFY_TYPE_LINK = "vtl";
	
	/**
	 * 验证码验证
	 */
	public final static String WX_REQTYPE_VERIFY_TYPE_VERIFYCODE = "vtc";
	
	/**
	 * PC端使用微信连Wi-Fi
	 */
	public static final String WX_REQTYPE_WIFIPC = "wifipc";
}
