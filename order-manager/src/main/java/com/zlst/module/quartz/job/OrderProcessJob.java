package com.zlst.module.quartz.job;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zlst.module.order.bean.OmsOrder;
import com.zlst.module.order.bean.OmsOrderException;
import com.zlst.module.order.bean.vo.ProcessInstanceResponse;
import com.zlst.module.order.service.OmsOrderExceptionService;
import com.zlst.module.order.service.OmsOrderService;
import com.zlst.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 定时任务拉起流程
 */
@DisallowConcurrentExecution
public class OrderProcessJob implements Job {
    Logger logger = LogManager.getLogger(getClass());

    @Value("${scheduledJobParams.delaySecond}")
    private int delaySecond;

    @Value("${scheduledJobParams.orderLimit}")
    private int orderLimit;

    @Value("${scheduledJobParams.retryNum}")
    private int retryNum;

    @Autowired
    private OmsOrderService omsOrderService;

    @Autowired
    private OmsOrderExceptionService omsOrderExceptionService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            List<OmsOrder> omsOrders = omsOrderService.queryOmsOrderbyOrderStatus(OrderStatusEnum.CREATED.getStatus(), delaySecond, orderLimit);
            if (omsOrders != null) {
                for (OmsOrder omsOrder : omsOrders) {
                    String processInstId = omsOrder.getProcessInstId();
                    if (StringUtils.isNotBlank(processInstId)) {
                        logger.info("定时任务流程拉起开始,processInstId:=" + processInstId + ",orderId:=" + omsOrder.getOrderId());
                        activateProcess(omsOrder, processInstId);
                        logger.info("定时任务流程拉起结束,processInstId:=" + processInstId + ",orderId:=" + omsOrder.getOrderId());
                    }
                }
                logger.info("scheduledOrderTask:size:=" + omsOrders.size());
            }
        } catch (Exception e) {
            logger.error("order ", e);
        }
    }

    @Transactional
    public void activateProcess(OmsOrder omsOrder, String processInstId) throws Exception {
        try {
            //拉起流程
            Map map = new HashMap();

            map.put("processInstanceId", processInstId);//  激活

            String url = "/runtime/process-instances/active";
            String responseObject = NavigatorUtil.putForObjcet(url, JSON.toJSONString(map),url+ RequestMethod.PUT.getName());

            ProcessInstanceResponse processInstanceResponse = JSON.parseObject(responseObject, ProcessInstanceResponse.class);

            if (processInstanceResponse != null && null != processInstanceResponse.getProcessDefinitionId()) {
                //更新数据
                omsOrder.setOrderStatus(OrderStatusEnum.RUNNING.getStatus());//流程启动
                omsOrder.setRemark("定时任务更新");
            } else {
                dealwithStartException(omsOrder);
                recordOrderException(omsOrder.getOrderId(), getResponseStackTrace(responseObject));
            }
        } catch (Exception ex) {
            dealwithStartException(omsOrder);
            recordOrderException(omsOrder.getOrderId(), ex);
        }

        omsOrder.setModifyTime(new Date());
        omsOrderService.toupdate(omsOrder);

        logger.debug("update success...");
    }

    private String getResponseStackTrace(String responseStr) {
        Map<String, Object> response = JSONObject.parseObject(responseStr, Map.class);
        Map<String,Object> errorInfoMap = (Map<String,Object>)response.get("errorInfo");
        String stackTrace = (String)errorInfoMap.get("stackTrace");
        if (stackTrace.length() > 3800) {
            stackTrace = stackTrace.substring(0, 3800);
        }
        return stackTrace;
    }


    private void dealwithStartException(OmsOrder omsOrder) {
        logger.error(omsOrder.getProcessInstId() + ": 流程启动失败...");
        int num = omsOrder.getStartRetryNum();
        if (num >= retryNum - 1) {
            omsOrder.setOrderStatus(OrderStatusEnum.START_EXCEPTION.getStatus());
            omsOrder.setStartRetryNum(retryNum);
            omsOrder.setRemark("启动失败次数达到最大");
        } else {
            omsOrder.setStartRetryNum(++num);
        }
    }

    private void recordOrderException(String orderId, String stackTrace) {
        try {
            OmsOrderException omsOrderException = new OmsOrderException();
            omsOrderException.setOrderId(orderId);
            omsOrderException.setCreateTime(new Date());
            omsOrderException.setExceptionType(ExceptionTypeEnum.START_EXCEPTION.getExceptionType());
            omsOrderException.setStackTrace(stackTrace);
            omsOrderExceptionService.save(omsOrderException);
        } catch (Exception e) {
            logger.error("recordOrderException occur exception,exceptionType: start");
        }
    }

    private void recordOrderException(String orderId, Exception ex) {
        recordOrderException(orderId, OrderUtils.getStackTrace(ex));
    }

}