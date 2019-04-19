package com.zlst.module.demo.dao;

import com.zlst.module.demo.bean.DemoBean;
import com.zlst.database.core.dao.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by user03 on 2017/7/10.
 */
@Repository
public interface DemoRepository  extends BaseRepository<DemoBean,String> {

    @Query(value = "select * from demo_jpa where firstname=:firstname",nativeQuery = true)
    List<DemoBean> queryDemoByFirstName(@Param("firstname") String firstName);
}
