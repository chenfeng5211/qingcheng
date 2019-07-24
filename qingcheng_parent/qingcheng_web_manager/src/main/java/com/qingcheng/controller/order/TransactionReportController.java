/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: TransactionReportController
 * Author:   chenf
 * Date:     2019/7/21 0021 14:17
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.controller.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.order.TransactionReport;
import com.qingcheng.service.order.TransactionReportService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 〈〉
 *
 * @author chenf
 * @create 2019/7/21 0021
 * @since 1.0.0
 */
@RestController
@RequestMapping("/transactionReport")
public class TransactionReportController {

    @Reference
    private TransactionReportService transactionReportService;


    @Scheduled(cron = "0 0 1 * * ?")
    public void saveTransactionReport(){
//        LocalDate localDate = LocalDate.now().minusDays(1);
        System.out.println("测试");
        LocalDate date = LocalDate.parse("2019-04-15", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        transactionReportService.saveTransactionReport(date);
    }

    @RequestMapping("/findTransactionReport")
    public List<TransactionReport> findTransactionReport(String date1, String date2){
        return transactionReportService.findTransactionReport(date1, date2);
    }

}