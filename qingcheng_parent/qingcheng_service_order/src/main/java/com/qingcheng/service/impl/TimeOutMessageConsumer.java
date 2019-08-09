/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: TimeOutMessageConsumer
 * Author:   chenf
 * Date:     2019/8/9 0009 1:49
 * Description: 演示支付MQ处理‘
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.qingcheng.dao.OrderMapper;
import com.qingcheng.pojo.order.Order;
import com.qingcheng.service.order.OrderService;
import com.qingcheng.service.order.WxPayService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * 〈演示支付MQ处理〉
 *
 * @author chenf
 * @create 2019/8/9 0009
 * @since 1.0.0
 */
public class TimeOutMessageConsumer implements MessageListener {

    @Autowired
    private WxPayService wxPayService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderService orderService;


    @Override
    public void onMessage(Message message) {
//        获取订单id
        String orderId = new String(message.getBody());

        if(!StringUtils.isEmpty(orderId)){
//            查询数据库订单支付状态
            Order order = orderMapper.selectByPrimaryKey(orderId);
            if("0".equals(order.getPayStatus())) {
                System.out.println("超时支付mQ..............................................");
//                查询微信订单支付状态
                Map<String, String> orderStatusMap = wxPayService.findOrder(orderId);
                if ("NOTPAY".equals(orderStatusMap.get("trade_state")) && "SUCCESS".equals(orderStatusMap.get("result_code"))) {
                    orderService.closeOrder(orderId);
                }
            }else{
                order.setPayStatus("1");
                orderMapper.updateByPrimaryKeySelective(order);
            }
        }
    }
}