package com.zlst.business.activiti.listener;

import org.activiti.bpmn.model.ReceiveTask;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.impl.bpmn.parser.BpmnParse;
import org.activiti.engine.impl.bpmn.parser.handler.ReceiveTaskParseHandler;

/**
 * Created by 170079 on 2017/11/13.
 */
public class ReceiveTaskParseCustomHandler extends ReceiveTaskParseHandler{

    public void executeParse(BpmnParse bpmnParse, ReceiveTask receiveTask) {
        super.executeParse(bpmnParse,receiveTask);
        bpmnParse.getCurrentActivity().addExecutionListener(ExecutionListener.EVENTNAME_END,new ReceiveTaskListener());
    }
}
