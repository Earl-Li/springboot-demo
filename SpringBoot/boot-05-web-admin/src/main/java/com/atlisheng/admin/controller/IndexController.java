package com.atlisheng.admin.controller;

import com.atlisheng.admin.bean.Account;
import com.atlisheng.admin.bean.City;
import com.atlisheng.admin.bean.User;
import com.atlisheng.admin.service.AccountService;
import com.atlisheng.admin.service.CityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
public class IndexController {
    @Resource
    JdbcTemplate jdbcTemplate;

    @Resource
    AccountService accountService;

    @Resource
    CityService cityService;

    @ResponseBody
    @PostMapping("/city")
    public City saveCity(City city){
        System.out.println(city);
        return cityService.saveCity(city);
    }

    @GetMapping("/city")
    @ResponseBody
    public City getCityById(@RequestParam Long id){
        return cityService.getCityById(id);
    }

    @ResponseBody
    @GetMapping("/acct")
    public Account getById(@RequestParam("id") Long id){
        return accountService.getActById(id);
    }


    @ResponseBody
    @GetMapping("/sql")
    public String queryFromDb(){
        Long aLong = jdbcTemplate.queryForObject("select count(*) from emp", Long.class);
        return aLong.toString();
    }


    /**
     * @return {@link String }
     * @描述 登录页
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/05/30
     * @since 1.0.0
     */
    @GetMapping({"/","/login"})
    public String IndexController(){
        return "login";
    }

    /**
     * @param user    用户
     * @param session 会话
     * @param model   模型
     * @return {@link String }
     * @描述 登录成功跳转主页面,转发存在表单重复提交问题，一般适用重定向到目标页面
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/05/30
     * @since 1.0.0
     */
    @PostMapping("/login")
    //public String main(String username,String password){//直接用User对象封装表单提交的值
    public String main(User user, HttpSession session, Model model){
        //如果会话域中账户和密码不为空，则判定已经登录过,此时把username和password放入会话域供控制器方法mainPage做登录判断
        //不为空的判断可以使用Spring下的StringUtils的isEmpty、hasText(有内容)、hasLength(有长度)方法进行判断

        //if (StringUtils.isEmpty(user.getUsername())&&StringUtils.hasLength(user.getPassword())) {
        if (!StringUtils.isEmpty(user.getUsername())&&"123".equals(user.getPassword())) {
            session.setAttribute("loginUser",user.getUsername());
            //不要放密码，这样不安全
            //session.setAttribute("password",user.getPassword());
            return "redirect:/main.html";
        }
        //return "main";
        //return "redirect:/main.html";
        //登录不成功就回到登录页面,此时可以给登录页面传递信息，给请求域中放提示错误信息
        model.addAttribute("msg","账号密码错误");
        return "login";
    }

    /**
     * @return {@link String }
     * @描述 主页面，这样也存在问题，只要前端一敲这个地址就可以不登录直接来到主页面，需要在这个控制器方法中加一个登录状态判断
     * 解决办法：创建一个User的bean类
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/05/30
     * @since 1.0.0
     */
    @GetMapping("/main.html")
    public String mainPage(HttpSession session,Model model){
        log.info("当前方法是:{}","mainPage");
        //直接通过路径访问主页面需要判断是否经过登录，合理的做法是通过拦截器或者过滤器判断登录状态，这里直接为了遍历写在方法里面
        /**if (session.getAttribute("loginUser") != null) {
            //如果会话域有用户名，跳转主页面【这还是有bug，一个登录处处登录，可以改善，暂时不管】
            return "main";
        }else {
            //登录不成功就回到登录页面,此时可以给登录页面传递信息，给请求域中放提示错误信息
            model.addAttribute("msg","请重新登录");
            return "login";
        }*/
        return "main";
    }
}
