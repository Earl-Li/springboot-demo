package com.atlisheng.admin.servlet;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
@Slf4j
//@WebFilter("/css/*")//可以设置Filter拦截访问静态资源的所有请求,注意拦截所有单*是Servlet的写法，
// 双*是Spring家的写法，使用原生Servlet需要使用单*表示所有路径
public class MyFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //这个是用于过滤器初始化的
        log.info("MyFilter正在初始化");
    }

    @Override
    public void destroy() {
        //这个方法用于过滤器销毁
        log.info("MyFilter正在销毁");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //这里面写业务逻辑
        log.info("MyFilter正在工作");
        //Filter工作需要filter链的doFilter方法进行放行
        chain.doFilter(request,response);
    }
}
