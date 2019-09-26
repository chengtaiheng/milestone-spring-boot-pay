package com.github.chengtaiheng.milestone.pay.wechatpay.impl;

import com.github.chengtaiheng.milestone.pay.common.HttpUtils;
import com.github.chengtaiheng.milestone.pay.common.PayReturn;
import com.github.chengtaiheng.milestone.pay.common.WeChatPayReturn;
import com.github.chengtaiheng.milestone.pay.common.WechatPayAdvancePaymentParams;
import com.github.chengtaiheng.milestone.pay.common.util.SignatureHelper;
import com.github.chengtaiheng.milestone.pay.common.util.WeChatPayUtil;
import com.github.chengtaiheng.milestone.pay.wechatpay.autoconfig.WechatPayAutoConfig;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: 程泰恒
 */
public class WechatPayClientImpl implements com.github.chengtaiheng.milestone.pay.wechatpay.WechatPayClient {

    private static final String URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";


    private final static Logger log = LoggerFactory.getLogger(WechatPayClientImpl.class);

    @Autowired
    private WechatPayAutoConfig.Props props;

    @Override
    public PayReturn advancePayment(WechatPayAdvancePaymentParams params) {

        String orderNo = params.getOrderNumber();
        long paymentCent = params.getPaymentCent();
        String passbackParams = params.getPassbackParams();
        String body = params.getSubject();
        String time_expire = params.getTimeExpire();
        String ip = params.getIp();

        WechatPayAutoConfig.Props.WechatPay wechatPay = props.getWechatPay();

        String nonce_str = RandomStringUtils.randomAlphanumeric(32);
        String sign_type = "MD5";
        long total_fee = paymentCent;


        //TODO:
//        if (ProfileUtils.anyActive("dev", "k8s")) {
//            total_fee = 1;
//        }

        log.info("total_fee:{}", total_fee);

//        String spbill_create_ip = getRemoteAddr(request);   //"123.12.12.123";
        String trade_type = "APP";
        String attach = passbackParams;//附加数据，在查询API和支付通知中原样返回，该字段主要用于商户携带订单的自定义数据
        WeChatPayReturn payReturn = new WeChatPayReturn();

        try {
            Map<String, String> map = new HashMap<>();
            map.put("appid", wechatPay.getAppId());
            map.put("mch_id", wechatPay.getMchId());
            map.put("nonce_str", nonce_str);
            map.put("sign_type", sign_type);
            map.put("body", body);
            map.put("out_trade_no", orderNo);
            map.put("total_fee", total_fee + "");
            map.put("spbill_create_ip", ip);
            if (time_expire != null) {
                map.put("time_expire", time_expire);//prepay_id只有两小时的有效期
            }
            map.put("notify_url", wechatPay.getNotifyUrl());
            map.put("trade_type", trade_type);
            map.put("attach", attach);
            String sign = SignatureHelper.getSign2(map, "key", wechatPay.getSecretKey(), false, SignatureHelper.DigestType.MD5, SignatureHelper.CharacterCase.UPPER_CASE);
            map.put("sign", sign);
            String wxReqStr = WeChatPayUtil.mapToXml(map);
            String resultInfo = HttpUtils.syncXmlPost(URL, wxReqStr);
            Map<String, String> resultObject = WeChatPayUtil.xmlToMap(resultInfo);
            log.info("微信请求返回结果是:{}", resultObject);
            if (StringUtils.equalsIgnoreCase("SUCCESS", resultObject.get("return_code"))) {
                if (StringUtils.equalsIgnoreCase("SUCCESS", resultObject.get("result_code"))) {
                    payReturn.setAppid(wechatPay.getAppId());
                    payReturn.setPartnerid(wechatPay.getMchId());
                    payReturn.setPrepayid(resultObject.get("prepay_id"));
                    payReturn.setPackageValue("Sign=WXPay");
                    payReturn.setNoncestr(RandomStringUtils.randomAlphanumeric(32));
                    payReturn.setTimestamp(System.currentTimeMillis() / 1000);
                    Map<String, String> hashMap = new HashMap<>();
                    hashMap.put("appid", payReturn.getAppid());
                    hashMap.put("partnerid", payReturn.getPartnerid());
                    hashMap.put("prepayid", payReturn.getPrepayid());
                    hashMap.put("noncestr", payReturn.getNoncestr());
                    hashMap.put("timestamp", Long.toString(payReturn.getTimestamp()));
                    hashMap.put("package", payReturn.getPackageValue());
                    String paySign = SignatureHelper.getSign2(hashMap, "key", wechatPay.getSecretKey(), false, SignatureHelper.DigestType.MD5, SignatureHelper.CharacterCase.UPPER_CASE);
                    payReturn.setSign(paySign);
                    payReturn.setOrderNumber(orderNo);
                } else {
                    Assert.isTrue(false, resultObject.get("err_code") + "-" + resultObject.get("err_code_des"));
                }
            } else {
                Assert.isTrue(false, resultObject.get("return_msg"));
            }

//            EpaymentTradeRequest epaymentTradeRequest = new EpaymentTradeRequest();
//            epaymentTradeRequest.setOrderNo(epaymentTrade.getOrderNo());
//            epaymentTradeRequest.setStatus(ConstantAll.STATUS_0);
//            List<EpaymentTrade> epaymentTradeList = new ArrayList<>();//具体业务 epaymentTradeService.listEpaymentTrade(epaymentTradeRequest);
//            EpaymentTrade eTrade = null;
//            Assert.isTrue(epaymentTradeList.size() <= 1, "电子支付凭证过多,异常数据!");
//            if (epaymentTradeList.size() == 0) {
//                eTrade = epaymentTrade;
//                eTrade.setRemarks("[微信支付]新产生的电子支付凭证单据");
//                eTrade.setPayType(ConstantAll.PAY_TYPE_WXPAY);
//                eTrade.setPayStatus(ConstantAll.EPAYMENT_STATUS_INPAYMENT);
//                //具体业务this.epaymentTradeService.insertSelective(eTrade);
//            } else if (epaymentTradeList.size() == 1) {
//                eTrade = epaymentTradeList.get(0);
//                Assert.isTrue(!StringUtils.equalsIgnoreCase(eTrade.getPayStatus(), ConstantAll.EPAYMENT_STATUS_PAID), "目前订单已经支付成功过!");
//                eTrade.setRemarks("调整为[微信支付],之前为:" + eTrade.getPayType());
//                eTrade.setGmtModified(new Date());
//                eTrade.setPayType(ConstantAll.PAY_TYPE_WXPAY);
//                eTrade.setPayStatus(ConstantAll.EPAYMENT_STATUS_INPAYMENT);
//                //具体业务this.epaymentTradeService.updateByPrimaryKeySelective(eTrade);
//            }
        } catch (Exception e) {
            Assert.isTrue(false, "微信支付异常:" + e.getMessage());
        }
        return payReturn;
    }

