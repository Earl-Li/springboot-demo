package com.atlisheng.boot.config;

import ch.qos.logback.core.db.DBHelper;
import com.atlisheng.boot.bean.Car;
import com.atlisheng.boot.bean.Pet;
import com.atlisheng.boot.bean.User;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

/**
 * @author Earl
 * @version 1.0.0
 * @描述
 * 1. @Configuration注解告诉SpringBoot这是一个配置类，可以取代Spring配置文件的作用，配置类本身也是IoC容器的组件
 * 2. 配置类中可以使用@Bean注解标注方法可以给容器注册组件，默认被注册组件是单实例的，方法名作为组件id，返回值类型作为组件类型，返回值作为组件在容器中的实例
 * 3. @Configuration注解的proxyBeanMethods属性为true时(即Full模式，全配置):
 *                                      此时配置类在容器中是被CGLIB增强的代理对象，使用代理对象调用组件注册方法，SpringBoot总会检查该组件是否已在容器中存在如果有会自动保持组件单实例
 *                                      实现原理是容器中有自动去容器中找组件
 *                        proxyBeanMethods属性为false时(即Lite模式，轻量级配置):
 *                                      此时配置类在容器中是普通对象，调用组件注册方法时，SpringBoot不会保持目标组件的单实例
 *                       该性质可以用来方便的解决组件依赖的场景：
 *                                      比如User中有Pet属性，user想获取容器中已经注册的Pet组件，直接在Full模式下调用配置类的pet组件注册方法即可
 *                                      注意:Lite模式下，这种方式为user获取的pet属性不是已经在容器中注册的组件
 *                       proxyBeanMethods属性的最佳实践
 *                                      Lite模式的优点:不会检查组件是否存在于容器中，启动和加载过程会很快，当组件之间不存在依赖关系，总是将proxyBeanMethods属性设置为false，
 *                                      Full模式的优点:如果组件存在依赖关系，组件注册方法被调用总会得到已经被注册的单实例组件
 * @创建日期 2023/05/18
 * @since 1.0.0
 */
@Import({User.class, DBHelper.class})
@Configuration(proxyBeanMethods = true)//告诉SpringBoot这是一个配置类，可以取代配置文件的作用，注意:配置类也是IoC容器的一个组件
//@Configuration(proxyBeanMethods = false)
//@ConditionalOnBean(name="tom")//标注在类上表示当容器中有组件tom时这个类中所有的注册组件才生效
@ConditionalOnMissingBean(name="tom")//标注在类上表示当容器中没有组件tom时这个类中所有的注册组件才生效
@ImportResource("classpath:beans.xml")
@EnableConfigurationProperties(Car.class)
public class MyConfig {
    @Bean//配置类中使用@Bean注解标注方法可以给容器注册组件，默认是单实例的，
    // 方法名将作为组件的id，返回类型就是组件类型。返回的值就是组件在容器中的实例
    //需求:因为user01依赖于tom，如果容器中没有tom组件就不要在容器中注册user01组件了
    //@ConditionalOnBean(name="tom")//@ConditionalOnBean中有很多属性，value、name等，分别表示class对象或者组件id
    //@ConditionalOnBean(name="tom")标注在组件注册方法上时，当容器中有tom时再通过组件注册方法注册user01
    //Q:判断放在什么时候，如果user01组件先注册怎么办?
    public User user01(){
        User zhangsan = new User("zhangsan", 18);
        //user组件依赖了Pet组件
        zhangsan.setPet(tomcatPet());
        return zhangsan;
    }
    //@Bean("tom")
    @Bean("tom22")
    public Pet tomcatPet(){
        return new Pet("tomcat");
    }
}
