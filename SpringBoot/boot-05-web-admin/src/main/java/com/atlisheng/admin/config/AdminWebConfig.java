package com.atlisheng.admin.config;

import com.atlisheng.admin.interceptor.LoginInterceptor;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

//@EnableWebMvc//这个注解表示用户全面接管SpringMVC，所有的静态资源、视图解析器、欢迎页等等所有的Spring官方的自动配置全部失效，必须自己来定义所有的事情比如静态资源的访问等全部底层行为
@Configuration
public class AdminWebConfig implements WebMvcConfigurer {
    /**
     * @param registry 注册表
     * @描述 定义静态资源行为
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/06/06
     * @since 1.0.0
     */
    /*@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        *//**意思是访问"/aa/**" 的所有请求都去 "classpath:/static/ 下面对**内容"进行匹配*//*
        registry.addResourceHandler("/aa/**")//添加静态资源处理，需要传参静态资源的请求路径，
                .addResourceLocations("classpath:/static/");//传参对应static目录下的所有资源路径
    }*/

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")//所有请求都会被拦截包括静态资源
                .excludePathPatterns("/","/login","/css/**","/fonts/**","/images/**","/js/**","/aa/**","/sql","/acct");
        /**
         除了登录页面还要放行所有的静态资源，包括
                    样式的/css/**,
                    字体的/fonts/**,
                    图片的/images/**,
                    Script脚本的/js/**,
         把以上路径添加到拦截器的排除路径中即可
         注意：不能直接写/static来表示静态资源，因为静态资源访问请求路径中不含/static
         要使用/static来排除静态资源需要通过spring.mvc.static-path-pattern属性配置静态资源访问前缀，以后所有访问静态资源的路径都需要添加
         访问前缀如/static，自然可以通过放行/static/**来放行所有静态资源的访问
         */
    }

    @Bean
    public WebMvcRegistrations webMvcRegistrations(){
        return new WebMvcRegistrations() {
            @Override//这种方式可以重新定义HandlerMapping的底层行为，但是太过底层，没有完全搞清楚SpringMVC的底层原理不建议这么做
            public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
                return WebMvcRegistrations.super.getRequestMappingHandlerMapping();
            }

            @Override
            public RequestMappingHandlerAdapter getRequestMappingHandlerAdapter() {
                return WebMvcRegistrations.super.getRequestMappingHandlerAdapter();
            }

            @Override
            public ExceptionHandlerExceptionResolver getExceptionHandlerExceptionResolver() {
                return WebMvcRegistrations.super.getExceptionHandlerExceptionResolver();
            }
        };
    }
}
