package com.zlst.module.order.ctrl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.zlst.common.ExceptionConstants;
import com.zlst.common.RestConstants;
import com.zlst.database.common.Common;
import com.zlst.database.common.GsonUtil;
import com.zlst.exception.ZLSTException;
import com.zlst.module.order.bean.OmsOrder;
import com.zlst.module.order.bean.OmsOrderTemplateRule;
import com.zlst.module.order.bean.OmsTaskInstance;
import com.zlst.module.order.bean.vo.*;
import com.zlst.module.order.service.*;
import com.zlst.param.*;
import com.zlst.utils.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/orderMgr")
@EnableAutoConfiguration
public class OrderMgrCtrl {

    private final static Logger LOG = LoggerFactory.getLogger(OrderMgrCtrl.class);

    /**
     * 分页查询支持的每页最大记录数
     */
    public static final int PAGE_MAX_NUM = 10000;

    @Autowired
    private OmsOrderService omsOrderService;

    @Autowired
    private OmsTaskInstanceService omsTaskInstService;

    @Autowired
    private OmsOrderTemplateService omsOrderTemplateService;

    @Autowired
    private OrderMgrService orderMgrService;

    @Autowired
    private MyNvativeSqlQueryServ nvativeSqlQueryServ;

    @Autowired
    private OmsTaskInstanceService omsTaskInstanceService;

    @Autowired
    private OmsActivityInstanceService omsActivityInstanceService;

