package com.zlst.proxy;

import org.activiti.engine.delegate.DelegateExecution;

public class ProxyRestFulCommon extends ProxyRestFul{
	
	@Override
	public void processResult(DelegateExecution execution, String result) {
		// TODO Auto-generated method stub
		// 可能需要将结果进行解析后存入到流程变量中，并影响后续流程的扭转
		// execution.setVariable("p5", result);
	}

}
