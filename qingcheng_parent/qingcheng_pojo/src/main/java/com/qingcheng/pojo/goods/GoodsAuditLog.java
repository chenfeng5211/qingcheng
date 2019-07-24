package com.qingcheng.pojo.goods;


import java.util.Date;

public class GoodsAuditLog {

  private String id;
  private Date auditTime;
  private String auditor;
  private String auditMessage;
  private String spuId;
  private String operateLogId;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    public String getAuditor() {
        return auditor;
    }

    public void setAuditor(String auditor) {
        this.auditor = auditor;
    }

    public String getAuditMessage() {
        return auditMessage;
    }

    public void setAuditMessage(String auditMessage) {
        this.auditMessage = auditMessage;
    }

    public String getSpuId() {
        return spuId;
    }

    public void setSpuId(String spuId) {
        this.spuId = spuId;
    }

    public String getOperateLogId() {
        return operateLogId;
    }

    public void setOperateLogId(String operateLogId) {
        this.operateLogId = operateLogId;
    }
}
