package com.zlst.module.workflow.service;

import com.zlst.business.activiti.util.DateConverter;
import com.zlst.business.activiti.util.WorkflowUtils;
import com.zlst.common.CommonConstants;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.*;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.*;

/**
 * Created by 凌鹏 170119 on 2019/2/11.
 */
@Service
public class ActivitiService {
    private static final Logger logger = LoggerFactory.getLogger(ActivitiService.class);

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private ProcessEngineConfiguration processEngineConfiguration;

    @Autowired
    private IdentityService identityService;

    /**
     * 通过流程ID获取流程图输出
     * 流程图样式类型,BRIGHT 系统高亮图片，dark 系统非高亮图片
     * @param resourceType      资源类型(BRIGHT|DARK)
     * @param processInstanceId 流程实例ID
     * @param response
     * @throws Exception
     */
    public void loadProcessInstance(@RequestParam("type") String resourceType, @RequestParam("pid") String processInstanceId, HttpServletResponse response)
            throws Exception {
        InputStream imageStream = null;
        try{
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            String processDefinitionId ;
            if (processInstance == null){
                HistoricProcessInstance hisprocessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
                processDefinitionId = hisprocessInstance.getProcessDefinitionId();
            } else {
                processDefinitionId = processInstance.getProcessDefinitionId();
            }

            ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity)repositoryService.getProcessDefinition(processDefinitionId);
            ProcessDiagramGenerator diagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

            //默认非高亮流程图
            if(resourceType.equals(CommonConstants.BRIGHT)){
                List<HistoricActivityInstance> highLightedActivitList =  historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).list();
                //高亮环节id集合
                List<String> highLightedActivitis = new ArrayList<String>();
                for(HistoricActivityInstance tempActivity : highLightedActivitList){
                    String activityId = tempActivity.getActivityId();
                    highLightedActivitis.add(activityId);
                }
                //高亮线路id集合
                List<String> highLightedFlows = getHighLightedFlows(processDefinition,highLightedActivitList);

                imageStream = diagramGenerator.generateDiagram(bpmnModel, "png", highLightedActivitis,highLightedFlows,"宋体","宋体","宋体",null,1.0);
            }else {
                imageStream = diagramGenerator.generateDiagram(bpmnModel, "png","宋体","宋体","宋体",null,1.0);
            }

