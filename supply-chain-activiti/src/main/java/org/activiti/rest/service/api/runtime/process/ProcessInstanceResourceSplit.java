//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//
package org.activiti.rest.service.api.runtime.process;

import com.zlst.exception.ZLSTException;
import com.zlst.proxy.vo.ActiviteProcessRequest;
import com.zlst.common.ExceptionConstants;
import com.zlst.utils.ResultUtil;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.RuntimeServiceImpl;
import org.activiti.engine.impl.cmd.StartProcessInstanceActiveCmd;
import org.activiti.engine.impl.cmd.StartProcessInstanceSuppendCmd;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.rest.service.api.engine.variable.RestVariable;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  新增的Rest服务，将原有的启动流程服务拆分为实例化和启动Rest服务
 */
@RestController
@CrossOrigin
public class ProcessInstanceResourceSplit extends BaseProcessInstanceResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessInstanceResource.class);

    @Autowired
    protected HistoryService historyService;

    @Autowired
    protected RepositoryService repositoryService;

    @Autowired
    ProcessEngineConfiguration processEngineConfiguration;

    public ProcessInstanceResourceSplit() {
    }


    @RequestMapping(value = "/resource/picture/processInstance/{processInstanceId}", method = RequestMethod.GET, produces="application/json")
    public Object getProcInstanceTracePicture(@PathVariable String processInstanceId){
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        String processDefinitionId ;
        if (processInstance == null){
            HistoricProcessInstance hisprocessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            if(null == hisprocessInstance){
                LOG.error("the processInstance and hisprocessInstance is null.");
                return "";
            }
            processDefinitionId = hisprocessInstance.getProcessDefinitionId();
        } else {
            processDefinitionId = processInstance.getProcessDefinitionId();
            LOG.debug("processDefinitionId:",processDefinitionId);
        }

        ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity)repositoryService.getProcessDefinition(processDefinitionId);
        ProcessDiagramGenerator diagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

        List<HistoricActivityInstance> highLightedActivitList =  historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).list();
        //高亮环节id集合
        List<String> highLightedActivitis = new ArrayList<String>();
        for(HistoricActivityInstance tempActivity : highLightedActivitList){
            String activityId = tempActivity.getActivityId();
            highLightedActivitis.add(activityId);
        }
        //高亮线路id集合
        List<String> highLightedFlows = getHighLightedFlows(processDefinition,highLightedActivitList);

        InputStream imageStream = diagramGenerator.generateDiagram(bpmnModel, "png", highLightedActivitis,highLightedFlows,"宋体","宋体","宋体",null,1.0);

        return ResultUtil.buildNormalResult(encodeToString(imageStream));
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

    public static String encodeToString(InputStream in){
        byte[] data = null;
        //读取图片字节数组
        try {
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (Exception e) {
            throw new ZLSTException(ExceptionConstants.ORDERID_IS_NULL);//TODO
        }
        //对字节数组Base64编码
        String result = Base64.encodeBase64String(data);

        LOG.debug("****:",result);

        return result;
    }

    /**
     * 创建流程实例但不启动
     * 该服务初始是来自ProcessInstanceCollectionResource.createProcessInstance
     * 将调用的startProcessInstanceById等方法进行了替换
     * 参数和调用方式等和原方法保持一致
     * @param request
     * @param httpRequest
     * @param response
     * @return
     */
    @RequestMapping(value="/runtime/process-instances/create/", method = RequestMethod.POST, produces="application/json")
    public ProcessInstanceResponse createProcessInstance(@RequestBody ProcessInstanceCreateRequest request,
                                                         HttpServletRequest httpRequest, HttpServletResponse response) {

        if (request.getProcessDefinitionId() == null && request.getProcessDefinitionKey() == null && request.getMessage() == null) {
            throw new ActivitiIllegalArgumentException("Either processDefinitionId, processDefinitionKey or message is required.");
        }

        int paramsSet = ((request.getProcessDefinitionId() != null) ? 1 : 0)
                + ((request.getProcessDefinitionKey() != null) ? 1 : 0)
                + ((request.getMessage() != null) ? 1 : 0);

        if (paramsSet > 1) {
            throw new ActivitiIllegalArgumentException("Only one of processDefinitionId, processDefinitionKey or message should be set.");
        }

        if (request.isCustomTenantSet()) {
            // Tenant-id can only be used with either key or message
            if(request.getProcessDefinitionId() != null) {
                throw new ActivitiIllegalArgumentException("TenantId can only be used with either processDefinitionKey or message.");
            }
        }

        Map<String, Object> startVariables = null;
        if (request.getVariables() != null) {
            startVariables = new HashMap<String, Object>();
            for (RestVariable variable : request.getVariables()) {
                if (variable.getName() == null) {
                    throw new ActivitiIllegalArgumentException("Variable name is required.");
                }
                startVariables.put(variable.getName(), restResponseFactory.getVariableValue(variable));
            }
        }

        // Actually start the instance based on key or id
        try {
            ProcessInstance instance = null;
            //Modify by songcj
            instance = ((RuntimeServiceImpl) runtimeService).getCommandExecutor().execute(new StartProcessInstanceSuppendCmd( request.getProcessDefinitionKey(), request.getProcessDefinitionId(), request.getBusinessKey(), startVariables,request.getTenantId()));
            //Modify end
            response.setStatus(HttpStatus.CREATED.value());

            //Added by Ryan Johnston
            if (request.getReturnVariables()) {
                Map<String, Object> runtimeVariableMap = null;
                List<HistoricVariableInstance> historicVariableList = null;
                if (instance.isEnded()) {
                    historicVariableList = historyService.createHistoricVariableInstanceQuery()
                            .processInstanceId(instance.getId())
                            .list();
                } else {
                    runtimeVariableMap = runtimeService.getVariables(instance.getId());
                }
                return restResponseFactory.createProcessInstanceResponse(instance, true,
                        runtimeVariableMap, historicVariableList);

            } else {
                return restResponseFactory.createProcessInstanceResponse(instance);
            }
            //End Added by Ryan Johnston

            //Removed by Ryan Johnston (obsolete given the above).
            //return factory.createProcessInstanceResponse(this, instance);
        } catch(ActivitiObjectNotFoundException aonfe) {
            throw new ActivitiIllegalArgumentException(aonfe.getMessage(), aonfe);
        }
    }

    /**
     * 启动流程实例
     * @param actionRequest
     * @return
     */
    @RequestMapping(
            value = {"/runtime/process-instances/active"},
            method = {RequestMethod.PUT},
            produces = {"application/json"}
    )
    public ProcessInstanceResponse performProcessInstanceAction(@RequestBody ActiviteProcessRequest actionRequest) {
        try {
            ProcessInstance instance = null;
            //Modify by songcj
            instance = ((RuntimeServiceImpl) runtimeService).getCommandExecutor().execute(new StartProcessInstanceActiveCmd(actionRequest.getProcessInstanceId()));
            //Modify end
            return restResponseFactory.createProcessInstanceResponse(instance);
        } catch(ActivitiObjectNotFoundException aonfe) {
            throw new ActivitiIllegalArgumentException(aonfe.getMessage(), aonfe);
        }
    }

}
