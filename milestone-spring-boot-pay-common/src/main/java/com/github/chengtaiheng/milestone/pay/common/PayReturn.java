package com.github.chengtaiheng.milestone.pay.common;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author 程泰恒
 */
@Getter
@Setter
public abstract class PayReturn implements Serializable {

    private static final long serialVersionUID = 786404034580230270L;
    /**
     * 订单的支付编号
     */
    private String orderNumber;

}