    /**
     * 流程变量获取接口
     */
    @GetMapping(value = "/{orderId}/variables")
    public Object orderVariables(@PathVariable String orderId) throws Exception {
        LOG.debug("orderVariables start,orderId:", orderId);
        //1.根据订单查询流程实例id
        OmsOrder omsOrder = this.omsOrderService.get(orderId);
        OrderVariblesGetReponse result = new OrderVariblesGetReponse();
        if (omsOrder != null && StringUtils.isNotBlank(omsOrder.getProcessInstId())) {
            //2.根据流程id获取流程所有变量
            String resultStr = this.orderMgrService.queryProcessVariables(omsOrder.getProcessInstId());
            if (CommonUtils.isReturnList(resultStr)) {
                List<RestVariableItemVo> itemResponseList = new Gson().fromJson(resultStr, new TypeToken<List<RestVariableItemVo>>() {
                }.getType());
                result.setVariables(itemResponseList);
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("result:", JSONObject.toJSONString(result));
        }
        return result;
    }

    /**
     * 根据订单标识查询订单信息
     *
     * @param orderId
     * @return
     */
    @GetMapping(value = "/order/{orderId}")
    public Object getOrderInfo(@PathVariable String orderId) {
        LOG.debug("orderId: ", orderId);
        OmsOrder omsOrder = null;
        OrderVO result = null;
        try {
            omsOrder = this.omsOrderService.get(orderId);
            if (null != omsOrder) {
                LOG.debug("omsOrder is not null.");
                result = new OrderVO();
                BeanUtils.copyProperties(omsOrder, result);
            }
        } catch (Exception e) {
            LOG.error("occur exception", e);
            return ResultUtil.buildNormalResult();
        }
        return ResultUtil.buildNormalResult(result);
    }


    /**
     * 订单流程操作接口
     *
     * @param orderId
     * @return
     */
    @PutMapping(value = "/order/{orderId}")
    @Transactional
    public Object operateOrderFlow(@PathVariable String orderId,
                                   @RequestBody OrderFlowMessagesRequest orderFlowMessagesRequest) throws Exception {
        LOG.debug("operateOrderFlow start, orderId={}", orderId);
        if (orderFlowMessagesRequest == null || StringUtils.isBlank(orderFlowMessagesRequest.getAction())) {
            throw new ZLSTException(ExceptionConstants.ERRORCODE_100204);
        }
        OmsOrder omsOrder = this.omsOrderService.get(orderId);
        if (omsOrder == null || StringUtils.isBlank(omsOrder.getProcessInstId())) {
            throw new ZLSTException(ExceptionConstants.ERRORCODE_100204, new Object[]{orderId});
        }
        LOG.debug("action :", orderFlowMessagesRequest.getAction());
        //2.根据流程实例ID获取流程executionId的所有节点  如果节点数大于1，且activitiId未传则返回错误信息，如果只有一个节点，则使用该节点
        if ("signal".equalsIgnoreCase(orderFlowMessagesRequest.getAction())) {
            triggerOrderFlow(orderFlowMessagesRequest, omsOrder.getProcessInstId());
        } else if ("cancel".equalsIgnoreCase(orderFlowMessagesRequest.getAction())) {
            cancelOrderFlow(omsOrder, orderFlowMessagesRequest.getReason());
        } else {
            throw new ZLSTException(ExceptionConstants.ERRORCODE_100204, new Object[]{orderId});
        }
        LOG.debug("operateOrderFlow end.");
        return ResultUtil.buildNormalResult();
    }

    private void cancelOrderFlow(OmsOrder omsOrder, String reason) {
        cancelOrderInfo(omsOrder, reason);
        cancelTaskInstanceInfo(omsOrder.getOrderId(), reason);
        cancelProcInstanceInfo(omsOrder.getProcessInstId(), reason);
    }

    private void cancelProcInstanceInfo(String processInstId, String reason) {
        orderMgrService.cancelProcInstance(processInstId, reason);
        //正确时返回空字符串，否则都视为异常
//        if (StringUtils.isNotBlank(result)) {
//            throw new ZLSTException(ExceptionConstants.DELETE_PROCESS_FAILURE);
//        }
    }

    private void cancelTaskInstanceInfo(String orderId, String reason) {
        LOG.debug("cancelTaskInstanceInfo start...");
        List<OmsTaskInstance> taskInstanceList = omsTaskInstanceService.getUnHandledTaskInstance(orderId);
        if (taskInstanceList.size() > 0) {
            for (OmsTaskInstance taskInstance : taskInstanceList) {
                taskInstance.setStatus("3");
                taskInstance.setStatusTime(new Date());
                taskInstance.setTaskComment(reason);
                try {
                    omsTaskInstanceService.toupdate(taskInstance);
                } catch (Exception e) {
                    throw new ZLSTException(ExceptionConstants.SAVE_FAILURE);
                }
            }
        }
        LOG.debug("cancelTaskInstanceInfo end...");
    }

    private void cancelOrderInfo(OmsOrder omsOrder, String reason) {
        LOG.debug("cancelOrderInfo start...");
        omsOrder.setOrderStatus(OrderStatusEnum.CANCEL.getStatus());
        omsOrder.setRemark(reason);
        omsOrder.setModifyTime(new Date());
        omsOrder.setModifiedBy(CommonUtils.getHeadUserId());
        try {
            omsOrderService.toupdate(omsOrder);
        } catch (Exception e) {
            throw new ZLSTException(ExceptionConstants.SAVE_FAILURE);
        }
        LOG.debug("cancelOrderInfo end...");
    }

    private void triggerOrderFlow(OrderFlowMessagesRequest orderFlowMessagesRequest, String processInstanceId) {
        String executionId = getExecutionId(orderFlowMessagesRequest.getActivityId(), processInstanceId);

        ExecutionActionRequest executionActionRequest = buildRequestInfo(orderFlowMessagesRequest);

        this.orderMgrService.callExecutions(executionId, executionActionRequest);
    }

    private String getExecutionId(String activityId, String processInstanceId) {
        LOG.debug("activityId:", activityId);
        String result = this.orderMgrService.queryExecutions(processInstanceId, activityId);
        if (!CommonUtils.isSuccess(result)) {
            throw new ZLSTException(ExceptionConstants.ERRORCODE_100207, new Object[]{result});
        }
        Gson gson = new Gson();
        DataResponse dataResponse = JSON.parseObject(result, DataResponse.class);
        List<OrderFlowMessageItemResponse> itemResponseList = gson.fromJson(gson.toJson(dataResponse.getData()),
                new TypeToken<List<OrderFlowMessageItemResponse>>() {
                }.getType());
        return itemResponseList.get(0).getId();
    }

    private ExecutionActionRequest buildRequestInfo(OrderFlowMessagesRequest orderFlowMessagesRequest) {
        ExecutionActionRequest executionActionRequest = new ExecutionActionRequest();
        List<RestVariable> variables = new ArrayList<>();
        if (orderFlowMessagesRequest.getVariables() != null && orderFlowMessagesRequest.getVariables().size() > 0) {
            RestVariable restVariableTemp = null;
            for (RestVariableItemVo restVariableItemVo : orderFlowMessagesRequest.getVariables()) {
                restVariableTemp = new RestVariable();
                restVariableTemp.setName(restVariableItemVo.getName());
                restVariableTemp.setType(restVariableItemVo.getType());
                restVariableTemp.setValue(restVariableItemVo.getValue());
                variables.add(restVariableTemp);
            }
        }
        executionActionRequest.setVariables(variables);
        executionActionRequest.setSignalName("");
        executionActionRequest.setMessageName("");
        executionActionRequest.setAction(orderFlowMessagesRequest.getAction());
        return executionActionRequest;
    }

    @PutMapping(value = "order/{orderId}/status")
    public Object updateOrderStatus(@PathVariable String orderId) {
        LOG.debug("updateOrderStatus start, orderId={}", orderId);

        OmsOrder omsOrder = omsOrderService.queryOmsOrderbyOrderId(orderId);

        if (null != omsOrder) {
            LOG.debug("previous status:", omsOrder.getOrderStatus());
            omsOrder.setModifyTime(new Date());
            omsOrder.setCompleteDate(new Date());
            omsOrder.setOrderStatus(OrderStatusEnum.COMPLETED.getStatus());
        }
        try {
            omsOrderService.toupdate(omsOrder);
        } catch (Exception e) {
            throw new ZLSTException(e, ExceptionConstants.UPDATE_ORDER_STATUS_FAILURE);
        }
        LOG.debug("updateOrderStatus end.");

        return ResultUtil.buildNormalResult();
    }

    /**
     * 流程轨迹查询
     *
     * @param orderId
     * @return
     */
    @GetMapping(value = "/order/trace/picture/{orderId}")
    public Object getOrderTracePicture(@PathVariable String orderId) throws Exception {
        LOG.debug("getOrderTracePicture start, orderId={}", orderId);
        String result = null;
        OmsOrder omsOrder = this.omsOrderService.get(orderId);
        if (null != omsOrder && StringUtils.isNotBlank(omsOrder.getProcessInstId())) {
            result = this.orderMgrService.getProcessPicture(omsOrder.getProcessInstId());
        }
        LOG.debug("getOrderTracePicture end.");
        return ResultUtil.buildNormalResult(result);
    }

    /**
     * 流程轨迹查询
     *
     * @param orderId
     * @return
     */
    @GetMapping(value = "/order/trace/{orderId}")
    public Object orderTrace(@PathVariable String orderId) throws Exception {
        LOG.debug("orderTrace start, orderId={}", orderId);

        //获取流程轨迹原始信息
        TraceResponse traceResponse = getProcessTrace(orderId);

        //结果中增加人工节点评论信息
        buildCommentInfo(traceResponse, omsTaskInstanceService.getCommentsByOrderId(orderId));

        //结果中增加处理人信息
        buildUserInfo(traceResponse, omsActivityInstanceService.getUserInfoByOrderId(orderId));

        if (LOG.isDebugEnabled()) {
            LOG.debug("orderTrace end, traceResponse={}", JSONObject.toJSONString(traceResponse));
        }
        return traceResponse;
    }

    private TraceResponse getProcessTrace(String orderId) throws Exception {
        TraceResponse result = null;

        //1.根据订单ID查询流程ID
        OmsOrder omsOrder = this.omsOrderService.get(orderId);
        if (null != omsOrder && StringUtils.isNotBlank(omsOrder.getProcessInstId())) {
            String resultStr = this.orderMgrService.getProcessTrail(omsOrder.getProcessInstId());
            if (CommonUtils.isSuccess(resultStr)) {
                result = JSON.parseObject(resultStr, TraceResponse.class);
            }
        }
        return result;
    }

    private void buildUserInfo(TraceResponse traceResponse, Map<String, String> taskUserMap) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("taskUserMap size: ", taskUserMap.size());
        }

