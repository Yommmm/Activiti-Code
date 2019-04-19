package com.zlst.utils;

/**
 * 订单状态枚举类
 * Created by 170079 on 2017/10/16.
 */
public enum OrderStatusEnum {

    //创建
    CREATED("OS00"),
    //启动异常,
    START_EXCEPTION("OS10"),
    //运行
    RUNNING("OS20"),
    //运行异常
    RUN_EXCEPTION("OS50"),
    //订单取消
    CANCEL("OS80"),
    //完成
    COMPLETED("OS90");

    //状态
    private String status;

    OrderStatusEnum(String status) {
        this.status  = status;
    }

    public String getStatus() {
        return status;
    }
}
