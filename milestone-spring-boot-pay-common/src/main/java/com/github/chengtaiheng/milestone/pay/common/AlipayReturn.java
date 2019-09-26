package com.github.chengtaiheng.milestone.pay.common;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 程泰恒
 */

@Getter
@Setter
@ToString
public class AlipayReturn extends PayReturn {

    private static final long serialVersionUID = 7309385559192605866L;
    /**
     * 阿里支付参数 (类似于签名)
     */
    private String aliParams = "";

}
