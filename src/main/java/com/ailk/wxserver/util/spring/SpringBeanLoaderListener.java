package com.ailk.wxserver.util.spring;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


public class SpringBeanLoaderListener implements ServletContextListener{

	@Override
	public void contextInitialized(ServletContextEvent sce) {
	
		SpringContainer.getInstance();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		
	}

}