            byte[] b = new byte[1024];
            int len = -1;
            while ((len = imageStream.read(b, 0, 1024)) != -1) {
                response.getOutputStream().write(b, 0, len);
            }
        }catch(Exception e){
            e.printStackTrace();
            logger.error("流程图生成失败：{}",e.getMessage());
        }finally {
            imageStream.close();
        }
    }



    /**
     * 获取需要高亮的线
     * @param processDefinitionEntity
     * @param historicActivityInstances
     * @return
     */
    private List<String> getHighLightedFlows(
            ProcessDefinitionEntity processDefinitionEntity,
            List<HistoricActivityInstance> historicActivityInstances) {
        List<String> highFlows = new ArrayList<String>();// 用以保存高亮的线flowId
        for (int i = 0; i < historicActivityInstances.size() - 1; i++) {// 对历史流程节点进行遍历
            ActivityImpl activityImpl = processDefinitionEntity
                    .findActivity(historicActivityInstances.get(i)
                            .getActivityId());// 得到节点定义的详细信息
            List<ActivityImpl> sameStartTimeNodes = new ArrayList<ActivityImpl>();// 用以保存后需开始时间相同的节点
            ActivityImpl sameActivityImpl1 = processDefinitionEntity
                    .findActivity(historicActivityInstances.get(i + 1)
                            .getActivityId());
            // 将后面第一个节点放在时间相同节点的集合里
            sameStartTimeNodes.add(sameActivityImpl1);
            for (int j = i + 1; j < historicActivityInstances.size() - 1; j++) {
                HistoricActivityInstance activityImpl1 = historicActivityInstances
                        .get(j);// 后续第一个节点
                HistoricActivityInstance activityImpl2 = historicActivityInstances
                        .get(j + 1);// 后续第二个节点
                if (activityImpl1.getStartTime().equals(
                        activityImpl2.getStartTime())) {
                    // 如果第一个节点和第二个节点开始时间相同保存
                    ActivityImpl sameActivityImpl2 = processDefinitionEntity
                            .findActivity(activityImpl2.getActivityId());
                    sameStartTimeNodes.add(sameActivityImpl2);
                } else {
                    // 有不相同跳出循环
                    break;
                }
            }
            List<PvmTransition> pvmTransitions = activityImpl
                    .getOutgoingTransitions();// 取出节点的所有出去的线
            for (PvmTransition pvmTransition : pvmTransitions) {
                // 对所有的线进行遍历
                ActivityImpl pvmActivityImpl = (ActivityImpl) pvmTransition
                        .getDestination();
                // 如果取出的线的目标节点存在时间相同的节点里，保存该线的id，进行高亮显示
                if (sameStartTimeNodes.contains(pvmActivityImpl)) {
                    highFlows.add(pvmTransition.getId());
                }
            }
        }
        return highFlows;
    }



    /**
     * 流程跟踪图信息
     *
     * @param processInstanceId 流程实例ID
     * @return 封装了各种节点信息
     */
    public List<Map<String, Object>> traceProcess(String processInstanceId) throws Exception {
        //执行实例
        Execution execution = runtimeService.createExecutionQuery().executionId(processInstanceId).singleResult();
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

        //查询历史轨迹节点
        List<HistoricActivityInstance> historyActivitList =  historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).list();
        //查询历史轨迹任务
        List<HistoricTaskInstance> taskActivitList = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).list();
        //获得当前任务的所有节点
        List<ActivityImpl> activitiList = processDefinition.getActivities();


        List<Map<String, Object>> activityInfos = new ArrayList<Map<String, Object>>();
        for (ActivityImpl activity : activitiList) {
            Map<String, Object> map = new HashMap<>();
            boolean currentActiviti = false;
            String id = activity.getId();

            // 当前节点
            currentActiviti = isHistoryOrCurrent(activity,activityId,historyActivitList);

            Map<String, Object> activityImageInfo = packageSingleActivitiInfo(activity,  currentActiviti,historyActivitList);
            activityImageInfo.put("activitiId",id);
            activityInfos.add(activityImageInfo);
        }

        return activityInfos;
    }


    /**
     * 历史流程跟踪图
     *
     * @param processInstanceId 流程实例ID
     * @return 封装了各种节点信息
     */
    public List<Map<String, Object>> traceHistoryProcess(String processInstanceId) throws Exception {

        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

        ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                .getDeployedProcessDefinition(historicProcessInstance.getProcessDefinitionId());

        //查询历史轨迹
        List<HistoricActivityInstance> historyActivitList =  historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).list();
        //查询历史轨迹任务
        List<HistoricTaskInstance> taskActivitList = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).list();
        //获得当前任务的所有节点
        List<ActivityImpl> activitiList = processDefinition.getActivities();

        List<Map<String, Object>> activityInfos = new ArrayList<Map<String, Object>>();
        for (ActivityImpl activity : activitiList) {
            boolean currentActiviti = false;
            String id = activity.getId();

            Map<String, Object> activityImageInfo = packageSingleActivitiInfo(activity,  currentActiviti,historyActivitList);
            activityInfos.add(activityImageInfo);
        }

        return activityInfos;
    }


    /**
     * 封装输出信息，包括：当前节点的X、Y坐标、变量信息、任务类型、任务描述
     *
     * @param activity
     * @param currentActiviti
     * @param currentActiviti
     * @return
     */
    private Map<String, Object> packageSingleActivitiInfo(ActivityImpl activity,
                                                          boolean currentActiviti, List<HistoricActivityInstance> historyActivitList) throws Exception {
        Map<String, Object> vars = new HashMap<String, Object>();
        Map<String, Object> activityInfo = new HashMap<String, Object>();
        activityInfo.put("currentActiviti", currentActiviti);
        setPosition(activity, activityInfo);
        setWidthAndHeight(activity, activityInfo);

        Map<String, Object> properties = activity.getProperties();
        vars.put("任务类型", WorkflowUtils.parseToZhType(properties.get("type").toString()));

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
                    vars.put("执行人", historicActivityInstance.getAssignee());
                    vars.put("开始时间", dateConverter.convert(String.class,historicActivityInstance.getStartTime()));
                    vars.put("结束时间", dateConverter.convert(String.class,historicActivityInstance.getEndTime()));
                }
            }
        }

        vars.put("节点说明", properties.get("documentation"));

        String description = activity.getProcessDefinition().getDescription();
        vars.put("描述", description);

        logger.debug("trace variables: {}", vars);
        activityInfo.put("vars", vars);
        return activityInfo;
    }

    /**
     * 判断是否为当前节点
     * @param activity
     * @param currentId
     * @param highLightedActivitList
     * @return
     */
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


    private void setTaskGroup(Map<String, Object> vars, Set<Expression> candidateGroupIdExpressions) {
        String roles = "";
        for (Expression expression : candidateGroupIdExpressions) {
            String expressionText = expression.getExpressionText();
            //String roleName = identityService.createGroupQuery().groupId(expressionText).singleResult().getName();
            roles += expressionText;
        }
        vars.put("任务所属角色", roles);
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
            vars.put("当前处理人", userInfo);
        }
    }
}
