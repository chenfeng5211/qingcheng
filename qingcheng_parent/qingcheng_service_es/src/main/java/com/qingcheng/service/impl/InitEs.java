/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: Init
 * Author:   chenf
 * Date:     2019/7/28 0028 20:57
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.service.goods.SkuService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * 〈初始或sku到elasticSearch〉
 *
 * @author chenf
 * @create 2019/7/28 0028
 * @since 1.0.0
 */
@Component
public class InitEs implements InitializingBean {

    @Reference
    private SkuService skuService;

    public void afterPropertiesSet() throws Exception {

        System.out.println("ddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd");
        skuService.findAllToElasticSearch();
    }
}