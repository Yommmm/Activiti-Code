package com.zlst.module.logistics.ctrl;

import com.zlst.module.logistics.bean.OmsTaskAllReqVO;
import com.zlst.module.logistics.bean.TaskBitchInfoVO;
import com.zlst.module.logistics.bean.TaskBitchVO;
import com.zlst.module.logistics.service.LogisticsOrderService;
import com.zlst.module.logistics.bean.OmsTaskRequestVO;
import com.zlst.param.ObjectToResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by 刘涛 on 2018/3/20.
 */
@RestController
@RequestMapping("/logisticsOrder")
@EnableAutoConfiguration
public class LogisticsOrderCtrl {
    private final static Logger LOG = LoggerFactory.getLogger(LogisticsOrderCtrl.class);
    @Autowired
    private LogisticsOrderService logisticsOrderService;
    /**
     * 大物流改造-分页查询
     */
    @PostMapping(value = "/queryWithPage")
    public Object searchWithPage(@RequestBody OmsTaskRequestVO query) throws Exception {
        return  ObjectToResult.getResult(logisticsOrderService.queryOmsTaskForPage(query));
    }
    /**
     * 大物流改造-WMS-全量查询
     */
    @PostMapping(value = "/list")
    public Object searchAll(@RequestBody OmsTaskAllReqVO query) throws Exception {
        return  ObjectToResult.getResult(logisticsOrderService.searchAll(query));
    }

    /**
     * 大物流改造-通过orderId查询TaskId，以推动人工节点
     * 此场景，只适用特殊流程。
     * @param orderId
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/getTaskIdByOrderId/{orderId}")
    public Object queryByStatusAndOrderId(@PathVariable String orderId)throws Exception{
        return ObjectToResult.getResult(logisticsOrderService.queryByStatusAndOrderId(orderId));
    }

    /**
     * 批量推动工作流
     * @param request
     * @throws Exception
     */
    @PostMapping(value = "/taskBitch")
    public Object TaskBitch(@RequestBody TaskBitchInfoVO request)throws Exception{
        List<String> oderIdStrList = logisticsOrderService.TaskBitch(request.getTaskBitchVOList());
        return ObjectToResult.getResult(oderIdStrList);
    }



}