        if (0 == taskUserMap.size() || null == traceResponse || null == traceResponse.getResult()
                || 0 == traceResponse.getResult().size()) {
            LOG.debug("the request is null.");
            return;
        }
        String userName = null;
        for (TraceItemResponse traceItem : traceResponse.getResult()) {
            if (StringUtils.isNotEmpty(traceItem.getActivityId())) {
                userName = taskUserMap.get(traceItem.getActivityId());
                if (StringUtils.isNotEmpty(userName)) {
                    LOG.debug("userName is :", userName);
                    traceItem.setAssignee(userName);
                }
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("result:", JSONObject.toJSONString(traceResponse));
        }
    }

    private void buildCommentInfo(TraceResponse traceResponse, Map<String, String> taskCommentMap) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("taskCommentMap size: ", taskCommentMap.size());
        }

        if (0 == taskCommentMap.size() || null == traceResponse || null == traceResponse.getResult()
                || 0 == traceResponse.getResult().size()) {
            LOG.debug("the request is null.");
            return;
        }
        String comment = null;
        for (TraceItemResponse traceItem : traceResponse.getResult()) {
            if (StringUtils.isNotEmpty(traceItem.getTaskId())) {
                comment = taskCommentMap.get(traceItem.getTaskId());
                if (StringUtils.isNotEmpty(comment)) {
                    LOG.debug("comment is :", comment);
                    traceItem.setComment(comment);
                }
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("result:", JSONObject.toJSONString(traceResponse));
        }
    }


    /**
     * 创建订单
     *
     * @param omsOrderRequest 订单创建请求入参信息
     * @return 订单ID
     * @throws Exception
     */
    @RequestMapping(value = "/order", method = RequestMethod.POST, consumes = "application/json")
    public Object createOrder(@RequestBody OmsOrderRequest omsOrderRequest) throws Exception {

        orderMgrService.validateReqInfo(omsOrderRequest);

        OmsOrder omsOrder = omsOrderService.saveOmsOrder(omsOrderRequest);
        
        String orderId = orderMgrService.saveProcessAndOrder(omsOrderRequest,omsOrder, getProcessDefId(omsOrderRequest.getBusiType()));

        Map<String, String> result = new HashMap<>();
        result.put("orderId", orderId);

        return ResultUtil.buildNormalResult(result);
    }

    private String getProcessDefId(String busiType) {

        String processDefId = null;

        OmsOrderTemplateRule omsOrderTemplateRule = omsOrderTemplateService.queryOmsOrderTemplatebyBusiType(busiType);

        if (omsOrderTemplateRule == null) {
            throw new ZLSTException(ExceptionConstants.ERRORCODE_100005);
        }

        processDefId = omsOrderTemplateRule.getProcessDefId();

        LOG.debug("processDefId:", processDefId);

        return processDefId;
    }

    /**
     * 批量创建订单
     *
     * @param omsOrderBatchRequest 订单创建请求入参信息
     * @return 订单ID
     * @throws Exception
     */
    @PostMapping(value = "/order/batch")
    public Object saveBatchOrder(@RequestBody OmsOrderBatchRequest omsOrderBatchRequest) {
        orderMgrService.validateBatchRequestInfo(omsOrderBatchRequest);

        List<Map<String, String>> orderIdList = null;

        try {
            orderIdList = orderMgrService.saveBatchProcessAndOrder(omsOrderBatchRequest, getProcessDefId(omsOrderBatchRequest.getBusiType()));
        } catch (ZLSTException e) {
            throw e;
        } catch (Exception e) {
            throw new ZLSTException(e, ExceptionConstants.ERRORCODE_100006);
        }

        return ResultUtil.buildNormalResult(orderIdList);
    }

    /**
     * 激活订单
     *
     * @param orderId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/order/active/{orderId}", method = RequestMethod.PUT, consumes = "application/json")
    public Object activeOrder(@PathVariable String orderId) throws Exception {
        //调用service服务，会改变订单状态和启动流程
        Result result = orderMgrService.activeProcessAndOrder(orderId);
        return result.getData();
    }

    public String getUserRoleInfo(String userId) {
        //通过服务获取角色Id及角色名称(角色必定能够获取，没有角色的用户有问题)
        String url = "/system/users/";
        String result = NavigatorUtil.getForObjcet(RestConstants.SYSTEM_REST_URL,  url+ userId + "/roles",url+ com.zlst.utils.RequestMethod.GET.name());
        LOG.debug("getOmsTasks rolesAPIResult=" + (result == null ? "" : result));
        Result resultObject = JSONObject.parseObject(result, Result.class);
        //获取Result中Role信息 Role可能不存在
        if (resultObject.getResultCode() != 0) {
            throw new ZLSTException(ExceptionConstants.CALL_SYSTEMMANAGE_FAILURE);
        }
        List<SysRoleVo> sysRoleList = new ArrayList<>();
        List<Object> list = JSONArray.parseArray(JSON.toJSONString(resultObject.getData()), Object.class);
        SysRoleVo sysRoleVoTemp = null;
        for (Object object : list) {
            sysRoleVoTemp = new SysRoleVo();
            sysRoleVoTemp.setRoleId(((JSONObject) object).get("roleId").toString());
            sysRoleVoTemp.setRoleName(((JSONObject) object).get("roleName").toString());
            sysRoleList.add(sysRoleVoTemp);
        }

        //1.如果用户ID非空和组织Id非空，使用用户ID+组织ID进行过滤
        //2.如果角色ID非空和组织Id非空，使用in (角色ID) +组织ID进行过滤
        StringBuffer conditionBuffer = new StringBuffer();
        for (int i = 0; i < sysRoleList.size(); i++) {
//            conditionBuffer.append("'").append(sysRoleList.get(i).getRoleId()).append("'");
            conditionBuffer.append(sysRoleList.get(i).getRoleId());
            if (i != sysRoleList.size() - 1) {
                conditionBuffer.append(",");
            }
        }

        return conditionBuffer.toString();
//        return "10,11122233";
    }

    @GetMapping(value = "/task")
    public Object getOmsTasks(@RequestParam(required = false) String data,
                              @RequestParam(required = false) Integer page,
                              @RequestParam(required = false) Integer rows) throws Exception {

        //通过header获取当前的用户id，用户名称，组织id，组织名称
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String userId = request.getHeader("gateway_userId");
        String orgId = request.getHeader("org_id");
        LOG.debug("getOmsTasks params userId=" + (userId == null ? "" : userId));
        LOG.debug("getOmsTasks params orgId=" + (orgId == null ? "" : orgId));

        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(orgId)) {
            throw new ZLSTException(ExceptionConstants.REQ_IS_NULL);
        }

        PageParam pageParam = new PageParam();
        if (null == data) {
            pageParam = new PageParam();
        } else if (CommonUtils.isJsonMap(data)) {
            pageParam = GsonUtil.gsonToBean(data, PageParam.class);
        } else {
            throw new ZLSTException(ExceptionConstants.REQ_IS_INVALID);
        }

        pageParam.setPageNum(Integer.valueOf(page == null ? 1 : page.intValue()));
        pageParam.setPageSize(Integer.valueOf(rows == null ? 10 : rows.intValue()));

        //最大只支持10000条记录查询，超出了就以10000
        if (pageParam.getPageSize() > PAGE_MAX_NUM) {
            LOG.error("the page size is ", pageParam.getPageSize(), ", exceed 10000.");
            pageParam.setPageNum(PAGE_MAX_NUM);
        }

        Filters filters = buildFilters(userId, orgId, pageParam.getFilters());

        pageParam.setFilters(filters);

        if(LOG.isDebugEnabled()){
            LOG.debug("request: ",JSONObject.toJSONString(pageParam));
        }

        Result resultQuery = ObjectToResult.getResult(omsTaskInstanceService.getOmsTaskByPage(pageParam));

        if(LOG.isDebugEnabled()) {
            LOG.debug("getOmsTasks result=" + (resultQuery == null ? "" : JSONObject.toJSONString(resultQuery)));
        }
        return resultQuery;
    }

    private Filters buildFilters(String userId, String orgId, Filters requestFilters) {
        Filters filters = new Filters();
        filters.setGroupOp(Filters.OP_AND);
        if (null != requestFilters) {
            List<Filters> requestFilterList = new ArrayList<>();
            requestFilterList.add(requestFilters);
            filters.setGroups(requestFilterList);
        }else{
            filters.setGroups(new ArrayList<>());
        }

        Filters orgIdFilters = buildOrgIdFilters(orgId);

        filters.getGroups().add(orgIdFilters);

        Rule userRule = buildRuleInfo(userId, "userId", Rule.OP_EQUAL);
        String roles = getUserRoleInfo(userId);
        if (StringUtils.isBlank(roles)) {
            filters.setRules(new ArrayList<>());
            filters.getRules().add(userRule);
        } else {
            Filters userAndRoleFileters = new Filters();
            userAndRoleFileters.setGroupOp(Filters.OP_OR);
            List<Rule> userAndRoleRuleList = new ArrayList<>();
            userAndRoleFileters.setRules(userAndRoleRuleList);
            userAndRoleRuleList.add(userRule);
            Rule roleRule = buildRuleInfo(roles, "roleId", Rule.OP_IN);
            userAndRoleRuleList.add(roleRule);

            filters.getGroups().add(userAndRoleFileters);
        }
        return filters;
    }

    private Filters buildOrgIdFilters(String orgId) {
        Filters orgIdFileters = new Filters();
        orgIdFileters.setGroupOp(Filters.OP_OR);
        List<Rule> orgIdRuleList = new ArrayList<>();
        orgIdFileters.setRules(orgIdRuleList);
        Rule orgRule = buildRuleInfo(orgId,"createOrgId",Rule.OP_EQUAL);
        orgIdRuleList.add(orgRule);
        Rule allOrgRule = buildRuleInfo("*","createOrgId",Rule.OP_EQUAL);
        orgIdRuleList.add(allOrgRule);
        return orgIdFileters;
    }

    private Rule buildRuleInfo(String data, String field, String op) {
        Rule userRule = new Rule();
        userRule.setField(field);
        userRule.setOp(op);
        userRule.setData(data);
        return userRule;
    }

//    @GetMapping(value = "/task1")
//    public Object getOmsTasks1(@RequestParam(required = false) String data,
//                               @RequestParam(required = false) Integer page,
//                               @RequestParam(required = false) Integer rows) throws Exception {
//
//        //通过header获取当前的用户id，用户名称，组织id，组织名称
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//        String userId = request.getHeader("gateway_userId");
//        String userName = request.getHeader("gateway_userName");
//        String orgId = request.getHeader("org_id");
//        String orgName = request.getHeader("org_name");
//        String orgCode = request.getHeader("org_code");
//        LOG.debug("getOmsTasks params userId=" + (userId == null ? "" : userId));
//        LOG.debug("getOmsTasks params userName=" + (userName == null ? "" : userName));
//        LOG.debug("getOmsTasks params orgId=" + (orgId == null ? "" : orgId));
//        LOG.debug("getOmsTasks params orgName=" + (orgName == null ? "" : orgName));
//        LOG.debug("getOmsTasks params orgCode=" + (orgCode == null ? "" : orgCode));
//
//        //测试代码
//        //userId = "8888888";
//        //userName = "大王来巡山";
//        //sysRoleList.add(new SysRoleVo("11122233","山大王",""));
//        //sysRoleList.add(new SysRoleVo("99999","孙悟空",""));
//        //orgId = "2222";
//        //orgName = "2部门";
//
//        //通过服务获取角色Id及角色名称(角色必定能够获取，没有角色的用户有问题)
//        String result = NavigatorUtil.getForObjcet(RestConstants.SYSTEM_REST_URL, "/system/users/" + userId + "/roles");
//        LOG.debug("getOmsTasks rolesAPIResult=" + (result == null ? "" : result));
//        Result resultObject = JSONObject.parseObject(result, Result.class);
//        //获取Result中Role信息 Role可能不存在
//        if (resultObject.getResultCode() != 0) {
//            throw new ZLSTException(ExceptionConstants.CALL_SYSTEMMANAGE_FAILURE);
//        }
//        List<SysRoleVo> sysRoleList = new ArrayList<>();
//        List<Object> list = JSONArray.parseArray(JSON.toJSONString(resultObject.getData()), Object.class);
//        SysRoleVo sysRoleVoTemp = null;
//        for (Object object : list) {
//            sysRoleVoTemp = new SysRoleVo();
//            sysRoleVoTemp.setRoleId(((JSONObject) object).get("roleId").toString());
//            sysRoleVoTemp.setRoleName(((JSONObject) object).get("roleName").toString());
//            sysRoleList.add(sysRoleVoTemp);
//        }
//
//        //1.如果用户ID非空和组织Id非空，使用用户ID+组织ID进行过滤
//        //2.如果角色ID非空和组织Id非空，使用in (角色ID) +组织ID进行过滤
//        StringBuffer conditionBuffer = new StringBuffer();
//        for (int i = 0; i < sysRoleList.size(); i++) {
//            conditionBuffer.append("'").append(sysRoleList.get(i).getRoleId()).append("'");
//            if (i != sysRoleList.size() - 1) {
//                conditionBuffer.append(",");
//            }
//        }
////        List<SysRoleVo> sysRoleList = new ArrayList<>();
////        String conditionBuffer = null;
//
//        String sql = "select i.id,i.order_id as orderId,i.order_source_id as orderSourceId,i.proc_def_name as procDefName,i.`status`," +
//                "DATE_FORMAT(i.status_time,'%Y-%m-%d %H:%i:%s') as statusTime,i.task_name as taskName,i.url " +
//                "from oms_task_instance i where 1=1 and i.create_org_id ='" + orgId + "' " +
//                "and ( (i.user_id = '" + userId + "' ) ";
//        //"order by status_time desc";
//        if (sysRoleList != null && sysRoleList.size() > 0) {
//            sql += " or (i.role_id in ( " + conditionBuffer.toString() + ") )";
//        }
//        sql += " ) ";
////        sql += " or (i.role_id in ('11122233'))) ";
//        Rule orgRule = new Rule();
//        orgRule.setField("createOrgId");
//        orgRule.setOp(Filters.OP_AND);
//        orgRule.setData(orgId);
//
//        List<Rule> userAndRoleRuleList = new ArrayList<>();
//
//        Filters userAndRoleFileters = new Filters();
//        userAndRoleFileters.setGroupOp(Filters.OP_OR);
//        userAndRoleFileters.setRules(userAndRoleRuleList);
//
//
//        Rule userRule = new Rule();
//        userRule.setField("userId");
//        userRule.setOp(Rule.OP_EQUAL);
//        userRule.setData(userId);
//        userAndRoleRuleList.add(userRule);
//        if (sysRoleList != null && sysRoleList.size() > 0) {
//            Rule roleRule = new Rule();
//            roleRule.setField("roleId");
//            roleRule.setOp(Rule.OP_IN);
//            roleRule.setData(conditionBuffer.toString());
//            userAndRoleRuleList.add(userRule);
//        }
//        SearchWithPageParam param = null;
//        if (!Common.isNull(data).booleanValue()) {
//            param = (SearchWithPageParam) GsonUtil.gsonToBean(data, SearchWithPageParam.class);
//        } else {
//            param = new SearchWithPageParam();
//        }
//
//        PageParam pageParam = new PageParam();
//        pageParam.setPageNum(Integer.valueOf(page == null ? 1 : page.intValue()));
//        pageParam.setPageSize(Integer.valueOf(rows == null ? 10 : rows.intValue()));
//        pageParam.setSidx(Common.isNull(param.getSidx()).booleanValue() ? null : param.getSidx());
//        pageParam.setSord(Common.isNull(param.getSord()).booleanValue() ? null : param.getSord());
//        Filters filters = param.getFilters();
//        List<Rule> rList = (filters == null ? null : filters.getRules());
//        Map<String, Object> params = new HashMap<>();
//        if (rList != null && rList.size() > 0) {
//            for (Rule r : rList) {
//                if (r.getField().equals("procDefName")) {
//                    sql += " and proc_def_name = :procDefName ";
//                    params.put("procDefName", r.getData());
//                } else if (r.getField().equals("status")) {
//                    sql += " and status = :status ";
//                    params.put("status", r.getData());
//                } else if (r.getField().equals("userId")) {
//                    System.out.println("userId=" + r.getData());
//                    //params.put("status", r.getData());
//                } else if (r.getField().equals("startTime")) {
//                    sql += " and create_time >= :startTime ";
//                    params.put("startTime", r.getData());
//                } else if (r.getField().equals("orderSourceId")) {
//                    sql += " and order_source_id = :orderSourceId ";
//                    params.put("orderSourceId", r.getData());
//                } else if (r.getField().equals("endTime")) {
//                    sql += " and create_time <= :endTime ";
//                    params.put("endTime", r.getData());
//                } else if (r.getField().equals("taskName") && r.getOp().equals("cn")) {
//                    sql += " and task_name like :taskName ";
//                    params.put("taskName", '%' + r.getData() + '%');
//                }
//            }
//        }
//        LOG.debug("getOmsTasks querySql=" + sql);
//        Result resultQuery = ObjectToResult.getResult(nvativeSqlQueryServ.nativeSqlPageQuery(sql, pageParam, OmsTasksDataVo.class, params));
//        LOG.debug("getOmsTasks result=" + (resultQuery == null ? "" : JSONObject.toJSONString(resultQuery)));
//        return resultQuery;
//    }

    @RequestMapping(value = "/task/{taskId}", method = RequestMethod.PUT, consumes = "application/json")
    public Object omsTaskInstanceOp(@PathVariable String taskId, @RequestBody OmsTaskInstanceOpRequest omsTaskInstanceOpRequest) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("omsTaskInstanceOp paramsInfo taskId=" + taskId);
            LOG.debug("omsTaskInstanceOpRequest paramsInfo omsTaskInstanceOpRequest=" + JSON.toJSONString(omsTaskInstanceOpRequest));
        }
        //userId通过header获取
        String userId = null;
        String userName = null ;
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        userId = request.getHeader("gateway_userId");
        userName = request.getHeader("gateway_userName");
        //如果取不到就去request头取
        if(Common.isNull(userId) || Common.isNull(userName)){
            for (RestVariableItemVo itemVo : omsTaskInstanceOpRequest.getVariables()) {
                if("createOperId".equals(itemVo.getName())){
                    userId = itemVo.getValue();
                }
                if("createOperName".equals(itemVo.getName())){
                    userName = itemVo.getValue();
                }
            }
        }
        omsTaskInstanceOpRequest.setUserId(userId);
        omsTaskInstanceOpRequest.setUserName(userName);
        this.orderMgrService.opOmsTaskInstance(taskId, omsTaskInstanceOpRequest);

        return ResultUtil.buildNormalResult();
    }
}
