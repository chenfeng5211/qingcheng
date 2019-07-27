/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: SkuPriceController
 * Author:   chenf
 * Date:     2019/7/26 0026 21:48
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.service.goods.SkuService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 〈〉
 *
 * @author chenf
 * @create 2019/7/26 0026
 * @since 1.0.0
 */
@RestController
@RequestMapping("/sku")
@CrossOrigin
public class SkuPriceController {

    @Reference
    private SkuService skuService;

    @RequestMapping("/price")
    public Integer price(String id){
        return skuService.findPriceByRedis(id);
    }
}