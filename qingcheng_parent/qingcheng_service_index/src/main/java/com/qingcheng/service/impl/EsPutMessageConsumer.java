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
import com.qingcheng.pojo.goods.Sku;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 〈索引库删除商品〉
 *
 * @author chenf
 * @create 2019/8/2 0002
 * @since 1.0.0
 */
public class EsPutMessageConsumer implements MessageListener {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public void onMessage(Message message) {
        String itemJson = new String(message.getBody());
        Map itemMap = JSON.parseObject(itemJson, Map.class);

        JSON skuJson = (JSON) itemMap.get("sku");
        Sku sku = JSON.toJavaObject(skuJson, Sku.class);
        IndexRequest request = new IndexRequest("sku", "doc", sku.getId());

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", sku.getName());
        map.put("price", sku.getPrice());
        map.put("image", sku.getImage());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if(sku.getCategoryName() != null) {
            map.put("createTime", sdf.format(sku.getCreateTime()));
        }
        map.put("spuId", sku.getSpuId());
        map.put("categoryName", sku.getCategoryName());
        map.put("brandName", sku.getBrandName());
        map.put("saleNum", sku.getSaleNum());
        map.put("commentNum", sku.getCommentNum());

//                将json转换成json对象
        Map<String, Object> specMap = (Map<String, Object>) JSON.parseObject(sku.getSpec());
        map.put("spec", specMap);

        request.source(map);

//        获取响应结果
        try {
            IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);
            int status = response.status().getStatus();
            System.out.println(status);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}