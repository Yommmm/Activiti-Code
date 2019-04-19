package com.zlst.module.demo.ctrl;


import com.zlst.module.demo.bean.Book;
import com.zlst.module.demo.service.BookService;
import com.zlst.database.core.ctrl.QueryAndOperateCtrl;
import com.zlst.utils.ResultUtil;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by user03 on 2017/7/10.
 */
@RestController
@RequestMapping("/book")
@EnableAutoConfiguration
public class BookCtrl extends QueryAndOperateCtrl<Book, BookService> {

    @PutMapping(value = "/execute")
    public Object execute() {
        System.out.println("execute...");
        return ResultUtil.buildNormalResult();
    }

}
