package com.github.chengtaiheng.milestone.pay.common;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * @author 程泰恒
 */
@Getter
@Setter
@ToString
public class WeChatPayReturn extends PayReturn {

    private static final long serialVersionUID = -3135343740926817483L;

    /**
     * 微信 应用ID
     */
    private String appid = "";

    /**
     * 微信 商户号
     */
    private String partnerid = "";

    /**
     * 微信 预支付交易会话ID
     */
    private String prepayid = "";

    /**
     * 微信 扩展字段
     */
    private String packageValue = "";

    /**
     * 微信 随机字符串
     */
    private String noncestr = "";

    /**
     * 微信 时间戳
     */
    private long timestamp = 0L;

    /**
     * 微信 签名
     */
    private String sign = "";

}
