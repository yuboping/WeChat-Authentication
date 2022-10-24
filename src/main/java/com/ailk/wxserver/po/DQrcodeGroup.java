package com.ailk.wxserver.po;

public class DQrcodeGroup {

	private int groupid;
	private String eccode;
	private String scene;
	private String ticket;
	private String qrurl;
	
	public int getGroupid() {
		return groupid;
	}
	public String getEccode() {
		return eccode;
	}
	public String getScene() {
		return scene;
	}
	public String getTicket() {
		return ticket;
	}
	public String getQrurl() {
		return qrurl;
	}
	public void setGroupid(int groupid) {
		this.groupid = groupid;
	}
	public void setEccode(String eccode) {
		this.eccode = eccode;
	}
	public void setScene(String scene) {
		this.scene = scene;
	}
	public void setTicket(String ticket) {
		this.ticket = ticket;
	}
	public void setQrurl(String qrurl) {
		this.qrurl = qrurl;
	}
	@Override
	public String toString() {
		return "DQrcodeGroup [groupid=" + groupid + ", eccode=" + eccode
				+ ", scene=" + scene + ", ticket=" + ticket + ", qrurl="
				+ qrurl + "]";
	}
	
}
