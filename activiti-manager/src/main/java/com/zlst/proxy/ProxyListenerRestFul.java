package com.zlst.proxy;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.delegate.TaskListener;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ResourceBundle;

/**
 * Created by 170058 on 2017/8/28.
 */
public abstract class ProxyListenerRestFul implements JavaDelegate,TaskListener {
	
	private static Logger log =  LoggerFactory.getLogger(ProxyListenerRestFul.class);
	private static String restfulIpConfigPath = "restful-ipconfig";
    private Expression apiIp;          //对应配置文件的IP地址和端口         eg>127.0.0.1
    private Expression methodType;     //POST/GET/PUT/DELETE eg>POST
    private Expression apiUrl;         //API地址                                                  eg>runtime
    private Expression content;        //直接转发参数                                          eg>{id:'123'}

    public void setApiIp(Expression apiIp) {
        this.apiIp = apiIp;
    }

    public void setApiUrl(Expression apiUrl) {
        this.apiUrl = apiUrl;
    }

    public void setMethodType(Expression methodType) {
        this.methodType = methodType;
    }

    public void setContent(Expression content) {
        this.content = content;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
    	
    	if(log.isDebugEnabled()){
    		log.debug("methodType="+(methodType==null?"":methodType.getValue(execution)));
    		log.debug("apiUrl="+(apiUrl==null?"":apiUrl.getValue(execution)));
    		log.debug("apiIp="+(apiIp==null?"":apiIp.getValue(execution)));
    		log.debug("content="+(content==null?"":content.getValue(execution)));
    	}
    	
        //地址配置文件
        ResourceBundle resource = ResourceBundle.getBundle(restfulIpConfigPath);
        //获取完整的URL
        StringBuffer urlBuffer = new StringBuffer();
        //获取IP及端口
        if(apiIp==null){
        	urlBuffer.append(resource.getString("localhost")); //默认取本地的ip和端口
        }else{
        	urlBuffer.append(resource.getString(apiIp.getValue(execution).toString()));
        }
        //获取API地址
        urlBuffer.append(apiUrl.getValue(execution).toString());  //apiRrl中可能已经存在 参数等信息    eg>. /a/{id}
        System.out.println(urlBuffer.toString());
        
        //根据method进行操作    
        CloseableHttpClient httpClient = HttpClients.createDefault();   //后续可以对超时等进行修改
        String methodTypeStr = methodType==null?"":methodType.getValue(execution).toString();
        CloseableHttpResponse response = null;
        if(RequestMethod.GET.name().equals(methodTypeStr)){	
        	HttpGet httpGet = new HttpGet(urlBuffer.toString());
        	response = httpClient.execute(httpGet);
        }else if(RequestMethod.POST.name().equals(methodTypeStr)){
         	HttpPost httpPost = new HttpPost(urlBuffer.toString());
         	httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");
         	StringEntity entity = new StringEntity(content.getValue(execution).toString(),"UTF-8");
         	entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_ENCODING,"UTF-8"));
         	httpPost.setEntity(entity);
         	response = httpClient.execute(httpPost); 	
        }else if(RequestMethod.PUT.name().equals(methodTypeStr)){
        	HttpPut httpPut = new HttpPut(urlBuffer.toString());
        	httpPut.addHeader(HTTP.CONTENT_TYPE, "application/json");
         	StringEntity entity = new StringEntity(content.getValue(execution).toString(),"UTF-8");
         	entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_ENCODING,"UTF-8"));
         	httpPut.setEntity(entity);
         	response = httpClient.execute(httpPut);
        }else if(RequestMethod.DELETE.name().equals(methodTypeStr)){
        	HttpDelete httpDelete = new HttpDelete(urlBuffer.toString());
        	response = httpClient.execute(httpDelete);
        }else{		
        }
        
        if(response!=null){
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
        }
    }
    
    public abstract void processResult(DelegateExecution execution,String result);
    
}
