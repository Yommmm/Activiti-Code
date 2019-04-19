package com.zlst.module.demo.bean.vo;

import com.zlst.module.demo.bean.DemoBean;
import com.zlst.database.common.inter.VoConverter;

/**
 * Created by 170059 on 2017/8/28.
 */
public class DemoBeanVoConvert implements VoConverter<DemoBean,DemoBeanVo> {
    @Override
    public DemoBeanVo convert(DemoBean demoBean) {
        DemoBeanVo vo = new DemoBeanVo();
        vo.setfName(demoBean.getFirstName());
        vo.setlName(demoBean.getLastName());
        return vo;
    }

}
