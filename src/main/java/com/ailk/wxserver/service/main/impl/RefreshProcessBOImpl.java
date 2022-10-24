package com.ailk.wxserver.service.main.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ailk.lcims.support.mp.client.MPClientUtils;
import com.ailk.lcims.support.util.CollectionUtils;
import com.ailk.wxserver.service.constant.RefreshConstant;
import com.ailk.wxserver.service.main.interfaces.RefreshProcessBO;
import com.ailk.wxserver.util.LogUtil;
import com.ailk.wxserver.util.log.LogFactory;

public class RefreshProcessBOImpl implements RefreshProcessBO{
	private Logger log = LogFactory.getLogger("error");
	
	/**
	 * COMMAND(参数名1=参数值1|参数值2|…,参数名2=参数值3|参数值4|…,…)
	 */
	@Override
	public void doRefresh(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		
		//ip地址校验
		String reqip=request.getRemoteAddr();
		if(!isAcceptedIp(reqip)){
			log.error("WebConsole error:Request IP["+reqip+"] isn't accepted.");
			out.print("error(info=Request IP["+reqip+"] isn't accepted.)");
			return;
		}
		
		String command = request.getParameter("command");
		//命令合法性校验
		if(!isValidCommand(command)){
			log.error("WebConsole error:Command["+command+"] isn't validate.");
			out.print("error(info=Command["+command+"] isn't validate.)");
			return;
		}
		
		out.print(process(command));
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private String process(String command){
		String commandtmp[]=command.split("\\(");
		String commandName=commandtmp[0];
		String commandContent="";
		if(commandtmp.length==2){
			String[] tmps=commandtmp[1].split("\\)");
			if(null!=tmps&&tmps.length>0){
				commandContent=tmps[0];
			}
		}
		
		boolean bRefresh = true;
		HashMap[] operateMaps=null;
		if(!commandContent.trim().equals("")){
			String operate[]=commandContent.split(",");
			operateMaps=new HashMap[operate.length];
			for(int i=0;i<operate.length;i++){
				String operatetmp[]=operate[i].split("=");
				String operateName=operatetmp[0];
				if(operatetmp.length==2){
					if(commandName.equalsIgnoreCase(RefreshConstant.COMMAND_RELOAD)&&operateName.equals("immediately")){
						bRefresh=false;
						operateMaps[i]=new HashMap();
						operateMaps[i].put("key", operateName);
					}else{
						String paramters[]=operatetmp[1].split("\\|");
						operateMaps[i]=new HashMap();
						operateMaps[i].put("key", operateName);
						operateMaps[i].put("value", paramters);
					}
					
				}else{
					operateMaps[i]=new HashMap();
					operateMaps[i].put("key", operateName);
				}
			}
		}
		StringBuffer sb = new StringBuffer();
		//reload
		if(commandName.equalsIgnoreCase(RefreshConstant.COMMAND_RELOAD)){
			sb.append("reload(");
			for(int i=0;i<operateMaps.length;i++){
				String operateName = (String) operateMaps[i].get("key");
				if(operateName.equals("immediately")){
					continue;
				}
				String paramters[] = (String[])  operateMaps[i].get("value");
				sb.append(operateName+"=");
				for(int j=0;j<paramters.length;j++){
					sb.append(reload(operateName,paramters[j],bRefresh));
					if(j<paramters.length-1){
						sb.append("|");
					}
				}
				if(i<operateMaps.length-1){
					sb.append(",");
				}
			}
			sb.append(")");
		}
		//list
		if(commandName.equalsIgnoreCase(RefreshConstant.COMMAND_LIST)){
			sb.append("list(");
			if(operateMaps==null){
				sb.append("config="+list("config")+",mp="+list("mp")+",property="+list("property"));
			}else{
				for(int i=0;i<operateMaps.length;i++){
					String operateName = (String) operateMaps[i].get("key");
					
					sb.append(operateName+"="+list(operateName));
					if(i<operateMaps.length-1){
						sb.append(",");
					}
				}
			}
			sb.append(")");
		}
		//info
		if(commandName.equalsIgnoreCase(RefreshConstant.COMMAND_INFO)){
			
			
			String operateName = (String) operateMaps[0].get("key");
			String paramters[] = (String[])  operateMaps[0].get("value");
			sb.append("info(info=<![CDATA[");
			sb.append(info(operateName, paramters[0]));
			sb.append("]]>)");
			
		}
		// status
		if (commandName.equalsIgnoreCase(RefreshConstant.COMMAND_STATUS)) {
			sb.append("status(result=ok)");
		}
		
		return sb.toString();
		
	}
	
	public boolean reload(String operateName,String paramterName, boolean bRefresh) {
		try {
			
			String serviceName=MPClientUtils.callService("/mp/portal/config/webconsole/getRefreshName",new Object[]{operateName,paramterName}, String.class);
			
			MPClientUtils.refreshMP(serviceName);
			return true;

		} catch (Exception e) {
			LogUtil.printErrorStackTrace(log, e);
			return false;
		}

	}
	
	
	public String list(String operateName) {
		try {
			
			@SuppressWarnings("unchecked")
			List<String> list=MPClientUtils.callService("/mp/portal/config/webconsole/list",new Object[]{operateName}, List.class);
			if(list==null||list.size()==0){
				return null;
			}
			StringBuffer sb=new StringBuffer("");
			int i=0;
			for(String str:list){
				if(i>0){
					sb.append("|");
				}
				sb.append(str);
				i++;				
			}
			return sb.toString();

		} catch (Exception e) {
			LogUtil.printErrorStackTrace(log, e);
			return "";
		}

	}
	
	private String info(String operateName,String paramterName){
		return "Not implement.";
	}
	
	/**
	 * 校验是否为允许的ip
	 * @param ip
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean isAcceptedIp(String ip){
		if(ip==null){
			return false;
		}
		List<String> list=MPClientUtils.callService("/mp/portal/config/webconsole/getIplimit",null, List.class);
		if(!CollectionUtils.isEmpty(list)){
			for(int i=0;i<list.size();i++){
				if(ip.equals((String)list.get(i))) return true;
			}
		}
		
		return false;
	}
	/**
	 * 是否是合法的指令
	 * COMMAND(参数名1=参数值1|参数值2|…,参数名2=参数值3|参数值4|…,…)
	 * @param command
	 * @return
	 */
	private boolean isValidCommand(String command){
		String regex="^\\w+(\\(((\\w+(=(\\w+)(\\|\\w+)*)*)(,\\w+(=(\\w+)(\\|\\w+)*)*)*)?\\))?$";
		try {
			regex=MPClientUtils.callService("/mp/p/portal/config/webconsole/getComandRule",null, String.class);
		} catch (Exception e) {
			LogUtil.printErrorStackTrace(log, e);
			return false;
		}
		if(command!=null&&command.matches(regex)){
			return true;
		}
		
		return false;
	}
	
}
