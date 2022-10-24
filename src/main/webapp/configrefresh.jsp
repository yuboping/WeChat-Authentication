<%@page import="com.ailk.lcims.support.mp.client.MPClientUtils"%>
<%@ page language="java" pageEncoding="utf-8"%>

<%@page import="com.ailk.wxserver.service.main.interfaces.RefreshProcessBO"%>
<%@page import="com.ailk.wxserver.util.spring.BeanUtil"%>
<%@ page import="java.util.List"%>
<%@ page import="java.net.*" %>
<%  
	List<String> configNames=MPClientUtils.callService("/mp/portal/config/webconsole/list",new Object[]{"config"}, List.class);
	List<String> memoryParamNames=MPClientUtils.callService("/mp/portal/config/webconsole/list",new Object[]{"mp"}, List.class);
	RefreshProcessBO refreshProcessBO=(RefreshProcessBO)BeanUtil.getBean("refreshProcessBO");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>配置刷新</title>
<script type="text/javascript">
function selectAllConfig(check){
	var configs=document.getElementsByName("config");
	if(check.checked==true){
		for(i=0;i<configs.length;i++){
			configs[i].checked="ischecked";
		}
	}
	else{
		for(i=0;i<configs.length;i++){
			configs[i].checked="";
		}
	}
}
function selectAllMP(check){
	var memoryParams=document.getElementsByName("mp");
	if(check.checked==true){
		for(i=0;i<memoryParams.length;i++){
			memoryParams[i].checked="ischecked";
		}
	}
	else{
		for(i=0;i<memoryParams.length;i++){
			memoryParams[i].checked="";
		}
	}
}
function selectAllProperty(check){
	var propertyParams=document.getElementsByName("property");
	if(check.checked==true){
		for(i=0;i<propertyParams.length;i++){
			propertyParams[i].checked="ischecked";
		}
	}
	else{
		for(i=0;i<propertyParams.length;i++){
			propertyParams[i].checked="";
		}
	}
}
</script>
</head>
<body>
<form action="configrefresh.jsp" method="post">
<table align="center" >
	<tr>
		<td valign="top" width="240">
			<table align="center"  border="0">
				<tr>
				<td colspan="2" align="center"><h4>配置文件刷新</h4></td>
				</tr>
				<tr>
				<td align="center" ><input type="checkbox" onclick="selectAllConfig(this)">全选</td>
				<td align="center" >配置文件</td>
				</tr>
				<%if(configNames!=null&&!configNames.isEmpty()){
				    for(int i=0;i<configNames.size();i++){
				    String name=(String)configNames.get(i);
				    %>
				<tr >
					<td align="left" ><input name="config" type="checkbox" value="<%=name %>"></td>
					<td align="center" ><%=name %></td>
				</tr>
				    <%
				    }
				} %>
			</table>
		</td>
		<td valign="top" width="240">
			<table align="center"  border="0">
				<tr>
				<td colspan="2" align="center" ><h4>内存参数刷新</h4></td>
				</tr>
				<tr>
				<td align="center" ><input type="checkbox" onclick="selectAllMP(this)">全选</td>
				<td align="center" >内存参数</td>
				</tr >
				<%if(memoryParamNames!=null&&!memoryParamNames.isEmpty()){
				    for(int i=0;i<memoryParamNames.size();i++){
				    String name=(String)memoryParamNames.get(i);
				    %>
				<tr >
				<td align="left" ><input name="mp" type="checkbox" value="<%=name %>"></td>
				<td align="center" ><%=name %></td>
				</tr>
				    <%
				    }
				} %>
			</table>
		</td>
	</tr>
	<tr>
		<td colspan="3" align="center" height="50" valign="center"><input type="submit" value="  刷  新  " ></td>
	</tr>
</table>
</form>
<table align="center" >
<% String[] configs=request.getParameterValues("config");
        if(configs!=null){
        	for(int i=0;i<configs.length;i++){
        	   	out.print("<tr><td>"+configs[i]+":"+refreshProcessBO.reload("config",configs[i],true)+";</td></tr>");
          }
        }
        out.print("<tr><td><br/></td></tr>");
        String[] memoryParams=request.getParameterValues("mp");
        if(memoryParams!=null){
			for(int i=0;i<memoryParams.length;i++){
            	out.print("<tr><td>"+memoryParams[i]+":"+refreshProcessBO.reload("mp",memoryParams[i],true)+";</td></tr>");
          }
        }
        out.print("<tr><td><br/></td></tr>");
        
        String[] porperty=request.getParameterValues("property");
        if(porperty!=null){
			for(int i=0;i<porperty.length;i++){
            	out.print("<tr><td>"+porperty[i]+":"+refreshProcessBO.reload("property",porperty[i],true)+";</td></tr>");
          }
        }
%>
 </table>
</body>
</html>