package com.zlst.utils;

/**
 * Created by 170079 on 2017/10/17.
 */
public enum ExceptionTypeEnum {
    //启动
    START_EXCEPTION("Start"),
    //启动异常,
    LISTENER_EXCEPTION("Listener"),
    //运行
    TASK_EXCEPTION("Task");


    //异常类型
    private String exceptionType;

    ExceptionTypeEnum(String exceptionType) {
        this.exceptionType  = exceptionType;
    }

    public String getExceptionType() {
        return exceptionType;
    }
}
