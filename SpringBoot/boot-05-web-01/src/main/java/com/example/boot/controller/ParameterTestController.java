package com.example.boot.controller;

import com.example.boot.pojo.Person;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ParameterTestController {
    @PostMapping("/saveuser")
    public Person saveUser(Person person){
        return person;
        //{"userName":"zhangsan","age":18,"birth":"2019-12-09T16:00:00.000+00:00","pet":{"name":"阿猫","age":"5"}}
        //如果没有表单提交数据的参数名没有使用级联属性的命名，就无法为级联属性赋值，且多个相同参数名而属性只能接收一个就只取第一个请求参数
    }

    /**
     * @return {@link Map }<{@link String },{@link Object }>
     * @描述 测试注解@PathVariable从Rest风格请求路径获取请求参数并将特定请求参数与形参对应
     *          假定请求路径:/car/2/owner/zhangsan[注意路径上的参数不带大括号]
     *          1. 在形参前面使用@PathVariable("id")能指定形参对应请求参数的位置并将请求参数赋值给形参，使用的前提
     *                  是请求映射@GetMapping("/car/{id}/owner/{username}")的匹配路径对相关请求参数用大括号进行标识，注意请求映射上大括号标注的路径变量可以动态的被替换
*               2. 使用key和value都为String类型的Map集合作为形参结合@PathVariable注解能获取请求映射注解匹配路径中所有
     *                  用大括号进行标识的请求参数
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/05/20
     * @since 1.0.0
     */
    @GetMapping("/car/{id}/owner/{username}")
    public Map<String,Object> getCar(@PathVariable("id") Integer id,
                                     @PathVariable("username") String username,
                                     @PathVariable Map<String,String> pv,
                                     HttpSession session){
        Map<String,Object> map=new HashMap<>();
        map.put("id",id);
        map.put("username",username);
        map.put("pv",pv);
        return map;
    }
    @GetMapping("/car/header")
    public Map<String,Object> getCar(@RequestHeader("User-Agent") String userAgent,
                                     @RequestHeader Map<String,String> header){
        Map<String,Object> map=new HashMap<>();
        map.put("userAgent",userAgent);
        map.put("header",header);
        return map;
    }
    @GetMapping("/car/param")
    public Map<String,Object> getCar(@RequestParam("age") Integer age,
                                     @RequestParam("inters") List<String> inters,
                                     //@RequestParam Map<String,String> params){
                                     @RequestParam Map<String,Object> params){//Map接收所有数据请求参数存在多个同名参数值的会丢值
        Map<String,Object> map=new HashMap<>();
        map.put("age",age);
        map.put("inters",inters);
        map.put("params1",params);
        return map;//{"inters":["basketball","game"],"params":{"age":"18","inters":"basketball"},"age":18}
    }

    @GetMapping("/car/cookie")
    public Map<String,Object> getCar(@CookieValue("JSESSIONID") String jSessionId,
                                     @CookieValue("JSESSIONID") Cookie cookie){
        System.out.println(cookie+" | "+cookie.getName()+":"+cookie.getValue());
        //javax.servlet.http.Cookie@4bbe7695 | JSESSIONID:44613C5A612B109B5DB9E9A6D71C6C3D
        Map<String,Object> map=new HashMap<>();
        map.put("JSESSIONID",jSessionId);
        return map;
    }

    @PostMapping("/save")
    public Map<String,Object> getCar(@RequestBody String requestBodyContent){
        System.out.println(requestBodyContent);//username=zhangsan&email=2625074321%40qq.com
        Map<String,Object> map=new HashMap<>();
        map.put("requestBodyContent",requestBodyContent);
        return map;//{"requestBodyContent":"username=zhangsan&email=2625074321%40qq.com"}
    }

    //  /cars/sell;low=34;brand=byd,audi,yd
    //@GetMapping("/cars/sell")
    @GetMapping("/cars/{path}")
    public Map<String,Object> carsSell(@MatrixVariable("low") Integer low,
                                       @MatrixVariable("brand") List<String> brand,
                                       @PathVariable("path") String path){
        System.out.println(path);
        Map<String,Object> map=new HashMap<>();
        map.put("low",low);
        map.put("brand",brand);
        map.put("path",path);//sell
        return map;//{"path":"sell","low":34,"brand":["byd","audi","yd"]}
    }

    //请求路径:   /boss/1;age=20/2;age=10
    @GetMapping("/boss/{bossId}/{empId}")
    public Map<String,Object> boss(@MatrixVariable(value = "age",pathVar = "bossId") Integer bossAge,
                                   @MatrixVariable(value = "age",pathVar = "empId") Integer empAge){
        Map<String,Object> map=new HashMap<>();
        map.put("bossAge",bossAge);
        map.put("empAge",empAge);
        return map;//{"bossAge":20,"empAge":10}
    }

}
