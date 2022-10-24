package com.ailk.wxserver.util;

/**
 * 通用于表示结果的返回
 * @author jalin
 *
 */
public class ResponseResult {
	//result 返回结果的值 
	private String result;
	//description 描述返回结果的详细信息
	private String description;
	//info 用于传递信息
	private String info;
	
	private Object object ;
	
	public ResponseResult(){
	}
	
	public ResponseResult(String result){
		this.result=result;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}
}
