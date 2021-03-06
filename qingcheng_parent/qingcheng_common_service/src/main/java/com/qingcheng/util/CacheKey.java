/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: CacheKey
 * Author:   chenf
 * Date:     2019/7/26 0026 19:14
 * Description: 枚举类
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.util;

/**
 * 〈枚举类,管理所有缓存的key〉
 *
 * @author chenf
 * @create 2019/7/26 0026
 * @since 1.0.0
 */
public enum  CacheKey {

//    广告
    AD,
//    种类
    CATEGORY_TREE,
//    sku商品价格
    SKU_PRICE,
//    品牌
    CATEGORY_BRAND,
//    规格
    CATEGORY_SPEC,
//    rabbitMQ,验证码，消息通知，群发等
    MQ,
//    购物车
    CART,
//    分类直接存入到缓存
    CATEGORY,
}