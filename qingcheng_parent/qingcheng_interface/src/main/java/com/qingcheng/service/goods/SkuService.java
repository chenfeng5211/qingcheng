package com.qingcheng.service.goods;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.goods.Sku;

import java.io.IOException;
import java.util.*;

/**
 * sku业务逻辑层
 */
public interface SkuService {


    public List<Sku> findAll();


    public PageResult<Sku> findPage(int page, int size);


    public List<Sku> findList(Map<String,Object> searchMap);


    public PageResult<Sku> findPage(Map<String,Object> searchMap,int page, int size);


    public Sku findById(String id);

    public void add(Sku sku);


    public void update(Sku sku);


    public void delete(String id);

    public void savePriceToRedis();

    public Integer  findPriceByRedis(String id);

    public void editPriceToRedis(String id, Integer price);

    public void deletePriceToRedis(String id);

    public void findAllToElasticSearch() throws IOException;


    /**
     * 功能描述:
     *
     * 根据销售数量和商品skuid
     * 更新sku销量和库存信息
     */

    public void updateStockNumSaleNum(String skuId, Integer num);

}
