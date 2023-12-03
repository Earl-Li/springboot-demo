package com.atlisheng.admin.exception;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//@Order(Ordered.HIGHEST_PRECEDENCE)//优先级，数字越小，优先级越高
@Component
public class CustomerHandlerExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        //也可以学SpringMVC直接发送错误请求/error去找BasicErrorController进行处理，提供对应请求码的错误页面即可,woc这个有异常也要捕获
        try {
            response.sendError(511,"我自己定义的控制器异常解析器");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ModelAndView();//可以自定义返回ModelAndView，设置跳转视图和请求域数据
    }
}
