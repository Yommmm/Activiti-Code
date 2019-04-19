package com.zlst.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by 170079 on 2017/10/17.
 */
public final class OrderUtils {
    private OrderUtils() {
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

}
