package com.ailk.wxserver.util;

import java.util.Date;

public class TimeUtil {

	/**
	 * 校验是否过期
	 * 
	 * @param verifyDate
	 * @return true--过期，false--未过期
	 */
	public static boolean isExpired(Date verifyDate) {
		if (verifyDate == null) {
			return true;
		}

		Date currentDate = new Date();
		if (verifyDate.after(currentDate)) {
			return false;
		} else {
			return true;
		}
	}
}
