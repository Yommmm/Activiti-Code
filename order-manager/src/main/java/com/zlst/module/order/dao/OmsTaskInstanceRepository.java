package com.zlst.module.order.dao;

import com.zlst.database.core.dao.BaseRepository;
import com.zlst.module.order.bean.OmsOrderTemplateRule;
import com.zlst.module.order.bean.OmsTaskInstance;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by songcj on 2017/9/21.
 */
@Repository
public interface OmsTaskInstanceRepository extends BaseRepository<OmsTaskInstance, String> {
    @Query(value = "select * from oms_task_instance where order_id=:orderId", nativeQuery = true)
    List<OmsTaskInstance> queryTaskInstanceByOrderId(@Param("orderId") String orderId);
}
