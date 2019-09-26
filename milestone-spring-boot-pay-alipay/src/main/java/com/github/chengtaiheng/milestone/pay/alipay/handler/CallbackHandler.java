package com.github.chengtaiheng.milestone.pay.alipay.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * @author: 程泰恒
 */

public interface CallbackHandler {


    public default void signatureVerificationFailed(HttpServletRequest  request, HttpServletResponse response, String tradeNo) throws IOException {

        response.getWriter().write("failure");
        response.getWriter().flush();

    }

    public default void tradeSuccess(HttpServletRequest request,HttpServletResponse response,String tradeNo) throws IOException {

        response.getWriter().write("success");
        response.getWriter().flush();

    }

    public default void tradeFailure(HttpServletRequest request,HttpServletResponse response,String tradeNo) throws IOException {

        response.getWriter().write("failure");
        response.getWriter().flush();
    }

    public default void exceptionHandler(HttpServletRequest request,HttpServletResponse response,Exception ex,String tradeNo) throws IOException {

        response.getWriter().write("failure");
        response.getWriter().flush();
    }
}
