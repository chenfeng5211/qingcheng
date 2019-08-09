/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: StockBackServiceImpl
 * Author:   chenf
 * Date:     2019/8/7 0007 18:01
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.qingcheng.dao.SkuMapper;
import com.qingcheng.dao.StockBackMapper;
import com.qingcheng.pojo.goods.StockBack;
import com.qingcheng.pojo.order.OrderItem;
import com.qingcheng.service.goods.StockBackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * 〈〉
 *
 * @author chenf
 * @create 2019/8/7 0007
 * @since 1.0.0
 */
@Service(interfaceClass = StockBackService.class)
public class StockBackServiceImpl implements StockBackService {

    @Autowired
    private StockBackMapper stockBackMapper;

    @Autowired
    private SkuMapper skuMapper;

    /**
     * 功能描述:
     *
     * 订单生成失败保存到库存表
     */

    @Transactional
    public void addStockBack(List<OrderItem> orderItemList) {
        for (OrderItem orderItem : orderItemList) {
            StockBack stockBack = new StockBack();

            System.out.println("生产者发送消息、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、");
            stockBack.setCreateTime(new Date());
            stockBack.setNum(orderItem.getNum());
            stockBack.setOrderId(orderItem.getOrderId());
            stockBack.setSkuId(orderItem.getSkuId());
            stockBack.setStatus("0");
            stockBackMapper.insertSelective(stockBack);
        }
    }

    @Transactional
    public void setStockBack() {
        System.out.println("回滚开始。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。");

        Example example = new Example(StockBack.class);
        example.createCriteria().andEqualTo("status", "0");
        List<StockBack> stockBacks = stockBackMapper.selectByExample(example);

        for (StockBack stockBack : stockBacks) {
//            回滚库存
            skuMapper.reduceStockNum(stockBack.getSkuId(), -stockBack.getNum());
//            回滚销量
            skuMapper.addSaleNum(stockBack.getSkuId(), -stockBack.getNum());

            stockBack.setBackTime(new Date());
            stockBack.setStatus("1");
            stockBackMapper.updateByExample(stockBack, example);
            System.out.println("回滚over、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、");
        }
        System.out.println("4444444444444444444444444444444444444444444444444444");
    }
}