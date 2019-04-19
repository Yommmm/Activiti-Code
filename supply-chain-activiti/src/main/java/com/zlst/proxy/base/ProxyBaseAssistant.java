package com.zlst.proxy.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.netflix.hystrix.HystrixCommand;
import com.zlst.exception.ZLSTException;
import com.zlst.navigator2.DefaultRestNavigatorImpl;
import com.zlst.navigator2.RestNavigator;
import com.zlst.navigator2.factory.HystrixSetterAdapter;
import com.zlst.navigator2.factory.HystrixSetterFactory;
import com.zlst.proxy.vo.ClientListenerRestFulRequest;
import com.zlst.proxy.vo.ClientRestFulRequest;
import com.zlst.proxy.vo.ClientRestFulResponse;
import com.zlst.common.ExceptionConstants;
import com.zlst.utils.ExceptionTypeEnum;
import com.zlst.utils.ResultUtil;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by songcj on 2017/9/12.
 */
public class ProxyBaseAssistant {

    private static Logger log = LoggerFactory.getLogger(ProxyBaseAssistant.class);

    //流程变量中订单标识KEY
    private static final String ORDER_ID = "orderId";

    private String serviceNameStr; //微服务名
    private String apiUrlStr;      //定义对象时传入的参数
    private String methodTypeStr;  //定义对象时传入的参数
    private String paramsStr;      //JSON格式的参数 

    private String url;            //最终发起HTTP请求的url
    private String content;        //最终发起HTTP请求的content
    private String commandKey;     //线程熔断的KEY

    public String getMethodTypeStr() {
        return methodTypeStr;
    }

    public void setMethodTypeStr(String methodTypeStr) {
        this.methodTypeStr = methodTypeStr;
    }

    public String getApiUrlStr() {
        return apiUrlStr;
    }

    public void setApiUrlStr(String apiUrlStr) {
        this.apiUrlStr = apiUrlStr;
    }

    public String getServiceNameStr() {
        return serviceNameStr;
    }

