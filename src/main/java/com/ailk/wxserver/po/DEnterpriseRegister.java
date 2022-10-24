package com.ailk.wxserver.po;

/**
 * 集团客户与商户注册号应关系表
 * 
 * @author zhoutj
 *
 */
public class DEnterpriseRegister {

    private String eccode;
    private String registerid;
    private int apptype;
    private String token;
    private String aeskey;
    private String uncode;
    private int accesstype;
    private String appid;
    private String shopid;
    private String ssid;
    private String bssid;
    private String secretkey;
    private int status;

    public String getEccode() {
        return eccode;
    }

    public String getRegisterid() {
        return registerid;
    }

    public int getApptype() {
        return apptype;
    }

    public String getToken() {
        return token;
    }

    public String getAeskey() {
        return aeskey;
    }

    public String getUncode() {
        return uncode;
    }

    public void setEccode(String eccode) {
        this.eccode = eccode;
    }

    public void setRegisterid(String registerid) {
        this.registerid = registerid;
    }

    public void setApptype(int apptype) {
        this.apptype = apptype;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setAeskey(String aeskey) {
        this.aeskey = aeskey;
    }

    public void setUncode(String uncode) {
        this.uncode = uncode;
    }

    public int getAccesstype() {
        return accesstype;
    }

    public String getAppid() {
        return appid;
    }

    public void setAccesstype(int accesstype) {
        this.accesstype = accesstype;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getShopid() {
        return shopid;
    }

    public String getSsid() {
        return ssid;
    }

    public String getBssid() {
        return bssid;
    }

    public String getSecretkey() {
        return secretkey;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public void setSecretkey(String secretkey) {
        this.secretkey = secretkey;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
