package com.zlst.module.logistics.dao;

import com.zlst.database.core.dao.BaseRepository;
import com.zlst.module.order.bean.OmsTaskInstance;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by songcj on 2017/9/21.
 */
@Repository
public interface LogisticsOrderRepository extends BaseRepository<OmsTaskInstance, String> {

    List<OmsTaskInstance> queryByStatusAndOrderId(String status, String orderId);
}
