package com.zlst.module.order.dao;

import com.zlst.database.core.dao.BaseRepository;
import com.zlst.module.order.bean.OmsTaskDefine;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by songcj on 2017/9/21.
 */
@Repository
public interface OmsTaskDefineRepository extends BaseRepository<OmsTaskDefine, String> {

    @Query(value = "select * from oms_task_define where proc_def_id=:procDefId and task_def_id=:taskDefId limit 1", nativeQuery = true)
    OmsTaskDefine queryInfTaskDefineByProcessAndTaskId(@Param("procDefId") String procDefId, @Param("taskDefId") String taskDefId);

}
