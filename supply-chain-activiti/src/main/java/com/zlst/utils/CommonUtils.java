package com.zlst.utils;

import com.alibaba.fastjson.JSONObject;
import com.zlst.module.order.ctrl.OrderMgrCtrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by 170079 on 2017/10/30.
 */
public final class CommonUtils {

    private final static Logger LOG = LoggerFactory.getLogger(CommonUtils.class);

    public static String getHeadUserId() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String userId = request.getHeader("gateway_userId");
        LOG.debug("userId :", userId);
        return userId;
    }

    public static String getHeadUserName() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String userName = request.getHeader("gateway_userName");
        LOG.debug("userName :", userName);
        return userName;
    }

    public static String getOrgId() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String orgId = request.getHeader("org_id");
        LOG.debug("orgId :", orgId);
        return orgId;
    }

    public static String getOrgName() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String orgName = request.getHeader("org_name");
        LOG.debug("orgName :", orgName);
        return orgName;
    }

    public static String getOrgCode() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String orgCode = request.getHeader("org_code");
        LOG.debug("orgCode :", orgCode);
        return orgCode;
    }

    public static boolean isReturnList(String responseStr) {
        LOG.debug("responseStr: ", responseStr);
        //返回为null的场景
        if (null == responseStr) {
            LOG.error("responseStr is null.");
            return false;
        }

        List<Map<String, Object>> response = null;

        //返回的串不是json串
        try {
            response = JSONObject.parseObject(responseStr, List.class);
        } catch (Exception e) {
            LOG.debug("responseStr:", responseStr);
            return false;
        }

        return true;
    }

    public static  boolean isJsonMap(String jsonStr){
        LOG.debug("responseStr: ", jsonStr);
        //返回为null的场景
        if (null == jsonStr) {
            return false;
        }
        //返回的串不是json串
        try {
            JSONObject.parseObject(jsonStr, Map.class);
        } catch (Exception e) {
            LOG.error("responseStr:", jsonStr);
            return false;
        }
        return true;
    }

    public static boolean isSuccess(String responseStr) {
        LOG.debug("responseStr: ", responseStr);
        //返回为null的场景
        if (null == responseStr) {
            LOG.error("responseStr is null.");
            return false;
        }

        Map<String, Object> response = null;

        //返回的串不是json串
        try {
            response = JSONObject.parseObject(responseStr, Map.class);
        } catch (Exception e) {
            LOG.debug("responseStr:", responseStr);
            return false;
        }

        Object resultCode = response.get("resultCode");
        boolean result = true;
        if (null != resultCode && (1 == (int) resultCode)) {
            result = false;
        }
        LOG.debug("resultCode: ", resultCode, ",result: ", result);
        return result;
    }

}
