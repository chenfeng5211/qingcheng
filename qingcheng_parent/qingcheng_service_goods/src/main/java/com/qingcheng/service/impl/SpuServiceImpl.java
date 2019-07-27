package com.qingcheng.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qingcheng.dao.*;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.goods.*;
import com.qingcheng.service.goods.SkuService;
import com.qingcheng.service.goods.SpuService;
import com.qingcheng.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = SpuService.class)
public class SpuServiceImpl implements SpuService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryBrandMapper categoryBrandMapper;

    @Autowired
    private GoodsAuditLogMapper goodsAuditLogMapper;

    @Autowired
    private GoodsOperateLogMapper goodsOperateLogMapper;

    @Autowired
    private GoodRecyledMapper goodRecyledMapper;

    @Autowired
    private SkuService skuService;
    /**
     * 返回全部记录
     * @return
     */
    public List<Spu> findAll() {
        return spuMapper.selectAll();
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Spu> findPage(int page, int size) {
        PageHelper.startPage(page,size);
        Page<Spu> spus = (Page<Spu>) spuMapper.selectAll();
        return new PageResult<Spu>(spus.getTotal(),spus.getResult());
    }

    /**
     * 条件查询
     * @param searchMap 查询条件
     * @return
     */
    public List<Spu> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return spuMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Spu> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        Page<Spu> spus = (Page<Spu>) spuMapper.selectByExample(example);
        return new PageResult<Spu>(spus.getTotal(),spus.getResult());
    }

    /**
     * 根据Id查询
     * @param id
     * @return
     */
    public Spu findById(String id) {
        return spuMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     * @param spu
     */
    public void add(Spu spu) {
        spuMapper.insert(spu);
    }

    /**
     * 修改
     * @param spu
     */
    public void update(Spu spu) {
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     *  删除
     * @param id
     */
    public void delete(String id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if(!"1".equals(spu.getIsDelete())) {
            throw new RuntimeException("不是逻辑删除，无法真正删除");
        }
        spuMapper.deleteByPrimaryKey(id);
    }


    /**
     * 保存商品spu和scu
     *
     */

    @Transactional
    public void saveGoods(Goods goods) {

        Date date = new Date();
//        获取分类对象
        Spu spu = goods.getSpu();
//        判断spuId是否为空，进而判断执行的是修改还是添加方法
        if(spu.getId()!=null){
            spuMapper.updateByPrimaryKeySelective(spu);

            Example example = new Example(Sku.class);
            example.createCriteria().orEqualTo("spuId", spu.getId());

            skuMapper.deleteByExample(example);
        }else {
//        设置雪花分布式id并转化成字符串
            spu.setId(idWorker.nextId() + "");
//        保存spu
            spuMapper.insertSelective(spu);
        }
//        获取种类对象，用于获取种类名称
        Category category = categoryMapper.selectByPrimaryKey(spu.getCategory3Id());

//        循环插入sku
        for (Sku sku : goods.getSkuList()) {
//            新增
            if(sku.getId() == null) {
//            设置雪花分布式id并转化成字符串
                sku.setId(idWorker.nextId() + "");
                sku.setCreateTime(date);//创建时间
            }
            sku.setSpuId(spu.getId());
//            拼接spu名称和规格参数
            StringBuilder name = new StringBuilder(spu.getName());

//            未启用规格处理
            if(StringUtils.isEmpty(sku.getSpec())){
                sku.setSpec("{}");
            }
//            将规格参数转换成json格式并存入到map集合中
            Map<String, String> skuMap = JSON.parseObject(sku.getSpec(), Map.class);

//            遍历map，将value转换成空格拼接
            for (String value : skuMap.values()) {
                name.append(" ").append(value);
            }
            sku.setName(name.toString());//sku名称
            sku.setCategoryId(spu.getCategory3Id());//分级id
            sku.setCategoryName(category.getName());//种类名称

            sku.setUpdateTime(date);//修改时间
            sku.setCommentNum(0);//评论数据
            sku.setSaleNum(0);//销售数量


            skuMapper.insertSelective(sku);

//            向缓存中更新数据库
            skuService.editPriceToRedis(sku.getId(), sku.getPrice());

        }

//        建立分类与品牌关系
        CategoryBrand categoryBrand = new CategoryBrand();
        categoryBrand.setCategoryId(spu.getCategory3Id());
        categoryBrand.setBrandId(spu.getBrandId());
//        判断是否已经有值
        int count = categoryBrandMapper.selectCount(categoryBrand);
        if(count == 0) {
            categoryBrandMapper.insertSelective(categoryBrand);
        }


    }


//    修改回显
    public Goods findGoodsById(String id) {
//        查询spu
        Spu spu = spuMapper.selectByPrimaryKey(id);

//        查询sku
        Example example = new Example(Sku.class);
        example.createCriteria().orEqualTo("spuId", id);
        List<Sku> skuList = skuMapper.selectByExample(example);

//        封装到goods
        Goods goods = new Goods();
        goods.setSpu(spu);
        goods.setSkuList(skuList);

        return goods;
    }

    @Transactional
    public void audit(String id, String status, String message, String auditId, String operateLogId) {
        Date date = new Date();
//        修改审核状态和上架状态
        Spu spu = new Spu();
        spu.setId(id);
        spu.setStatus(status);

        if("1".equals(status)){
            spu.setIsMarketable("1");
        }
        Spu spu1 = spuMapper.selectByPrimaryKey(id);
        GoodsOperateLog goodsOperateLog = new GoodsOperateLog();
        goodsOperateLog.setIsDeleteOld(spu1.getIsDelete());
        goodsOperateLog.setIsMarketableOld(spu1.getIsMarketable());
        goodsOperateLog.setIsStatusOld(spu1.getStatus());
        int i = spuMapper.updateByPrimaryKeySelective(spu);



//        记录商品审核记录
        GoodsAuditLog goodsAuditLog = new GoodsAuditLog();
        goodsAuditLog.setId(auditId);
        goodsAuditLog.setAuditMessage(message);
        goodsAuditLog.setAuditTime(date);
        goodsAuditLog.setOperateLogId(operateLogId);
        goodsAuditLog.setSpuId(id);
        goodsAuditLogMapper.insertSelective(goodsAuditLog);
//        记录商品日志
        goodsOperateLog.setAuditId(auditId);
        goodsOperateLog.setId(operateLogId);
        goodsOperateLog.setIsDeleteNow(spu.getIsDelete());
        goodsOperateLog.setIsMarketableNow(spu.getIsMarketable());
        goodsOperateLog.setIsStatusNow(spu.getStatus());
        goodsOperateLog.setOperateTime(date);
        goodsOperateLog.setSpuId(id);
        goodsOperateLogMapper.insertSelective(goodsOperateLog);
    }


    /**
     * 功能描述:
     * 修改商品下架，上架修改为下架，下架不修改
     * @Date: 2019/7/17 0017 16:43
     */

    public void pull(String id) {
        Date date = new Date();
        Spu spu = new Spu();
        spu.setId(id);
        spu.setIsMarketable("0");
        Spu spu1 = spuMapper.selectByPrimaryKey(id);
        GoodsOperateLog goodsOperateLog = new GoodsOperateLog();
        goodsOperateLog.setIsDeleteOld(spu1.getIsDelete());
        goodsOperateLog.setIsMarketableOld(spu1.getIsMarketable());
        goodsOperateLog.setIsStatusOld(spu1.getStatus());


        goodsOperateLog.setId(idWorker.nextId() + "");
        goodsOperateLog.setIsDeleteNow(spu.getIsDelete());
        goodsOperateLog.setIsMarketableNow(spu.getIsMarketable());
        goodsOperateLog.setIsStatusNow(spu.getStatus());
        goodsOperateLog.setOperateTime(date);
        goodsOperateLog.setSpuId(id);
        goodsOperateLogMapper.insertSelective(goodsOperateLog);
        spuMapper.updateByPrimaryKeySelective(spu);
    }


    /**
     * 功能描述:
     * 批量下架
     */

    public int pullMany(String[] ids) {

        Spu spu = new Spu();
        spu.setIsMarketable("0");

        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", Arrays.asList(ids));
        criteria.andEqualTo("status", "0");
        criteria.andEqualTo("status", "2");
        criteria.andEqualTo("isMarketable", "1");

        int i = spuMapper.updateByExampleSelective(spu, example);

        return 0;
    }


    /**
     * 功能描述:
     * 修改商品上架
     */

    public void put(String id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if(!"1".equals(spu.getStatus())){
            throw new RuntimeException("审核未通过");
        }
        spu.setIsMarketable("1");
        spuMapper.updateByPrimaryKeySelective(spu);

    }


    /**
     * 功能描述:
     *
     * 批量上架
     */

    public int putMany(String[] ids) {

        Spu spu = new Spu();
        spu.setIsMarketable("1");

        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
//        sql语句中in条件
        criteria.andIn("id", Arrays.asList(ids));
//        未上架条件
        criteria.andEqualTo("isMarketable", "0");
//        审核通过条件
        criteria.andEqualTo("status", "1");


        int row = spuMapper.updateByExampleSelective(spu, example);
        return row;
    }


    /**
     * 功能描述:
     * 逻辑删除
     */

    @Transactional
    public void deleteById(String id) {
//        Spu spu = new Spu();
//        spu.setIsDelete("1");
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if(spu == null){
            throw  new  RuntimeException("删除的商品不存在");
        }
        Example example = new Example(Sku.class);
        example.createCriteria().andEqualTo("spuId", id);
        List<Sku> skus = skuMapper.selectByExample(example);
        spu.setIsDelete("1");
        spuMapper.updateByPrimaryKeySelective(spu);


        for (Sku sku : skus) {
            GoodRecyled goodRecyled = new GoodRecyled();
            goodRecyled.setGoodCategory(spu.getCategory3Id());
            goodRecyled.setGoodName(spu.getName());
            goodRecyled.setId(idWorker.nextId() + "");
            goodRecyled.setImage(spu.getImage());
            goodRecyled.setPrice(sku.getPrice());
            goodRecyled.setSaleNum(spu.getSaleNum());
            goodRecyled.setSpuId(id);
            goodRecyledMapper.insertSelective(goodRecyled);
            sku.setStatus("0");
            skuMapper.updateByPrimaryKeySelective(sku);
            skuService.deletePriceToRedis(sku.getId());
        }


    }


    /**
     * 功能描述:
     * 还原删除
     */
    @Transactional
    public void restore(String id) {
//        Spu spu = new Spu();
//        spu.setIsDelete("0");
//        修改spu是否删除状态
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if(spu == null){
            throw new RuntimeException("查找的商品不存在");
        }
        spu.setIsDelete("0");
        spuMapper.updateByPrimaryKeySelective(spu);

//        删除回收站商品
        GoodRecyled goodRecyled = new GoodRecyled();
        goodRecyled.setSpuId(id);
        goodRecyledMapper.deleteByExample(goodRecyled);

    }


    /**
     * 构建查询条件
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 主键
            if(searchMap.get("id")!=null && !"".equals(searchMap.get("id"))){
                criteria.andLike("id","%"+searchMap.get("id")+"%");
            }
            // 货号
            if(searchMap.get("sn")!=null && !"".equals(searchMap.get("sn"))){
                criteria.andLike("sn","%"+searchMap.get("sn")+"%");
            }
            // SPU名
            if(searchMap.get("name")!=null && !"".equals(searchMap.get("name"))){
                criteria.andLike("name","%"+searchMap.get("name")+"%");
            }
            // 副标题
            if(searchMap.get("caption")!=null && !"".equals(searchMap.get("caption"))){
                criteria.andLike("caption","%"+searchMap.get("caption")+"%");
            }
            // 图片
            if(searchMap.get("image")!=null && !"".equals(searchMap.get("image"))){
                criteria.andLike("image","%"+searchMap.get("image")+"%");
            }
            // 图片列表
            if(searchMap.get("images")!=null && !"".equals(searchMap.get("images"))){
                criteria.andLike("images","%"+searchMap.get("images")+"%");
            }
            // 售后服务
            if(searchMap.get("saleService")!=null && !"".equals(searchMap.get("saleService"))){
                criteria.andLike("saleService","%"+searchMap.get("saleService")+"%");
            }
            // 介绍
            if(searchMap.get("introduction")!=null && !"".equals(searchMap.get("introduction"))){
                criteria.andLike("introduction","%"+searchMap.get("introduction")+"%");
            }
            // 规格列表
            if(searchMap.get("specItems")!=null && !"".equals(searchMap.get("specItems"))){
                criteria.andLike("specItems","%"+searchMap.get("specItems")+"%");
            }
            // 参数列表
            if(searchMap.get("paraItems")!=null && !"".equals(searchMap.get("paraItems"))){
                criteria.andLike("paraItems","%"+searchMap.get("paraItems")+"%");
            }
            // 是否上架
            if(searchMap.get("isMarketable")!=null && !"".equals(searchMap.get("isMarketable"))){
                criteria.andLike("isMarketable","%"+searchMap.get("isMarketable")+"%");
            }
            // 是否启用规格
            if(searchMap.get("isEnableSpec")!=null && !"".equals(searchMap.get("isEnableSpec"))){
                criteria.andLike("isEnableSpec","%"+searchMap.get("isEnableSpec")+"%");
            }
            // 是否删除
            if(searchMap.get("isDelete")!=null && !"".equals(searchMap.get("isDelete"))){
                criteria.andLike("isDelete","%"+searchMap.get("isDelete")+"%");
            }
            // 审核状态
            if(searchMap.get("status")!=null && !"".equals(searchMap.get("status"))){
                criteria.andLike("status","%"+searchMap.get("status")+"%");
            }

            // 品牌ID
            if(searchMap.get("brandId")!=null ){
                criteria.andEqualTo("brandId",searchMap.get("brandId"));
            }
            // 一级分类
            if(searchMap.get("category1Id")!=null ){
                criteria.andEqualTo("category1Id",searchMap.get("category1Id"));
            }
            // 二级分类
            if(searchMap.get("category2Id")!=null ){
                criteria.andEqualTo("category2Id",searchMap.get("category2Id"));
            }
            // 三级分类
            if(searchMap.get("category3Id")!=null ){
                criteria.andEqualTo("category3Id",searchMap.get("category3Id"));
            }
            // 模板ID
            if(searchMap.get("templateId")!=null ){
                criteria.andEqualTo("templateId",searchMap.get("templateId"));
            }
            // 运费模板id
            if(searchMap.get("freightId")!=null ){
                criteria.andEqualTo("freightId",searchMap.get("freightId"));
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
