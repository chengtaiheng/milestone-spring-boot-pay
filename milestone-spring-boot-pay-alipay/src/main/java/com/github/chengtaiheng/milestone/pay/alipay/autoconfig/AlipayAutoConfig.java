package com.github.chengtaiheng.milestone.pay.alipay.autoconfig;

import com.github.chengtaiheng.milestone.pay.alipay.AlipayClient;
import com.github.chengtaiheng.milestone.pay.alipay.impl.AlipayClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author: 程泰恒
 */
@EnableConfigurationProperties({AlipayAutoConfig.Props.class})
@ConditionalOnProperty(
        prefix = "milestone.pay",
        name = {"enabled"},
        havingValue = "true",
        matchIfMissing = true
)
public class AlipayAutoConfig {

    private static final Logger log = LoggerFactory.getLogger(AlipayAutoConfig.class);

    @Bean
    @ConditionalOnMissingBean
    public AlipayClient alipayClient() {
        return new AlipayClientImpl();
    }

    @ConfigurationProperties("milestone.pay")
    public static class Props {

        private boolean enabled = true;

        private AlipayAutoConfig.Props.Alipay alipay;

        public boolean isEnabled() {
            return enabled;
        }

        public Alipay getAlipay() {
            return alipay;
        }

        public void setEnabled(boolean param) {
            this.enabled = param;
        }

        public void setAlipay(Alipay param) {
            this.alipay = param;
        }

        Props() {
            this.alipay = new AlipayAutoConfig.Props.Alipay();
        }

        public static class Alipay {

            private String appId = "";

            private String privateKey = "";

            private String publicKey = "";

            private String notifyUrl = "";

            private String notifyPath = "";


            Alipay() {

            }

            public String getAppId() {
                return appId;
            }

            public String getPrivateKey() {
                return privateKey;
            }

            public String getPublicKey() {
                return publicKey;
            }

            public String getNotifyUrl() {
                return notifyUrl;
            }

            public void setAppId(String appId) {
                this.appId = appId;
            }

            public void setPrivateKey(String privateKey) {
                this.privateKey = privateKey;
            }

            public void setPublicKey(String publicKey) {
                this.publicKey = publicKey;
            }

            public void setNotifyUrl(String notifyUrl) {
                this.notifyUrl = notifyUrl;
            }

            public void setNotifyPath(String notifyPath) {
                this.notifyPath = notifyPath;
            }

            public String getNotifyPath() {

                return notifyPath;
            }
        }
    }

}
