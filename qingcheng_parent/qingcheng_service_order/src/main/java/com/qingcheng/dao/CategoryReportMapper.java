/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: CategoryReportMapper
 * Author:   chenf
 * Date:     2019/7/20 0020 10:39
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.qingcheng.dao;

import com.qingcheng.pojo.order.CategoryReport;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author chenf
 * @create 2019/7/20 0020
 * @since 1.0.0
 */
public interface CategoryReportMapper extends Mapper<CategoryReport> {


    /**
     * 功能描述:
     * 订单表和订单详情联合查
     */

    @Select("SELECT oi.`category_id1` categoryId1,oi.`category_id2` categoryId2,oi.`category_id3` categoryId3,DATE_FORMAT( o.`pay_time`,'%Y-%m-%d') countDate,SUM(oi.`num`) num,SUM(oi.`pay_money`) money " +
            "FROM qingcheng_order.tb_order_item oi,qingcheng_order.tb_order o " +
            "WHERE oi.`order_id`=o.`id` " +
            "AND o.`pay_status`='1' " +
            "AND o.`is_delete`='0'  " +
            "AND  DATE_FORMAT( o.`pay_time`,'%Y-%m-%d')=#{date} " +
            "GROUP BY oi.`category_id1`,oi.`category_id2`,oi.`category_id3`,DATE_FORMAT( o.`pay_time`,'%Y-%m-%d')")
    public List<CategoryReport> findCategoryReport(@Param("date") LocalDate date);


    /**
     * 功能描述:
     * 按时间查询统计条目
     */

    @Select("select tcr.category_id1,v.name categoryName, sum(tcr.num) num, sum(tcr.money) money " +
            "from qingcheng_order.tb_category_report tcr, qingcheng_order.v_category1 v " +
            "where tcr.category_id1 = v.id " +
            "and tcr.`count_date` between #{date1}  and #{date2}  " +
            "group by tcr.`category_id1`, v.name")
    public List<Map> findCategoryReportByTime(@Param("date1") String date1, @Param("date2") String date2);



}