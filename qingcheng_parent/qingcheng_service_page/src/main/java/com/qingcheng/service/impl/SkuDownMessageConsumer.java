/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: SkuDownMessageConsumer
 * Author:   chenf
 * Date:     2019/8/2 0002 12:37
 * Description: 商品下架
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.util.Map;

/**
 * 〈商品下架消费方〉
 *
 * @author chenf
 * @create 2019/8/2 0002
 * @since 1.0.0
 */
@Service
public class SkuDownMessageConsumer implements MessageListener {

    @Value("${pagePath}")
    private String pagePath;


    public void onMessage(Message message) {
//        1.从队列获取skuid
        String jsonSkuId = new String(message.getBody());
        Map<String, String> skuMap = JSON.parseObject(jsonSkuId, Map.class);

//        2.对相关商品执行删除操作
        String skuId = skuMap.get("skuId");
        System.out.println("ddddddddddddddddddddddddddddddddddddddddd");
        File file = new File(pagePath, skuId+".html");
//        删除生成的商品详情页
        if(file.exists()) {
            final boolean delete = file.delete();
        }


    }
}