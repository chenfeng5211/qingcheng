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
}
