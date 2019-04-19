package com.zlst.module.order.bean.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * OrderVaribles接口返回结果对象
 */
public class OrderVariblesGetReponse {

    private List<RestVariableItemVo> variables;

    public List<RestVariableItemVo> getVariables() {
        return variables;
    }

    public void setVariables(List<RestVariableItemVo> variables) {
        this.variables = variables;
    }
}
