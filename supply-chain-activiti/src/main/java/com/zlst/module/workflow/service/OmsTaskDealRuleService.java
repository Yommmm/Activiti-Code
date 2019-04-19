package com.zlst.module.workflow.service;

import com.zlst.database.core.service.QueryAndOperateServ;
import com.zlst.module.workflow.bean.OmsTaskDealRule;
import com.zlst.module.workflow.dao.OmsTaskDealRuleRepository;
import com.zlst.module.workflow.vo.OmsTaskDealRuleVO;
import com.zlst.param.Page;
import com.zlst.param.PageParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OmsTaskDealRuleService extends QueryAndOperateServ<OmsTaskDealRule, OmsTaskDealRuleRepository> {
	
    private static final Logger logger = LoggerFactory.getLogger(OmsTaskDealRuleService.class);
    
    @Autowired
    private OmsTaskDealRuleRepository omsTaskDealRuleRepository;

    /**
   	 * 保存OmsTaskDealRule
   	 * @param omsTaskDealRule
   	 */
    public OmsTaskDealRule saveOmsTaskDealRule(OmsTaskDealRule omsTaskDealRule) throws Exception {
    	omsTaskDealRule = this.save(omsTaskDealRule);
    	return omsTaskDealRule;
    }

    /**
	 * 删
	 * @param omsTaskDealRuleId XXXX
	 */
    public String delOmsTaskDealRule(String omsTaskDealRuleId) throws Exception {
    	OmsTaskDealRule omsTaskDealRule = this.get(omsTaskDealRuleId);
//    	omsTaskDealRule.setStatus(GlobalConstants.UNDELETED);
    	omsTaskDealRule = this.update(omsTaskDealRuleId, omsTaskDealRule);
    	return omsTaskDealRuleId;
    }

    /**
	 * 修改OmsTaskDealRule
	 * @param omsTaskDealRule XXXX
	 */
    public OmsTaskDealRule updateOmsTaskDealRule(String omsTaskDealRuleId, OmsTaskDealRule omsTaskDealRule) throws Exception {
    	omsTaskDealRule = this.update(omsTaskDealRuleId, omsTaskDealRule);
    	return omsTaskDealRule;
    }

    /**
	 * 通过omsTaskDealRuleID查询OmsTaskDealRule
	 * @param omsTaskDealRuleId XXXX
	 */
    public OmsTaskDealRuleVO getOmsTaskDealRule(String omsTaskDealRuleId) throws Exception {
    	OmsTaskDealRule omsTaskDealRule = this.get(omsTaskDealRuleId);
    	OmsTaskDealRuleVO omsTaskDealRuleVO = new OmsTaskDealRuleVO();

    	BeanUtils.copyProperties(omsTaskDealRule, omsTaskDealRuleVO);
    	this.translate(omsTaskDealRuleVO);

    	return omsTaskDealRuleVO;
    }

    /**
	 * 查
	 * @param conditions XXXX
	 */
    public Page<OmsTaskDealRuleVO> getOmsTaskDealRule(OmsTaskDealRuleVO conditions) throws Exception {
    	StringBuilder sql = new StringBuilder("SELECT t.* FROM oms_task_deal_rule t ");
    	sql.append("");

    	PageParam pageParam = new PageParam();
    	pageParam.setPageNum(conditions.getPageNum());
		pageParam.setPageSize(conditions.getPageSize());

    	Map<String, Object> params = new HashMap<String, Object>();

    	Page<OmsTaskDealRule> omsTaskDealRulePage = this.sqlPageQuery(sql.toString(), pageParam, OmsTaskDealRule.class, params);
    	List<OmsTaskDealRule> omsTaskDealRuleList = omsTaskDealRulePage.getObjList();

    	Page<OmsTaskDealRuleVO> omsTaskDealRuleVOPage = new Page<OmsTaskDealRuleVO>();
    	List<OmsTaskDealRuleVO> omsTaskDealRuleVOList = new ArrayList<OmsTaskDealRuleVO>();
		BeanUtils.copyProperties(omsTaskDealRulePage, omsTaskDealRuleVOPage, "objList");

    	for(OmsTaskDealRule omsTaskDealRule : omsTaskDealRuleList) {
    		OmsTaskDealRuleVO omsTaskDealRuleVO = new OmsTaskDealRuleVO();
    		BeanUtils.copyProperties(omsTaskDealRule, omsTaskDealRuleVO);
    		this.translate(omsTaskDealRuleVO);

    		omsTaskDealRuleVOList.add(omsTaskDealRuleVO);
    	}
    	omsTaskDealRuleVOPage.setObjList(omsTaskDealRuleVOList);

    	return omsTaskDealRuleVOPage;
    }

    private void translate(OmsTaskDealRuleVO omsTaskDealRuleVO) {
    	// TODO
    }

    
}


