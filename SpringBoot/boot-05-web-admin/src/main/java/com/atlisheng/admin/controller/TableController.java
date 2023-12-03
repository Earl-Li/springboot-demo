package com.atlisheng.admin.controller;

import com.atlisheng.admin.bean.User;
import com.atlisheng.admin.exception.UserTooManyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

/**
 * 负责处理table相关的路径跳转
 */
@Controller
public class TableController {
    /**
     *如果参数中不带请求参数a或者参数类型错误就会报错400，400 BadRequest 一般都是浏览器参数没有传递正确
     * */
    @GetMapping("/basic_table")
    public String basic_table(@RequestParam("a") int a){
        //模拟500错误
        int errorTest=100/0;
        return "table/basic_table";
    }


    @GetMapping("/dynamic_table")
    public String dynamic_table(Model model){
        //表格内容的遍历，准备表格
        List<User> users= Arrays.asList(new User("zhangsan","123"),
                new User("lisi","1234"),
                new User("wangwu","12345"),
                new User("zhaoliu","123456"));
        model.addAttribute("users",users);
        if (users.size()>3) {
            throw new UserTooManyException();//注意哦，这个手动抛异常并没有在方法上抛异常，和在方法上抛效果是一样的
        }
        return "table/dynamic_table";
    }

    @GetMapping("/responsive_table")
    public String responsive_table(){
        return "table/responsive_table";
    }

    @GetMapping("/editable_table")
    public String editable_table(){
        return "table/editable_table";
    }
}
