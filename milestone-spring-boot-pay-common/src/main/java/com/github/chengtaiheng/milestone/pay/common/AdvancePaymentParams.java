package com.github.chengtaiheng.milestone.pay.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author: 程泰恒
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public abstract class AdvancePaymentParams {

    /**
     * 支付业务订单编号
     */
    private String orderNumber;

    /**
     * 支付主题
     */
    private String subject;

    /**
     * 回传参数
     */
    private String passbackParams;

    /**
     * 支付金额（单位：分）
     */
    long paymentCent;

    /**
     * 支付过期时间 (默认为2小时)
     */
    String timeExpire;


}
