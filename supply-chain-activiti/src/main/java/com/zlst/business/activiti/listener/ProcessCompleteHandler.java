package com.zlst.business.activiti.listener;

import java.util.Date;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zlst.common.ExceptionConstants;
import com.zlst.exception.ZLSTException;
import com.zlst.module.order.bean.OmsOrder;
import com.zlst.module.order.service.OmsOrderService;
import com.zlst.utils.OrderStatusEnum;

@Service("processCompleteHandler")
public class ProcessCompleteHandler implements GlobalEventHandler {

    private final static Logger LOG = LoggerFactory.getLogger(ProcessCompleteHandler.class);
    
    @Autowired
    private OmsOrderService omsOrderService;

    @Override
    public void handle(ActivitiEvent event) {
        LOG.debug("完成监听器start...");

        /*String url = "/orderMgr/order/";

        String result = NavigatorUtil.putForObjcet( url + getOrderId(event) + "/status", "",url+ RequestMethod.PUT.getName());

        if (ResultUtil.occurException(result)) {
            ResultUtil.recordOrderException(event.getProcessDefinitionId(), "End Node", getOrderId(event),
                    ExceptionTypeEnum.COMPLETED_LISTENER_EXCEPTION.getExceptionType(), ResultUtil.getResponseStackTrace(result));
        }*/
        
        /**
         * 2019.02.11 新年第一天上班，把上面这个调用改成本地方法调用
         */
        this.callOrderManager(this.getOrderId(event));

        LOG.debug("完成监听器end...");
    }
    
    /**
     * 调用本地方法更新【任务状态信息】
     * @param orderId
     */
    private void callOrderManager(String orderId) {
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
    }

    private String getOrderId(ActivitiEvent event) {
        String orderId = event.getEngineServices().getRuntimeService().getVariable(event.getExecutionId(), "orderId", String.class);
        LOG.debug("orderId:", orderId);
        return orderId;
    }
}




