define(['jquery','util/validutil','util/stringutil'],function($,validutil,stringutil){
	
	function loginButtonDisabled() {
		$("#loginbutton").attr("disabled",true);
		$("#loginbutton").css({"filter": "gray","-moz-opacity":".5","opacity":"0.5"});
	}
	
	function loginButtonAbled() {
		$("#loginbutton").attr("disabled",false);
		$("#loginbutton").css({"filter": "","-moz-opacity":"","opacity":""});
	}
	
	//验证
	function verify() {
		var phone=$("#phone").val();
		var vc = $("#vc").val();
		if (phone == null || phone == "") {
			alert("尊敬的用户：手机号不能为空！");
			$("#phone").focus();
	    	$("#phone").select();
			return;
		}
		if (validutil.isMobilePhone(phone)!=true) {
			alert("尊敬的用户：请输入11位的手机号码！");
			$("#phone").focus();
	    	$("#phone").select();
			return;
		}
		if (vc == null || vc == "") {
			alert("尊敬的用户：验证码不能为空！");
			$("#vc").focus();
	    	$("#vc").select();
			return;
		}
		
		phone = stringutil.trim(phone);
		$("#phone").val(phone);

		vc = stringutil.trim(vc);
		$("#vc").val(vc);

		loginButtonDisabled();
		document.forms[0].action = "/wx/vf?vt=vtc";
		document.forms[0].submit();
	}

	function agreedisclare_verify(){
		var phone=$("#phone").val();
		var vc = $("#vc").val();
		if (phone == null || phone == "") {
			alert("尊敬的用户：手机号不能为空！");
			$("#phone").focus();
	    	$("#phone").select();
			return;
		}
		if (validutil.isMobilePhone(phone)!=true) {
			alert("尊敬的用户：请输入11位的手机号码！");
			$("#phone").focus();
	    	$("#phone").select();
			return;
		}
		if (vc == null || vc == "") {
			alert("尊敬的用户：验证码不能为空！");
			$("#vc").focus();
	    	$("#vc").select();
			return;
		}
		if($("#disclarecheck").prop("checked")!=true){
			alert("尊敬的用户：请先同意《服务协议说明》！");
			return;
		}
		
		phone = stringutil.trim(phone);
		$("#phone").val(phone);

		vc = stringutil.trim(vc);
		$("#vc").val(vc);

		loginButtonDisabled();
		document.forms[0].action = "/wx/vf?vt=vtc";
		document.forms[0].submit();
	}
	
	function disclarecheck(){
		if($("#disclarecheck").prop("checked")==true){
			loginButtonAbled();
		}else{
			loginButtonDisabled();
		}
	}
	
	return {
		verify:verify,
		agreedisclare_verify:agreedisclare_verify,
		disclarecheck:disclarecheck
	};
});
