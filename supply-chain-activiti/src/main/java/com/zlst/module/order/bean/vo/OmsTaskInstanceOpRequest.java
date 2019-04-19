package com.zlst.module.order.bean.vo;

import java.util.List;

/**
 * Created by songcj on 2017/9/28.
 */
public class OmsTaskInstanceOpRequest {

    private String comment;//备注
    private String userId;   //用户标识
    private String userName; //用户名称
    private List<RestVariableItemVo> variables;//流程变量

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
