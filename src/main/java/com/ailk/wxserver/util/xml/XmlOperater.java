package com.ailk.wxserver.util.xml;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.dom.DOMProcessingInstruction;
import org.dom4j.io.HTMLWriter;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.ailk.wxserver.util.SystemUtil;

public class XmlOperater {

	public XmlOperater() {
		// TODO Auto-generated constructor stub
	}

	public Document generateXMLFromObj(Object obj, Class objClass, Object id) throws Exception {
		// TODO Auto-generated method stub
		Document document = DocumentHelper.createDocument();
		document.setXMLEncoding("gbk");
		String className = objClass.getName();
		className = className.substring(className.lastIndexOf(".")+1);
		Element ele;
		if (id instanceof String) {
			ele = document.addElement(className+"_"+id);		
		}else if (id instanceof Integer) {
			ele = document.addElement(className+"_"+id.toString());		
		}else {
			String strID = "";
			Method[] pkMethods = id.getClass().getMethods();
			for (int i = 0; i < pkMethods.length; i++) {
				Method m = pkMethods[i];
				String methodName = m.getName();
//				System.out.println(i+":\t"+methodName);
				if (!methodName.startsWith("get")) continue;
				//String fieldName = methodName.substring(3,4).toLowerCase()+methodName.substring(4);
				String fieldValue = "";
				Class fieldClass = m.getReturnType();
				if (fieldClass == Integer.class) {
					fieldValue = String.valueOf(m.invoke(id,null));
				}else if (fieldClass == String.class) {
					if (m.invoke(id,null) != null) {
						fieldValue = (String)m.invoke(id,null);
					}else {
						fieldValue = "";
					}
				}else if (fieldClass == Date.class) {
					if (m.invoke(id,null) != null) {
						fieldValue = SystemUtil.getDate(((Date)m.invoke(id,null)),"yyyy-MM-dd");
					}else {
						fieldValue = "";
					}
				}else {
					continue;
				}
				strID += "_"+fieldValue;
			}
			ele = document.addElement(className+strID);
			for (int i = 0; i < pkMethods.length; i++) {
				Method m = pkMethods[i];
				String methodName = m.getName();
//				System.out.println(i+":\t"+methodName);
				if (!methodName.startsWith("get")) continue;
				String fieldName = methodName.substring(3,4).toLowerCase()+methodName.substring(4);
				String fieldValue = "";
				Class fieldClass = m.getReturnType();
				if (fieldClass == Integer.class) {
					fieldValue = String.valueOf(m.invoke(id,null));
				}else if (fieldClass == String.class) {
					if (m.invoke(id,null) != null) {
						fieldValue = (String)m.invoke(id,null);
					}else {
						fieldValue = "";
					}
				}else if (fieldClass == Date.class) {
					if (m.invoke(id,null) != null) {
						fieldValue = SystemUtil.getDate(((Date)m.invoke(id,null)),"yyyy-MM-dd");
					}else {
						fieldValue = "";
					}
				}else {
					continue;
				}
				ele.addAttribute(fieldName, fieldValue);
			}
		}
		Method[] methods = objClass.getMethods();
		for (int i = 0; i < methods.length; i++) {
			Method m = methods[i];
			String methodName = m.getName();
//			System.out.println(i+":\t"+methodName);
			if (!methodName.startsWith("get")) continue;
			String fieldName = methodName.substring(3,4).toLowerCase()+methodName.substring(4);
			String fieldValue = "";
			Class fieldClass = m.getReturnType();
			if (fieldClass == Integer.class) {
				fieldValue = String.valueOf(m.invoke(obj,null));
			}else if (fieldClass == String.class) {
				if (m.invoke(obj,null) != null) {
					fieldValue = (String)m.invoke(obj,null);
				}else {
					fieldValue = "";
				}
			}else if (fieldClass == Date.class) {
				if (m.invoke(obj,null) != null) {
					fieldValue = SystemUtil.getDate(((Date)m.invoke(obj,null)),"yyyy-MM-dd");
				}else {
					fieldValue = "";
				}
			}else {
				continue;
			}
			ele.addAttribute(fieldName, fieldValue);
		}
		ele.addText("");
		return document;
	}

