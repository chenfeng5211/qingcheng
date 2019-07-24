/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: CategoryReport
 * Author:   chenf
 * Date:     2019/7/20 0020 10:37
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.pojo.order;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 〈〉
 *
 * @author chenf
 * @create 2019/7/20 0020
 * @since 1.0.0
 */
@Table(name = "tb_category_report")
public class CategoryReport implements Serializable {

    @Id
    private Integer categoryId1;
    @Id
    private Integer categoryId2;
    @Id
    private Integer categoryId3;
    @Id
    private Date countDate;
    private Integer num;
    private Integer money;

    public Integer getCategoryId1() {
        return categoryId1;
    }

    public void setCategoryId1(Integer categoryId1) {
        this.categoryId1 = categoryId1;
    }

    public Integer getCategoryId2() {
        return categoryId2;
    }

    public void setCategoryId2(Integer categoryId2) {
        this.categoryId2 = categoryId2;
    }

    public Integer getCategoryId3() {
        return categoryId3;
    }

    public void setCategoryId3(Integer categoryId3) {
        this.categoryId3 = categoryId3;
    }

    public Date getCountDate() {
        return countDate;
    }

    public void setCountDate(Date countDate) {
        this.countDate = countDate;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }
}