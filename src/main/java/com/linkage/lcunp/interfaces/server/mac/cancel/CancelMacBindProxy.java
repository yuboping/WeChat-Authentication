package com.linkage.lcunp.interfaces.server.mac.cancel;

public class CancelMacBindProxy implements com.linkage.lcunp.interfaces.server.mac.cancel.CancelMacBind {
  private String _endpoint = null;
  private com.linkage.lcunp.interfaces.server.mac.cancel.CancelMacBind cancelMacBind = null;
  
  public CancelMacBindProxy() {
    _initCancelMacBindProxy();
  }
  
  public CancelMacBindProxy(String endpoint) {
    _endpoint = endpoint;
    _initCancelMacBindProxy();
  }
  
  private void _initCancelMacBindProxy() {
    try {
      cancelMacBind = (new com.linkage.lcunp.interfaces.server.mac.cancel.CancelMacBindServiceLocator()).getCancelMacBind();
      if (cancelMacBind != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)cancelMacBind)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)cancelMacBind)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (cancelMacBind != null)
      ((javax.xml.rpc.Stub)cancelMacBind)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public com.linkage.lcunp.interfaces.server.mac.cancel.CancelMacBind getCancelMacBind() {
    if (cancelMacBind == null)
      _initCancelMacBindProxy();
    return cancelMacBind;
  }
  
  public com.linkage.lcunp.interfaces.server.mac.cancel.OperateMacResponse cancelMacBind(com.linkage.lcunp.interfaces.server.mac.cancel.OperateMacRequest request) throws java.rmi.RemoteException{
    if (cancelMacBind == null)
      _initCancelMacBindProxy();
    return cancelMacBind.cancelMacBind(request);
  }
  
  
}