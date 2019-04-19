package com.zlst.module.order.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by 170079 on 2017/10/16.
 */
@Table(name = "oms_order_exception")
@Entity
public class OmsOrderException {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "id", length = 32)
    private String id;

    @Column(name = "task_define_name", length = 128)
    private String taskDefineName;

    @Column(name = "order_id", length = 32)
    private String orderId;

    @Column(name = "exception_type", length = 32)
    private String exceptionType;

    @Column(name = "proc_def_id", length = 128)
    private String procDefId;

    @Column(name = "create_time", updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @CreationTimestamp
    private Date createTime;

    @Column(name = "stack_trace", length = 4000)
    private String stackTrace;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
