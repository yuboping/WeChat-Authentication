/**
 * WXServiceImplServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.asiainfo.lcims.lcbmi.weixin.server;

public class WXServiceImplServiceLocator extends org.apache.axis.client.Service implements com.asiainfo.lcims.lcbmi.weixin.server.WXServiceImplService {

    public WXServiceImplServiceLocator() {
    }


    public WXServiceImplServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public WXServiceImplServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for WXServiceImplPort
    private java.lang.String WXServiceImplPort_address = "http://127.0.0.1:17118/services/WXService";

    public java.lang.String getWXServiceImplPortAddress() {
        return WXServiceImplPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String WXServiceImplPortWSDDServiceName = "WXServiceImplPort";

    public java.lang.String getWXServiceImplPortWSDDServiceName() {
        return WXServiceImplPortWSDDServiceName;
    }

    public void setWXServiceImplPortWSDDServiceName(java.lang.String name) {
        WXServiceImplPortWSDDServiceName = name;
    }

    public com.asiainfo.lcims.lcbmi.weixin.server.WXService getWXServiceImplPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(WXServiceImplPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getWXServiceImplPort(endpoint);
    }

    public com.asiainfo.lcims.lcbmi.weixin.server.WXService getWXServiceImplPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.asiainfo.lcims.lcbmi.weixin.server.WXServiceImplPortBindingStub _stub = new com.asiainfo.lcims.lcbmi.weixin.server.WXServiceImplPortBindingStub(portAddress, this);
            _stub.setPortName(getWXServiceImplPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setWXServiceImplPortEndpointAddress(java.lang.String address) {
        WXServiceImplPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.asiainfo.lcims.lcbmi.weixin.server.WXService.class.isAssignableFrom(serviceEndpointInterface)) {
                com.asiainfo.lcims.lcbmi.weixin.server.WXServiceImplPortBindingStub _stub = new com.asiainfo.lcims.lcbmi.weixin.server.WXServiceImplPortBindingStub(new java.net.URL(WXServiceImplPort_address), this);
                _stub.setPortName(getWXServiceImplPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("WXServiceImplPort".equals(inputPortName)) {
            return getWXServiceImplPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://server.weixin.lcbmi.lcims.asiainfo.com/", "WXServiceImplService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://server.weixin.lcbmi.lcims.asiainfo.com/", "WXServiceImplPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("WXServiceImplPort".equals(portName)) {
            setWXServiceImplPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
