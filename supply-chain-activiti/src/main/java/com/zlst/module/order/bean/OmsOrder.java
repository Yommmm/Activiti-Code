package com.zlst.module.order.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.dom4j.tree.AbstractEntity;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by 170066 on 2017/9/5.
 */
@Table(name = "oms_order")
@Entity
public class OmsOrder extends AbstractEntity {

    /**
     * ID 32位，系统自动生成
     */
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "order_id", length = 32)
    private String orderId;

    @Column(name = "parent_order_id", length = 32)
    private String parentOrderId;//上级订单ID

    @Column(name = "order_no", length = 32)
    private String orderNo;//订单编号

    @Column(name = "order_source_id", length = 32)
    private String orderSourceId;//订单来源标识

    @Column(name = "busi_type", length = 32)
    private String busiType;//业务类型

    @Column(name = "process_inst_id", length = 32)
    private String processInstId;//流程实例ID

    @Column(name = "order_pri", length = 2)
    private int orderPri;//订单优先级

    @Column(name = "complete_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @CreationTimestamp
    private Date completeDate;//完成日期

    @Column(name = "wished_finish_date", updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date wishedFinishDate;//要求完成日期

    @Column(name = "order_status", length = 32)
    private String orderStatus;//订单状态

    @Column(name="start_retry_num",length = 2)
    private int startRetryNum;//启动重试次数

    @Column(name = "order_create_org_id", length = 32)
    private String orderCreateOrgId;//订单创建部门

    @Column(name = "created_by", length = 32)
    private String createdBy;//创建人

    @Column(name = "create_time", updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @CreationTimestamp
    private Date createTime;//创建时间

    @Column(name = "modified_by", length = 32)
    private String modifiedBy;//修改人

    @Column(name = "modify_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @CreationTimestamp
    private Date modifyTime;//修改时间

    @Column(name = "remark", length = 256)
    private String remark;//备注

    public OmsOrder() {
    }

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
