package com.zlst.module.order.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by 170079 on 2017/10/30.
 */
public class OrderVO implements Serializable {
    private String orderId;

    private String parentOrderId;//上级订单ID

    private String orderNo;//订单编号

    private String orderSourceId;//订单来源标识

    private String busiType;//业务类型

    private String processInstId;//流程实例ID

    private int orderPri;//订单优先级

    private Date completeDate;//完成日期

    private Date wishedFinishDate;//要求完成日期

    private String orderStatus;//订单状态

    private int startRetryNum;//启动重试次数

    private String orderCreateOrgId;//订单创建部门

    private String createdBy;//创建人

    private Date createTime;//创建时间

    private String modifiedBy;//修改人

    private Date modifyTime;//修改时间

    private String remark;//备注

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getParentOrderId() {
        return parentOrderId;
    }

    public void setParentOrderId(String parentOrderId) {
        this.parentOrderId = parentOrderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getBusiType() {
        return busiType;
    }

    public void setBusiType(String busiType) {
        this.busiType = busiType;
    }

    public String getProcessInstId() {
        return processInstId;
    }

    public void setProcessInstId(String processInstId) {
        this.processInstId = processInstId;
    }

    public int getOrderPri() {
        return orderPri;
    }

    public void setOrderPri(int orderPri) {
        this.orderPri = orderPri;
    }

    public Date getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(Date completeDate) {
        this.completeDate = completeDate;
    }

    public String getOrderSourceId() {
        return orderSourceId;
    }

    public void setOrderSourceId(String orderSourceId) {
        this.orderSourceId = orderSourceId;
    }

    public Date getWishedFinishDate() {
        return wishedFinishDate;
    }

    public void setWishedFinishDate(Date wishedFinishDate) {
        this.wishedFinishDate = wishedFinishDate;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public int getStartRetryNum() {
        return startRetryNum;
    }

    public void setStartRetryNum(int startRetryNum) {
        this.startRetryNum = startRetryNum;
    }

    public String getOrderCreateOrgId() {
        return orderCreateOrgId;
    }

    public void setOrderCreateOrgId(String orderCreateOrgId) {
        this.orderCreateOrgId = orderCreateOrgId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
