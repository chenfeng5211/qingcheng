/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: TransactionReportMapper
 * Author:   chenf
 * Date:     2019/7/21 0021 12:36
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.qingcheng.dao;

import com.qingcheng.pojo.order.TransactionReport;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.time.LocalDate;
import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈交易统计〉
 *
 * @author chenf
 * @create 2019/7/21 0021
 * @since 1.0.0
 */
public interface TransactionReportMapper extends Mapper<TransactionReport> {


    /**
     * 功能描述:
     *
     * 下单人数， 点单数，下单件数
     */

    @Select("SELECT  COUNT(DISTINCT username) orderUserNum, COUNT(id) orderNum, SUM(total_num) orderPieceNum " +
            "FROM qingcheng_order.tb_order " +
            "WHERE DATE_FORMAT(create_time, '%Y-%m-%d') = #{date}  ")
    public TransactionReport getTransactionReport1(@Param("date")LocalDate date);


    /**
     * 功能描述:
     * 有效订单数
     */

    @Select("SELECT COUNT(*) " +
            "FROM qingcheng_order.tb_order " +
            "WHERE is_delete = '0' " +
            "AND DATE_FORMAT(pay_time, '%Y-%m-%d') = #{date} ")
    public Integer getEffectiveOrderNum(@Param("date") LocalDate date);


    /**
     * 功能描述:
     *
     * 下单金额
     */

    @Select("SELECT SUM(total_money) " +
            "FROM qingcheng_order.tb_order " +
            "WHERE is_delete = '0' " +
            "AND DATE_FORMAT(create_time, '%Y-%m-%d') = #{date} ")
    public Integer getOrderMoney(@Param("date") LocalDate date);


    /**
     * 功能描述:
     *
     * 退款金额
     */
    @Select("SELECT SUM(return_money) " +
            "FROM qingcheng_order.tb_return_order " +
            "WHERE DATE_FORMAT(dispose_time, '%Y-%m-%d') = #{date} ")
    public Integer getReturnMoney(@Param("date") LocalDate date);


    /**
     * 功能描述:
     *
     * 付款人数，付款订单数，付款件数，付款金额
     */

    @Select("SELECT COUNT(DISTINCT username) payUserNum, COUNT(*) payOrderNum, SUM(total_num) payPieceNum, SUM(pay_money) payMoney " +
            "FROM qingcheng_order.tb_order " +
            "WHERE pay_status = '1' " +
            "AND DATE_FORMAT(pay_time, '%Y-%m-%d') = #{date}  ")
    public TransactionReport getTransactionReport2(@Param("date") LocalDate date);


    /**
     * 功能描述:
     * 查询交易统计
     */
    @Select("SELECT tr.vc vc, tr.order_user_num orderUserNum, tr.order_num orderNum, tr.order_piece_num orderPieceNum, tr.order_effective_num orderEffectiveNum, " +
            "tr.order_money orderMoney, tr.return_money returnMoney, tr.pay_user_num payUserNum, tr.pay_order_num payOrderNum, " +
            "tr.pay_piece_num payPieceNum, tr.pay_money payMoney, tr.pay_customer_price payCustomerPrice, tr.count_date countDate " +
            "FROM qingcheng_order.tb_transaction_report tr " +
            "WHERE tr.count_date BETWEEN #{date1}  AND #{date2}  ")
    public List<TransactionReport> findTransactionReport(@Param("date1") String date1, @Param("date2") String date2);

}