package com.zlst.module.order.ctrl;


import com.zlst.database.core.ctrl.QueryAndOperateCtrl;
import com.zlst.module.order.bean.OmsOrderTemplateRule;
import com.zlst.module.order.service.OmsOrderTemplateService;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by user03 on 2017/7/10.
 */
@RestController
@RequestMapping("/omsOrderTemplate")
@EnableAutoConfiguration
public class OmsOrderTemplateCtrl extends QueryAndOperateCtrl<OmsOrderTemplateRule, OmsOrderTemplateService> {



}
