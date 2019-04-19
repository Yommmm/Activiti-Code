package com.zlst.business.activiti.listener;

import java.util.Date;

import com.zlst.navigator.common.spring.SpringUtil;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.zlst.common.ExceptionConstants;
import com.zlst.exception.ZLSTException;
import com.zlst.module.order.bean.OmsActivityInstance;
import com.zlst.module.order.bean.vo.CreateActivityInstRequestVO;
import com.zlst.module.order.service.OmsActivityInstanceService;

public class ReceiveTaskListener implements ExecutionListener {

    private final static Logger LOG = LoggerFactory.getLogger(ReceiveTaskListener.class);
    
//    @Autowired
//    public OmsActivityInstanceService omsActivityInstanceService;

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
        /*String url = "/order/activity";
        String result = NavigatorUtil.postForObjcet(url,
                JSON.toJSONString(createActivityInstRequestVO),url+ RequestMethod.POST.getName());

        if (ResultUtil.occurException(result)) {
            ResultUtil.recordOrderException(delegateExecution.getProcessDefinitionId(), delegateExecution.getCurrentActivityName(), getVariable(delegateExecution,"orderId"),
                    ExceptionTypeEnum.COMMON_TASK_EXCEPTION.getExceptionType(), ResultUtil.getResponseStackTrace(result));
        }*/
        
        /**
         * 2019.02.11 新年第一天上班，把上面这个调用改成本地方法调用
         */
        this.callOrderManager(createActivityInstRequestVO);

        LOG.info("Receive Task Listener end.");
    }
    
    /**
     * 调用本地方法保存【任务节点信息】
     * @param createActivityInstRequestVO
     */
	private void callOrderManager(CreateActivityInstRequestVO createActivityInstRequestVO) {
		if (createActivityInstRequestVO == null || StringUtils.isBlank(createActivityInstRequestVO.getOrderId())) {
			throw new ZLSTException(ExceptionConstants.REQ_IS_NULL);
		}
		OmsActivityInstance omsActivityInstance = new OmsActivityInstance();
		BeanUtils.copyProperties(createActivityInstRequestVO, omsActivityInstance);
		omsActivityInstance.setCreateTime(new Date());
		try {
		    //这个地方注入方式。拿不到。所以只能从Spring 上下文拿
            OmsActivityInstanceService omsActivityInstanceService = (OmsActivityInstanceService) SpringUtil.getBean("omsActivityInstanceService");
			omsActivityInstanceService.save(omsActivityInstance);
		} catch (Exception e) {
			throw new ZLSTException(e, ExceptionConstants.SAVE_FAILURE);
		}
	}

    private String getVariable(DelegateExecution delegateExecution,String variableName) {
        String value = delegateExecution.getVariable(variableName, String.class);
        LOG.debug(variableName," : " ,value );
        return value;
    }
}
