package com.zlst.module.order.ctrl;

import com.zlst.database.core.ctrl.QueryAndOperateCtrl;
import com.zlst.exception.ZLSTException;
import com.zlst.module.order.bean.OmsActivityInstance;
import com.zlst.module.order.bean.vo.CreateActivityInstRequestVO;
import com.zlst.module.order.service.OmsActivityInstanceService;
import com.zlst.common.ExceptionConstants;
import com.zlst.utils.ResultUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/order")
@EnableAutoConfiguration
public class ActivityInstanceMgrCtrl extends QueryAndOperateCtrl<OmsActivityInstance, OmsActivityInstanceService> {

    private static final Logger LOG = LoggerFactory.getLogger(ActivityInstanceMgrCtrl.class);

    @Autowired
    public OmsActivityInstanceService omsActivityInstanceService;

    @PostMapping(value = "/activity")
    public Object save(@RequestBody CreateActivityInstRequestVO createActivityInstRequestVO){
        LOG.debug("save start...");
        if(createActivityInstRequestVO ==null||StringUtils.isBlank(createActivityInstRequestVO.getOrderId())){
            throw  new ZLSTException(ExceptionConstants.REQ_IS_NULL);
        }
        OmsActivityInstance omsActivityInstance = new OmsActivityInstance();
        BeanUtils.copyProperties(createActivityInstRequestVO, omsActivityInstance);
        omsActivityInstance.setCreateTime(new Date());
        try {
            omsActivityInstanceService.save(omsActivityInstance);
        } catch (Exception e) {
            throw new ZLSTException(e,ExceptionConstants.SAVE_FAILURE);
        }
        LOG.debug("save end...");

        return ResultUtil.buildNormalResult();
    }
}
