package com.github.chengtaiheng.milestone.pay.common;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: 程泰恒
 */

@Getter
@Setter
public class AlipayAdvancePaymentParams extends AdvancePaymentParams {


    @Builder
    public AlipayAdvancePaymentParams(String orderNumber, String subject, String passbackParams, long paymentCent, String timeExpire) {
        super(orderNumber, subject, passbackParams, paymentCent, timeExpire);
    }
}
