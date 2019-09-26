package com.github.chengtaiheng.milestone.pay.alipay.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.github.chengtaiheng.milestone.alipay.exception.AlipayException;
import com.github.chengtaiheng.milestone.pay.alipay.AlipayClient;
import com.github.chengtaiheng.milestone.pay.common.AlipayAdvancePaymentParams;
import com.github.chengtaiheng.milestone.pay.common.AlipayConstants;
import com.github.chengtaiheng.milestone.pay.common.AlipayReturn;
import com.github.chengtaiheng.milestone.pay.common.PayReturn;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.text.DecimalFormat;

import static com.github.chengtaiheng.milestone.pay.alipay.autoconfig.AlipayAutoConfig.Props;

/**
 * @author: 程泰恒
 */

public class AlipayClientImpl implements AlipayClient {

    private static final Logger log = LoggerFactory.getLogger(AlipayClientImpl.class);

    @Autowired
    private Props props;

    @Override
    public PayReturn advancePayment(AlipayAdvancePaymentParams params) {

        Props.Alipay alipay = props.getAlipay();

        String orderNo = params.getOrderNumber();
        String passbackParams = params.getPassbackParams();
        long paymentCent = params.getPaymentCent();
        String subject = params.getSubject();
        String timeExpire = params.getTimeExpire();

        // TODO: 参数
//        Assert.isTrue(epaymentTrade.getTypeId() != null, "所属端口 typeId 必须有值");
//        Assert.isTrue(epaymentTrade.getUserId() != null, "所属用户 userId 必须有值");
//        Assert.isTrue(StringUtils.isNotBlank(epaymentTrade.getOrderType()), "业务交易类型 orderType 必须有值");
//        Assert.isTrue(StringUtils.isNotBlank(epaymentTrade.getOrderNo()), "业务交易单号 orderNo 必须有值");
//        Assert.isTrue(epaymentTrade.getPayment() != null && epaymentTrade.getPayment() > 0 && epaymentTrade.getPayment() < 10000000001L, "支付金额 payment的取值范围[1,10000000000]");

        com.alipay.api.AlipayClient alipayClient = new DefaultAlipayClient(AlipayConstants.URL, alipay.getAppId(), alipay.getPrivateKey(), AlipayConstants.PACKAGE_FORMAT, AlipayConstants.CHARSET, alipay.getPublicKey(), AlipayConstants.SIGN_TYPE);

        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setPassbackParams(params.getPassbackParams());//公用回传参数，如果请求时传递了该参数，则返回给商户时会回传该参数。支付宝会在异步通知时将该参数原样返回。本参数必须进行UrlEncode之后才可以发送给支付宝
        model.setSubject(subject);
        model.setOutTradeNo(orderNo);

        if (timeExpire != null) {
            model.setTimeExpire(timeExpire);
        } else {
            model.setTimeoutExpress("2h");//最长2小时
        }

        Double payment = (double) paymentCent;
        DecimalFormat df = new DecimalFormat("######0.00");
        payment = payment / 100.00;
        String totalAmount = df.format(payment.doubleValue());//取值范围[0.01,100000000]

        // 测试阶段实际只支付一分钱 TODO:
//        if (ProfileUtils.anyActive("dev", "k8s")) {
////            totalAmount = "0.01";
////        }

        log.info("目前阿里支付真实交易金额是:'{}'元 传递的临时交易值:{}分", totalAmount, paymentCent);
        model.setTotalAmount(totalAmount);
        model.setProductCode("QUICK_MSECURITY_PAY");//销售产品码，商家和支付宝签约的产品码，为固定值QUICK_MSECURITY_PAY
        request.setBizModel(model);
        request.setNotifyUrl(alipay.getNotifyUrl());
        AlipayReturn payReturn = new AlipayReturn();

        try {
            //这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            log.info("查看目前支付宝返回体情况:{}", response.getBody());
            Assert.isTrue(response.isSuccess(), response.getSubMsg() + " [支付宝响应异常] " + response.getMsg());
            payReturn.setAliParams(response.getBody());
            payReturn.setOrderNumber(orderNo);
        } catch (AlipayApiException e) {
            log.info("支付宝支付问题:" + e.getMessage(), e);
            Assert.isTrue(false, "支付宝支付问题:" + e.getMessage());
        }

//        EpaymentTradeRequest epaymentTradeRequest = new EpaymentTradeRequest();
//        epaymentTradeRequest.setOrderNo(epaymentTrade.getOrderNo());
//        epaymentTradeRequest.setStatus(ConstantAll.STATUS_0);
//        List<EpaymentTrade> epaymentTradeList = new ArrayList<>();//具体业务 epaymentTradeService.listEpaymentTrade(epaymentTradeRequest);
//        EpaymentTrade eTrade = null;
//        Assert.isTrue(epaymentTradeList.size() <= 1, "电子支付凭证过多,异常数据!");
//        if (epaymentTradeList.size() == 0) {
//            eTrade = epaymentTrade;
//            eTrade.setRemarks("[支付宝支付]新产生的电子支付凭证单据");
//            eTrade.setPayType(ConstantAll.PAY_TYPE_ALIPAY);
//            eTrade.setPayStatus(ConstantAll.EPAYMENT_STATUS_INPAYMENT);
//            //具体业务this.epaymentTradeService.insertSelective(eTrade);
//        } else if (epaymentTradeList.size() == 1) {
//            eTrade = epaymentTradeList.get(0);
//            Assert.isTrue(!StringUtils.equalsIgnoreCase(eTrade.getPayStatus(), ConstantAll.EPAYMENT_STATUS_PAID), "目前订单已经支付成功");
//            eTrade.setRemarks("调整[支付宝支付],之前为:" + eTrade.getPayType());
//            eTrade.setGmtModified(new Date());
//            eTrade.setPayType(ConstantAll.PAY_TYPE_ALIPAY);
//            eTrade.setPayStatus(ConstantAll.EPAYMENT_STATUS_INPAYMENT);
//            //具体业务this.epaymentTradeService.updateByPrimaryKeySelective(eTrade);
//        }

        return payReturn;
    }

    @Override
    public boolean paySuccess(String orderNo) throws AlipayApiException {

        Props.Alipay alipay = props.getAlipay();

        if (StringUtils.isBlank(orderNo)) {
            return false;
        }

        String message = null;
        boolean paySuccess = false;

        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        com.alipay.api.AlipayClient alipayClient = new DefaultAlipayClient(AlipayConstants.URL, alipay.getAppId(), alipay.getPrivateKey(), AlipayConstants.PACKAGE_FORMAT, AlipayConstants.CHARSET, alipay.getPublicKey(), AlipayConstants.SIGN_TYPE);
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();

        model.setOutTradeNo(orderNo);
        request.setBizModel(model);

        try {
          AlipayTradeQueryResponse response = alipayClient.execute(request);

          if (!response.isSuccess()) {
              throw new AlipayException(response.getMsg(),response.getSubMsg());
          }

          log.info("查看目前支付宝响应返回体情况:{}", response.getBody());

          if (StringUtils.equalsIgnoreCase("TRADE_SUCCESS", response.getTradeStatus())) {//WAIT_BUYER_PAY（交易创建，等待买家付款）、TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）、TRADE_SUCCESS（交易支付成功）、TRADE_FINISHED（交易结束，不可退款）
              paySuccess = true;
          } else {
              log.info("处于非[TRADE_SUCCESS]交易结果:{}", response);
          }


        }catch (AlipayApiException e)
        {
          throw  new AlipayException(e);
        }

        Assert.isTrue(message == null, message);

        return paySuccess;
    }
}
