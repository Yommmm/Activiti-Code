package com.zlst.module.logistics.bean;

import com.zlst.common.BaseQuery;

import java.util.Date;
import java.util.List;

/**
 * Created by 刘涛 on 2018/3/16.
 */
public class OmsTaskRequestVO extends BaseQuery {
    /**
     * 流程定义Id
     * 支持多个同时查询
     */
    List<String> procDefIds;
    /**
     * orderId    流水Id
     */
    String orderId;
    /**
     * 拼接的orderId.以,隔开
     */
    String orderIdStr;
    /**
     * 状态
     */
    String status;
    /**
     * 任务名称
     */
    String taskName;
    /**
     * orderSourceId
     */
    String orderSourceId;
    /**
     * 创建开始时间
     */
    Date createTimeBegin;
    /**
     * 创建结束时间
     */
    Date createTimeEnd;

    public String getOrderIdStr() {
        return orderIdStr;
    }

    public void setOrderIdStr(String orderIdStr) {
        this.orderIdStr = orderIdStr;
    }

    public List<String> getProcDefIds() {
        return procDefIds;
    }

    public void setProcDefIds(List<String> procDefIds) {
        this.procDefIds = procDefIds;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Date getCreateTimeBegin() {
        return createTimeBegin;
    }

    public void setCreateTimeBegin(Date createTimeBegin) {
        this.createTimeBegin = createTimeBegin;
    }

    public Date getCreateTimeEnd() {
        return createTimeEnd;
    }

    public void setCreateTimeEnd(Date createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
    }

    public String getOrderSourceId() {
        return orderSourceId;
    }

    public void setOrderSourceId(String orderSourceId) {
        this.orderSourceId = orderSourceId;
    }
}
