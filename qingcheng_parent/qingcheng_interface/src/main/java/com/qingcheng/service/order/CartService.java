/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: CartService
 * Author:   chenf
 * Date:     2019/8/6 0006 19:37
 * Description: 购物车
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.qingcheng.service.order;

import java.util.List;
import java.util.Map;

/**
 * 〈一句话功能简述〉<br> 
 * 〈购物车〉
 *
 * @author chenf
 * @create 2019/8/6 0006
 * @since 1.0.0
 */
public interface CartService {

    public List<Map<String, Object>> findCart(String username);

    public void addItemToCart(String username, String skuId, Integer num);

    public void updateChecked(String username, String skuId, boolean checked);

    public void deleteChecked(String username);

    public Integer getPreMoney(String username);


    /**
     * 功能描述:
     *
     * 在订单页展示从购物车跳转的订单
     *
     */

    public List<Map<String, Object>> findPayOrderItem(String username);
}