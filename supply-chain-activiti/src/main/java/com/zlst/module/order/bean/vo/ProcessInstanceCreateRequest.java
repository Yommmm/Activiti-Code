/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zlst.module.order.bean.vo;

import java.util.List;

/**
 * Activit原生修改
 */
public class ProcessInstanceCreateRequest {

    private String processDefinitionId;
    private String processDefinitionKey;
    private String message;
    private String businessKey;
    private List<RestVariableItemVo> variables;
    private String tenantId;

    //Added by Ryan Johnston
    private boolean returnVariables;

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public void setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantId() {
        return tenantId;
    }

    //Added by Ryan Johnston
    public boolean getReturnVariables() {
        return returnVariables;
    }

    //Added by Ryan Johnston
    public void setReturnVariables(boolean returnVariables) {
        this.returnVariables = returnVariables;
    }

    public List<RestVariableItemVo> getVariables() {
        return variables;
    }

    public void setVariables(List<RestVariableItemVo> variables) {
        this.variables = variables;
    }
}
