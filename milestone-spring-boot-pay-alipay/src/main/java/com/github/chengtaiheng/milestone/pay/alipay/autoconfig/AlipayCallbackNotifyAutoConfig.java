package com.github.chengtaiheng.milestone.pay.alipay.autoconfig;

import com.github.chengtaiheng.milestone.pay.alipay.filter.NotifyUrlFilter;
import com.github.chengtaiheng.milestone.pay.alipay.handler.CallbackHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

import java.util.Objects;

/**
 * @author: 程泰恒
 */

@ConditionalOnProperty(
        prefix = "milestone.pay",
        name = {"enabled"},
        havingValue = "true",
        matchIfMissing = true
)
public class AlipayCallbackNotifyAutoConfig {

    @Bean
    public FilterRegistrationBean<NotifyUrlFilter> filterRegistrationBean(AlipayAutoConfig.Props props,CallbackHandler callbackHandler) {

        if(Objects.isNull(callbackHandler))
        {
            callbackHandler = new CallbackHandler() {

            };
        }
        AlipayAutoConfig.Props.Alipay alipay = props.getAlipay();
        FilterRegistrationBean<NotifyUrlFilter> bean = new FilterRegistrationBean<>();
        NotifyUrlFilter myfileter = new NotifyUrlFilter(props,callbackHandler);

        bean.setName(myfileter.getClass().getName());
        bean.setFilter(myfileter);
        bean.addUrlPatterns(alipay.getNotifyPath());
        bean.setOrder(Ordered.LOWEST_PRECEDENCE);

        return bean;
    }
}
