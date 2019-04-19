package com.zlst.module.order.bean.vo;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

public class ExecutionActionRequest extends RestActionRequest {

  public static final String ACTION_SIGNAL = "signal";
  public static final String ACTION_SIGNAL_EVENT_RECEIVED = "signalEventReceived";
  public static final String ACTION_MESSAGE_EVENT_RECEIVED = "messageEventReceived";
  
  protected String signalName;
  protected String messageName;
  protected List<RestVariable> variables;
  
  public void setVariables(List<RestVariable> variables) {
    this.variables = variables;
  }

  public List<RestVariable> getVariables() {
    return variables;
  }
  
  public String getSignalName() {
    return signalName;
  }
  
  public void setSignalName(String signalName) {
    this.signalName = signalName;
  }
  
  public String getMessageName() {
    return messageName;
  }
  
  public void setMessageName(String messageName) {
    this.messageName = messageName;
  }
}