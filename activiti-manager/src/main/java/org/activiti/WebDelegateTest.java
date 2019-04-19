package org.activiti;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.runtime.Execution;


public class WebDelegateTest implements JavaDelegate {


    public Expression getTextEx() {
        return textEx;
    }

    public void setTextEx(Expression textEx) {
        this.textEx = textEx;
    }

    protected Expression textEx ;


    @Override
    public void execute(DelegateExecution execution) throws Exception {


        if(textEx!=null){
            System.out.println("the testEx is:" +textEx.getValue(execution));
        }else{
            System.out.println("the Second node start...");
        }
        System.out.println(execution.getVariable("result"));
    }
}
