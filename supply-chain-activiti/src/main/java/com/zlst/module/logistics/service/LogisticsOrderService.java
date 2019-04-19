package com.zlst.module.logistics.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zlst.database.common.Common;
import com.zlst.database.common.ZlstBeanUtils;
import com.zlst.database.core.dao.Myspec;
import com.zlst.database.core.service.QueryAndOperateServ;
import com.zlst.exception.ZLSTException;
import com.zlst.module.logistics.bean.OmsTaskAllReqVO;
import com.zlst.module.logistics.bean.OmsTaskRequestVO;
import com.zlst.module.logistics.bean.OmsTasksResponseVO;
import com.zlst.module.logistics.bean.TaskBitchVO;
import com.zlst.module.order.bean.OmsTaskInstance;
import com.zlst.module.order.bean.vo.OmsTaskInstanceOpRequest;
import com.zlst.module.order.bean.vo.RestVariableItemVo;
import com.zlst.module.order.dao.OmsTaskInstanceRepository;
import com.zlst.module.order.service.OrderMgrService;
import com.zlst.param.Filters;
import com.zlst.param.Page;
import com.zlst.param.PageParam;
import com.zlst.param.Result;
import com.zlst.param.Rule;
import com.zlst.common.ExceptionConstants;
import com.zlst.utils.NavigatorUtil;
import com.zlst.common.RestConstants;

/**
 * Created by 刘涛 on 2018/3/20.
 */
@Service
public class LogisticsOrderService extends QueryAndOperateServ<OmsTaskInstance, OmsTaskInstanceRepository> {
    
	private static final Logger log = LoggerFactory.getLogger(LogisticsOrderService.class);
    
	@Autowired
    private HttpServletRequest request;
    
	@Autowired
    private OrderMgrService orderMgrService;
	
	@Autowired
	private OmsTaskInstanceRepository omsTaskInstanceRepository;

