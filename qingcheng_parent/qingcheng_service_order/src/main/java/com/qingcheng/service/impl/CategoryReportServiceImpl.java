/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: CategoryReportServiceImpl
 * Author:   chenf
 * Date:     2019/7/20 0020 10:43
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.qingcheng.dao.CategoryReportMapper;
import com.qingcheng.pojo.order.CategoryReport;
import com.qingcheng.service.order.CategoryReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 〈〉
 *
 * @author chenf
 * @create 2019/7/20 0020
 * @since 1.0.0
 */
@Service(interfaceClass = CategoryReportService.class)
public class CategoryReportServiceImpl implements CategoryReportService {

    @Autowired
    private CategoryReportMapper categoryReportMapper;


    /**
     * 功能描述:
     *
     * 按时间查询条目统计,品牌的小时数量和金额统计
     */

    @Override
    public List<CategoryReport> findCategoryReport(LocalDate date) {
        return categoryReportMapper.findCategoryReport(date);
    }


    /**
     * 功能描述:
     * 定时统计，并存入到CatetoryReport数据表中
     * @Date: 2019/7/20 0020 12:33
     */

    @Override
    @Transactional
    public void saveCategoryReport() {

//        统计前一天数据
//        LocalDate localDate = LocalDate.now().minusDays(1);
//        测试
        LocalDate localDate = LocalDate.parse("2019-04-15", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        List<CategoryReport> categoryReportList = categoryReportMapper.findCategoryReport(localDate);
        for (CategoryReport categoryReport : categoryReportList) {

            categoryReportMapper.insert(categoryReport);
        }

    }


    /**
     * 功能描述:
     *
     * 查询统计条目
     */

    @Override
    public List<Map> findCategoryReportByTime(String date1, String date2) {

        return categoryReportMapper.findCategoryReportByTime(date1, date2);
    }

}