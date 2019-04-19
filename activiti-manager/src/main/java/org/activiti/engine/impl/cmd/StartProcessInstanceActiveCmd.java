package org.activiti.engine.impl.cmd;

import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.runtime.ProcessInstance;

public class StartProcessInstanceActiveCmd implements Command<ProcessInstance> {

    private static final long serialVersionUID = 1L;
    private String processInstanceId;

    public StartProcessInstanceActiveCmd(String processInstanceId){
        this.processInstanceId = processInstanceId;
    }

    public ProcessInstance execute(CommandContext commandContext) {
        ExecutionEntity executionEntity = commandContext.getExecutionEntityManager().findExecutionById(processInstanceId);
        ProcessDefinitionEntity deployedProcessDefinition = Context
                .getProcessEngineConfiguration()
                .getDeploymentManager()
                .findDeployedProcessDefinitionById(executionEntity.getProcessDefinitionId());
        executionEntity.setProcessDefinition(deployedProcessDefinition);
        executionEntity.start();
        return executionEntity;
    }

}  