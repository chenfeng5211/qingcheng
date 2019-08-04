/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: EsDownMessageConsumer
 * Author:   chenf
 * Date:     2019/8/2 0002 13:10
 * Description: 索引库删除商品
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.service.impl;

import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Map;

/**
 * 〈索引库删除商品〉
 *
 * @author chenf
 * @create 2019/8/2 0002
 * @since 1.0.0
 */

public class EsDownMessageConsumer implements MessageListener {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public void onMessage(Message message) {
//        1.从队列中获取skuId
        String jsonSku = new String(message.getBody());
        Map<String, String> skuMap = JSON.parseObject(jsonSku, Map.class);
        String skuId = skuMap.get("skuId");

//        2.删除索引库相关skuId商品
//        封装查询请求
//        HttpHost http = new HttpHost("127.0.0.1", 9200, "http");
//        RestClientBuilder builder = RestClient.builder(http);//rest构建器
//        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(builder);

        try {
            DeleteRequest deleteRequest = new DeleteRequest("sku", "doc", skuId);
            DeleteResponse response = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
            int status = response.status().getStatus();
            System.out.println(status);
            System.out.println("rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}