package com.zlst.module.order.bean.vo;

import java.util.List;

/**
 * 订单流程通知接口
 */
public class OrderFlowMessagesRequest {

    private String action;
    private String activiyId;
    private String reason;
    private List<RestVariableItemVo> variables;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getActivityId() {
        return activiyId;
    }

    public void setActivityId(String activiyId) {
        this.activiyId = activiyId;
    }

    public List<RestVariableItemVo> getVariables() {
        return variables;
    }

    public void setVariables(List<RestVariableItemVo> variables) {
        this.variables = variables;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
