package com.ailk.wxserver.service.constant;

/**
 * 微信服务resultcode常量
 * 
 * @author zhoutj
 *
 */
public class WXResultConstant {

    /**
     * 成功
     */
    public static final int RESULT_SUCCESS = 0;

    /**
     * 不存在商户集客与微信号对应关系
     */
    public static final int RESULT_ENTERPRISE_REGISTER_NOEXIST = -10101;

    /**
     * 非法的消息体
     */
    public static final int RESULT_INVALID_MSG = -10102;

    /**
     * 非法手机号
     */
    public static final int RESULT_INVALID_MOBILE = -10103;

    /**
     * 操作数据库失败
     */
    public static final int RESULT_OPERATEDB_FAIL = -10104;

    /**
     * 操作数据库异常
     */
    public static final int RESULT_OPERATEDB_EXCEPTION = -10105;

    /**
     * AAA请求失败
     */
    public static final int RESULT_AAAREQ_FAIL = -10106;

    /**
     * 没有验证
     */
    public static final int RESULT_VERIFY_NO = -10107;
    /**
     * 验证失败
     */
    public static final int RESULT_VERIFY_FAIL = -10108;

    /**
     * 已验证通过
     */
    public static final int RESULT_HAS_VERIFIED = -10109;

    /**
     * 验证超时
     */
    public static final int RESULT_VERIFY_EXPIRED = -10110;

    /**
     * 下发短信失败
     */
    public static final int RESULT_SENDSMS_FAIL = -10111;

    /**
     * 微信用户不存在
     */
    public static final int RESULT_SPECIALUSER_NOTEXIST = -10112;

    /**
     * 宽带用户不存在
     */
    public static final int RESULT_BROADUSER_NOTEXIST = -10113;

    /**
     * 内存库操作失败
     */
    public static final int RESULT_MEMCACHED_OPERATE_FAIL = -10114;

    /**
     * 内存库数据不存在
     */
    public static final int RESULT_MEMCACHED_NOEXIST = -10115;

    /**
     * mac请求失败
     */
    public static final int RESULT_MACREQ_FAIL = -10116;

    /**
     * 构造认证url失败
     */
    public static final int RESULT_FORMAUTHCODEURL_ERROR = -10117;

    /**
     * 需要验证手机号
     */
    public static final int RESULT_VERIFY_NEED = -10118;

    /**
     * 请求数据有问题
     */
    public static final int RESULT_REQPARAM_ERROR = -10119;

    /**
     * 上网链接已过期
     */
    public static final int RESULT_NETLINK_EXPIRED = -10120;

    /**
     * 不存在该请求类型
     */
    public static final int RESULT_REQTYPE_NOTEXIST = -10121;

    /**
     * 非法来源IP
     */
    public static final int RESULT_NOT_PERMIT_IP = -10122;

    /**
     * 微信认证业务暂停
     */
    public static final int RESULT_ENTERPRISE_REGISTER_STATUS_PAUSE = -10123;

    /**
     * 系统异常
     */
    public static final int RESULT_SYSTEM_EXCEPTION = -10199;

    /**
     * 根据处理结果，回应消息。
     * 
     * @param result_code
     * @return
     */
    public static String getMsgByResultCode(int result_code) {

        switch (result_code) {
        case RESULT_ENTERPRISE_REGISTER_NOEXIST:
            return "没有配置商户与微信公众号对应关系";
        case RESULT_INVALID_MSG:
            return "非法的消息体";
        case RESULT_OPERATEDB_FAIL:
            return "操作数据库失败";
        case RESULT_OPERATEDB_EXCEPTION:
            return "操作数据库异常";
        case RESULT_AAAREQ_FAIL:
            return "aaa请求失败";
        case RESULT_SYSTEM_EXCEPTION:
            return "系统异常";
        case RESULT_ENTERPRISE_REGISTER_STATUS_PAUSE:
            return "微信认证业务暂停";
        default:
            return "";
        }
    }
}
