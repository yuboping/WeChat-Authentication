package com.asiainfo.lcims.lcbmi.weixin.server;

public class WXServiceProxy implements com.asiainfo.lcims.lcbmi.weixin.server.WXService {
  private String _endpoint = null;
  private com.asiainfo.lcims.lcbmi.weixin.server.WXService wXService = null;
  
  public WXServiceProxy() {
    _initWXServiceProxy();
  }
  
  public WXServiceProxy(String endpoint) {
    _endpoint = endpoint;
    _initWXServiceProxy();
  }
  
  private void _initWXServiceProxy() {
    try {
      wXService = (new com.asiainfo.lcims.lcbmi.weixin.server.WXServiceImplServiceLocator()).getWXServiceImplPort();
      if (wXService != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)wXService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)wXService)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (wXService != null)
      ((javax.xml.rpc.Stub)wXService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public com.asiainfo.lcims.lcbmi.weixin.server.WXService getWXService() {
    if (wXService == null)
      _initWXServiceProxy();
    return wXService;
  }
  
  public com.asiainfo.lcims.lcbmi.weixin.server.CloseWXUserResp closeWXUser(com.asiainfo.lcims.lcbmi.weixin.server.CloseWXUserReq request) throws java.rmi.RemoteException{
    if (wXService == null)
      _initWXServiceProxy();
    return wXService.closeWXUser(request);
  }
  
  public com.asiainfo.lcims.lcbmi.weixin.server.OpenWXUserResp openWXUser(com.asiainfo.lcims.lcbmi.weixin.server.OpenWXUserReq request) throws java.rmi.RemoteException{
    if (wXService == null)
      _initWXServiceProxy();
    return wXService.openWXUser(request);
  }
  
  public com.asiainfo.lcims.lcbmi.weixin.server.QueryWXUserResp queryWXUser(com.asiainfo.lcims.lcbmi.weixin.server.QueryWXUserReq request) throws java.rmi.RemoteException{
    if (wXService == null)
      _initWXServiceProxy();
    return wXService.queryWXUser(request);
  }
  
  public com.asiainfo.lcims.lcbmi.weixin.server.BindWXUserResp bindWXUser(com.asiainfo.lcims.lcbmi.weixin.server.BindWXUserReq request) throws java.rmi.RemoteException{
    if (wXService == null)
      _initWXServiceProxy();
    return wXService.bindWXUser(request);
  }
  
  
}