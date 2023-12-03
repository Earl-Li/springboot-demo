package com.atlisheng.boot;

import ch.qos.logback.core.db.DBHelper;
import com.atlisheng.boot.bean.Pet;
import com.atlisheng.boot.bean.User;
import com.atlisheng.boot.config.MyConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.cache.interceptor.CacheAspectSupport;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.util.Arrays;

/**
 * @author Earl
 * @version 1.0.0
 * @描述 主程序类，必须使用@SpringBootApplication注解告诉springBoot这是一个springboot应用，并且在主程序类的主方法中编写
 * SpringApplication.run(MainApplication.class,args)传入主程序类的class对象和主方法的args，该方法的作用相当于让主程序类对应的springboot
 * 应用跑起来
 * @创建日期 2023/05/17
 * @since 1.0.0
 */
//@SpringBootApplication(scanBasePackages = "com.atlisheng")
//等同于以下三个注释
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan("com.atlisheng")
public class MainApplication {
    public static void main(String[] args) {
        //SpringApplication.run(MainApplication.class, args)会返回IoC容器
        ConfigurableApplicationContext run = SpringApplication.run(MainApplication.class, args);
        //通过IoC容器的getBeanDefinitionNames()方法可以获取所有组件的名字，返回字符串数组
        String[] names = run.getBeanDefinitionNames();
        //Stream流式编程
        Arrays.stream(names).forEach(name->{
            System.out.println(name);
        });

        //从容器中获取组件
        /*Pet tom01=run.getBean("tom",Pet.class);
        Pet tom02=run.getBean("tom",Pet.class);
        System.out.println("组件:"+((tom01==tom02)?"单实例":"多实例"));

        //配置类也是一个组件，可以从容器中获取，从对象引用可以看出配置类是一个被CGLIB增强的代理对象
        MyConfig bean = run.getBean(MyConfig.class);
        System.out.println(bean);
        //com.atlisheng.boot.config.MyConfig$$EnhancerBySpringCGLIB$$a82a58f4@47ffe971

        //外部调用配置类的组件注册方法无论多少遍获取的都是注册在容器中的单实例对象
        //如果@Configuration(proxyBeanMethods = true)默认值就是true，此时配置类使用代理对象调用组件注册方法，SpringBoot总会检查该组件是否已在容器中存在如果有会自动保持组件单实例
        //如果@Configuration(proxyBeanMethods = false)，此时配置类不会使用调用组件注册方法，SpringBoot不会保持该组件的单实例
        User user=bean.user01();
        User user1=bean.user01();
        System.out.println("组件:"+((user==user1)?"单实例":"多实例"));

        User user01 = run.getBean("user01", User.class);
        Pet tom = run.getBean("tom", Pet.class);
        System.out.println("用户"+(user01.getPet()==tom?"user01依赖于组件tom":"user01不依赖于组件tom"));

        //从容器中根据类型获取该类型对应的所有组件名字
        String[] users = run.getBeanNamesForType(User.class);
        Arrays.stream(users).forEach(user2->{
            System.out.println(user2);
        });
        //从容器中根据类型获取单个组件
        DBHelper bean1 = run.getBean(DBHelper.class);
        System.out.println(bean1);*/

        boolean tom = run.containsBean("tom");
        System.out.println("容器中Tom组件:"+(tom?"存在":"不存在"));

        boolean user01 = run.containsBean("user01");
        System.out.println("容器中user01组件:"+(user01?"存在":"不存在"));

        boolean tom22 = run.containsBean("tom22");
        System.out.println("容器中tom22组件:"+(tom22?"存在":"不存在"));

        boolean haha = run.containsBean("haha");
        boolean hehe = run.containsBean("hehe");
        System.out.println("haha:"+(haha?"存在":"不存在"));
        System.out.println("hehe:"+(hehe?"存在":"不存在"));

        //获取IoC容器中的组件个数
        int beanDefinitionCount = run.getBeanDefinitionCount();
        System.out.println(beanDefinitionCount);

        //查看IoC容器中CacheAspectSupport类型的组件有几个，根据类型获取组件名字，并获取名字数组的长度
        String[] beanNamesForType = run.getBeanNamesForType(CacheAspectSupport.class);
        System.out.println(beanNamesForType.length);

        String[] beanNamesForType1 = run.getBeanNamesForType(WebMvcProperties.class);
        System.out.println(beanNamesForType1.length);//1

    }
}
