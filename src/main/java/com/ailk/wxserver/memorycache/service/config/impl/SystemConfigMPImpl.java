package com.ailk.wxserver.memorycache.service.config.impl;

import java.io.File;
import java.util.List;

import com.ailk.lcims.support.mp.AbstractMP;
import com.ailk.lcims.support.mp.MPContext;
import com.ailk.lcims.support.util.GenericTypeReference;
import com.ailk.lcims.support.util.StringUtils;
import com.ailk.wxserver.memorycache.service.config.interfaces.SystemConfigMP;
import com.ailk.wxserver.util.config.ConfigUtil;
import com.ailk.wxserver.util.config.model.Config;

public class SystemConfigMPImpl extends AbstractMP<Config> implements SystemConfigMP{

	@Override
	public List<Config> getAll() {
		return MPContext.getContext().getDataContainer().getInstance("systemConfig", new GenericTypeReference<List<Config>>(){});
		}
	
	/**
	 * 获取基本的参数配置
	 * @param pname
	 * @return
	 */
	public String getMainValue(String pname){
		return ConfigUtil.getConfigPropertyValue(getAll(), "main",pname);
		 
	}
	
	/**
	 * 获取配置文件的路径
	 * @param configname
	 * @return
	 */
	public String getConfigPath(String configname){
		Config c=ConfigUtil.getConfig(getAll(), configname);
		if(c==null){
			return null;
		}
		String file_name=ConfigUtil.getPropertyValue(c.getProperties(), "file_name");
		String base_path=ConfigUtil.getPropertyValue(c.getProperties(), "base_path");
		//config下有base_path则使用，没有就是用默认配置中的
		if(StringUtils.isEmpty(base_path)){
			base_path=getMainValue("base_path");
		}				
		String operator=getOperator();
		String province=getProvince();
		return base_path+File.separator+operator+File.separator+province+File.separator+file_name;		
	}

	/**
	 * 获取省份配置
	 * @return
	 */
	public String getProvince(){
		return getMainValue("province");
    }
	/**
	 * 获取运营商配置
	 * @return
	 */
	public String getOperator() {
		return getMainValue("operator");
	}
}
