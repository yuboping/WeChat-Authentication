package com.ailk.wxserver.service.constant;

public class WXMsgCodeConstant {

    /**
     * 提示用户回复获取用户上网链接信息
     */
    public final static int WX_MSG_CODE_GETLINK = 1;

    /**
     * 提示用户回复输入手机号信息
     */
    public final static int WX_MSG_CODE_GETPHONE = 2;

    /**
     * 验证过期提示重新验证
     */
    public final static int WX_MSG_CODE_EXPIRED = 3;

    /**
     * 取消关注成功
     */
    public final static int WX_MSG_CODE_UNSUBCRIBE_SUCCESS = 4;

    /**
     * 取消关注失败
     */
    public final static int WX_MSG_CODE_UNSUBCRIBE_FAILURE = 5;

    /**
     * help信息
     */
    public final static int WX_MSG_CODE_HELP = 6;

    /**
     * 重复绑定手机号码信息
     */
    public final static int WX_MSG_CODE_REBINDPHONE = 7;

    /**
     * 下发短信提醒
     */
    public final static int WX_MSG_CODE_SENDSMS_SUCCESS = 8;

    /**
     * 下发短信失败
     */
    public final static int WX_MSG_CODE_SENDSMS_FAIL = 9;

    /**
     * 上网链接
     */
    public final static int WX_MSG_CODE_LINK = 10;

    /**
     * 非法手机号
     */
    public final static int WX_MSG_INVALID_MOBILE = 11;

    /**
     * portal认证短信内容
     */
    public final static int WX_MSG_CODE_AUTH_SMSCONTENT = 13;

    /**
     * portal认证短信下发成功提醒
     */
    public final static int WX_MSG_CODE_SENDAUTHSMS_SUCCESS = 14;

    /**
     * 关注公众号欢迎信息
     */
    public final static int WX_MSG_CODE_WELCOME = 15;

    /**
     * 空消息
     */
    public final static int WX_MSG_CODE_NULL = 16;

    /**
     * 系统出问题
     */
    public final static int WX_MSG_CODE_SYSTEM_ERROR = 90;

    /**
     * 系统忙消息
     */
    public final static int WX_MSG_CODE_SYSTEM_BUSY = 98;

    /**
     * 系统异常
     */
    public final static int WX_MSG_CODE_SYSTEMEXCEPTION = 99;

    /**
     * 微信短信内容：验证链接短信
     */
    public final static int WX_SMS_CODE_VERIFYLINK = 1;

    /**
     * 微信短信内容：验证码短信
     */
    public final static int WX_SMS_CODE_VERIFYCODE = 2;

    /**
     * 微信连wifi：请求参数有问题
     */
    public final static int WX_WIFI_CODE_PARAMERROR = 1;

    /**
     * 微信连wifi:不存在微信公众号配置
     */
    public final static int WX_WIFI_CODE_WECHAT_NOTCONFIGURE = 2;

    /**
     * 微信连wifi:没有关注
     */
    public final static int WX_WIFI_CODE_NOTATTENTION = 3;

    /**
     * 微信连wifi:微信认证业务暂停
     */
    public final static int WX_WIFI_CODE_STATUS_PAUSE = 4;

    /**
     * 微信连wifi:系统异常
     */
    public final static int WX_WIFI_CODE_EXCEPTION = 99;

    /**
     * 微信获取验证码：成功
     */
    public final static int WX_VF_GETTOKEN_CODE_SUCCESS = 0;

    /**
     * 微信获取验证码：请求参数有问题
     */
    public final static int WX_VF_GETTOKEN_CODE_PARAMERROR = 1;

    /**
     * 微信获取验证码：不存在微信公众号配置
     */
    public final static int WX_VF_GETTOKEN_CODE_WECHAT_NOTCONFIGURE = 2;

    /**
     * 微信获取验证码：没有关注
     */
    public final static int WX_VF_GETTOKEN_CODE_NOTATTENTION = 3;

    /**
     * 微信获取验证码：下发短信失败
     */
    public final static int WX_VF_GETTOKEN_CODE_SMSFAIL = 4;

    /**
     * 微信获取验证码：已验证过
     */
    public final static int WX_VF_GETTOKEN_CODE_VERFIED = 5;

    /**
     * 微信获取验证码：微信认证业务暂停
     */
    public final static int WX_VF_GETTOKEN_CODE_STATUS_PAUSE = 6;

    /**
     * 微信获取验证码：系统繁忙
     */
    public final static int WX_VF_GETTOKEN_CODE_SYSTEMBUSY = 98;

    /**
     * 微信获取验证码：系统异常
     */
    public final static int WX_VF_GETTOKEN_CODE_SYSTEMEXCEPTION = 99;

    /**
     * 微信验证成功
     */
    public final static int WX_VERIFY_CODE_SUCCESS = 0;

    /**
     * 微信验证：请求参数有问题
     */
    public final static int WX_VERIFY_CODE_REQPARAM_ERROR = 1;

    /**
     * 微信验证：验证码不存在
     */
    public final static int WX_VERIFY_CODE_VERIFYCODE_NOEXIST = 2;

    /**
     * 微信验证：验证码过期
     */
    public final static int WX_VERIFY_CODE_VERIFYCODE_EXPIRE = 3;
    /**
     * 微信验证：系统忙信息
     */
    public final static int WX_VERIFY_CODE_SYSTEM_BUSY = 98;
    /**
     * 微信验证：系统忙信息
     */
    public final static int WX_VERIFY_CODE_SYSTEM_EXCEPTION = 99;

    /**
     * 微信上网认证：成功
     */
    public final static int WX_NETAUTH_CODE_SUCCESS = 0;

    /**
     * 微信上网认证：请求参数有问题
     */
    public final static int WX_NETAUTH_CODE_PARAMERROR = 1;

    /**
     * 微信上网认证：不存在微信公众号配置
     */
    public final static int WX_NETAUTH_CODE_WECHAT_NOTCONFIGURE = 2;

    /**
     * 微信上网认证：没有关注
     */
    public final static int WX_NETAUTH_CODE_NOTATTENTION = 3;

    /**
     * 微信上网认证：没有验证手机号
     */
    public final static int WX_NETAUTH_CODE_NOVERIFY = 4;

    /**
     * 微信上网认证：手机号验证过期
     */
    public final static int WX_NETAUTH_CODE_VERIFYEXPIRED = 5;
    /**
     * 微信上网认证：已过期
     */
    public final static int WX_NETAUTH_CODE_LINKEXPIRED = 6;

    /**
     * 微信上网认证：微信认证业务暂停
     */
    public final static int WX_NETAUTH_CODE_STATUS_PAUSE = 7;

    /**
     * 微信获取验证码：系统繁忙
     */
    public final static int WX_NETAUTH_CODE_SYSTEMBUSY = 98;

    /**
     * 微信获取验证码：系统异常
     */
    public final static int WX_NETAUTH_CODE_SYSTEMEXCEPTION = 99;

}
