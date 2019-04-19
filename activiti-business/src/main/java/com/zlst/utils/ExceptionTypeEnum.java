package com.zlst.utils;

/**
 * Created by 170079 on 2017/10/17.
 */
public enum ExceptionTypeEnum {
    //启动
    START_EXCEPTION("Start"),
    //监听异常,
    LISTENER_EXCEPTION("Listener"),
    //业务节点
    TASK_EXCEPTION("BusinessTask"),
    //公共监听
    COMMON_TASK_EXCEPTION("CommonTask"),
    //完成监听
    COMPLETED_LISTENER_EXCEPTION("Compeleted Listener");

    //异常类型
    private String exceptionType;

    ExceptionTypeEnum(String exceptionType) {
        this.exceptionType  = exceptionType;
    }

    public String getExceptionType() {
        return exceptionType;
    }
}
