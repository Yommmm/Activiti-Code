package com.zlst.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import com.alibaba.fastjson.JSONObject;
import com.zlst.business.activiti.listener.ProcessCompleteHandler;
import com.zlst.module.order.bean.OmsOrderException;
import com.zlst.module.order.service.OmsOrderExceptionService;
import com.zlst.proxy.vo.CreateOrderExceptionRequest;

/**
 * Created by 170079 on 2017/10/19.
 */
public final class ResultUtil {

    private final static Logger LOG = LoggerFactory.getLogger(ProcessCompleteHandler.class);

    private final static int RESULT_CODE_NORMAL = 0;
    
    private static OmsOrderExceptionService omsOrderExceptionService = null;
    
    static {
    	if(null != omsOrderExceptionService) {
    		omsOrderExceptionService = new OmsOrderExceptionService();
    	}
    }

    public static boolean occurException(String responseStr) {
        LOG.debug("responseStr: ", responseStr);
        //返回为null的场景
        if (null == responseStr) {
            LOG.error("responseStr is null.");
            return true;
        }

        Map<String, Object> response = null;

        try {
            response = JSONObject.parseObject(responseStr, Map.class);
        } catch (Exception e) {
            //返回的串不是json串
            LOG.debug("responseStr:", responseStr);
            return true;
        }
        Object resultCode = response.get("resultCode");
        boolean result = false;
        if (null != resultCode) {
            result = (1 == (int) resultCode);
        }
        LOG.debug("resultCode: ", resultCode, ",result: ", result);
        return result;
    }

    /**
     * 从异常中获取string类型的堆栈
     *
     * @param e
     * @return
     */
    public static String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String exception = sw.toString();
        if (exception.length() > 4000) {
            exception = exception.substring(0, 3800);//TODO
        }
        return exception;
    }

    public static String getResponseStackTrace(String responseStr) {
        if (null == responseStr) {
            LOG.error("responseStr is null.");
            return "response is null.";
        }

        Map<String, Object> response = null;
        try {
            response = JSONObject.parseObject(responseStr, Map.class);
        } catch (Exception e) {
            return responseStr;
        }
        Map<String, Object> errorInfoMap = (Map<String, Object>) response.get("errorInfo");
        String stackTrace = (String) errorInfoMap.get("stackTrace");
        if (stackTrace.length() > 3800) {
            stackTrace = stackTrace.substring(0, 3800);
        }
        return stackTrace;
    }

    public static void recordOrderException(String procDefId, String activityName, String orderId, String exceptionType, String staceTrace) {
        LOG.debug("occur exception...exceptionType: ", exceptionType);
        try {
            CreateOrderExceptionRequest createOrderExceptionRequest = new CreateOrderExceptionRequest();
            createOrderExceptionRequest.setProcDefId(procDefId);
            createOrderExceptionRequest.setTaskDefineName(activityName);
            createOrderExceptionRequest.setOrderId(orderId);
            createOrderExceptionRequest.setCreateTime(new Date());
            createOrderExceptionRequest.setExceptionType(exceptionType);
            createOrderExceptionRequest.setStackTrace(staceTrace);
            /*String url = "/omsOrderException";
            NavigatorUtil.postForObjcet(url, JSON.toJSONString(createOrderExceptionRequest),url+RequestMethod.POST.getName());*/
            
            /**
             * 2019.02.11 新年第一天上班，把上面这个调用改成本地方法调用
             */
            OmsOrderException omsOrderException = new OmsOrderException();
            BeanUtils.copyProperties(createOrderExceptionRequest, omsOrderException);
            omsOrderExceptionService.save(omsOrderException);
            
        } catch (Exception e) {
            LOG.error("recordOrderException occur exception,exceptionType: ", exceptionType);
        }
    }

    /**
     * 内部类，用于正常场景时接口返回
     */
    private static class Result{
        private int resultCode;

        private Object data;

        public Result(int resultCode) {
            this.resultCode = resultCode;
        }

        public Result(int resultCode,Object data) {
            this.resultCode = resultCode;
            this.data = data;
        }

        public int getResultCode() {
            return resultCode;
        }

        public void setResultCode(int resultCode) {
            this.resultCode = resultCode;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }

    public static Object buildNormalResult(){
        return new Result(RESULT_CODE_NORMAL);
    }

    public static Object buildNormalResult(Object data){
        return new Result(RESULT_CODE_NORMAL,data);
    }
}