	public Document generateComplexXMLFromObj(Object obj, Class objClass, Object id) throws Exception {
		// TODO Auto-generated method stub
		Document document = DocumentHelper.createDocument();
//		setDocumentXSLT(document, "test.xslt");
		document.setXMLEncoding("gbk");
		String className = objClass.getName();
		className = className.substring(className.lastIndexOf(".")+1);
		Element ele;
		if (id instanceof String) {
			ele = document.addElement(className+"_"+id);		
		}else if (id instanceof Integer) {
			ele = document.addElement(className+"_"+id.toString());		
		}else {
			String strID = "";
			Method[] pkMethods = id.getClass().getMethods();
			for (int i = 0; i < pkMethods.length; i++) {
				Method m = pkMethods[i];
				String methodName = m.getName();
//				System.out.println(i+":\t"+methodName);
				if (!methodName.startsWith("get")) continue;
				//String fieldName = methodName.substring(3,4).toLowerCase()+methodName.substring(4);
				String fieldValue = "";
				Class fieldClass = m.getReturnType();
				if (fieldClass == Integer.class) {
					fieldValue = String.valueOf(m.invoke(id,null));
				}else if (fieldClass == String.class) {
					if (m.invoke(id,null) != null) {
						fieldValue = (String)m.invoke(id,null);
					}else {
						fieldValue = "";
					}
				}else if (fieldClass == Date.class) {
					if (m.invoke(id,null) != null) {
						fieldValue = SystemUtil.getDate(((Date)m.invoke(id,null)),"yyyy-MM-dd");
					}else {
						fieldValue = "";
					}
				}else {
					continue;
				}
				strID += "_"+fieldValue;
			}
			ele = document.addElement(className+strID);
			ele.addAttribute("pkClass", id.getClass().getName());
			for (int i = 0; i < pkMethods.length; i++) {
				Method m = pkMethods[i];
				String methodName = m.getName();
//				System.out.println(i+":\t"+methodName);
				if (!methodName.startsWith("get")) continue;
				String fieldName = methodName.substring(3,4).toLowerCase()+methodName.substring(4);
				String fieldValue = "";
				Class fieldClass = m.getReturnType();
				if (fieldClass == Integer.class) {
					fieldValue = String.valueOf(m.invoke(id,null));
				}else if (fieldClass == String.class) {
					if (m.invoke(id,null) != null) {
						fieldValue = (String)m.invoke(id,null);
					}else {
						fieldValue = "";
					}
				}else if (fieldClass == Date.class) {
					if (m.invoke(id,null) != null) {
						fieldValue = SystemUtil.getDate(((Date)m.invoke(id,null)),"yyyy-MM-dd");
					}else {
						fieldValue = "";
					}
				}else {
					continue;
				}
				ele.addElement(fieldName).addText(fieldValue);
			}
		}
		ele.addAttribute("class", objClass.getName());
		Method[] methods = objClass.getMethods();
		for (int i = 0; i < methods.length; i++) {
			Method m = methods[i];
			String methodName = m.getName();
//			System.out.println(i+":\t"+methodName);
			if (!methodName.startsWith("get")) continue;
			String fieldName = methodName.substring(3,4).toLowerCase()+methodName.substring(4);
			String fieldValue = "";
			Class fieldClass = m.getReturnType();
			if (fieldClass == Integer.class) {
				fieldValue = String.valueOf(m.invoke(obj,null));
			}else if (fieldClass == String.class) {
				if (m.invoke(obj,null) != null) {
					fieldValue = (String)m.invoke(obj,null);
				}else {
					fieldValue = "";
				}
			}else if (fieldClass == Date.class) {
				if (m.invoke(obj,null) != null) {
					fieldValue = SystemUtil.getDate(((Date)m.invoke(obj,null)),"yyyy-MM-dd");
				}else {
					fieldValue = "";
				}
			}else {
				continue;
			}
			ele.addElement(fieldName).addText(fieldValue);
		}
		return document;
	}
	
	
	
	public HashMap generateObjFromXML(Document document, HashMap retMap) throws Exception {
		if (retMap == null) {
			retMap = new HashMap();
		}
		
		return retMap;
	}