    @Override
    public boolean paySuccess(String orderNo) {

        WechatPayAutoConfig.Props.WechatPay wechatPay = props.getWechatPay();

        if (StringUtils.isBlank(orderNo)) {
            return false;
        }

        String message = null;
        boolean isPayOk = false;

        String url = "https://api.mch.weixin.qq.com/pay/orderquery";
        String nonce_str = RandomStringUtils.randomAlphanumeric(32);
        try {
            Map<String, String> params = new HashMap<>();
            params.put("appid", wechatPay.getAppId());
            params.put("mch_id", wechatPay.getMchId());
            params.put("nonce_str", nonce_str);
            params.put("out_trade_no", orderNo);//微信的订单号，优先使用 transaction_id  二选一
            String sign = SignatureHelper.getSign2(params, "key", wechatPay.getSecretKey(), false, SignatureHelper.DigestType.MD5, SignatureHelper.CharacterCase.UPPER_CASE);
            params.put("sign", sign);
            String wxReqStr = WeChatPayUtil.mapToXml(params);
            log.info("wxReqStr:{}", wxReqStr);
            String resultInfo = HttpUtils.syncXmlPost(url, wxReqStr);
            log.info("resultInfo:{}", resultInfo);
            Map<String, String> resultObject = WeChatPayUtil.xmlToMap(resultInfo);
            log.info("微信请求返回结果是:{}", resultObject);
            if (StringUtils.equalsIgnoreCase("SUCCESS", resultObject.get("return_code"))) {
                if (StringUtils.equalsIgnoreCase("SUCCESS", resultObject.get("result_code"))) {
                    String trade_state = resultObject.get("trade_state");
                    log.info("交易状态:{}", trade_state);
                    if (StringUtils.equalsIgnoreCase("SUCCESS", trade_state)) {//NOTPAY 为支付情况下,没有transaction_id返回
                        //具体业务
                        isPayOk = true;
                    } else {
                        log.info("NOTPAY 为支付情况下,没有transaction_id返回");
                    }
                } else {
                    message = resultObject.get("err_code") + "-" + resultObject.get("err_code_des");
                }
            } else {
                message = resultObject.get("return_msg");
            }
        } catch (Exception e) {
            log.info("微信请求解析异常:" + e.getMessage(), e);
        }
        Assert.isTrue(message == null, message);
        return isPayOk;
    }
}
