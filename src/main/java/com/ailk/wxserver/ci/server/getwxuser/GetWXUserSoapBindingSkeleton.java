/**
 * GetWXUserSoapBindingSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.ailk.wxserver.ci.server.getwxuser;

public class GetWXUserSoapBindingSkeleton implements com.ailk.wxserver.ci.server.getwxuser.GetWXUser, org.apache.axis.wsdl.Skeleton {
    private com.ailk.wxserver.ci.server.getwxuser.GetWXUser impl;
    private static java.util.Map _myOperations = new java.util.Hashtable();
    private static java.util.Collection _myOperationsList = new java.util.ArrayList();

    /**
    * Returns List of OperationDesc objects with this name
    */
    public static java.util.List getOperationDescByName(java.lang.String methodName) {
        return (java.util.List)_myOperations.get(methodName);
    }

    /**
    * Returns Collection of OperationDescs
    */
    public static java.util.Collection getOperationDescs() {
        return _myOperationsList;
    }

    static {
        org.apache.axis.description.OperationDesc _oper;
        org.apache.axis.description.FaultDesc _fault;
        org.apache.axis.description.ParameterDesc [] _params;
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://getwxuser.server.ci.wxserver.ailk.com", "request"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://getwxuser.server.ci.wxserver.ailk.com", "GetPhoneRequest"), com.ailk.wxserver.ci.server.getwxuser.GetPhoneRequest.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getPhone", _params, new javax.xml.namespace.QName("http://getwxuser.server.ci.wxserver.ailk.com", "getPhoneReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://getwxuser.server.ci.wxserver.ailk.com", "GetPhoneResponse"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://getwxuser.server.ci.wxserver.ailk.com", "getPhone"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getPhone") == null) {
            _myOperations.put("getPhone", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getPhone")).add(_oper);
    }

    public GetWXUserSoapBindingSkeleton() {
        this.impl = new com.ailk.wxserver.ci.server.getwxuser.GetWXUserSoapBindingImpl();
    }

    public GetWXUserSoapBindingSkeleton(com.ailk.wxserver.ci.server.getwxuser.GetWXUser impl) {
        this.impl = impl;
    }
    public com.ailk.wxserver.ci.server.getwxuser.GetPhoneResponse getPhone(com.ailk.wxserver.ci.server.getwxuser.GetPhoneRequest request) throws java.rmi.RemoteException
    {
        com.ailk.wxserver.ci.server.getwxuser.GetPhoneResponse ret = impl.getPhone(request);
        return ret;
    }

}
