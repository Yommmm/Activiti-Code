package com.zlst.module.demo.service;

import com.zlst.module.demo.bean.Book;
import com.zlst.module.demo.dao.BookRepository;
import com.zlst.database.core.service.QueryAndOperateServ;
import org.springframework.stereotype.Service;

/**
 * Created by user03 on 2017/7/10.
 */
@Service
public class BookService extends QueryAndOperateServ<Book,BookRepository> {


}
