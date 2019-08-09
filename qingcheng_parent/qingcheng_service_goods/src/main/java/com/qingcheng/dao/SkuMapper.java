package com.qingcheng.dao;

import com.qingcheng.pojo.goods.Sku;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

public interface SkuMapper extends Mapper<Sku> {


    /**
     * 功能描述:
     *
     * 根据id更新商品库存
     */

    @Update("update tb_sku ts set ts.num=ts.num-#{num}  where id=#{id} ;")
    public void reduceStockNum(@Param("id") String skuId, @Param("num") Integer num);


    /**
     * 功能描述:
     *
     * 根据id更新销售量
     */

    @Update("update tb_sku ts set ts.sale_num=ts.sale_num+#{num}  where id=#{id} ;")
    public void addSaleNum(@Param("id") String skuId, @Param("num") Integer num);
}
