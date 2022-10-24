package com.ailk.wxserver.ci.client.sendsms;

public class CommonSendSMSProxy implements com.ailk.wxserver.ci.client.sendsms.CommonSendSMS {
  private String _endpoint = null;
  private com.ailk.wxserver.ci.client.sendsms.CommonSendSMS commonSendSMS = null;
  
  public CommonSendSMSProxy() {
    _initCommonSendSMSProxy();
  }
  
  public CommonSendSMSProxy(String endpoint) {
    _endpoint = endpoint;
    _initCommonSendSMSProxy();
  }
  
  private void _initCommonSendSMSProxy() {
    try {
      commonSendSMS = (new com.ailk.wxserver.ci.client.sendsms.CommonSendSMSServiceLocator()).getCommonSendSMS();
      if (commonSendSMS != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)commonSendSMS)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)commonSendSMS)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (commonSendSMS != null)
      ((javax.xml.rpc.Stub)commonSendSMS)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public com.ailk.wxserver.ci.client.sendsms.CommonSendSMS getCommonSendSMS() {
    if (commonSendSMS == null)
      _initCommonSendSMSProxy();
    return commonSendSMS;
  }
  
  public com.ailk.wxserver.ci.client.sendsms.CommonSendSmsResponse sendsms(com.ailk.wxserver.ci.client.sendsms.CommonSendSMSRequest request) throws java.rmi.RemoteException{
    if (commonSendSMS == null)
      _initCommonSendSMSProxy();
    return commonSendSMS.sendsms(request);
  }
  
  
}