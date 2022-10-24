<%@ page language="java" contentType="text/html;charset=utf-8"%>
<%@ page import="java.net.*" %>
<% String  serverName=InetAddress.getLocalHost().getHostName();
String  serverIP=InetAddress.getLocalHost().getHostAddress();
String userIP=request.getHeader("x-real-ip");
if(userIP==null || userIP.trim().equals("")){
	userIP=request.getRemoteAddr();
}
%>
<%out.print("userIP:"+userIP+"<BR>");%>
<%out.print("serverName:"+serverName+"<BR>");%>
<%out.print("serverIP:"+serverIP);%>
<%out.print("<br>The server is healthy !");%>