/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: OrderController
 * Author:   chenf
 * Date:     2019/8/7 0007 8:57
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.order.Order;
import com.qingcheng.service.order.OrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 〈〉
 *
 * @author chenf
 * @create 2019/8/7 0007
 * @since 1.0.0
 */
@RestController
@RequestMapping("/order")
public class OrderController {
    @Reference
    private OrderService orderService;

    @RequestMapping("/saveOrder")
    public Map<String, Object> saveOrder(@RequestBody Order order){

//        获取当前用户
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        order.setUsername(username);

        return orderService.saveOrder(order);
    }
}