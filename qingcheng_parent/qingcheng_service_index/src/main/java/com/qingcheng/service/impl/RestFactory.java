/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: RestFactoryBean
 * Author:   chenf
 * Date:     2019/7/29 0029 18:34
 * Description: 建立工厂类，封装rest高级客户端
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.service.impl;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * 〈建立工厂类，封装rest高级客户端〉
 *
 * @author chenf
 * @create 2019/7/29 0029
 * @since 1.0.0
 */
public class RestFactory {


    public static RestHighLevelClient getHighLevelClient(String hostName, Integer port, String scheme) {
        HttpHost http = new HttpHost(hostName, port, scheme);
        RestClientBuilder builder = RestClient.builder(http);//rest构建器
        return new RestHighLevelClient(builder);//高级客户端对象
    }
}