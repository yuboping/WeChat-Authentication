package com.ailk.wxserver.util.md5;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

	/**
	 * md5签名
	 * @return
	 * @throws NoSuchAlgorithmException 
	 */
	public static String sign(String soruce) throws NoSuchAlgorithmException{
		 MessageDigest md5;  
	     md5 = MessageDigest.getInstance("MD5"); 
	     md5.update(soruce.getBytes()); //先更新摘要  
	     byte[] digest = md5.digest();
	     String hex = toHex(digest);  
	     return hex;
	}
	
	/** 
     * md5 摘要转16进制 
     * @param digest 
     * @return 
     */  
    private static String toHex(byte[] digest) {  
        StringBuilder sb = new StringBuilder();  
        int len = digest.length;  
          
        String out = null;  
        for (int i = 0; i < len; i++) {  
//          out = Integer.toHexString(0xFF & digest[i] + 0xABCDEF); //加任意 salt  
            out = Integer.toHexString(0xFF & digest[i]);//原始方法  
            if (out.length() == 1) {  
                sb.append("0");//如果为1位 前面补个0  
            }  
            sb.append(out);  
        }  
        return sb.toString();  
    }  
}
