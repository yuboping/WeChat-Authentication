package com.ailk.wxserver.ci.client.smgp;


import java.net.*;
import java.io.*;

import org.apache.log4j.Logger;

import com.ailk.wxserver.util.LogUtil;
import com.ailk.wxserver.util.log.LogFactory;

public class SocketClient {
	
	private String ip="127.0.0.1";
	private int port=9987;

	private Logger log = LogFactory.getLogger("smgpsms");

    public SocketClient(String ip, int port) {

    		this.ip=ip;
    		this.port=port;
    }
    


    public int sendSigle(String phonenumber,String message) {
    	
    	int logid = (int) (Math.random() * Integer.MAX_VALUE);
    	Socket clientSocket = null;
    	DataOutputStream out = null;
    	DataInputStream in = null;
    	    
		try {
			
			log.info("logid["+logid+"] phonenumber["+phonenumber+"] message["+message+"]");
			if (null==message|| message.trim().equals("")||null==phonenumber|| phonenumber.trim().equals("")) {
				log.error("logid["+logid+"] input is null");
				return -1;
			}
			
			String line = phonenumber+"|||"+message+"\n";	
			
    	    clientSocket = new Socket(ip, port);
    	    
    	    out = new DataOutputStream(clientSocket.getOutputStream());
    	    byte[] cbuf = line.getBytes();
    	    out.write(cbuf);
    	    out.flush();
    	    
    	    in = new DataInputStream(clientSocket.getInputStream());
			
    	    // 接收返回结果,等待处理.
			
			byte[] b = new byte[8192];
			String returnValues = "";
			try {
			    in.read(b);
			    returnValues = (new String(b)).trim();
			} catch (Exception e) {
				log.error("logid["+logid+"] Exception ["+e.getMessage()+"]");
				LogUtil.printErrorStackTrace(log, e);
				return -1;
			}
			log.info("logid["+logid+"]  phonenumber["+phonenumber+"] returnValues["+returnValues+"]");
			
			if (null==returnValues||"".equals(returnValues.trim())) {
				log.error("logid["+logid+"] returnValues  is null");
				return -1;
			}
			String[] strs= returnValues.split("\\|\\|\\|");
			
			if(null==strs||strs.length!=2){
				log.error("logid["+logid+"] returnValues split is error");
				return -1;
			}
			if("0".equals(strs[0])){
				log.info("logid["+logid+"]sms sucess");
				return 0;
			}else{
				log.error("logid["+logid+"]sms fail,returnValues["+returnValues+"]");
				return -1;
			}

			
		} catch (Exception e) {

			LogUtil.printErrorStackTrace(log, e);
			return -1;
		}finally{
			try {
				if(out!=null)out.close();
				if(in!=null)in.close();
				if(clientSocket!=null)clientSocket.close();
			} catch (Exception e) {
				LogUtil.printErrorStackTrace(log, e);
			}
		}
		
	}
    
    // 负责向服务器发送和接受数据流.
    public static void main(String[] args){
    }
}
