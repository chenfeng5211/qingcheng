/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: CategoryBrand
 * Author:   chenf
 * Date:     2019/7/17 0017 14:35
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.pojo.goods;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 〈〉
 *
 * @author chenf
 * @create 2019/7/17 0017
 * @since 1.0.0
 */
@Table(name="tb_category_brand")
public class CategoryBrand implements Serializable {

    @Id
    private Integer categoryId;
    @Id
    private Integer brandId;

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }
}