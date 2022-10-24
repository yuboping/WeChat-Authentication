package com.ailk.wxserver.service.constant;

public class UserConstant {

	/**
	 * 微信用户正常
	 */
	public static final int WXUSER_NORMAIL = 0;
	/**
	 * 微信用户不存在
	 */
	public static final int WXUSER_SPECIALUSER_NOTEXIST = 1;
	/**
	 * 微信用户的宽带用户不存在或者开户失败
	 */
	public static final int WXUSER_BROADUSER_NOORFAIL = 2;
	/**
	 * 微信用户没有验证或者验证失败
	 */
	public static final int WXUSER_VERIFY_NOORFAIL = 3;
	/**
	 * 微信用户验证过期
	 */
	public static final int WXUSER_VERIFY_EXPIRED = 4;
}