    /**
     * 高级查询
     * @param query
     * @return
     */
    public Page queryOmsTaskForPage(OmsTaskRequestVO query)throws Exception{
        log.debug("logistics queryOmsTaskForPage is start paramer is {} ", JSONObject.toJSON(query));
        Page<OmsTaskInstance> pages = getPage(query);
        Page<OmsTasksResponseVO> pagesV = new Page<>();
        //复制发货通知
        BeanUtils.copyProperties(pages, pagesV);
        List<OmsTaskInstance> list = pages.getObjList();
        List<OmsTasksResponseVO> gavList = new ArrayList<>();
        for (int i=0;i<list.size();i++){
            OmsTasksResponseVO gvo = new OmsTasksResponseVO();
            OmsTaskInstance omsTaskInstance= list.get(i);
            BeanUtils.copyProperties(omsTaskInstance,gvo);
            gavList.add(gvo);
        }
        pagesV.setObjList(gavList);
        log.debug("logistics queryOmsTaskForPage is end result is {} ", JSONObject.toJSON(pagesV));
        return pagesV;
    }
    /**
     *   查询Sql
     * @author liutao
     * @date 2018/6/7
     * @return java.lang.String
     */
    private String getSql(){
        StringBuilder sb =new StringBuilder();
        sb.append(" SELECT t.id,t.task_define_id,t.order_id,t.order_source_id,t.proc_inst_id,t.task_inst_id,");
        sb.append(" t.proc_def_id,t.proc_def_Name,t.task_name,t.task_def_id,t.role_id,t.role_name,t.user_id,");
        sb.append(" t.user_name,t.task_comment,t.status,t.status_time,t.url,t.create_user_id,t.create_org_id,t.create_time ");
        sb.append(" FROM oms_task_instance t JOIN  ");
        sb.append(" ( SELECT d.proc_def_id proc_def_id,d.task_def_id task_def_id FROM ");
        sb.append(" oms_task_deal_rule o JOIN oms_task_define d ON o.id = d.id AND ( o.TASK_DEAL_ORG_ID IN :roleList OR o.TASK_DEAL_USER_ID =:userId ) ");
        sb.append(" WHERE o. STATUS = '0' GROUP BY proc_def_id,task_def_id) d ");
        sb.append(" WHERE t.proc_def_id = d.proc_def_id AND t.task_def_id = d.task_def_id ");
        sb.append(" {{ AND t.`status` =:status }} ");
        sb.append(" {{ AND t.create_org_id =:createOrgId }} ");
        sb.append(" {{ AND t.order_id =:orderId }}  ");
        sb.append(" {{ AND t.order_id in :orderIdStr }} ");
        sb.append(" {{ AND t.proc_def_id in :procDefIdStr }} ");
        sb.append(" {{ AND t.order_source_id =:orderSourceId }} ");
        sb.append(" {{ AND t.task_name LIKE :taskName }} ");
        sb.append(" {{ AND t.create_time >= :createTimeBegin }} ");
        sb.append(" {{ AND t.create_time <= :createTimeEnd }} ");
        sb.append(" ORDER BY t.create_time DESC ");
        return sb.toString();
    }
    /**
     *   查询Page
     * @author liutao
     * @date 2018/6/7
     * @param query
     * @return com.zlst.param.Page
     */
    private Page getPage(OmsTaskRequestVO query)throws Exception{
        PageParam pageParam = new PageParam();
        pageParam.setPageNum(query.getPageNum());
        pageParam.setPageSize(query.getPageSize());
        String userId = request.getHeader("gateway_userId");
        Map<String ,Object> map = new HashMap<>();
        List<String> roleList = getUserRoleInfo(userId);
        map.put("roleList",roleList);
        map.put("userId",userId);
        if(query.getStatus()!=null&&query.getStatus()!=""){
            map.put("status",query.getStatus());
        }
        String orgType = request.getHeader("corp_type");
        if (!"Corp".equals(orgType)) {
            String companyId = request.getHeader("corp_id");//公司ID
            map.put("createOrgId",companyId);
        }
        if(null != query.getOrderId()&&query.getOrderId()!=""){
            map.put("orderId",query.getOrderId());
        }
        if(null != query.getOrderIdStr()&&query.getOrderIdStr()!=""){
            List<String> result = Arrays.asList(query.getOrderIdStr().split(","));
            map.put("orderIdStr",result);
        }
        if(null != query.getOrderSourceId()&&query.getOrderSourceId()!=""){
            map.put("orderSourceId",query.getOrderSourceId());
        }
        if(null != query.getTaskName()&&query.getTaskName()!=""){
            map.put("taskName","%"+query.getTaskName()+"%");
        }
        if(null != query.getProcDefIds()&&query.getProcDefIds().size()!=0){
            map.put("procDefIdStr",query.getProcDefIds());
        }
        if(query.getCreateTimeBegin()!=null){
            map.put("createTimeBegin",query.getCreateTimeBegin());
        }
        if(null != query.getCreateTimeEnd()){
            map.put("createTimeEnd",query.getCreateTimeEnd());
        }
        String sql = this.getSql();
        Page page = super.sqlPageQuery(sql, pageParam, Map.class, map);
        List taskList = page.getObjList();
        List<OmsTaskInstance> list = ZlstBeanUtils.mapsToObjectsFromOracle(taskList,OmsTaskInstance.class);
        page.setObjList(list);
        return page;
    }

