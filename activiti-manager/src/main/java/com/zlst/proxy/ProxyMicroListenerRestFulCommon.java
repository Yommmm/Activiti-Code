package com.zlst.proxy;


import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.delegate.TaskListener;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@Component
//@EnableAutoConfiguration
@SuppressWarnings("serial")
public class ProxyMicroListenerRestFulCommon implements JavaDelegate,TaskListener {

    private static Logger log =  LoggerFactory.getLogger(ProxyMicroListenerRestFulCommon.class);
    private Expression apiIp;          //对应配置文件的IP地址和端口
    private Expression methodType;     //POST/GET/PUT/DELETE
    private Expression apiUrl;         //API地址
    private Expression params;         //节点参数JSON格式

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
    
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        if(log.isDebugEnabled()){
            log.debug("methodType="+(methodType==null?"":methodType.getValue(execution).toString()));
            log.debug("apiUrl="+(apiUrl==null?"":apiUrl.getValue(execution).toString()));
            log.debug("apiIp="+(apiIp==null?"":apiIp.getValue(execution).toString()));
        }
        String apiIpStr = (apiIp==null?"":apiIp.getValue(execution).toString());
        String apiUrlStr = (apiUrl==null?"":apiUrl.getValue(execution).toString());
        String methodTypeStr = (methodType==null?"":methodType.getValue(execution).toString());
        String paramsStr = (params==null?"":params.getValue(execution).toString());

        //校验参数
        if(StringUtils.isBlank(apiIpStr)||StringUtils.isBlank(apiUrlStr)||StringUtils.isBlank(methodTypeStr)){
            throw new Exception("参数校验不合格");
        }

        ProxyHttpBase activitiHttpBase = new ProxyHttpBase(apiIpStr,apiUrlStr,methodTypeStr,paramsStr);
        activitiHttpBase.execute(execution);

        if(log.isDebugEnabled()){
            log.debug("监听代理execute处理完成");
        }
    }

    @Override
    public void notify(DelegateTask delegateTask){
        DelegateExecution execution = delegateTask.getExecution();
        if(log.isDebugEnabled()){
            log.debug("methodType="+(methodType==null?"":methodType.getValue(delegateTask).toString()));
            log.debug("apiUrl="+(apiUrl==null?"":apiUrl.getValue(execution).toString()));
            log.debug("apiIp="+(apiIp==null?"":apiIp.getValue(execution).toString()));
        }
        String apiIpStr = (apiIp==null?"":apiIp.getValue(execution).toString());
        String apiUrlStr = (apiUrl==null?"":apiUrl.getValue(execution).toString());
        String methodTypeStr = (methodType==null?"":methodType.getValue(execution).toString());
        String paramsStr = (params==null?"":params.getValue(execution).toString());

        //校验参数
        if(StringUtils.isBlank(apiIpStr)||StringUtils.isBlank(apiUrlStr)||StringUtils.isBlank(methodTypeStr)){
            log.info("参数校验不合格");
            return;
        }

        ProxyHttpBase activitiHttpBase = new ProxyHttpBase(apiIpStr,apiUrlStr,methodTypeStr,paramsStr);
        try {
            activitiHttpBase.executeListener(delegateTask);
        }catch (Exception e){
            log.info("监听代理notify异常");
            e.printStackTrace();
        }

        if(log.isDebugEnabled()){
            log.debug("监听代理notify处理完成");
        }
    }
}
