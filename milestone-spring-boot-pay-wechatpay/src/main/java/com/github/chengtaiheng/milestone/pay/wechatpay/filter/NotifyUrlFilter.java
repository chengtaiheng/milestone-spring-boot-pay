package com.github.chengtaiheng.milestone.pay.wechatpay.filter;

import com.github.chengtaiheng.milestone.pay.common.util.SignatureHelper;
import com.github.chengtaiheng.milestone.pay.common.util.WeChatPayUtil;
import com.github.chengtaiheng.milestone.pay.wechatpay.autoconfig.WechatPayAutoConfig;
import com.github.chengtaiheng.milestone.pay.wechatpay.handler.CallbackNotifyHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: 程泰恒
 */
public class NotifyUrlFilter extends OncePerRequestFilter {

    public static final Logger log = LoggerFactory.getLogger(NotifyUrlFilter.class);

    private WechatPayAutoConfig.Props props;

    private CallbackNotifyHandler callbackNotifyHandler;

    public NotifyUrlFilter(WechatPayAutoConfig.Props props, CallbackNotifyHandler callbackNotifyHandler) {
        this.props = props;
        this.callbackNotifyHandler = callbackNotifyHandler;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ResultResponse resultResponse = new ResultResponse();

        String reqBody = org.apache.commons.io.IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);

        String message = null;
        Map<String, String> resultObject = null;
        try {
            resultObject = WeChatPayUtil.xmlToMap(reqBody);
        } catch (Exception e) {
            callbackNotifyHandler.exceptionHandler(request, response, e, resultObject.get("out_trade_no"));
        }
        try {
            log.info("微信回调响应的返回结果是:{}", resultObject);
            if (StringUtils.equalsIgnoreCase("SUCCESS", resultObject.get("return_code"))) {
                if (StringUtils.equalsIgnoreCase("SUCCESS", resultObject.get("result_code"))) {
                    Map<String, String> payparms = new HashMap<>();
                    payparms.put("result_code", resultObject.get("result_code"));
//                    payparms.put("return_msg", resultObject.get("return_msg"));
                    payparms.put("appid", resultObject.get("appid"));
                    payparms.put("mch_id", resultObject.get("mch_id"));
                    if (StringUtils.isNoneBlank(resultObject.get("device_info")))
                        payparms.put("device_info", resultObject.get("device_info"));
                    payparms.put("nonce_str", resultObject.get("nonce_str"));
                    payparms.put("return_code", resultObject.get("return_code"));
                    if (StringUtils.isNoneBlank(resultObject.get("err_code")))
                        payparms.put("err_code", resultObject.get("err_code"));
                    if (StringUtils.isNoneBlank(resultObject.get("err_code_des")))
                        payparms.put("err_code_des", resultObject.get("err_code_des"));
                    payparms.put("openid", resultObject.get("openid"));
                    payparms.put("is_subscribe", resultObject.get("is_subscribe"));
                    payparms.put("trade_type", resultObject.get("trade_type"));
                    payparms.put("bank_type", resultObject.get("bank_type"));
                    payparms.put("total_fee", resultObject.get("total_fee"));
                    if (StringUtils.isNoneBlank(resultObject.get("fee_type")))
                        payparms.put("fee_type", resultObject.get("fee_type"));
                    payparms.put("cash_fee", resultObject.get("cash_fee"));
                    if (StringUtils.isNoneBlank(resultObject.get("cash_fee_type")))
                        payparms.put("cash_fee_type", resultObject.get("cash_fee_type"));
                    if (StringUtils.isNoneBlank(resultObject.get("coupon_fee")))
                        payparms.put("coupon_fee", resultObject.get("coupon_fee"));
                    if (StringUtils.isNoneBlank(resultObject.get("coupon_count")))
                        payparms.put("coupon_count", resultObject.get("coupon_count"));
                    if (StringUtils.isNoneBlank(resultObject.get("coupon_id_1")))
                        payparms.put("coupon_id_1", resultObject.get("coupon_id_1"));
                    if (StringUtils.isNoneBlank(resultObject.get("coupon_fee_1")))
                        payparms.put("coupon_fee_1", resultObject.get("coupon_fee_1"));
                    payparms.put("transaction_id", resultObject.get("transaction_id"));
                    payparms.put("out_trade_no", resultObject.get("out_trade_no"));
                    if (StringUtils.isNoneBlank(resultObject.get("attach")))
                        payparms.put("attach", resultObject.get("attach"));
                    payparms.put("time_end", resultObject.get("time_end"));
                    log.info("结果字典是:{} 封装字典是:{}", resultObject, payparms);
                    String signLocal = SignatureHelper.getInstance().getSign2(payparms, "key", props.getWechatPay().getSecretKey(), false,
                            SignatureHelper.DigestType.MD5, SignatureHelper.CharacterCase.UPPER_CASE);
                    log.info("输出结果是: 支付结果通知 sign:{},signLocal:{}", resultObject.get("sign"), signLocal);
                    boolean isValidSign = StringUtils.equalsIgnoreCase(resultObject.get("sign"), signLocal);
                    if (isValidSign) {
                        callbackNotifyHandler.tradeSuccess(request, response, resultObject.get("out_trade_no"));
                    } else {
                        callbackNotifyHandler.signatureVerificationFailed(request, response, resultObject.get("out_trade_no"));
                    }
                } else {
                    callbackNotifyHandler.tradeFailure(request, response, resultObject.get("out_trade_no"));
                }
            } else {
                callbackNotifyHandler.tradeFailure(request, response, resultObject.get("out_trade_no"));
            }
        } catch (Exception e) {
            callbackNotifyHandler.exceptionHandler(request, response, e, resultObject.get("out_trade_no"));
        }
    }


    /**
     * 微信格式化输出
     */
    public static class ResultResponse {
        private String returnCode = "SUCCESS";
        private String returnMsg = "OK";

        public String toXmlString() {
            StringBuilder sb = new StringBuilder();
            sb.append("<xml><return_code><![CDATA[")
                    .append(returnCode)
                    .append("]]></return_code><return_msg><![CDATA[")
                    .append(returnMsg)
                    .append("]]></return_msg></xml>");
            return sb.toString();
        }
    }
}
