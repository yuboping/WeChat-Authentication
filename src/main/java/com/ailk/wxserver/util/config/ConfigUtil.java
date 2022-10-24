package com.ailk.wxserver.util.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ailk.lcims.support.mp.MPContext;
import com.ailk.lcims.support.util.GenericTypeReference;
import com.ailk.lcims.support.util.StringUtils;
import com.ailk.wxserver.util.config.model.Config;
import com.ailk.wxserver.util.config.model.Property;

public class ConfigUtil {
	
	
	/**
	 * 根据Config名获取对应的Config
	 * @param ls
	 * @param name
	 * @return
	 */
	public static Config getConfig(List<Config> ls,String name){
		for(Config config:ls){
			if(name.equals(config.getConfigname())){
				return config;
			}
		}		
		return null;
	}
	
	
	/**
	 * 根据Config名获取对应的ProPerty列表
	 * @param ls
	 * @param name
	 * @return
	 */
	public static List<Property> getConfigProperties(List<Config> ls,String name){
		Config config=getConfig(ls,name);
		if(null==config){
			return null;
		}
		return  config.getProperties();
	}
	/**
	 * 根据Config名获取对应的ProPerty列表
	 * @param ls
	 * @param name
	 * @return
	 */
	public static List<Property> getConfigProperties(List<Config> ls,String configname,String propertyname){
		Config config=getConfig(ls,configname);
		if(null==config){
			return null;
		}
		List<Property> properties=config.getProperties();
		if(null==properties){
			return null;
		}
		List<Property> rProperties=new ArrayList<Property>();
		for(Property p:properties){
			if(propertyname.equals(p.getName())){
				rProperties.add(p);
			}
		}
		return  rProperties;
	}
	
	/**
	 * 根据Config名获取对应的ProPerty列表名
	 * @param ls
	 * @param name
	 * @return
	 */
	public static List<String> getConfigPropertiesName(List<Config> ls,String name){
		List<Property> properties=getConfigProperties(ls, name);
    	if(properties==null||properties.size()==0){
    		return null;
    	}
    	List<String> propertyNames=new ArrayList<String>();
    	for(Property p:properties){
    		propertyNames.add(p.getName());
    	}
		return  propertyNames;
	}
	
	/**
	 * 根据Config名,讲对应的ProPerty列表转成map
	 * @param ls
	 * @param name
	 * @return
	 */
	public static  Map<String,String>  getConfigPropertiesMap(List<Config> ls,String name){
		List<Property> properties=getConfigProperties(ls, name);
    	if(properties==null||properties.size()==0){
    		return null;
    	}
    	Map<String,String> configs=new HashMap<String, String>();
    	for(Property p:properties){
    		configs.put(p.getName(), p.getValue());
    	}
    	return configs;
	}
	
	
	/**
	 * 根据Config名，和propery名获取对应的value
	 * @param ls
	 * @param name
	 * @return
	 */
	public static String getConfigPropertyValue(List<Config> ls,String name,String pname){
		
		return getPropertyValue(getConfigProperties(ls,name),pname);
		
		  
	}
	
	/**
	 * 根据Config名，和propery名获取对应的Text
	 * @param ls
	 * @param name
	 * @return
	 */
	public static String getConfigPropertyText(List<Config> ls,String name,String pname){
		
		return getPropertyText(getConfigProperties(ls,name),pname);
		
		  
	}
	
	/**
	 * 根据Config名，和propery名获取对应的value列表
	 * @param ls
	 * @param name
	 * @return
	 */
	public static List<String> getConfigPropertyValues(List<Config> ls,String name,String pname){
		
		return getPropertyValues(getConfigProperties(ls,name),pname);
		
		  
	}
	/**
	 * 根据Property的名字获取对应的Property
	 * @param ps
	 * @param name
	 * @return
	 */
	public static Property getProperty(List<Property> ps,String name){
		if(ps!=null&&!ps.isEmpty()){
			for(Property p:ps){
				if(name.equals(p.getName())){
					return p;				
				}
			}
		}
		return null;
	}

	/**
	 * 根据Property的名字获取对应的value值
	 * @param ps
	 * @param name
	 * @return
	 */
	public static String getPropertyValue(List<Property> ps,String name){
		Property property=getProperty(ps,name);
		if(null==property){
			return null;
		}
		return property.getValue();
	}
	
	/**
	 * 根据Property的名字获取对应的Text值
	 * @param ps
	 * @param name
	 * @return
	 */
	public static String getPropertyText(List<Property> ps,String name){
		Property property=getProperty(ps,name);
		if(null==property){
			return null;
		}
		return property.getText();
	}
	
	/**
	 * 根据Property的名字获取对应的value值列表
	 * @param ps
	 * @param name
	 * @return
	 */
	public static List<String> getPropertyValues(List<Property> ps,String name){
		Property property=getProperty(ps,name);
		if(null==property){
			return null;
		}
		return property.getValues();
	}

	
	public static List<Config> getSystemConfigData() {
		return MPContext.getContext().getDataContainer().getInstance("systemConfig", new GenericTypeReference<List<Config>>(){});
		}
	
	/**
	 * 获取基本的参数配置
	 * @param pname
	 * @return
	 */
	public static String getMainValue(String pname){
		return ConfigUtil.getConfigPropertyValue(getSystemConfigData(), "main",pname);
		 
	}
	
	/**
	 * 获取配置文件的路径
	 * @param configname
	 * @return
	 */
	public static String getConfigPath(String configname){
		Config c=ConfigUtil.getConfig(getSystemConfigData(), configname);
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
	public static String getProvince(){
		return getMainValue("province");
    }
	/**
	 * 获取运营商配置
	 * @return
	 */
	public static String getOperator() {
		return getMainValue("operator");
	}
}
