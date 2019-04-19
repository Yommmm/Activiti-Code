package com.zlst.business.activiti.rest.service;


import com.zlst.business.activiti.util.DateConverter;
import org.activiti.engine.*;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zlst.business.activiti.rest.vo.TraceItemResponse;
import com.zlst.business.activiti.rest.vo.TraceResponse;
import com.zlst.business.activiti.util.WorkflowUtils;

import java.util.*;

/**
 * 工作流跟踪相关Service
 * @author HenryYan
 */
@Component
public class WorkflowTraceService {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    
    public final static String TASK_TYPE = "taskType";                //任务类型
    public final static String ASSIGNEE = "assignee";                 //任务签收人
    public final static String START_TIME = "startTime";              //开始时间
    public final static String END_TIME = "endTime";                  //结束时间
    public final static String DOCUMENTATION = "documentation";       //节点说明
    public final static String DESCRIPTION = "description";           //描述
    public final static String ROLES = "roles";                       //任务所属角色
    public final static String USER_INFO = "userInfo";                //当前处理人
    
    @Autowired
    protected RuntimeService runtimeService;

    @Autowired
    protected TaskService taskService;

    @Autowired
    protected RepositoryService repositoryService;

    @Autowired
    protected IdentityService identityService;

    @Autowired
    protected HistoryService historyService;
    
    /**
     * 根据流程实例ID获取流程轨迹信息
     * @param processInstanceId
     * @return
     */
    public TraceResponse getProcessTrace(String processInstanceId){
    	//判断流程是否正在执行中，
    	Execution execution = runtimeService.createExecutionQuery().executionId(processInstanceId).singleResult();//执行实例
        //非执行中的流程，预判为全部在历史表，直接根据历史表返回
    	if (execution == null){
            return getHisProcessTrace(processInstanceId);
        }
    	
    	//正在执行的流程，获取全部的节点
    	ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
                .singleResult();
        ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                .getDeployedProcessDefinition(processInstance.getProcessDefinitionId());
        List<ActivityImpl> activitiList = processDefinition.getActivities();//获得当前任务的所有节点
        
        //获取历史的流程节点
        TraceResponse hisProcessTrace = getHisProcessTrace(processInstanceId);
        return mergeProcessTrace(hisProcessTrace,activitiList);
    }
    
