package com.ailk.wxserver.memorycache.service.config.impl;

import java.util.List;

import com.ailk.lcims.support.mp.AbstractMP;
import com.ailk.lcims.support.mp.MPContext;
import com.ailk.lcims.support.util.GenericTypeReference;
import com.ailk.wxserver.memorycache.service.config.interfaces.MessageShowConfigMP;
import com.ailk.wxserver.util.config.ConfigUtil;
import com.ailk.wxserver.util.config.model.Config;
import com.ailk.wxserver.util.config.model.Property;

public class MessageShowConfigMPImpl extends AbstractMP<Config> implements MessageShowConfigMP{

	@Override
	public List<Config> getAll() {
		return MPContext.getContext().getDataContainer().getInstance("messageshowConfig", new GenericTypeReference<List<Config>>(){});
		}
	
	
	
	/**
	 * 根据功能名和结果获取提示信息
	 * @param funcName
	 * @param result
	 * @return
	 */
	public String getMessage(String funcName,String result){
		String message ="其他";
		
		List<Config> ls=getAll();
		message = getMsg(funcName, result);
		if(message == null || message.trim().equals("")){
			message=ConfigUtil.getConfigPropertyText(ls, "initshow", funcName);
		}
		return message;
	}
	
	/**
	 * 先根据功能名查找，为空则再根据提示信息查找
	 * @param funcName -- 功能名
	 * @param defaulFuncName -- 默认功能名
	 * @param result
	 * @return
	 */
	public String getSpecialMessage(String funcName,String defaulFuncName,String result){
		String message ="其他";
		List<Config> ls=getAll();
		message = getMsg(funcName, result);
		if(message == null || message.trim().equals("")){
			message = getMsg(defaulFuncName, result);
		}
		if(message == null || message.trim().equals("")){
			message=ConfigUtil.getConfigPropertyText(ls, "initshow", funcName);
		}
		if(message == null || message.trim().equals("")){
			message=ConfigUtil.getConfigPropertyText(ls, "initshow", defaulFuncName);
		}
		return message;
	}


	private String getMsg(String funcName, String result) {
		String message = "";
		List<Config> ls=getAll();
		List<Property> properties=ConfigUtil.getConfigProperties(ls, funcName);
		if(null==properties||properties.size()==0){
			return message;
		}
		for(Property p:properties){
			if(result.equals(p.getValue())){
				message=p.getText();break;
			}
		}
		return message;
	}
	
}
