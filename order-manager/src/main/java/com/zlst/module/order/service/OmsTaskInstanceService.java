package com.zlst.module.order.service;

import com.alibaba.fastjson.JSONObject;
import com.zlst.database.core.service.QueryAndOperateServ;
import com.zlst.module.order.bean.OmsTaskInstance;
import com.zlst.module.order.dao.OmsTaskInstanceRepository;
import com.zlst.param.Page;
import com.zlst.param.PageParam;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by songcj on 2017/9/21.
 */
@Service
public class OmsTaskInstanceService extends QueryAndOperateServ<OmsTaskInstance, OmsTaskInstanceRepository> {

    private final static Logger LOG = LoggerFactory.getLogger(OmsTaskInstanceService.class);

    private static final String UNHANDLED_STATUS = "0";

    @Autowired
    private OmsTaskInstanceRepository omsTaskInstanceRepository;

    public List<OmsTaskInstance>  getUnHandledTaskInstance(String orderId) {
        LOG.debug("getUnHandledTaskInstance start...");

        List<OmsTaskInstance> result = new ArrayList<OmsTaskInstance>();

        List<OmsTaskInstance> taskInstanceList = omsTaskInstanceRepository.queryTaskInstanceByOrderId(orderId);

        if(null != taskInstanceList && taskInstanceList.size() > 0){
            for (OmsTaskInstance taskInstance : taskInstanceList){
                if (UNHANDLED_STATUS.equals(taskInstance.getStatus())){
                    result.add(taskInstance);
                }
            }
        }
        LOG.debug("getUnHandledTaskInstance end...");

        return result;
    }

    public Map<String, String> getCommentsByOrderId(String orderId) {
        LOG.debug("getCommentsByOrderId start...");

        Map<String, String> result = new HashMap<String, String>();

        List<OmsTaskInstance> taskInstanceList = omsTaskInstanceRepository.queryTaskInstanceByOrderId(orderId);

        if (null != taskInstanceList && taskInstanceList.size() > 0) {
            for (OmsTaskInstance taskInstance : taskInstanceList) {
                if (StringUtils.isNotBlank(taskInstance.getTaskComment())) {
                    LOG.debug("taskInstId:", taskInstance.getTaskInstId());
                    result.put(taskInstance.getTaskInstId(), taskInstance.getTaskComment());
                }
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("result:", JSONObject.toJSONString(result));
        }
        return result;
    }

    public Page getOmsTaskByPage(PageParam pageParam) throws Exception{
       return page(pageParam);
    }

}