    /**
     * 根据流程号获取历史流程轨迹信息
     * @param processInstanceId
     * @return
     */
    public TraceResponse getHisProcessTrace(String processInstanceId){
    	List<HistoricActivityInstance> historyActivitList =  historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).list();
    	List<TraceItemResponse> traceItemResponseList = new ArrayList<>();
    	TraceItemResponse traceItemResponse = null;
    	DateConverter dateConverter = new DateConverter();
    	for(HistoricActivityInstance his : historyActivitList){
    		traceItemResponse = new TraceItemResponse();
    		traceItemResponse.setActivityId(his.getActivityId());
    		traceItemResponse.setActivityName(his.getActivityName());
            traceItemResponse.setTaskId(his.getTaskId());
            traceItemResponse.setAssignee(his.getAssignee());
    		traceItemResponse.setStartTime(dateConverter.doConvertToString(his.getStartTime()));
    		traceItemResponse.setEndTime(dateConverter.doConvertToString(his.getEndTime()));
    		traceItemResponse.setIsExecuted(true);  //默认为已经执行
    		traceItemResponse.setIsCurrentActivity(false); //默认为非当前节点
    		//isExecuted 根据起始时间可以判断
    		if(his.getEndTime()==null){
    			traceItemResponse.setIsExecuted(false);
    			traceItemResponse.setIsCurrentActivity(true);
    		}
    		traceItemResponseList.add(traceItemResponse);
    	}
    	TraceResponse traceResponse = new TraceResponse();
    	traceResponse.setResult(traceItemResponseList);
    	return traceResponse;
    }
    
    //合并历史轨迹
    private TraceResponse mergeProcessTrace(TraceResponse hisProcessTrace,List<ActivityImpl> activitiList){
    	//校验历史轨迹列表，如果为空则进行初始化
    	if(hisProcessTrace==null||hisProcessTrace.getResult()==null){
    		hisProcessTrace = new TraceResponse();
    		hisProcessTrace.setResult(new ArrayList<TraceItemResponse>());
    	}
    	
    	//用于复制，避免多次定义
    	List<TraceItemResponse> resultTemp = new ArrayList<>();
    	resultTemp.addAll(hisProcessTrace.getResult());
    	
    	//所有节点都和历史轨迹列表进行比较
    	TraceItemResponse traceItemResponseTemp = null;
    	for(ActivityImpl activityImpl : activitiList){
    		boolean isContain = false;
    		for(TraceItemResponse traceItemResponse : hisProcessTrace.getResult()){
    			if(traceItemResponse.getActivityId().equals(activityImpl.getId())){
    				isContain = true;
    			}
    		}
    		
    		if(!isContain){
    			traceItemResponseTemp = new TraceItemResponse();
                traceItemResponseTemp.setActivityId(activityImpl.getId());
                traceItemResponseTemp.setActivityName((String)(activityImpl.getProperty("name")));
    			traceItemResponseTemp.setIsCurrentActivity(false);
    			traceItemResponseTemp.setIsExecuted(false);
    			resultTemp.add(traceItemResponseTemp);
    		}
    	}
    	//先进行排序 再插入list
        resultTemp.sort(TraceItemResponse::compareTo);
    	hisProcessTrace.setResult(resultTemp);
    	return hisProcessTrace;
    }
    
    /**
     * 流程跟踪图
     *
     * @param processInstanceId 流程实例ID
     * @return 封装了各种节点信息
     */
    public List<Map<String, Object>> traceProcess(String processInstanceId) throws Exception {
        Execution execution = runtimeService.createExecutionQuery().executionId(processInstanceId).singleResult();//执行实例
        if (execution == null){
            return traceHistoryProcess(processInstanceId);
        }
        Object property = PropertyUtils.getProperty(execution, "activityId");
        String activityId = "";
        if (property != null) {
            activityId = property.toString();
        }
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
                .singleResult();
        ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                .getDeployedProcessDefinition(processInstance.getProcessDefinitionId());

        List<HistoricActivityInstance> historyActivitList =  historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).list();

        List<ActivityImpl> activitiList = processDefinition.getActivities();//获得当前任务的所有节点


        List<Map<String, Object>> activityInfos = new ArrayList<Map<String, Object>>();
        for (ActivityImpl activity : activitiList) {

            boolean currentActiviti = false;
            @SuppressWarnings("unused")
			String id = activity.getId();

            // 当前节点
            currentActiviti = isHistoryOrCurrent(activity,activityId,historyActivitList);

            Map<String, Object> activityImageInfo = packageSingleActivitiInfo(activity,  currentActiviti,historyActivitList);
            activityInfos.add(activityImageInfo);
        }

        return activityInfos;
    }

    
    
    /**
     * 流程跟踪图
     *
     * @param processInstanceId 流程实例ID
     * @return 封装了各种节点信息
     */
    public List<Map<String, Object>> traceHistoryProcess(String processInstanceId) throws Exception {

        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

        ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                .getDeployedProcessDefinition(historicProcessInstance.getProcessDefinitionId());

        List<HistoricActivityInstance> historyActivitList =  historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).list();

        List<ActivityImpl> activitiList = processDefinition.getActivities();//获得当前任务的所有节点


        List<Map<String, Object>> activityInfos = new ArrayList<Map<String, Object>>();
        for (ActivityImpl activity : activitiList) {
            boolean currentActiviti = false;
            @SuppressWarnings("unused")
			String id = activity.getId();

            Map<String, Object> activityImageInfo = packageSingleActivitiInfo(activity,  currentActiviti,historyActivitList);
            activityInfos.add(activityImageInfo);
        }

        return activityInfos;
    }

    private boolean isHistoryOrCurrent(ActivityImpl activity,String currentId,List<HistoricActivityInstance> highLightedActivitList){
        boolean isHistoryOrCurrent = false;
        String id = activity.getId();
        if (id.equals(currentId)) {
            return true;
        }

        for(HistoricActivityInstance history:highLightedActivitList){
            if(history.getActivityId().equals(id)){
                return true;
            }
        }
        return isHistoryOrCurrent;
    }
    



    /**
     * 封装输出信息，包括：当前节点的X、Y坐标、变量信息、任务类型、任务描述
     *
     * @param activity
     * //@param processInstance
     * @param currentActiviti
     * @return
     */
    @SuppressWarnings("unused")
	private Map<String, Object> packageSingleActivitiInfo(ActivityImpl activity,
                                                          boolean currentActiviti, List<HistoricActivityInstance> historyActivitList) throws Exception {
        Map<String, Object> vars = new HashMap<String, Object>();
        Map<String, Object> activityInfo = new HashMap<String, Object>();
        activityInfo.put("currentActiviti", currentActiviti);
        setPosition(activity, activityInfo);
        setWidthAndHeight(activity, activityInfo);

        Map<String, Object> properties = activity.getProperties();
        vars.put(TASK_TYPE, WorkflowUtils.parseToZhType(properties.get("type").toString()));

        ActivityBehavior activityBehavior = activity.getActivityBehavior();
        logger.debug("activityBehavior={}", activityBehavior);
        if (activityBehavior instanceof UserTaskActivityBehavior) {

            Task currentTask = null;



			/*
			 * 当前任务的分配角色
			 */
            UserTaskActivityBehavior userTaskActivityBehavior = (UserTaskActivityBehavior) activityBehavior;
            TaskDefinition taskDefinition = userTaskActivityBehavior.getTaskDefinition();
            Set<Expression> candidateGroupIdExpressions = taskDefinition.getCandidateGroupIdExpressions();
            if (!candidateGroupIdExpressions.isEmpty()) {

                // 任务的处理角色
                setTaskGroup(vars, candidateGroupIdExpressions);

                // 当前处理人
                if (currentTask != null) {
                    setCurrentTaskAssignee(vars, currentTask);
                }
            }
            DateConverter dateConverter = new DateConverter();
            for (HistoricActivityInstance historicActivityInstance:historyActivitList){
                if (historicActivityInstance.getActivityId().equals(activity.getId())){
                    vars.put(ASSIGNEE, historicActivityInstance.getAssignee());
                    vars.put(START_TIME, dateConverter.convert(String.class,historicActivityInstance.getStartTime()));
                    vars.put(END_TIME, dateConverter.convert(String.class,historicActivityInstance.getEndTime()));
                }
            }
        }

        vars.put(DOCUMENTATION, properties.get("documentation"));

        String description = activity.getProcessDefinition().getDescription();
        vars.put(DESCRIPTION, description);

        logger.debug("trace variables: {}", vars);
        activityInfo.put("vars", vars);
        return activityInfo;
    }

    private void setTaskGroup(Map<String, Object> vars, Set<Expression> candidateGroupIdExpressions) {
        String roles = "";
        for (Expression expression : candidateGroupIdExpressions) {
            String expressionText = expression.getExpressionText();
            //String roleName = identityService.createGroupQuery().groupId(expressionText).singleResult().getName();
            roles += expressionText;
        }
        vars.put(ROLES, roles);
    }

    /**
     * 设置当前处理人信息
     *
     * @param vars
     * @param currentTask
     */
    private void setCurrentTaskAssignee(Map<String, Object> vars, Task currentTask) {
        String assignee = currentTask.getAssignee();
        if (assignee != null) {
            User assigneeUser = identityService.createUserQuery().userId(assignee).singleResult();
            String userInfo = assigneeUser.getFirstName() + " " + assigneeUser.getLastName();
            vars.put(USER_INFO, userInfo);
        }
    }

    /**
     * 获取当前节点信息
     *
     * @param processInstance
     * @return
     */
    @SuppressWarnings("unused")
	private Task getCurrentTaskInfo(ProcessInstance processInstance) {
        Task currentTask = null;
        try {
            String activitiId = (String) PropertyUtils.getProperty(processInstance, "activityId");
            logger.debug("current activity id: {}", activitiId);

            currentTask = taskService.createTaskQuery().processInstanceId(processInstance.getId()).taskDefinitionKey(activitiId)
                    .singleResult();
            logger.debug("current task for processInstance: {}", ToStringBuilder.reflectionToString(currentTask));

        } catch (Exception e) {
            logger.error("can not get property activityId from processInstance: {}", processInstance);
        }
        return currentTask;
    }

    /**
     * 设置宽度、高度属性
     *
     * @param activity
     * @param activityInfo
     */
    private void setWidthAndHeight(ActivityImpl activity, Map<String, Object> activityInfo) {
        activityInfo.put("width", activity.getWidth());
        activityInfo.put("height", activity.getHeight());
    }

    /**
     * 设置坐标位置
     *
     * @param activity
     * @param activityInfo
     */
    private void setPosition(ActivityImpl activity, Map<String, Object> activityInfo) {
        activityInfo.put("x", activity.getX());
        activityInfo.put("y", activity.getY());
    }
}