    /**
     * 任务工单高级查询条件过滤
     * @param query
     * @return
     */
    public PageParam getPageParamNew(OmsTaskRequestVO query) {
        log.debug("logistics getPageParamNew is start paramer is {} ", JSONObject.toJSON(query));
        int page = query.getPageNum();
        int rows = query.getPageSize();
        PageParam pageParam = new PageParam();
        pageParam.setPageNum(page);
        pageParam.setPageSize(rows);
        pageParam.setSidx("createTime");
        pageParam.setSord("desc");

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Filters filters = new Filters();
        filters.setGroupOp(Filters.OP_AND);
        List<Rule> rules = new ArrayList<>();

        String companyId = request.getHeader("corp_id");//公司ID
        Rule ruleCompany = new Rule();
        ///获取头部信息 所属公司
        String orgType = request.getHeader("corp_type");
        if (!"Corp".equals(orgType)) {
            ruleCompany.setField("createOrgId");
            ruleCompany.setOp(Filters.OP_AND);
            ruleCompany.setData(companyId);
            rules.add(ruleCompany);
        }

        //状态
        if(query.getStatus()!=null&&query.getStatus()!=""){
            Rule rule= new Rule();
            rule.setField("status");
            rule.setOp(Rule.OP_EQUAL);
            rule.setData(query.getStatus());
            rules.add(rule);
        }
        //大于开始时间
        if(query.getCreateTimeBegin()!=null){
            String dateStr = format.format(query.getCreateTimeBegin());
            Rule rule= new Rule();
            rule.setField("createTime");
            rule.setOp(Rule.OP_GREATER_EQUAL);
            rule.setData(dateStr);
            rules.add(rule);
        }
        //小于结束时间
        if(null != query.getCreateTimeEnd()){
            String dateStr = format.format(query.getCreateTimeEnd());
            Rule rule= new Rule();
            rule.setField("createTime");
            rule.setOp(Rule.OP_LESS_EQUAL);
            rule.setData(dateStr);
            rules.add(rule);
        }
        //orderId
        if(null != query.getOrderId()&&query.getOrderId()!=""){
            Rule rule= new Rule();
            rule.setField("orderId");
            rule.setOp(Rule.OP_EQUAL);
            rule.setData(query.getOrderId());
            rules.add(rule);
        }
        //orderId
        if(null != query.getOrderIdStr()&&query.getOrderIdStr()!=""){
            Rule ruleStr= new Rule();
            ruleStr.setField("orderId");
            ruleStr.setOp(Rule.OP_IN);
            ruleStr.setData(query.getOrderIdStr());
            rules.add(ruleStr);
        }
        //流程名
        if(null != query.getProcDefIds()&&query.getProcDefIds().size()!=0){
            List<String> procDefIds = query.getProcDefIds();
            StringBuffer stringBuffer = new StringBuffer();
            //        拼接orderId
            for(int i = 0 ; i<procDefIds.size();i++){
                if(i==procDefIds.size()-1){
                    stringBuffer.append(procDefIds.get(i));
                }else {
                    stringBuffer.append(procDefIds.get(i)+",");
                }
            }
            Rule rule= new Rule();
            rule.setField("procDefId");
            rule.setOp(Rule.OP_IN);
            rule.setData(stringBuffer.toString());
            rules.add(rule);
        }
        //orderSourceId
        if(null != query.getOrderSourceId()&&query.getOrderSourceId()!=""){
            Rule rule= new Rule();
            rule.setField("orderSourceId");
            rule.setOp(Rule.OP_EQUAL);
            rule.setData(query.getOrderSourceId());
            rules.add(rule);
        }
        //任务名称
        if(null != query.getTaskName()&&query.getTaskName()!=""){
            Rule rule= new Rule();
            rule.setField("taskName");
            rule.setOp(Rule.OP_LIKE);
            rule.setData(query.getTaskName());
            rules.add(rule);
        }
        filters.setRules(rules);

//        List<Filters> filtersList = buildFilters();//权限数据过滤
//        filtersList.add(filters);//查询条件过滤

//        Filters filtersAll = new Filters();
//        filtersAll.setGroupOp(Filters.OP_AND);
//        filtersAll.setGroups(filtersList);
        pageParam.setFilters(filters);
        log.debug("logistics getPageParamNew is end result is {} ", JSONObject.toJSON(pageParam));
        return pageParam;
    }

