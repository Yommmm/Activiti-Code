package com.zlst.module.demo.service;

import com.zlst.module.demo.bean.DemoBean;
import com.zlst.module.demo.dao.DemoRepository;
import com.zlst.database.core.service.QueryAndOperateServ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by user03 on 2017/7/10.
 */
@Service
public class DemoService extends QueryAndOperateServ<DemoBean,DemoRepository> {

    @Autowired
    private DemoRepository demoRepository;

    public List<DemoBean> queryDemoByFirstName (String firstName){
        return demoRepository.queryDemoByFirstName(firstName);
    }


}
