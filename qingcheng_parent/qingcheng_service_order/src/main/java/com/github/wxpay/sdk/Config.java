/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: Config
 * Author:   chenf
 * Date:     2019/8/8 0008 16:19
 * Description: 微信支付工具
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.github.wxpay.sdk;

import java.io.InputStream;

/**
 * 〈微信支付工具〉
 *
 * @author chenf
 * @create 2019/8/8 0008
 * @since 1.0.0
 */
public class Config extends WXPayConfig {

    @Override
    public String getAppID() {
        return "wx8397f8696b538317";
    }

    @Override
    public String getMchID() {
        return "1473426802";
    }

    @Override
    public String getKey() {
        return "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb";
    }

    @Override
    public InputStream getCertStream() {
        return null;
    }


    /**
     * 功能描述:
     *
     * 微信支付域名
     */

    @Override
    public IWXPayDomain getWXPayDomain() {
        return new IWXPayDomain(){

            @Override
            public void report(String s, long l, Exception e) {

            }

            @Override
            public DomainInfo getDomain(WXPayConfig wxPayConfig) {
                return new DomainInfo("api.mch.weixin.qq.com", true);
            }
        };
    }
}