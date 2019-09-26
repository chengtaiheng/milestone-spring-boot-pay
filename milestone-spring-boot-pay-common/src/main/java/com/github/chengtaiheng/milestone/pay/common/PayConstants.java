package com.github.chengtaiheng.milestone.pay.common;

/**
 * @author 程泰恒
 */
public interface PayConstants {

    /**
     * 状态类型
     */
    public static final String STATUS_0 = "0";//有效 可用正常诸葛那天
    public static final String STATUS_1 = "1";


    public static final String EPAYMENT_STATUS_INIT = "INIT";//初始
    public static final String EPAYMENT_STATUS_INPAYMENT = "INPAYMENT";//付款中
    public static final String EPAYMENT_STATUS_PAID = "PAID";//已付

    public static final String PAY_TYPE_WXPAY = "WXPAY";//微信
    public static final String PAY_TYPE_ALIPAY = "ALIPAY";//支付宝
    public static final String PAY_TYPE_BALANCE = "BALANCE";//余额
    public static final String PAY_TYPE_CASH = "CASH";//现金

    public static final String ORDER_TYPE_INCREASE = "increase";//充值
    public static final String ORDER_TYPE_BALANCE = "balance";//余额支付
    public static final String ORDER_TYPE_MEDICAL = "medical";//门诊订单
    public static final String ORDER_TYPE_TAKEDRUG = "takedrug";//取药订单
    public static final String ORDER_TYPE_SENDDRUG = "senddrug";//送药订单
    public static final String ORDER_TYPE_ACCEPTDRUG = "accept";//接送药订单

}
