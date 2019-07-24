/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: Goods
 * Author:   chenf
 * Date:     2019/7/16 0016 7:34
 * Description: 商品spu，sku
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.pojo.goods;

import java.io.Serializable;
import java.util.List;

/**
 * 〈商品spu，sku〉
 *
 * @author chenf
 * @create 2019/7/16 0016
 * @since 1.0.0
 */
public class Goods implements Serializable {

//    单个商品的spu对象
    private Spu spu;

//    每个spu对应的多个sku
    private List<Sku> skuList;

    public Spu getSpu() {
        return spu;
    }

    public void setSpu(Spu spu) {
        this.spu = spu;
    }

    public List<Sku> getSkuList() {
        return skuList;
    }

    public void setSkuList(List<Sku> skuList) {
        this.skuList = skuList;
    }
}