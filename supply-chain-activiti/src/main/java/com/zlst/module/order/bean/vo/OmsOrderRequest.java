package com.zlst.module.order.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by 170066 on 2017/9/7.
 */
public class OmsOrderRequest implements Serializable {

    private String busiType;//业务类型（必填）
    private String createOperId;//创建人（必填）
    private String createOrderOrgId;//订单创建部门标识（必填）
    private String externalTaskId;//外部任务标识（可选）
    private int orderPriority;//订单优先级（可选）
    private String parentOrderId;//上级订单标识（可选）
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date wishedFinishDate;//期望完成时间（可选）
    private List<RestVariableItemVo> variables;
    private String synFlag;//是否是同步执行（创建和拉起一块），Y:是，N:不是，默认为N，即异步执行
    public OmsOrderRequest() {
    }

    public String getBusiType() {
        return busiType;
    }

    public void setBusiType(String busiType) {
        this.busiType = busiType;
    }

    public String getCreateOperId() {
        return createOperId;
    }

    public void setCreateOperId(String createOperId) {
        this.createOperId = createOperId;
    }

    public String getCreateOrderOrgId() {
        return createOrderOrgId;
    }

    public void setCreateOrderOrgId(String createOrderOrgId) {
        this.createOrderOrgId = createOrderOrgId;
    }

    public String getExternalTaskId() {
        return externalTaskId;
    }

    public void setExternalTaskId(String externalTaskId) {
        this.externalTaskId = externalTaskId;
    }

    public int getOrderPriority() {
        return orderPriority;
    }

    public void setOrderPriority(int orderPriority) {
        this.orderPriority = orderPriority;
    }

    public String getParentOrderId() {
        return parentOrderId;
    }

    public void setParentOrderId(String parentOrderId) {
        this.parentOrderId = parentOrderId;
    }

    public Date getWishedFinishDate() {
        return wishedFinishDate;
    }

    public void setWishedFinishDate(Date wishedFinishDate) {
        this.wishedFinishDate = wishedFinishDate;
    }

    public List<RestVariableItemVo> getVariables() {
        return variables;
    }

    public void setVariables(List<RestVariableItemVo> variables) {
        this.variables = variables;
    }

    public String getSynFlag() {
        return synFlag;
    }

    public void setSynFlag(String synFlag) {
        this.synFlag = synFlag;
    }
}
