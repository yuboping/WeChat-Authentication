package com.ailk.wxserver.memorycache.service.config.impl;

import java.util.List;

import com.ailk.lcims.support.mp.AbstractMP;
import com.ailk.lcims.support.mp.MPContext;
import com.ailk.lcims.support.util.GenericTypeReference;
import com.ailk.wxserver.memorycache.service.config.interfaces.BusinessConfigMP;
import com.ailk.wxserver.util.config.ConfigUtil;
import com.ailk.wxserver.util.config.model.Config;

public class BusinessConfigMPImpl extends AbstractMP<Config> implements BusinessConfigMP{

	@Override
	public List<Config> getAll() {
		return MPContext.getContext().getDataContainer().getInstance("businessConfig", new GenericTypeReference<List<Config>>(){});
		}
	public String getMainConfig(String name){
    	return ConfigUtil.getConfigPropertyValue(getAll(), "main", name);
    }
	
	public int getUserPasswordType(){
    	return Integer.parseInt(ConfigUtil.getConfigPropertyValue(getAll(), "main", "user_passwordtype"));
    }
	
	public int getUserHash(){
    	return Integer.parseInt(ConfigUtil.getConfigPropertyValue(getAll(), "main", "user_hash"));
    }
	
	public boolean isStartupWithoutDB(){
    	if("true".equals(ConfigUtil.getConfigPropertyValue(getAll(), "emergency", "isstartupwithoutdb"))){
    		return true;
    	}
    	return false;
    }
	
	public boolean startupAdaptivity(String name){
    	if("true".equals(ConfigUtil.getConfigPropertyValue(getAll(), "emergency", "startupadaptivity"))){
    		return true;
    	}
    	return false;
    }
	public String getConfig(String configname,String propertyName){
    	return ConfigUtil.getConfigPropertyValue(getAll(), configname, propertyName);
    }
	public List<String> getConfigValues(String configname,String propertyName){
    	return ConfigUtil.getConfigPropertyValues(getAll(), configname, propertyName);
    }
	
	
}