<%@page import="com.ailk.wxserver.service.constant.WXReqTypeConstant"%>
<%@page pageEncoding="utf-8"%>
<%@page import="com.ailk.wxserver.service.constant.ParamConstant"%>
<%@page import="com.ailk.wxserver.service.constant.WXAuthContant"%>
<%@page import="com.ailk.wxserver.util.RequestParamUtil"%>
<%@page import="com.ailk.wxserver.util.MessageUtil"%>
<%@page import="com.ailk.wxserver.util.StringUtils"%>
<%@page import="java.util.Map"%>
<% 
String paramStr = request.getParameter(ParamConstant.KEY_PARAMSTR);
Map<String,String> paramMap=RequestParamUtil.StringValueToMap(paramStr);
String vt = paramMap.get(WXAuthContant.WX_VERIFY_REQNAME);
boolean backButton = false;
if(WXReqTypeConstant.WX_REQTYPE_VERIFY_TYPE_VERIFYCODE.equals(vt)){
	backButton = true;
}
paramMap.remove(WXAuthContant.WX_VERIFY_REQNAME);
String paramStrEnc = RequestParamUtil.MapValueToString(paramMap);
String replyMsgCode = (String)paramMap.get(ParamConstant.KEY_REPLYMSG_CODE);
String eccode = (String)paramMap.get(ParamConstant.KEY_ECCODE);
String replyMsg = MessageUtil.getMessage(eccode+"_"+WXAuthContant.MSG_FUNCNAME_WX_VERIFY,WXAuthContant.MSG_FUNCNAME_WX_VERIFY,
		String.valueOf(replyMsgCode));
if(StringUtils.isEmpty(replyMsg)){
	replyMsg = "";
}
%>
<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>
<html>
<head>
<meta charset="utf-8">
<title></title>
<meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0" name="viewport">
<link href="css/style.css" rel="stylesheet" type="text/css" />
</head>

<body>
<div class="top"><img src="images/top.jpg" alt=""/></div>
  <ul class="e_main">
  <h2 class="red">很抱歉！验证失败！</h2>   
     <li><p><%=replyMsg %>。</p></li> 
     <% if(backButton){ %>
     <li><input name="Submit" type="submit" class="login_btn" value="重新验证" onclick="window.location.href='verify.jsp?paramStr=<%=paramStrEnc%>';"/></li>
     <% } %>         
  </ul>
</body>
</html>
