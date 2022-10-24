package com.ailk.wxserver.po;

import java.util.Date;

public class BSpecialUser {

	public static final String TABLE_NAME = "B_SPECIAL_USER";
	
	private String openid;
	private String eccode;
	private int apptype;
	private String phone;
	private int verifyStatus;
	private int openuserStatus;
	private Date verifyexpireDate;
	private Date openDate;
	private Date modDate;
	private String username;
	private int qrgroupid;
	
	
	public String getOpenid() {
		return openid;
	}
	public String getEccode() {
		return eccode;
	}
	public String getPhone() {
		return phone;
	}
	public int getVerifyStatus() {
		return verifyStatus;
	}
	public int getOpenuserStatus() {
		return openuserStatus;
	}
	public Date getOpenDate() {
		return openDate;
	}
	public Date getModDate() {
		return modDate;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public void setEccode(String eccode) {
		this.eccode = eccode;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public void setVerifyStatus(int verifyStatus) {
		this.verifyStatus = verifyStatus;
	}
	public void setOpenuserStatus(int openuserStatus) {
		this.openuserStatus = openuserStatus;
	}
	public void setOpenDate(Date openDate) {
		this.openDate = openDate;
	}
	public void setModDate(Date modDate) {
		this.modDate = modDate;
	}
	public Date getVerifyexpireDate() {
		return verifyexpireDate;
	}
	public void setVerifyexpireDate(Date verifyexpireDate) {
		this.verifyexpireDate = verifyexpireDate;
	}
	public int getApptype() {
		return apptype;
	}
	public void setApptype(int apptype) {
		this.apptype = apptype;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public int getQrgroupid() {
		return qrgroupid;
	}
	public void setQrgroupid(int qrgroupid) {
		this.qrgroupid = qrgroupid;
	}
	@Override
	public String toString() {
		return "BSpecialUser [openid=" + openid + ", eccode=" + eccode
				+ ", apptype=" + apptype + ", phone=" + phone
				+ ", verifyStatus=" + verifyStatus + ", openuserStatus="
				+ openuserStatus + ", verifyexpireDate=" + verifyexpireDate
				+ ", openDate=" + openDate + ", modDate=" + modDate
				+ ", username=" + username + ", qrgroupid=" + qrgroupid + "]";
	}

}
