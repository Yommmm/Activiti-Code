package com.zlst.module.order.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by 170066 on 2017/9/7.
 */
public class OmsOrderBatchRequest implements Serializable {

    private String busiType;//业务类型（必填）
    private String createOperId;//创建人（必填）
    private String createOrderOrgId;//订单创建部门标识（必填）
    private List<OrderListItem> orderList;
    private String parentOrderId;//上级订单标识（可选）

    public OmsOrderBatchRequest() {
    }

    public static class OrderListItem{

        private int orderPriority;//订单优先级（可选）
        private String orderSourceId;//订单来源标识（必填）
        private List<RestVariableItemVo> variables;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
        private Date wishedFinishDate;//期望完成时间（可选）

        public String getOrderSourceId() {
            return orderSourceId;
        }

        public void setOrderSourceId(String orderSourceId) {
            this.orderSourceId = orderSourceId;
        }

        public int getOrderPriority() {
            return orderPriority;
        }

        public void setOrderPriority(int orderPriority) {
            this.orderPriority = orderPriority;
        }

        public Date getWishedFinishDate() {
            return wishedFinishDate;
        }

        public void setWishedFinishDate(Date wishedFinishDate) {
            this.wishedFinishDate = wishedFinishDate;
        }

        public List<RestVariableItemVo> getVariables() {
            return variables;
        }

        public void setVariables(List<RestVariableItemVo> variables) {
            this.variables = variables;
        }
    }

    public String getBusiType() {
        return busiType;
    }

    public void setBusiType(String busiType) {
        this.busiType = busiType;
    }

    public String getCreateOperId() {
        return createOperId;
    }

    public void setCreateOperId(String createOperId) {
        this.createOperId = createOperId;
    }

    public String getCreateOrderOrgId() {
        return createOrderOrgId;
    }

    public void setCreateOrderOrgId(String createOrderOrgId) {
        this.createOrderOrgId = createOrderOrgId;
    }

    public String getParentOrderId() {
        return parentOrderId;
    }

    public void setParentOrderId(String parentOrderId) {
        this.parentOrderId = parentOrderId;
    }

    public List<OrderListItem> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<OrderListItem> orderList) {
        this.orderList = orderList;
    }
}
