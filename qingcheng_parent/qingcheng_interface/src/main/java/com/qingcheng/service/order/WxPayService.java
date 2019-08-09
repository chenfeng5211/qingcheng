/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: WxPayService
 * Author:   chenf
 * Date:     2019/8/8 0008 16:29
 * Description: 微信支付同一下单接口
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.qingcheng.service.order;

import java.util.Map;

/**
 * 〈一句话功能简述〉<br> 
 * 〈微信支付同一下单接口〉
 *
 * @author chenf
 * @create 2019/8/8 0008
 * @since 1.0.0
 */
public interface WxPayService {


    /**
     * 功能描述:
     *  订单号
     *  支付金额
     *  回调地址（工程url）
     *  返回订单好和支付金额map集合
     */

    public Map<String, String> createNative(String orderId, Integer payMoney, String notifyUrl);


    /**
     * 功能描述:
     *
     * 回调解析
     */

    public void notifyLogic(String returnStr);


    /**
     * 功能描述:
     *
     * 查询订单
     */

    public Map<String, String> findOrder(String orderId);


    /**
     * 功能描述:
     * 关闭订单
     */

    public Map<String, String> closeOrder(String orderId);
}