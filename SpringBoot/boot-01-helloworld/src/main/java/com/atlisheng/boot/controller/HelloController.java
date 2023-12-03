package com.atlisheng.boot.controller;

import com.atlisheng.boot.bean.Car;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

//@ResponseBody//注意:这个注解可以标注在控制器类上，表示所有的控制器方法直接返回对象给浏览器。还可以直接用复合注解代替@ResponseBody和@Controller
//@Controller
@Slf4j
@RestController
public class HelloController {
    @Autowired
    Car car;
    @RequestMapping("/car")
    public Car car(){
        return car;//{"brand":"BYD","price":10000}
    }
    @RequestMapping("/hello")
    //@ResponseBody
    //请求参数和响应参数不乱码的原因就在于HttpEncodingAutoConfiguration
    public String handle01(@RequestParam String name){
        log.info("请求进来了!");
        return "Hello,Spring Boot 2!中文"+name;
    }
}
