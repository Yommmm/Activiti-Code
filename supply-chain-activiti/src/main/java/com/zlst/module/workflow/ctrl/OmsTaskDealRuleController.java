package com.zlst.module.workflow.ctrl;

import com.zlst.module.workflow.bean.OmsTaskDealRule;
import com.zlst.module.workflow.service.OmsTaskDealRuleService;
import com.zlst.module.workflow.vo.OmsTaskDealRuleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zlst.database.core.ctrl.QueryAndOperateCtrl;

import com.zlst.param.ObjectToResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(value = "员工API")
@RestController
@RequestMapping("/activiti/v1/omsTaskDealRule")
@EnableAutoConfiguration
public class OmsTaskDealRuleController {
// public class OmsTaskDealRuleController extends QueryAndOperateCtrl<OmsTaskDealRule, OmsTaskDealRuleService> {
	
    private static final Logger logger = LoggerFactory.getLogger(OmsTaskDealRuleController.class);
    
    @Autowired
    private OmsTaskDealRuleService omsTaskDealRuleService;
    
    /**
	 * 增
	 * @param omsTaskDealRule XXXX
	 */
    @PostMapping("/save")
	@ApiOperation(value = "新增 TODO description", notes="新增 TODO description API", produces = "application/json")
	@ApiImplicitParam(name = "omsTaskDealRule", value = "TODO description", dataType = "OmsTaskDealRule", required = true)
    public Object saveOmsTaskDealRule(@RequestBody OmsTaskDealRule omsTaskDealRule) throws Exception {
    	return ObjectToResult.getResult(omsTaskDealRuleService.saveOmsTaskDealRule(omsTaskDealRule));
    }
    
    /**
	 * 删
	 * @param omsTaskDealRuleId XXXX
	 */
    @DeleteMapping("/{omsTaskDealRuleId}")
    @ApiOperation(value = "删除 TODO description", notes="删除 TODO description API", produces = "application/json")
    @ApiImplicitParam(name = "omsTaskDealRuleId", value = "TODO description", dataType = "String", paramType = "path")
    public Object delOmsTaskDealRule(@PathVariable String omsTaskDealRuleId) throws Exception {
    	return ObjectToResult.getResult(omsTaskDealRuleService.delOmsTaskDealRule(omsTaskDealRuleId));
    }
    
    /**
	 * 改
	 * @param omsTaskDealRuleId XXXX
	 * @param omsTaskDealRule XXXX
	 */
    @PutMapping("/{omsTaskDealRuleId}")
	@ApiOperation(value = "更新 TODO description", notes="更新 TODO description API", produces = "application/json")
	@ApiImplicitParams({ 
    	@ApiImplicitParam(name = "omsTaskDealRuleId", value = "TODO description", dataType = "String", paramType = "path") ,
    	@ApiImplicitParam(name = "omsTaskDealRule", value = "TODO description", dataType = "OmsTaskDealRule", paramType = "body")})
    public Object updateOmsTaskDealRule(@PathVariable String omsTaskDealRuleId, @RequestBody OmsTaskDealRule omsTaskDealRule) throws Exception {
    	return ObjectToResult.getResult(omsTaskDealRuleService.updateOmsTaskDealRule(omsTaskDealRuleId, omsTaskDealRule));
    }
    
    /**
	 * 查
	 * @param omsTaskDealRuleId XXXX
	 */
    @GetMapping("/{omsTaskDealRuleId}")
    @ApiOperation(value = "查询 TODO description", notes="查询 TODO description API", produces = "application/json")
    @ApiImplicitParam(name = "omsTaskDealRuleId", value = "TODO description", dataType = "String", paramType = "path")
    public Object getOmsTaskDealRule(@PathVariable String omsTaskDealRuleId) throws Exception {
    	return ObjectToResult.getResult(omsTaskDealRuleService.getOmsTaskDealRule(omsTaskDealRuleId));
    }
    
    /**
	 * 查
	 * @param omsTaskDealRuleVO XXXX
	 */
    @PostMapping("/queryWithPage")
    @ApiOperation(value = "查询 TODO description", notes="查询 TODO description API", produces = "application/json")
    @ApiImplicitParam(name = "omsTaskDealRuleVO", value = "TODO description", dataType = "OmsTaskDealRuleVO", paramType = "body")
    public Object getOmsTaskDealRule(@RequestBody OmsTaskDealRuleVO omsTaskDealRuleVO) throws Exception {
    	return ObjectToResult.getResult(omsTaskDealRuleService.getOmsTaskDealRule(omsTaskDealRuleVO));
    }
    
}