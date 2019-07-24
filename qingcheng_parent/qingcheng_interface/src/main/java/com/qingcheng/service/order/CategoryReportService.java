/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: CategoryReportService
 * Author:   chenf
 * Date:     2019/7/20 0020 10:42
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.qingcheng.service.order;

import com.qingcheng.pojo.order.CategoryReport;

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
public interface CategoryReportService {

    public List<CategoryReport> findCategoryReport(LocalDate date);

    public void saveCategoryReport();

    public List<Map> findCategoryReportByTime(String date1, String date2);
}