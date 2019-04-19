package org.activiti;

import org.activiti.engine.delegate.DelegateExecution;

import org.activiti.engine.delegate.JavaDelegate;



public class WebDelegate implements JavaDelegate {



    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.out.println("-----------##########################-------------");
        //System.out.println(execution.getVariable("result"));
        //execution.setVariable("result","222");

    }
}
