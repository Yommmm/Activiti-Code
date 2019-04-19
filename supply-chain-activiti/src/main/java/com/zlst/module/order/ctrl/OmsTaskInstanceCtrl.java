package com.zlst.module.order.ctrl;

import com.alibaba.fastjson.JSONObject;
import com.zlst.database.core.ctrl.QueryAndOperateCtrl;
import com.zlst.module.order.bean.OmsTaskDefine;
import com.zlst.module.order.bean.OmsTaskInstance;
import com.zlst.module.order.bean.OmsOrder;
import com.zlst.module.order.bean.vo.InitOmsTaskInstanceRequest;
import com.zlst.module.order.bean.vo.InitOmsTaskInstanceResponse;
import com.zlst.module.order.service.OmsTaskDefineService;
import com.zlst.module.order.service.OmsTaskInstanceService;
import com.zlst.module.order.service.OmsOrderService;
import com.zlst.utils.ResultUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by songcj on 2017/9/20.
 */
@RestController
@RequestMapping("/omsTaskInstance")
@EnableAutoConfiguration
public class OmsTaskInstanceCtrl extends QueryAndOperateCtrl<OmsTaskInstance, OmsTaskInstanceService> {

    private final static Logger LOG = LoggerFactory.getLogger(OmsTaskInstanceCtrl.class);

    @Autowired
    public OmsTaskInstanceService omsTaskInstanceService;
    @Autowired
    public OmsTaskDefineService omsTaskDefineService;
    @Autowired
    public OmsOrderService omsOrderService;

    @PostMapping(value = "/saveOmsTaskInstance")
    public Object saveOmsTaskInstance(@RequestBody InitOmsTaskInstanceRequest initOmsTaskInstanceRequest) throws Exception {
        LOG.debug("request: ", JSONObject.toJSONString(initOmsTaskInstanceRequest));
        //1.根据输入信息到表InfTaskDefine中查找相关信息
        //检查输入必须包含项目ProcDefId和TaskDefId
        if (initOmsTaskInstanceRequest == null || StringUtils.isBlank(initOmsTaskInstanceRequest.getProcDefId()) ||
                StringUtils.isBlank(initOmsTaskInstanceRequest.getTaskDefId())) {
            return null;
        }
        OmsTaskInstance omsTaskInstance = new OmsTaskInstance();
        OmsTaskDefine omsTaskDefine = new OmsTaskDefine();
        BeanUtils.copyProperties(initOmsTaskInstanceRequest, omsTaskInstance);
        omsTaskDefine.setProcDefId(omsTaskInstance.getProcDefId());
        omsTaskDefine.setTaskDefId(omsTaskInstance.getTaskDefId());
        //开始执行查询
        omsTaskDefine = omsTaskDefineService.queryInfTaskDefineByProcessAndTaskId(omsTaskDefine);
        if (omsTaskDefine == null) {
            LOG.error("the omsTaskDefine is null.");
            return ResultUtil.buildNormalResult();
        }
//        select * from oms_order where process_inst_id=:processInstId limit 1
//        OmsOrder omsOrder = omsOrderService.queryOmsOrderbyProcessInstId(omsTaskInstance.getProcInstId());

//        Map<String, Object> params = new HashMap<>();
//        params.put("processInstId", omsTaskInstance.getProcInstId());
//        List<OmsOrder> omsOrders = omsOrderService.hqlQuery("from OmsOrder o where o.processInstId=:processInstId", params);
//
//        if (omsOrders == null) {
//            LOG.error("omsOrders list is empty.");
//            return null;
//        }
//        LOG.debug("omsOrders size :", omsOrders.size());
//        OmsOrder omsOrder = omsOrders.get(0);
        OmsOrder omsOrder = omsOrderService.queryOmsOrderbyOrderId(omsTaskInstance.getOrderId());
        //2.根据infTaskDefine填充infTaskInstance
        omsTaskInstance.setTaskDefineId(omsTaskDefine.getId()); //任务工单定义标识
        omsTaskInstance.setOrderId(omsOrder.getOrderId());      //订单标识
        omsTaskInstance.setOrderSourceId(omsOrder.getOrderSourceId());
        //角色或用户信息，现在先只取ID
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
        LOG.debug("createOrgId: ", createOrgId);
        omsTaskInstance.setCreateOrgId(createOrgId);//组织ID
        omsTaskInstance.setCreateUserId(omsOrder.getCreatedBy());
        omsTaskInstance.setUrl(omsTaskDefine.getUrl());   //处理页面链接
        LOG.debug(JSONObject.toJSONString(omsTaskInstance));
        //3.插入表InfTaskInstance
        omsTaskInstance = omsTaskInstanceService.save(omsTaskInstance);
        InitOmsTaskInstanceResponse initOmsTaskInstanceResponse = new InitOmsTaskInstanceResponse();
        BeanUtils.copyProperties(omsTaskInstance, initOmsTaskInstanceResponse);
        return initOmsTaskInstanceResponse;
    }

}
