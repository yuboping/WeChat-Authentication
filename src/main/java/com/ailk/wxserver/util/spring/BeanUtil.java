package com.ailk.wxserver.util.spring;

import com.ailk.lcims.support.util.spring.BeanLocator;

public class BeanUtil {
	static {
		//初始化springcontext
		SpringContainer.getInstance();
	}

    public static Object getBean(String name) {
    	
        return BeanLocator.getInstance().getBean(name);
    }


}
