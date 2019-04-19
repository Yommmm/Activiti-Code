package com.zlst.module.demo.ctrl;


import com.zlst.module.demo.bean.Author;
import com.zlst.module.demo.service.AuthorService;
import com.zlst.database.core.ctrl.QueryAndOperateCtrl;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by user03 on 2017/7/10.
 */
@RestController
@RequestMapping("/author")
@EnableAutoConfiguration
public class AuthorCtrl extends QueryAndOperateCtrl<Author, AuthorService> {


}
