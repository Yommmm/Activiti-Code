package com.zlst.module.workflow.vo;

import com.zlst.common.BaseQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "OmsTaskDealRuleVO", description = "TODO")
public class OmsTaskDealRuleVO extends BaseQuery {
    
    /**
	 * 处理人规则ID
	 */
	@ApiModelProperty(value = "处理人规则ID")
	private String dealRuleId;
	
    /**
	 * 唯一标识
	 */
	@ApiModelProperty(value = "唯一标识")
	private String id;
	
    /**
	 * 任务来源单位
	 */
	@ApiModelProperty(value = "任务来源单位")
	private String taskOrgId;
	
    /**
	 * 处理人员类型：R：角色,U：用户
	 */
	@ApiModelProperty(value = "处理人员类型：R：角色,U：用户")
	private String taskDealUserType;
	
    /**
	 * 处理人员标识
	 */
	@ApiModelProperty(value = "处理人员标识")
	private String taskDealUserId;
	
    /**
	 * 处理人组织
	 */
	@ApiModelProperty(value = "处理人组织")
	private String taskDealOrgId;
	
    /**
	 * 状态:0启用，1禁用
	 */
	@ApiModelProperty(value = "状态:0启用，1禁用")
	private String status;
	
	public void setDealRuleId(String dealRuleId) {
		this.dealRuleId = dealRuleId;
	}
	
	public String getDealRuleId() {
		return this.dealRuleId;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return this.id;
	}
	
	public void setTaskOrgId(String taskOrgId) {
		this.taskOrgId = taskOrgId;
	}
	
	public String getTaskOrgId() {
		return this.taskOrgId;
	}
	
	public void setTaskDealUserType(String taskDealUserType) {
		this.taskDealUserType = taskDealUserType;
	}
	
	public String getTaskDealUserType() {
		return this.taskDealUserType;
	}
	
	public void setTaskDealUserId(String taskDealUserId) {
		this.taskDealUserId = taskDealUserId;
	}
	
	public String getTaskDealUserId() {
		return this.taskDealUserId;
	}
	
	public void setTaskDealOrgId(String taskDealOrgId) {
		this.taskDealOrgId = taskDealOrgId;
	}
	
	public String getTaskDealOrgId() {
		return this.taskDealOrgId;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getStatus() {
		return this.status;
	}
	
	
}