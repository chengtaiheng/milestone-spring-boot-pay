package com.github.chengtaiheng.milestone.pay.alipay;

import com.alipay.api.AlipayApiException;
import com.github.chengtaiheng.milestone.pay.common.AlipayAdvancePaymentParams;
import com.github.chengtaiheng.milestone.pay.common.PayReturn;

/**
 * @author: 程泰恒
 */

public interface AlipayClient {

    PayReturn advancePayment(AlipayAdvancePaymentParams params);

    boolean paySuccess(String orderNo) throws AlipayApiException;
}
