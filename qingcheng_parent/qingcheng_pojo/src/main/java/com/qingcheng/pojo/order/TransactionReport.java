package com.qingcheng.pojo.order;


import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "tb_transaction_report")
public class TransactionReport implements Serializable {
    @Id
    private Date countDate;
    private Integer vc;
    private Integer orderUserNum;
    private Integer orderNum;
    private Integer orderPieceNum;
    private Integer orderEffectiveNum;
    private Integer orderMoney;
    private Integer returnMoney;
    private Integer payUserNum;
    private Integer payOrderNum;
    private Integer payPieceNum;
    private Integer payMoney;
    private Integer payCustomerPrice;

    public Date getCountDate() {
        return countDate;
    }

    public void setCountDate(Date countDate) {
        this.countDate = countDate;
    }

    public Integer getVc() {
        return vc;
    }

    public void setVc(Integer vc) {
        this.vc = vc;
    }

    public Integer getOrderUserNum() {
        return orderUserNum;
    }

    public void setOrderUserNum(Integer orderUserNum) {
        this.orderUserNum = orderUserNum;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public Integer getOrderPieceNum() {
        return orderPieceNum;
    }

    public void setOrderPieceNum(Integer orderPieceNum) {
        this.orderPieceNum = orderPieceNum;
    }

    public Integer getOrderEffectiveNum() {
        return orderEffectiveNum;
    }

    public void setOrderEffectiveNum(Integer orderEffectiveNum) {
        this.orderEffectiveNum = orderEffectiveNum;
    }

    public Integer getOrderMoney() {
        return orderMoney;
    }

    public void setOrderMoney(Integer orderMoney) {
        this.orderMoney = orderMoney;
    }

    public Integer getReturnMoney() {
        return returnMoney;
    }

    public void setReturnMoney(Integer returnMoney) {
        this.returnMoney = returnMoney;
    }

    public Integer getPayUserNum() {
        return payUserNum;
    }

    public void setPayUserNum(Integer payUserNum) {
        this.payUserNum = payUserNum;
    }

    public Integer getPayOrderNum() {
        return payOrderNum;
    }

    public void setPayOrderNum(Integer payOrderNum) {
        this.payOrderNum = payOrderNum;
    }

    public Integer getPayPieceNum() {
        return payPieceNum;
    }

    public void setPayPieceNum(Integer payPieceNum) {
        this.payPieceNum = payPieceNum;
    }

    public Integer getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(Integer payMoney) {
        this.payMoney = payMoney;
    }

    public Integer getPayCustomerPrice() {
        return payCustomerPrice;
    }

    public void setPayCustomerPrice(Integer payCustomerPrice) {
        this.payCustomerPrice = payCustomerPrice;
    }
}
