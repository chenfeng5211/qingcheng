package com.qingcheng.service.order;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.order.Order;
import com.qingcheng.pojo.order.OrderItem;
import com.qingcheng.pojo.order.OrderTatol;

import java.util.*;

/**
 * order业务逻辑层
 */
public interface OrderService {


    public List<Order> findAll();


    public PageResult<Order> findPage(int page, int size);


    public List<Order> findList(Map<String,Object> searchMap);


    public PageResult<Order> findPage(Map<String,Object> searchMap,int page, int size);


    public Order findById(String id);

    public void add(Order order);


    public void update(Order order);


    public void delete(String id);

    public OrderTatol findOrderById(String id);

    public List<Order> findUnConsign(Map<String,Object> searchMap);

    public void putManyConsign (List<Order> orderList);

    public void overTimeOrder();

    public void mergeOrder(String orderId1, String orderId2);

    public void splitOrder(List<OrderItem> orderItemList, String orderId);


    /**
     * 功能描述:
     *
     *
     *  生成支付订单
     */

    public Map<String, Object> saveOrder(Order order);


    /**
     * 功能描述:
     *
     * 订单支付成功后更新订单信息
     */

    public void updatePayedStatus(String payType, String orderId, String transactionId);


    /**
     * 功能描述:
     *  订单未正常完成，关闭订单
     *
     * @param orderId 订单id
     * @return: void
     * @since: 1.0.0
     * @Author:chenf
     * @Date: 2019/8/9 0009 0:35
     */
    public void closeOrder(String orderId);
}
