package com.ailk.wxserver.util;

import java.io.IOException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


public class EncodDecodeUtil {
	public static byte[] encryptMode(byte[] keybyte, byte[] src) {
		try {

			SecretKey deskey = new SecretKeySpec(keybyte, "DESede");
			// 加密
			Cipher c1 = Cipher.getInstance("DESede/ECB/PKCS5Padding");

			c1.init(Cipher.ENCRYPT_MODE, deskey);

			return c1.doFinal(src);
		} catch (java.lang.Exception e) {
			return null;
		}
		
	}
	
	public static byte[] decryptMode(byte[] keybyte, byte[] src) {
		try {
			// 生成密钥
			SecretKey deskey = new SecretKeySpec(keybyte, "DESede");
			// 解密
			Cipher c1 = Cipher.getInstance("DESede/ECB/PKCS5Padding");

			c1.init(Cipher.DECRYPT_MODE, deskey);
			return c1.doFinal(src);
		} catch (java.lang.Exception e) {
			return null;
		}
	}
	
	public static String Base64Encode( byte[] src) {
		
		BASE64Encoder encode=new BASE64Encoder();
		return encode.encode(src);
	}
	
	public static byte[] Base64Decode( String str) {
		
		BASE64Decoder decode=new BASE64Decoder();
		try {
			return decode.decodeBuffer(str);
		} catch (IOException e) {
			return null;
		}
	}
	
	public static String Base64EncodeNoEnter( byte[] src) {
		return new String(Base64.encodeBase64(src));
	}
	
	public static byte[] Base64DecodeNoEnter( String str) {
		
		return Base64.decodeBase64(str.getBytes());
	}
	
	public static String getByteStr(byte[] byt) {
		String strRet = "";
		for (int i = 0; i < byt.length; i++) {
			// System.out.println(byt[i]);
			strRet += getHexValue((byt[i] & 240) / 16);
			strRet += getHexValue(byt[i] & 15);
		}
		return strRet;
	}

	/**
	 * 输入密码的字符形式，返回字节数组形式。 如输入字符串：AD67EA2F3BE6E5AD
	 * 返回字节数组：{173,103,234,47,59,230,229,173}
	 */
	public static byte[] getKeyByStr(String str) {
		byte[] bRet = new byte[str.length() / 2];
		for (int i = 0; i < str.length() / 2; i++) {
			Integer itg =16 * getChrInt(str.charAt(2 * i))
					+ getChrInt(str.charAt(2 * i + 1));
			bRet[i] = itg.byteValue();
		}
		return bRet;
	}

	public static String getHexValue(int s) {
		String sRet = null;
		switch (s) {
		case 0:
			sRet = "0";
			break;
		case 1:
			sRet = "1";
			break;
		case 2:
			sRet = "2";
			break;
		case 3:
			sRet = "3";
			break;
		case 4:
			sRet = "4";
			break;
		case 5:
			sRet = "5";
			break;
		case 6:
			sRet = "6";
			break;
		case 7:
			sRet = "7";
			break;
		case 8:
			sRet = "8";
			break;
		case 9:
			sRet = "9";
			break;
		case 10:
			sRet = "A";
			break;
		case 11:
			sRet = "B";
			break;
		case 12:
			sRet = "C";
			break;
		case 13:
			sRet = "D";
			break;
		case 14:
			sRet = "E";
			break;
		case 15:
			sRet = "F";
		}
		return sRet;
	}

	/**
	 * 计算一个16进制字符的10进制值 输入：0-F
	 */
	public static int getChrInt(char chr) {
		int iRet = 0;
		if (chr == "0".charAt(0))
			iRet = 0;
		if (chr == "1".charAt(0))
			iRet = 1;
		if (chr == "2".charAt(0))
			iRet = 2;
		if (chr == "3".charAt(0))
			iRet = 3;
		if (chr == "4".charAt(0))
			iRet = 4;
		if (chr == "5".charAt(0))
			iRet = 5;
		if (chr == "6".charAt(0))
			iRet = 6;
		if (chr == "7".charAt(0))
			iRet = 7;
		if (chr == "8".charAt(0))
			iRet = 8;
		if (chr == "9".charAt(0))
			iRet = 9;
		if (chr == "A".charAt(0))
			iRet = 10;
		if (chr == "B".charAt(0))
			iRet = 11;
		if (chr == "C".charAt(0))
			iRet = 12;
		if (chr == "D".charAt(0))
			iRet = 13;
		if (chr == "E".charAt(0))
			iRet = 14;
		if (chr == "F".charAt(0))
			iRet = 15;
		return iRet;
	}
}
