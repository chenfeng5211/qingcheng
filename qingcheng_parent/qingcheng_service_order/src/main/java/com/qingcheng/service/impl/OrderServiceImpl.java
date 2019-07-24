package com.qingcheng.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qingcheng.dao.OrderConfigMapper;
import com.qingcheng.dao.OrderItemMapper;
import com.qingcheng.dao.OrderLogMapper;
import com.qingcheng.dao.OrderMapper;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.order.*;
import com.qingcheng.service.order.OrderService;
import com.qingcheng.util.IdWorker;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = OrderService.class)
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private OrderLogMapper orderLogMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private OrderConfigMapper orderConfigMapper;

    /**
     * 返回全部记录
     *
     * @return
     */
    @Override
    public List<Order> findAll() {
        return orderMapper.selectAll();
    }

    /**
     * 分页查询
     *
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    @Override
    public PageResult<Order> findPage(int page, int size) {
        PageHelper.startPage(page, size);
        Page<Order> orders = (Page<Order>) orderMapper.selectAll();
        return new PageResult<Order>(orders.getTotal(), orders.getResult());
    }

    /**
     * 条件查询
     *
     * @param searchMap 查询条件
     * @return
     */
    @Override
    public List<Order> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return orderMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     *
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageResult<Order> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page, size);
        Example example = createExample(searchMap);
        Page<Order> orders = (Page<Order>) orderMapper.selectByExample(example);
        return new PageResult<Order>(orders.getTotal(), orders.getResult());
    }

    /**
     * 根据Id查询
     *
     * @param id
     * @return
     */
    @Override
    public Order findById(String id) {
        return orderMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     *
     * @param order
     */
    @Override
    public void add(Order order) {
        orderMapper.insert(order);
    }

    /**
     * 修改
     *
     * @param order
     */
    @Override
    public void update(Order order) {
        orderMapper.updateByPrimaryKeySelective(order);
    }

    /**
     * 删除
     *
     * @param id
     */
    @Override
    public void delete(String id) {
        orderMapper.deleteByPrimaryKey(id);
    }


    /**
     * 功能描述:
     * 查询主表和详表
     */

    @Override
    public OrderTatol findOrderById(String id) {

//        获取Order对象
        Order order = orderMapper.selectByPrimaryKey(id);

//        获取OrderItems列表
        Example example = new Example(OrderItem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderId", id);
        List<OrderItem> orderItems = orderItemMapper.selectByExample(example);

//        封装到OrderTatol
        OrderTatol orderTatol = new OrderTatol();
        orderTatol.setOrder(order);
        orderTatol.setOrderItemList(orderItems);
        return orderTatol;
    }


    /**
     * 功能描述:
     * <p>
     * 查询需要发货的商品列表
     */

    @Override
    public List<Order> findUnConsign(Map<String, Object> searchMap) {
//        添加查询条件
        Example example = new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();

        List<Order> orderList = null;
        if (searchMap.get("ids") != null) {
//            JSONArray idsJSON = (JSONArray) searchMap.get("ids");
//            String[] ids = new String[idsJSON.size()];
//            for (int i = 0; i < ids.length; i++) {
//                ids[i] = idsJSON.getString(i);
//            }
            criteria.andIn("id", (Iterable) searchMap.get("ids"));
            criteria.andEqualTo("consignStatus", searchMap.get("consignStatus"));
            orderList = orderMapper.selectByExample(example);
        }
        return orderList;
    }


    /**
     * 功能描述:
     * 批量发货
     */


    @Override
    @Transactional
    public void putManyConsign(List<Order> orderList) {

//        判断配送方法，物流单号是否为空
        for (Order order : orderList) {
            if (order.getShippingName() == null || order.getShippingCode() == null) {
                throw new RuntimeException("物流信息不全");
            }
        }

//        循环发货
        for (Order order : orderList) {
            order.setConsignStatus("1");
            order.setOrderStatus("2");
            order.setConsignTime(new Date());

            orderMapper.updateByPrimaryKeySelective(order);

//            记录日志
            OrderLog orderLog = new OrderLog();
            orderLog.setId(idWorker.nextId() + "");
            orderLog.setConsignStatus("1");
            orderLog.setOperater("system");
            orderLog.setOperateTime(new Date());
            orderLog.setOrderId(order.getId());
            orderLog.setOrderStatus(order.getOrderStatus());
            orderLog.setPayStatus(order.getPayStatus());
            orderLog.setRemarks("系统操作");

            orderLogMapper.insertSelective(orderLog);
        }

    }

    /**
     * 功能描述:
     * 超时订单自动关闭
     */

    @Transactional
    @Override
    public void overTimeOrder() {
        OrderConfig orderConfig = orderConfigMapper.selectByPrimaryKey(1);
//        获取超时时间段
        Integer orderTimeout = orderConfig.getOrderTimeout();

//        获取超时时间
        LocalDateTime localDateTime = LocalDateTime.now().minusMinutes(orderTimeout);

//        设置订单关闭条件
        Example example = new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andLessThan("createTime", localDateTime);
        criteria.andEqualTo("payStatus", "0");
        criteria.andEqualTo("orderStatus", "0");
        criteria.andEqualTo("isDelete", "0");

//        查询超时订单
        List<Order> orders = orderMapper.selectByExample(example);

        for (Order order : orders) {

//        更改订单状态，关闭订单
            order.setOrderStatus("4");
            order.setEndTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);

//        记录订单日志
            OrderLog orderLog = new OrderLog();
            orderLog.setId(idWorker.nextId() + "");
            orderLog.setOperater("system");
            orderLog.setOperateTime(new Date());
            orderLog.setOrderId(order.getId());
            orderLog.setOrderStatus("4");
            orderLog.setPayStatus(order.getPayStatus());
            orderLog.setRemarks("测试2");
            orderLog.setConsignStatus(order.getConsignStatus());
            orderLogMapper.insertSelective(orderLog);

        }
    }


    /**
     * 功能描述:
     * 合并订单
     */

    @Transactional
    @Override
    public void mergeOrder(String orderId1, String orderId2) {
//        从数据库查询两个订单对象
        Order order1 = orderMapper.selectByPrimaryKey(orderId1);
        Order order2 = orderMapper.selectByPrimaryKey(orderId2);

        if (order1 == null) {
            throw new RuntimeException("主订单不存在");
        }
        if (order2 == null) {
            throw new RuntimeException("从订单不存在");
        }

//        修改主订单相关信息
        //修改时间
        order1.setUpdateTime(new Date());
//        实际支付金额
        order1.setPayMoney(order1.getPayMoney() + order2.getPayMoney());
//        总金额
        order1.setTotalMoney(order1.getTotalMoney() + order2.getTotalMoney());
//        优惠金额
        order1.setPreMoney(order1.getPreMoney() + order2.getPreMoney());
//        总数量
        order1.setTotalNum(order1.getTotalNum() + order2.getTotalNum());
//        邮费
        order1.setPostFee(order1.getPostFee() + order2.getPostFee());
        orderMapper.updateByPrimaryKeySelective(order1);

//        获取从订单订单明细列表
        Example example = new Example(OrderItem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderId", order2.getId());
        List<OrderItem> orderItems = orderItemMapper.selectByExample(example);
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrderId(order1.getId());
            orderItemMapper.updateByPrimaryKeySelective(orderItem);
        }
//        逻辑删除从订单的
        order2.setIsDelete("1");
        orderMapper.updateByPrimaryKeySelective(order2);

//        记录日志
//        主订单日志
        OrderLog order1Log = new OrderLog();
        order1Log.setConsignStatus("0");
        order1Log.setId(idWorker.nextId() + "");
        order1Log.setOperater("admin");
        if (StringUtils.isEmpty(order1.getId())) {
            order1Log.setOrderId(order1.getId());
        }
        order1Log.setOrderStatus("0");
        order1Log.setPayStatus("0");
        order1Log.setRemarks("合并后主订单");
        order1Log.setOperateTime(new Date());
        order1Log.setOrderId("1");
        orderLogMapper.insertSelective(order1Log);

//        从订单日志
        OrderLog order2Log = new OrderLog();
        order2Log.setConsignStatus("0");
        order2Log.setId(idWorker.nextId() + "");
        order2Log.setOperater("admin");
        if (StringUtils.isEmpty(order2.getId())) {
            order2Log.setOrderId(order2.getId());
        }
        order2Log.setOrderStatus("4");
        order2Log.setPayStatus("0");
        order2Log.setRemarks("合并后从订单");
        order2Log.setOperateTime(new Date());
        order2Log.setOrderId("1");
        orderLogMapper.insertSelective(order2Log);

    }


    /**
     * 功能描述:
     * <p>
     * 订单拆分
     */

    @Transactional
    @Override
    public void splitOrder(List<OrderItem> orderList, String orderId) {
//        查询旧订单
        Order oldOrder = orderMapper.selectByPrimaryKey(orderId);
        if (oldOrder == null) {
            throw new RuntimeException("无此订单");
        }

//        创建新的订单
        Order newOrder = new Order();
//        设置新订单和旧订单相同属性值
        BeanUtils.copyProperties(oldOrder, newOrder);

//        定义新订单其他属性
        Date date = new Date();
        newOrder.setId(idWorker.nextId() + "");
        newOrder.setCreateTime(date);
        newOrder.setUpdateTime(date);
        newOrder.setPayMoney(0);
        newOrder.setPostFee(0);
        newOrder.setPreMoney(0);
        newOrder.setTotalMoney(0);


//        判断主订单数量是否小于拆封数量
        for (OrderItem newOrderItem : orderList) {
//            获取原订单明细中的订单数量
            OrderItem oldOrderItem = orderItemMapper.selectByPrimaryKey(newOrderItem.getId());
            Integer oldOrderItemNum = oldOrderItem.getNum();

//            获取拆分订单中的订单数
            Integer newOrderItemNum = newOrderItem.getNum();
//            将原订单详情属性值复制给新订单
            BeanUtils.copyProperties(oldOrderItem, newOrderItem);
            if (oldOrderItemNum <= 1) {
                continue;
            }
            if(newOrderItemNum.equals(oldOrderItemNum)){
                throw new RuntimeException("拆分数量相同");
            }
            if (newOrderItemNum > oldOrderItemNum) {
                throw new RuntimeException("输入的拆分数量不正确");
            } else {

//                设置新订单详情属性
                newOrderItem.setId(idWorker.nextId() + "");
                newOrderItem.setNum(newOrderItemNum);
                newOrderItem.setMoney(oldOrderItem.getMoney() / oldOrderItemNum * newOrderItemNum);
                newOrderItem.setPayMoney(oldOrderItem.getPayMoney() / oldOrderItemNum * newOrderItemNum);
                newOrderItem.setPostFee(oldOrderItem.getPostFee() / oldOrderItemNum * newOrderItemNum);
                newOrderItem.setWeight(oldOrderItem.getWeight() / oldOrderItemNum * newOrderItemNum);
                newOrderItem.setOrderId(newOrder.getId());
                orderItemMapper.insertSelective(newOrderItem);

//                设置旧订单详情属性
                oldOrderItem.setNum(oldOrderItemNum - newOrderItemNum);
                oldOrderItem.setMoney(oldOrderItem.getMoney() - newOrderItem.getMoney());
                oldOrderItem.setPayMoney(oldOrderItem.getPayMoney() - newOrderItem.getPayMoney());
                oldOrderItem.setPostFee(oldOrderItem.getPostFee() - newOrderItem.getPostFee());
                oldOrderItem.setWeight(oldOrderItem.getWeight() - newOrderItem.getWeight());
                orderItemMapper.updateByPrimaryKeySelective(oldOrderItem);

//                设置新订单属性   这里还是要先定义一下新订单的部分属性值，让其归位0
//                                因为新订单属性值是从旧订单复制过来的，造成数值不准确
                newOrder.setPayMoney(newOrder.getPayMoney() + newOrderItem.getPayMoney());
                newOrder.setPreMoney(newOrder.getPreMoney() + newOrderItem.getMoney() - newOrderItem.getPayMoney() - newOrderItem.getPostFee());
                newOrder.setTotalMoney(newOrder.getTotalMoney() + newOrderItem.getMoney());
                newOrder.setPostFee(newOrder.getPostFee() + newOrderItem.getPostFee());
                newOrder.setTotalNum(newOrder.getTotalNum() + newOrderItem.getNum());

//                设置旧订单
                oldOrder.setTotalNum(oldOrder.getTotalNum() - newOrderItem.getNum());
                oldOrder.setTotalMoney(oldOrder.getTotalMoney() - newOrderItem.getMoney());
                oldOrder.setPreMoney(oldOrder.getPreMoney() - (newOrderItem.getMoney() + newOrderItem.getPostFee() - newOrderItem.getPayMoney()));
                oldOrder.setPayMoney(oldOrder.getPayMoney() - newOrderItem.getPayMoney());
                oldOrder.setPostFee(oldOrder.getPostFee() - newOrderItem.getPostFee());
                oldOrder.setUpdateTime(date);

            }


        }
        orderMapper.updateByPrimaryKeySelective(oldOrder);
        orderMapper.insertSelective(newOrder);


//        记录旧订单日志
        OrderLog oldOrderLog = new OrderLog();
        if (!StringUtils.isEmpty(oldOrder.getId())) {
            oldOrderLog.setOrderId(oldOrder.getId());
        }
        oldOrderLog.setOrderStatus(oldOrder.getOrderStatus());
        oldOrderLog.setPayStatus(oldOrder.getPayStatus());
        oldOrderLog.setRemarks("拆分后的旧订单");
        oldOrderLog.setConsignStatus(oldOrder.getConsignStatus());
        oldOrderLog.setId(idWorker.nextId() + "");
        oldOrderLog.setOperater("admin");
        oldOrderLog.setOperateTime(date);
        orderLogMapper.insertSelective(oldOrderLog);

//        记录新订单日志
        OrderLog newOrderLog = new OrderLog();
        newOrderLog.setId(idWorker.nextId() + "");
        newOrderLog.setOperater("admin");
        newOrderLog.setOperateTime(date);
        if (!StringUtils.isEmpty(newOrder.getId())) {
            newOrderLog.setOrderId(newOrder.getId());
        }
        newOrderLog.setOrderStatus(newOrder.getOrderStatus());
        newOrderLog.setPayStatus(newOrder.getPayStatus());
        newOrderLog.setRemarks("拆分后新订单");
        orderLogMapper.insertSelective(newOrderLog);


    }

    /**
     * 构建查询条件
     *
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap) {
        Example example = new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        if (searchMap != null) {
            // 订单id
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                criteria.andLike("id", "%" + searchMap.get("id") + "%");
            }
            // 支付类型，1、在线支付、0 货到付款
            if (searchMap.get("payType") != null && !"".equals(searchMap.get("payType"))) {
                criteria.andLike("payType", "%" + searchMap.get("payType") + "%");
            }
            // 物流名称
            if (searchMap.get("shippingName") != null && !"".equals(searchMap.get("shippingName"))) {
                criteria.andLike("shippingName", "%" + searchMap.get("shippingName") + "%");
            }
            // 物流单号
            if (searchMap.get("shippingCode") != null && !"".equals(searchMap.get("shippingCode"))) {
                criteria.andLike("shippingCode", "%" + searchMap.get("shippingCode") + "%");
            }
            // 用户名称
            if (searchMap.get("username") != null && !"".equals(searchMap.get("username"))) {
                criteria.andLike("username", "%" + searchMap.get("username") + "%");
            }
            // 买家留言
            if (searchMap.get("buyerMessage") != null && !"".equals(searchMap.get("buyerMessage"))) {
                criteria.andLike("buyerMessage", "%" + searchMap.get("buyerMessage") + "%");
            }
            // 是否评价
            if (searchMap.get("buyerRate") != null && !"".equals(searchMap.get("buyerRate"))) {
                criteria.andLike("buyerRate", "%" + searchMap.get("buyerRate") + "%");
            }
            // 收货人
            if (searchMap.get("receiverContact") != null && !"".equals(searchMap.get("receiverContact"))) {
                criteria.andLike("receiverContact", "%" + searchMap.get("receiverContact") + "%");
            }
            // 收货人手机
            if (searchMap.get("receiverMobile") != null && !"".equals(searchMap.get("receiverMobile"))) {
                criteria.andLike("receiverMobile", "%" + searchMap.get("receiverMobile") + "%");
            }
            // 收货人地址
            if (searchMap.get("receiverAddress") != null && !"".equals(searchMap.get("receiverAddress"))) {
                criteria.andLike("receiverAddress", "%" + searchMap.get("receiverAddress") + "%");
            }
            // 订单来源：1:web，2：app，3：微信公众号，4：微信小程序  5 H5手机页面
            if (searchMap.get("sourceType") != null && !"".equals(searchMap.get("sourceType"))) {
                criteria.andLike("sourceType", "%" + searchMap.get("sourceType") + "%");
            }
            // 交易流水号
            if (searchMap.get("transactionId") != null && !"".equals(searchMap.get("transactionId"))) {
                criteria.andLike("transactionId", "%" + searchMap.get("transactionId") + "%");
            }
            // 订单状态
            if (searchMap.get("orderStatus") != null && !"".equals(searchMap.get("orderStatus"))) {
                criteria.andLike("orderStatus", "%" + searchMap.get("orderStatus") + "%");
            }
            // 支付状态
            if (searchMap.get("payStatus") != null && !"".equals(searchMap.get("payStatus"))) {
                criteria.andLike("payStatus", "%" + searchMap.get("payStatus") + "%");
            }
            // 发货状态
            if (searchMap.get("consignStatus") != null && !"".equals(searchMap.get("consignStatus"))) {
                criteria.andLike("consignStatus", "%" + searchMap.get("consignStatus") + "%");
            }
            // 是否删除
            if (searchMap.get("isDelete") != null && !"".equals(searchMap.get("isDelete"))) {
                criteria.andLike("isDelete", "%" + searchMap.get("isDelete") + "%");
            }

            // 数量合计
            if (searchMap.get("totalNum") != null) {
                criteria.andEqualTo("totalNum", searchMap.get("totalNum"));
            }
            // 金额合计
            if (searchMap.get("totalMoney") != null) {
                criteria.andEqualTo("totalMoney", searchMap.get("totalMoney"));
            }
            // 优惠金额
            if (searchMap.get("preMoney") != null) {
                criteria.andEqualTo("preMoney", searchMap.get("preMoney"));
            }
            // 邮费
            if (searchMap.get("postFee") != null) {
                criteria.andEqualTo("postFee", searchMap.get("postFee"));
            }
            // 实付金额
            if (searchMap.get("payMoney") != null) {
                criteria.andEqualTo("payMoney", searchMap.get("payMoney"));
            }

        }
        return example;
    }

}
