package com.zlst.business.activiti.listener;

import org.activiti.bpmn.model.ActivitiListener;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.impl.bpmn.parser.BpmnParse;
import org.activiti.engine.impl.bpmn.parser.handler.UserTaskParseHandler;
import org.activiti.engine.impl.task.TaskDefinition;

/**
 * Created by songcj on 2017/9/19.
 */
public class UserTaskCreateParserHandler extends UserTaskParseHandler{

    protected void executeParse(BpmnParse bpmnParse, UserTask userTask){
        super.executeParse(bpmnParse,userTask);
        TaskDefinition taskDefinition = (TaskDefinition) bpmnParse.getCurrentActivity().getProperty("taskDefinition");
        ActivitiListener activitiListener = new ActivitiListener();
        activitiListener.setEvent("create");
        activitiListener.setImplementationType("delegateExpression");
        activitiListener.setImplementation("${userTaskCreateListener}");
        taskDefinition.addTaskListener("create",bpmnParse.getListenerFactory().createDelegateExpressionTaskListener(activitiListener));
    }

}
