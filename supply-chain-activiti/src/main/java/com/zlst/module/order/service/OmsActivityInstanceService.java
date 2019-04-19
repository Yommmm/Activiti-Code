package com.zlst.module.order.service;

import com.alibaba.fastjson.JSONObject;
import com.zlst.database.core.service.QueryAndOperateServ;
import com.zlst.module.order.bean.OmsActivityInstance;
import com.zlst.module.order.bean.OmsTaskInstance;
import com.zlst.module.order.dao.OmsActivityInstanceRepository;
import com.zlst.module.order.dao.OmsTaskInstanceRepository;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by songcj on 2017/9/21.
 */
@Service
public class OmsActivityInstanceService extends QueryAndOperateServ<OmsActivityInstance, OmsActivityInstanceRepository> {

    private final static Logger LOG = LoggerFactory.getLogger(OmsActivityInstanceService.class);

    @Autowired
    private OmsActivityInstanceRepository omsActivityInstanceRepository;

    public Map<String, String> getUserInfoByOrderId(String orderId) {
        LOG.debug("getReceiveTaskUserInfoByOrderId start...");

        Map<String, String> result = new HashMap<String, String>();

        List<OmsActivityInstance> activityInstanceList = omsActivityInstanceRepository.queryActivityInstanceByOrderId(orderId);

        if (null != activityInstanceList && activityInstanceList.size() > 0) {
            for (OmsActivityInstance activityInstance : activityInstanceList) {
                if (StringUtils.isNotBlank(activityInstance.getUserName())) {
                    LOG.debug("userName:", activityInstance.getUserName());
                    result.put(activityInstance.getActId(), activityInstance.getUserName());
                }
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("result:", JSONObject.toJSONString(result));
        }
        return result;
    }


}
