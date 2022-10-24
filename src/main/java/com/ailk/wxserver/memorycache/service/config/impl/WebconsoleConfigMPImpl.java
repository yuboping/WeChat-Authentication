package com.ailk.wxserver.memorycache.service.config.impl;

import java.util.List;

import com.ailk.lcims.support.mp.AbstractMP;
import com.ailk.lcims.support.mp.MPContext;
import com.ailk.lcims.support.util.GenericTypeReference;
import com.ailk.wxserver.memorycache.service.config.interfaces.WebconsoleConfigMP;
import com.ailk.wxserver.util.config.ConfigUtil;
import com.ailk.wxserver.util.config.model.Config;

public class WebconsoleConfigMPImpl extends AbstractMP<Config> implements WebconsoleConfigMP{

	@Override
	public List<Config> getAll() {
		return MPContext.getContext().getDataContainer().getInstance("webconsoleConfig", new GenericTypeReference<List<Config>>(){});
		}
	
	public List<String> getIplimit(){
    	return ConfigUtil.getConfigPropertyValues(getAll(), "iplimit", "ip");
    }
	
	public String  getComandRule(){
    	return ConfigUtil.getConfigPropertyValue(getAll(), "command", "rule");
    }
	
	public List<String>  list(String operaterName){
    	return ConfigUtil.getConfigPropertiesName(getAll(),operaterName);
    }
	
	public String  getRefreshName(String operaterName,String paramterName){
    	return ConfigUtil.getConfigPropertyValue(getAll(), operaterName, paramterName);
    }
	
}
