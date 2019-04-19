package com.zlst.business.activiti.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zlst.business.activiti.listener.vo.CreateActivityInstRequestVO;
import com.zlst.utils.ExceptionTypeEnum;
import com.zlst.utils.NavigatorUtil;
import com.zlst.utils.RequestMethod;
import com.zlst.utils.ResultUtil;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.repository.ProcessDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public class ReceiveTaskListener implements ExecutionListener {

    private final static Logger LOG = LoggerFactory.getLogger(ReceiveTaskListener.class);



    @Override
    public void notify(DelegateExecution delegateExecution) throws Exception {
        LOG.info("Receive Task Listener ",delegateExecution.getCurrentActivityName()," start...");
        CreateActivityInstRequestVO  createActivityInstRequestVO = new CreateActivityInstRequestVO();
        createActivityInstRequestVO.setActId(delegateExecution.getCurrentActivityId());
        createActivityInstRequestVO.setActName(delegateExecution.getCurrentActivityName());
        createActivityInstRequestVO.setActType("receiveTask");
        createActivityInstRequestVO.setOrderId(getVariable(delegateExecution,"orderId"));
        ProcessDefinition processDefinition = delegateExecution.getEngineServices().getRepositoryService().getProcessDefinition(delegateExecution.getProcessDefinitionId());
        createActivityInstRequestVO.setProcDefId(processDefinition.getKey());
        createActivityInstRequestVO.setProcDefName(processDefinition.getName());
        createActivityInstRequestVO.setProcInstId(delegateExecution.getProcessInstanceId());
        createActivityInstRequestVO.setUserId(getVariable(delegateExecution,"receiveUserId"));
        createActivityInstRequestVO.setUserName(getVariable(delegateExecution,"receiveUserName"));

        //调用BUSINESS服务，插入节点任务信息
        String url = "/order/activity";
        String result = NavigatorUtil.postForObjcet(url,
                JSON.toJSONString(createActivityInstRequestVO),url+ RequestMethod.POST.getName());

        if (ResultUtil.occurException(result)) {
            ResultUtil.recordOrderException(delegateExecution.getProcessDefinitionId(), delegateExecution.getCurrentActivityName(), getVariable(delegateExecution,"orderId"),
                    ExceptionTypeEnum.COMMON_TASK_EXCEPTION.getExceptionType(), ResultUtil.getResponseStackTrace(result));
        }

        LOG.info("Receive Task Listener end.");
    }

    private String getVariable(DelegateExecution delegateExecution,String variableName) {
        String value = delegateExecution.getVariable(variableName, String.class);
        LOG.debug(variableName," : " ,value );
        return value;
    }
}
