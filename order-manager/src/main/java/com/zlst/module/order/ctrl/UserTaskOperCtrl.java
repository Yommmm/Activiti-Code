package com.zlst.module.order.ctrl;

import com.zlst.exception.ZLSTException;
import com.zlst.module.order.bean.vo.TaskActionRequest;
import com.zlst.module.order.service.OrderMgrService;
import com.zlst.utils.ExceptionConstants;
import com.zlst.utils.ResultUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;


@RestController
@RequestMapping("/runtime")
@EnableAutoConfiguration
public class UserTaskOperCtrl {

    private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(UserTaskOperCtrl.class);

    @Autowired
    private OrderMgrService orderMgrService;

    /**
     * 人工节点操作
     * @return
     */
    @PostMapping(value = "/tasks/{taskId}")
    public Object userTaskOperation(@PathVariable String taskId,
                                    @RequestBody TaskActionRequest taskActionRequest){
        LOG.debug("userTaskOperation start...taskId:",taskId);

        //1.校验入参 1>.taskId不为空 2>.taskActionRequest不为空 3>.taskActionRequest.action目前仅支持claim-认领，complete-完成
        if(StringUtils.isBlank(taskId)){
            throw new ZLSTException(ExceptionConstants.ERRORCODE_100208);
        }
        if(taskActionRequest==null){
            throw new ZLSTException(ExceptionConstants.ERRORCODE_100209);
        }
        if(!TaskActionRequest.ACTION_COMPLETE.equals(taskActionRequest.getAction())&&
                !TaskActionRequest.ACTION_CLAIM.equals(taskActionRequest.getAction())){
            throw new ZLSTException(ExceptionConstants.ERRORCODE_100210,new Object[]{taskActionRequest.getAction()});
        }

        this.orderMgrService.executeTaskAction(taskId, taskActionRequest);

        LOG.debug("userTaskOperation end.");

        return ResultUtil.buildNormalResult();
    }

}
