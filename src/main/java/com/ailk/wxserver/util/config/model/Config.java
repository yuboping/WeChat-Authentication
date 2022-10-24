package com.ailk.wxserver.util.config.model;

import java.util.List;

public class Config {
	private String configname;
	private List<Property> properties;
	public String getConfigname() {
		return configname;
	}
	public void setConfigname(String configname) {
		this.configname = configname;
	}
	public List<Property> getProperties() {
		return properties;
	}
	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

}
