package com.ailk.wxserver.util.sha;

import java.security.MessageDigest;
import java.util.Arrays;

public class SHA1 {

	/**
	 * 对fields数组进行字典排序后，然后按照顺序组成字符串，再进行SHA1加密
	 * @param fields
	 * @return
	 * @throws Exception
	 */
	public static String getSHA1(String[] fields) throws Exception {
		// 字典序排序
		Arrays.sort(fields);
		StringBuffer sb = new StringBuffer();
		for (String field : fields) {
			sb.append(field);
		}
		// SHA1加密
		String digest = getSHA1(sb.toString());
		return digest;
	}

	/**
	 * 用SHA1算法生成安全签名
	 * 
	 * @return 安全签名
	 * @throws Exception
	 * @throws AesException
	 */
	public static String getSHA1(String source) throws Exception {
		String str = source;
		// SHA1签名生成
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.update(str.getBytes());
		byte[] digest = md.digest();

		StringBuffer hexstr = new StringBuffer();
		String shaHex = "";
		for (int i = 0; i < digest.length; i++) {
			shaHex = Integer.toHexString(digest[i] & 0xFF);
			if (shaHex.length() < 2) {
				hexstr.append(0);
			}
			hexstr.append(shaHex);
		}
		return hexstr.toString();
	}
}
