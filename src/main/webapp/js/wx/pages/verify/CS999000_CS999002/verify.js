define(["jquery","wx/business/getvfcode","wx/business/verify"],function($,getvfcode,verify){
	
	$(document).ready(function(){
		//组件初始化
		componentInit();
		
		//页面元素初始化
		pageInit();
		
		//页面事件绑定
		pageBind();
		
	});
	//组件初始化
	function componentInit(){	
	}
	
	
	//页面元素初始化
	function pageInit(){
		$("#paramStr").val(wxconfig.paramStr);
		$("#paramStrEnc").val(wxconfig.paramStrEnc);	
		
		//弹窗初始不显示
		$('.black_overlay').hide();
		$(this).parent().hide();
	}
	
	//页面事件绑定
	function pageBind(){
		/*获取验证码事件*/
		$("#gpbutton").click(function(){
			getvfcode.getverifycode();
		});
		/*验证事件*/
		$("#loginbutton").click(function(){
			verify.agreedisclare_verify();
		});
		/*免责声明事件*/
		$("#disclarecheck").click(function(){
			verify.disclarecheck();
		});
		
		/*弹窗关闭×按钮*/
		$('.close').click(function() {
			$('.black_overlay').hide();
			$(this).parent().hide();
		});
		  
		/*弹窗*/  
		$('#mz').click(function() {
			$('.black_overlay').show(); 
			$('#tc').show(); 
		}); 
	}

});
