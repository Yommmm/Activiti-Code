package com.zlst.proxy;


import com.zlst.proxy.base.ProxyBaseAssistant;
import com.zlst.proxy.base.ProxyHttpBaseAssistant;
import org.activiti.engine.delegate.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@Component
//@EnableAutoConfiguration
@SuppressWarnings("serial")
public class TaskListenerProxy implements JavaDelegate,TaskListener {

    private static Logger log =  LoggerFactory.getLogger(TaskListenerProxy.class);
    private Expression serviceName;    //微服务名称
    private Expression apiIp;          //对应配置文件的IP地址和端口
    private Expression methodType;     //POST/GET/PUT/DELETE
    private Expression apiUrl;         //API地址
    private Expression params;         //节点参数JSON格式
    //线程熔断保护的KEY，如果请求URL带路径/A/XX 形式，或者带？的URL不带"{}"的参数形式必传
    //否则会有内存泄漏的问题!
    private Expression commandKey;

    //ServiceTaskProxy;
    //TaskListenerProxy;
    //ProxyBaseAssistant;
    
    public Expression getServiceName() {
		return serviceName;
	}

	public void setServiceName(Expression serviceName) {
		this.serviceName = serviceName;
	}

	public void setApiIp(Expression apiIp) {
        this.apiIp = apiIp;
    }

    public void setApiUrl(Expression apiUrl) {
        this.apiUrl = apiUrl;
    }

    public void setMethodType(Expression methodType) {
        this.methodType = methodType;
    }
    
    public void setParams(Expression params) {
        this.params = params;
    }

    public Expression getCommandKey() {
        return commandKey;
    }

    public void setCommandKey(Expression commandKey) {
        this.commandKey = commandKey;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        if(log.isDebugEnabled()){
        	log.debug("serviceName="+(serviceName==null?"":serviceName.getValue(execution).toString()));
        	log.debug("apiUrl="+(apiUrl==null?"":apiUrl.getValue(execution).toString()));
            log.debug("apiIp="+(apiIp==null?"":apiIp.getValue(execution).toString()));
            log.debug("methodType="+(methodType==null?"":methodType.getValue(execution).toString()));
            log.debug("params="+(params==null?"":params.getValue(execution).toString()));
            log.debug("TaskListenerProxy execute commandKey="+(commandKey==null?"":commandKey.getValue(execution).toString()));
        }

        String businessName = execution.getVariable("business_eureka_name")==null?"":execution.getVariable("business_eureka_name").toString();
        String serviceNameStr = "";
        if (businessName != null && !businessName.equals("")){
            serviceNameStr = businessName;
        }else{
            serviceNameStr = (serviceName==null?"":serviceName.getValue(execution).toString());
        }

        String apiIpStr = (apiIp==null?"":apiIp.getValue(execution).toString());
        String apiUrlStr = (apiUrl==null?"":apiUrl.getValue(execution).toString());
        String methodTypeStr = (methodType==null?"":methodType.getValue(execution).toString());
        String paramsStr = (params==null?"":params.getValue(execution).toString());
        String commandKeyStr = (commandKey==null?null:commandKey.getValue(execution).toString());//不做校验，除非URL以路径形式和参数形式

        //校验参数
        if((!(StringUtils.isNotBlank(serviceNameStr)||StringUtils.isNotBlank(apiIpStr)))||StringUtils.isBlank(apiUrlStr)||StringUtils.isBlank(methodTypeStr)){
            throw new Exception("参数校验不合格");
        }

        if(StringUtils.isNotBlank(serviceNameStr)){
        	//采用SERVICE方式访问
        	ProxyBaseAssistant  proxyMicroServiceBase = new ProxyBaseAssistant(serviceNameStr,apiUrlStr,methodTypeStr,paramsStr,commandKeyStr);
        	proxyMicroServiceBase.execute(execution);
        }else{
        	//采用IP方式访问
        	ProxyHttpBaseAssistant proxyHttpBase = new ProxyHttpBaseAssistant(apiIpStr,apiUrlStr,methodTypeStr,paramsStr);
        	proxyHttpBase.execute(execution);
        }

        if(log.isDebugEnabled()){
            log.debug("监听代理execute处理完成");
        }
    }

    @Override
    public void notify(DelegateTask delegateTask){
        DelegateExecution execution = delegateTask.getExecution();
        if(log.isDebugEnabled()){
        	log.debug("serviceName="+(serviceName==null?"":serviceName.getValue(execution).toString()));
        	log.debug("apiUrl="+(apiUrl==null?"":apiUrl.getValue(execution).toString()));
            log.debug("apiIp="+(apiIp==null?"":apiIp.getValue(execution).toString()));
            log.debug("methodType="+(methodType==null?"":methodType.getValue(delegateTask).toString()));
            log.debug("params="+(params==null?"":params.getValue(execution).toString()));
            log.debug("TaskListenerProxy notify commandKey="+(commandKey==null?"":commandKey.getValue(execution).toString()));
        }
        
        String serviceNameStr = (serviceName==null?"":serviceName.getValue(execution).toString());
        String apiIpStr = (apiIp==null?"":apiIp.getValue(execution).toString());
        String apiUrlStr = (apiUrl==null?"":apiUrl.getValue(execution).toString());
        String methodTypeStr = (methodType==null?"":methodType.getValue(execution).toString());
        String paramsStr = (params==null?"":params.getValue(execution).toString());
        String commandKeyStr = (commandKey==null?null:commandKey.getValue(execution).toString());//不做校验，除非URL以路径形式和参数形式
        
        //校验参数
        if((!(StringUtils.isNotBlank(serviceNameStr)||StringUtils.isNotBlank(apiIpStr)))||StringUtils.isBlank(apiUrlStr)||StringUtils.isBlank(methodTypeStr)){
            log.info("参数校验不合格");
            return;
        }
        
        try{
        if(StringUtils.isNotBlank(serviceNameStr)){
        	//采用SERVICE方式访问
        	ProxyBaseAssistant  proxyMicroServiceBase = new ProxyBaseAssistant(serviceNameStr,apiUrlStr,methodTypeStr,paramsStr,commandKeyStr);
        	proxyMicroServiceBase.executeListener(delegateTask);
        }else{
        	//采用IP方式访问
        	ProxyHttpBaseAssistant proxyHttpBase = new ProxyHttpBaseAssistant(apiIpStr,apiUrlStr,methodTypeStr,paramsStr);
        	proxyHttpBase.executeListener(delegateTask);
        }
        }catch(Exception e){
        	log.info("监听代理notify异常");
        	e.printStackTrace();
        }
        
        if(log.isDebugEnabled()){
            log.debug("监听代理notify处理完成");
        }
    }
}
