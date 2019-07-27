package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qingcheng.dao.CategoryMapper;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.goods.Category;
import com.qingcheng.service.goods.CategoryService;
import com.qingcheng.util.CacheKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private RedisTemplate<CacheKey, List<Map<String, Object>>> redisTemplate;

    /**
     * 返回全部记录
     * @return
     */
    public List<Category> findAll() {
        return categoryMapper.selectAll();
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Category> findPage(int page, int size) {
        PageHelper.startPage(page,size);
        Page<Category> categorys = (Page<Category>) categoryMapper.selectAll();
        return new PageResult<Category>(categorys.getTotal(),categorys.getResult());
    }

    /**
     * 条件查询
     * @param searchMap 查询条件
     * @return
     */
    public List<Category> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return categoryMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Category> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        Page<Category> categorys = (Page<Category>) categoryMapper.selectByExample(example);
        return new PageResult<Category>(categorys.getTotal(),categorys.getResult());
    }

    /**
     * 根据Id查询
     * @param id
     * @return
     */
    public Category findById(Integer id) {
        return categoryMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     * @param category
     */
    public void add(Category category) {
        categoryMapper.insert(category);
        saveCategoryToRedis();
    }

    /**
     * 修改
     * @param category
     */
    public void update(Category category) {
        categoryMapper.updateByPrimaryKeySelective(category);
        saveCategoryToRedis();
    }

    /**
     *  删除
     * @param id
     */
    public void delete(Integer id) {

        Example example = new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.orEqualTo("parentId", id);
        int count = categoryMapper.selectCountByExample(example);
        if(count > 0) {
            throw new RuntimeException("有下级分类");
        }
        categoryMapper.deleteByPrimaryKey(id);
        saveCategoryToRedis();

    }

    /**
     * 从查询首页分类导航
     * @return
     */
    public List<Map<String, Object>> findCategoryByIsShow() {

        return redisTemplate.boundValueOps(CacheKey.CATEGORY_TREE).get();
    }


    /**
     * 功能描述:
     * 从数据查询到的商品类别同步到redis中
     */

    public void saveCategoryToRedis() {

        //        构建查询条件
        Example example = new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isShow", "1");//是否在主页显示
        example.setOrderByClause("seq");//排序
//        查询所有的种类
        List<Category> categories = categoryMapper.selectByExample(example);


        List<Map<String, Object>> categoryTree = categoryTree(categories, 0);

        redisTemplate.boundValueOps(CacheKey.CATEGORY_TREE).set(categoryTree);
    }

    /**
     * 递归遍历树形结构
     * @param categories
     * @param id
     * @return
     */
    private List<Map<String, Object>> categoryTree(List<Category> categories, Integer id){

//        定义集合存入处理了后的分类
        List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();

        for (Category category : categories) {
            Map<String, Object> categoryMap = new HashMap<String, Object>();
//            判断当前的种类的parentId与该种类的上级id是否相同
            if(category.getParentId().equals(id)) {
                categoryMap.put("name", category.getName());
//                递归遍历，获取每个种类的下级分类的子集合
                categoryMap.put("menus", categoryTree(categories, category.getId()));
                maps.add(categoryMap);
            }
        }
        return maps;
    }

    /**
     * 构建查询条件
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 分类名称
            if(searchMap.get("name")!=null && !"".equals(searchMap.get("name"))){
                criteria.andLike("name","%"+searchMap.get("name")+"%");
            }
            // 是否显示
            if(searchMap.get("isShow")!=null && !"".equals(searchMap.get("isShow"))){
                criteria.andLike("isShow","%"+searchMap.get("isShow")+"%");
            }
            // 是否导航
            if(searchMap.get("isMenu")!=null && !"".equals(searchMap.get("isMenu"))){
                criteria.andLike("isMenu","%"+searchMap.get("isMenu")+"%");
            }

            // 分类ID
            if(searchMap.get("id")!=null ){
                criteria.andEqualTo("id",searchMap.get("id"));
            }
            // 商品数量
            if(searchMap.get("goodsNum")!=null ){
                criteria.andEqualTo("goodsNum",searchMap.get("goodsNum"));
            }
            // 排序
            if(searchMap.get("seq")!=null ){
                criteria.andEqualTo("seq",searchMap.get("seq"));
            }
            // 上级ID
            if(searchMap.get("parentId")!=null ){
                criteria.andEqualTo("parentId",searchMap.get("parentId"));
            }
            // 模板ID
            if(searchMap.get("templateId")!=null ){
                criteria.andEqualTo("templateId",searchMap.get("templateId"));
            }

        }
        return example;
    }

}
