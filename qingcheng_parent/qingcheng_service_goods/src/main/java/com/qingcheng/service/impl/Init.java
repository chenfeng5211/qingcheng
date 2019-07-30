/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: Init
 * Author:   chenf
 * Date:     2019/7/26 0026 19:51
 * Description: 服务器启动时就将数据库中的商品种类加入到缓存中
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.service.impl;

import com.qingcheng.service.goods.BrandService;
import com.qingcheng.service.goods.CategoryService;
import com.qingcheng.service.goods.SkuService;
import com.qingcheng.service.goods.SpecService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 〈服务器启动时就将数据库中的商品种类加入到缓存中〉
 *
 * @author chenf
 * @create 2019/7/26 0026
 * @since 1.0.0
 */
@Component//注解别忘了加，交由spring容器管理
public class Init implements InitializingBean {


    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private SpecService specService;

    @Autowired
    private SkuService skuService;

    public void afterPropertiesSet() throws Exception {

        System.out.println("缓存来了");
        categoryService.saveCategoryToRedis();
        skuService.savePriceToRedis();
        brandService.saveBrandByCategoryToRedis();
        specService.saveSpecByCategoryToRedis();
        System.out.println("缓存结束");
    }
}