package com.zlst.proxy;


import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zlst.proxy.base.ProxyBaseAssistant;
import com.zlst.proxy.base.ProxyHttpBaseAssistant;
//@Component
//@EnableAutoConfiguration
public class ServiceTaskProxy implements JavaDelegate {

    private static Logger log =  LoggerFactory.getLogger(ServiceTaskProxy.class);
    private Expression serviceName;    //微服务名称
    private Expression apiIp;          //对应配置文件的IP地址和端口
    private Expression apiUrl;         //API地址
    private Expression methodType;     //POST/GET/PUT/DELETE
    private Expression params;         //节点参数JSON格式
    //线程熔断保护的KEY，如果请求URL带路径/A/XX 形式，或者带？的URL不带"{}"的参数形式必传
    //否则会有内存泄漏的问题!
    private Expression commandKey;

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

    public Expression getCommandKey() {
        return commandKey;
    }

    public void setCommandKey(Expression commandKey) {
        this.commandKey = commandKey;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("ServiceTaskProxy execute serviceName="+(serviceName==null?"":serviceName.getValue(execution).toString()));
        log.info("ServiceTaskProxy execute apiIp="+(apiIp==null?"":apiIp.getValue(execution).toString()));
        log.info("ServiceTaskProxy execute apiUrl="+(apiUrl==null?"":apiUrl.getValue(execution).toString()));
        log.info("ServiceTaskProxy execute methodType="+(methodType==null?"":methodType.getValue(execution).toString()));
        log.info("ServiceTaskProxy execute params="+(params==null?"":params.getValue(execution).toString()));
        log.info("ServiceTaskProxy execute commandKey="+(commandKey==null?"":commandKey.getValue(execution).toString()));

        String businessName = execution.getVariable("business_eureka_name")==null?"":execution.getVariable("business_eureka_name").toString();
        log.info("ServiceTaskProxy execute businessName="+(businessName==null?"":businessName));
        String serviceNameStr = "";
    	if (businessName != null && !businessName.equals("")){
            serviceNameStr = businessName;
        }else{
            serviceNameStr = (serviceName==null?"":serviceName.getValue(execution).toString());
        }

        String apiIpStr = (apiIp==null?"":apiIp.getValue(execution).toString());
        String apiUrlStr = (apiUrl==null?"":apiUrl.getValue(execution).toString());
        String methodTypeStr = (methodType==null?"":methodType.getValue(execution).toString());
        String paramsStr = (params==null?"":params.getValue(execution).toString()); //不做校验
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
        	ProxyHttpBaseAssistant activitiHttpBase = new ProxyHttpBaseAssistant(apiIpStr,apiUrlStr,methodTypeStr,paramsStr);
            activitiHttpBase.execute(execution);
        }
        log.info("ServiceTaskProxy execute处理完成");
    }
}
