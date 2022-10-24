package com.ailk.wxserver.util.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
/**
 * spring容器
 * @author shenxy
 *
 */
public class SpringContainer {

	 private ApplicationContext ac;
	
	 private SpringContainer() {
		 //取环境变量中的
		 String filepath=System.getProperty("lcunp_spring_path", "classpath:spring.xml");
	     ac=new FileSystemXmlApplicationContext(filepath);
	 }
	 
	 
	 public static class Holder {
		 static SpringContainer instance = new SpringContainer();  
	 }  
	 
	 public static SpringContainer getInstance() {  
	  //调用时直接使用内部类构建单例
	  return Holder.instance;  
	 }  

	 public ApplicationContext getApplicationContext(){
		 return this.ac;
	 }
}
