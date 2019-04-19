package com.zlst.business.activiti.listener;

import com.alibaba.fastjson.JSONObject;
import com.zlst.utils.*;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("processCompleteHandler")
public class ProcessCompleteHandler implements GlobalEventHandler {

    private final static Logger LOG = LoggerFactory.getLogger(ProcessCompleteHandler.class);

    @Override
    public void handle(ActivitiEvent event) {
        LOG.debug("完成监听器start...");

        String url = "/orderMgr/order/";

        String result = NavigatorUtil.putForObjcet( url + getOrderId(event) + "/status", "",url+ RequestMethod.PUT.getName());

        if (ResultUtil.occurException(result)) {
            ResultUtil.recordOrderException(event.getProcessDefinitionId(), "End Node", getOrderId(event),
                    ExceptionTypeEnum.COMPLETED_LISTENER_EXCEPTION.getExceptionType(), ResultUtil.getResponseStackTrace(result));
        }

        LOG.debug("完成监听器end...");
    }

    private String getOrderId(ActivitiEvent event) {
        String orderId = event.getEngineServices().getRuntimeService().getVariable(event.getExecutionId(), "orderId", String.class);
        LOG.debug("orderId:", orderId);
        return orderId;
    }
}




