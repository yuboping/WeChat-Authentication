/**
 * WXService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.asiainfo.lcims.lcbmi.weixin.server;

public interface WXService extends java.rmi.Remote {
    public com.asiainfo.lcims.lcbmi.weixin.server.CloseWXUserResp closeWXUser(com.asiainfo.lcims.lcbmi.weixin.server.CloseWXUserReq request) throws java.rmi.RemoteException;
    public com.asiainfo.lcims.lcbmi.weixin.server.OpenWXUserResp openWXUser(com.asiainfo.lcims.lcbmi.weixin.server.OpenWXUserReq request) throws java.rmi.RemoteException;
    public com.asiainfo.lcims.lcbmi.weixin.server.QueryWXUserResp queryWXUser(com.asiainfo.lcims.lcbmi.weixin.server.QueryWXUserReq request) throws java.rmi.RemoteException;
    public com.asiainfo.lcims.lcbmi.weixin.server.BindWXUserResp bindWXUser(com.asiainfo.lcims.lcbmi.weixin.server.BindWXUserReq request) throws java.rmi.RemoteException;
}
