package com.zlst.module.order.bean.vo;

import java.util.Date;

/**
 * Created by songcj on 2017/9/26.
 */
public class OmsTasksDataVo {

    private String id;          //工单标识
    private String orderId;     //订单标识
    private String orderSourceId;  //订单来源标识
    private String procDefName; //业务流程名称
    private String status;      //工单状态
    private String statusTime;    //任务状态时间
    private String taskName;    //任务名称
    private String url;         //处理页面URL

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProcDefName() {
        return procDefName;
    }

    public void setProcDefName(String procDefName) {
        this.procDefName = procDefName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusTime() {
        return statusTime;
    }

    public void setStatusTime(String statusTime) {
        this.statusTime = statusTime;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOrderSourceId() {
        return orderSourceId;
    }

    public void setOrderSourceId(String orderSourceId) {
        this.orderSourceId = orderSourceId;
    }
}
