/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: StockBackService
 * Author:   chenf
 * Date:     2019/8/7 0007 17:56
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.qingcheng.service.goods;

import com.qingcheng.pojo.order.OrderItem;

import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈订单生成失败，库存回滚〉
 *
 * @author chenf
 * @create 2019/8/7 0007
 * @since 1.0.0
 */
public interface StockBackService {


    /**
     * 功能描述:
     *
     * 订单生成失败保存失败订单到库存回滚表
     */

    public void addStockBack(List<OrderItem> orderItemList);


    /**
     * 功能描述:
     *
     * 执行回滚方法
     */

    public void setStockBack();

}