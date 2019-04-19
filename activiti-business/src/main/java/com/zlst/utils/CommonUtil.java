package com.zlst.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * Created by lingpeng 170119 on 2017/9/19.
 */
public class CommonUtil {
    private static final Logger logger = LoggerFactory.getLogger(CommonUtil.class);

    /**
     * 获取系统UUID
     */
    public String getUuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

//    public static void main (String[] args) throws Exception {
//        CommonUtil cu = new CommonUtil();
//        String url = "/A/B/C?a={1}&b={2}&c={3}";
//        //String url = "/A/{123}/B/{234}/C";
//        cu.getUrl(url);
//    }

    /**
     * 针对GET 方法解析URL 包含以路径{}请求的方式和以参数{}请求的方式
     * 【注意新的导航器请求不允许/A/{XX}/B 和/A/{XX}/C 这种写法，
     * 因为熔断线程KEY为A，该路径为两个接口，如果其中一个熔断必将影响另外一个接口】
     */
    public static Map<String,Object> getUrl(String url) throws Exception{
        if((url.indexOf("{")<0) || (url.indexOf("}")<0)){
            throw new RuntimeException("微服务调用请求URL写法不规范！请按规范书写！");
        }
        Map<String,Object> map = new HashMap<>();
        int index = url.indexOf("?");
        Map<String,String> maps = null;
        if(index>0){
            String params = url.substring(index+1);
            String path = url.substring(0,index);
            String urls = params.replace("{","").replace("}","").trim();//去除以路径请求URL中的{}
            maps = URLRequest(urls);
            map.put("flag",true);
            map.put("commandKey",path);//线程KEY
            map.put("params",maps);
        }else{
            int firstIndex = url.indexOf("/{");//拿到第一个占位符的索引位置
            String path = url.substring(0,firstIndex);
            String urls = url.replace("{","").replace("}","").trim(); //去除以路径请求URL中的{}
            map.put("flag",false);
            map.put("commandKey",path);//线程KEY
            map.put("url",urls);//新的URL
        }
        return map;
    }

    /**
     * 解析出url参数中的键值对
     * 如 "index.jsp?Action=del&id=123"，解析出Action:del,id:123存入map中
     * @param URL url地址
     * @return url请求参数部分
     */
    public static Map<String, String> URLRequest(String URL) {
        Map<String, String> mapRequest = new HashMap<String, String>();
        String[] arrSplit = null;
        String strUrlParam = URL;
        if (strUrlParam == null) {
            return mapRequest;
        }
        //每个键值为一组
        arrSplit = strUrlParam.split("&");
        for (String strSplit : arrSplit) {
            String[] arrSplitEqual = null;
            arrSplitEqual = strSplit.split("=");
            //解析出键值
            if (arrSplitEqual.length > 1) {
                //正确解析
                mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);
            } else {
                if (arrSplitEqual[0] != "") {
                    //只有参数没有值，不加入
                    mapRequest.put(arrSplitEqual[0], "");
                }
            }
        }
        return mapRequest;
    }
}
