package com.zlst.proxy.base;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.zlst.proxy.vo.ClientListenerRestFulRequest;
import com.zlst.proxy.vo.ClientRestFulRequest;
import com.zlst.proxy.vo.ClientRestFulResponse;

/**
 * Created by songcj on 2017/9/12.
 */
public class ProxyHttpBaseAssistant {

    private static Logger log =  LoggerFactory.getLogger(ProxyHttpBaseAssistant.class);
    private static String restfulIpConfigPath = "restful-ipconfig";
    private static final String ORDER_ID  = "orderId";

    private String apiUrlStr;      //定义对象时传入的参数
    private String apiIpStr;       //定义对象时传入的参数
    private String methodTypeStr;  //定义对象时传入的参数
    private String paramsStr;      //JSON格式的参数 
    
    private String url;            //最终发起HTTP请求的url
    private String content;        //最终发起HTTP请求的content

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

    public String getApiIpStr() {
        return apiIpStr;
    }

    public void setApiIpStr(String apiIpStr) {
        this.apiIpStr = apiIpStr;
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

    public ProxyHttpBaseAssistant(String apiIpStr,String apiUrlStr, String methodTypeStr, String paramsStr){
        this.setApiIpStr(apiIpStr);
        this.setApiUrlStr(apiUrlStr);
        this.setMethodTypeStr(methodTypeStr);
        this.setParamsStr(paramsStr);
    }

    /**
     * 实现非监听代理及Expression Listener代理
     * @param execution
     * @throws Exception
     */
    public void execute(DelegateExecution execution) throws Exception{

        //准备HTTP请求信息
        this.prepareHttpUrlInfo(execution);  //获取URL
        this.prepareHttpContent(execution);  //获取CONTENT
        if(log.isDebugEnabled()){
            log.debug("url="+url);
            log.debug("methodType="+methodTypeStr);
            log.debug("content="+content);
        }

        /**
         ClientRestFulResponse response = null;
         if(RequestMethod.GET.name().equals(methodTypeStr)){
         response = this.restTemplate.getForObject(url, ClientRestFulResponse.class);
         }else if(RequestMethod.DELETE.name().equals(methodTypeStr)){
         this.restTemplate.delete(url);
         }else if(RequestMethod.PUT.name().equals(methodTypeStr)){
         ClientRestFulRequest clientRestFulRequest  = this.processRequestObject(execution);
         HttpEntity<ClientRestFulRequest> httpEntity = new HttpEntity<ClientRestFulRequest>(clientRestFulRequest);
         ResponseEntity<ClientRestFulResponse> responseEntity = this.restTemplate.exchange(url,HttpMethod.PUT,httpEntity,
         ClientRestFulResponse.class);
         response = responseEntity.getBody();
         }else if(RequestMethod.POST.name().equals(methodTypeStr)){
         response = this.restTemplate.postForObject(url, content, ClientRestFulResponse.class);
         }else{}

         if(response!=null){
         this.processResultObject(execution,response);
         }

         if(log.isDebugEnabled()){
         log.debug("response="+response==null?"":new Gson().toJson(response));
         }
         **/

        //发起HTTP请求
        CloseableHttpResponse response = this.executeHttp();

        //解析HTTP服务返回结果
        //暂时先只处理状态码为200的正确返回  2XX的返回应该都表示成功
        if(response!=null&&response.getStatusLine().getStatusCode()==200){
            try{
                HttpEntity entity = response.getEntity();
                if(entity!= null){
                    InputStream instream = entity.getContent();
                    try{
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(instream));
                        StringBuffer strBuffer = new StringBuffer();
                        String line = "";
                        while ((line = bufferedReader.readLine()) != null) {
                            strBuffer.append(line);
                        }
                        //做一些有用的事情
                        processResult(execution,strBuffer.toString());
                        log.debug("StringBuffer = "+ strBuffer.toString());
                    } finally {
                        instream.close();
                    }
                }
            } finally {
                response.close();
            }
        }else{
            StringBuffer exceptionStrBuffer = new StringBuffer();
            if(response!=null){
                exceptionStrBuffer.append("StatusCode=").append(response.getStatusLine().getStatusCode());
                exceptionStrBuffer.append(" ");
                exceptionStrBuffer.append("Message=").append(response.getStatusLine().getReasonPhrase());
            }else{
                exceptionStrBuffer.append("Response=null");
            }
            throw new Exception(exceptionStrBuffer.toString());
        }
    }