   /**
    *
    * @author liutao
    * @date 2018/3/21
    * @return com.zlst.param.Filters
    */
    private List<Filters> buildFilters() {
        List<Filters> filtersList = new ArrayList<>();

        String userId = request.getHeader("gateway_userId");
        String companyId = request.getHeader("corp_id");//公司ID
        log.debug("getOmsTasks params userId=" + (userId == null ? "" : userId));
        log.debug("getOmsTasks params companyId=" + (companyId == null ? "" : companyId));
        if (StringUtils.isEmpty(userId)  || StringUtils.isEmpty(companyId)) {
            throw new ZLSTException(ExceptionConstants.REQ_IS_NULL);
        }
        Filters filters = new Filters();
        filters.setGroupOp(Filters.OP_AND);
        List<Rule> rules = new ArrayList<>();

        //公司数据隔离
        Rule ruleCompanyId = new Rule();
        String orgType = request.getHeader("corp_type");
        if(!orgType.equals("Corp")){
            ruleCompanyId.setField("createOrgId");
            ruleCompanyId.setOp(Filters.OP_AND);
            ruleCompanyId.setData(companyId);
            rules.add(ruleCompanyId);
        }
        //用户数据隔离
        Rule ruleUser = new Rule();
        ruleUser.setField("userId");
        ruleUser.setOp(Filters.OP_AND);
        ruleUser.setData(userId);

//        String roles = getUserRoleInfo(userId);//获取用户角色id，并拼接
//        log.debug("there are some roleIds are {}, the roles'user is {}",roles,userId);
//        if (StringUtils.isBlank(roles)) {
//            rules.add(ruleUser);
//        } else {
//            Filters filtersRoles = new Filters();
//            filtersRoles.setGroupOp(Filters.OP_OR);
//            List<Rule> rulesRoles = new ArrayList<>();
//            //用户数据隔离
//            Rule ruleRoles = new Rule();
//            ruleRoles.setField("roleId");
//            ruleRoles.setOp(Rule.OP_IN);
//            ruleRoles.setData(roles);
//            //按用户隔离筛选，或者是按角色筛选
//            rulesRoles.add(ruleRoles);
//            rulesRoles.add(ruleUser);
//            filtersRoles.setRules(rulesRoles);
//            filtersList.add(filtersRoles);
//        }
        filters.setRules(rules);
//        filtersList.add(filters);
        return filtersList;
    }

    /**
     *
     * @param userId
     * @return
     */
    public List<String> getUserRoleInfo(String userId) {
        //通过服务获取角色Id及角色名称(角色必定能够获取，没有角色的用户有问题)
        String url = "/system/users/";
        String result = NavigatorUtil.getForObjcet(RestConstants.SYSTEM_REST_URL,  url+ userId + "/roles",url+ com.zlst.utils.RequestMethod.GET);
        log.debug("getOmsTasks rolesAPIResult=" + (result == null ? "" : result));
        Result resultObject = JSONObject.parseObject(result, Result.class);
        //获取Result中Role信息 Role可能不存在
        if (resultObject.getResultCode() != 0) {
            throw new ZLSTException(ExceptionConstants.CALL_SYSTEMMANAGE_FAILURE);
        }
//        List<SysRoleVo> sysRoleList = new ArrayList<>();
        List<Object> list = JSONArray.parseArray(JSON.toJSONString(resultObject.getData()), Object.class);
//        SysRoleVo sysRoleVoTemp = null;
        List<String> roleLsit = new ArrayList<>();
        //查询到系统管理的角色后遍历返回数据
        for (Object object : list) {
//            sysRoleVoTemp = new SysRoleVo();
//            sysRoleVoTemp.setRoleId(((JSONObject) object).get("roleId").toString());
//            sysRoleVoTemp.setRoleName(((JSONObject) object).get("roleName").toString());
//            sysRoleList.add(sysRoleVoTemp);
            roleLsit.add(((JSONObject) object).get("roleId").toString());
        }

        //1.如果用户ID非空和组织Id非空，使用用户ID+组织ID进行过滤
        //2.如果角色ID非空和组织Id非空，使用in (角色ID) +组织ID进行过滤
//        StringBuffer conditionBuffer = new StringBuffer();
//        for (int i = 0; i < sysRoleList.size(); i++) {
//            conditionBuffer.append(sysRoleList.get(i).getRoleId());
//            if (i != sysRoleList.size() - 1) {
//                conditionBuffer.append(",");
//            }
//        }

        return roleLsit;
    }

