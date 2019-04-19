package com.zlst.module.eureka.ctrl;

import com.zlst.config.NavigatorConfig;
import org.apache.commons.codec.binary.Base64;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;

@RestController
@RequestMapping("/eurekaDemo")
@EnableAutoConfiguration
public class EurekaClientDemoCtrl {


    @RequestMapping("/{id}")
    public String demo(@PathVariable("id") String id) throws URISyntaxException {
        NavigatorConfig config = new NavigatorConfig()
                .setServiceId("microservice-zlst-manage-system")
                .setAction("/system/users/1")
                .put("userCode", "U0001")
                .put("username", "kingschan")
                .put("password", "123456")
                .put("sex", "F")
                .put("deptId", "1")
                .put("postId", "1");
//        MicroServiceNavigator nav = new MicroServiceNavigator(config);
//        String result= nav.execute(String.class, RequestType.POST);
        return null;

    }

    @RequestMapping("/test")
    public String test() throws URISyntaxException {
        //目前eureka不支持朝header中传值，所以需要使用原生的HTTP
        String plainCredentials="admin:000000";
        String base64Credentials = new String(Base64.encodeBase64(plainCredentials.getBytes()));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Credentials);

        NavigatorConfig config = new NavigatorConfig()
                .setServiceId("microservice-activiti-business") //要调用的微服务id
                .setAction("/repository/deployments") //要调用的url
                //.put("userCode", "U0001") //设置参数
                //.put("username", "admin")
                //.put("password", "000000");
                .put("Authorization", "Basic " + base64Credentials);
//        MicroServiceNavigator nav = new MicroServiceNavigator(config);
//        String result = nav.execute(String.class, RequestType.GET);
//        System.out.println(result);
        return null;
    }

//
//
//    public static void main(String[] args) {
//        SpringApplication application = new SpringApplication(NavdemoApplication.class);
//        application.setBannerMode(Banner.Mode.OFF);
//        application.run(args);
//
//    }
}
