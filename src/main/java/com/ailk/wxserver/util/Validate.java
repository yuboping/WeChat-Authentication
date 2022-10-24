package com.ailk.wxserver.util;

import java.util.Date;

public class Validate {
    public static boolean isNumber(String l) {
    	return l.matches("[0-9]+");
    }
    /**
	 * 验证是否为小灵通号
	 * 
	 * @param phonenumber
	 * @return
	 */
	public static boolean isValidPasPhonenumber(String phonenumber) {
		return phonenumber.matches("^0[0-9]{10,11}$");
		
		
	}

	/**
	 * 验证是否为CDMA手机号
	 * 考虑到携号转网功能，仅校验1开头的11位数字
	 * @param phonenumber
	 * @return
	 */
	public static boolean isValidCDMAPhonenumber(String phonenumber) {
		return phonenumber.matches("^1[0-9]{10}$");
	}
	

	/**
	 * 验证是否为所有手机号
	 * 考虑到携号转网功能，仅校验1开头的11位数字
	 * @param phonenumber
	 * @return
	 */
	public static boolean isValidMobilePhonenumber(String phonenumber) {
		return phonenumber.matches("^1[0-9]{10}$");
	}
	
	
	/**
	 * 验证账号输入是否合法
	 * 
	 * @param userName
	 * @return
	 */
	public static boolean isValidUserName(String userName) {
		return userName.matches("^[\\w]+(@[\\w]+(\\.[\\w]+)+)*$");
	}
	/**
	 * 空字符串check
	 * 
	 * @param 输入值
	 * @return 输入值是否为空{""||null}(是:true/否:false)
	 */
	public static boolean isNullString(String str) {

		boolean ret = true;

		if (str != null && !"".equals(str)) {
			ret = false;
		}

		return ret;
	}

	/**
	 * 移动电话号码check
	 * 
	 * @param username
	 * @return 是否移动电话号码(是:true/否:false)
	 */
	public static boolean isCMCCPhoneNumber(String phonenumber) {

		return phonenumber.matches("^(1)\\d{10}$");
	}
	
	/**
	 * 校验时间戳
	 * false：超时，true不超时
	 * @param timeout
	 * @param timeStamp
	 * @return
	 * @throws Exception
	 */
	public static boolean validateTimestamp(long timeout, String timeStamp)
			throws Exception {
		Date nowTime = new Date();
		Date requestTime = null;
		requestTime = FormatUtil.timestampToDate(timeStamp);
		long margin = nowTime.getTime() - requestTime.getTime();
		margin = Math.abs(margin);
		if (timeout < margin) {
			return false;
		}
		return true;
	}
	
    public static void main(String[] args){
//    	System.out.println(isNumber("214"));
//    	System.out.println(isValidOtherMobilePhonenumber("13323222222"));
    	System.out.println(isValidUserName("12134332"));
    }
}
