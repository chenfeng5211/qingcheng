/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: TransactionReportService
 * Author:   chenf
 * Date:     2019/7/21 0021 13:27
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.qingcheng.service.order;

import com.qingcheng.pojo.order.TransactionReport;

import java.time.LocalDate;
import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author chenf
 * @create 2019/7/21 0021
 * @since 1.0.0
 */
public interface TransactionReportService {

    public void saveTransactionReport(LocalDate date);

    public List<TransactionReport> findTransactionReport(String date1, String date2);
}