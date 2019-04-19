package com.zlst.module.order.dao;

import com.zlst.database.core.dao.BaseRepository;
import com.zlst.module.order.bean.OmsActivityInstance;
import com.zlst.module.order.bean.OmsTaskInstance;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by songcj on 2017/9/21.
 */
@Repository
public interface OmsActivityInstanceRepository extends BaseRepository<OmsActivityInstance, String> {
    @Query(value = "select * from oms_activity_instance where order_id=:orderId", nativeQuery = true)
    List<OmsActivityInstance> queryActivityInstanceByOrderId(@Param("orderId") String orderId);
}
