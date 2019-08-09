/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: StockBackMessageConsumer
 * Author:   chenf
 * Date:     2019/8/7 0007 18:13
 * Description: tabbitMQ消费方，监听订单生成失败后发来的消息
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.service.impl;

import com.alibaba.fastjson.JSON;
import com.qingcheng.pojo.order.OrderItem;
import com.qingcheng.service.goods.StockBackService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 〈tabbitMQ消费方，监听订单生成失败后发来的消息〉
 *
 * @author chenf
 * @create 2019/8/7 0007
 * @since 1.0.0
 */
public class StockBackMessageConsumer implements MessageListener {

    @Autowired
    private StockBackService stockBackService;


    public void onMessage(Message message) {
//        获取消费方发来的消息
        String orderItemListJson = new String(message.getBody());

        List<OrderItem> orderItems = JSON.parseArray(orderItemListJson, OrderItem.class);

//        调用stockBackService方法完成回滚信息存储
        stockBackService.addStockBack(orderItems);


    }
}