package com.zlst.business.activiti.rest.vo;

import java.util.List;

public class TraceResponse {
	
	private List<TraceItemResponse> result;

	public List<TraceItemResponse> getResult() {
		return result;
	}

	public void setResult(List<TraceItemResponse> result) {
		this.result = result;
	}
	
}
