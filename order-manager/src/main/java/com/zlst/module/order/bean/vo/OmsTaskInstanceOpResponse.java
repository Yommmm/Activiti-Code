package com.zlst.module.order.bean.vo;

import com.zlst.exception.conf.ExceptionInfo;

/**
 * Created by songcj on 2017/9/28.
 */
public class OmsTaskInstanceOpResponse {

    private String resultCode; //错误信息
    private ExceptionInfo exceptionInfo; //返回编码

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public ExceptionInfo getExceptionInfo() {
        return exceptionInfo;
    }

    public void setExceptionInfo(ExceptionInfo exceptionInfo) {
        this.exceptionInfo = exceptionInfo;
    }
}