    public void setServiceNameStr(String serviceNameStr) {
        this.serviceNameStr = serviceNameStr;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getParamsStr() {
        return paramsStr;
    }

    public void setParamsStr(String paramsStr) {
        this.paramsStr = paramsStr;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCommandKey() {
        return commandKey;
    }

    public void setCommandKey(String commandKey) {
        this.commandKey = commandKey;
    }

    public ProxyBaseAssistant(String serviceNameStr, String apiUrlStr, String methodTypeStr, String paramsStr,String commandKey) {
        this.setServiceNameStr(serviceNameStr);
        this.setApiUrlStr(apiUrlStr);
        this.setMethodTypeStr(methodTypeStr);
        this.setParamsStr(paramsStr);
        this.setCommandKey(commandKey);
    }

    /**
     * 实现非监听代理及Expression Listener代理
     *
     * @param execution
     * @throws Exception
     */
    public void execute(DelegateExecution execution) throws Exception {

        //准备HTTP请求信息
        this.prepareHttpContent(execution);  //获取CONTENT

        //通过service发起请求
        String responseStr = this.executeServiceHttp();
        if (log.isDebugEnabled()) {
            log.debug("responseStr=" + responseStr == null ? "" : responseStr);
        }
        log.info("responseStr=" + responseStr == null ? "" : responseStr);

        //将结果存入到流程中
        processResult(execution, responseStr);
    }

    /**
     * 实现监听TaskListener代理
     *
     * @param delegateTask
     * @throws Exception
     */
    public void executeListener(DelegateTask delegateTask) throws Exception {
        //准备HTTP请求信息
        this.prepareListenerHttpContent(delegateTask);         //content

        //通过service发起请求
        String responseStr = this.executeServiceHttp();
        if (log.isDebugEnabled()) {
            log.debug("responseStr=" + responseStr == null ? "" : responseStr);
        }

        //将结果存入到流程中
        processListenerResult(delegateTask, responseStr);
    }

    /**
     * 处理Task监听结果，并将返回结果塞到流程变量中
     *
     * @param delegateTask
     * @param responseStr
     */
    private void processListenerResult(DelegateTask delegateTask, String responseStr) {
        if (ResultUtil.occurException(responseStr)) {
            recordOrderException(delegateTask.getExecution(), ExceptionTypeEnum.LISTENER_EXCEPTION.getExceptionType(), ResultUtil.getResponseStackTrace(responseStr));
            return;
        }

        ClientRestFulResponse clientRestFulResponse = JSON.parseObject(responseStr, ClientRestFulResponse.class);
        if (clientRestFulResponse != null &&
                clientRestFulResponse.getVariables() != null &&
                clientRestFulResponse.getVariables().size() > 0) {
            for (Map.Entry<String, Object> entry : clientRestFulResponse.getVariables().entrySet()) {
                delegateTask.setVariable(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * 处理非监听或Expression监听返回结果，并将结果塞到流程变量中
     *
     * @param execution
     * @param responseStr
     */
    private void processResult(DelegateExecution execution, String responseStr) throws Exception {
        if (ResultUtil.occurException(responseStr)) {
            recordOrderException(execution, ExceptionTypeEnum.TASK_EXCEPTION.getExceptionType(), ResultUtil.getResponseStackTrace(responseStr));
            throw new Exception(execution.getCurrentActivityName() + " occur exception.");
        }

        ClientRestFulResponse clientRestFulResponse = JSON.parseObject(responseStr, ClientRestFulResponse.class);

        if (clientRestFulResponse != null &&
                clientRestFulResponse.getVariables() != null &&
                clientRestFulResponse.getVariables().size() > 0) {
            for (Map.Entry<String, Object> entry : clientRestFulResponse.getVariables().entrySet()) {
                execution.setVariable(entry.getKey(), entry.getValue());
            }
        }
    }

    @Transactional
    private void recordOrderException(DelegateExecution execution, String exceptionType, String staceTrace) {
        ResultUtil.recordOrderException(execution.getProcessDefinitionId(), execution.getCurrentActivityName(), getOrderId(execution), exceptionType, staceTrace);
    }

    private String getOrderId(DelegateExecution execution) {
        String orderId = execution.getVariable(ORDER_ID) == null ? "" : execution.getVariable(ORDER_ID).toString();
        log.debug("orderId: ", orderId);
        return orderId;
    }


    /**
     * 通过TASK监听方法notify的参数delegateTask，获取请求外部Rest接口参数
     *
     * @param delegateTask
     * @return
     * @throws Exception
     */
    private ClientListenerRestFulRequest processListenerRequestObject(DelegateTask delegateTask) {
        DelegateExecution execution = delegateTask.getExecution();
        //拼接当前流程节点访问外部Rest服务的参数
        String orderId = getOrderId(execution);
        if (StringUtils.isBlank(orderId)) {
            throw new ZLSTException(ExceptionConstants.ORDERID_IS_NULL);
        }
        String taskInstanceId = delegateTask.getId();
        String taskInstanceName = delegateTask.getName();
        Map<String, Object> variables = execution.getVariables();
        ClientListenerRestFulRequest clientRestFulRequest = new ClientListenerRestFulRequest();
        clientRestFulRequest.setOrderId(orderId);
        clientRestFulRequest.setTaskInstanceId(taskInstanceId);
        ;
        clientRestFulRequest.setTaskInstanceName(taskInstanceName);
        //将paramsStr转化为Map<String,Object>并与原来的参数进行合并
        if (StringUtils.isNotBlank(paramsStr)) {
            @SuppressWarnings("serial")
            Map<String, Object> map = new Gson().fromJson(paramsStr, new TypeToken<HashMap<String, Object>>() {
            }.getType());
            variables.putAll(map);
        }
        clientRestFulRequest.setVariables(variables);
        return clientRestFulRequest;
    }

    /**
     * 通过方法execute的参数execution，获取请求外部Rest接口参数
     *
     * @param execution
     * @return
     * @throws Exception
     */
    private ClientRestFulRequest processRequestObject(DelegateExecution execution) throws Exception {
        //拼接当前流程节点访问外部Rest服务的参数
        String orderId = execution.getVariable(ORDER_ID) == null ? "" : execution.getVariable(ORDER_ID).toString();
        if (StringUtils.isBlank(orderId)) {
            throw new Exception("ORDER_ID为空，请重新配置");
        }
        String activitiId = execution.getCurrentActivityId();
        String activitiName = execution.getCurrentActivityName();
        Map<String, Object> variables = execution.getVariables();
        ClientRestFulRequest clientRestFulRequest = new ClientRestFulRequest();
        clientRestFulRequest.setOrderId(orderId);
        clientRestFulRequest.setActivitiId(activitiId);
        clientRestFulRequest.setActivitiName(activitiName);
        //将paramsStr转化为Map<String,Object>并与原来的参数进行合并
        if (StringUtils.isNotBlank(paramsStr)) {
            @SuppressWarnings("serial")
            Map<String, Object> map = new Gson().fromJson(paramsStr, new TypeToken<HashMap<String, Object>>() {
            }.getType());
            variables.putAll(map);
        }
        clientRestFulRequest.setVariables(variables);
        return clientRestFulRequest;
    }

    @SuppressWarnings("unused")
    private void processResultObject(DelegateExecution execution, ClientRestFulResponse clientRestFulResponse) {
        if (clientRestFulResponse != null &&
                clientRestFulResponse.getVariables() != null &&
                clientRestFulResponse.getVariables().size() > 0) {
            for (Map.Entry<String, Object> entry : clientRestFulResponse.getVariables().entrySet()) {
                execution.setVariable(entry.getKey(), entry.getValue());
            }
        }
    }


    /**
     * 将获取到即将调用非监听或Expression监听的REST接口的参数对象转化为GSON，传入到content字段
     *
     * @param execution
     * @throws Exception
     */
    private void prepareHttpContent(DelegateExecution execution) throws Exception {
        this.setContent(new Gson().toJson(processRequestObject(execution)));
    }

    /**
     * 将获取到即将调用Task监听REST接口的参数对象转化为GSON，传入到content字段
     *
     * @throws Exception
     */
    private void prepareListenerHttpContent(DelegateTask delegateTask) throws Exception {
        this.setContent(new Gson().toJson(processListenerRequestObject(delegateTask)));
    }

    /**
     * 根据参数具体执行HTTP请求  PUT/POST/GET/DELETE/
     *
     * @return
     * @throws Exception
     */
    private String executeServiceHttp() throws Exception {
        //日志信息
        log.info("executeServiceHttp params serviceNameStr=" + serviceNameStr == null ? "" : serviceNameStr);
        log.info("executeServiceHttp params apiUrlStr=" + apiUrlStr == null ? "" : apiUrlStr);
        log.info("executeServiceHttp params methodTypeStr=" + methodTypeStr == null ? "" : methodTypeStr);
        log.info("executeServiceHttp params content=" + content == null ? "" : content);
        //入参 校验
        if (StringUtils.isBlank(serviceNameStr) || StringUtils.isBlank(apiUrlStr) || StringUtils.isBlank(methodTypeStr)) {
            return null;
        }
        content = content == null ? "" : content; //
//        NavigatorConfig config = new NavigatorConfig()
//                .setServiceId(serviceNameStr)
//                .setAction(apiUrlStr)
//                .setJsonParams(JSONObject.parseObject(content));
//        NavigatorCmd nav = new NavigatorCmd(config,
//                new NavigatorAdapter()
//                        .setMode(NavigatorAdapter.MODEL.JSON) //传参模式现在有两种 form 和 Json
//                        .setRequestType(RequestType.valueOf(methodTypeStr.toUpperCase())) //请求类型
//                        .setTimeout(8 * 1000) //设置熔断超时秒数 默认6S 1.0 版本为3秒 单位毫秒
//        );
        //新版导航器改造
        HystrixSetterAdapter adapter;
        if(null == commandKey || "".equals(commandKey.trim())){
	    	 adapter = new HystrixSetterAdapter(serviceNameStr)
	         .withExecutionTimeoutInMilliseconds(10000) // 配置熔断超时时间
	         .withThreadCoreSize(50);  // 配置服务接口最大线程数
        }else{
	    	adapter = new HystrixSetterAdapter(serviceNameStr)
	        .withExecutionTimeoutInMilliseconds(10000) // 配置熔断超时时间
	        .withThreadCoreSize(50)  // 配置服务接口最大线程数
	        .withCommand(commandKey) // 配置线程commandKey
	        .withThread(commandKey); //线程KEY
        }
        HystrixCommand.Setter setter = HystrixSetterFactory.setter(adapter);
        RestNavigator<String> impl = new DefaultRestNavigatorImpl<>(String.class, serviceNameStr, apiUrlStr,setter);
        String result;
        if(methodTypeStr.toUpperCase().equals("POST")){
            result = impl.post(null,JSONObject.parseObject(content));
        }else if(methodTypeStr.toUpperCase().equals("GET")){
            result = impl.get(null,JSONObject.parseObject(content));
        }else if(methodTypeStr.toUpperCase().equals("PUT")){
            result = impl.put(null,JSONObject.parseObject(content));
        }else {//DELETE
            result = impl.delete(null,JSONObject.parseObject(content));
        }
        log.info("executeServiceHttp  result=" + result == null ? "" : result);
        return result;
    }

}
