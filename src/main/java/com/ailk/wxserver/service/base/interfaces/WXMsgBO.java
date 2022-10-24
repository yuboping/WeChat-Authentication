package com.ailk.wxserver.service.base.interfaces;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.ailk.wxserver.model.WXMsgModel;
import com.ailk.wxserver.util.ResponseResult;
import com.ailk.wxserver.util.qq.wx.AesException;

public interface WXMsgBO {

	/**
	 * 获取微信消息内容O
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public String getRequestPostMsg(HttpServletRequest request) throws IOException;

	
	
	/**
	 * 解密消息体
	 * 根据加密参数确定是否要解密
	 * 
	 * @return
	 * @throws AesException
	 */
	public ResponseResult decryptMsg(Map<String, String> paramMap);
	
	
	/**
	 * 解析消息，生成model
	 * 
	 * @param paramMap
	 * @return
	 * @throws IOException
	 */
	public WXMsgModel parseMessage(String message) throws IOException ;
	
	
	/**
	 * 构造微信回复文本消息
	 * 明文
	 * @param wxMsgModel
	 * @return
	 */
	public String formReplyPlainMsg(WXMsgModel wxMsgModel,String content);
	
	/**
	 * 回应消息加密处理
	 * @param paramMap
	 * @param msg
	 * @return
	 */
	public ResponseResult encryptReplyMsg(Map<String, String> paramMap,WXMsgModel wxMsgModel,String replymsg);
	
	
	/**
	 * 从消息内容中提取手机号
	 * @param content
	 * @param regex
	 * @return
	 */
	public String getPhone(String content,String regex);
	
	/**
	 * 关注事件
	 * @param paramMap
	 * @param wxMsgModel
	 * @return
	 */
	public ResponseResult event_subscribe(Map<String, String> paramMap,
			WXMsgModel wxMsgModel) ;
	
	/**
	 * 取消关注
	 * 
	 * @param paramMap
	 * @param wxMsgModel
	 * @return
	 */
	public ResponseResult event_unsubscribe(Map<String, String> paramMap,
			WXMsgModel wxMsgModel);
	
	/**
	 * 已关注用户，扫描带参数二维码事件
	 * 回应空消息 
	 * 
	 * @param paramMap
	 * @param wxMsgModel
	 * @return
	 */
	public ResponseResult event_scan(Map<String, String> paramMap,
			WXMsgModel wxMsgModel) ;
	
	/**
	 * 微信连wifi事件
	 * @param paramMap
	 * @param wxMsgModel
	 * @return
	 */
	public ResponseResult event_wifiConnected(Map<String, String> paramMap,WXMsgModel wxMsgModel);
	
	/**
	 * 其他事件
	 * @param paramMap
	 * @param wxMsgModel
	 * @return
	 */
	public ResponseResult event_other(Map<String, String> paramMap,
			WXMsgModel wxMsgModel) ;
	
	/**
	 * 绑定手机号验证消息
	 * @param paramMap
	 * @param wxMsgModel
	 * @return
	 */
	public ResponseResult msg_bindphone(Map<String, String> paramMap,WXMsgModel wxMsgModel);
	
	/**
	 * 获取上网链接消息
	 * @param paramMap
	 * @param wxMsgModel
	 * @return
	 */
	public ResponseResult msg_getlink(Map<String, String> paramMap,WXMsgModel wxMsgModel);
	
	/**
	 * 其他消息
	 * @param paramMap
	 * @param wxMsgModel
	 * @return
	 */
	public ResponseResult msg_other(Map<String, String> paramMap,WXMsgModel wxMsgModel);
}