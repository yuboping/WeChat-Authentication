package com.ailk.wxserver.util;

import java.util.Date;

import com.ailk.lcims.support.util.password.Password;
import com.ailk.wxserver.service.constant.SpecialAuthConstant;

public class SpecialAuthUtil {

	/**
	 * 类型有4类：1、微信，缩写wx；2、易信，缩写yx； 3、QQ，缩写qq；4微博（新浪），缩写wb。
	 * 
	 * @param type
	 * @return
	 */
	public static String switchType(int type) {

		String typeStr = "";
		switch (type) {
		case SpecialAuthConstant.APPTYPE_WX_VALUE:
			typeStr = SpecialAuthConstant.APPTYPE_WX;
			break;
		case SpecialAuthConstant.APPTYPE_YX_VALUE:
			typeStr = SpecialAuthConstant.APPTYPE_YX;
			break;
		case SpecialAuthConstant.APPTYPE_QQ_VALUE:
			typeStr = SpecialAuthConstant.APPTYPE_QQ;
			break;
		case SpecialAuthConstant.APPTYPE_WB_VALUE:
			typeStr = SpecialAuthConstant.APPTYPE_WB;
			break;
		}

		return typeStr;
	}

	/**
	 * 账号生成规则：类型缩写+集团编号+openid唯一标识。 如qq.kfc.C81E728D9D4C2F636F067F89CC14862C
	 * MD5 16或者32位加密
	 * @param appType
	 * @param ecCode
	 * @param openID
	 * @param MD5_bittype md5加密位数 1--16位，2--32位
	 * @return
	 */
	public static String generateUserName(int appType, String ecCode,
			String openID,int MD5_bittype) {
		StringBuffer uname = new StringBuffer();
		uname.append(SpecialAuthUtil.switchType(appType));
		uname.append(".");
		
		String md5str = Password.encryptPassword(ecCode+"."+openID, 5);
		if(MD5_bittype == 1){
			md5str = md5str.substring(8, 24);
		}
		uname.append(md5str);
		return uname.toString();
	}
	
	/**
	 * 账号生成规则：类型缩写+集团编号+openid唯一标识。 如qq.kfc.C81E728D9D4C2F636F067F89CC14862C
	 * MD5 32位加密
	 * @param user
	 * @return
	 */
	public static String generateUserName(int appType, String ecCode,
			String openID) {
		return generateUserName(appType,ecCode,openID,2);
	}

	/**
	 * 密码生成规则
	 * @param user
	 * @param MD5_bittype 1-16位加密 ， 2-32加密
	 * @param length -- 密码长度
	 * @return
	 */
	public static String generatePwd(String username,int MD5_bittype,int length) {

		// 密码生成方式
		String pwd = Password.encryptPassword(username, 5);
		if(MD5_bittype == 1){
			pwd = pwd.substring(8, 24);
		}
		int len = pwd.length();
		int tmpLength = length;
		if(length>len){
			tmpLength = len;
		}
		pwd = pwd.substring(len - tmpLength, len);

		return pwd;
	}
	
	/**
	 * 密码生成规则：
	 * 用户名 32位加密后取后8位作为密码
	 * @param user
	 * @return
	 */
	public static String generatePwd(String username) {
		return generatePwd(username, 2);
	}
	
	/**
	 * 密码生成规则：
	 * 用户名 MD5加密后取后8位作为密码
	 * @param user
	 * @param MD5_bittype 1=16位加密 2-32位加密（默认）
	 * @return
	 */
	public static String generatePwd(String username,int MD5_bittype) {

		// 密码生成方式
		String pwd = Password.encryptPassword(username, 5);
		if(MD5_bittype == 1){
			pwd = pwd.substring(8, 24);
		}
		int len = pwd.length();
		pwd = pwd.substring(len - 8, len);

		return pwd;
	}

	/**
	 * 密码生成规则：
	 * 
	 * @param user
	 * @return
	 */
	public static String generateEncryptPwd(String password, String pwdtype) {
		
		String pwd = Password.encryptPassword(password, pwdtype);

		return pwd;
	}
	
	/**
	 * 设置随机token
	 * @return
	 */
	public static String formToken(int appType, String ecCode,String openID){
		StringBuffer sb = new StringBuffer();
		sb.append(SpecialAuthUtil.switchType(appType));
		sb.append(".");
		sb.append(ecCode);
		sb.append(".");
		sb.append(openID);
		sb.append(".");
		sb.append(SystemUtil.getDate(new Date(), "yyyyMMddHHmmss"));
		sb.append(".");
		sb.append(StringUtils.getRandomString(5));
		String token = Password.encryptPassword(sb.toString(), 5);
		token = token.substring(8, 24);
		return token;
	}
}
