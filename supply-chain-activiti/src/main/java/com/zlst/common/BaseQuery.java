package com.zlst.common;

import java.io.Serializable;
import java.util.Map;

public class BaseQuery implements Serializable {
	  private static final long serialVersionUID = 1L;
	  	//页码
	    private int pageNum = 1;
	    //行数
	    private int pageSize = 10;
	    //排序规则   desc 降序 other 升序
		private String sord;
		//排序字段名
		private String sidx;
		/**
         * key:sidx
         * value:sord
         * */
        private Map<String,String> sorts;
	    public BaseQuery() {
	    }

	    public void setPageNumber(Integer pageNum) {
	        if(pageNum != null && pageNum.intValue() > 0) {
	            this.pageNum = pageNum.intValue();
	        }

	    }

	    public void setCurPage(Integer pageNum) {
	        if(pageNum != null && pageNum.intValue() > 0) {
	            this.pageNum = pageNum.intValue();
	        }

	    }

	    public int getPageNum() {
	        return this.pageNum;
	    }

	    public void setPageSize(Integer pageSize) {
	        if(pageSize != null && pageSize.intValue() > 0) {
	            this.pageSize = pageSize.intValue();
	        }

	    }

	    public int getPageSize() {
	        return this.pageSize;
	    }

	    protected boolean isBlank(String value) {
	        return null == value || "".equals(value.trim());
	    }

		public String getSord() {
			return sord;
		}

		public void setSord(String sord) {
			this.sord = sord;
		}

		public String getSidx() {
			return sidx;
		}

		public void setSidx(String sidx) {
			this.sidx = sidx;
		}

		public Map<String, String> getSorts() {
			return sorts;
		}

		public void setSorts(Map<String, String> sorts) {
			this.sorts = sorts;
		}
}
