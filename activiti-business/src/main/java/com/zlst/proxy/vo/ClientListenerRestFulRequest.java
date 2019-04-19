package com.zlst.proxy.vo;

import java.util.Map;

/**
 * Created by songcj on 2017/9/11.
 */
public class ClientListenerRestFulRequest {

    private String orderId;
    private String taskInstanceId;
    private String taskInstanceName;
    private Map<String,Object> variables;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
	public String getTaskInstanceId() {
		return taskInstanceId;
	}

	public void setTaskInstanceId(String taskInstanceId) {
		this.taskInstanceId = taskInstanceId;
	}

	public String getTaskInstanceName() {
		return taskInstanceName;
	}

	public void setTaskInstanceName(String taskInstanceName) {
		this.taskInstanceName = taskInstanceName;
	}

	public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }
}
