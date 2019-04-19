package com.zlst.module.order.ctrl;

import com.zlst.database.core.ctrl.QueryAndOperateCtrl;
import com.zlst.module.order.bean.OmsOrderException;
import com.zlst.module.order.service.OmsOrderExceptionService;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by 170079 on 2017/10/16.
 */
@RestController
@RequestMapping("/omsOrderException")
@EnableAutoConfiguration
public class OmsOrderExceptionCtrl extends QueryAndOperateCtrl<OmsOrderException,OmsOrderExceptionService> {
}
