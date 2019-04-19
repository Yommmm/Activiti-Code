package com.zlst.utils;

import com.netflix.hystrix.HystrixCommand;
import com.zlst.common.RestConstants;
import com.zlst.navigator2.DefaultRestNavigatorImpl;
import com.zlst.navigator2.RestNavigator;
import com.zlst.navigator2.factory.HystrixSetterAdapter;
import com.zlst.navigator2.factory.HystrixSetterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 微服务内部调用接口
 * Created by 170066 on 2017/9/14.
 */
public class NavigatorUtil {

    private static Logger log = LoggerFactory.getLogger(NavigatorUtil.class);
    /**
     * Navigator post请求
     *
     * @param url        请求地址
     * @param jsonStr json入参
     * @return
     */
    public static String postForObjcet(String url, String jsonStr,String commandKey) {
        return postForObjcet(RestConstants.BASE_REST_URL,url,jsonStr,commandKey);
    }



    /**
     * Navigator put请求
     *
     * @param url        请求地址
     * @param jsonStr json入参
     * @return
     */
    public static String putForObjcet(String url, String jsonStr,String commandKey) {
        return putForObjcet(RestConstants.BASE_REST_URL,url,jsonStr,commandKey);
    }

    /**
     * Navigator post请求
     * @param microServiceName        微服务名称
     * @param url        请求地址
     * @param jsonStr json入参
     * @param commandKey 熔断线程分组KEY
     * @return
     */
    public static String postForObjcet(String microServiceName,String url, String jsonStr,String commandKey) {
        log.debug("microServiceName: ",microServiceName," ,url: ",url," ,jsonStr: ",jsonStr,",commandKey: ",commandKey);
        HystrixCommand.Setter setter = hystrixCommandSetter(microServiceName,commandKey);
        RestNavigator<String> navigator = new DefaultRestNavigatorImpl<>(String.class, microServiceName, url,setter);
        String result = navigator.post(null, jsonStr);
        log.debug("result: ",result);
        return result;
    }
    
    public static String getForObjcet(String url,String commandKey) {
        return getForObjcet(RestConstants.BASE_REST_URL,url,commandKey);
     }

    /**
     * Navigator get请求
     * @param microServiceName        微服务名称
     * @param url        请求地址
     * @return
     */
    public static String getForObjcet(String microServiceName,String url,String commandKey) {
        log.debug("microServiceName: ",microServiceName," ,url: ",url," ,commandKey: ",commandKey);
        HystrixCommand.Setter setter = hystrixCommandSetter(microServiceName,commandKey);
        String result = new DefaultRestNavigatorImpl<>(String.class, microServiceName, url,setter).get(null);
        log.debug("result: ",result);
        return result;
    }
    
    /**
     * Navigator delete请求
     *
     * @param url        请求地址
     * @param jsonStr json入参
     * @return
     */
    public static String deleteForObjcet(String url, String jsonStr,String commandKey) {
        return deleteForObjcet(RestConstants.BASE_REST_URL,url,jsonStr,commandKey);
    }

    /**
     * Navigator delete请求
     * @param microServiceName        微服务名称
     * @param url        请求地址
     * @param jsonStr json入参
     * @return
     */
    public static String deleteForObjcet(String microServiceName, String url, String jsonStr,String commandKey) {
        log.debug("microServiceName: ",microServiceName," ,url: ",url," ,jsonStr: ",jsonStr);
        HystrixCommand.Setter setter = hystrixCommandSetter(microServiceName,commandKey);
        RestNavigator<String> navigator = new DefaultRestNavigatorImpl<>(String.class, microServiceName, url,setter);
        String result = navigator.delete(null, jsonStr);
        log.debug("result: ",result);
        return result;
    }

    /**
     * Navigator put请求
     * @param microServiceName        微服务名称
     * @param url        请求地址
     * @param jsonStr json入参
     * @return
     */
    public static String putForObjcet(String microServiceName, String url, String jsonStr,String commandKey) {
        log.debug("microServiceName: ",microServiceName," ,url: ",url," ,jsonStr: ",jsonStr," ,commandKey:",commandKey);
        HystrixCommand.Setter setter = hystrixCommandSetter(microServiceName,commandKey);
        RestNavigator<String> navigator = new DefaultRestNavigatorImpl<>(String.class, microServiceName, url,setter);
        String result = navigator.put(null, jsonStr);
        log.debug("result: ",result);
        return result;
    }

    /**
     * 适配器
     * @param microServiceName 服务名称
     * @param commandKey 熔断线程KEY
     * @return
     */
    public static HystrixCommand.Setter hystrixCommandSetter(String microServiceName, String commandKey){
        HystrixSetterAdapter adapter = new HystrixSetterAdapter(microServiceName)
                .withExecutionTimeoutInMilliseconds(10000) // 配置熔断超时时间
                .withCommand(commandKey) // 服务标识【熔断起决定作用】
                .withThread(commandKey); //线程标识【线程分组】
        HystrixCommand.Setter setter = HystrixSetterFactory.setter(adapter);
        return setter;
    }
    
}
