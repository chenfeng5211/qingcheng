/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: WxPayServiceImpl
 * Author:   chenf
 * Date:     2019/8/8 0008 16:44
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.Config;
import com.github.wxpay.sdk.WXPayRequest;
import com.github.wxpay.sdk.WXPayUtil;
import com.qingcheng.service.order.OrderService;
import com.qingcheng.service.order.WxPayService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * 〈〉
 *
 * @author chenf
 * @create 2019/8/8 0008
 * @since 1.0.0
 */
@Service
public class WxPayServiceImpl implements WxPayService {

    @Autowired
    private Config config;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public Map<String, String> createNative(String orderId, Integer payMoney, String notifyUrl) {

//        1.封装参数
        Map<String, String> wxMap = new HashMap<>();

        wxMap.put("appid", config.getAppID());
        wxMap.put("mch_id", config.getMchID());
        wxMap.put("nonce_str", WXPayUtil.generateNonceStr());
        wxMap.put("body", "随便哈");
        wxMap.put("out_trade_no", orderId);
        wxMap.put("total_fee", payMoney + "");
        wxMap.put("spbill_create_ip", "127.0.0.1");
        wxMap.put("notify_url", notifyUrl);
        wxMap.put("trade_type", "NATIVE");

        try {
//            map转xml并封装签名
            String generateSignedXml = WXPayUtil.generateSignedXml(wxMap, config.getKey());
//        2.发送请求
            WXPayRequest wxPayRequest = new WXPayRequest(config);
            String xmlRequest = wxPayRequest.requestWithCert("/pay/unifiedorder", null, generateSignedXml, false);
            System.out.println(xmlRequest);
//        3.封装结果
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xmlRequest);

            Map<String, String> returnMap = new HashMap<String, String>();
            returnMap.put("out_trade_no", orderId);//订单号
            returnMap.put("code_url", resultMap.get("code_url"));//二维码地址
            returnMap.put("total_fee", payMoney + "");//交易金额
            return returnMap;

        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<String, String>();
        }
    }

    @Override
    public void notifyLogic(String returnStr) {

//        解析支付结果并验证
        try {
            Map<String, String> resultMap = WXPayUtil.xmlToMap(returnStr);

//            检验支付结果
            boolean signatureValid = WXPayUtil.isSignatureValid(resultMap, config.getKey());

            System.out.println(signatureValid);
            System.out.println(resultMap.get("result_code"));
            System.out.println(resultMap.get("transaction_id"));
            if (signatureValid && "SUCCESS".equals(resultMap.get("result_code"))) {
//              更新订单状态
                orderService.updatePayedStatus("1", resultMap.get("out_trade_no"), resultMap.get("transaction_id"));

//            通过MQ完成客服端向服务端推送
                rabbitTemplate.convertAndSend("paynotify", "", resultMap.get("out_trade_no"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public Map<String, String> findOrder(String orderId) {

//        1.封装参数
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("appid", config.getAppID());
        queryMap.put("mch_id", config.getMchID());
        queryMap.put("out_trade_no", orderId);
        queryMap.put("nonce_str", WXPayUtil.generateNonceStr());
        try {
//            查询结果map装xml
            String queryXml = WXPayUtil.generateSignedXml(queryMap, config.getKey());
            System.out.println(queryXml);
//        2.发送请求
            WXPayRequest wxPayRequest = new WXPayRequest(config);
            String queryResult = wxPayRequest.requestWithCert("/pay/orderquery", null, queryXml, false);
            System.out.println(queryResult);

//        3.解析结果
            Map<String, String> queryResultMap = WXPayUtil.xmlToMap(queryResult);

            Map<String, String> queryReturnMap = new HashMap<>();
            queryReturnMap.put("trade_state", queryResultMap.get("trade_state"));
            queryReturnMap.put("transaction_id", queryResultMap.get("transaction_id"));
            queryReturnMap.put("out_trade_no", queryResultMap.get(orderId));
            queryReturnMap.put("result_code", queryResultMap.get("result_code"));

            return queryReturnMap;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<String, String>();
        }

    }

    @Override
    public Map<String, String> closeOrder(String orderId) {

//        1.封装参数
        Map<String, String> closeMap = new HashMap<>();
        closeMap.put("appid", config.getAppID());
        closeMap.put("mch_id", config.getMchID());
        closeMap.put("out_trade_no", orderId);
        closeMap.put("nonce_str", WXPayUtil.generateNonceStr());

        try {
//            map装xml
            String closeXml = WXPayUtil.generateSignedXml(closeMap, config.getKey());

//            2.发送请求
            WXPayRequest wxPayRequest = new WXPayRequest(config);
            String closeResult = wxPayRequest.requestWithCert("/pay/closeorder", null, closeXml, false);
            System.out.println(closeResult);

//            3.解析结果
            Map<String, String> closeResultMap = WXPayUtil.xmlToMap(closeResult);
            System.out.println(closeResultMap);

            Map<String, String> queryReturnMap = new HashMap<>();
            queryReturnMap.put("trade_state", closeResultMap.get("trade_state"));
            return queryReturnMap;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<String, String>();
        }
    }
}