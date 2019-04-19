package com.zlst.utils;

/**
 * Created by 凌鹏 170119 on 2018/5/18.
 */
public enum RequestMethod {
    POST("/post"),PUT("/put"),GET("/get"),DELETE("/delete");

    private String name;

    public String getName() {
        return name;
    }

    RequestMethod(String name) {
        this.name = name;
    }
}
