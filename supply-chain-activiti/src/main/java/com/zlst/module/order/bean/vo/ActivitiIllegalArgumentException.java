package com.zlst.module.order.bean.vo;

public class ActivitiIllegalArgumentException extends ActivitiException {

  private static final long serialVersionUID = 1L;
  
  public ActivitiIllegalArgumentException(String message) {
    super(message);
  }
  
  public ActivitiIllegalArgumentException(String message, Throwable cause) {
    super(message, cause);
  }
}
