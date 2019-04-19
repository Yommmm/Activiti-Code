//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//
package org.activiti.rest.service.api.runtime.process;

import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.RuntimeServiceImpl;
import org.activiti.engine.impl.cmd.StartProcessInstanceActiveCmd;
import org.activiti.engine.impl.cmd.StartProcessInstanceSuppendCmd;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.rest.service.api.engine.variable.RestVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  新增的Rest服务，将原有的启动流程服务拆分为实例化和启动Rest服务
 */
@RestController
public class ProcessInstanceSplitResource extends BaseProcessInstanceResource {

    @Autowired
    protected HistoryService historyService;

    public ProcessInstanceSplitResource() {
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
     * @param processInstanceId
     * @param actionRequest
     * @param request
     * @return
     */
    @RequestMapping(
            value = {"/runtime/process-instances/active/{processInstanceId}"},
            method = {RequestMethod.PUT},
            produces = {"application/json"}
    )
    public ProcessInstanceResponse performProcessInstanceAction(@PathVariable String processInstanceId, @RequestBody ProcessInstanceActionRequest actionRequest, HttpServletRequest request) {
        try {
            ProcessInstance instance = null;
            //Modify by songcj
            instance = ((RuntimeServiceImpl) runtimeService).getCommandExecutor().execute(new StartProcessInstanceActiveCmd(processInstanceId));
            //Modify end
            return restResponseFactory.createProcessInstanceResponse(instance);
        } catch(ActivitiObjectNotFoundException aonfe) {
            throw new ActivitiIllegalArgumentException(aonfe.getMessage(), aonfe);
        }
    }

}
