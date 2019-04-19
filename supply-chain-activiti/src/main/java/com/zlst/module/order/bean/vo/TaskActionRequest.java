package com.zlst.module.order.bean.vo;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

public class TaskActionRequest extends RestActionRequest {

  public static final String ACTION_COMPLETE = "complete";
  public static final String ACTION_CLAIM = "claim";
  public static final String ACTION_DELEGATE = "delegate";
  public static final String ACTION_RESOLVE = "resolve";
  
  private String assignee;
  private List<RestVariable> variables;
  
  public void setAssignee(String assignee) {
    this.assignee = assignee;
  }
  public String getAssignee() {
    return assignee;
  }
  public void setVariables(List<RestVariable> variables) {
    this.variables = variables;
  }
  @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, defaultImpl=RestVariable.class)
  public List<RestVariable> getVariables() {
    return variables;
  }
}
