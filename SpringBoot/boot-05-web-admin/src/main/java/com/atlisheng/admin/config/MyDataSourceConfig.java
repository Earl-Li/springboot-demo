package com.atlisheng.admin.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;

@Configuration
public class MyDataSourceConfig {
    //@ConfigurationProperties("spring.datasource")//这个注解的作用是指定组件的属性和属性配置文件中对应前缀的同名属性进行绑定
    //@Bean
    public DataSource dataSource() throws SQLException {
        DruidDataSource druidDataSource=new DruidDataSource();
        //druidDataSource.setUrl();//也可以手动设置数据源的属性值
        //只要能调用set方法设置的属性就可以直接在全局属性配置文件中直接写
        //开启德鲁伊数据源监控统计功能
        //druidDataSource.setFilters("stat,wall");
        //druidDataSource.setMaxActive(10);//设置最大活跃线程数
        return druidDataSource;
    }

    /**
     * @return {@link ServletRegistrationBean }
     * @描述 配置druid的监控页
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/06/06
     * @since 1.0.0
     */
    //@Bean
    public ServletRegistrationBean statViewServlet(){
        StatViewServlet statViewServlet=new StatViewServlet();
        ServletRegistrationBean<StatViewServlet> registrationBean=new ServletRegistrationBean<>(statViewServlet,"/druid/*");
        registrationBean.addInitParameter("loginUsername","admin");
        registrationBean.addInitParameter("loginPassword","123456");
        return registrationBean;
    }

    //@Bean
    public FilterRegistrationBean webStatFilter(){
        WebStatFilter webStatFilter=new WebStatFilter();
        FilterRegistrationBean<WebStatFilter> filterRegistrationBean=new FilterRegistrationBean<>(webStatFilter);
        filterRegistrationBean.setUrlPatterns(Arrays.asList("/*"));
        filterRegistrationBean.addInitParameter("exclusions","*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }
}
