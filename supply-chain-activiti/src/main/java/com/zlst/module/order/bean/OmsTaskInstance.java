package com.zlst.module.order.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by songcj on 2017/9/20.
 */
@Table(name = "oms_task_instance")
@Entity
public class OmsTaskInstance {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "id", length = 32)
    private String id;

    @Column(name = "task_define_id", length = 32)
    private String taskDefineId;

    @Column(name = "order_id", length = 32)
    private String orderId;

    @Column(name = "order_source_id", length = 32)
    private String orderSourceId;

    @Column(name = "proc_inst_id", length = 32)
    private String procInstId;

    @Column(name = "task_inst_id", length = 32)
    private String taskInstId;

    @Column(name = "proc_def_id", length = 128)
    private String procDefId;

    @Column(name = "proc_def_Name", length = 128)
    private String procDefName;

    @Column(name = "task_name", length = 128)
    private String taskName;

    @Column(name = "task_def_id", length = 32)
    private String taskDefId;

    @Column(name = "role_id", length = 32)
    private String roleId;

    @Column(name = "role_name", length = 128)
    private String roleName;

    @Column(name = "user_id", length = 32)
    private String userId;

    @Column(name = "user_name", length = 128)
    private String userName;

    @Column(name = "task_comment", length = 512)
    private String taskComment;

    @Column(name = "status", length = 32)
    private String status;

    @Column(name = "status_time", updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date statusTime;

    @Column(name = "url", length = 512)
    private String url;

    @Column(name = "create_user_id", length = 128)
    private String createUserId;

    @Column(name = "create_org_id", length = 128)
    private String createOrgId;

    @Column(name = "create_time", updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @CreationTimestamp
    private Date createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskDefineId() {
        return taskDefineId;
    }

    public void setTaskDefineId(String taskDefineId) {
        this.taskDefineId = taskDefineId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProcInstId() {
        return procInstId;
    }

    public void setProcInstId(String procInstId) {
        this.procInstId = procInstId;
    }

    public String getTaskInstId() {
        return taskInstId;
    }

    public void setTaskInstId(String taskInstId) {
        this.taskInstId = taskInstId;
    }

    public String getProcDefId() {
        return procDefId;
    }

    public void setProcDefId(String procDefId) {
        this.procDefId = procDefId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDefId() {
        return taskDefId;
    }

    public void setTaskDefId(String taskDefId) {
        this.taskDefId = taskDefId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTaskComment() {
        return taskComment;
    }

    public void setTaskComment(String taskComment) {
        this.taskComment = taskComment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getStatusTime() {
        return statusTime;
    }

    public void setStatusTime(Date statusTime) {
        this.statusTime = statusTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateOrgId() {
        return createOrgId;
    }

    public void setCreateOrgId(String createOrgId) {
        this.createOrgId = createOrgId;
    }

    public String getProcDefName() {
        return procDefName;
    }

    public void setProcDefName(String procDefName) {
        this.procDefName = procDefName;
    }

    public String getOrderSourceId() {
        return orderSourceId;
    }

    public void setOrderSourceId(String orderSourceId) {
        this.orderSourceId = orderSourceId;
    }
}
