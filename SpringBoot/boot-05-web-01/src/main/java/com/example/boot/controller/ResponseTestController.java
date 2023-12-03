package com.example.boot.controller;

import com.example.boot.pojo.Person;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
public class ResponseTestController {

    @ResponseBody//@ResponseBody注解会调用RequestResponseBodyMethodProcessor-->messageConverter
    @GetMapping("hello")
    public FileSystemResource file(){
        //以FileSystemResource注明返回值类型底层会自动根据返回值类型自动去调用对应的消息转换器
        return null;
    }

    /**
     * @return {@link Person }
     * @描述 扩展场景:
     *          1.如果是浏览器发请求直接返回xml数据    [application/xml]    jacksonXmlConverter
     *          2.如果是ajax请求，返回json数据    [application/json]    jacksonJsonConverter
     *          3.如果是客户端如硅谷app发请求，返回自定义协议数据    [application/x-guigu]    xxxConverter
     *                  属性值1；属性值2；...[这种方式只要值，省了很多数据，传输更快]
 *          即适配三种不同的场景
     *
*          解决流程:
     *          1. 添加自定义的MessageConverter进系统底层
     *          2. 系统底层会自动统计处所有MessageConverter针对返回值类型能操作的内容类型
     *          3. 服务器根据客户端需要的内容类型以及自己能提供的内容类型进行内容协商，如需要x-guigu，一看刚好有x-guigu对应的转换器，就自动使用自定义
     *              转换器来处理相应的返回值
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/05/28
     * @since 1.0.0
     */
    @ResponseBody
    @GetMapping("/test/person")
    public Person getPerson(){
        Person person=new Person();
        person.setAge(27);
        person.setBirth(new Date());
        person.setUserName("zhangsan");
        return person;
    }
}
