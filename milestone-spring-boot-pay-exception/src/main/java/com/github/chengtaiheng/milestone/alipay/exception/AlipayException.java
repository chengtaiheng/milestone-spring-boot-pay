package com.github.chengtaiheng.milestone.alipay.exception;

import com.alipay.api.AlipayApiException;

/**
 * @author: 程泰恒
 */
public class AlipayException extends RuntimeException {

     private String subMessage;

     public AlipayException(AlipayApiException cause){
          super(cause);
     }

     public AlipayException(String message,String subMessage){
          super(message);
          this.subMessage = subMessage;
     }

     public String getSubMessage(){
          return this.subMessage;

     }
}
