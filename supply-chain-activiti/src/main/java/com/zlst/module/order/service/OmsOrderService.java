package com.zlst.module.order.service;

import com.zlst.database.core.service.QueryAndOperateServ;
import com.zlst.module.order.bean.OmsOrder;
import com.zlst.module.order.bean.vo.OmsOrderRequest;
import com.zlst.module.order.dao.OmsOrderRepository;
import com.zlst.utils.CommonUtils;
import com.zlst.utils.OrderStatusEnum;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;
import java.util.List;


/**
 * Created by user03 on 2017/7/10.
 */
@Service
public class OmsOrderService extends QueryAndOperateServ<OmsOrder, OmsOrderRepository> {

    @Autowired
    private OmsOrderRepository omsOrderRepository;

    /**
     * 新增
     *
     * @return
     * @throws Exception
     */
    @Transactional
    public OmsOrder saveOmsOrder(OmsOrderRequest omsOrderRequest) throws Exception {
        OmsOrder omsOrder = new OmsOrder();
        omsOrder.setBusiType(omsOrderRequest.getBusiType());
        omsOrder.setParentOrderId(omsOrderRequest.getParentOrderId());
        omsOrder.setOrderSourceId(omsOrderRequest.getExternalTaskId());
        if (StringUtils.isNotEmpty(omsOrderRequest.getCreateOperId())) {
            omsOrder.setCreatedBy(omsOrderRequest.getCreateOperId());
        } else {
            omsOrder.setCreatedBy(CommonUtils.getHeadUserId());
        }

        omsOrder.setOrderPri(omsOrderRequest.getOrderPriority());
        omsOrder.setWishedFinishDate(omsOrderRequest.getWishedFinishDate());
        if(StringUtils.isNotEmpty(omsOrderRequest.getCreateOrderOrgId())) {
            omsOrder.setOrderCreateOrgId(omsOrderRequest.getCreateOrderOrgId());
        }
        else{
            omsOrder.setOrderCreateOrgId(CommonUtils.getOrgId());
        }

        omsOrder.setModifiedBy(omsOrder.getCreatedBy());
        omsOrder.setOrderStatus(OrderStatusEnum.CANCEL.getStatus());//创建中
        omsOrder.setRemark("新增");
        omsOrderRepository.saveAndFlush(omsOrder);
        return omsOrder;
    }

    /**
     * 简单新增
     *
     * @return
     * @throws Exception
     */
    public OmsOrder saveOmsOrderSimp(OmsOrder omsOrder) throws Exception {
     return omsOrderRepository.save(omsOrder);
     }

     /**
     * 批量更新
     *
     * @param omsOrders
     * @throws Exception
     */
    public void updateOmsBatchOrder(List<OmsOrder> omsOrders) throws Exception {
        for (OmsOrder omsOrder : omsOrders) {
            omsOrderRepository.updateOmsOrderbyOrderId(omsOrder.getProcessInstId(), omsOrder.getOrderId());
        }
    }


    public List<OmsOrder> queryOmsOrderbyOrderStatus(String orderStatus, int delaySecond, int orderLimit) throws Exception {
        return omsOrderRepository.queryOmsOrderbyOrderStatus(orderStatus, delaySecond, orderLimit);
    }

    /**
     * 根据流程实例ID查询订单信息
     *
     * @param processInstId
     * @return
     */
    public List<OmsOrder> queryOmsOrderbyProcessInstId(String processInstId) {
        return omsOrderRepository.queryOmsOrderByProcessInstId(processInstId);
    }

    /**
     * @param orderId
     * @return
     */
    public OmsOrder queryOmsOrderbyOrderId(String orderId) {
        return omsOrderRepository.findOne(orderId);
//        return omsOrderRepository.queryOmsOrderbyOrderId(orderId);
    }

    /**
     * @param orderSourceId 订单来源Id
     * @param busiType      业务类型
     * @return
     */
    public int queryOmsOrderbyOrderSourceId(String orderSourceId, String busiType) {
        return omsOrderRepository.queryOmsOrderbyOrderSourceId(orderSourceId, busiType);
    }
}
