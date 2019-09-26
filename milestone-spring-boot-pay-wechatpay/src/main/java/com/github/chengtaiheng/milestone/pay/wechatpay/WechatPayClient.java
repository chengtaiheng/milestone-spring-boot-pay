package com.github.chengtaiheng.milestone.pay.wechatpay;

import com.github.chengtaiheng.milestone.pay.common.PayReturn;
import com.github.chengtaiheng.milestone.pay.common.WechatPayAdvancePaymentParams;

/**
 * @author: 程泰恒
 */

public interface WechatPayClient {

    PayReturn advancePayment(WechatPayAdvancePaymentParams params);

    boolean paySuccess(String orderNo);


}
