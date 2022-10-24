package com.ailk.wxserver.service.main.interfaces;

import com.ailk.wxserver.ci.server.getwxuser.GetPhoneRequest;
import com.ailk.wxserver.ci.server.getwxuser.GetPhoneResponse;

/**
 * 查询微信用户信息
 * @author zhoutj
 *
 */
public interface GetWXUserOperateBO {

	/**
	 * 获取微信用户绑定的手机号
	 * @param request
	 * @return
	 */
	public GetPhoneResponse getPhone(GetPhoneRequest request);
}
