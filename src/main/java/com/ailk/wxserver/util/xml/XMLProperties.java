/*
 * Created on 2005-7-4
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ailk.wxserver.util.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * @author Administrator
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class XMLProperties {
	private Document doc = null;

	/**
	 * 缓存element值 key为element全称，root.parent.child value为element值
	 * 没有子节点的element才保存
	 */
	private Map propertiesCache = new HashMap();

	/**
	 * 缓存element的attribute值
	 * key为element全称＋attribute名称，root.parent.child.attribute value为attribute值
	 */
	private Map attributesCache = new HashMap();

	/**
	 * 根据指定的xml文件构造本类，构造后遍历该文件，填充propertiesCache和attributesCache
	 * 
	 * @param xmlFile
	 * @throws DocumentException
	 */
	public XMLProperties(String xmlFile) throws Exception {
		try {
			SAXReader reader = new SAXReader();
			doc = reader.read(new File(xmlFile));
			doc.getRootElement().accept(
					new MyXMLVisitor(propertiesCache, attributesCache));
		} catch (DocumentException e) {
			throw new Exception(e);
		}
		// TODO Auto-generated constructor stub
	}

	/**
	 * 返回根节点
	 * 
	 * @return
	 */
	public Element getRootElement() throws Exception {
		try {
			return doc.getRootElement();
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	/**
	 * 根据element名称，返回element值 没有找到指定的element，返回null
	 * 
	 * @param propertyName
	 *            全称，从root开始
	 * @return
	 */
	public String getElementValue(String elementName) throws Exception {
		try {
			if (propertiesCache.containsKey(elementName)) {
				return (String) propertiesCache.get(elementName);
			}
			Element element = getLeafElement(elementName);
			if (element != null) {
				String value = element.getText();
				value = value.trim();
				return value;
			} else
				return null;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}
	/**
	 * 根据父element名称，获取子element列表，匹配其中一个孙的值，返回另外一个孙的值
	 * @param parentName	父
	 * @param equalName		匹配的孙的元素名
	 * @param equalValue	匹配的孙的元素值
	 * @param resultName	需要获取值的孙元素的名称
	 * @return
	 * @throws Exception
	 */
	public String getElementValue(String parentName, String equalName,
			String equalValue, String resultName) throws Exception {
		try{
			List l = getElements(parentName);
			if(l.size() == 0)
				return "";
			Iterator iter = l.iterator();
			Element child = null;
			Element grandson = null;
			boolean flag = false;
			while(iter.hasNext()){
				child = (Element)iter.next();
				if(isLeafElement(child))
					return "";
				List list = child.elements();
				for (int i = 0; i < list.size(); i++){
					grandson = (Element)list.get(i);
					if(equalName.equals(grandson.getName())){
						if(equalValue.equals(grandson.getTextTrim())){
							flag = true;
							break;
						}
					}
				}
				if(flag){
					for(int i = 0; i < list.size(); i++){
						grandson = (Element)list.get(i);
						if(resultName.equals(grandson.getName())){
							return grandson.getTextTrim();
						}
					}
				}
			}
		} catch (Exception e) {
			throw new Exception(e);
		}
		return "";
	}

	/**
	 * 根据名称，返回指定element，如果非叶子element，返回null
	 * 
	 * @param propertyName
	 * @return
	 */
	public Element getLeafElement(String elementName) throws Exception {
		try {
			String[] propName = parseElementName(elementName);
			Element element = getRootElement();
			for (int i = 1; i < propName.length; i++) {
				element = element.element(propName[i]);
				if (element == null) {
					return null;
				}
			}
			if (!isLeafElement(element)) {
				System.out.println("not leaf element");
				return null;
			}
			return element;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	/**
	 * 根据名称，返回指定element，不判断是否为叶子
	 * 
	 * @param propertyName
	 * @return
	 */
	public Element getElement(String elementName) throws Exception {
		try {
			String[] propName = parseElementName(elementName);
			Element element = getRootElement();
			for (int i = 1; i < propName.length; i++) {
				element = element.element(propName[i]);
				if (element == null) {
					return null;
				}
			}
			return element;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	/**
	 * 根据名称，返回子元素List
	 * 
	 * @param elementName
	 * @return
	 */
	public List getElements(String elementName) throws Exception {
		try {
			String[] names = parseElementName(elementName);
			Element element = getRootElement();
			for (int i = 1; i < names.length; i++) {
				element = element.element(names[i]);
				if (element == null) {
					return null;
				}
			}
			return element.elements();
		} catch (Exception e) {
			throw new Exception(e);
		}
	}
	/**
	 * 根据父元素名称，返回子元素的value的List，子元素必须为叶子，否则抛出异常
	 * @param elementName
	 * @return
	 * @throws Exception
	 */
	public List getElementsValues(String elementName) throws Exception {
		try{
			String[] names = parseElementName(elementName);
			Element element = getRootElement();
			for (int i = 1; i < names.length; i++) {
				element = element.element(names[i]);
				if (element == null) {
					return null;
				}
			}
			List tmpElements = element.elements();
			if(tmpElements == null || tmpElements.size() == 0){
				return null;
			}else{
				List valueList = new ArrayList();
				Iterator iter = tmpElements.iterator();
				while(iter.hasNext()){
					element = (Element)iter.next();
					if(!isLeafElement(element)){
						throw new Exception("子元素中包含非叶子元素，不能取值！");
					}else{
						valueList.add(element.getText().trim());
					}
				}
				return valueList;
			}
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	/**
	 * 判断element是否为叶子
	 * 
	 * @param element
	 * @return
	 */
	public boolean isLeafElement(Element element) throws Exception {
		try {
			List list = element.elements();
			if (list.size() > 0)
				return false;
			else
				return true;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	/**
	 * 根据属性名，返回属性值
	 * 
	 * @param attributeName
	 *            全称 root.parent.child.attribute
	 * @return
	 */
	public String getAttributeValue(String attributeName) throws Exception {
		try {
			if (attributesCache.containsKey(attributeName)) {
				return (String) attributesCache.get(attributeName);
			}
			Element element = getElement(attributeName.substring(0,
					attributeName.lastIndexOf(".")));
			return element.attributeValue(attributeName.substring(attributeName
					.lastIndexOf(".") + 1));
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	/**
	 * 切分元素名称
	 * 
	 * @param name
	 * @return
	 */
	private String[] parseElementName(String name) throws Exception {
		// return name.split(".");
		try {
			int size = 1;
			for (int i = 0; i < name.length(); i++) {
				if (name.charAt(i) == '.') {
					size++;
				}
			}
			String[] eleName = new String[size];
			// Use a StringTokenizer to tokenize the property name.
			StringTokenizer tokenizer = new StringTokenizer(name, ".");
			int i = 0;
			while (tokenizer.hasMoreTokens()) {
				eleName[i] = tokenizer.nextToken();
				i++;
			}
			return eleName;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	public static void main(String[] args) {
		try {
			XMLProperties properties = new XMLProperties(
					"d:\\lcims60\\webapps\\config\\sysconfig.xml");
			System.out.println(properties.getElementValue("lcbms.modGroupid.checkBill"));
			System.out.println(properties
					.getElementValue("lcbms.modGroupid.results","value","5","description"));
//			System.out
//					.println(properties
//							.getAttributeValue("table.m_customer.customername.querytype"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
	}
}