	public HashMap generateObjFromComplexXML(Document document, HashMap retMap) throws Exception {
		if (retMap == null) {
			retMap = new HashMap();
		}
		Element root = document.getRootElement();
		String objClassName = root.attributeValue("class");
		Class objClass = Class.forName(objClassName);
		Object obj = objClass.newInstance();
		String pkClassName = root.attributeValue("pkClass");
		Class pkClass = null;
		Object pkObj = null;
		
		String objName = root.getName();
		if (retMap.get(objName) != null) {
			obj = retMap.get(objName);
			if (pkClassName != null) {
				pkClass = Class.forName(pkClassName);
				Method m = objClass.getMethod("getComp_id",null);
				pkObj = m.invoke(obj, null);
			}
		}else{
			if (pkClassName != null) {
				pkClass = Class.forName(pkClassName);
				pkObj = pkClass.newInstance();
			}
		}
		
		Iterator iter = root.elementIterator();
		while (iter.hasNext()) {
			Element ele = (Element)iter.next();
			if (ele.isTextOnly()) {
				boolean isPKField = false;
				String fieldName = ele.getName();
				String fieldValue = ele.getTextTrim();
				String setMethodName = "set" + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
				String getMethodName = "get" + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
				Method m = null;
				Class colClass = null;
//				System.out.println(fieldName+"/"+fieldValue);
				if (pkClassName != null) {
					try {
						m = objClass.getMethod(getMethodName,null);
					}catch (NoSuchMethodException nsme) {
						m = pkClass.getMethod(getMethodName,null);
						isPKField = true;
					}
				}else {
					m = objClass.getMethod(getMethodName,null);
				}
				if (isPKField) {
					colClass = m.getReturnType();
					m = pkClass.getMethod(setMethodName,new Class[]{colClass});
				}else {
					colClass = m.getReturnType();
					m = objClass.getMethod(setMethodName,new Class[]{colClass});
				}
				if (colClass == Integer.class) {
					if (isPKField) {
						m.invoke(pkObj,new Object[]{Integer.parseInt(fieldValue)});
					}else {
						m.invoke(obj,new Object[]{Integer.parseInt(fieldValue)});
					}
				}else if (colClass == String.class) {
					if (isPKField) {
						m.invoke(pkObj,new Object[]{fieldValue});
					}else {
						m.invoke(obj,new Object[]{fieldValue});
					}
				}else if (colClass == Date.class) {
					if (isPKField) {
						m.invoke(pkObj,new Object[]{SystemUtil.getDateFromString(fieldValue)});
					}else {
						m.invoke(obj,new Object[]{SystemUtil.getDateFromString(fieldValue)});
					}
				}else {
//					throw new Exception("非支持的字段类型"+colClass+";"+colName);
				}
			}else {
//				String fieldName = ele.getName();
//				String fieldValue = ele.getTextTrim();
//				System.out.println("other:"+fieldName+"/"+fieldValue);
				Document tempDoc = DocumentHelper.createDocument();
				Element tempElement = (Element)ele.clone();
				tempDoc.setRootElement(tempElement);
				generateObjFromComplexXML(tempDoc,retMap);
			}
		}
		if (pkClassName != null) {
			Method m = objClass.getMethod("setComp_id",new Class[] {pkClass});
			m.invoke(obj, new Object[]{pkObj});
		}
//		System.out.println(objName+"/"+obj);
		retMap.put(objName, obj);
		return retMap;
	}
	
	public String documentToXMLString(Document doc) throws Exception {
		String ret = "";
		ByteArrayOutputStream  sout = new ByteArrayOutputStream(); 
		OutputFormat format = OutputFormat.createPrettyPrint(); 
		format.setEncoding("gbk");
		XMLWriter out = new XMLWriter(sout, format);
		out.write(doc);
		ret = new String(sout.toByteArray());
		out.close();
		return ret;
	}
	
	public String documentToXMLString(Document doc, String charset) throws Exception {
		String ret = "";
		ByteArrayOutputStream  sout = new ByteArrayOutputStream(); 
		OutputFormat format = OutputFormat.createPrettyPrint(); 
		format.setEncoding(charset);
		XMLWriter out = new XMLWriter(sout, format);
		out.write(doc);
		ret = new String(sout.toByteArray());
		out.close();
		return ret;
	}
	
	public String documentToHTMLString(Document doc) throws Exception {
		String ret = "";
		ByteArrayOutputStream  sout = new ByteArrayOutputStream(); 
		OutputFormat format = OutputFormat.createPrettyPrint(); 
		format.setEncoding("gbk");
		HTMLWriter out = new HTMLWriter(sout, format);
		out.write(doc);
		ret = new String(sout.toByteArray());
		out.close();
		System.out.println(ret);
		return ret;
	}
	
	public String documentToHTMLString(Document doc, String charset) throws Exception {
		String ret = "";
		ByteArrayOutputStream  sout = new ByteArrayOutputStream(); 
		OutputFormat format = OutputFormat.createPrettyPrint(); 
		format.setEncoding(charset);
		HTMLWriter out = new HTMLWriter(sout, format);
		out.write(doc);
		ret = new String(sout.toByteArray());
		out.close();
		System.out.println(ret);
		return ret;
	}
	
	public void setDocumentXSLT(Document doc,String xslt) throws Exception {
		DOMProcessingInstruction dpi = new DOMProcessingInstruction(
				"xml-stylesheet","href=\""+xslt+"\"   type=\"text/xsl\"");  
		Element ele = doc.getRootElement();
		doc.remove(ele);
		doc.add(dpi);
		doc.add(ele);
	}
	

}
