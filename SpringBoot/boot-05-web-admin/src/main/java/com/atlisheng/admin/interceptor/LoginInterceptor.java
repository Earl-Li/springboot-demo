package com.atlisheng.admin.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author Earl
 * @version 1.0.0
 * @描述 登录状态检查
 * 1. 配置好拦截器需要拦截哪些请求
 * 2. 把这些配置放在容器中
 * @创建日期 2023/06/02
 * @since 1.0.0
 */
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    /**
     * @param request  请求
     * @param response 响应
     * @param handler  处理程序
     * @return boolean
     * @描述 控制器方法执行前处理
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/06/02
     * @since 1.0.0
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取请求路径的URI
        String uri = request.getRequestURI();
        log.info("preHandle拦截的请求路径是{}",uri);//log打印日志可以直接在字符串的{}中添加变量自动拼接字符串
        /**
         * 访问登录页拦截的静态资源有
         * 拦截的请求路径是/js/modernizr.min.js
         * 拦截的请求路径是/js/bootstrap.min.js
         * 拦截的请求路径是/images/login-logo.png
         * 拦截的请求路径是/js/jquery-1.10.2.min.js
         * 拦截的请求路径是/css/style-responsive.css
         * 拦截的请求路径是/css/style.css
         * */
        HttpSession session = request.getSession();
        if (session.getAttribute("loginUser") != null) {
            System.out.println(session.getAttribute("loginUser"));
            //放行
            return true;
        }
        /**拦截住最好跳转登录页面,
         *      注意1：这里拦截器拦截所有把静态资源的访问也拦截了【此时直接访问静态资源也会来到不完整的登录页】，比如CSS样式，这种情况下，无需拦截的页面的样式
         *          等静态资源无法加载，导致页面很难看
     *          注意2：重定向在页面中不一定能取出会话域中的信息，使用转发解决这个问题
         *          */
        /*session.setAttribute("msg","请先登录!");
        response.sendRedirect("/");*/
        System.out.println(session.getAttribute("loginUser"));
        request.setAttribute("msg","请先登录!");
        request.getRequestDispatcher("/").forward(request,response);
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("postHandle执行{}",modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("afterCompletion执行异常{}",ex);
    }
}
