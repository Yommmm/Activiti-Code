package com.zlst.proxy.vo;

import java.util.Date;

/**
 * Created by 170079 on 2017/10/17.
 */
public class CreateOrderExceptionRequest {

    //任务节点名称
    private String taskDefineName;

    //订单标识
    private String orderId;

    //异常类型
    private String exceptionType;

    //流程定义标识
    private String procDefId;

    //异常创建时间
    private Date createTime;

    //堆栈信息
    private String stackTrace;

    public String getTaskDefineName() {
        return taskDefineName;
    }

    public void setTaskDefineName(String taskDefineName) {
        this.taskDefineName = taskDefineName;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }

    public String getProcDefId() {
        return procDefId;
    }

    public void setProcDefId(String procDefId) {
        this.procDefId = procDefId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }
}
