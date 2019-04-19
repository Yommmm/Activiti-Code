package com.zlst.module.order.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.dom4j.tree.AbstractEntity;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by 170066 on 2017/9/5.
 */
@Table(name = "oms_order_template_rule")
@Entity
public class OmsOrderTemplateRule extends AbstractEntity {

    /**
     * ID 32位，系统自动生成
     */
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "rule_id", length = 32)
    private String ruleId;//规则ID

    @Column(name = "process_def_id", length = 32)
    private String processDefId;//流程定义ID

    @Column(name = "busi_type", length = 32)
    private String busiType;//业务类型

    @Column(name = "effect_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date effectTime;//生效时间

    @Column(name = "expire_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date expireTime;//失效时间

    @Column(name = "created_by", length = 32)
    private String createdBy;//创建人

    @Column(name = "create_time", updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @CreationTimestamp
    private Date createTime;//创建时间

    @Column(name = "modified_by", length = 32)
    private String modifiedBy;//修改人

    @Column(name = "modify_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @CreationTimestamp
    private Date modifyTime;//修改时间

    @Column(name = "remark", length = 256)
    private String remark;//备注

    public OmsOrderTemplateRule() {
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getProcessDefId() {
        return processDefId;
    }

    public void setProcessDefId(String processDefId) {
        this.processDefId = processDefId;
    }

    public Date getEffectTime() {
        return effectTime;
    }

    public void setEffectTime(Date effectTime) {
        this.effectTime = effectTime;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public String getBusiType() {
        return busiType;
    }

    public void setBusiType(String busiType) {
        this.busiType = busiType;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
