package com.zlst.business.activiti.listener;

import java.util.Date;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zlst.common.ExceptionConstants;
import com.zlst.exception.ZLSTException;
import com.zlst.module.order.bean.OmsOrder;
import com.zlst.module.order.bean.OmsTaskDefine;
import com.zlst.module.order.bean.OmsTaskInstance;
import com.zlst.module.order.bean.vo.InitOmsTaskInstanceRequest;
import com.zlst.module.order.bean.vo.InitOmsTaskInstanceResponse;
import com.zlst.module.order.service.OmsOrderService;
import com.zlst.module.order.service.OmsTaskDefineService;
import com.zlst.module.order.service.OmsTaskInstanceService;

/**
 * Created by songcj on 2017/9/19.
 */
@Service("userTaskCreateListener")
public class UserTaskCreateListener implements TaskListener{

    protected Logger logger = LoggerFactory.getLogger(UserTaskCreateListener.class);

    @Autowired
    public RepositoryService repositoryService;
    
    @Autowired
    public OmsOrderService omsOrderService;
    
    @Autowired
    public OmsTaskDefineService omsTaskDefineService;
    
    @Autowired
    public OmsTaskInstanceService omsTaskInstanceService;

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
        /*String url = "/omsTaskInstance/saveOmsTaskInstance";
        String result = NavigatorUtil.postForObjcet(url,
                JSON.toJSONString(initOmsTaskInstanceRequest),url+ RequestMethod.POST.getName());

        if (ResultUtil.occurException(result)) {
            ResultUtil.recordOrderException(delegateTask.getProcessDefinitionId(), delegateTask.getName(), delegateTask.getVariable("orderId").toString(),
                    ExceptionTypeEnum.COMMON_TASK_EXCEPTION.getExceptionType(), ResultUtil.getResponseStackTrace(result));
        }*/
        
        /**
         * 2019.02.11 新年第一天上班，把上面这个调用改成本地方法调用
         */
        try {
			this.callOrderManager(initOmsTaskInstanceRequest);
		} catch (Exception e) {
			throw new ZLSTException(e, ExceptionConstants.SAVE_FAILURE);
		}
        
    }
    
    /**
     * 调用本地方法保存【不明所以的信息】
     * @param initOmsTaskInstanceRequest
     * @throws Exception
     */
    private void callOrderManager(InitOmsTaskInstanceRequest initOmsTaskInstanceRequest) throws Exception {
        /** 
         * 1.根据输入信息到表InfTaskDefine中查找相关信息
         */
        // 检查输入必须包含项目ProcDefId和TaskDefId
        if (initOmsTaskInstanceRequest == null || StringUtils.isBlank(initOmsTaskInstanceRequest.getProcDefId()) ||
                StringUtils.isBlank(initOmsTaskInstanceRequest.getTaskDefId())) {
        	throw new ZLSTException(ExceptionConstants.SAVE_FAILURE);
        }
        OmsTaskInstance omsTaskInstance = new OmsTaskInstance();
        OmsTaskDefine omsTaskDefine = new OmsTaskDefine();
        BeanUtils.copyProperties(initOmsTaskInstanceRequest, omsTaskInstance);
        omsTaskDefine.setProcDefId(omsTaskInstance.getProcDefId());
        omsTaskDefine.setTaskDefId(omsTaskInstance.getTaskDefId());
        
        // 开始执行查询
        omsTaskDefine = omsTaskDefineService.queryInfTaskDefineByProcessAndTaskId(omsTaskDefine);
        if (omsTaskDefine == null) {
        	throw new ZLSTException(ExceptionConstants.SAVE_FAILURE);
        }

        OmsOrder omsOrder = omsOrderService.queryOmsOrderbyOrderId(omsTaskInstance.getOrderId());
        
        /**
         * 2.根据infTaskDefine填充infTaskInstance
         */
        omsTaskInstance.setTaskDefineId(omsTaskDefine.getId()); //任务工单定义标识
        omsTaskInstance.setOrderId(omsOrder.getOrderId());      //订单标识
        omsTaskInstance.setOrderSourceId(omsOrder.getOrderSourceId());
        
        // 角色或用户信息，现在先只取ID
        if (OmsTaskDefine.TASK_DEAL_USER_TYPE_R.equals(omsTaskDefine.getTaskDealUserType())) {
            omsTaskInstance.setRoleId(omsTaskDefine.getTaskDealUserId());
        } else {
            if (StringUtils.isEmpty(omsTaskDefine.getTaskDealUserId())) {
                omsTaskInstance.setUserId(omsOrder.getCreatedBy());
            } else {
                omsTaskInstance.setUserId(omsTaskDefine.getTaskDealUserId());
            }
        }
        omsTaskInstance.setProcDefName(initOmsTaskInstanceRequest.getProcDefName());
        String createOrgId = StringUtils.isNotBlank(omsTaskDefine.getTaskDealOrgId()) ? omsTaskDefine.getTaskDealOrgId() : omsOrder.getOrderCreateOrgId();
        omsTaskInstance.setCreateOrgId(createOrgId);//组织ID
        omsTaskInstance.setCreateUserId(omsOrder.getCreatedBy());
        omsTaskInstance.setUrl(omsTaskDefine.getUrl());   //处理页面链接
        
        /**
         * 3.插入表InfTaskInstance
         */
        omsTaskInstance = omsTaskInstanceService.save(omsTaskInstance);
        InitOmsTaskInstanceResponse initOmsTaskInstanceResponse = new InitOmsTaskInstanceResponse();
        BeanUtils.copyProperties(omsTaskInstance, initOmsTaskInstanceResponse);
        logger.info("人工节点任务监听完成, result=" + initOmsTaskInstanceResponse);
    }

}
