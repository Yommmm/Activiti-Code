package com.zlst.module.order.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by songcj on 2017/9/20.
 */
@Table(name = "oms_task_define")
@Entity
public class OmsTaskDefine {

    /**
     * R：角色
     */
    public final static String TASK_DEAL_USER_TYPE_R = "R";

    /**
     * U：用户
     */
    public final static String TASK_DEAL_USER_TYPE_U = "U";

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "id", length = 32)
    private String id;

    @Column(name = "proc_def_id", length = 128)
    private String procDefId;

    @Column(name = "task_def_id", length = 128)
    private String taskDefId;

    @Column(name = "task_deal_user_type", length = 4)
    private String taskDealUserType;

    @Column(name = "task_deal_user_id", length = 128)
    private String taskDealUserId;

    @Column(name = "task_deal_org_id", length = 128)
    private String taskDealOrgId;

    @Column(name = "status", length = 1)
    private String status;

    @Column(name = "status_time", updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date statusTime;

    @Column(name = "create_oper_id", length = 128)
    private String createOperId;

    @Column(name = "create_time", updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @CreationTimestamp
    private Date createTime;

    @Column(name = "modify_oper_id", length = 128)
    private String modifyOperId;

    @Column(name = "modify_time", updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date modifyTime;

    @Column(name = "url", length = 512)
    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProcDefId() {
        return procDefId;
    }

    public void setProcDefId(String procDefId) {
        this.procDefId = procDefId;
    }

    public String getTaskDefId() {
        return taskDefId;
    }

    public void setTaskDefId(String taskDefId) {
        this.taskDefId = taskDefId;
    }

    public String getTaskDealUserType() {
        return taskDealUserType;
    }

    public void setTaskDealUserType(String taskDealUserType) {
        this.taskDealUserType = taskDealUserType;
    }

    public String getTaskDealUserId() {
        return taskDealUserId;
    }

    public void setTaskDealUserId(String taskDealUserId) {
        this.taskDealUserId = taskDealUserId;
    }

    public String getTaskDealOrgId() {
        return taskDealOrgId;
    }

    public void setTaskDealOrgId(String taskDealOrgId) {
        this.taskDealOrgId = taskDealOrgId;
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

    public String getCreateOperId() {
        return createOperId;
    }

    public void setCreateOperId(String createOperId) {
        this.createOperId = createOperId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getModifyOperId() {
        return modifyOperId;
    }

    public void setModifyOperId(String modifyOperId) {
        this.modifyOperId = modifyOperId;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
