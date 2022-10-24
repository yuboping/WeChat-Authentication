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
String eccode = (String)paramMap.get(ParamConstant.KEY_ECCODE);
String paramStrEnc = RequestParamUtil.MapValueToString(paramMap);
%>
<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>
<html>
<head>
<meta charset="utf-8">
<title></title>
<meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0" name="viewport">
<link href="css/style.css" rel="stylesheet" type="text/css" />

<script src="/js/thirdparty/require.js"></script>
	<script>
	window.wxconfig={'paramStr':'<%=paramStr%>',
		'paramStrEnc':'<%=paramStrEnc%>',
		'eccode':'<%=eccode%>'
		};
	require(['/js/common.js'], function (common) {
    	require(['wx/pages/verify/JK000078_JK000078/verify']);
	});
	</script>
</head>

<body>
<div class="top"><img src="images/top.jpg" alt=""/></div>
  <div class="main">    
  <form method="post" action="">
  	<input type="hidden" name="paramStr" id="paramStr" value=""/>
	<input type="hidden" name="paramStrEnc" id="paramStrEnc" value=""/>
    <ul class="login">
      <li><input id="phone" name="phone" class="logininput"  value="" placeholder="手机号" type="text"/></li>
      <li><input id="vc" name="vc" class="logininput password" value="" placeholder="验证码" type="password"/>
          <input id="gpbutton" type="button" class="login_btn gray" value="获取验证码"/>
          <input id="gpbutton2" type="button" class="login_btn gray" value="" style="display:none;"/>
      </li>
      <li class="txt"><input id="disclarecheck" type="checkbox" checked/>已阅读并同意<a href="###" id="mz">&lt;&lt;服务协议内容&gt;&gt;</a></li>
      <li><input id="loginbutton" type="button" class="login_btn" value="验&nbsp;&nbsp;证" style="-webkit-appearance: none;"/></li>
    </ul>
   </form>
  </div> 
  <!--弹出层时背景层DIV遮罩层-->
  <div class="black_overlay hide"></div>
<!--弹出层-->
  <div id="tc" class="tc hide">
  <a class="close" title="Close">×</a>
  <div class="text">
  <h1>服务协议内容</h1>
  <p>1、用户自行承担使用WIFI服务的全部责任。用户承诺：<br/>
（1）用户使用WIFI服务时必须遵守所有适用于WIFI服务的国家和地方性法律、法规和国际法律，不得在网页上或者利用WIFI服务制作、复制、发布、传播上述法律、法规所禁止的内容。<br/>
（2）不利用WIFI服务从事危害计算机信息网络安全的活动。<br/>
（3）禁止使用P2P软件或以其他方式干扰运营商网络的正常运转以及其他用户正常使用WIFI服务。<br/>
（4）遵守承办方及运营商的所有其他规定和程序。</p>
<p>2、用户对自己在使用WIFI服务过程中的行为所承担的法律责任包括但不限于对受到侵害者进行赔偿，在承办方及运营商首先承担了因用户行为导致的行政处罚或侵权损害赔偿责任后，用户应给予承办方及运营商等额的赔偿。</p>
<p>3、承办方及运营商保留判定用户行为是否符合本服务条款的权利，如果承办方及运营商发现用户所传输的信息存在违反法律法规及本免责声明中用户所承诺的情形，依据相关法律法规，运营商有权立即停止传输并将在保存有关记录后向国家有关机关报告，并终止提供WIFI服务。</p>
<p>4、因通讯线路故障、计算机病毒、黑客攻击及其他各种不可抗力原因而导致遭受的一切损失，用户须自行承担。如用户已经开始使用我单位提供的无线网络，则意味着用户已仔细阅读并已接受上述提示内容。</p>
<p>5、本免责声明中的条款应符合中国的法律、法规，与法律、法规相抵触的部分无效，但不影响其他部分的效力。   
</p></div>
  </div>
</body>
</html>
