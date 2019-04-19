package com.zlst.module.order.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zlst.exception.ZLSTException;
import com.zlst.module.order.bean.OmsOrder;
import com.zlst.module.order.bean.OmsTaskInstance;
import com.zlst.module.order.bean.vo.*;
import com.zlst.param.ObjectToResult;
import com.zlst.param.PageParam;
import com.zlst.param.Result;
import com.zlst.utils.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class OrderMgrService {

    private final static Logger LOG = LoggerFactory.getLogger(OrderMgrService.class);
    public static final String SYN_FLAG = "Y";

    @Autowired
    private OmsOrderService omsOrderService;
    @Autowired
    private OmsTaskInstanceService omsTaskInstanceService;
    @Autowired
    private OrderMgrService orderMgrService;


    /**
     * 查询流程变量
     *
     * @param processInstanceId
     * @return
     */
    public String queryProcessVariables(String processInstanceId) {
        LOG.debug("processInstId :", processInstanceId);
        String url = "/runtime/process-instances/";
        return NavigatorUtil.getForObjcet( url + processInstanceId + "/variables",url+RequestMethod.GET.name());
    }

    /**
     * 查询流程正在运行的节点
     *
     * @param processInstanceId
     * @param activityId
     * @return
     */
    public String queryExecutions(String processInstanceId, String activityId) {
        LOG.debug("queryExecutions start,processInstanceId: ", processInstanceId, ",activityId: ", activityId);
        String activityIdPart = "";
        if (StringUtils.isNotBlank(activityId)) {
            activityIdPart = "&activityId=" + activityId;
        }
        String url = "/runtime/executions";
        return NavigatorUtil.getForObjcet(url+"?processInstanceId=" + processInstanceId + activityIdPart,url+RequestMethod.GET.getName());
    }

    /**
     * 通知指定的流程节点
     *
     * @return
     */
    public void callExecutions(String executionId, ExecutionActionRequest executionActionRequest) {
        LOG.debug("orderFlowMessages callExecutions, executionActionRequest {}", JSON.toJSONString(executionActionRequest));
        String url = "/runtime/executions/";
        String result = NavigatorUtil.putForObjcet( url+ executionId,
                JSON.toJSONString(executionActionRequest),url+RequestMethod.PUT.name());
        //两个分支时，第一次返回的是json串，第二次返回成功时是空字符串
        LOG.debug("result: ", result);
        if (!((null == result) || CommonUtils.isJsonMap(result))) {
            throw new ZLSTException(ExceptionConstants.ERRORCODE_100207, new Object[]{result});
        }
    }




    /**
     * 获取流程轨迹
     *
     * @param processInstanceId
     * @return
     */
    public String getProcessTrail(String processInstanceId) {
        String url = "/processTrail/";
        return NavigatorUtil.getForObjcet(url + processInstanceId,url+RequestMethod.GET.name());
    }

    /**
     * 获取流程轨迹图片信息
     *
     * @param processInstanceId
     * @return
     */
    public String getProcessPicture(String processInstanceId) {
        LOG.debug("getProcessPicture start,processInstanceId: ", processInstanceId);
        String result = null;
        String url = "/resource/picture/processInstance/";
        String responseStr = NavigatorUtil.getForObjcet( url + processInstanceId,url+RequestMethod.GET.name());
        if (CommonUtils.isSuccess(responseStr)) {
            Map<String, String> map = JSONObject.parseObject(responseStr, Map.class);
            result = map.get("data");
        }
        LOG.debug("result:", result);
        return result;
    }

    /**
     * 人工任务操作
     *
     * @param taskId
     * @param taskActionRequest
     * @return
     */
    public void executeTaskAction(String taskId, TaskActionRequest taskActionRequest) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("taskId: ", taskId, ",taskActionRequest: ", JSONObject.toJSONString(taskActionRequest));
        }
        //先认领
        taskActionRequest.setAction(TaskActionRequest.ACTION_CLAIM);

        String url = "/runtime/tasks/";
        String result = NavigatorUtil.postForObjcet(url + taskId,
                JSON.toJSONString(taskActionRequest),url+RequestMethod.POST.name());

        //执行成功时返回空字符串,失败时按标准结构返回
        if (StringUtils.isNotBlank(result)) {
            throw new ZLSTException(ExceptionConstants.ERRORCODE_100207);
        }

        //认领后直接处理
        taskActionRequest.setAction(TaskActionRequest.ACTION_COMPLETE);

        String urls = "/runtime/tasks/";
        result = NavigatorUtil.postForObjcet(urls + taskId,
                JSON.toJSONString(taskActionRequest),urls+RequestMethod.POST.name());

        if (StringUtils.isNotBlank(result)) {
            throw new ZLSTException(ExceptionConstants.ERRORCODE_100207);
        }
    }

    public String cancelProcInstance(String processInstanceId, String reason) {
        LOG.debug("cancelProcInstance start...");
        Map<String, Object> reasonMap = new HashMap<>();
        reasonMap.put("deleteReason", reason);

        String url = "/runtime/process-instances/";
        String result = NavigatorUtil.deleteForObjcet( url+ processInstanceId,
                JSON.toJSONString(reasonMap),url+RequestMethod.DELETE.name());

        LOG.debug("cancelProcInstance end.result :", result);

        return result;
    }

    /**
     * 对人工任务的操作，修改人工任务表实例状态，并完成流程节点
     *
     * @return
     */
    @Transactional
    public void opOmsTaskInstance(String taskId, OmsTaskInstanceOpRequest omsTaskInstanceOpRequest) throws Exception {
        //1.根据TaskId 查询当前实例化的任务工单，
        OmsTaskInstance omsTaskInstance = this.omsTaskInstanceService.get(taskId);
        if (omsTaskInstance == null) {
            throw new ZLSTException(ExceptionConstants.ERRORCODE_100211, new Object[]{taskId});
        }

        //2.根据返回结果对记录进行更新
        omsTaskInstance.setStatus("1");
        omsTaskInstance.setStatusTime(new Date());
        omsTaskInstance.setUserId(omsTaskInstanceOpRequest.getUserId());
        omsTaskInstance.setUserName(omsTaskInstanceOpRequest.getUserName());
        omsTaskInstance.setTaskComment(omsTaskInstanceOpRequest.getComment());
        this.omsTaskInstanceService.toupdate(omsTaskInstance);

        //3.更新后调用接口对任务进行完成动作
        //omsTaskInstanceOpRequest.getVariables()
        TaskActionRequest taskActionRequest = new TaskActionRequest();
        List<RestVariable> restVariableList = new ArrayList<>();
        //将变量赋值到restVariableList中
        RestVariable restVariable = null;
        if (omsTaskInstanceOpRequest.getVariables() != null && omsTaskInstanceOpRequest.getVariables().size() > 0) {
            for (RestVariableItemVo restVariableItemVo : omsTaskInstanceOpRequest.getVariables()) {
                restVariable = new RestVariable();
                BeanUtils.copyProperties(restVariableItemVo, restVariable);
                restVariableList.add(restVariable);
            }
        }
        taskActionRequest.setVariables(restVariableList);
        taskActionRequest.setAssignee(omsTaskInstanceOpRequest.getUserName());
        orderMgrService.executeTaskAction(omsTaskInstance.getTaskInstId(), taskActionRequest);
    }


    /**
     * 激活订单和流程
     *
     * @param orderId
     * @return
     * @throws Exception
     */
    @Transactional
    public Result activeProcessAndOrder(String orderId) throws Exception {
        OmsOrder omsOrder = omsOrderService.get(orderId);
        if (omsOrder == null) {
            //modify bugId 1718
            return (Result) ResultUtil.buildNormalResult();
//            throw new ZLSTException(ExceptionConstants.ERRORCODE_100201,new Object[]{orderId});
        }
        String processInstanceId = omsOrder.getProcessInstId();
        if (StringUtils.isBlank(omsOrder.getProcessInstId())) {
            return (Result) ResultUtil.buildNormalResult();
            //modify bugId 1718
//            throw new ZLSTException(ExceptionConstants.ERRORCODE_100202,new Object[]{orderId});
        }
        //1.通过网关发起对应流程启动请求
        Map map = new HashMap();
        map.put("processInstanceId",  processInstanceId);//激活
        String url = "/runtime/process-instances/active";
        String responseObject = NavigatorUtil.putForObjcet(url, JSON.toJSONString(map),url+RequestMethod.PUT.getName());
        ProcessInstanceResponse processInstanceResponse = JSON.parseObject(responseObject, ProcessInstanceResponse.class);
        //2.进行更新该数据的标志
        omsOrder.setOrderStatus("1"); //流程启动
        omsOrder.setModifyTime(new Date());
        omsOrder.setRemark("Rest请求更新");
        omsOrderService.toupdate(omsOrder);
        //3.返回结果
        return ObjectToResult.getResult(processInstanceResponse);
    }

    /**
     * 业务处理批量保存订单和创建流程实例
     *
     * @param omsOrderBatchRequest 批量请求订单
     * @param processDefKey        流程定义key
     * @return 结果对象
     * @throws Exception 异常
     */
    @Transactional
    public List<Map<String, String>> saveBatchProcessAndOrder(OmsOrderBatchRequest omsOrderBatchRequest, String processDefKey) throws Exception {
        LOG.debug("saveBatchProcessAndOrder start,processDefKey:", processDefKey);

        List<Map<String, String>> result = new ArrayList<>();
        OmsOrder omsOrder = null;

        for (OmsOrderBatchRequest.OrderListItem orderListItem : omsOrderBatchRequest.getOrderList()) {
            omsOrder = createOrderInfo(omsOrderBatchRequest, orderListItem);

            String jsonStr = buildCreateProcessReqInfo(omsOrderBatchRequest.getBusiType(), processDefKey, omsOrder, orderListItem.getVariables());

            String url = "/runtime/process-instances/create/";
            String responseObject = NavigatorUtil.postForObjcet(url, jsonStr,url+RequestMethod.POST.name());
            if (!CommonUtils.isSuccess(responseObject)) {
                throw new ZLSTException(ExceptionConstants.SAVE_FAILURE);
            }

            ProcessInstanceResponse processInstanceResponse = JSON.parseObject(responseObject, ProcessInstanceResponse.class);

            omsOrder.setProcessInstId(processInstanceResponse.getId());
            omsOrderService.update(omsOrder.getOrderId(), omsOrder);

            Map<String, String> map = new HashMap();
            map.put("orderId", omsOrder.getOrderId());
            map.put("orderSourceId", omsOrder.getOrderSourceId());
            result.add(map);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("result:", JSONObject.toJSONString(result));
        }

        LOG.debug("saveBatchProcessAndOrder end.");
        return result;
    }

    private String buildCreateProcessReqInfo(String busiType, String processDefKey, OmsOrder omsOrder, List<RestVariableItemVo> variableItemVoList) {
        ProcessInstanceCreateRequest request = new ProcessInstanceCreateRequest();
        request.setProcessDefinitionKey(processDefKey);
        request.setBusinessKey(busiType);
        request.setVariables(getVariableItems(variableItemVoList, omsOrder));
        String jsonStr = JSON.toJSONString(request);
        LOG.debug("request:", jsonStr);
        return jsonStr;
    }

    private OmsOrder createOrderInfo(OmsOrderBatchRequest omsOrderBatchRequest, OmsOrderBatchRequest.OrderListItem item) throws Exception {
        OmsOrder omsOrder;
        omsOrder = new OmsOrder();
        omsOrder.setBusiType(omsOrderBatchRequest.getBusiType());
        omsOrder.setCreatedBy(omsOrderBatchRequest.getCreateOperId());
        if (StringUtils.isNotEmpty(omsOrderBatchRequest.getCreateOperId())) {
            omsOrder.setCreatedBy(omsOrderBatchRequest.getCreateOperId());
        } else {
            omsOrder.setCreatedBy(CommonUtils.getHeadUserId());
        }
        if (StringUtils.isNotEmpty(omsOrderBatchRequest.getCreateOrderOrgId())) {
            omsOrder.setOrderCreateOrgId(omsOrderBatchRequest.getCreateOrderOrgId());
        } else {
            omsOrder.setOrderCreateOrgId(CommonUtils.getOrgId());
        }
        omsOrder.setOrderPri(item.getOrderPriority());
        omsOrder.setWishedFinishDate(item.getWishedFinishDate());
        omsOrder.setOrderSourceId(item.getOrderSourceId());
        omsOrder.setParentOrderId(omsOrderBatchRequest.getParentOrderId());
        omsOrder.setOrderStatus(OrderStatusEnum.CREATED.getStatus());
        omsOrder.setRemark("批量创建");
        omsOrder = omsOrderService.saveOmsOrderSimp(omsOrder);
        return omsOrder;
    }

    /**
     * 业务处理保存订单和创建流程实例
     *
     * @param omsOrderRequest 订单请求数据
     * @param processDefKey   流程定义key
     * @return 结果对象
     * @throws Exception 异常
     */
    @Transactional
    public String saveProcessAndOrder(OmsOrderRequest omsOrderRequest,OmsOrder omsOrder, String processDefKey) throws Exception {
        LOG.debug("saveProcessAndOrder start, processDefKey: ", processDefKey);

        String jsonStr = buildCreateProcessReqInfo(omsOrderRequest.getBusiType(), processDefKey, omsOrder, omsOrderRequest.getVariables());

        String requestUrl = null;

        //订单同步执行，即创建流程和流程执行在同一个线程中完成，默认为异步执行，保持与之前兼容
        if (SYN_FLAG.equalsIgnoreCase(omsOrderRequest.getSynFlag())) {
            requestUrl = "/runtime/process-instances";
        } else {
            requestUrl = "/runtime/process-instances/create/";
        }

        String responseObject = NavigatorUtil.postForObjcet(requestUrl, jsonStr,requestUrl+RequestMethod.POST.getName());
        if (!CommonUtils.isSuccess(responseObject)) {
            throw new ZLSTException(ExceptionConstants.SAVE_FAILURE);
        }

        ProcessInstanceResponse processInstanceResponse = JSON.parseObject(responseObject, ProcessInstanceResponse.class);

        omsOrder.setProcessInstId(processInstanceResponse.getId());
        if (SYN_FLAG.equalsIgnoreCase(omsOrderRequest.getSynFlag())) {
            omsOrder.setOrderStatus(OrderStatusEnum.RUNNING.getStatus());
        }
        else{
            omsOrder.setOrderStatus(OrderStatusEnum.CREATED.getStatus());
        }
        omsOrderService.update(omsOrder.getOrderId(), omsOrder);
        LOG.debug("saveProcessAndOrder end.");
        return omsOrder.getOrderId();
    }

    private List<RestVariableItemVo> getVariableItems(List<RestVariableItemVo> restVariableItemVos, OmsOrder omsOrder) {
        if (restVariableItemVos == null) {
            restVariableItemVos = new ArrayList<>();
        }
        restVariableItemVos.add(buildRestVariableItemVO("orderId", omsOrder.getOrderId()));
        restVariableItemVos.add(buildRestVariableItemVO("orgId", omsOrder.getOrderCreateOrgId()));
        restVariableItemVos.add(buildRestVariableItemVO("userId", omsOrder.getCreatedBy()));

        return restVariableItemVos;
    }


    private RestVariableItemVo buildRestVariableItemVO(String name, String value) {
        RestVariableItemVo restVariableItemVo = new RestVariableItemVo();

        restVariableItemVo.setName(name);
        restVariableItemVo.setValue(value);
        restVariableItemVo.setType("string");

        return restVariableItemVo;
    }

    /**
     * 检测创建订单请求数据
     *
     * @param omsOrderRequest 订单请求数据
     * @return 结果对象
     */
    public void validateReqInfo(OmsOrderRequest omsOrderRequest) {
        if (null == omsOrderRequest) {
            throw new ZLSTException(ExceptionConstants.REQ_IS_NULL);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(JSONObject.toJSONString(omsOrderRequest));
        }

        if (StringUtils.isBlank(omsOrderRequest.getBusiType())) {
            throw new ZLSTException(ExceptionConstants.ERRORCODE_100001);
        }
        if (StringUtils.isBlank(omsOrderRequest.getCreateOperId())) {
            throw new ZLSTException(ExceptionConstants.ERRORCODE_100002);
        }
        if (StringUtils.isBlank(omsOrderRequest.getCreateOrderOrgId())) {
            throw new ZLSTException(ExceptionConstants.ERRORCODE_100003);
        }
        validateOrderPri(omsOrderRequest.getOrderPriority());

        resetOrderPri(omsOrderRequest);

        validateVariableType(omsOrderRequest.getVariables());
    }

    private void resetOrderPri(OmsOrderRequest omsOrderRequest) {
        omsOrderRequest.setOrderPriority(getOrderPri(omsOrderRequest.getOrderPriority()));
    }

    private void resetOrderListItemPri(OmsOrderBatchRequest.OrderListItem orderListItem) {
        orderListItem.setOrderPriority(getOrderPri(orderListItem.getOrderPriority()));
    }

    private void validateOrderPri(Integer orderPrio) {
        orderPrio = getOrderPri(orderPrio);
        if (orderPrio > 99) {
            throw new ZLSTException(ExceptionConstants.ERRORCODE_100004, new Object[]{orderPrio});
        }
    }

    private Integer getOrderPri(Integer orderPrio) {
        return orderPrio == 0 ? 1 : orderPrio;
    }

    /**
     * 检测批量创建订单请求数据
     *
     * @param omsOrderBatchRequest 请求数据
     * @return 结果对象
     */
    public void validateBatchRequestInfo(OmsOrderBatchRequest omsOrderBatchRequest) {
        if (null == omsOrderBatchRequest) {
            throw new ZLSTException(ExceptionConstants.REQ_IS_NULL);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(JSONObject.toJSONString(omsOrderBatchRequest));
        }

        if (StringUtils.isBlank(omsOrderBatchRequest.getBusiType())) {
            throw new ZLSTException(ExceptionConstants.ERRORCODE_100001);
        }
        if (StringUtils.isBlank(omsOrderBatchRequest.getCreateOperId())) {
            throw new ZLSTException(ExceptionConstants.ERRORCODE_100002);
        }
        if (StringUtils.isBlank(omsOrderBatchRequest.getCreateOrderOrgId())) {
            throw new ZLSTException(ExceptionConstants.ERRORCODE_100003);
        }

        OmsOrderBatchRequest.OrderListItem orderListItem = null;

        Set<String> orderSourceIdSet = new HashSet<>();

        for (int i = 0, len = omsOrderBatchRequest.getOrderList().size(); i < len; i++) {
            orderListItem = omsOrderBatchRequest.getOrderList().get(i);

            validateOrderPri(orderListItem.getOrderPriority());

            resetOrderListItemPri(orderListItem);

            validateOrderSourceId(orderListItem, i, orderSourceIdSet);

            validateVariableType(orderListItem.getVariables());
        }
    }

    private void validateOrderSourceId(OmsOrderBatchRequest.OrderListItem orderListItem, int i, Set<String> orderSourceIdSet) {
        if (StringUtils.isBlank(orderListItem.getOrderSourceId())) {
            throw new ZLSTException(ExceptionConstants.ERRORCODE_100009, new Object[]{i + 1});
        }
        if (orderSourceIdSet.contains(orderListItem.getOrderSourceId())) {
            throw new ZLSTException(ExceptionConstants.ERRORCODE_100010, new Object[]{i + 1});
        } else {
            orderSourceIdSet.add(orderListItem.getOrderSourceId());
        }
    }

    /**
     * 检测流程变量类型格式支持
     *
     * @param restVariableItemVos 流程变量vo
     * @return
     */
    public void validateVariableType(List<RestVariableItemVo> restVariableItemVos) {
        if (restVariableItemVos != null && restVariableItemVos.size() > 0) {
            RestVariableItemVo restVariableItemVo;
            for (int i = 0, len = restVariableItemVos.size(); i < len; i++) {
                restVariableItemVo = restVariableItemVos.get(i);
                if (!VariableTypeUtil.chkType(restVariableItemVo.getType())) {
                    throw new ZLSTException(ExceptionConstants.ERRORCODE_100008, new Object[]{i + 1});
                }
            }
        }
    }

}
