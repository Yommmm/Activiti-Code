package com.zlst.module.order.service;

import com.zlst.database.core.service.QueryAndOperateServ;
import com.zlst.module.order.bean.OmsTaskDefine;
import com.zlst.module.order.dao.OmsTaskDefineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by songcj on 2017/9/21.
 */
@Service
public class OmsTaskDefineService extends QueryAndOperateServ<OmsTaskDefine, OmsTaskDefineRepository> {

    @Autowired
    private OmsTaskDefineRepository omsTaskDefineRepository;

    /**
     * 通过ProcDefId和TaskDefId查询InfTaskDefine记录
     * @param omsTaskDefine
     * @return
     */
    public OmsTaskDefine queryInfTaskDefineByProcessAndTaskId(OmsTaskDefine omsTaskDefine){
        return omsTaskDefineRepository.queryInfTaskDefineByProcessAndTaskId(omsTaskDefine.getProcDefId(),
                omsTaskDefine.getTaskDefId());
    }

}
