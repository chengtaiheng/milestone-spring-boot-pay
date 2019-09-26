package com.github.chengtaiheng.milestone.pay.wechatpay.autoconfig;

import com.github.chengtaiheng.milestone.pay.wechatpay.filter.NotifyUrlFilter;
import com.github.chengtaiheng.milestone.pay.wechatpay.handler.CallbackNotifyHandler;
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
public class WechatPayNotifyAutoConfig {

    @Bean
    public FilterRegistrationBean<NotifyUrlFilter> wFilterRegistrationBean(WechatPayAutoConfig.Props props, CallbackNotifyHandler handler) {

        if (Objects.isNull(handler)) {
            handler = new CallbackNotifyHandler() {
            };
        }

        WechatPayAutoConfig.Props.WechatPay wechatPay = props.getWechatPay();
        FilterRegistrationBean bean = new FilterRegistrationBean();
        NotifyUrlFilter filter = new NotifyUrlFilter(props, handler);

        bean.setName(NotifyUrlFilter.class.getName());
        bean.setFilter(filter);
        bean.addUrlPatterns(wechatPay.getNotifyPath());
        bean.setOrder(Ordered.LOWEST_PRECEDENCE);
        return bean;
    }
}
