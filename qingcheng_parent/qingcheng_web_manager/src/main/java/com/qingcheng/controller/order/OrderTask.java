/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: OrderTask
 * Author:   chenf
 * Date:     2019/7/19 0019 1:08
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.controller.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.service.order.OrderService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 〈〉
 *
 * @author chenf
 * @create 2019/7/19 0019
 * @since 1.0.0
 */
@Component
public class OrderTask {

    @Reference
    private OrderService orderService;
    @Scheduled(cron="0 0/2 * * * ?")
    public void overTimeOrder(){
        orderService.overTimeOrder();

    }
}