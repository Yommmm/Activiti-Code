package com.zlst.module.workflow.ctrl;

import com.zlst.common.CommonConstants;
import com.zlst.module.workflow.service.ActivitiService;
import com.zlst.param.ObjectToResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.image.ProcessDiagramGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by 凌鹏 170119 on 2019/2/11.
 * 工作流流程图相关控制类
 */
@Api(value = "工作流流程图API")
@RestController
@RequestMapping("/activiti/v1/workflow")
@EnableAutoConfiguration
public class ActivitiCtrl {
    private static final Logger logger = LoggerFactory.getLogger(ActivitiCtrl.class);

    @Autowired
    private ActivitiService activitiService;

    /**
     * 通过流程ID获取流程图输出
     * 流程图样式类型,BRIGHT 系统高亮图片，dark 系统非高亮图片
     * @param resourceType      图片类型(BRIGHT|DARK)
     * @param processInstanceId 流程实例ID
     * @param response
     * @throws Exception
     */
    @GetMapping(value = "/resource/process-instance")
    @ApiOperation(value = "通过流程实例ID获取流程图输出", notes="通过流程实例ID获取流程图输出 API，以字节输出图片", produces = MediaType.IMAGE_PNG_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "resourceType", value = "图片类型(BRIGHT|DARK)", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "processInstanceId", value = "流程实例ID", dataType = "String", paramType = "query")})
    public void loadProcessInstance(@RequestParam("type") String resourceType,
                                    @RequestParam("pid") String processInstanceId,
                                    HttpServletResponse response) throws Exception {
        activitiService.loadProcessInstance(resourceType,processInstanceId,response);
    }


    /**
     * 通过流程实例ID获取流程图输出
     * @param processDefinitionId 流程实例ID
     * @param response
     * @throws Exception
     */
    @GetMapping(value = "/resource/process-definition")
    @ApiOperation(value = "通过流程定义ID获取流程图输出", notes="通过流程定义ID获取流程图输出 API，以字节输出图片", produces = MediaType.IMAGE_PNG_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processDefinitionId", value = "流程定义ID", dataType = "String", paramType = "query")})
    public void loadProcessDefinition(@RequestParam("pdid") String processDefinitionId,
                                    HttpServletResponse response) throws Exception {
        activitiService.loadProcessDefinition(processDefinitionId,response);
    }


    /**
     * 输出当前流程所有节点信息
     *
     * @param processDefinitionId
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "通过流程定义ID输出当前流程跟踪轨迹信息", notes="通过流程定义ID输出当前流程跟踪轨迹信息", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams(@ApiImplicitParam(name = "processDefinitionId", value = "流程定义ID", dataType = "String", paramType = "query"))
    @GetMapping(value = "/process/traceAll")
    public Object traceAllProcess(@RequestParam("pdid") String processDefinitionId) throws Exception {
        List<Map<String, Object>> activityInfos = activitiService.traceAllProcess(processDefinitionId);
        return ObjectToResult.getResult(activityInfos);
    }

    /**
     * 输出当前流程跟踪轨迹信息
     *
     * @param processInstanceId
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "通过流程ID输出当前流程跟踪轨迹信息", notes="通过流程ID输出当前流程跟踪轨迹信息", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams(@ApiImplicitParam(name = "processInstanceId", value = "流程实例ID", dataType = "String", paramType = "query"))
    @GetMapping(value = "/process/trace")
    public Object traceProcess(@RequestParam("pid") String processInstanceId) throws Exception {
        List<Map<String, Object>> activityInfos = activitiService.traceProcess(processInstanceId);
        return ObjectToResult.getResult(activityInfos);
    }

    /**
     * 输出历史流程跟踪轨迹信息
     *
     * @param processInstanceId
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "通过流程ID输出历史流程跟踪轨迹信息", notes="通过流程ID输出历史流程跟踪轨迹信息", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams(@ApiImplicitParam(name = "processInstanceId", value = "流程实例ID", dataType = "String", paramType = "query"))
    @GetMapping(value = "/process/traceHis")
    public Object traceHisProcess(@RequestParam("pid") String processInstanceId) throws Exception {
        List<Map<String, Object>> activityInfos = activitiService.traceProcess(processInstanceId);
        return ObjectToResult.getResult(activityInfos);
    }



}
