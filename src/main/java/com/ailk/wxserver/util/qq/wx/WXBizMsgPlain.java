package com.ailk.wxserver.util.qq.wx;

import org.apache.log4j.Logger;

import com.ailk.wxserver.util.LogUtil;
import com.ailk.wxserver.util.log.LogFactory;
import com.ailk.wxserver.util.sha.SHA1;


/**
 * 不加密
 * @author zhoutj
 *
 */
public class WXBizMsgPlain {
	public static final Logger error = LogFactory.getLogger("error");
	
	String token;

	public WXBizMsgPlain(String token) throws AesException {

		this.token = token;
	}

	/**
	 * 在非加密方式下校验url
	 * @param signature
	 * @param timeStamp
	 * @param nonce
	 * @param echostr
	 * @return 原值echostr
	 * @throws AesException
	 */
	public String verifyUrlNoEncry(String signature,String timeStamp,String nonce,String echostr) throws AesException{
		String result = "";
		String digest;
		try {
			digest = SHA1.getSHA1(new String[] { token, timeStamp,
					nonce });
		} catch (Exception e) {
			LogUtil.printErrorStackTrace(error, e);
			throw new AesException(AesException.ComputeSignatureError);
		}
		//签名校验
		if (digest.equals(signature)) {
			result = echostr;
		}else{
			throw new AesException(AesException.ValidateSignatureError);
		}
		return result;
	}
}