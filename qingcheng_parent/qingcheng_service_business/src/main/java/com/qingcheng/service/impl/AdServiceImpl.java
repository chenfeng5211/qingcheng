package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qingcheng.dao.AdMapper;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.business.Ad;
import com.qingcheng.service.business.AdService;
import com.qingcheng.util.CacheKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class AdServiceImpl implements AdService {

    @Autowired
    private AdMapper adMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 返回全部记录
     * @return
     */
    public List<Ad> findAll() {
        return adMapper.selectAll();
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Ad> findPage(int page, int size) {
        PageHelper.startPage(page,size);
        Page<Ad> ads = (Page<Ad>) adMapper.selectAll();
        return new PageResult<Ad>(ads.getTotal(),ads.getResult());
    }

    /**
     * 条件查询
     * @param searchMap 查询条件
     * @return
     */
    public List<Ad> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return adMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Ad> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        Page<Ad> ads = (Page<Ad>) adMapper.selectByExample(example);
        return new PageResult<Ad>(ads.getTotal(),ads.getResult());
    }

    /**
     * 根据Id查询
     * @param id
     * @return
     */
    public Ad findById(Integer id) {
        return adMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     * @param ad
     */
    public void add(Ad ad) {
        adMapper.insert(ad);
        saveAdByPositionToRedis(ad.getPosition());
    }

    /**
     * 修改
     * @param ad
     */
    public void update(Ad ad) {

//        查询修改前的广告
        Ad key = adMapper.selectByPrimaryKey(ad.getId());

        adMapper.updateByPrimaryKeySelective(ad);
//        当修改的不是位置时，直接同步到redis
        saveAdByPositionToRedis(ad.getPosition());
//        当修改的位置不同时，还需要对原来的位置同步
        if(!key.getPosition().equals(ad.getPosition())){
            saveAdByPositionToRedis(key.getPosition());
        }
    }

    /**
     *  删除
     * @param id
     */
    public void delete(Integer id) {

        Ad ad = adMapper.selectByPrimaryKey(id);

//        逻辑删除
        adMapper.deleteByPrimaryKey(id);

        saveAdByPositionToRedis(ad.getPosition());
    }


    /**
     * 功能描述:
     *
     * 根据首页位置查找轮播图
     */

    public List<Ad> findByP(String position) {


//        return adMapper.selectAll();
        return (List<Ad>) redisTemplate.boundHashOps(CacheKey.AD).get(position);

    }


    private List<String> position(){
        ArrayList<String> positionList = new ArrayList<String>();
        positionList.add("web_index_lb");
        return positionList;
    }
    /**
     * 功能描述:
     * 将说有广告同步到redis中
     */

    public void saveAdAllToRedis() {
        List<String> positionList = position();
        for (String position : positionList) {
            //        创建查询条件
            saveAdByPositionToRedis(position);

        }
    }


    /**
     * 功能描述:
     * 根据单个位置添加广告到redis
     */

    public void saveAdByPositionToRedis(String position) {
        //        创建查询条件
        Example example = new Example(Ad.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("position", position); //确定轮播图首页
        criteria.andLessThanOrEqualTo("startTime", new Date());//现在时间要小于轮播图规定最大时间
        criteria.andGreaterThanOrEqualTo("endTime", new Date());//现在时间大于数据库中规定的最小时间
        criteria.andEqualTo("status", "1");//注意状态
        List<Ad> ads = adMapper.selectByExample(example);

        redisTemplate.boundHashOps(CacheKey.AD).put(position, ads);
    }

    /**
     * 构建查询条件
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Ad.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 广告名称
            if(searchMap.get("name")!=null && !"".equals(searchMap.get("name"))){
                criteria.andLike("name","%"+searchMap.get("name")+"%");
            }
            // 广告位置
            if(searchMap.get("position")!=null && !"".equals(searchMap.get("position"))){
                criteria.andLike("position","%"+searchMap.get("position")+"%");
            }
            // 状态
            if(searchMap.get("status")!=null && !"".equals(searchMap.get("status"))){
                criteria.andLike("status","%"+searchMap.get("status")+"%");
            }
            // 图片地址
            if(searchMap.get("image")!=null && !"".equals(searchMap.get("image"))){
                criteria.andLike("image","%"+searchMap.get("image")+"%");
            }
            // URL
            if(searchMap.get("url")!=null && !"".equals(searchMap.get("url"))){
                criteria.andLike("url","%"+searchMap.get("url")+"%");
            }
            // 备注
            if(searchMap.get("remarks")!=null && !"".equals(searchMap.get("remarks"))){
                criteria.andLike("remarks","%"+searchMap.get("remarks")+"%");
            }

            // ID
            if(searchMap.get("id")!=null ){
                criteria.andEqualTo("id",searchMap.get("id"));
            }

        }
        return example;
    }

}
