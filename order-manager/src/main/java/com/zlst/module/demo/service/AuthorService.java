package com.zlst.module.demo.service;

import com.zlst.module.demo.bean.Author;
import com.zlst.module.demo.dao.AuthorRepository;
import com.zlst.database.core.service.QueryAndOperateServ;
import org.springframework.stereotype.Service;

/**
 * Created by user03 on 2017/7/10.
 */
@Service
public class AuthorService extends QueryAndOperateServ<Author,AuthorRepository> {


}
