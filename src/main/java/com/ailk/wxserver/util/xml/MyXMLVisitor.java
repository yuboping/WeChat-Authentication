/*
 * Created on 2005-7-4
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ailk.wxserver.util.xml;

import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.VisitorSupport;

/**
 * @author Administrator
 * dom4jxml文件遍历类
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MyXMLVisitor extends VisitorSupport {
	private Map propertiesCache;
	private Map attributesCache;
	public MyXMLVisitor(Map propertiesCache,Map attributesCache){
		super();
		this.propertiesCache = propertiesCache;
		this.attributesCache = attributesCache;
	}
	/**
	 * 遍历元素
	 */
	public void visit(Element element){
//		if(element.nodeCount() == 1){
		if(element.isTextOnly()){
//			System.out.println(getFullName(element)+"="+element.getText());
			propertiesCache.put(getFullName(element),element.getText());
		}
	}
	/**
	 * 递归调用，返回父节点全名
	 * @param parentElement
	 * @return
	 */
	private String getParentName(Element parentElement){
		String tmp = "";
		if(!parentElement.isRootElement())
			tmp = getParentName(parentElement.getParent());
		return tmp+parentElement.getName()+".";
	}
	/**
	 * 得到属性的全名
	 * @param element
	 * @return
	 */
	private String getFullName(Element element){
		if(element.isRootElement())
			return element.getName();
		else
			return getParentName(element.getParent())+element.getName();
	}
	/**
	 * 遍历属性
	 */
	public void visit(Attribute attribute){
//		System.out.println(getFullName(attribute.getParent())+"."+attribute.getName()+"="+attribute.getText());
		attributesCache.put(getFullName(attribute.getParent())+"."+attribute.getName(),attribute.getText());
//		System.out.println(attribute.getName()+"="+attribute.getText());
//		System.out.println(getFullName(attribute.getParent()));
	}
}
