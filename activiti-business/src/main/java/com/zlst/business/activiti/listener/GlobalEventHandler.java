package com.zlst.business.activiti.listener;

import org.activiti.engine.delegate.event.ActivitiEvent;

public interface GlobalEventHandler {
 /** 
  * 事件处理器 
  * @param event 
  */  
 public void handle(ActivitiEvent event);
} 