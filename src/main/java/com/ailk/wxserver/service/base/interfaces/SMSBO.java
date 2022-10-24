package com.ailk.wxserver.service.base.interfaces;

import java.util.Map;

public interface SMSBO {

	/**
	 * 下发短信
	 * @param paramMap
	 * @return
	 */
	public int sendSMS(Map<String, String> paramMap) ;
}
