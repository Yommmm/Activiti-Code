package com.zlst.module.order.bean.vo;

public class TraceItemResponse implements Comparable<TraceItemResponse> {

	private String taskId;
	private String activityId;
	private String activityName;
	private String comment;
	private String assignee;
	private String startTime;
	private String endTime;
	private boolean isExecuted;
	private boolean isCurrentActivity;
	
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getActivityId() {
		return activityId;
	}
	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}
	public String getActivityName() {
		return activityName;
	}
	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public boolean getIsExecuted() {
		return isExecuted;
	}
	public void setIsExecuted(boolean isExecuted) {
		this.isExecuted = isExecuted;
	}
	public boolean getIsCurrentActivity() {
		return isCurrentActivity;
	}
	public void setIsCurrentActivity(boolean isCurrentActivity) {
		this.isCurrentActivity = isCurrentActivity;
	}

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public int compareTo(TraceItemResponse o) {
		//先比较STARTTIME
		if (this.getStartTime() == null && o.getStartTime() == null) {
			return 0;
		} else if (this.getStartTime() == null && o.getStartTime() != null) {
			return 1;
		} else if (this.getStartTime() != null && o.getStartTime() == null) {
			return -1;
		} else  {  //if (this.getStartTime() != null && o.getStartTime() != null)
			if (this.getStartTime().compareTo(o.getStartTime()) > 0) {
				return 1;
			} else if (this.getStartTime().compareTo(o.getStartTime()) < 0) {
				return -1;
			} else {
				//再比较ENDTIME
				if (this.getEndTime() == null && o.getEndTime() == null) {
					return 0;
				} else if (this.getEndTime() == null && o.getEndTime() != null) {
					return 1;
				} else if (this.getEndTime() != null && o.getEndTime() == null) {
					return -1;
				} else { //if (this.getEndTime() != null && o.getEndTime() != null)
					if (this.getEndTime().compareTo(o.getEndTime()) > 0) {
						return 1;
					} else if (this.getEndTime().compareTo(o.getEndTime()) < 0) {
						return -1;
					} else {
						return 0;
					}
				}
			}
		}
	}

}
