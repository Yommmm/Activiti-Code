package com.zlst.business.activiti.listener.vo;

import java.util.Date;

/**
 * Created by 170079 on 2017/11/14.
 */
public class CreateActivityInstRequestVO {
    private String procDefId;    //流程定义标识
    private String procInstId;   //流程实例标识
    private String actId;    //节点标识
    private String actName;   //节点名称
    private String actType;     //节点类型
    private String userId; //操作人员标识
    private String userName;  //操作人员名称
    private String procDefName;  //流程定义名称
    private String orderId;  //订单标识

    public String getProcDefId() {
        return procDefId;
    }

    public void setProcDefId(String procDefId) {
        this.procDefId = procDefId;
    }

    public String getProcInstId() {
        return procInstId;
    }

    public void setProcInstId(String procInstId) {
        this.procInstId = procInstId;
    }

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public String getActName() {
        return actName;
    }

    public void setActName(String actName) {
        this.actName = actName;
    }

    public String getActType() {
        return actType;
    }

    public void setActType(String actType) {
        this.actType = actType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProcDefName() {
        return procDefName;
    }

    public void setProcDefName(String procDefName) {
        this.procDefName = procDefName;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
