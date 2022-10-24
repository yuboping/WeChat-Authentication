/**
 * GetWXUserSoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.ailk.wxserver.ci.server.getwxuser;

import com.ailk.wxserver.service.main.interfaces.GetWXUserOperateBO;
import com.ailk.wxserver.util.spring.BeanUtil;


public class GetWXUserSoapBindingImpl implements com.ailk.wxserver.ci.server.getwxuser.GetWXUser{
	
	
    public com.ailk.wxserver.ci.server.getwxuser.GetPhoneResponse getPhone(com.ailk.wxserver.ci.server.getwxuser.GetPhoneRequest request) throws java.rmi.RemoteException {
    	GetWXUserOperateBO getWXUserOperateBO = (GetWXUserOperateBO) BeanUtil.getBean("getWXUserOperateBO");
    	return getWXUserOperateBO.getPhone(request);
    }

}
