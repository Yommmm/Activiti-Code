package com.zlst.module.order.dao;

import com.zlst.database.core.dao.BaseRepository;
import com.zlst.module.order.bean.OmsOrder;
import com.zlst.module.order.bean.OmsOrderTemplateRule;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by user03 on 2017/7/10.
 */
@Repository
public interface OmsOrderRepository extends BaseRepository<OmsOrder, String> {

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE oms_order SET process_inst_id=:processInstId where order_id=:orderId", nativeQuery = true)
    void updateOmsOrderbyOrderId(@Param("processInstId") String processInstId, @Param("orderId") String orderId);

    @Query(value = "select * from oms_order where order_status=:orderStatus and create_time<=DATE_SUB(NOW(), " +
            "interval :delaySecond second) ORDER BY create_time desc limit :orderLimit",nativeQuery = true)
    List<OmsOrder> queryOmsOrderbyOrderStatus(@Param("orderStatus") String orderStatus, @Param("delaySecond") int delaySecond, @Param("orderLimit") int orderLimit);

//    @Query(value = "select * from oms_order where process_inst_id=:processInstId limit 1", nativeQuery = true)
//    OmsOrder queryOmsOrderbyProcessInstId(@Param("processInstId") String processInstId);

    List<OmsOrder> queryOmsOrderByProcessInstId(@Param("processInstId") String processInstId);

    @Query(value = "select count(1) from oms_order where busi_type=:busiType and order_source_id=:orderSourceId", nativeQuery = true)
    int queryOmsOrderbyOrderSourceId(@Param("orderSourceId") String orderSourceId, @Param("busiType") String busiType);

//    @Query(value = "select * from oms_order where order_id=:orderId limit 1", nativeQuery = true)
//    OmsOrder queryOmsOrderbyOrderId(@Param("orderId") String orderId);
}