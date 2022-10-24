define( function () {
	/**
	 * 是否是手机号码
	 * @param str
	 * @returns {Boolean}
	 */
	function isMobilePhone(str){
		if(/^1[0-9]{10}$/.test(str) == true){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 是否是天翼手机号码
	 * @param str
	 * @returns {Boolean}
	 */
	function isCDMAMobilePhone(str){
		if (/^((133)|(153)|(18[019]))[0-9]{8}$/.test(str) == true) {
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 校验行业用户名
	 * @param username
	 */
	function validEccodeUsername(username){
		if (/^[a-zA-Z0-9]{5,25}$/.test(username) == true) {
			return true;
		}else{
			return false;
		}
		
	}

	/**
	 * 校验行业用户密码
	 * @param password
	 * @returns {Boolean}
	 */
	function validEccodePassword(password){
		if (/^[a-zA-Z0-9]{4,12}$/.test(password) == true) {
			return true;
		}else{
			return false;
		}
	}
	
	return {
		isMobilePhone : isMobilePhone,
		isCDMAMobilePhone:isCDMAMobilePhone,
		validEccodeUsername:validEccodeUsername,
		validEccodePassword:validEccodePassword
		};
});
	