package com.zlst.proxy;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;

public class ProxyListenerRestFulCommon extends ProxyListenerRestFul{
	
	@Override
	public void processResult(DelegateExecution execution, String result) {
		// TODO Auto-generated method stub
		// 可能需要将结果进行解析后存入到流程变量中，并影响后续流程的扭转
		// execution.setVariable("p5", result);
	}

	@Override
	public void notify(DelegateTask delegateTask) {
	}

}
