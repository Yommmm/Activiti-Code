package com.zlst.module.order.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zlst.module.order.bean.OmsOrder;
import com.zlst.module.order.bean.vo.ProcessInstanceResponse;
import com.zlst.module.order.service.OmsOrderService;
import com.zlst.utils.NavigatorUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 废弃掉,改用quartz定时任务
 * Created by 170066 on 2017/9/21.
 */
@Service
@EnableScheduling
public class ScheduledOrderTasks {

    @Value("${scheduledJobParams.delaySecond}")
    private int delaySecond;

    @Value("${scheduledJobParams.orderLimit}")
    private int orderLimit;

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OmsOrderService omsOrderService;


//    @Scheduled(cron = "0 */1 *  * * * ")
//    public void orderTask() throws Exception {
//        List<OmsOrder> omsOrders = omsOrderService.queryOmsOrderbyOrderStatus("0", delaySecond, orderLimit);
//        if (omsOrders != null) {
//            for (OmsOrder omsOrder : omsOrders) {
//                String processInstId = omsOrder.getProcessInstId();
//                if (StringUtils.isNotBlank(processInstId)) {
//                    logger.info("定时任务流程拉起开始,processInstId:=" + processInstId);
//                    opterInfo(omsOrder, processInstId);
//                    logger.info("定时任务流程拉起结束,processInstId:=" + processInstId);
//                }
//            }
//            logger.info("scheduledOrderTask:size:=" + omsOrders.size());
//        }
//    }

//    @Transactional
//    public void opterInfo(OmsOrder omsOrder, String processInstId) throws Exception {
//        //拉起流程
//        Map map = new HashMap();
//        map.put("action", "activate");//  激活
//        JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(map));
//
//        String responseObject = NavigatorUtil.putForObjcet("/runtime/process-instances/active/" + processInstId, jsonObject, 8 * 1000);
//        ProcessInstanceResponse processInstanceResponse = JSON.parseObject(responseObject, ProcessInstanceResponse.class);
//
//        if (processInstanceResponse != null) {
//            //更新数据
//            omsOrder.setOrderStatus("1");//流程启动
//            omsOrder.setModifyTime(new Date());
//            omsOrder.setRemark("定时任务更新");
//            omsOrderService.toupdate(omsOrder);
//        }
//
//    }


    private SimpleDateFormat dateFormat() {
        return new SimpleDateFormat("HH:mm:ss");
    }
}
