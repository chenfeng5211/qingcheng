/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: PayController
 * Author:   chenf
 * Date:     2019/8/8 0008 17:37
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.order.Order;
import com.qingcheng.service.order.OrderService;
import com.qingcheng.service.order.WxPayService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 〈支付接口〉
 *
 * @author chenf
 * @create 2019/8/8 0008
 * @since 1.0.0
 */
@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private OrderService orderService;

    @Reference
    private WxPayService wxPayService;

    @RequestMapping("/wxPay")
    public Map<String, String> wxPay(String orderId){

//        1.获取当前用户
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        2.判断订单是否合法
        Order order = orderService.findById(orderId);
        if(order != null && username.equals(order.getUsername()) &&
                "0".equals(order.getPayStatus()) && "0".equals(order.getIsDelete()) && "0".equals(order.getOrderStatus())){

            return wxPayService.createNative(orderId, order.getPayMoney(), "https://qingchengtest.cn1.utools.club/pay/notify.do");

        }else{
            return null;
        }


    }


    /**
     * 功能描述:
     *
     * 回调函数
     */

    @RequestMapping("/notify")
    public Map<String, String> notifyLogic(HttpServletRequest request){
        System.out.println("测试对对对                                     对对对");

        try {
//            1.获取微信支付结果的输入流
            ServletInputStream inputStream = request.getInputStream();

//            2.创建输出流
            ByteArrayOutputStream outPutStream = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            int len = 0;

//            3.循环读写流
            while ((len = inputStream.read(bytes)) != -1){
                outPutStream.write(bytes, 0, len);
            }

//            4.关流
            inputStream.close();
            outPutStream.close();

//            5.获取结果
            String returnStr = new String(outPutStream.toByteArray(), StandardCharsets.UTF_8);
            System.out.println(returnStr+".............................");
            wxPayService.notifyLogic(returnStr);
        } catch (IOException e) {
            e.printStackTrace();

        }

        return new HashMap<String, String>();
    }

}