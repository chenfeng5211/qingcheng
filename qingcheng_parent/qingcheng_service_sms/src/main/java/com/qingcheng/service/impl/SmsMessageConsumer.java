/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: SmsMessageConsumer
 * Author:   chenf
 * Date:     2019/8/1 0001 19:50
 * Description: rabbitMQ消息消费方
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.service.impl;

import com.alibaba.fastjson.JSON;
import com.qingcheng.utils.SmsUtil;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

/**
 * 〈rabbitMQ消息接收消息〉
 *
 * @author chenf
 * @create 2019/8/1 0001
 * @since 1.0.0
 */

public class SmsMessageConsumer implements MessageListener {


    @Value("${smsCode}")
    private String smsCode;

    @Value("${param}")
    private String param;

    @Autowired
    private SmsUtil smsUtil;

    public void onMessage(Message message) {
//        获取用户发送的json字符串验证码信息，并转换成map
        String codeString = new String(message.getBody());
        Map<String, String> codeMap = JSON.parseObject(codeString, Map.class);

        String phone = codeMap.get("phone");
        String code = codeMap.get("code");
        System.out.println(phone);
        System.out.println(code);
        smsUtil.SendSms(phone, smsCode, param.replace("[value]", code));


    }
}