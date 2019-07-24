/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: OrderTatol
 * Author:   chenf
 * Date:     2019/7/18 0018 0:28
 * Description: 订单和订单详情组合实体类
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.pojo.order;

import java.io.Serializable;
import java.util.List;

/**
 * 〈订单和订单详情组合实体类〉
 *
 * @author chenf
 * @create 2019/7/18 0018
 * @since 1.0.0
 */
public class OrderTatol implements Serializable {

//    订单主表
    private Order order;

//    订单详情
    private List<OrderItem> orderItemList;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }
}