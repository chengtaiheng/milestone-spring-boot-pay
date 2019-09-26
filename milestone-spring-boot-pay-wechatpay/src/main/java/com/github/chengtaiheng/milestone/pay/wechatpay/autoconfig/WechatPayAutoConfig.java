package com.github.chengtaiheng.milestone.pay.wechatpay.autoconfig;

import com.github.chengtaiheng.milestone.pay.wechatpay.WechatPayClient;
import com.github.chengtaiheng.milestone.pay.wechatpay.impl.WechatPayClientImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author: 程泰恒
 */

@EnableConfigurationProperties({WechatPayAutoConfig.Props.class})
@ConditionalOnProperty(
        prefix = "milestone.pay",
        name = {"enabled"},
        havingValue = "true",
        matchIfMissing = true
)
public class WechatPayAutoConfig {

    @Bean
    @ConditionalOnMissingBean
    public WechatPayClient wechatPayClient(){
        return new WechatPayClientImpl();
    }

    @ConfigurationProperties("milestone.pay")
    public static class Props {
        private boolean enabled = true;

        private WechatPay wechatPay;

        public Props() {
            this.wechatPay = new WechatPay();
        }

        public boolean isEnabled() {
            return enabled;
        }

        public WechatPay getWechatPay() {
            return wechatPay;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public void setWechatPay(WechatPay wechatPay) {
            this.wechatPay = wechatPay;
        }

        public static class WechatPay {

            private String appId;

            private String mchId;

            private String secretKey;

            private String notifyUrl;

            private String notifyPath;

            public WechatPay() {
            }

            public String getNotifyPath() {
                return notifyPath;
            }

            public void setNotifyPath(String notifyPath) {
                this.notifyPath = notifyPath;
            }

            public String getAppId() {
                return appId;
            }

            public String getMchId() {
                return mchId;
            }

            public String getSecretKey() {
                return secretKey;
            }

            public String getNotifyUrl() {
                return notifyUrl;
            }

            public void setAppId(String appId) {
                this.appId = appId;
            }

            public void setMchId(String mchId) {
                this.mchId = mchId;
            }

            public void setSecretKey(String secretKey) {
                this.secretKey = secretKey;
            }

            public void setNotifyUrl(String notifyUrl) {
                this.notifyUrl = notifyUrl;
            }
        }
    }
}
