package com.atlisheng.admin.servlet;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@Slf4j
//@WebListener
public class MyServletContextListener implements ServletContextListener {//监听应用上下文的创建和销毁
    @Override
    public void contextInitialized(ServletContextEvent sce) {//监听当前项目的初始化
        log.info("MyServletContextListener监听到项目初始化完成");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.info("MyServletContextListener监听到项目销毁");//直接点stop相当于拔电源，是监听不到项目的销毁的，那么如何相当于停项目呢
    }
}
