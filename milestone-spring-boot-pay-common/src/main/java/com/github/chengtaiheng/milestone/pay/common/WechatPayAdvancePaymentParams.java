package com.github.chengtaiheng.milestone.pay.common;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author: 程泰恒
 */

@Getter
@Setter
@ToString
public class WechatPayAdvancePaymentParams extends AdvancePaymentParams {

    /**
     * ip地址
     */
    String ip = "127.0.0.1";

    @Builder
    public WechatPayAdvancePaymentParams(String orderNumber, String subject, String passbackParams, long paymentCent, String timeExpire,String ip) {
        super(orderNumber, subject, passbackParams, paymentCent, timeExpire);
    }
}
