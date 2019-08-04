package com.qingcheng.pojo.goods;


import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name="tb_goods_operate_log")
public class GoodsOperateLog {

    @Id
  private String id;
  private String operator;
  private Date operateTime;
  private String auditId;
  private String spuId;
  private String isMarketableNow;
  private String isStatusNow;
  private String isDeleteNow;
  private String isMarketableOld;
  private String isStatusOld;
  private String isDeleteOld;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public String getAuditId() {
        return auditId;
    }

    public void setAuditId(String auditId) {
        this.auditId = auditId;
    }

    public String getSpuId() {
        return spuId;
    }

    public void setSpuId(String spuId) {
        this.spuId = spuId;
    }

    public String getIsMarketableNow() {
        return isMarketableNow;
    }

    public void setIsMarketableNow(String isMarketableNow) {
        this.isMarketableNow = isMarketableNow;
    }

    public String getIsStatusNow() {
        return isStatusNow;
    }

    public void setIsStatusNow(String isStatusNow) {
        this.isStatusNow = isStatusNow;
    }

    public String getIsDeleteNow() {
        return isDeleteNow;
    }

    public void setIsDeleteNow(String isDeleteNow) {
        this.isDeleteNow = isDeleteNow;
    }

    public String getIsMarketableOld() {
        return isMarketableOld;
    }

    public void setIsMarketableOld(String isMarketableOld) {
        this.isMarketableOld = isMarketableOld;
    }

    public String getIsStatusOld() {
        return isStatusOld;
    }

    public void setIsStatusOld(String isStatusOld) {
        this.isStatusOld = isStatusOld;
    }

    public String getIsDeleteOld() {
        return isDeleteOld;
    }

    public void setIsDeleteOld(String isDeleteOld) {
        this.isDeleteOld = isDeleteOld;
    }
}
