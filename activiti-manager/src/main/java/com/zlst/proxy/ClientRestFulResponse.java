package com.zlst.proxy;

import java.util.Map;

/**
 * Created by songcj on 2017/9/11.
 */
public class ClientRestFulResponse {

    private Map<String,Object> variables;

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }
}
