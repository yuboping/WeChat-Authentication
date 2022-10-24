package com.ailk.wxserver.service.base.interfaces;

import java.util.Map;

import com.ailk.wxserver.util.ResponseResult;

public interface MacOperateBO {

	/**
	 * 取消绑定
	 * @param paramMap
	 * @return
	 */
	public ResponseResult directCancelMacBind(Map<String, String> paramMap);
}
