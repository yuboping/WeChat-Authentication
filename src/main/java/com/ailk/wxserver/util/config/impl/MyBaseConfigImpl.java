package com.ailk.wxserver.util.config.impl;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

import com.ailk.lcims.support.mp.client.MPClientUtils;
import com.ailk.lcims.support.util.StringUtils;
import com.ailk.lcims.support.util.xml.XMLProperties;
import com.ailk.wxserver.util.config.ConfigUtil;
import com.ailk.wxserver.util.config.model.Config;
import com.ailk.wxserver.util.config.model.Property;


public class MyBaseConfigImpl {
	
	private String xmlFile;
	private String type;
	/**
	 * 获取配置
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<Config> getConfig() throws Exception{
		
		try{
			XMLProperties xmlProperties=new XMLProperties(getXmlFile());
			Element e=xmlProperties.getRootElement();
			List<Element> ls=e.elements("config");
			if(ls.size()==0){
				return null;
			}
			List<Config> configs=new ArrayList<Config>();	
			Config c=null;
			for(Element ele:ls){
				c=new Config();
				c.setConfigname(ele.attributeValue("id"));
				c.setProperties(getProperties(ele));
				configs.add(c);
			}
			return configs;
		}catch(Exception e){
			throw new Exception(e);
		}
		
    }
	/**
	 * 获取property列表
	 * @param e
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Property> getProperties(Element e){
		List<Element> ls=e.elements("property");
		
		if(ls.size()==0){
			return null;
		}
		List<Property> properties=new ArrayList<Property>();
		Property p=null;
		String value=null;
		List<String> values=null;
		for(Element ele:ls){
			p=new Property();
			value=ele.attributeValue("value");
			values=getValues(ele);
			p.setName(ele.attributeValue("name"));
			p.setValues(values);
			if(StringUtils.isEmpty(value)){
				if(null!=values&&values.size()>0){
					value=values.get(0);
				}
			}			
			p.setValue(value);	
			p.setText(ele.getTextTrim());
			p.setProperties(getProperties(ele));
			properties.add(p);
		}
		return properties;
	}
	/**
	 * 获取value列表的值
	 * @param e
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<String> getValues(Element e){
		List<Element> ls=e.elements("value");
		
		if(ls.size()==0){
			return null;
		}
		List<String> values=new ArrayList<String>();
		int type=0;
		String typestr=null;
		for(Element ele:ls){
			typestr=ele.attributeValue("type");
			if("1".equals(typestr)){
				type=1;
			}
			values.add(getValueByType(type,ele.getTextTrim()));
		}
		return values;
	}
	
	/**
	 * 根据type获取value的值 type=1时从systemconfig的main配置中提取${}内的信息。
	 * @param type
	 * @param value
	 * @return
	 */
	public String getValueByType(int type,String value){
		switch (type) {
		case 0:
			return value;
		case 1:
			if(StringUtils.isEmpty(value)){
				return value;
			}
			String[] strs=value.split("\\$\\{");
			StringBuffer sb=new StringBuffer();
			int postion=0;
			String name=null;
			int i=0;
			for(String str:strs){
				if(i==0){
					sb.append(str);
					i++;
					continue;
				}
				postion=str.indexOf('}');
				if(postion>0){
					name=str.substring(0,postion);
					sb.append(MPClientUtils.callService("/mp/portal/config/system/getMainValue",
							new Object[]{name}, String.class));
					sb.append(str.substring(postion+1));
				}else{
					sb.append("${");
					sb.append(str);
				}

				i++;
				
			}
			
			return sb.toString();
		default:
			return value;
		}
			
		
	}
	
	
	
	public String getXmlFile() {
		int typeValue=0;
		if(!StringUtils.isEmpty(type)){
			typeValue=Integer.parseInt(type);
		}
		switch (typeValue) {
		case 1:
			return ConfigUtil.getConfigPath(xmlFile);
		case 2:
			String default_path=this.getClass().getResource("/").getPath()+"systemconfig.xml";
			String path = System.getProperty("wxserver_config", default_path);
			return path;
		default:
			return xmlFile;
		}
	}
	public void setXmlFile(String xmlFile) {
		this.xmlFile = xmlFile;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
