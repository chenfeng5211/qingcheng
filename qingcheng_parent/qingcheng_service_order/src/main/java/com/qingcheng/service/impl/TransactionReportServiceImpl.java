/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: TransactionReportServiceImpl
 * Author:   chenf
 * Date:     2019/7/21 0021 13:29
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 */
package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.qingcheng.dao.TransactionReportMapper;
import com.qingcheng.pojo.order.TransactionReport;
import com.qingcheng.service.order.TransactionReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**
 * 〈〉
 *
 * @author chenf
 * @create 2019/7/21 0021
 * @since 1.0.0
 */
@Service(interfaceClass = TransactionReportService.class)
public class TransactionReportServiceImpl implements TransactionReportService {

    @Autowired
    private TransactionReportMapper transactionReportMapper;
    /**
     * 功能描述:
     *
     * 交易统计存入到数据库
     */

    @Override
    @Transactional
    public void saveTransactionReport(LocalDate date) {
        TransactionReport transactionReport = transactionReportMapper.getTransactionReport1(date);
        TransactionReport transactionReport2 = transactionReportMapper.getTransactionReport2(date);

//        LocalDate转Date
        String dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dateParse = null;
        try {
            dateParse = simpleDateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        transactionReport.setCountDate(dateParse);
        transactionReport.setOrderMoney(transactionReportMapper.getOrderMoney(date));
        transactionReport.setReturnMoney(transactionReportMapper.getReturnMoney(date));
        transactionReport.setOrderEffectiveNum(transactionReportMapper.getEffectiveOrderNum(date));
        transactionReport.setPayMoney(transactionReport2.getPayMoney());
        transactionReport.setPayOrderNum(transactionReport2.getPayOrderNum());
        transactionReport.setPayPieceNum(transactionReport2.getPayPieceNum());
        transactionReport.setPayUserNum(transactionReport2.getPayUserNum());
        transactionReport.setVc(12155551);
        if (transactionReport2.getPayOrderNum() != 0){
            transactionReport.setPayCustomerPrice(transactionReport2.getPayMoney() / transactionReport2.getPayOrderNum());
        }else{
            transactionReport.setPayCustomerPrice(0);
        }
        transactionReportMapper.insert(transactionReport);
    }


    /**
     * 功能描述:
     * 查询交易统计
     */

    @Override
    public List<TransactionReport> findTransactionReport(String date1, String date2) {
        return transactionReportMapper.findTransactionReport(date1, date2);
    }
}