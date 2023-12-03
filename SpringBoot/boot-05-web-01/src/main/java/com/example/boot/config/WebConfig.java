package com.example.boot.config;

import com.example.boot.converter.GuiGuMessageConverter;
import com.example.boot.pojo.Pet;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import org.springframework.web.accept.ParameterContentNegotiationStrategy;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UrlPathHelper;

import java.util.*;

@Configuration(proxyBeanMethods = false)
public class WebConfig /*implements WebMvcConfigurer*/ {


    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter(){
        HiddenHttpMethodFilter methodFilter=new HiddenHttpMethodFilter();
        methodFilter.setMethodParam("_m");
        return methodFilter;
    }
    //方式一:使用@Bean配置一个WebMvcConfigurer组价
    @Bean
    public WebMvcConfigurer webMvcConfigurer(){
        return new WebMvcConfigurer() {//woc这是什么写法，没见过，记录一下，匿名内部类


            /**
             * @param configurer 配置
             * @描述 自定义内容协商策略
             * @author Earl
             * @version 1.0.0
             * @创建日期 2023/05/29
             * @since 1.0.0
             */
            @Override
            public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
                Map<String, MediaType> mediaTypes=new HashMap<String,MediaType>();
                mediaTypes.put("json",MediaType.APPLICATION_JSON);
                mediaTypes.put("xml",MediaType.APPLICATION_ATOM_XML);
                //自定义的媒体类型使用MediaType.parseMediaType对字符串进行转换
                mediaTypes.put("x-guigu",MediaType.parseMediaType("application/x-guigu"));
                //创建基于参数的内容协商策略对象,需要传入mediaTypes属性，这个属性是从父类的父类继承来的，是一个Map集合，存储了可以处理的客户端要求的媒体类型
                ParameterContentNegotiationStrategy parameterStrategies = new ParameterContentNegotiationStrategy(mediaTypes);
                parameterStrategies.setParameterName("mediaType");//这一步可以将默认参数名format改为mediaType
                HeaderContentNegotiationStrategy headerStrategy = new HeaderContentNegotiationStrategy();
                //Arrays.asList(parameterStrategies)这个是什么意思没说
                configurer.strategies(Arrays.asList(parameterStrategies,headerStrategy));
            }

            @Override
            public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
                converters.add(new GuiGuMessageConverter());
            }

            @Override
            public void configurePathMatch(PathMatchConfigurer configurer) {
                UrlPathHelper urlPathHelper=new UrlPathHelper();
                //设置removeSemicolonContent属性为false就可以保留矩阵变量功能分号后面的内容
                urlPathHelper.setRemoveSemicolonContent(false);
                //感觉像把容器中的默认urlPathHelper换成自己创建的
                configurer.setUrlPathHelper(urlPathHelper);
            }

            @Override
            public void addFormatters(FormatterRegistry registry){
                registry.addConverter(new Converter<String, Pet>(){//这个converter的类型转换关系就会被参数对应pet属性自动获取调用其中的convert方法
                    @Override
                    public Pet convert(String source) {//这个source就是页面提交过来的值"阿猫,3",这
                        // 里面就是自定义类型转换的操作，这里面直接把数据转换就搞进去了，这样不好，违背OOP软件设计原则，而且
                        //pojo类写死了
                        //StringUtils工具类【这个工具类是Spring的】的方法可以去了解一下
                        if (!StringUtils.isEmpty(source)) {
                            Pet pet=new Pet();
                            String[] split = source.split(",");
                            pet.setName(split[0]);
                            pet.setAge(Integer.parseInt(split[1]));
                            return pet;//{"userName":"zhangsan","age":18,"birth":"2019-12-09T16:00:00.000+00:00","pet":{"name":"阿猫","age":3}}
                        }
                        return null;//如果传过来的字符串都是空的就返回null
                    }
                });
            }
        };
    }

    //方式二:自定义配置类实现WebMvcConfigurer接口并重写configurePathMatch方法
    /*@Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        UrlPathHelper urlPathHelper=new UrlPathHelper();
        //设置removeSemicolonContent属性为false就可以保留矩阵变量功能分号后面的内容
        urlPathHelper.setRemoveSemicolonContent(false);
        //感觉像把容器中的默认urlPathHelper换成自己创建的
        configurer.setUrlPathHelper(urlPathHelper);
    }*/
}
