package com.zlst.module.order.bean.vo;

/**
 * Created by songcj on 2017/9/8.
 */
public class OrderFlowMessageItemResponse {

    private String id;
    private String url;
    private String parentId;
    private String parentUrl;
    private String superExecutionId;
    private String superExecutionUrl;
    private String processInstanceId;
    private String processInstanceUrl;
    private String suspended;
    private String activityId;
    private String tenantId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentUrl() {
        return parentUrl;
    }

    public void setParentUrl(String parentUrl) {
        this.parentUrl = parentUrl;
    }

    public String getSuperExecutionId() {
        return superExecutionId;
    }

    public void setSuperExecutionId(String superExecutionId) {
        this.superExecutionId = superExecutionId;
    }

    public String getSuperExecutionUrl() {
        return superExecutionUrl;
    }

    public void setSuperExecutionUrl(String superExecutionUrl) {
        this.superExecutionUrl = superExecutionUrl;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getProcessInstanceUrl() {
        return processInstanceUrl;
    }

    public void setProcessInstanceUrl(String processInstanceUrl) {
        this.processInstanceUrl = processInstanceUrl;
    }

    public String getSuspended() {
        return suspended;
    }

    public void setSuspended(String suspended) {
        this.suspended = suspended;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
