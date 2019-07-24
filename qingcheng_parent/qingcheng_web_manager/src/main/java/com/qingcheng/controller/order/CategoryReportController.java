/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: CategoryReportController
 * Author:   chenf
 * Date:     2019/7/20 0020 11:01
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.controller.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.order.CategoryReport;
import com.qingcheng.service.order.CategoryReportService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 〈条目统计〉
 *
 * @author chenf
 * @create 2019/7/20 0020
 * @since 1.0.0
 */
@RestController
@RequestMapping("/categoryReport")
public class CategoryReportController {

    @Reference
    private CategoryReportService categoryReportService;

    @RequestMapping("/findCategoryReport")
    public List<CategoryReport> findCategoryReport(){

//        统计前一天
//        LocalDate localDate = LocalDate.now().minusDays(1);
        LocalDate localDate = LocalDate.parse("2019-04-15", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return categoryReportService.findCategoryReport(localDate);
    }

    @Scheduled(cron = "0 0 1 * * ?")
    @RequestMapping("/saveYesterday")
    public void saveYesterday() {
        System.out.println("测试啦");
        categoryReportService.saveCategoryReport();

    }

    @RequestMapping("/findCategoryReportBytime")
    public List<Map> findCategoryReportBytime(String date1, String date2){
        return categoryReportService.findCategoryReportByTime(date1, date2);
    }
}