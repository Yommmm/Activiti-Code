package com.zlst.module.demo.dao;

import com.zlst.module.demo.bean.Book;
import com.zlst.database.core.dao.BaseRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by user03 on 2017/7/10.
 */
@Repository
public interface BookRepository extends BaseRepository<Book,String> {


}
