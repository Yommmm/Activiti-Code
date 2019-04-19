package com.zlst.module.order.dao;

import com.zlst.database.core.dao.BaseRepository;
import com.zlst.module.order.bean.OmsOrderTemplateRule;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by user03 on 2017/7/10.
 */
@Repository
public interface OmsOrderTemplateRepository extends BaseRepository<OmsOrderTemplateRule, String> {

    @Query(value = "select * from oms_order_template_rule where busi_type=:busiType and effect_time<=NOW() and NOW()<=expire_time limit 1", nativeQuery = true)
    OmsOrderTemplateRule queryOmsOrderTemplatebyBusiType(@Param("busiType") String busiType);

}
