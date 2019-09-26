package com.github.chengtaiheng.milestone.pay.wechatpay.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: 程泰恒
 */
public interface CallbackNotifyHandler {

    public  static final Logger log = LoggerFactory.getLogger(CallbackNotifyHandler.class);

    public default void signatureVerificationFailed(HttpServletRequest request,HttpServletResponse response,String tradeNo) throws IOException {
        ResultResponse.returnCode = "FAILURE";
        ResultResponse.returnMsg = "SIGN INVALID";

        response.getWriter().write(ResultResponse.toXmlString());
        response.getWriter().flush();
    }

    public  default  void exceptionHandler(HttpServletRequest request,HttpServletResponse response,Exception e,String tradeNo) throws IOException {
        ResultResponse.returnCode = "FAILURE";
        ResultResponse.returnMsg = "EXCEPTION";

        response.getWriter().write(ResultResponse.toXmlString());
        response.getWriter().flush();

    }

    public default void tradeSuccess(HttpServletRequest request,HttpServletResponse response,String tradeNo) throws IOException {
        response.getWriter().write(ResultResponse.toXmlString());
        response.getWriter().flush();
    }

    public  default void tradeFailure(HttpServletRequest request,HttpServletResponse response,String tradeNo) throws IOException {
        ResultResponse.returnMsg = "FAILURE";
        ResultResponse.returnCode = "FAILURE";

        response.getWriter().write(ResultResponse.toXmlString());
        response.getWriter().flush();
    }


    /**
     * 微信格式化输出
     */
    public static class ResultResponse {
        private static String  returnCode = "SUCCESS";
        private static String returnMsg = "OK";

        public static String toXmlString() {
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
