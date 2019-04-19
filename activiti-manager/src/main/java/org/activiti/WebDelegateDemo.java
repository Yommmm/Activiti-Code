package org.activiti;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.util.ResourceBundle;

public class WebDelegateDemo implements JavaDelegate {

    private Expression apiIp;           //对应配置文件的IP地址和端口
    private Expression methodType;    //POST/GET/DELETE...
    private Expression apiUrl;         //API地址
    private Expression content;        //直接转发参数

    private final String defaultApiAddressStart = "http://";
    private final String defaultApiIpAddress = "127.0.0.1"; //默认IP
    private final String defaultPort = "8080"; //默认端口

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

    private int index = 1;

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        ResourceBundle resource = ResourceBundle.getBundle("config");
        String apiIpVlue = "";
        if(apiIp==null){
            apiIpVlue = resource.getString("localhost");
        }else{
            apiIpVlue = resource.getString(apiIp.getValue(execution).toString());
        }

        //IP端口等信息根据参数到配置文件中查找
        System.out.println("-----------------begin to process-------------------");
        System.out.println("methodType="+methodType.getValue(execution));
        System.out.println("apiUrl="+apiUrl.getValue(execution));
        System.out.println("content="+content.getValue(execution));
        System.out.println("apiIp="+apiIp.getValue(execution));

        //跨系统调用接口  HTTP GET/POST
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String uri = "";
        uri = apiIpVlue;
        uri += apiUrl.getValue(execution).toString();

        //uri = "http://127.0.0.1:8088/api/v1/student";
        System.out.println("URL="+uri);

        HttpPost httpPost = new HttpPost(uri);
        String myname = "myname"+ String.valueOf(index++);
        //StringEntity entity = new StringEntity("[{\"id\":\"myid\",\"name\":\"myname\"}]","utf-8");
        StringEntity entity = new StringEntity("[{\"id\":\"myid\",\"name\":\""+myname+"\"}]","utf-8");
        httpPost.setEntity(entity);
        CloseableHttpResponse response = httpClient.execute(httpPost);

        for(int i=0;i<10;i++){
            Thread.sleep(1000);
            System.out.println("已执行"+i+"秒");
        }

        long t = System.currentTimeMillis();
        if(t%2==0){
            execution.setVariable("flag",true);
            execution.setVariable("excutetime",String.valueOf(t));
        }else{
            execution.setVariable("flag",false);
            execution.setVariable("excutetime",String.valueOf(t));
        }

        System.out.println(execution.getVariable("flag").toString());
        System.out.println(execution.getVariable("excutetime").toString());

        System.out.println("response status is " + response.getStatusLine().getStatusCode());
        System.out.println("-----------------end to process-------------------");
    }

}
