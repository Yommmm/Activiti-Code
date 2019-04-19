package com.zlst.module.order.ctrl;

import com.zlst.database.core.ctrl.QueryAndOperateCtrl;
import com.zlst.module.order.bean.OmsTaskDefine;
import com.zlst.module.order.service.OmsTaskDefineService;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by songcj on 2017/9/20.
 */
@RestController
@RequestMapping("/omsTaskDefine")
@EnableAutoConfiguration
public class OmsTaskDefineCtrl extends QueryAndOperateCtrl<OmsTaskDefine, OmsTaskDefineService> {

}
