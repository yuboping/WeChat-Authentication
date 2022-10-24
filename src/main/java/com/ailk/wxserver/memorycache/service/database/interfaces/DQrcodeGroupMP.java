package com.ailk.wxserver.memorycache.service.database.interfaces;

import com.ailk.wxserver.po.DQrcodeGroup;

public interface DQrcodeGroupMP {

	/**
	 * 获取组别信息
	 * @param scene
	 * @param eccode
	 * @return
	 */
	public DQrcodeGroup getQrcodeGroup(String scene,String eccode);
}
