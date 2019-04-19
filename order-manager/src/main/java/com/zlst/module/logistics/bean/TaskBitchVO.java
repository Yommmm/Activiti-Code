package com.zlst.module.logistics.bean;

import com.zlst.module.order.bean.vo.RestVariableItemVo;

import java.util.List;

/**
 * Created by 刘涛 on 2018/10/11.
 */
public class TaskBitchVO {
    private String orderId;
    private String taskDefId;
    private String comment;//备注
    private String userId;   //用户标识
    private String userName; //用户名称
    private List<RestVariableItemVo> variables;//流程变量

    public String getTaskDefId() {
        return taskDefId;
    }

    public void setTaskDefId(String taskDefId) {
        this.taskDefId = taskDefId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public List<RestVariableItemVo> getVariables() {
        return variables;
    }

    public void setVariables(List<RestVariableItemVo> variables) {
        this.variables = variables;
    }
}
