/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: GoodsTask
 * Author:   chenf
 * Date:     2019/7/30 0030 15:57
 * Description: 品牌，规格，分类定时添加到缓存中
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.controller.goods;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.service.goods.BrandService;
import com.qingcheng.service.goods.SpecService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 〈品牌，规格，分类定时添加到缓存中〉
 *
 * @author chenf
 * @create 2019/7/30 0030
 * @since 1.0.0
 */
@Component
public class GoodsTask {

    @Reference
    private BrandService brandService;

    @Reference
    private SpecService specService;


    @Scheduled(cron = "0 0 1 * * ?")
    public void saveToRedis(){
        brandService.saveBrandByCategoryToRedis();
        specService.saveSpecByCategoryToRedis();
    }

}