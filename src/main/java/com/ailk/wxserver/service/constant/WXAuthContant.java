package com.ailk.wxserver.service.constant;



/**
 * 微信认证常亮
 * @author zhoutj
 *
 */
public class WXAuthContant {

	/**
	 * 微信加密签名key
	 */
	public final static String KEY_URL_SIGNATURE = "signature";

	/**
	 * 时间戳
	 */
	public final static String KEY_URL_TIMESTAMP = "timestamp";

	/**
	 * 随机数
	 */
	public final static String KEY_URL_NONCE = "nonce";

	/**
	 * 随机字符串
	 */
	public final static String KEY_URL_ECHOSTR = "echostr";
	
	/**
	 * 加密类型
	 */
	public final static String KEY_URL_ENCRYPT_TYPE = "encrypt_type";
	
	/**
	 * 对消息体的签名
	 */
	public final static String KEY_URL_MSG_SIGNATURE = "msg_signature";
	
	/**
	 * 商户唯一键，微信公众平台配置时的url中有该参数，接入校验时传递参数中存在该参数。
	 */
	public final static String KEY_URL_UNCODE = "uncode";
	
	/**
	 * 接入校验返回的随机字符串
	 */
	public final static String KEY_RECHOSTR = "rechostr";
	
	
	/**
	 * 消息字符串key
	 */
	public final static String KEY_MESSAGE = "message";
	
	/**
	 * 微信验证key:token 
	 */
	public final static String KEY_VERIFY_TOKEN = "token";
	
	/**
	 * 微信验证key:eccode
	 */
	public final static String KEY_VERIFY_ECCODE = "eccode";
	
	/**
	 * 微信验证key:openid
	 */
	public final static String KEY_VERIFY_OPENID = "openid";
	
	
	/**
	 * 原始idkey:registerid
	 */
	public final static String KEY_REGISTER_ID = "registerid";
	
	
	/**
	 * 微信验证key:type
	 */
	public final static String KEY_VERIFY_TYPE = "type";
	
	/**
	 * 微信验证key:phone
	 */
	public final static String KEY_VERIFY_PHONE = "phone";
	
	/**
	 * 微信验证key:timestamp 
	 */
	public final static String KEY_VERIFY_TIMESTAMP = "timestamp";
	
	/**
	 * 信息中的实际内容，去除命令信息后的内容
	 */
	public final static String KEY_CONTENT_INFO = "contentinfo";
	
	/**
	 * 微信连wifi extend参数
	 */
	public final static String KEY_WIFI_EXTEND = "extend";
	
	/**
	 * 微信连wifi cookie extend参数
	 */
	public final static String KEY_WIFI_C_EXTEND = "c_extend";
	
	/**
	 * 微信连wifi 加密后的手机号码参数
	 */
	public final static String KEY_WIFI_TID = "tid";
	
	/**
	 * 微信连wifi cookie 加密后的手机号码参数
	 */
	public final static String KEY_WIFI_C_TID = "c_tid";
	
	/**
	 * 微信连wifi openid
	 */
	public final static String KEY_WIFI_OPENID = "openId";
	
	/**
	 * 微信连wifi cookie中的openid
	 */
	public final static String KEY_WIFI_C_OPENID = "c_openId";
	
	/**
	 * 微信加密：不加密
	 */
	public final static String WX_ENCRYPT_TYPE_RAW = "raw";
	
	/**
	 * 微信加密：aes加密
	 */
	public final static String WX_ENCRYPT_TYPE_AES = "aes";
	
	/**
	 * 发送消息
	 */
	public final static String MSGTYPE_TEXT = "text";
	
	/**
	 * 触发事件（订阅、取消订阅）
	 */
	public final static String MSGTYPE_EVENT = "event";
	
	/**
	 * 订阅
	 */
	public final static String EVENT_SUBSCRIBE = "subscribe";
	