    /**
     * 大物流改造-WMS-全量查询
     * @param query
     * @return
     * @throws Exception
     */
    public List<OmsTasksResponseVO> searchAll(OmsTaskAllReqVO query)throws Exception{
        log.debug("logistics searchAll is start paramer is {} ", JSONObject.toJSON(query));
//        Filters filters = new Filters();
//        filters.setGroupOp(Filters.OP_AND);
//        List<Rule> rules = new ArrayList<>();
//
//        String companyId = request.getHeader("corp_id");//公司ID
//        Rule ruleCompany = new Rule();
//        ///获取头部信息 所属公司
//        String orgType = request.getHeader("corp_type");
//        if (!"Corp".equals(orgType)) {
//            ruleCompany.setField("companyId");
//            ruleCompany.setOp(Filters.OP_AND);
//            ruleCompany.setData(companyId);
//            rules.add(ruleCompany);
//        }
//
//        //状态
//        if(query.getStatus()!=null&&query.getStatus()!=""){
//            Rule rule= new Rule();
//            rule.setField("status");
//            rule.setOp(Rule.OP_EQUAL);
//            rule.setData(query.getStatus());
//            rules.add(rule);
//        }
//        //流程名
//        if(null != query.getProcDefIds()&&query.getProcDefIds().size()!=0){
//            List<String> procDefIds = query.getProcDefIds();
//            StringBuffer stringBuffer = new StringBuffer();
//            //        拼接orderId
//            for(int i = 0 ; i<procDefIds.size();i++){
//                if(i==procDefIds.size()-1){
//                    stringBuffer.append(procDefIds.get(i));
//                }else {
//                    stringBuffer.append(procDefIds.get(i)+",");
//                }
//            }
//            Rule rule= new Rule();
//            rule.setField("procDefId");
//            rule.setOp(Rule.OP_IN);
//            rule.setData(stringBuffer.toString());
//            rules.add(rule);
//        }
//        //任务名称
//        if(null != query.getTaskName()&&query.getTaskName()!=""){
//            Rule rule= new Rule();
//            rule.setField("taskName");
//            rule.setOp(Rule.OP_LIKE);
//            rule.setData(query.getTaskName());
//            rules.add(rule);
//        }
//        filters.setRules(rules);
//
////        List<Filters> filtersList = buildFilters();//权限数据过滤
////        filtersList.add(filters);//查询条件过滤
//        Filters filtersAll = new Filters();
//        filtersAll.setGroupOp(Filters.OP_AND);
//        filtersAll.setGroups(filtersList);
        String userId = request.getHeader("gateway_userId");
        Map<String ,Object> map = new HashMap<>();
        List<String> roleList = getUserRoleInfo(userId);
        map.put("roleList",roleList);
        map.put("userId",userId);
        if(query.getStatus()!=null&&query.getStatus()!=""){
            map.put("status",query.getStatus());
        }
        String orgType = request.getHeader("corp_type");
        if (!"Corp".equals(orgType)) {
            String companyId = request.getHeader("corp_id");//公司ID
            map.put("createOrgId",companyId);
        }
        if(null != query.getTaskName()&&query.getTaskName()!=""){
            map.put("taskName","%"+query.getTaskName()+"%");
        }
        if(null != query.getProcDefIds()&&query.getProcDefIds().size()!=0){
            map.put("procDefIdStr",query.getProcDefIds());
        }
        String sql = this.getSql();
        List<OmsTaskInstance> omsTaskInstances = super.sqlQuery(sql, OmsTaskInstance.class,map);
        List<OmsTasksResponseVO> omsTasksResponseVOS = new ArrayList<>();
        for (OmsTaskInstance omsTaskInstance : omsTaskInstances) {
            OmsTasksResponseVO omsTasksResponseVO = new OmsTasksResponseVO();
            BeanUtils.copyProperties(omsTaskInstance,omsTasksResponseVO);
            omsTasksResponseVOS.add(omsTasksResponseVO);
        }
        log.debug("logistics searchAll is end result is {} ", JSONObject.toJSON(omsTasksResponseVOS));
        return omsTasksResponseVOS;
    }