    /**
     * 实现监听TaskListener代理
     * @param delegateTask
     * @throws Exception
     */
    public void executeListener(DelegateTask delegateTask) throws Exception{
        //准备HTTP请求信息
        this.prepareHttpUrlInfo(delegateTask.getExecution());  //URL
        this.prepareListenerHttpContent(delegateTask);         //content
        if(log.isDebugEnabled()) {
            log.debug("url=" + url);
            log.debug("methodType=" + methodTypeStr);
            log.debug("content=" + content);
        }
        
        //发起HTTP请求
        CloseableHttpResponse response  = this.executeHttp();
        //解析HTTP返回信息
        //暂时先只处理状态码为200的正确返回  2XX的返回应该都表示成功
        if(response!=null&&response.getStatusLine().getStatusCode()==200){
            try{
                HttpEntity entity = response.getEntity();
                if(entity!= null){
                    InputStream instream = entity.getContent();
                    try{
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(instream));
                        StringBuffer strBuffer = new StringBuffer();
                        String line = "";
                        while ((line = bufferedReader.readLine()) != null) {
                            strBuffer.append(line);
                        }
                        //做一些有用的事情
                        processListenerResult(delegateTask,strBuffer.toString());
                        log.debug("StringBuffer = "+ strBuffer.toString());
                    } finally {
                        instream.close();
                    }
                }
            } finally {
                response.close();
            }
        }else{
            StringBuffer exceptionStrBuffer = new StringBuffer();
            if(response!=null){
                exceptionStrBuffer.append("StatusCode=").append(response.getStatusLine().getStatusCode());
                exceptionStrBuffer.append(" ");
                exceptionStrBuffer.append("Message=").append(response.getStatusLine().getReasonPhrase());
            }else{
                exceptionStrBuffer.append("Response=null");
            }
            throw new Exception(exceptionStrBuffer.toString());
        }
    }

    /**
     * 处理Task监听结果，并将返回结果塞到流程变量中
     * @param delegateTask
     * @param result
     */
    private void processListenerResult(DelegateTask delegateTask,String result){
        Gson gson = new Gson();
        ClientRestFulResponse clientRestFulResponse = gson.fromJson(result,ClientRestFulResponse.class);
        if(clientRestFulResponse!=null&&
                clientRestFulResponse.getVariables()!=null&&
                clientRestFulResponse.getVariables().size()>0) {
            for (Map.Entry<String, Object> entry : clientRestFulResponse.getVariables().entrySet()) {
                delegateTask.setVariable(entry.getKey(),entry.getValue());
            }
        }
    }

    /**
     * 处理非监听或Expression监听返回结果，并将结果塞到流程变量中
     * @param execution
     * @param result
     */
    private void processResult(DelegateExecution execution,String result){
        Gson gson = new Gson();
        ClientRestFulResponse clientRestFulResponse = gson.fromJson(result,ClientRestFulResponse.class);
        if(clientRestFulResponse!=null&&
                clientRestFulResponse.getVariables()!=null&&
                clientRestFulResponse.getVariables().size()>0) {
            for (Map.Entry<String, Object> entry : clientRestFulResponse.getVariables().entrySet()) {
                execution.setVariable(entry.getKey(),entry.getValue());
            }
        }
    }

    /**
     * 通过TASK监听方法notify的参数delegateTask，获取请求外部Rest接口参数
     * @param delegateTask
     * @return
     * @throws Exception 
     */
    private ClientListenerRestFulRequest processListenerRequestObject(DelegateTask delegateTask) throws Exception{
    	DelegateExecution execution = delegateTask.getExecution();
        //拼接当前流程节点访问外部Rest服务的参数
        String orderId = execution.getVariable(ORDER_ID)==null?"":execution.getVariable(ORDER_ID).toString();
        if(StringUtils.isBlank(orderId)){
        	throw new Exception("ORDER_ID为空，请重新配置");
        }
        String taskInstanceId = delegateTask.getId();
        String taskInstanceName = delegateTask.getName();
        Map<String,Object> variables = execution.getVariables();
        ClientListenerRestFulRequest clientRestFulRequest = new ClientListenerRestFulRequest();
        clientRestFulRequest.setOrderId(orderId);
        clientRestFulRequest.setTaskInstanceId(taskInstanceId);;
        clientRestFulRequest.setTaskInstanceName(taskInstanceName);
        //将paramsStr转化为Map<String,Object>并与原来的参数进行合并
        if(StringUtils.isNotBlank(paramsStr)){
        	@SuppressWarnings("serial")
			Map<String, Object> map = new Gson().fromJson(paramsStr, new TypeToken<HashMap<String, Object>>() {}.getType());
        	variables.putAll(map);
        }
        clientRestFulRequest.setVariables(variables);
        return  clientRestFulRequest;
    }

    /**
     * 通过方法execute的参数execution，获取请求外部Rest接口参数
     * @param execution
     * @return
     * @throws Exception 
     */
    private ClientRestFulRequest processRequestObject(DelegateExecution execution) throws Exception{
        //拼接当前流程节点访问外部Rest服务的参数
        String orderId = execution.getVariable(ORDER_ID)==null?"":execution.getVariable(ORDER_ID).toString();
        if(StringUtils.isBlank(orderId)){
        	throw new Exception("ORDER_ID为空，请重新配置");
        }
        String activitiId = execution.getCurrentActivityId();
        String activitiName = execution.getCurrentActivityName();
        Map<String,Object> variables = execution.getVariables();
        ClientRestFulRequest clientRestFulRequest = new ClientRestFulRequest();
        clientRestFulRequest.setOrderId(orderId);
        clientRestFulRequest.setActivitiId(activitiId);
        clientRestFulRequest.setActivitiName(activitiName);
        //将paramsStr转化为Map<String,Object>并与原来的参数进行合并
        if(StringUtils.isNotBlank(paramsStr)){
        	@SuppressWarnings("serial")
			Map<String, Object> map = new Gson().fromJson(paramsStr, new TypeToken<HashMap<String, Object>>() {}.getType());
        	variables.putAll(map);
        }
        clientRestFulRequest.setVariables(variables);
        return  clientRestFulRequest;
    }

    @SuppressWarnings("unused")
	private void processResultObject(DelegateExecution execution,ClientRestFulResponse clientRestFulResponse){
        if(clientRestFulResponse!=null&&
                clientRestFulResponse.getVariables()!=null&&
                clientRestFulResponse.getVariables().size()>0) {
            for (Map.Entry<String, Object> entry : clientRestFulResponse.getVariables().entrySet()) {
                execution.setVariable(entry.getKey(),entry.getValue());
            }
        }
    }

    /**
     * 通过DelegateExecution获取配置的URL相关参数，并结合配置文件拼接出URL,最后传到本对象的url属性
     * @param execution
     */
    private void prepareHttpUrlInfo(DelegateExecution execution){
        //地址配置文件
        ResourceBundle resource = ResourceBundle.getBundle(restfulIpConfigPath);
        //获取完整的URL
        StringBuffer urlBuffer = new StringBuffer();
        //获取IP及端口
        if(apiIpStr==null){
            urlBuffer.append(resource.getString("localhost")); //默认取本地的ip和端口
        }else{
            urlBuffer.append(resource.getString(apiIpStr));
        }
        //获取API地址
        urlBuffer.append(apiUrlStr);  //apiRrl中可能已经存在 参数等信息
        this.setUrl(urlBuffer.toString());
    }

    /**
     * 将获取到即将调用非监听或Expression监听的REST接口的参数对象转化为GSON，传入到content字段
     * @param execution
     * @throws Exception 
     */
    private void prepareHttpContent(DelegateExecution execution) throws Exception{
    	this.setContent(new Gson().toJson(processRequestObject(execution)));
    }

    /**
     * 将获取到即将调用Task监听REST接口的参数对象转化为GSON，传入到content字段
     * @throws Exception 
     */
    private void prepareListenerHttpContent(DelegateTask delegateTask) throws Exception{
    	this.setContent(new Gson().toJson(processListenerRequestObject(delegateTask)));
    }

    /**
     * 根据参数具体执行HTTP请求  PUT/POST/GET/DELETE/
     * @return
     * @throws Exception
     */
    private CloseableHttpResponse executeHttp() throws Exception{
        CloseableHttpClient httpClient = HttpClients.createDefault();   //后续可以对超时等进行修改
        //String methodTypeStr = methodType==null?"":methodType.getValue(execution).toString();
        CloseableHttpResponse response = null;
        if(RequestMethod.GET.name().equals(methodTypeStr)){
            HttpGet httpGet = new HttpGet(url);
            response = httpClient.execute(httpGet);
        }else if(RequestMethod.POST.name().equals(methodTypeStr)){
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");
            StringEntity entity = new StringEntity(content,"UTF-8");
            entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_ENCODING,"UTF-8"));
            httpPost.setEntity(entity);
            response = httpClient.execute(httpPost);
        }else if(RequestMethod.PUT.name().equals(methodTypeStr)){
            HttpPut httpPut = new HttpPut(url);
            httpPut.addHeader(HTTP.CONTENT_TYPE, "application/json");
            StringEntity entity = new StringEntity(content,"UTF-8");
            entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_ENCODING,"UTF-8"));
            httpPut.setEntity(entity);
            response = httpClient.execute(httpPut);
        }else if(RequestMethod.DELETE.name().equals(methodTypeStr)){
            HttpDelete httpDelete = new HttpDelete(url);
            response = httpClient.execute(httpDelete);
        }else{
        }
        return response;

        /**
        //修改为通过微服务网关的调用
        JSONObject json = new JSONObject();
        json.put("roleName", "角色名称");
        json.put("remark", "备注信息");
        NavigatorConfig config = new NavigatorConfig()
                .setServiceId("microservice-zlst-manage-system")
                .setAction("/system/roles")
                .setJsonParams(json);
        NavigatorCmd nav = new NavigatorCmd(config,
                new NavigatorAdapter()
                        .setMode(NavigatorAdapter.MODEL.JSON) //传参模式现在有两种 form 和 Json
                        .setRequestType(RequestType.POST) //请求类型
                        .setTimeout(8*1000) //设置熔断超时秒数 默认6S 1.0 版本为3秒 单位毫秒
        );
        String result = nav.execute();
         **/
    }

}
