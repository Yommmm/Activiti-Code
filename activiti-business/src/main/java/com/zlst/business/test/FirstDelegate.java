package com.zlst.business.test;

import org.activiti.engine.delegate.DelegateExecution;

import org.activiti.engine.delegate.JavaDelegate;



public class FirstDelegate implements JavaDelegate {



    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.out.println("-----------##########################-------------");
        System.out.println(execution.getVariable("result"));
        
        execution.setVariable("result","222");
    }
}