	/**
	 * 取消订阅
	 */
	public final static String EVENT_UNSUBSCRIBE = "unsubscribe";
	
	/**
	 * click事件
	 */
	public final static String EVENT_CLICK = "CLICK";
	
	/**
	 * 已关注微信号，再扫描二维码进入公众号事件
	 */
	public final static String EVENT_SCAN = "SCAN";
	
	/**
	 * 微信连wifi,连网事件
	 */
	public final static String EVENT_WIFICONNECTED="WifiConnected";
	
	/**
	 *  验证状态：未验证
	 */
	public final static int VERIFYSTATUS_NO = 0;
	
	/**
	 *  验证状态：验证中
	 */
	public final static int VERIFYSTATUS_VERIFYING = 1;
	
	/**
	 *  验证状态：验证成功
	 */
	public final static int VERIFYSTATUS_VERIFYOK = 2;
	
	/**
	 *  验证状态：验证失败
	 */
	public final static int VERIFYSTATUS_VERIFYFAIL = 3;
	
	
	/**
	 *  开户状态：未开户
	 */
	public final static int OPENUSERSTATUS_NO = 0;
	
	/**
	 *  开户状态：开户中
	 */
	public final static int OPENUSERSTATUS_OPENING = 1;
	
	/**
	 *  开户状态：开户成功
	 */
	public final static int OPENUSERSTATUS_OK = 2;
	
	/**
	 *  开户状态：开户失败
	 */
	public final static int OPENUSERSTATUS_FAIL = 3;
	
	
	/**
	 * 微信回复消息funcname，对应messageshowconfig.xml中的配置
	 */
	public final static String MSG_FUNCNAME_WX = "weixin_show";
	
	/**
	 * 微信验证信息funcname，对应messageshowconfig.xml中的配置
	 */
	public final static String MSG_FUNCNAME_WX_VERIFY = "weixin_verify_show";
	
	/**
	 * 微信验证获取验证码信息funcname，对应messageshowconfig.xml中的配置
	 */
	public final static String MSG_FUNCNAME_WX_VERIFY_GETTOKEN = "weixin_vf_gettoken_show";
	
	/**
	 * 微信下发短信funcname，对应messageshowconfig.xml中的配置
	 */
	public final static String MSG_FUNCNAME_WX_SMS = "weixin_sms_show";
	
	/**
	 * 微信连wifi信息funcname，对应messageshowconfig.xml中的配置
	 */
	public final static String MSG_FUNCNAME_WX_WIFI = "weixin_wifi_show";
	
	/**
	 * 微信上网认证信息funcname，对应messageshowconfig.xml中的配置
	 */
	public final static String MSG_FUNCNAME_WX_NETAUTH = "weixin_netauth_show";
	
	/**
	 * 下发上网链接：微信下发
	 */
	public final static String GETLINKTYPE_WX = "1";
	
	/**
	 * 下发上网链接：短信下发
	 */
	public final static String GETLINKTYPE_SMS = "2";
	
	
	/**
	 * 微信事件key值前缀
	 */
	public static final String WX_EVENTKEY_PREFIX = "qrscene_";
	
	
	/**
	 * 微信WIFI接口请求参数名
	 */
	public final static String WX_WIFI_REQNAME = "wifireq";
	
	/**
	 * 微信验证接口请求参数名
	 */
	public final static String WX_VERIFY_REQNAME = "vt";
	
    /**
     * 微信网页授权 code作为换取access_token的票据，每次用户授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期
     */
    public final static String WX_SURFNET_CODE = "code";

    /**
     * 微信网页授权 重定向后会带上state参数
     */
    public final static String WX_SURFNET_STATE = "state";

    /**
     * 微信营销请求类型
     */
    public final static String WX_MANAGER_TYPE = "wxManagerType";

    /**
     * 微信营销获取手机号请求
     */
    public final static String WX_MANAGER_FANSPHONE = "fansPhone";
}
