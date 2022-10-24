package com.ailk.wxserver.model;

/**
 * POST的XML数据包转换为消息接受对象
 * 
 * <p>
 * 由于POST的是XML数据包，所以不确定为哪种接受消息，<br/>
 * 所以直接将所有字段都进行转换，最后根据<tt>MsgType</tt>字段来判断取何种数据
 * </p>
 * 
 */
public class WXMsgModel {
	private String ToUserName;
	private String FromUserName;
	private Long CreateTime;
	private String MsgType = "text";
	private Long MsgId;
	// 文本消息
	private String Content;
	// 图片消息
	private String PicUrl;
	// 位置消息
	private String LocationX;
	private String LocationY;
	private Long Scale;
	private String Label;
	// 链接消息
	private String Title;
	private String Description;
	private String Url;
	// 语音信息
	private String MediaId;
	private String Format;
	private String Recognition;
	// 事件
	private String Event;
	private String EventKey;
	private String Ticket;

	//密文部分
	private String Encrypt;
	
	//连网时间
	private Long ConnectTime;
	//系统保留字段，固定值
	private String ExpireTime;
	//系统保留字段，固定值
	private String VendorId;
	//门店ID
	private String ShopId;
	//连网的设备无线mac地址
	private String DeviceNo;
	
	public String getEncrypt() {
		return Encrypt;
	}

	public void setEncrypt(String encrypt) {
		Encrypt = encrypt;
	}

	public String getToUserName() {
		return ToUserName;
	}

	public void setToUserName(String toUserName) {
		ToUserName = toUserName;
	}

	public String getFromUserName() {
		return FromUserName;
	}

	public void setFromUserName(String fromUserName) {
		FromUserName = fromUserName;
	}

	public Long getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(Long createTime) {
		CreateTime = createTime;
	}

	public String getMsgType() {
		return MsgType;
	}

	public void setMsgType(String msgType) {
		MsgType = msgType;
	}

	public Long getMsgId() {
		return MsgId;
	}

	public void setMsgId(Long msgId) {
		MsgId = msgId;
	}

	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		Content = content;
	}

	public String getPicUrl() {
		return PicUrl;
	}

	public void setPicUrl(String picUrl) {
		PicUrl = picUrl;
	}

	public String getLocationX() {
		return LocationX;
	}

	public void setLocationX(String locationX) {
		LocationX = locationX;
	}

	public String getLocationY() {
		return LocationY;
	}

	public void setLocationY(String locationY) {
		LocationY = locationY;
	}

	public Long getScale() {
		return Scale;
	}

	public void setScale(Long scale) {
		Scale = scale;
	}

	public String getLabel() {
		return Label;
	}

	public void setLabel(String label) {
		Label = label;
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public String getUrl() {
		return Url;
	}

	public void setUrl(String url) {
		Url = url;
	}

	public String getEvent() {
		return Event;
	}

	public void setEvent(String event) {
		Event = event;
	}

	public String getEventKey() {
		return EventKey;
	}

	public void setEventKey(String eventKey) {
		EventKey = eventKey;
	}

	public String getMediaId() {
		return MediaId;
	}

	public void setMediaId(String mediaId) {
		MediaId = mediaId;
	}

	public String getFormat() {
		return Format;
	}

	public void setFormat(String format) {
		Format = format;
	}

	public String getRecognition() {
		return Recognition;
	}

	public void setRecognition(String recognition) {
		Recognition = recognition;
	}

	public String getTicket() {
		return Ticket;
	}

	public void setTicket(String ticket) {
		Ticket = ticket;
	}

	public Long getConnectTime() {
		return ConnectTime;
	}

	public String getExpireTime() {
		return ExpireTime;
	}

	public String getVendorId() {
		return VendorId;
	}

	public String getShopId() {
		return ShopId;
	}

	public String getDeviceNo() {
		return DeviceNo;
	}

	public void setConnectTime(Long connectTime) {
		ConnectTime = connectTime;
	}

	public void setExpireTime(String expireTime) {
		ExpireTime = expireTime;
	}

	public void setVendorId(String vendorId) {
		VendorId = vendorId;
	}

	public void setShopId(String shopId) {
		ShopId = shopId;
	}

	public void setDeviceNo(String deviceNo) {
		DeviceNo = deviceNo;
	}

	@Override
	public String toString() {
		return "WXMsgModel [ToUserName=" + ToUserName + ", FromUserName="
				+ FromUserName + ", CreateTime=" + CreateTime + ", MsgType="
				+ MsgType + ", MsgId=" + MsgId + ", Content=" + Content
				+ ", PicUrl=" + PicUrl + ", LocationX=" + LocationX
				+ ", LocationY=" + LocationY + ", Scale=" + Scale + ", Label="
				+ Label + ", Title=" + Title + ", Description=" + Description
				+ ", Url=" + Url + ", MediaId=" + MediaId + ", Format="
				+ Format + ", Recognition=" + Recognition + ", Event=" + Event
				+ ", EventKey=" + EventKey + ", Ticket=" + Ticket
				+ ", Encrypt=" + Encrypt + ", ConnectTime=" + ConnectTime
				+ ", ExpireTime=" + ExpireTime + ", VendorId=" + VendorId
				+ ", ShopId=" + ShopId + ", DeviceNo=" + DeviceNo + "]";
	}
	
}