    public List<OmsTaskInstance> listAll (Filters filters) throws Exception {
        Specification<OmsTaskInstance> sc = new Myspec(filters);
        List<OmsTaskInstance> instanceList = dao.findAll(sc);
        return instanceList;
    }
    /**
     * 通过OrderId加上status确定一条待办数据，
     * WMS特殊情况，不包括所有情况。
     * @param orderId
     * @return
     */
    public String queryByStatusAndOrderId(String orderId){
        List<OmsTaskInstance> omsTaskInstances = omsTaskInstanceRepository.queryByStatusAndOrderId("0", orderId);
        if(omsTaskInstances.size() == 0){
            log.error("omsTaskInstance is null parameter is orderId:{} and status :2 ",orderId);
            throw new ZLSTException("omsTaskInstance is null");
        }else if(omsTaskInstances.size()>1){
            log.error("omsTaskInstanceList length is over one parameter is orderId:{} and status :2 ",orderId);
            throw new ZLSTException("omsTaskInstance length is over one");
        }
        OmsTaskInstance omsTaskInstance = omsTaskInstances.get(0);
        String taskId = omsTaskInstance.getId();
        return taskId;
    }
    /**
     * 通过OrderId加上status确定一条待办数据，
     * WMS特殊情况，不包括所有情况。
     * @param orderId
     * @return
     */
    public OmsTaskInstance queryOmsTaskInstanceByOrderId(String orderId){
        List<OmsTaskInstance> omsTaskInstances = omsTaskInstanceRepository.queryByStatusAndOrderId("0", orderId);
        if(omsTaskInstances.size() == 0){
            log.error("omsTaskInstance is null parameter is orderId:{} and status :2 ",orderId);
            throw new ZLSTException("omsTaskInstance is null");
        }else if(omsTaskInstances.size()>1){
            log.error("omsTaskInstanceList length is over one parameter is orderId:{} and status :2 ",orderId);
            throw new ZLSTException("omsTaskInstance length is over one");
        }
        OmsTaskInstance omsTaskInstance = omsTaskInstances.get(0);
        return omsTaskInstance;
    }

    /**
     * 批量推动工作流
     * @param request
     * @throws Exception
     */
    public List<String> TaskBitch(List<TaskBitchVO> request)throws Exception{
        if (log.isDebugEnabled()) {
            log.debug("omsTaskInstanceOpRequest paramsInfo omsTaskInstanceOpRequest=" + JSON.toJSONString(request));
        }
        List<String> oderIdStr = new ArrayList<>(); // 推动失败的OrderId
        for (TaskBitchVO taskBitchVO : request) {
            String orderId = null;
            try {
                String userId = null;
                String userName = null ;
                for (RestVariableItemVo itemVo : taskBitchVO.getVariables()) {
                    if("createOperId".equals(itemVo.getName())){
                        userId = itemVo.getValue();
                    }
                    if("createOperName".equals(itemVo.getName())){
                        userName = itemVo.getValue();
                    }
                }
                OmsTaskInstanceOpRequest omsTaskInstanceOpRequest = new OmsTaskInstanceOpRequest();
                omsTaskInstanceOpRequest.setUserId(userId);
                omsTaskInstanceOpRequest.setUserName(userName);
                omsTaskInstanceOpRequest.setVariables(taskBitchVO.getVariables());
                OmsTaskInstance omsTaskInstance = queryOmsTaskInstanceByOrderId(taskBitchVO.getOrderId());
                //如果不传节点定义id 就直接推流程
                if(Common.notNull(taskBitchVO.getTaskDefId())){
                    //如果传了，判断是否与传过得值相等，不相等不推流程，并且插入失败的orderId
                    if(taskBitchVO.getTaskDefId().equals(omsTaskInstance.getTaskDefId())){
                        orderMgrService.opOmsTaskInstance(omsTaskInstance.getId(), omsTaskInstanceOpRequest);
                    }
//                    else{
//                        orderId = taskBitchVO.getOrderId();
//                    }
                }else{
                    orderMgrService.opOmsTaskInstance(omsTaskInstance.getId(), omsTaskInstanceOpRequest);
                }

            } catch (Exception e) {
                e.printStackTrace();
                orderId = taskBitchVO.getOrderId();
                log.error("task error request is {}"+JSON.toJSONString(taskBitchVO));
            }finally {
                //不管怎么样，有值就插
                if(Common.notNull(orderId)){
                    oderIdStr.add(orderId);
                }
            }
        }
        return oderIdStr;
    }

}
