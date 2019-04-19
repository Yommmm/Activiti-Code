package com.zlst.business.activiti.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zlst.business.activiti.listener.vo.InitOmsTaskInstanceRequest;
import com.zlst.business.activiti.listener.vo.InitOmsTaskInstanceResponse;
import com.zlst.utils.ExceptionTypeEnum;
import com.zlst.utils.NavigatorUtil;
import com.zlst.utils.RequestMethod;
import com.zlst.utils.ResultUtil;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.repository.ProcessDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by songcj on 2017/9/19.
 */
@Service("userTaskCreateListener")
public class UserTaskCreateListener implements TaskListener{

    protected Logger logger = LoggerFactory.getLogger(UserTaskCreateListener.class);

    @Autowired
    public RepositoryService repositoryService;

    @Override
    public void notify(DelegateTask delegateTask) {
        logger.info("人工节点任务监听开始");
        InitOmsTaskInstanceRequest initOmsTaskInstanceRequest = new InitOmsTaskInstanceRequest();
        ProcessDefinition processDefinition = repositoryService.getProcessDefinition(delegateTask.getProcessDefinitionId());
        initOmsTaskInstanceRequest.setProcDefId(processDefinition.getKey());
        initOmsTaskInstanceRequest.setProcInstId(delegateTask.getProcessInstanceId());
        initOmsTaskInstanceRequest.setTaskInstId(delegateTask.getId());
        initOmsTaskInstanceRequest.setTaskDefId(delegateTask.getTaskDefinitionKey());
        initOmsTaskInstanceRequest.setTaskName(delegateTask.getName());
        initOmsTaskInstanceRequest.setStatus("0");
        initOmsTaskInstanceRequest.setOrderId(delegateTask.getVariable("orderId").toString());
        initOmsTaskInstanceRequest.setStatusTime(new Date());
        initOmsTaskInstanceRequest.setCreateTime(new Date());
        initOmsTaskInstanceRequest.setProcDefName(processDefinition.getName());
        //调用BUSINESS服务，插入节点任务信息
        String url = "/omsTaskInstance/saveOmsTaskInstance";
        String result = NavigatorUtil.postForObjcet(url,
                JSON.toJSONString(initOmsTaskInstanceRequest),url+ RequestMethod.POST.getName());

        if (ResultUtil.occurException(result)) {
            ResultUtil.recordOrderException(delegateTask.getProcessDefinitionId(), delegateTask.getName(), delegateTask.getVariable("orderId").toString(),
                    ExceptionTypeEnum.COMMON_TASK_EXCEPTION.getExceptionType(), ResultUtil.getResponseStackTrace(result));
        }
//        try{
//            InitOmsTaskInstanceResponse initOmsTaskInstanceResponse = JSONObject.parseObject(result,InitOmsTaskInstanceResponse.class);
//        }catch (Exception e){
//            logger.info("人工节点任务监听异常信息如下：");
//            e.printStackTrace();
//        }
        logger.info("人工节点任务监听完成, result="+result);
    }

}
