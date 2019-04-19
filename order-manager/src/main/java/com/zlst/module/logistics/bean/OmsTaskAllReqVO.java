package com.zlst.module.logistics.bean;

import java.util.List;

/**
 * Created by 刘涛 on 2018/3/20.
 */
public class OmsTaskAllReqVO {
    /**
     * 流程定义Id
     * 支持多个同时查询
     */
    List<String> procDefIds;
    /**
     * 状态
     */
    String status;
    /**
     * 任务名称
     */
    String taskName;

    public List<String> getProcDefIds() {
        return procDefIds;
    }

    public void setProcDefIds(List<String> procDefIds) {
        this.procDefIds = procDefIds;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
}
