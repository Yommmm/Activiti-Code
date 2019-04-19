package com.zlst.module.order.service;

import com.zlst.database.core.service.QueryAndOperateServ;
import com.zlst.module.order.bean.OmsOrderTemplateRule;
import com.zlst.module.order.dao.OmsOrderTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Created by user03 on 2017/7/10.
 */
@Service
public class OmsOrderTemplateService extends QueryAndOperateServ<OmsOrderTemplateRule, OmsOrderTemplateRepository> {

    @Autowired
    private OmsOrderTemplateRepository omsOrderTemplateRepository;

    public OmsOrderTemplateRule queryOmsOrderTemplatebyBusiType(String busiType) {
        return omsOrderTemplateRepository.queryOmsOrderTemplatebyBusiType(busiType);
    }
}
