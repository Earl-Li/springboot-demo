package com.atlisheng.admin.servlet;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
//(proxyBeanMethods = true)是保证依赖的组件始终是单实例的
@Configuration//注意这个配置类的@Configuration注解不要设置(proxyBeanMethods = false),因为有了这个设置有组件依赖，
// 一个组件被多次依赖就会创建多个对象，功能上不会有大问题，但是会导致容器中有很多冗余的对象
public class MyRegistConfig {
    @Bean
    public ServletRegistrationBean myServlet(){
        MyServlet myServlet=new MyServlet();
        return new ServletRegistrationBean(myServlet,"/my","/my02");
    }
    @Bean
    public FilterRegistrationBean myFilter(){
        MyFilter myFilter=new MyFilter();

        //方式一
        //return new FilterRegistrationBean(myFilter,myServlet());//注意FilterRegistrationBean的构造方法可以传参ServletRegistrationBean，该组件对应的Servlet对应的映射路径都会对过滤器起作用，

        //方式二
        //FilterRegistrationBean的构造方法不能直接写映射路径，只能通过setUrlPatterns方法传参字符串的list集合来设置映射路径，原因不明，记住就行
        //Arrays.asList("/my","/css/*")就可以把一串可变数量的字符串参数转换成list集合
        FilterRegistrationBean filterRegistrationBean=new FilterRegistrationBean(myFilter);
        //这个路径设置可以实现以不同的请求路径访问servlet可以控制哪些路径需要经过filter
        filterRegistrationBean.setUrlPatterns(Arrays.asList("/my","/css/*"));
        return filterRegistrationBean;
    }
    @Bean//这个是监听时间点的，有些监听器不需要设置请求路径
    public ServletListenerRegistrationBean myListener(){
        MyServletContextListener myServletContextListener=new MyServletContextListener();
        return new ServletListenerRegistrationBean(myServletContextListener);
    }
}
