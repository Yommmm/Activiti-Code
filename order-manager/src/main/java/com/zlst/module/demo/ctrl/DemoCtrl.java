package com.zlst.module.demo.ctrl;


import com.zlst.exception.ZLSTException;
import com.zlst.module.demo.bean.Author;
import com.zlst.module.demo.bean.Book;
import com.zlst.module.demo.bean.DemoBean;
import com.zlst.module.demo.bean.vo.DemoBeanVoConvert;
import com.zlst.module.demo.service.BookService;
import com.zlst.module.demo.service.DemoService;
import com.zlst.database.core.ctrl.QueryAndOperateCtrl;
import com.zlst.database.common.annotation.ValidateFiled;
import com.zlst.database.common.annotation.ValidateGroup;
import com.zlst.param.ObjectToResult;
import com.zlst.utils.ExceptionConstants;
import com.zlst.utils.NavigatorUtil;
import com.zlst.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by user03 on 2017/7/10.
 */
@RestController
@RequestMapping("/demo")
@EnableAutoConfiguration
public class DemoCtrl extends QueryAndOperateCtrl<DemoBean, DemoService> {

    @Autowired
    private DemoService demoService;

    @Autowired
    private BookService bookService;

    /**
     * demo自定义方法
     *
     * @return
     */
    @ValidateGroup(fileds = { @ValidateFiled(filedName = "id", name = "系统ID", required = true),
            @ValidateFiled(filedName = "name", name = "姓名", required = true),
            @ValidateFiled(filedName = "name", name = "姓名", required = true,maxLen = 20),
            @ValidateFiled(filedName = "name", name = "姓名", required = true,minLen = 4),
            @ValidateFiled (filedName = "age", name = "年龄", required = true, maxVal = 70),
            @ValidateFiled (filedName = "age", name = "年龄", required = true, minVal = 5),
            @ValidateFiled (filedName = "dept_type", name = "部门类型", required = true, includeList = { "1",
                    "2", "3", "4", "5", "6", "7", "8", "9" }),
            @ValidateFiled (filedName = "c_phone", name = "联系人电话", required = true, regStr = "validateMobile")
           })
    @GetMapping(value = "/queryDemoByFirstName")
    public Object queryDemoByFirstName(String firstName) throws Exception {
        return ObjectToResult.getResult(demoService.queryDemoByFirstName(firstName));
    }

    @Override
    public Object get(@PathVariable String id) throws Exception {
        return ObjectToResult.getResult(demoService.get(id, new DemoBeanVoConvert()));
    }

    @PutMapping(value = "/listen")
    public Object listen() throws Exception {

        System.out.println("listen...");
//        saveBook();
//        throw  new Exception("dd");
        return ResultUtil.buildNormalResult();
//        throw new ZLSTException(ExceptionConstants.ERRORCODE_100001);
    }

    @Transactional
    public void saveBook()
    {
        Book book = new Book();
        book.setBookName("test");
        Author author = new Author();
        author.setName("fuwei");
        Set<Author> authorSet = new HashSet<Author>();
        book.setAuthors(authorSet);
        try {
            bookService.save(book);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
