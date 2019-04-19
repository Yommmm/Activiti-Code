package com.zlst.module.order.service;

import com.zlst.database.core.service.QueryAndOperateServ;
import com.zlst.module.order.bean.OmsOrderException;
import com.zlst.module.order.dao.OmsOrderExceptionRepository;
import com.zlst.module.order.dao.OmsTaskInstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by 170079 on 2017/10/16.
 */
@Service
public class OmsOrderExceptionService extends QueryAndOperateServ<OmsOrderException, OmsOrderExceptionRepository> {

    @Autowired
    private OmsOrderExceptionRepository omsOrderExceptionRepository;

}
