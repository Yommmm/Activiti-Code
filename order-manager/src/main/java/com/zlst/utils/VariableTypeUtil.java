package com.zlst.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 170066 on 2017/9/12.
 */
public class VariableTypeUtil {

    private static List<String> chkVarType = new ArrayList<>();

    static {
        chkVarType.add("string");
        chkVarType.add("boolean");
        chkVarType.add("double");
        chkVarType.add("int");
        chkVarType.add("long");
        chkVarType.add("date");
    }

    /**
     * 检测流程变量格式是否支持
     *
     * @param type
     * @return
     */
    public static boolean chkType(String type) {
        if (!chkVarType.contains(type)) {
            return false;
        }
        return true;
    }

}
