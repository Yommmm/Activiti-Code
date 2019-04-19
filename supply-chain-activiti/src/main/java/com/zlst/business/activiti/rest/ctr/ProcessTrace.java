package com.zlst.business.activiti.rest.ctr;

import org.activiti.rest.common.api.DataResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zlst.business.activiti.rest.service.WorkflowTraceService;
import com.zlst.business.activiti.rest.vo.TraceResponse;

@RestController
@RequestMapping("/processTrail")
//@EnableAutoConfiguration 
public class ProcessTrace {
	
	
	@Autowired
	private WorkflowTraceService workflowTraceService;
	
	@GetMapping(value = "/{processInstanceId}")
    public Object orderVariables(@PathVariable String processInstanceId) {
		TraceResponse result =  null;
		try {
			//result = this.workflowTraceService.traceProcess(processInstanceId);
			result = this.workflowTraceService.getProcessTrace(processInstanceId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
    

}
