define(['jquery','util/validutil'],function($,validutil){
	
	var timeoutMills = 15000;
	var scanMills = 0;
	var startMills;
	var stepMills = 1000;
	
	var GetRandomPassword="获取验证码";
	var GetAgain="秒";
	var loadnum=0;
	
	var num=15;
	//获取密码倒计时
	function count(){
		if(num==0){
			$("#gpbutton").css("display","");
			$("#gpbutton2").css("display","none");
			 num=15;
		}else if(num==15){
			$("#gpbutton").css("display","none");
			$("#gpbutton2").css({"display":"","cursor":"text"});
			$("#gpbutton2").attr("disabled",true);
			$("#gpbutton2").attr("value",num+GetAgain);
			num=num-1;
			setTimeout(count, 1000);
		}else{
			$("#gpbutton2").attr("value",num+GetAgain);
			num=num-1;
			setTimeout(count, 1000);
		}
	}
	
	function getverifycode(){
	    var phone=$("#phone").val();
	    if(phone == null || phone == ""){
	    	alert("尊敬的用户，手机号码不能为空");
	    	$("#phone").focus();
	    	$("#phone").select();
			return ;    
	    }else if(validutil.isMobilePhone(phone)!=true){
	    	alert("尊敬的用户，请输入11位手机号码");
	    	$("#phone").focus();
	    	$("#phone").select();
			return ;
	    }else{ 	
	    	$.ajax({
	    	   type: "POST",
	    	   url: "/wx/vf",
	    	   data: "vt=gvf&paramStr="+wxconfig.paramStrEnc+"&phone="+phone+"&t="+Math.random()*10000,
	    	   success: function(msg){
	   				var json = eval("(" + msg + ")");
	   				var replyMsg=json.replyMsg;
	   				alert(replyMsg);
	    		   
	    	   }
	    	});
	    	count();
	    }
	}	
	
	return {getverifycode:getverifycode};
});
