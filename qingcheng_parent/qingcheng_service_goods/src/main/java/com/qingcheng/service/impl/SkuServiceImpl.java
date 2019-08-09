package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qingcheng.dao.SkuMapper;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.goods.Sku;
import com.qingcheng.service.goods.SkuService;
import com.qingcheng.util.CacheKey;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = SkuService.class)
public class SkuServiceImpl implements SkuService {

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 返回全部记录
     * @return
     */
    public List<Sku> findAll() {
        return skuMapper.selectAll();
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Sku> findPage(int page, int size) {
        PageHelper.startPage(page,size);
        Page<Sku> skus = (Page<Sku>) skuMapper.selectAll();
        return new PageResult<Sku>(skus.getTotal(),skus.getResult());
    }

    /**
     * 条件查询
     * @param searchMap 查询条件
     * @return
     */
    public List<Sku> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return skuMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Sku> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        Page<Sku> skus = (Page<Sku>) skuMapper.selectByExample(example);
        return new PageResult<Sku>(skus.getTotal(),skus.getResult());
    }

    /**
     * 根据Id查询
     * @param id
     * @return
     */
    public Sku findById(String id) {
        return skuMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     * @param sku
     */
    public void add(Sku sku) {
        skuMapper.insert(sku);
    }

    /**
     * 修改
     * @param sku
     */
    public void update(Sku sku) {
        skuMapper.updateByPrimaryKeySelective(sku);
    }

    /**
     *  删除同步缓存
     * @param id
     */
    public void delete(String id) {
        skuMapper.deleteByPrimaryKey(id);
        redisTemplate.boundHashOps(CacheKey.SKU_PRICE).delete(id);
    }

    /**
     * 将某个商品的所有价格添加到redis中
     */
    public void savePriceToRedis() {

        if(!redisTemplate.hasKey(CacheKey.SKU_PRICE)){
        List<Sku> skus = skuMapper.selectAll();
            System.out.println("//////////////////////////////////////////////////////");
        for (Sku sku : skus) {
                if ("1".equals(sku.getStatus())) {
                    redisTemplate.boundHashOps(CacheKey.SKU_PRICE).put(sku.getId(), sku.getPrice());
                }
            }
        }
    }


    /**
     * 功能描述:
     * 从查询缓存中查询价格
     */

    public Integer findPriceByRedis(String id) {

        return (Integer) redisTemplate.boundHashOps(CacheKey.SKU_PRICE).get(id);
    }


    /**
     * 功能描述:
     *
     * 根据id更新修改的价格
     */

    public void editPriceToRedis(String id, Integer price) {
        redisTemplate.boundHashOps(CacheKey.SKU_PRICE).put(id,price);
    }


    /**
     * 功能描述:
     * 删除缓存中的价格
     */

    public void deletePriceToRedis(String id) {
        redisTemplate.boundHashOps(CacheKey.SKU_PRICE).delete(id);
    }


    /**
     * 功能描述:
     *
     * 查询所有的sku商品并存入到到ElasticSearch
     */

    public void findAllToElasticSearch() throws IOException {

//        获取所有的数据并分页
        PageHelper.startPage(1,5000);
        Page<Sku> skus = (Page<Sku>) skuMapper.selectAll();
        int totalPage = skus.getPages();
        System.out.println(totalPage);
        System.out.println(skus.getTotal());

//        连接rest接口
        HttpHost http=new HttpHost("127.0.0.1",9200,"http");
        RestClientBuilder builder= RestClient.builder(http);//rest构建器
        RestHighLevelClient restHighLevelClient=new RestHighLevelClient(builder);//高级客户端对象

//        封装请求对象
        BulkRequest bulkRequest = new BulkRequest();

        for (int i = 1; i <= totalPage; i++) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            PageHelper.startPage(i,5000);
            Page<Sku> skuPage = (Page<Sku>) skuMapper.selectAll();
            List<Sku> skuList = skuPage.getResult();
            for (Sku sku : skuList) {
                IndexRequest indexRequest=new IndexRequest("sku","doc",sku.getId());
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("name", sku.getName());
                map.put("price", sku.getPrice());
                map.put("image", sku.getImage());
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

                indexRequest.source(map);
                bulkRequest.add(indexRequest);
            }
            BulkResponse response = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            int status = response.status().getStatus();
            System.out.println(status + i);

        }

//        获取响应结果
        try {
            BulkResponse response = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            int status = response.status().getStatus();
            System.out.println(status);


            String message = response.buildFailureMessage();
            System.out.println(message);
            restHighLevelClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 功能描述:
     *
     * 根据id和售卖num更新销售数量和库存量
     */

    @Transactional
    public void updateStockNumSaleNum(String skuId, Integer num) {

        Sku sku = skuMapper.selectByPrimaryKey(skuId);
        if(sku==null){
            throw new RuntimeException("无此商品");
        }
        if(num > sku.getNum()){
            throw new RuntimeException("库存不足");
        }
        if(!"1".equals(sku.getStatus())){
            throw new RuntimeException("商品已下架");
        }

        skuMapper.reduceStockNum(skuId, num);
        skuMapper.addSaleNum(skuId, num);

    }

    /**
     * 构建查询条件
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 商品id
            if(searchMap.get("id")!=null && !"".equals(searchMap.get("id"))){
                criteria.andLike("id","%"+searchMap.get("id")+"%");
            }
            // 商品条码
            if(searchMap.get("sn")!=null && !"".equals(searchMap.get("sn"))){
                criteria.andLike("sn","%"+searchMap.get("sn")+"%");
            }
            // SKU名称
            if(searchMap.get("name")!=null && !"".equals(searchMap.get("name"))){
                criteria.andLike("name","%"+searchMap.get("name")+"%");
            }
            // 商品图片
            if(searchMap.get("image")!=null && !"".equals(searchMap.get("image"))){
                criteria.andLike("image","%"+searchMap.get("image")+"%");
            }
            // 商品图片列表
            if(searchMap.get("images")!=null && !"".equals(searchMap.get("images"))){
                criteria.andLike("images","%"+searchMap.get("images")+"%");
            }
            // SPUID
            if(searchMap.get("spuId")!=null && !"".equals(searchMap.get("spuId"))){
                criteria.andLike("spuId","%"+searchMap.get("spuId")+"%");
            }
            // 类目名称
            if(searchMap.get("categoryName")!=null && !"".equals(searchMap.get("categoryName"))){
                criteria.andLike("categoryName","%"+searchMap.get("categoryName")+"%");
            }
            // 品牌名称
            if(searchMap.get("brandName")!=null && !"".equals(searchMap.get("brandName"))){
                criteria.andLike("brandName","%"+searchMap.get("brandName")+"%");
            }
            // 规格
            if(searchMap.get("spec")!=null && !"".equals(searchMap.get("spec"))){
                criteria.andLike("spec","%"+searchMap.get("spec")+"%");
            }
            // 商品状态 1-正常，2-下架，3-删除
            if(searchMap.get("status")!=null && !"".equals(searchMap.get("status"))){
                criteria.andLike("status","%"+searchMap.get("status")+"%");
            }

            // 价格（分）
            if(searchMap.get("price")!=null ){
                criteria.andEqualTo("price",searchMap.get("price"));
            }
            // 库存数量
            if(searchMap.get("num")!=null ){
                criteria.andEqualTo("num",searchMap.get("num"));
            }
            // 库存预警数量
            if(searchMap.get("alertNum")!=null ){
                criteria.andEqualTo("alertNum",searchMap.get("alertNum"));
            }
            // 重量（克）
            if(searchMap.get("weight")!=null ){
                criteria.andEqualTo("weight",searchMap.get("weight"));
            }
            // 类目ID
            if(searchMap.get("categoryId")!=null ){
                criteria.andEqualTo("categoryId",searchMap.get("categoryId"));
            }
            // 销量
            if(searchMap.get("saleNum")!=null ){
                criteria.andEqualTo("saleNum",searchMap.get("saleNum"));
            }
            // 评论数
            if(searchMap.get("commentNum")!=null ){
                criteria.andEqualTo("commentNum",searchMap.get("commentNum"));
            }

        }
        return example;
    }

}
