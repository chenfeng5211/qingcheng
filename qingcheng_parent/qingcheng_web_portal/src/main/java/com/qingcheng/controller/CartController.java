/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: CartController
 * Author:   chenf
 * Date:     2019/8/4 0004 19:00
 * Description: 购物车
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.entity.Result;
import com.qingcheng.pojo.user.Address;
import com.qingcheng.service.order.CartService;
import com.qingcheng.service.user.AddressService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 〈购物车〉
 *
 * @author chenf
 * @create 2019/8/4 0004
 * @since 1.0.0
 */
@RestController
@RequestMapping("/cart")

public class CartController {

    @Reference
    private CartService cartService;

    @Reference
    private AddressService addressService;


    /**
     * 功能描述:
     *
     * 展示购物车
     */

    @RequestMapping("/findCart")
    public List<Map<String, Object>> findCart(){

//        获取当前登录的用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();


        return cartService.findCart(username);
    }

    /**
     * 购物车商品添加，删减
     * @param skuId
     * @param num
     * @return
     */

    @RequestMapping("/addItem")
    public Result addItem(String skuId, int num){

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        cartService.addItemToCart(username, skuId, num);
        return new Result();
    }


    /**
     * 功能描述:
     * 商品详情跳转购物车
     * @Date: 2019/8/5 0005 1:09
     */

    @RequestMapping("/toCart")
    public void toCart(HttpServletResponse response, String skuId, Integer num) throws IOException {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        cartService.addItemToCart(username, skuId, num);


        response.sendRedirect("/cart.html");
    }


    /**
     * 功能描述:
     *
     * 商品复选框状态
     */

    @RequestMapping("/updateChecked")
    public Result updateChecked(String skuId, boolean checked){

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        cartService.updateChecked(username, skuId, checked);

        return new Result();
    }


    /**
     * 功能描述:
     * 删除所有商品
     */

    @RequestMapping("/deleteChecked")
    public Result deleteChecked(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        cartService.deleteChecked(username);

        return new Result();
    }

    /**
     *  获取优惠金额
     * @return
     */
    @RequestMapping("/preMoney")
    public Map getPreMoney(){

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Integer preMoney = cartService.getPreMoney(username);

        Map<String, Integer> map = new HashMap<>();

        map.put("preMoney", preMoney);
        return map;
    }


    /**
     * 功能描述:
     * 订单页面展示选中购物车商品
     */

    @RequestMapping("/findOrderItem")
    public List<Map<String, Object>> findOrderItem(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();


        return cartService.findPayOrderItem(username);
    }


    /**
     * 功能描述:
     *
     * 根据当前的登录用户名查找地址
     */

    @RequestMapping("/findByUsername")
    public List<Address> findByUsername(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return addressService.findByUsername(username);
    }

}