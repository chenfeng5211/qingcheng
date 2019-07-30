package com.qingcheng.dao;

import com.qingcheng.pojo.goods.Brand;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface BrandMapper extends Mapper<Brand> {

    @Select("SELECT b.name,b.image FROM tb_brand b WHERE b.id IN " +
            "(SELECT cb.brand_id FROM tb_category_brand cb WHERE cb.category_id IN " +
            "(SELECT c.id FROM tb_category c WHERE c.name = #{categoryName} )) order by b.seq")
    public List<Map> findBrandByCategoryName(@Param("categoryName") String categoryName);
}
