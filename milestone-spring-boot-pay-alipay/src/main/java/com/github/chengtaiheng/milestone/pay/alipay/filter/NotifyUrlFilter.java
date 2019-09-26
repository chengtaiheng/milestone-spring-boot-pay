package com.github.chengtaiheng.milestone.pay.alipay.filter;


import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.github.chengtaiheng.milestone.pay.alipay.autoconfig.AlipayAutoConfig;
import com.github.chengtaiheng.milestone.pay.alipay.handler.CallbackHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author: 程泰恒
 */

public class NotifyUrlFilter extends OncePerRequestFilter {

    public final static Logger log = LoggerFactory.getLogger(NotifyUrlFilter.class);

    public final static String SIGN_TYPE = "RSA2";
    public final static String CHARSET = "UTF-8";

    private AlipayAutoConfig.Props props;

    private CallbackHandler callbackHandler;

    public NotifyUrlFilter(AlipayAutoConfig.Props props, CallbackHandler callbackHandler) {
        this.props = props;
        this.callbackHandler = callbackHandler;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        AlipayAutoConfig.Props.Alipay alipay = props.getAlipay();
        String message = null;

        String tradeStatus = request.getParameter("trade_status");
        String outTradeNo = request.getParameter("out_trade_no");
        String subject = request.getParameter("subject");

        try {
            // 获取支付宝POST过来反馈信息
            log.info("获取支付宝POST过来反馈信息显示:");
            log.info("Content-Type = {}", request.getHeader("Content-Type"));
            log.debug("tradeStatus = {}", tradeStatus);

            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();

            for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
                String name = iter.next();
                String[] values = (String[]) requestParams.get(name);
                String valueStr = "";

                for (int i = 0; i < values.length; i++) {
                    String value = values[i];
                    valueStr = (i == values.length - 1) ? valueStr + value : valueStr + value + ",";
                }

                log.info("key = {} , value = {}", name, valueStr);

                params.put(name, valueStr);//凡是通知返回回来的参数皆是待验签的参数
            }

            boolean isValidSign = false;

            isValidSign = AlipaySignature.rsaCheckV1(params, alipay.getPublicKey(), CHARSET, SIGN_TYPE);

            if (!isValidSign) {
                callbackHandler.signatureVerificationFailed(request, response, outTradeNo);
            }

            log.info("isValidSign = {}", isValidSign);

            if (isValidSign) {
                if (StringUtils.equals("TRADE_SUCCESS", tradeStatus) == false) {
                   callbackHandler.tradeFailure(request, response, outTradeNo);
                } else {
                   callbackHandler.tradeSuccess(request, response, outTradeNo);
                }
            }

        }catch (AlipayApiException e) {
            callbackHandler.exceptionHandler(request, response, e, outTradeNo);
        }
        finally {
            try {
                response.getWriter().close();
            } catch (IOException e) {
                callbackHandler.exceptionHandler(request, response, e,outTradeNo);
            }
        }

    }
}
