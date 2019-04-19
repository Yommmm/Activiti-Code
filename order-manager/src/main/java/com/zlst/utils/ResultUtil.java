package com.zlst.utils;

/**
 * Created by 170079 on 2017/10/12.
 */
public final class ResultUtil {

    private final static int RESULT_CODE_NORMAL = 0;

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
