读文档以后的笔记

# 1、第一个SpringBoot应用

需求:浏览发送/hello请求，服务器响应Hello,SpringBoot2

使用原生spring的弊端：使用原生spring的方式,需要导入spring和springMVC依赖，编写配置文件，开发代码，将tomcat引入idea，将应用部署在tomcat上启动运行,依赖管理麻烦，配置文件麻烦

## 1.1、maven设置

注意:需要在maven的settings.xml中profiles标签配置jdk的版本为8，避免编译过程使用其他版本

```xml
<mirrors>
  <mirror>
    <id>nexus-aliyun</id>
    <mirrorOf>central</mirrorOf>
    <name>Nexus aliyun</name>
    <url>http://maven.aliyun.com/nexus/content/groups/public</url>
  </mirror>
</mirrors>
<profiles>
     <profile>
          <id>jdk-1.8</id>
          <activation>
            <activeByDefault>true</activeByDefault>
            <jdk>1.8</jdk>
          </activation>
          <properties>
            <maven.compiler.source>1.8</maven.compiler.source>
            <maven.compiler.target>1.8</maven.compiler.target>
            <maven.compiler.compilerVersion>1.8</maven.compiler.compilerVersion>
          </properties>
     </profile>
</profiles>
```

## 1.2、用springBoot开发web场景

#### 1.2.1、创建maven工程配置pom.xml

1. 在pom.xml中引入父工程spring-boot-starter-parent
2. 在pom.xml中引入web的场景启动器依赖spring-boot-starter-web

```xml
<!--向pom.xml中导入父工程spring-boot-starter-parent-->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.3.4.RELEASE</version>
</parent>

<!--配置web的场景启动器，用于springboot的web场景启动器的依赖
    导入这个依赖，web场景开发的日志，springMVC、spring核心等等的依赖都被导入进来
-->
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>
```

#### 1.2.2、编写主程序类

1. 编写主程序类如MainApplication
   + 必须用@SpringBootApplication注解告诉springBoot这是一个springboot应用，也称主程序类或主配置类
   + 在主程序类的主方法中编写代码SpringApplication.run(MainApplication.class,args)传入主程序类的class对象和主方法的args，该方法的作用相当于让主程序类对应的springboot应用跑起来
   + 可以直接运行主方法，也可以直接点debug运行SpringBoot应用

```java
/**
 * @author Earl
 * @version 1.0.0
 * @描述 主程序类，必须使用@SpringBootApplication注解告诉springBoot这是一个springboot应用，并且在主程序类的主方法中编写
 * SpringApplication.run(MainApplication.class,args)传入主程序类的class对象和主方法的args，该方法的作用相当于让主程序类对应的springboot
 * 应用跑起来
 * @创建日期 2023/05/17
 * @since 1.0.0
 */
@SpringBootApplication
public class MainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class,args);
    }
}
```

#### 1.2.3、编写业务代码

1. 在主程序类所在的包下创建控制器，编写对应/hello请求路径的对应控制器方法，并响应浏览器一段字符串，注意这里的/hello就是webapp的路径，即http://localhost:8080/hello

```java
//@ResponseBody//注意:这个注解可以标注在控制器类上，表示所有的控制器方法直接返回对象给浏览器。
//还可以直接用复合注解代替@ResponseBody和@Controller
//@Controller
@RestController
public class HelloController {
    @RequestMapping("/hello")
    //@ResponseBody
    public String handle01(){
        return "Hello,Spring Boot 2!";
    }
}
```

#### 1.2.4、测试

1. 直接运行主程序的主方法即可(对比以前还需要整Tomcat和很多配置文件)

2. 也可以直接点击debug按钮或者运行符号

   **经过测试，浏览器访问确实Ok**

#### 1.2.5、简化配置

springBoot最强大的功能是简化配置(比如改tomcat端口号，以前需要打开tomcat的配置文件改端口号)

1. springboot可以直接在类路径下的一个属性配置文件中修改所有的配置信息，该文件有固定名字application.properties

2. springboot本身有默认配置，在application.properties文件中可以进行修改的配置可以参考官方文档的Application Properties

   > 比如服务器的端口名固定为server.port;除此外还有配置的默认值信息
   >
   > 使用ctrl+f能在文档中进行搜索,IDEA对属性名还有提示功能

```xml
application.properties文件中的配置示例，注意配置名是固定的
server.port=8888
```

#### 1.2.6、简化部署

1. 使用springboot的spring-boot-maven-plugin插件把springboot应用打成一个可执行的jar包

   > 这个jar包称为小胖jar(fat.jars)，包含整个运行环境，可以直接通过DOS窗口的当前目录使用命令:
   >
   > ​	java -jar boot-01-helloworld-1.0-SNAPSHOT.jar直接运行，经验证OK
   >
   > 
   >
   > 实际生产环境部署也是直接引入打包插件打成小胖jar，直接在目标服务器执行即可，注意:要关闭DOS命	令窗口的快速编辑模式，否则鼠标只要一点DOS命令窗口，springboot应用的启动进程就会卡住,以前	需要打成war包部署到服务器上
   >
   > 
   >
   > 小胖jar中BOOT-INF下的lib下是第三方的所有jar包,BOOT-INF下的classes下是我们自己写的代码和配置	文件

2. pom.xml插件配置代码

```xml
<!--导入springboot把应用打成小胖jar的插件，注意:按视频那样不加版本号会报红，打包需要maven的clean和package操作-->
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <version>2.3.4.RELEASE</version>
        </plugin>
    </plugins>
</build>
```



#### 1.2.7、其他事项

------

1. DOS窗口检查java和mvn版本的命令
   + java -version
   + mvn -v



# 2、自动配置原理

SpringBoot两大优秀特性 : 依赖管理|自动配置

## 2.1、依赖管理

### 2.1.1、父工程做依赖管理

1. 在pom.xml使用父工程spring-boot-starter-parent进行依赖版本号管理

   ```xml
   使用父工程进行依赖管理
   <parent>
   	<groupId>org.springframework.boot</groupId>
   	<artifactId>spring-boot-starter-parent</artifactId>
   	<version>2.3.4.RELEASE</version>
   </parent>
   ```

2. 父工程的pom.xml中还有父工程spring-boot-dependencies

   ```xml
   <parent>
   	<groupId>org.springframework.boot</groupId>
   	<artifactId>spring-boot-dependencies</artifactId>
   	<version>2.3.4.RELEASE</version>
   </parent>
   ```

   + 在spring-boot-dependencies的pom.xml中的properties标签中声明了几乎开发中使用的所有jar包的版本

   + 使用ctrl+f可以搜索响应的依赖信息

   + 需要使用未被starter启动器引入的依赖，只需引入相关的groupId和artifactId，无需添加版本信息，默认使用父工程中设置的默认版本，注意引入非版本仲裁的jar要写版本号。

   + 如果想自己指定依赖的版本，直接在pom.xml中新建properties标签，依照spring-boot-dependencies中的依赖版本属性名格式配置自己想要的版本，springboot会自动根据就近原则选取用户自己配置的版本

     ```
     <properties>
     	<mysql.version>5.1.43</mysql.version>
     </properties>
     ```

[^注意]: 由SpringBoot中的spring-boot-dependencies控制版本的机制称为自动版本仲裁机制

3. 导入starter场景启动器

   ```
   <!--配置web的场景启动器，用于springboot的web场景启动器的依赖
           导入这个依赖，web场景开发的日志，springMVC、spring核心等等的依赖都被导入进来
       -->
       <dependencies>
           <dependency>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-starter-web</artifactId>
           </dependency>
       </dependencies>
   ```

   官方文档的starter相关信息位置:Using Spring Boot下的starters

   + starter是一组依赖集合的描述，只要引入一个starter，这个starter开发场景的所有依赖都被引入了

     原理实际上是依赖的传递性，因为starter-*依赖依赖于开发某个场景所需要的全部依赖

   + Spring官方starter的命名规范:`spring-boot-starter-*`*为某种场景

   + 第三方提供的starter的命名规范:` *-spring-boot-starter`

   + 所有starter的最基本依赖都是spring-boot-starter依赖

     ```xml
     所有场景启动器最底层的依赖
     <dependency>
     	<groupId>org.springframework.boot</groupId>
     	<artifactId>spring-boot-starter</artifactId>
     	<version>2.3.4.RELEASE</version>
     	<scope>compile</scope>
     </dependency>
     ```



## 2.2、自动配置

在spring-boot-starter-web的pom.xml下配置了Tomcat、SpringMVC等依赖

1. 自动配置Tomcat

   ```xml
   <dependency>
   	<groupId>org.springframework.boot</groupId>
   	<artifactId>spring-boot-starter-tomcat</artifactId>
   	<version>2.3.4.RELEASE</version>
   	<scope>compile</scope>
   </dependency>
   ```

   + 自动引入Tomcat依赖
   + 自动配置Tomcat，关于如何配置和启动Tomcat后面再讲

2. 自动配置SpringMVC

   + 自动引入SpringMVC全套组件

     ```xml
     <dependency>
       <groupId>org.springframework</groupId>
       <artifactId>spring-web</artifactId>
       <version>5.2.9.RELEASE</version>
       <scope>compile</scope>
     </dependency>
     <dependency>
       <groupId>org.springframework</groupId>
       <artifactId>spring-webmvc</artifactId>
       <version>5.2.9.RELEASE</version>
       <scope>compile</scope>
     </dependency>
     ```

   + 自动配好了SpringMVC的常用组件(功能)

     + DispatcherServlet

     + characterEncoding?是视图解析器的属性吗，作为属性为什么可以作为IoC容器的组件?

     + viewResolver 视图解析器

     + mutipartResolver 文件上传解析器

     + ...SpringBoot已经帮用户配置好了所有的web开发常见场景，会自动在容器中生效，生效原理以后再说

       > 以上组件都可以获取IoC容器并获取组件名字打印查看

3. springboot中默认的包扫描规则

   + 官方文档的Using Spring Boot下的Structuring Your Code下有默认包扫描规则

   + 默认规则为主程序所在的包及该包下的所有子包都能被扫描到，无需再配置包扫描

   + 如果必须要扫描主程序类所在包外的包或类，可以在标注主程序类的@SpringBootApplication注解中为其属性scanBasePackages赋值更大的扫描范围即可

     ```java
     //由于@SpringBootApplication由@ComponentScan注解复合而成，所以不能直接在主程序类上使用@ComponentScan扩大扫描范围，会提示注解重复
     @SpringBootApplication(scanBasePackages = "com.atlisheng")
     ```

   + 可以直接把@SpringBootApplication注解拆成三个注解，并在注解之一@ComponentScan中扩大包扫描的范围，这样不会报注解重复异常

     ```
     @SpringBootApplication(scanBasePackages = "com.atlisheng")
     等同于
     @SpringBootConfiguration
     @EnableAutoConfiguration
     @ComponentScan("com.atlisheng")
     ```

4. 自动配置的默认值

   + SpringBoot的自动配置都有默认值，如果想要修改直接在application.properties文件中修改对应的key和value即可

   + 默认配置最终都是映射到某个类上，如：`MultipartProperties`

     配置文件的值最终会绑定每个类上，这个类会在容器中创建对象

     研究如何映射绑定和使用后面介绍

5. 自动配置按需加载
   + 引入了哪些场景启动器，对应的自动配置才会开启
   + SpringBoot所有的自动配置功能都在spring-boot-starter包依赖的spring-boot-autoconfigure 包里面，在外部libirary中的autoconfigure中可以找到，里面按照amqp、aop、拼接码、缓存、批处理等等场景按包进行了分类，这些自动配置类中发红的对象就是没有生效的，比如批处理，导入批处理场景启动器spring-boot-starter-batch部分报红的对象就会恢复正常



## 2.3、底层注解

### 2.3.1、@Configuration配置类

> 原生Spring向IoC容器中添加bean对象的方式:
>
> + 在xml文件中用bean标签向IoC容器添加bean对象(组件)
> + 在类上标注@Component、@Controller、@Service、@Repository注解代表该类是一个组件

+ 配置类示例:

  ```java
  @Configuration(proxyBeanMethods = true)//告诉SpringBoot这是一个配置类，可以取代配置文件的作用，注意:配置类也是IoC容器的一个组件
  //@Configuration(proxyBeanMethods = false)
  public class MyConfig {
      @Bean//配置类中使用@Bean注解标注方法可以给容器注册组件，默认是单实例的，
      // 方法名将作为组件的id，返回类型就是组件类型。返回的值就是组件在容器中的实例
      public User user01(){
          User zhangsan = new User("zhangsan", 18);
          //user组件依赖了Pet组件
          zhangsan.setPet(tomcatPet());
          return zhangsan;
      }
      @Bean("tom")
      public Pet tomcatPet(){
          return new Pet("tomcat");
      }
  }
  ```

+ Configuration配置类测试代码

  ```java
  public static void main(String[] args) {
      ConfigurableApplicationContext run = SpringApplication.run(MainApplication.class, args);
      //从容器中获取组件
      Pet tom01=run.getBean("tom",Pet.class);
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
  }
  ```

1. 使用@Configuration(意为配置)标注类可以告诉SpringBoot这个类是一个配置类，可以取代Spring配置文件的作用，配置文件的功能这个类都有，配置类本身也是IoC容器的组件

2. 在配置类中使用@Bean注解标注组件注册方法，方法返回的对象会被作为组件自动纳入IoC容器的管理

   > 以方法名作为组件的id，返回的类型就是组件类型，返回的值就是组件在容器中的实例
   >
   > 添加@Bean注解的value属性可以手动设置组件的id
   >
   > 默认被注册组件是单实例的

3. @Configuration注解的proxyBeanMethods属性

   + proxyBeanMethods属性为true时(即Full模式，全配置):

     > 此时配置类在容器中是被CGLIB增强的代理对象，使用代理对象调用组件注册方法，SpringBoot总会检查该组件是否已在容器中存在如果有会自动保持组件单实例
     >
     > 实现原理是容器中有自动去容器中找组件

   + proxyBeanMethods属性为false时(即Lite模式，轻量级配置):

     > 此时配置类在容器中是普通对象，调用组件注册方法时，SpringBoot不会保持目标组件的单实例

4. proxyBeanMethods属性可以用来方便的处理组件依赖的场景

   > 如User中有Pet属性，user想获取容器中已经注册的Pet组件，直接在Full模式下调用配置类的pet组件注册方法即可，注意Lite模式下这种方式为user获取的pet属性不是已经在容器中注册的组件

   + proxyBeanMethods属性的最佳实践

     [^Lite模式]: 不会检查组件是否存在于容器中，启动和加载过程会很快，当组件之间不存在依赖关系，总是将proxyBeanMethods属性设置为false
     [^Full模式]: 如果组件存在依赖关系，组件注册方法被调用总会得到已经被注册的单实例组件

   

### 2.3.2、@Import导入组件

@Import注解的作用也是给IoC容器中导入一个组件

1. @Import注解需要标注在配置类或者组件类的类名上

   + 该注解的value属性是一个class数组，可以导入用户或者第三方的类的class对象
   + 作用是将指定类型的组件导入IoC容器，调用对应类型的无参构造创建出对应的组件对象
   + 通过@Import注解导入的组件的默认名字是对应类型的全限定类名

   ~@Import注解使用实例

   ```java
   @Import({User.class, DBHelper.class})
   @Configuration(proxyBeanMethods = false) //告诉SpringBoot这是一个配置类 == 配置文件
   public class MyConfig {
   }
   ```

   ~@Import注解的测试代码

   ```java
   public static void main(String[] args) {
       ConfigurableApplicationContext run = SpringApplication.run(MainApplication.class, args);
       //从容器中根据类型获取该类型对应的所有组件名字
       String[] users = run.getBeanNamesForType(User.class);
       Arrays.stream(users).forEach(user2->{
           System.out.println(user2);
       });
       //从容器中根据类型获取单个组件
       DBHelper bean1 = run.getBean(DBHelper.class);
       System.out.println(bean1);
   }
   ```

2. @Import注解还有高级用法，参考：[08_尚硅谷_组件注册-@Import-给容器中快速导入一个组件](https://www.bilibili.com/video/BV1gW411W7wy/?p=8&vd_source=26f61db0af5e0fa6d06f4623d654b1c3)

### 2.3.3、@Conditional条件装配

@Conditional条件装配注解的作用是：**满足Conditional指定的条件，则对标注组件进行组件注入**

1. @Conditional是一个根注解，其下派生出非常多的派生注解
   + 派生注解可以标注在组件注册方法上，表示条件装配只对该方法对应的组件生效
   + 派生注解也可以标注在配置类或者组件类上，表示条件装配对配置类下的所有组件均有效或对组件类有效

![@Conditional是一个根注解，其下派生出非常多的派生注解](C:/Users/Earl/Desktop/SpringBoot2学习笔记/image/20210205005453173.png)

2. @Conditional的常用派生注解

   + @ConditionalOnBean

     当容器中存在用户指定的组件时才向容器注入指定的组件

   + @ConditionalOnMissingBean

     当容器中没有用户指定的组件时才向容器注入指定的组件，没有指定就表示容器中没有当前组件就配置当前组件，配置了当前组件就不配置了

   + @ConditionalOnClass

     当容器中存在用户指定的类时才向容器注入指定的组件

   + @ConditionalOnMissingClass

     当容器中没有用户指定的类时才向容器注入指定的组件

   + @ConditionalOnResource

     当类路径下存在用户指定的资源时才向容器注入指定的组件

   + @ConditionalOnJava

     当Java是用户指定的版本号时才向容器注入指定的组件

   + @ConditionalOnWebApplication

     当应用是一个web应用时才向容器注入指定的组件

   + @ConditionalOnWebApplication

     当应用不是一个web应用时才向容器注入指定的组件

   + @ConditionalOnSingleCandidate

     当特定组件只有一个实例或者多个实例中有一个主实例时才向容器注入指定的组件

   + @ConditionalOnProperty

     当配置文件中配置了特定属性时才向容器注入指定的组件

3. 以@ConditionalOnBean举例说明@Conditional派生注解的用法

   ~@ConditionalOnBean和@ConditionalOnMissingBean代码实例:

   ```java
   @Configuration(proxyBeanMethods = true)
   //@ConditionalOnBean(name="tom")//标注在类上表示当容器中有组件tom时这个类中所有的注册组件才生效
   @ConditionalOnMissingBean(name="tom")//标注在类上表示当容器中没有组件tom时这个类中所有的注册组件才生效
   public class MyConfig {
       @Bean
       //需求:因为user01依赖于tom，如果容器中没有tom组件就不要在容器中注册user01组件了
       //@ConditionalOnBean(name="tom")
       //@ConditionalOnBean中有很多属性，value、name等，分别表示class对象或者组件id
       //@ConditionalOnBean(name="tom")标注在组件注册方法上时，当容器中有tom时再通过组件注册方法注册user01
       //Q:判断放在什么时候，如果user01组件先注册怎么办?暂时对结果没有影响
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
   ```

   ~测试代码:

   ```java
   public static void main(String[] args) {
       ConfigurableApplicationContext run = SpringApplication.run(MainApplication.class, args);
       boolean tom = run.containsBean("tom");
       System.out.println("容器中Tom组件:"+(tom?"存在":"不存在"));
   
       boolean user01 = run.containsBean("user01");
       System.out.println("容器中user01组件:"+(user01?"存在":"不存在"));
   
       boolean tom22 = run.containsBean("tom22");
       System.out.println("容器中tom22组件:"+(tom22?"存在":"不存在"));
   }
   ```



### 2.3.4、@ImportResource导入Spring配置文件

> 有些公司还在使用spring.xml配置IoC组件，这些xml文件需要使用`BeanFactory applicationContext=new ClassPathXmlApplicationContext("spring.xml");`获取Bean工厂才能创建对应的IoC容器并生成对应的组件,无法直接生效在springboot的IoC容器中生成对应的组件，使用SpringBoot需要对应去添加配置类和@Bean注解来生成组件，很麻烦

1. SpringBoot提供@ImportResource注解配置在随意配置类上导入Spring配置文件，使配置文件中的组件在springboot的IoC容器中生效，无需添加配置类和@Bean注解

   ~beans.xml配置代码

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <beans xmlns="http://www.springframework.org/schema/beans"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
       <bean id="user01" class="com.atlisheng.boot.bean.User">
           <property name="name" value="zhangsan"/>
           <property name="age" value="18"/>
       </bean>
       <bean id="cat" class="com.atlisheng.boot.bean.Pet">
           <property name="name" value="tomcat"/>
       </bean>
   </beans>
   ```

   ~@ImportResource注解用法实例

   ```java
   @Configuration
   @ImportResource("classpath:beans.xml")
   public class MyConfig {
       ...
   }
   ```

   ~测试代码

   ```java
   public static void main(String[] args) {
       ConfigurableApplicationContext run = SpringApplication.run(MainApplication.class, args);
       boolean haha = run.containsBean("haha");
       boolean hehe = run.containsBean("hehe");
       System.out.println("haha:"+(haha?"存在":"不存在"));
       System.out.println("hehe:"+(hehe?"存在":"不存在"));
   }
   ```

   

### 2.3.5、@ConfigurationProperties配置绑定

> 配置绑定就是使用Java读取配置文件中的内容，并将数据封装到JavaBean中，如数据库连接信息封装到数据源中，对于配置项上百行的配置文件有时需要使用正则表达式来进行匹配寻找，非常麻烦

~传统方式代码

```java
public class getProperties {
     public static void main(String[] args) throws FileNotFoundException, IOException {
         Properties pps = new Properties();
         pps.load(new FileInputStream("a.properties"));
         Enumeration enum1 = pps.propertyNames();//得到配置文件的名字
         while(enum1.hasMoreElements()) {
             String strKey = (String) enum1.nextElement();
             String strValue = pps.getProperty(strKey);
             System.out.println(strKey + "=" + strValue);
             //封装到JavaBean。
         }
     }
 }
```

**springboot提供了两种配置绑定的方案**

1. 方案一：@ConfigurationProperties + @Component

   + 步骤一：在配置文件application.properties中对目标类Car依据属性进行了如下配置

     ```properties
     mycar.brand=BYD
     mycar.price=100000
     ```

   + 步骤二：确认目标类Car被纳入了IoC容器管理，只有被纳入IoC容器管理的组件才能享受类似配置绑定等其他spring强大功能

   + 步骤三：在目标类上使用@ConfigurationProperties(prefix = "mycar")注解通知springboot自动根据前缀mycar查找application.properties对应含有前缀的key如mycar.brand和mycar.price,并将值通过目标类的set注入注入属性值，注意value属性和prefix属性互为别名，使用哪一个都可以

     ~配置绑定代码

     ```java
     @Component
     @ConfigurationProperties(prefix = "mycar")
     @ConfigurationProperties(prefix = "mycar")
     public class Car {
         private String brand;
         private Integer price;
     	...这种方式必须写set方法...
     }
     ```

2. 方案二：@EnableConfigurationProperties + @ConfigurationProperties

   [^注意]: 方案二必须在配置类上使用@EnableConfigurationProperties注解对配置类进行标注，此时无需将配置绑定目标类使用@Component注解纳入IoC容器的管理，因为很多时候需要引用第三方的类进行配置绑定，这些类没有注解来向容器注册组件

   + 步骤一：在配置文件application.properties中对目标类Car进行属性配置

   + 步骤二：在配置类上使用@EnableConfigurationProperties注解对配置类进行标注，注解的value属性为目标类的class对象，这一步的目的:

     + 根据class对象开启目标类的配置绑定功能

     + 把目标类这个组件自动注册到容器中

       ```java
       @EnableConfigurationProperties(Car.class)
       public class MyConfig {
       ...
       }
       ```

   + 步骤三：在目标类上使用@ConfigurationProperties注解指定前缀属性prefix进行配置绑定

     ```java
     @ConfigurationProperties(prefix = "mycar")
     public class Car {
     ...
     }
     ```

     [^？Ques]: 这种配置绑定的中文响应到浏览器有乱码怎么解决，注意此时直接通过@ResponseBody响应到浏览器的中文没有乱码，是正常的

     

## 2.4、自动配置源码分析

### 2.4.1、自动包规则的原理

#### @SpringBootApplication

1. 核心注解@SpringBootApplication是一个复合注解，用来标注主程序类，该注解相当于@ComponentScan、@SpringBootConfiguration、@EnableAutoConfiguration三个注解的合成注解，从这三个注解的功能就能反应@SpringBootApplication的核心功能

   ```java
   @Target(ElementType.TYPE)
   @Retention(RetentionPolicy.RUNTIME)
   @Documented
   @Inherited
   @SpringBootConfiguration
   @EnableAutoConfiguration
   @ComponentScan(excludeFilters = { 
       @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
       @Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })
   public @interface SpringBootApplication {
   ...
   }
   ```

#### @SpringBootConfiguration

1. @Configuration注解是@SpringBootConfiguration元注解，表明@SpringBootConfiguration标注的类也是一个配置类，即主程序类是一个核心配置类

   ```java
   @Target(ElementType.TYPE)
   @Retention(RetentionPolicy.RUNTIME)
   @Documented
   @Configuration
   public @interface SpringBootConfiguration {
   	@AliasFor(annotation = Configuration.class)
   	boolean proxyBeanMethods() default true;
   }
   ```

#### @ComponentScan

1. 包扫描注解，指定要扫描哪些包，包扫描注解@ComponentScan有两个SpringBoot自定义的扫描器

   > 雷丰阳的spring注解视频中有介绍：[尚硅谷Spring注解驱动教程(雷丰阳源码级讲解)](https://www.bilibili.com/video/BV1gW411W7wy/?spm_id_from=333.337.search-card.all.click&vd_source=26f61db0af5e0fa6d06f4623d654b1c3)

#### @EnableAutoConfiguration

1. @EnableAutoConfiguration也是一个合成注解，由注解@AutoConfigurationPackage以及组件导入@Import(AutoConfigurationImportSelector.class)构成

   ```java
   @Target(ElementType.TYPE)
   @Retention(RetentionPolicy.RUNTIME)
   @Documented
   @Inherited
   @AutoConfigurationPackage
   @Import(AutoConfigurationImportSelector.class)
   public @interface EnableAutoConfiguration {
       String ENABLED_OVERRIDE_PROPERTY = "spring.boot.enableautoconfiguration";
       Class<?>[] exclude() default {};
       String[] excludeName() default {};
   }
   ```

2. **@AutoConfigurationPackage**

   > @AutoConfigurationPackage注解的作用就是实现了默认包扫描范围为主程序类所在包及其所有子包

   + @AutoConfigurationPackage意为自动配置包，由其源码：@Import(AutoConfigurationPackages. Registrar.class)，可知该注解就是给容器导入Registrar组件，通过Registrar组件的registerBeanDefinitions方法给容器导入一系列组件

     ```java
     @Target(ElementType.TYPE)
     @Retention(RetentionPolicy.RUNTIME)
     @Documented
     @Inherited
     @Import(AutoConfigurationPackages.Registrar.class)//给容器中导入一个组件
     public @interface AutoConfigurationPackage {
         String[] basePackages() default {};
         Class<?>[] basePackageClasses() default {};
     }
     ```

   + Registrar类的registerBeanDefinitions方法分析

     ```java
     static class Registrar implements ImportBeanDefinitionRegistrar, DeterminableImports {
         @Override
         public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
             register(registry, new PackageImports(metadata).getPackageNames().toArray(new String[0]));
         }
         @Override
         public Set<Object> determineImports(AnnotationMetadata metadata) {
             return Collections.singleton(new PackageImports(metadata));
         }
     }
     ```

     > + 通过Registrar组件的registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegister)方法执行批量导入组件的操作，这些组件即主程序类MainApplication所在包及其所有子包下的组件
     >
     > + AnnotationMetadata metadata是注解源信息，该注解指的是@AutoConfigurationPackage，注解的源信息包括了该注解的标注位置MainApplication的全限定类名以及该注解有哪些属性值
     >
     >   ***[因为@AutoConfigurationPackage合成了注解@EnableAutoConfiguration，最终相当于@AutoConfigurationPackage注解标注在主程序类MainApplication上，在registerBeanDefinitions方法中new PackageImports(metadata).getpackageName(). toArray(new Array[0])通过元注解信息获取到组件所在的包名，即主程序类所在的包名，封装在数组中然后使用register方法进行注册，这就是默认包扫描范围在主程序类MainApplication所在包及其所有子包下的原因]***

### 2.4.2、初始加载自动配置类

3. **@Import(AutoConfigurationImportSelector.class)**

   + @Import(AutoConfigurationImportSelector.class)注解是@EnableAutoConfiguration注解的组分之一

   + 利用selector机制引入AutoConfigurationImportSelector组件给容器批量导入组件

     + AutoConfigurationImportSelector中的selectImports方法返回的字符串数组中规定了要批量导入组件的列表，该列表是调用getAutoConfigurationEntry(annotationMetadata)获取的；重点就是方法：getAutoConfigurationEntry(annotationMetadata)

       ```java
       public class AutoConfigurationImportSelector implements DeferredImportSelector, BeanClassLoaderAware,ResourceLoaderAware, BeanFactoryAware, EnvironmentAware, Ordered {
           ...
       	@Override
       	public String[] selectImports(AnnotationMetadata annotationMetadata) {
       		if (!isEnabled(annotationMetadata)) {
       			return NO_IMPORTS;
       		}
       		AutoConfigurationEntry autoConfigurationEntry = getAutoConfigurationEntry(annotationMetadata);
       		return StringUtils.toStringArray(autoConfigurationEntry.getConfigurations());
       	}
           ...
       }
       ```

     + getAutoConfigurationEntry(annotationMetadata)方法解析:

       + 该方法获取所有需要自动配置的集合

       + getCandidateConfigurations(annotationMetadata, attributes)方法，作用是获取所有备选的配置，返回configurations；

       + 接下来依次对configurations移除重复的选项，排除一些配置选项，以及一些额外操作封装成AutoConfigurationEntry进行返回，在没有重复项和需排除项的情况下configurations中一共有127个组件，这127个组件默认需要导入容器，现在的重点变成了获取备选配置的方法getCandidateConfigurations(annotationMetadata, attributes)

         ```java
         protected AutoConfigurationEntry getAutoConfigurationEntry(AnnotationMetadata annotationMetadata) {
             if (!isEnabled(annotationMetadata)) {
                 return EMPTY_ENTRY;
             }
             AnnotationAttributes attributes = getAttributes(annotationMetadata);
             List<String> configurations = getCandidateConfigurations(annotationMetadata, attributes);
             configurations = removeDuplicates(configurations);
             Set<String> exclusions = getExclusions(annotationMetadata, attributes);
             checkExcludedClasses(configurations, exclusions);
             configurations.removeAll(exclusions);
             configurations = getConfigurationClassFilter().filter(configurations);
             fireAutoConfigurationImportEvents(configurations, exclusions);
             return new AutoConfigurationEntry(configurations, exclusions);
         }
         ```

       + getCandidateConfigurations(annotationMetadata, attributes)方法解析:

         + SpringFactoriesLoader.loadFactoryNames( getSpringFactoriesLoaderFactoryClass(), getBeanClassLoader())方法使用Spring工厂加载器加载一些资源，重点是loadFactoryNames方法

           ```java
           protected List<String> getCandidateConfigurations(AnnotationMetadata metadata, AnnotationAttributes attributes) {
               List<String> configurations = SpringFactoriesLoader.loadFactoryNames( getSpringFactoriesLoaderFactoryClass(),getBeanClassLoader());
               Assert.notEmpty(configurations,"No auto configuration classes found in META-INF/spring.factories. If you "+"are using a custom packaging, make sure that file is correct.");
               return configurations;
           }
           ```

         + loadFactoryNames方法中的重点是loadSpringFactories(classLoader)方法，利用loadSpringFactories方法加载得到一个Map集合，Map集合的value是一个List集合；这个Map集合中保存的就是所有的组件，只要看懂loadSpringFactories方法就能知道从哪里获取的所有组件

           ```java
           public final class SpringFactoriesLoader {
               ...
               public static List<String> loadFactoryNames(Class<?> factoryType, @Nullable ClassLoader classLoader) {
                   String factoryTypeName = factoryType.getName();
                   return (List)loadSpringFactories(classLoader).getOrDefault( factoryTypeName, Collections.emptyList());
               }
           }
           ```

       + loadSpringFactories(classLoader)方法解析

         + classLoader.getResources("META-INF/spring.factories")：从META-INF/spring.factories位置加载一个文件，默认扫描当前系统中所有META-INF/spring.factories位置的文件，在引入的第三方jar包中，有些jar包有META-INF/spring.factories这个文件，如spring-boot、spring-boot-autoconfigure(最核心的)；有些包则没有这个文件

           > 在最核心的jar包spring-boot-autoconfigure-2.3.4.RELEASE.jar中的META-INF/spring.factories中第21行开始配置了@EnableAutoConfiguration注解由@Import(AutoConfigurationImportSelector.class)引入的全部127个自动配置组件，即该文件这127行写死了spring-boot一启动就要给容器加载的所有配置类，这些类也都在spring-boot-autoconfigure这个jar包下

         + 虽然127个场景的所有自动配置启动的时候默认全部加载，但是最终会按需分配，通过IoC容器的组件数量只有135个，包括我们自己配置的组件和其他组件，显然127个组件并未全部生效

         ```java
         private static Map<String, List<String>> loadSpringFactories(@Nullable ClassLoader classLoader) {
             MultiValueMap<String, String> result = (MultiValueMap)cache.get(classLoader);
             if (result != null) {
                 return result;
             } else {
                 try {
                     Enumeration<URL> urls = classLoader != null ? classLoader.getResources("META-INF/spring.factories") : ClassLoader.getSystemResources("META-INF/spring.factories");
                     LinkedMultiValueMap result = new LinkedMultiValueMap();
                     while(urls.hasMoreElements()) {
                         URL url = (URL)urls.nextElement();
                         UrlResource resource = new UrlResource(url);
                         Properties properties = PropertiesLoaderUtils.loadProperties(resource);
                         Iterator var6 = properties.entrySet().iterator();
                         while(var6.hasNext()) {
                             Entry<?, ?> entry = (Entry)var6.next();
                             String factoryTypeName = ((String)entry.getKey()).trim();
                             String[] var9 = StringUtils.commaDelimitedListToStringArray((String)entry.getValue());
                             int var10 = var9.length;
                             for(int var11 = 0; var11 < var10; ++var11) {
                                 String factoryImplementationName = var9[var11];
                                 result.add(factoryTypeName, factoryImplementationName.trim());
                             }
                         }
                     }
                     cache.put(classLoader, result);
                     return result;
                 } catch (IOException var13) {
                     throw new IllegalArgumentException("Unable to load factories from location [META-INF/spring.factories]", var13);
                 }
             }
         }
         ```

   + 组件按需配置机制

     + 在对应127个组件的自动配置类中都有派生条件装配注解，如:

       + AopAutoConfiguration有@ConditionalOnClass(Advice.class),意思是当类路径中存在Advice这个类这个组件才会生效，也即只有导入了aop相关的包aspectj，对应的组件才会生效

       + BatchAutoConfiguration有@ConditionalOnClass(JobLauncher.class，DataSource.class)，也只有导入批处理的包，这个类下的组件才会生效

         ```java
         @Configuration(proxyBeanMethods = false)
         @ConditionalOnClass({ JobLauncher.class, DataSource.class })
         @AutoConfigureAfter(HibernateJpaAutoConfiguration.class)
         @ConditionalOnBean(JobLauncher.class)
         @EnableConfigurationProperties(BatchProperties.class)
         @Import(BatchConfigurerConfiguration.class)
         public class BatchAutoConfiguration {
             ...
         }
         ```

       + 总结，派生条件装配注解的value发红对应组件就不会生效

     + 启动全部加载，最终按照条件装配规则按需配置

### 2.4.3、自动配置流程

1. 以AopAutoconfiguration分析自动配置功能(经确认AopAutoconfiguration确实在127个中)

   ```java
   @Configuration(proxyBeanMethods = false)//表明这是一个配置类
   @ConditionalOnProperty(prefix = "spring.aop", name = "auto", havingValue = "true", matchIfMissing = true)//判断配置文件中是否有spring.aop.auto=true，有就生效,matchIfMissing = true意思是没有以上配置默认就是上述配置，满足上述条件允许注册AopAutoConfiguration类中的组件
   public class AopAutoConfiguration {
       
       //AopAutoConfiguration中两个类之一AspectJAutoProxyingConfiguration
   	@Configuration(proxyBeanMethods = false)//配置类，非单实例
   	@ConditionalOnClass(Advice.class)//判断整个应用中是否存在Advice这个类，Advice属于aspectj包下的Advice，由于没有导入aop场景，没有导入Aspectj的相关依赖，所以AspectJAutoProxyingConfiguration自动配置类下的所有组件都不生效
   	static class AspectJAutoProxyingConfiguration {
   		@Configuration(proxyBeanMethods = false)
   		@EnableAspectJAutoProxy(proxyTargetClass = false)
   		@ConditionalOnProperty(prefix = "spring.aop", name = "proxy-target-class", havingValue = "false",
   				matchIfMissing = false)
   		static class JdkDynamicAutoProxyConfiguration {
   
   		}
   		@Configuration(proxyBeanMethods = false)
   		@EnableAspectJAutoProxy(proxyTargetClass = true)
   		@ConditionalOnProperty(prefix = "spring.aop", name = "proxy-target-class", havingValue = "true",
   				matchIfMissing = true)
   		static class CglibAutoProxyConfiguration {
   		}
   	}
       
       //AopAutoConfiguration中的另一个类ClassProxyingConfiguration
   	@Configuration(proxyBeanMethods = false)
   	@ConditionalOnMissingClass("org.aspectj.weaver.Advice")//应用中没有Advice这个类就加载该类的所有组件，正好和上面的互斥
   	@ConditionalOnProperty(prefix = "spring.aop", name = "proxy-target-class", havingValue = "true",matchIfMissing = true)//是否配置了spring.aop.proxy-target-class = true,没有配默认就是true，所以下列组件生效了
   	static class ClassProxyingConfiguration {
           //以下是开启简单的AOP功能，在spring注解哪个课的aop原理有介绍
   		ClassProxyingConfiguration(BeanFactory beanFactory) {
   			if (beanFactory instanceof BeanDefinitionRegistry) {
   				BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
   				AopConfigUtils.registerAutoProxyCreatorIfNecessary(registry);
   				AopConfigUtils.forceAutoProxyCreatorToUseClassProxying(registry);
   			}
   		}
   	}
   }
   ```

2. 以CacheAutoconfiguration分析自动配置功能(经确认CacheAutoconfiguration确实在127个中)

   ```java
   @Configuration(proxyBeanMethods = false)
   @ConditionalOnClass(CacheManager.class)//判断应用中有CacheManager缓存管理器这个类存在，该类在spring-context即spring的核心包下，是有的
   @ConditionalOnBean(CacheAspectSupport.class)//判断应用中是否有CacheAspectSupport这种类型的组件，经过查询容器中相关类型组件的数量，发现是0，说明容器中没有配置缓存支持的组件，以下所有配置全部失效，相当于整个缓存的所有配置都没有生效
   @ConditionalOnMissingBean(value = CacheManager.class, name = "cacheResolver")
   @EnableConfigurationProperties(CacheProperties.class)
   @AutoConfigureAfter({ CouchbaseDataAutoConfiguration.class, 		    HazelcastAutoConfiguration.class,HibernateJpaAutoConfiguration.class, RedisAutoConfiguration.class })
   @Import({ CacheConfigurationImportSelector.class, CacheManagerEntityManagerFactoryDependsOnPostProcessor.class })
   public class CacheAutoConfiguration {
   	...
   }
   ```

3. 以DispatcherServletAutoconfiguration分析自动配置功能(经确认确实在127个中)

   > web下的servlet包下有很多自动配置类，不止DispatcherServletAutoconfiguration

   ```java
   @AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)//先不管，是说当前类的生效顺序，相当于指定当前类生效的优先级
   @Configuration(proxyBeanMethods = false)
   @ConditionalOnWebApplication(type = Type.SERVLET)//判断是否原生servlet的web应用，因为springboot2支持两种方式的web开发，一种响应式编程，一种原生servlet技术栈，当前是原生web开发
   @ConditionalOnClass(DispatcherServlet.class)//当前应用有没有DispatcherServlet这个类，导了springMVC就肯定有这个类，有所以生效
   @AutoConfigureAfter(ServletWebServerFactoryAutoConfiguration.class)//先不管，在括号内的自动配置类配置完后再来配置当前类，即想要配置当前类必须先配好web服务器先配好
   public class DispatcherServletAutoConfiguration {
   
   	public static final String DEFAULT_DISPATCHER_SERVLET_BEAN_NAME = "dispatcherServlet";
   
   	public static final String DEFAULT_DISPATCHER_SERVLET_REGISTRATION_BEAN_NAME = "dispatcherServletRegistration";
   	
       //总类配置有效才有必要继续看DispatcherServletConfiguration
   	@Configuration(proxyBeanMethods = false)
   	@Conditional(DefaultDispatcherServletCondition.class)//有该条件才能生效，为什么生效后面再说
   	@ConditionalOnClass(ServletRegistration.class)//应用中有没有ServletRegistration类型的组件，该组件导入tomcat核心包就有，所以有效
   	@EnableConfigurationProperties(WebMvcProperties.class)//开启WebMvcProperties的配置绑定功能，把WebMvcProperties类的组件放在IoC容器中，经过测试，确实有一个
   	protected static class DispatcherServletConfiguration {
           
           @Bean(name = DEFAULT_DISPATCHER_SERVLET_BEAN_NAME)//成功注册组件dispatcherServlet
           public DispatcherServlet dispatcherServlet(WebMvcProperties webMvcProperties) {
               DispatcherServlet dispatcherServlet = new DispatcherServlet();
   			
               //这里为配置属性的代码
               ...
               
               return dispatcherServlet;
           }
   
           @Bean
           @ConditionalOnBean(MultipartResolver.class)//容器中如果有MultipartResolver类型的组件就生效，利用自己的一段代码判断生效的，在spring注解版中会讲
           @ConditionalOnMissingBean(name = DispatcherServlet.MULTIPART_RESOLVER_BEAN_NAME)//容器中没有名为multipartResolver的组件就生效，结合起来就是组件中有MultipartResolver类型的组件但是名字不为multipartResolver
           public MultipartResolver multipartResolver(MultipartResolver resolver) {
               //spring给@Bean标注的方法传入对象参数，这个参数会自动从容器中找并自动装配
               //很多用户不知道springMVC的底层原理，没有为MultipartResolver组件取名multipartResolver，spring也可以通过这个方法找到并返回给用户相应的名字不为multipartResolver的MultipartResolver组件，防止有些用户配置的文件上传解析器不符合规范，一返回到容器中，该组件的名字默认就变成了方法名multipartResolver，就符合规范了
               return resolver;
           }
   
   	}
   	...
   }
   ```

4. HttpEncodingAutoconfiguration分析自动配置功能(经确认确实在127个中)

   > 请求参数和响应参数不乱码的原因就在于HttpEncodingAutoConfiguration

   ```java
   @Configuration(proxyBeanMethods = false)
   @EnableConfigurationProperties(ServerProperties.class)//开启了ServerProperties类的配置绑定
   @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)//判断是否为原生的servlet应用
   @ConditionalOnClass(CharacterEncodingFilter.class)//判断应用中是否有CharacterEncodingFilter这个组件，只要导入springMVC的jar包就肯定有这个filter
   @ConditionalOnProperty(prefix = "server.servlet.encoding", value = "enabled", matchIfMissing = true)//判断server.servlet.encoding=enabled是否成立，没有配置也默认是enabled，故以上判断均生效
   public class HttpEncodingAutoConfiguration {
   
   	private final Encoding properties;
   
   	public HttpEncodingAutoConfiguration(ServerProperties properties) {
   		this.properties = properties.getServlet().getEncoding();
   	}
   
   	@Bean//这个characterEncodingFilter是springMVC解决编码问题的过滤器，请求和响应都可以设置
   	@ConditionalOnMissingBean//不写属性表示当前组件容器中没有就生效，有就不生效，这里也显示出springBoot的设计思想，SpringBoot默认在底层配置好所有的组件，但是如果用户配置了就以用户的优先
   	public CharacterEncodingFilter characterEncodingFilter() {
   		CharacterEncodingFilter filter = new OrderedCharacterEncodingFilter();
   		filter.setEncoding(this.properties.getCharset().name());
   		filter.setForceRequestEncoding(this.properties.shouldForce(Encoding.Type.REQUEST));
   		filter.setForceResponseEncoding(this.properties.shouldForce(Encoding.Type.RESPONSE));
   		return filter;
   	}
   	...
   }
   ```

#### 总结

1. SpringBoot先加载所有的自动配置类  xxxxxAutoConfiguration
2. 每个自动配置类按照条件进行生效，默认都会绑定配置文件指定的值。（xxxxProperties里面读取，xxxProperties和配置文件进行了绑定，在xxxxxAutoConfiguration中会大量看到@EnableConfigurationProperties(XxxxProperties.class)注解，而配置文件中各种配置代表什么意思官方文档中的Application Properties都可以查到，学好技术改配置比如使用redis就一句话的事）
3. 生效的配置类会给容器中装配很多组件
4. 只要容器中有这些组件，相当于这些功能就有了
5. 只要用户有自己配置的，就以用户的优先，即定制化配置:
   + 办法一：用户直接自己用@Bean替换底层的组件
   + 办法二：用户查看目标组件获取的配置文件的对应值去属性配置文件自己修改即可，比如上述例子中的字符编码格式:
     + 点开对应的CharacterEncodingFilter类查看@ConditionalOnProperty注解的前缀prefix="server.servlet.encoding" 
     + 在HttpEncodingAutoConfiguration中观察到通过this.properties.getCharset()方法获取属性值
     + 即属性配置文件的key对应为server.servlet.encoding.charset=GBK
6. 自动配置原理全流程：
   + **xxxxxAutoConfiguration加载自动配置类 ---> 根据条件注册组件 ---> 组件从xxxxProperties里面拿值  ----> xxxxProperties绑定的就是application.properties里面的值**



## 2.5、最佳实践

### 2.5.1、SpringBoot开发技巧

1. 引入场景依赖
   + 比如开发缓存、消息队列等首先看SpringBoot或者第三方有没有开发相关的场景依赖，第三方技术提供的starter和Spring官方提供的starter列表：[官方文档](https://docs.spring.io/spring-boot/docs/current/reference/html/using-spring-boot.html#using-boot-starter)(Using Spring Boot--->Starters)

2. 查看自动配置了哪些组件（选做，因为比较偏底层原理，关心源码比较有用）
   - 自己分析，引入场景对应的自动配置一般都会生效，但是逐行分析比较麻烦
   - 配置文件中添加debug=true开启自动配置报告，会在控制台输出哪些配置类生效，哪些未生效，未生效会提示Did not match：不生效的原因
     - 控制台的Negative matches下展示未生效的组件列表
     - 控制台的Positive matches下展示生效的组件列表

3. 修改配置项

   + 参考官方文档的Application Properties：[官方文档](https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-application-properties.html#common-application-properties)

   + 自己到XxxxProperties查看绑定了配置文件的哪些配置然后自己分析更改

     ~示例修改spring.banner；banner是springboot启动时的图标，可以使用本地的图片替换，可以通过指定spring.banner.image.location的值，默认值是找classpath:banner.gif或者jpg或png；可以直接把图片的名字改成banner，也可以指定spring.banner.image.location=图片名.jpg;

   + 自己添加或者替换组件
     + 通过@Bean、@Component...，因为用户有的以用户优先
     
   + 自定义器  XXXXXCustomizer
     + 以后会讲
     
   + ...

### 2.5.2、Lombok简化开发

1. 使用Lombok的步骤

   + 步骤一：添加Lomback依赖

   + 步骤二：IDEA中File->Settings->Plugins，搜索安装Lombok插件

   + 步骤三：使用Lombok

     + 通过注解@Data标注类在编译阶段生成setter/getter方法
     + 通过注解@ToString标注类在编译阶段生成toString方法
     + 通过注解@AllArgsConstructor标注类在编译阶段生成所有属性的有参构造器
     + 通过注解@NoArgsConstructor标注类在编译阶段生成无参构造器
     + 通过注解@EqualsAndHashCode标注类在编译阶段生成Equals和HashCode方法
     + 通过注解@Slf4j标注类自动给类添加log属性用来记录日志以简化日志开发

     [^注意1]: 部分属性的有参构造还是需要自己写
     [^注意2]: IDEA2021与Lombok1.18.12版本不兼容，需要使用1.18.20及以上

2. SpringBoot2默认管理的lombok版本1.18.12，需要用户自己引入依赖，Lombok的作用是在程序编译阶段自动生成程序的setter/getter方法、toString方法、构造方法、Equals和HashCode方法

   ~Lombok依赖

   ```xml
   <dependency>     
       <groupId>org.projectlombok</groupId>     
       <artifactId>lombok</artifactId>
   </dependency>
   ```

3. 用法实例

   ```java
   @Slf4j
   @Data
   @ToString
   @NoArgsConstructor
   @AllArgsConstructor
   @EqualsAndHashCode
   public class User {
       private String name;
       private Integer age;
       private Pet pet;
   
       public User(String name, Integer age) {
       	log.info("有参构造方法执行了!");
           this.name = name;
           this.age = age;
       }
   }
   ```



### 2.5.3、spring-boot-devtools

1. 使用spring-boot-devtools的步骤

   + 步骤一：官方文档-->Using Spring Boot-->Developer Tools-->拷贝依赖信息

     ~dev-tools依赖

     ```xml
     <dependencies>
         <dependency>
             <groupId>org.springframework.boot</groupId>
             <artifactId>spring-boot-devtools</artifactId>
             <optional>true</optional>
         </dependency>
     </dependencies>
     ```

2. spring-boot-devtools的作用是热更新，修改代码或者修改web页面后只需要按Ctrl+F9(作用是让项目重新编译一遍，编译完毕devtools就能自动帮助用户重新加载)，页面就能实时生效；不需要再重启服务器。devtools的实质是自动重启服务器
   + 注意：如果没有修改任何资源，ctrl+f9不会生效
3. 需要使用真正的热更新Reload需要使用付费的插件JRebel



### 2.5.4、Spring Initailizr

1. Spring Initailizr的使用
   + [Spring Initailizr](https://start.spring.io/)是创建Spring Boot项目初始化向导，创建工程的时候不选择maven或者空工程，直接创建Spring Initailizr工程

2. Spring Initailizr的作用

   + 在工程创建过程中就可以勾选想要引入的场景，想要使用的数据库，Mybatis，SpringBoot的版本等等

   + 创建工程后会自动生成pom.xml的场景启动器相关配置并且引入junit和打包插件

   + 自动创建全局配置文件、static目录(写所有的静态资源，即CSS、JS文件)、templates目录(写所有的视图页面)

   + 自动创建主程序类(以工程名+Application构成)

     [^注意1]: 以后创建SpringBoot应用都会使用Spring Initailizr
     [^注意2]: 引入了数据库的场景启动器就必须配置数据源相关信息，否则启动应用会直接报错

     

# 3、配置文件

## 3.1、yaml配置文件格式

> SpringBoot兼容两种配置文件格式，properties文件和yaml，注意yaml的后缀可以是.yaml，也可以是.yml;SpringBoot中需要起名application.yml,如果两种类型的文件都有，两个文件都会生效，且同名配置以properties文件优先加载
>
> YAML 是 "YAML Ain't Markup Language"（YAML 不是一种标记语言）的递归缩写。在开发的这种语言时，YAML 的意思其实是："Yet Another Markup Language"（仍是一种标记语言），标记语言表示文件是用标签写的
>
> **YAML非常适合用来做以数据为中心的配置文件，即存储配置数据而不是定义一些行为动作，spring的配置首选yaml，优点就是能够清晰的看见属性配置的从属关系**



### 3.1.1、基本语法

1. 配置数据的格式
   + key: value；[***所有冒号和value之间有空格，且key和value严格区分大小写***]

2. 使用缩进表示层级关系

   [***XMl通过标签表示层级关系，而YAML通过缩进表示层级关系，缩进一般不允许使用tab，只允许使用空格，但是IDEA中可以使用tab空格数不重要，但是要保证相同层级的元素要对齐，不同层级的空格数可以不一样***]

 	3. '#'表示注释
 	4. 字符串无需加引号，默认行为与加单引号相同，可以按需加单双引号
     + **单引号会转义字符串中的'转义字符'，双引号不会转义字符串中的'转义字符'**



### 3.1.2、数据类型

1. 字面量：字面量是单个的、不可再分的值。对应的java类型有date、boolean、string、number、null

   ```yaml
   key: value
   ```

2. 对象：表示对象有两种写法，对应的java对象有map、hash、set[***set集合不是只有value吗，不应该在数组的表示方法中吗***]、object 

   ```yaml
   #行内写法： 注意大括号中的冒号要有空格，不然展示的是字符串
   k: {k1:v1,k2:v2,k3:v3}
   
   #或
   
   #对象表示法,注意表示的对象如果作为数组或集合的元素可以用'-'代替'k:'
   k: 
     k1: v1
     k2: v2
     k3: v3
   ```

3. 数组以及以数组为基础的集合：一组按次序排列的值。对应的java对象array、list、queue

   ```yaml
   #行内写法：  
   k: [v1,v2,v3]
   
   #或者
   
   #普通写法:一个'-'代表一个元素，'-'和值之间用空格隔开
   k:
    - v1
    - v2
    - v3
   ```

   

### 3.1.3、yaml实例

1. 目标类

   ```java
   @ConfigurationProperties(prefix = "person")
   @Component
   @ToString
   @Data
   public class Person {
       private String userName;
       private Boolean boss;
       private Date birth;
       private Integer age;
       private Pet pet;
       private String[] interests;
       private List<String> animal;
       private Map<String, Object> score;
       private Set<Double> salarys;
       private Map<String, List<Pet>> allPets;
   }
   
   @ToString
   @Data
   public class Pet {
       private String name;
       private Double weight;
   }
   ```

2. application.yml对应配置

   ```yaml
   person:
     userName: "zhangsan \n 李四"
     #单引号会将其中的特殊字符转换成普通字符，与不加单引号双引号的默认行为相同
     #双引号不会将其中的特殊字符转换成普通字符，如"zhangsan \n 李四"整体作为字符串输出到控制台，换行符仍然生效
     #即单引号会转义字符串中的'转义字符'，双引号不会转义字符串中的'转义字符'
     boss: true
     birth: 2019/12/9
     age: 19
     #interests: [抽烟,喝酒,烫头]
     interests:
       - 抽烟
       - 喝酒
       - 烫头
     animal: [阿猫,阿狗]
     #score:
       #English: 80
       #Math: 90
     score: {english: 80,math: 90}
     salarys:
       - 9999.98
       - 9999.99
     pet:
       name: 阿狗
       weight: 99.99
     allPets:
       sick:
         - {name: 阿狗,weight: 99.99}
         - name: 阿猫
           weight: 88.88
         - name: 阿虫
           weight: 77.77
       health:
         - {name: 阿花,weight: 199.99}
         - {name: 阿明,weight: 199.99}
   ```




### 3.1.4、配置注解处理器

> 注解处理器的作用是针对用户自定义类添加了@ConfigurationProperties注解后在属性配置文件中提供提示功能

[^注意1]: 用户自己的类进行配置绑定添加了@ConfigurationProperties(prefix = "person")注解后，springBoot会提示配置注解处理器没有配置，这意味着自定义类在配置文件中没有属性名提示功能，处理办法参见官方文档的附录--->Configuration Metadata--->Configuration the Annotation Processer（拷贝spring-boot-configuration-processor依赖到pom.xml，需要重新启动一下应用把资源加载一下）

~引入Configuration the Annotation Processer依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>
```

[^注意2]: 点击提示自动会把属性名添加到最后一行，属性名单词间用'-'连接，并且全部变成小写
[^注意3]: 添加依赖Configuration the Annotation Processer只是为了开发方便，需要在打包插件中用exclude标签配置打包时不要将Configuration the Annotation Processer一并进行打包

~排除Configuration the Annotation Processer依赖的插件打包行为

```xml
<!-- 下面插件作用是工程打包时，不将spring-boot-configuration-processor打进包内，让其只在编码的时候有用 -->
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <excludes>
                    <exclude>
                        <groupId>org.springframework.boot</groupId>
                        <artifactId>spring-boot-configuration-processor</artifactId>
                    </exclude>
                </excludes>
            </configuration>
        </plugin>
    </plugins>
</build>
```



# 4、SpringBoot-Web开发

Web开发的官方文档位于Spring Boot Features--->Developing Web Application

## 4.1、Web开发简介

### 4.1.1、 SpringMVC自动配置概览

1. SpringBoot-Web开发为SpringMVC自动配置的组件列表

   [***没有说全，只说了大部分内容***]

   + Inclusion of `ContentNegotiatingViewResolver` and `BeanNameViewResolver` beans.

     - 内容协商视图解析器和BeanName视图解析器

   + Support for serving static resources, including support for WebJars (covered [later in this document](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-spring-mvc-static-content))).

     - 静态资源的访问组件（包括webjars）

       [^注意]: 【***原生的SpringMVC需要配置的是`<mvc:default-servlet-handler/>`***】

   + Automatic registration of `Converter`, `GenericConverter`, and `Formatter` beans.

     - 自动注册所有的转换器和格式化器【用来类型转换的转换器】

   + Support for `HttpMessageConverters` (covered [later in this document](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-spring-mvc-message-converters)).

     - 支持 `HttpMessageConverters` 

       【***后来配合内容协商理解原理***】

   + Automatic registration of `MessageCodesResolver` (covered [later in this document](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-spring-message-codes)).

     - 自动注册 `MessageCodesResolver` 

       【***国际化用，一般用不到，因为一般针对国内外用户做两套网站，因为语言和文化的区别***】

   + Static `index.html` support.

     - 静态index.html 页支持

       【***将欢迎页放到指定位置会自动支持欢迎页机制***】

   + Custom `Favicon` support (covered [later in this document](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-spring-mvc-favicon)).

     - 自定义 `Favicon`  

   + Automatic use of a `ConfigurableWebBindingInitializer` bean (covered [later in this document](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-spring-mvc-web-binding-initializer)).

     - 自动使用 `ConfigurableWebBindingInitializer` 

       【***DataBinder负责将请求数据绑定到JavaBean上，如WebDataBinder***】
       
     - 没有喜欢的也可以自定义数据绑定器

2. SpringBoot自定义组件的三种方案

   > If you want to keep those Spring Boot MVC customizations and make more [MVC customizations](https://docs.spring.io/spring/docs/5.2.9.RELEASE/spring-framework-reference/web.html#mvc) (interceptors, formatters, view controllers, and other features), you can add your own `@Configuration` class of type `WebMvcConfigurer` but **without** `@EnableWebMvc`.
   >
   > **不用@EnableWebMvc注解。使用** **`@Configuration`** **+** **`WebMvcConfigurer`** **自定义规则**

   > If you want to provide custom instances of `RequestMappingHandlerMapping`, `RequestMappingHandlerAdapter`, or `ExceptionHandlerExceptionResolver`, and still keep the Spring Boot MVC customizations, you can declare a bean of type `WebMvcRegistrations` and use it to provide custom instances of those components.
   >
   > **声明** **`WebMvcRegistrations`** **改变默认底层组件**

   > If you want to take complete control of Spring MVC, you can add your own `@Configuration` annotated with `@EnableWebMvc`, or alternatively add your own `@Configuration`-annotated `DelegatingWebMvcConfiguration` as described in the Javadoc of `@EnableWebMvc`.
   >
   > **使用** **`@EnableWebMvc+@Configuration+DelegatingWebMvcConfiguration 全面接管SpringMVC`**



## 4.2、静态资源规则与定制化

> 静态资源指图片、视频、JS文件、CSS文件等等

### 4.2.1、静态资源访问

1. 静态资源目录

   + 只要以下目录放在类路径下[***resources目录下***]都可以作为静态资源目录

     【  `/static` or `/public` or `/resources` or `/META-INF/resources`】

2. 静态资源的请求路径

   + 当前项目根路径即/ + 带后缀的静态资源名 【如：http://localhost:8080/1.png】

     [^原理]: 静态资源映射所有请求/**，请求先通过DispatcherServlet寻找资源，找不到就通过静态资源处理器去找，静态资源处理器也找不到就报404

3. 静态资源的访问前缀

   + 静态资源的请求路径默认无前缀

   + 可以通过spring.mvc.static-path-pattern属性配置静态资源访问前缀，设置前缀后的请求路径为:

     当前项目根路径即/ +spring.mvc.static-path-pattern+ 带后缀的静态资源名【如：http://localhost:8080/res/1.png】

     ```yaml
     spring:
       mvc:
         static-path-pattern: /res/**
     ```

4. 改变默认的静态资源路径

   + 通过spring.web.resources.static-locations属性指定新的静态资源目录，指定的新目录可以是一个数组，即可以配置多个静态目录，此时默认静态资源目录除 `/META-INF/resources`外的确全部失效

     ```yaml
     spring:
       web:
         resources:
           static-locations: [classpath:/app1]
     ```

5. WebJars

   > 作用是把一些前端资源文件以jar包的方式作为maven依赖导入应用

   + 把常见的一些Bootstrap、JQuery、CSS等资源文件弄成了jar包，实际jar包解压之后里面还是原版资源文件，jar包可以作为依赖导入maven

     ~导入jquery的依赖

     ```xml
     <dependency>
         <groupId>org.webjars</groupId>
         <artifactId>jquery</artifactId>
         <version>3.5.1</version>
     </dependency>
     ```

   + WebJars官网：https://www.webjars.org/

   + 访问地址：[http://localhost:8080/webjars/**jquery/3.5.1/dist/jquery.js**](http://localhost:8080/webjars/jquery/3.5.1/dist/jquery.js)  【***项目根路径'/'后面地址webjars/jquery/3.5.1/dist/jquery.js是按照对应依赖jar包里面的类路径写的***】

     [^注意]: 这个资源访问地址不需要加前缀?为什么这里不需要加前缀res，已验证：`/META-INF/resources`仍然需要加前缀才能访问

### 4.2.2、欢迎页支持

> SpringBoot支持两种欢迎页设置方式

1. 静态资源目录下的index.html

   + 当设置了访问前缀，所有的欢迎页都会失效；未设置访问前缀，所有静态资源目录下的欢迎页均有效

     ```yaml
     spring:
       #mvc:
       #  static-path-pattern: /res/**  #这个会导致所有静态资源目录下index.html的欢迎页功能失效
       web:
         resources:
           static-locations: [classpath:/app1]
     ```

2. 使用Controller处理"/"跳转index.html

   + 这个暂时还没说



### 4.2.3、自定义Favicon

> Favicon的作用是设置网页标签上的小图标，访问该应用的任何请求都会展示该小图标，小图标是浏览器显示在Title前面的图标

1. 设置方式

   + 将目标小图标命名成favicon.ico放在静态资源目录下即可

     【注意设置了访问前缀会导致Favicon功能失效，添加了静态资源应该clear和package一下，避免资源没有打包到target】

     ```yaml
     spring:
       #mvc:
       #  static-path-pattern: /res/**  #这个设置会导致Favicon功能失效
     ```

[^注意]: 设置静态资源访问前缀会导致欢迎页功能和小图标功能都失效，这个问题是Spring的bug，因为浏览器发的请求是：当前项目根路径即/+favicon.ico；一旦配置了前缀，访问favicon.ico还必须加上前缀，所以自然找不到



## 4.3、静态资源配置原理

1. SpringBoot启动默认加载自动配置类xxxAutoConfiguration类，相关的xxxAutoConfiguration有：

   + DispatcherServlertConfiguration【配置DispatcherServlet规则的】
   + HttpEncodingAutoConfiguration【配置编解码的】
   + MultipartAutoConfiguration【配置文件上传的】
   + ServletWebServerAutoConfiguration【配置服务器的】
   + WebMvcAutoConfiguration【这个是SpringMVC的自动配置，SpringMVC的功能大多集中在这个类中】

2. WebMvcAutoConfiguration源码解析

   + 重点一：配置类组件WebMvcAutoConfigurationAdapter中进行配置绑定的两个类

     + WebMvcProperties：绑定spring.mvc前缀的配置属性
     + ResourceProperties：绑定spring.spring.resources前缀的配置属性

   + 重点二：WebMvcAutoConfiguration配置类给容器中配置的组件

     + ResourceProperties resourceProperties：获取和spring.resources配置绑定所有值的对象

     + WebMvcProperties mvcProperties：获取和spring.mvc配置绑定所有值的对象

     + ListableBeanFactory beanFactory:Spring的beanFactory(bean工厂)，即IoC容器
       【***以下ObjectProvider<Xxx>表示找到容器中的所有Xxx组件***】

     + ObjectProvider<HttpMessageConverters>:找到所有的HttpMessageConverters

     + ObjectProvider<ResourceHandlerRegistrationCustomizer>:找到资源处理器的自定义器

     + ObjectProvider<DispatcherServletPath>:找DispatcherServlet能处理的路径

     + ObjectProvider<ServletRegistrationBean<?>>:找到所有给应用注册Servlet、Filter、Listener的组件

       [^注意]: 只有一个有参数构造方法的配置类会自动调用容器组件为参数赋值

   + 重点三：静态资源的默认处理规则

     + WebJars对应静态资源的相关规则

       + 如果有/webjars/**请求，去类路径下找/META-INF/resources/webjars/，且相应静态资源应用缓存策略，缓存时间由spring.resources.cache.period配置属性控制

     + 静态资源路径的相关配置规则

       + 通过spring.mvc.static-path-pattern属性获取静态资源目录是否有前缀，没配置也有默认值staticPathPattern=/**,即匹配所有请求，通过this.resourceProperties.getStaticLocations()去找静态资源，staticLocations是一个字符串数组，有四个字符串默认值:被定义成常量的字符串数组{ "classpath:/META-INF/resources/","classpath:/resources/", "classpath:/static/", "classpath:/public/" }，即静态资源默认的四个位置，且这里的静态资源也有缓存策略

         [^Ques?]: 如果设置了自定义静态资源目录，上述静态资源目录数组会发生变化吗

   + 重点四：欢迎页处理规则

     + 通过spring.mvc.static-path-pattern属性(指定静态资源请求前缀，默认是没有前置，即/**)创建welcomePageHandlerMapping
     + 如果欢迎页存在且静态资源请求路径没有前缀，则重定向到静态资源目录的index.html，所以有自定义静态资源请求前缀欢迎页就找不到了，在创建对象welcomePageHandlerMapping的第一个if中写死了
     + 否则如果欢迎页存在，但是staticPathPattern不为/**，直接设置viewName为index转到Controller看是否存在控制器方法能处理"/index"请求

   【***WebMvcAutoConfiguration的源码逐行解析***】

   ```java
   @Configuration(proxyBeanMethods = false)
   @ConditionalOnWebApplication(type = Type.SERVLET)//判断是否原生servlet的web应用，是就生效
   @ConditionalOnClass({ Servlet.class, DispatcherServlet.class, WebMvcConfigurer.class })//应用中有Servlet类、DispatcherServlet类、WebMvcConfigurer类就生效，这些是导了spring-webmvc就一定会有的
   @ConditionalOnMissingBean(WebMvcConfigurationSupport.class)//容器中没有WebMvcConfigurationSupport这个组件就生效，只要有这个组件下面这个类的所有配置都不生效，在这儿规定了后面介绍的全面接管SpringMVC，全面的定制SpringMVC，只要用户配置了，自动配置SpringMVC就不生效
   @AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 10)
   @AutoConfigureAfter({ DispatcherServletAutoConfiguration.class, TaskExecutionAutoConfiguration.class,
   		ValidationAutoConfiguration.class })
   public class WebMvcAutoConfiguration {
   	...
   
   	@Bean//OrderedHiddenHttpMethodFilter是HiddenHttpMethodFilter的子类,SpringMVC兼容Rest风格的过滤器组件，接收前端发送的put、delete请求,SpringBoot默认就配置了HiddenHttpMethodFilter组件
   	@ConditionalOnMissingBean(HiddenHttpMethodFilter.class)
   	@ConditionalOnProperty(prefix = "spring.mvc.hiddenmethod.filter", name = "enabled", matchIfMissing = false)//如果spring.mvc.hiddenmethod.filter.name属性为true就开启HiddenHttpMethodFilter组件的功能，默认值为false，所以在表单发送请求的前提下还要在全局配置文件中配置spring.mvc.hiddenmethod.filter.name=true
   	public OrderedHiddenHttpMethodFilter hiddenHttpMethodFilter() {
   		return new OrderedHiddenHttpMethodFilter();
   	}
   
   	@Bean//OrderedFormContentFilter表单内容的过滤器
   	@ConditionalOnMissingBean(FormContentFilter.class)
   	@ConditionalOnProperty(prefix = "spring.mvc.formcontent.filter", name = "enabled", matchIfMissing = true)
   	public OrderedFormContentFilter formContentFilter() {
   		return new OrderedFormContentFilter();
   	}
   
   	...
   
           
       //重点一
   	@Configuration(proxyBeanMethods = false)//配置类WebMvcAutoConfigurationAdapter是一个WebMvcConfigurer组件
   	@Import(EnableWebMvcConfiguration.class)
   	@EnableConfigurationProperties({ WebMvcProperties.class, ResourceProperties.class })//这行代码表示配置类与WebMvcProperties、ResourceProperties直接进行绑定，简介与配置文件中的配置数据进行绑定
       //WebMvcProperties：该类上@Configurationproperties(prefix="spring.mvc")表明其跟配置文件的spring.mvc前缀存在配置绑定关系
       //ResourceProperties:该类上@Configurationproperties(prefix="spring.resources")表明其跟配置文件的spring.resources前缀存在配置绑定关系
   	@Order(0)
   	public static class WebMvcAutoConfigurationAdapter implements WebMvcConfigurer {
   
   		...属性
   
               
           //重点二
           //这个配置类只有一个有参构造器，仅有一个有参构造器的所有参数都会从IoC容器中确定,以下是从容器中确定的参数
           //ResourceProperties resourceProperties：获取和spring.resources配置绑定所有值的对象
           //WebMvcProperties mvcProperties：获取和spring.mvc配置绑定所有值的对象
           //ListableBeanFactory beanFactory:Spring的beanFactory(bean工厂)，即IoC容器
           //以下ObjectProvider<Xxx>表示找到容器中的所有Xxx组件
           //ObjectProvider<HttpMessageConverters>:找到所有的HttpMessageConverters
           //ObjectProvider<ResourceHandlerRegistrationCustomizer>:找到资源处理器的自定义器
           //ObjectProvider<DispatcherServletPath>:找DispatcherServlet能处理的路径
           //ObjectProvider<ServletRegistrationBean<?>>:找到所有给应用注册Servlet、Filter、Listener的组件
   		public WebMvcAutoConfigurationAdapter(ResourceProperties resourceProperties, WebMvcProperties mvcProperties,
   				ListableBeanFactory beanFactory, ObjectProvider<HttpMessageConverters> messageConvertersProvider,
   				ObjectProvider<ResourceHandlerRegistrationCustomizer> resourceHandlerRegistrationCustomizerProvider,
   				ObjectProvider<DispatcherServletPath> dispatcherServletPath,
   				ObjectProvider<ServletRegistrationBean<?>> servletRegistrations) {
   			...给属性赋值
   		}
   
   		...无关代码
   
   		@Bean//配置视图资源解析器InternalResourceViewResolver，配置条件容器中没有就配置，用户配置了就不自动配置了
   		@ConditionalOnMissingBean
   		public InternalResourceViewResolver defaultViewResolver() {
   			InternalResourceViewResolver resolver = new InternalResourceViewResolver();
   			resolver.setPrefix(this.mvcProperties.getView().getPrefix());
   			resolver.setSuffix(this.mvcProperties.getView().getSuffix());
   			return resolver;
   		}
   
   		...无关代码
               
   		@Bean//添加国际化支持组件localeResolver()
   		@ConditionalOnMissingBean
   		@ConditionalOnProperty(prefix = "spring.mvc", name = "locale")
   		public LocaleResolver localeResolver() {
   			if (this.mvcProperties.getLocaleResolver() == WebMvcProperties.LocaleResolver.FIXED) {
   				return new FixedLocaleResolver(this.mvcProperties.getLocale());
   			}
   			AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
   			localeResolver.setDefaultLocale(this.mvcProperties.getLocale());
   			return localeResolver;
   		}
   
   		...无关代码
               
   		@Override//格式化器，一般用来格式化货币日期
   		public void addFormatters(FormatterRegistry registry) {
   			ApplicationConversionService.addBeans(registry, this.beanFactory);
   		}
   
           
           //重点三(*****核心在此处)
   		@Override//添加资源处理器，所有的资源处理默认规则都在这
   		public void addResourceHandlers(ResourceHandlerRegistry registry) {
   			if (!this.resourceProperties.isAddMappings()) {//ResourceProperties中的addMapping属性，对应配置文件中的spring.resources.add-mapping属性，可选值true和false，默认是true。如果配置成false，就会执行日志输出，下面所有的配置都不生效，下面的配置是:静态资源的规则配置，可以通过设置spring.resources.add-mapping为false禁用所有静态资源的路径映射，所有的静态资源都无法访问
   				logger.debug("Default resource handling disabled");//日志信息是静态资源已经被禁用掉了
   				return;
   			}
   			Duration cachePeriod = this.resourceProperties.getCache().getPeriod();//通过spring.resources.cache.period获取缓存策略，cache.period是缓存资源刷新时间，从ResourceProperties中可以看出以秒为单位，意为所有的静态资源浏览器默认可以存多少秒
   			CacheControl cacheControl = this.resourceProperties.getCache().getCachecontrol().toHttpCacheControl();
               
               //WebJars的相关规则
               //开始注册webJars下的所有请求，如果有/webjars/**请求，去类路径下找/META-INF/resources/webjars/，且相应静态资源应用缓存策略，缓存时间由spring.resources.cache.period配置属性控制
   			if (!registry.hasMappingForPattern("/webjars/**")) {
              customizeResourceHandlerRegistration(registry.addResourceHandler("/webjars/**")
   						.addResourceLocations("classpath:/META-INF/resources/webjars/")
   				.setCachePeriod(getSeconds(cachePeriod)).setCacheControl(cacheControl));
   			//所有webjars请求都去类路径下找/META-INF/resources/webjars/，直接从External Libraries下找吗？在target下的/META-INF/resources/下没看见对应资源，但是还是能够访问，且该静态资源能够在缓存中按照缓存策略设置的时间缓存一段时间，在响应头的Cache-Control能够看到缓存的最大时间
               }
               
               //静态资源路径的相关配置规则
   			String staticPathPattern = this.mvcProperties.getStaticPathPattern();//通过spring.mvc.static-path-pattern属性获取静态资源目录是否有前缀，没配置也有默认值staticPathPattern=/**,即匹配所有请求
   			if (!registry.hasMappingForPattern(staticPathPattern)) {
   				customizeResourceHandlerRegistration(registry.addResourceHandler(staticPathPattern)
   						.addResourceLocations(getResourceLocations(this.resourceProperties.getStaticLocations()))
   						.setCachePeriod(getSeconds(cachePeriod)).setCacheControl(cacheControl));
               //所有请求都去指定的位置this.resourceProperties.getStaticLocations()去找静态资源，staticLocations是一个字符串数组，有四个字符串默认值:被定义成常量的字符串数组{ "classpath:/META-INF/resources/","classpath:/resources/", "classpath:/static/", "classpath:/public/" }，即静态资源默认的四个位置，且这里的静态资源也有缓存策略
   			}
   		}
   		...无关代码
   	}
   
   	@Configuration(proxyBeanMethods = false)//配置类
   	public static class EnableWebMvcConfiguration extends DelegatingWebMvcConfiguration implements ResourceLoaderAware {
   		
           ...无关代码
   		
               
           //重点四
           @Bean//欢迎页的配置规则
           //HandleMapping:处理器映射，是SpringMVC中的一个核心组件，里面保存了每一个Handler能处理哪些请求，找到了就利用反射机制调用对应的方法,WelcomePageHandlerMapping中就存放了谁来处理欢迎页的请求映射规则
   		public WelcomePageHandlerMapping welcomePageHandlerMapping(ApplicationContext applicationContext,
   				FormattingConversionService mvcConversionService, ResourceUrlProvider mvcResourceUrlProvider) {
               //通过spring.mvc.static-path-pattern属性(指定静态资源请求前缀，默认是没有前置，即/**)创建welcomePageHandlerMapping，重点是创建welcomePageHandlerMapping对象时配置了静态资源请求前缀无法访问欢迎页面的问题
   			WelcomePageHandlerMapping welcomePageHandlerMapping = new WelcomePageHandlerMapping(
   					new TemplateAvailabilityProviders(applicationContext), applicationContext, getWelcomePage(),
   					this.mvcProperties.getStaticPathPattern());
   	welcomePageHandlerMapping.setInterceptors(getInterceptors(mvcConversionService, mvcResourceUrlProvider));
   			welcomePageHandlerMapping.setCorsConfigurations(getCorsConfigurations());
   			return welcomePageHandlerMapping;
   		}
   		...无关代码
   	}
       //定义接口
   	interface ResourceHandlerRegistrationCustomizer {
   		void customize(ResourceHandlerRegistration registration);
   	}
       //定义类
   	static class ResourceChainResourceHandlerRegistrationCustomizer implements ResourceHandlerRegistrationCustomizer {
   		...无关代码
   	}
       //定义类
   	static class OptionalPathExtensionContentNegotiationStrategy implements ContentNegotiationStrategy {
   		...无关代码
   }
   ```

   【***WelcomePageHandlerMapping欢迎页处理映射器构造方法的代码逐行解析***】

   ```java
   WelcomePageHandlerMapping(TemplateAvailabilityProviders templateAvailabilityProviders,
   			ApplicationContext applicationContext, Optional<Resource> welcomePage, String staticPathPattern) {
       //如果欢迎页存在且静态资源请求路径没有前缀，则重定向到静态资源目录的index.html，所以有自定义静态资源请求前缀欢迎页就找不到了，在第一个if中写死了
       if (welcomePage.isPresent() && "/**".equals(staticPathPattern)) {
           //所以要使用欢迎页功能不能添加静态资源请求前缀，staticPathPattern必须是/**
           logger.info("Adding welcome page: " + welcomePage.get());
           setRootViewName("forward:index.html");
       }
       else if (welcomeTemplateExists(templateAvailabilityProviders, applicationContext)) {
           logger.info("Adding welcome page template: index");
           //否则如果欢迎页存在，但是staticPathPattern不为/**，直接设置viewName为index转到Controller看谁能不能处理"/index"请求
           setRootViewName("index");
       }
   }
   ```

   

## 4.4、请求参数处理

###  4.4.1、Rest风格请求映射原理

1. @RequestMapping的派生注解

   + @GetMapping【相当于**@RequestMapping(value = "/user",method = RequestMethod.GET)**】
   + @PostMapping【~**@RequestMapping(value = "/user",method = RequestMethod.POST)**】
   + @PutMapping【~**@RequestMapping(value = "/user",method = RequestMethod.PUT)**】
   + @DeleteMapping【~**@RequestMapping(value = "/user",method = RequestMethod.DELETE)**】

2. Rest风格支持【***使用同一请求路径不同的HTTP请求方式动词来区分对资源的操作***】

   + 以前：[***/getUser 获取用户***]，[***/deleteUser 删除用户***]，[***/editUser 修改用户***]，[***/saveUser保存用户***]
   + 现在：/user   [***GET-获取用户***]，[***DELETE-删除用户***]，[***PUT-修改用户***]，[***POST-保存用户***]
   + Rest风格支持核心的核心Filter组件：**HiddenHttpMethodFilter**
     + SpringBoot默认就配置了HiddenHttpMethodFilter组件，其实是其子类OrderedHiddenHttpMethodFilter

3. HiddenHttpMethodFilter组件的用法

   + 步骤一：通过前端页面form表单发送post请求，设置隐藏域类型的_method参数为put、delete发送PUT或DELETE请求，GET请求和POST请求正常发，实现前端Rest风格请求的发送准备

     ```html
     <form action="/user" method="get">
         <input value="REST-GET提交" type="submit" />
     </form>
     
     <form action="/user" method="post">
         <input value="REST-POST提交" type="submit" />
     </form>
     
     <form action="/user" method="post">
         <input name="_method" type="hidden" value="DELETE"/>
         <input value="REST-DELETE 提交" type="submit"/>
     </form>
     
     <form action="/user" method="post">
         <input name="_method" type="hidden" value="PUT" />
         <input value="REST-PUT提交"type="submit" />
     </form>
     ```

   + 步骤二：通过在全局配置文件中配置spring.mvc.hiddenmethod.filter.name=true开启页面表单的隐藏请求方式过滤器组件的功能

     ```yaml
     spring:
       mvc:
         hiddenmethod:
           filter:
             enabled: true   #开启页面表单的Rest功能
     ```

   + 步骤三：编写控制器方法处理Rest风格请求映射

     ```java
     @GetMapping("/user")
     //@RequestMapping(value = "/user",method = RequestMethod.GET)
     public String getUser(){
         return "GET-张三";
     }
     
     @PostMapping("/user")
     //@RequestMapping(value = "/user",method = RequestMethod.POST)
     public String saveUser(){
         return "POST-张三";
     }
     
     @PutMapping("/user")
     //@RequestMapping(value = "/user",method = RequestMethod.PUT)
     public String putUser(){
         return "PUT-张三";
     }
     
     @DeleteMapping("/user")
     //@RequestMapping(value = "/user",method = RequestMethod.DELETE)
     public String deleteUser(){
         return "DELETE-张三";
     }
     ```

4. form表单提交使用REST风格的原理

   + 发送put和delete请求表单以post方式提交并添加请求参数'_method',即开启页面表单的Rest功能
   + 请求被服务器捕获并被HiddenHttpMethodFilter拦截
     + 请求方式是否为post且请求没有错误和异常
       + 获取表单请求参数_method的属性值
         + 如果属性值不为空串或者null则全部转换为大写字母
         + 判断_method的属性值是否为PUT、DELETE、PATCH三者之一
           + 是就创建原生请求对象的包装类，将_method的属性值赋值给包装类自己的method属性中，通过重写getMethod方法将获取请求方式的值指向_method的属性值
     + 如果请求方式不为post直接放行原生请求，如果是post就放行请求的包装类
   + **相关源码**

   【***配置spring.mvc.hiddenmethod.filter.name=true的源码***】

   ```java
   public class WebMvcAutoConfiguration {
       ...
   	@Bean//OrderedHiddenHttpMethodFilter是HiddenHttpMethodFilter的子类,SpringMVC兼容Rest风格的过滤器组件，接收前端发送的put、delete请求,SpringBoot默认就配置了HiddenHttpMethodFilter组件
   	@ConditionalOnMissingBean(HiddenHttpMethodFilter.class)
   	@ConditionalOnProperty(prefix = "spring.mvc.hiddenmethod.filter", name = "enabled", matchIfMissing = false)//如果spring.mvc.hiddenmethod.filter.name属性为true就开启HiddenHttpMethodFilter组件的功能，默认值为false，所以在表单发送请求的前提下还要在全局配置文件中配置spring.mvc.hiddenmethod.filter.name=true
   	public OrderedHiddenHttpMethodFilter hiddenHttpMethodFilter() {
   		return new OrderedHiddenHttpMethodFilter();
   	}
       ...
   }
   ```

   【***使用表单post请求并添加_method属性的源码***】

   ```java
   public class HiddenHttpMethodFilter extends OncePerRequestFilter {
   
   	private static final List<String> ALLOWED_METHODS =
   			Collections.unmodifiableList(Arrays.asList(HttpMethod.PUT.name(),
   					HttpMethod.DELETE.name(), HttpMethod.PATCH.name()));
   
   	public static final String DEFAULT_METHOD_PARAM = "_method";
   
   	private String methodParam = DEFAULT_METHOD_PARAM;
       
   	public void setMethodParam(String methodParam) {
   		Assert.hasText(methodParam, "'methodParam' must not be empty");
   		this.methodParam = methodParam;
   	}
   
   	@Override//执行Rest风格的请求拦截代码
   	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
   			throws ServletException, IOException {
   		HttpServletRequest requestToUse = request;//原生请求的引用赋值给requestToUse
   		if ("POST".equals(request.getMethod()) && request.getAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE) == null) {//判断原生请求的请求方式是否为post且请求没有错误
   			String paramValue = request.getParameter(this.methodParam);//获取表单请求参数_method的属性值
   			if (StringUtils.hasLength(paramValue)) {//判断_method的属性值是否为空串或者null
   				String method = paramValue.toUpperCase(Locale.ENGLISH);//_method的属性值全部转成大写
   				if (ALLOWED_METHODS.contains(method)) {//判断允许的请求方式是否包含_method的大写属性值，允许的请求方式ALLOWED_METHODS是一个字符串类型的List集合，内容是三个枚举PUT、DELETE、PATCH的名字
   					requestToUse = new HttpMethodRequestWrapper(request, method);//如果包含则创建一个Http请求方式的请求装饰器并赋值给requestToUse，HttpMethodRequestWrapper继承于HttpServletRequestWrapper，HttpServletRequestWrapper实现了HttpServletRequest接口，所以HttpMethodRequestWrapper还是一个原生的request请求，HttpMethodRequestWrapper把新的请求方式通过构造器传参保存在自己的method属性中并重写了getMethod方法，返回装饰器自己的method属性值而非继承来的method属性值
   				}
   			}
   		}
           //过滤器放行的是原生request的装饰器对象HttpMethodRequestWrapper
   		filterChain.doFilter(requestToUse, response);
   	}
   
   	private static class HttpMethodRequestWrapper extends HttpServletRequestWrapper {
   
   		private final String method;
   
   		public HttpMethodRequestWrapper(HttpServletRequest request, String method) {
   			super(request);
   			this.method = method;
   		}
   
   		@Override
   		public String getMethod() {
   			return this.method;
   		}
   	}
   }
   ```

5. 可以使用客户端工具或者Ajax直接发送put或者delete请求

   > 在支持发送真实put、delete请求的场景下就没有必要使用包装类HttpMethodRequestWrapper了，此时请求方式会直接为put或者delete，直接把request赋值给requestToUse，然后立即放行requestToUse

   - PostMan可直接发送put或者delete请求

   - 安卓直接发put或者delete请求
   - Ajax直接发put或者delete请求

6. 扩展点：如何把_method这个名字换成用户自定义的

   + 要点

     + HiddenHttpMethodFilter组件如果有SpringBoot就不再自动装配，用户可以自己配置
     + HiddenHttpMethodFilter中的setMethodParam方法可以设置methodParam用自定义参数名代替_method

     ```java
     public class WebMvcAutoConfiguration {
         ...    
         @Bean//hiddenHttpMethodFilter条件装配，如果自己配置了HiddenHttpMethodFilter并设置了相应的属性，SpringBoot就不会配置HiddenHttpMethodFilter组件了，利用这点用户可以配置自定义HiddenHttpMethodFilter组件，通过setMethodParam修改methodParam为自定义参数名实现自定义_method这个参数名
         @ConditionalOnMissingBean(HiddenHttpMethodFilter.class)
         @ConditionalOnProperty(prefix = "spring.mvc.hiddenmethod.filter", name = "enabled", matchIfMissing = false)
         public OrderedHiddenHttpMethodFilter hiddenHttpMethodFilter() {
             return new OrderedHiddenHttpMethodFilter();
         }   
         ...
     }
     //HiddenHttpMethodFilter中的setMethodParam方法可以设置methodParam用自定义参数名代替_method
     public class HiddenHttpMethodFilter extends OncePerRequestFilter {
     	...
     	private String methodParam = DEFAULT_METHOD_PARAM;
         
     	public void setMethodParam(String methodParam) {
     		Assert.hasText(methodParam, "'methodParam' must not be empty");
     		this.methodParam = methodParam;
     	}
     	...
     }
     ```

     

### 4.4.2、请求映射原理

> SpringBoot底层处理请求还是使用SpringMVC，DispatcherServlet是所有请求处理的开始，DispatcherServlet又名前端控制器，分发控制器
>
> DispatcherServlet继承于FrameworkServlet，FrameworkServlet继承于HttpServletBean，HttpServletBean继承于HttpServlet
>
> HttpServlet中没有重写doXxx方法，这一类方法的重写在子类FrameworkServlet中完成，而且统一都去调用自己的processRequest方法，processRequest核心方法是doService方法，执行doService方法前后分别是初始化过程【调用setter/getter方法】和日志处理过程，FrameworkServlet中的doService方法是一个抽象方法，在DispatcherServlet子类中进行了实现
>
> DispatcherServlet中doService方法的核心方法是doDispatch方法【doDispatch意为给请求做派发】，也是请求处理的核心逻辑方法，每一个请求进来都会调用doDispatch方法

#### 01、doDispatch方法源码解析

```java
protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
    HttpServletRequest processedRequest = request;//[/user GET方式请求]请求重新赋值，此时请求的路径是/user
    HandlerExecutionChain mappedHandler = null;//Handler执行链，执行链中有handler属性，这个应该才是真正的handler
    boolean multipartRequestParsed = false;//文件上传请求解析默认是false

    WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);//请求是否有异步，如果有异步使用异步管理器

    try {
        ModelAndView mv = null;//视图空初始化
        Exception dispatchException = null;//异常空初始化

        try {
            processedRequest = checkMultipart(request);//检查是否文件上传请求，如果是文件上传请求把原生请求request赋值给processedRequest，即如果是文件上传请求，就把原生请求包装成文件上传请求processedRequest；checkMultipart方法中使用的是MultipartResolver的isMultipart方法对是否文件上传请求进行判断的，判断依据是请求的内容类型是否叫“multipart/”,所有这里决定了form表单需要写enctype="multipart/form-data";如果是文件上传请求使用MultipartResolver文件上传解析器的resolverMultipart(request)方法把文件上传请求进行解析包装成StandardMultipartHttpServletRequest这个类型并返回
            multipartRequestParsed = (processedRequest != request);//如果是文件上传请求根据检查结果重新设定文件上传请求解析

            //重点一:真正获取处理器执行链
            // Determine handler for the current request.
            mappedHandler = getHandler(processedRequest);//决定当前请求的处理器，handler就是用户自己写的处理器方法,mappedHandler最终显示的是HelloController#getUser()方法，即自定义控制器HelloController的getUser()方法
            if (mappedHandler == null) {
                noHandlerFound(processedRequest, response);
                return;
            }

            //重点二：获取处理器适配器，通过这个反射工具调用处理器方法
            // Determine handler adapter for the current request.
            HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());//为确定的Handler找到合适的适配器，mappedHandler.getHandler()的运算结果是对应请求的处理器方法，使用注解@RequestMapping的控制器方法对应的类型是HandlerMethod；控制器方法的调用是通过反射机制实现的，SpringMVC将使用反射机制调用控制器方法的过程封装进了HandlerAdapter中，相当于把HandlerAdapter作为一个大的反射工具，适配器HandlerAdapter通过handle()方法调用目标控制方法； HandlerAdapter是一个接口，该接口中的supports(handler)方法告诉SpringMVC该HandlerAdapter支持处理哪种Handler，handler方法可以处理被支持的handler的目标方法调用，HandlerAdapter也可以自定义，需要指定支持的handler以及具体的handler方法

            // Process last-modified header, if supported by the handler.
            String method = request.getMethod();
            boolean isGet = "GET".equals(method);//获取当前请求的请求方式并判断是否get方法
            if (isGet || "HEAD".equals(method)) {//HEAD请求的响应可被缓存，也就是说，响应中的信息可能用来更新以前缓存的实体，如果有浏览器缓存需要更新可以进行缓存处理
                long lastModified = ha.getLastModified(request, mappedHandler.getHandler());
                if (new ServletWebRequest(request, response).checkNotModified(lastModified) && isGet) {
                    return;
                }
            }

            if (!mappedHandler.applyPreHandle(processedRequest, response)) {
                return;
            }//执行拦截器的preHandle方法

            //重点三：反射调用处理器方法
            // Actually invoke the handler.通过处理器适配器真正执行handler目标方法，传入请求、响应对象，以及匹配的handler；在handle()方法中调用了handleInternal()方法
            mv = ha.handle(processedRequest, response, mappedHandler.getHandler());

            if (asyncManager.isConcurrentHandlingStarted()) {
                return;
            }

            applyDefaultViewName(processedRequest, mv);//如果控制器方法没有返回视图名，applyDefaultViewName方法，如果ModelAndView不为空但是view为空，会添加一个默认的视图地址，该默认视图地址还是会用请求地址作为页面地址，细节没说，暂时不管
            mappedHandler.applyPostHandle(processedRequest, response, mv);//这里面就是单纯执行相关拦截器的postHandle方法，没有其他任何操作
        }
        catch (Exception ex) {
            dispatchException = ex;
        }
        catch (Throwable err) {
            // As of 4.3, we're processing Errors thrown from handler methods as well,
            // making them available for @ExceptionHandler methods and other scenarios.
            dispatchException = new NestedServletException("Handler dispatch failed", err);
        }
        //重点四：处理派发最终的结果，这一步执行前，整个页面还没有跳转过去
        processDispatchResult(processedRequest, response, mappedHandler, mv, dispatchException);
    }
    catch (Exception ex) {
        triggerAfterCompletion(processedRequest, response, mappedHandler, ex);
    }
    catch (Throwable err) {
        triggerAfterCompletion(processedRequest, response, mappedHandler,
                new NestedServletException("Handler processing failed", err));
    }
    finally {
        if (asyncManager.isConcurrentHandlingStarted()) {
            // Instead of postHandle and afterCompletion
            if (mappedHandler != null) {
                mappedHandler.applyAfterConcurrentHandlingStarted(processedRequest, response);
            }
        }
        else {
            // Clean up any resources used by a multipart request.
            if (multipartRequestParsed) {
                cleanupMultipart(processedRequest);
            }
        }
    }
}
```

> 注意以下方法都是从doDispatcher方法中进入延伸

##### 重点1：getHandler(processedRequest)源码解析

1. mappedHandler = getHandler(processedRequest)的底层原理
   + 所有的请求映射都在应用启动时被解析放入各个HandlerMapping中

   + SpringBoot自动配置欢迎页的 WelcomePageHandlerMapping ，配置了"/"对应的映射规则：view= "forward:index.html"，能处理访问路径" /"转发到index.html
   + SpringBoot自动配置了默认的RequestMappingHandlerMapping，在mappingRegistry属性【映射的注册中心】中保存了用户自定义请求路径和控制器方法的映射规则，可以处理控制器方法的对应请求路径
   + 请求处理流程
     + 请求进来，for增强循环遍历所有的HandlerMapping匹配请求映射信息，找得到就返回对应控制器方法的Handler【Handler中封装了控制器方法的所有信息】，找不到就返回null继续遍历下一个HandlerMapping
2. 自定义处理器映射
   + 处理器映射容器中不止一个，用户可以根据需要自行给容器中配置HandlerMapping组件
   + 比如访问api文档，根据不同api版本设计不同的请求路径，不仅通过控制器来处理请求，还可以通过处理器映射指定请求映射规则来派发不同api版本的请求的对应资源路径

【***mappedHandler = getHandler(processedRequest)寻找处理当前请求的Handler的原理***】

```java
//该方法属于DispatcherServlet
@Nullable
protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
    if (this.handlerMappings != null) {
        for (HandlerMapping mapping : this.handlerMappings) {//增强for循环，从五个HandlerMapping中找出能处理当前请求的处理器映射，第一个就是RequestMappingHandlerMapping，从映射注册中心就能匹配对应的请求处理器
            HandlerExecutionChain handler = mapping.getHandler(request);//核心方法:mapping. getHandler(request)方法[该方法在HandlerMapping接口的实现类AbstractHandlerMapping中]中的getHandlerInternal(request)[该方法是类RequestMappingInfoHandlerMapping中的]方法，在这个方法的层层调用中就能判断对应的HandlerMapping有没有相应的请求映射信息，有就返回对应控制器方法的handler，没有就返回null
            if (handler != null) {
                return handler;
            }
        }
    }
    return null;
}
```

【***mapping.getHandler(request)方法中的getHandlerInternal(request)方法解析***】

```java
public abstract class RequestMappingInfoHandlerMapping extends AbstractHandlerMethodMapping<RequestMappingInfo> {
    ...
	@Override
	protected HandlerMethod getHandlerInternal(HttpServletRequest request) throws Exception {
		request.removeAttribute(PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE);//不管，移除一些东西
		try {
			return super.getHandlerInternal(request);//super.getHandlerInternal(request)核心方法
		}
		finally {
			ProducesRequestCondition.clearMediaTypesAttribute(request);
		}
	}
    ...
}
```

【***getHandlerInternal(request)方法中的super.getHandlerInternal(request)方法解析***】

```java
public abstract class AbstractHandlerMethodMapping<T> extends AbstractHandlerMapping implements InitializingBean {	
	...
    @Override
	protected HandlerMethod getHandlerInternal(HttpServletRequest request) throws Exception {
		String lookupPath = getUrlPathHelper().getLookupPathForRequest(request);//拿到原生请求想要访问的路径，此处是"/user"
		request.setAttribute(LOOKUP_PATH, lookupPath);
		this.mappingRegistry.acquireReadLock();//拿到一把锁，害怕并发查询mappingRegistry，在核心对象中可知mappingRegistry是RequestMappingHandlerMapping中的保存映射规则的一个属性
		try {
			HandlerMethod handlerMethod = lookupHandlerMethod(lookupPath, request);//核心方法：lookupHandlerMethod(lookupPath, request)方法对当前路径"/user"和请求进行处理
			return (handlerMethod != null ? handlerMethod.createWithResolvedBean() : null);
		}
		finally {
			this.mappingRegistry.releaseReadLock();
		}
	}
    ...
}
```

【***super.getHandlerInternal(request)方法中的lookupHandlerMethod(lookupPath, request)方法解析***】

```java
public abstract class AbstractHandlerMethodMapping<T> extends AbstractHandlerMapping implements InitializingBean {	
    ...
	@Nullable
	protected HandlerMethod lookupHandlerMethod(String lookupPath, HttpServletRequest request) throws Exception {
		List<Match> matches = new ArrayList<>();
		List<T> directPathMatches = this.mappingRegistry.getMappingsByUrl(lookupPath);//使用请求路径/user去mappingRegistry找哪一个handler能处理该路径，先根据url[即/user]不管请求方式去找，能找到四个请求路径为/user各种请求方式的处理器映射信息
		if (directPathMatches != null) {
			addMatchingMappings(directPathMatches, matches, request);//这一步没进去，功能是从找到的4个请求映射信息中找到最匹配的并添加到匹配映射集合matches中，当前请求此时matches中就只有一个了，如果写了多个能处理/user路径的GET请求的控制器方法，匹配映射集合matches中就会保存不止一个
		}
		if (matches.isEmpty()) {//如果没找到请求映射信息
			// No choice but to go through all mappings...
			addMatchingMappings(this.mappingRegistry.getMappings().keySet(), matches, request);//如果没找到就向集合中添加一些空的东西
		}

		if (!matches.isEmpty()) {//如果找到了请求映射信息且不为null
			Match bestMatch = matches.get(0);//如果matches中只有一个，则从匹配映射集合中取出第一个请求映射信息视为请求映射的最佳匹配
			if (matches.size() > 1) {//如果匹配映射集合matches的请求映射信息不止一个，接下来就会各种排序对比
				Comparator<Match> comparator = new MatchComparator(getMappingComparator(request));
				matches.sort(comparator);
				bestMatch = matches.get(0);
				if (logger.isTraceEnabled()) {
					logger.trace(matches.size() + " matching mappings: " + matches);
				}
				if (CorsUtils.isPreFlightRequest(request)) {
					return PREFLIGHT_AMBIGUOUS_MATCH;
				}
				Match secondBestMatch = matches.get(1);
				if (comparator.compare(bestMatch, secondBestMatch) == 0) {
					Method m1 = bestMatch.handlerMethod.getMethod();
					Method m2 = secondBestMatch.handlerMethod.getMethod();
					String uri = request.getRequestURI();
					throw new IllegalStateException(
							"Ambiguous handler methods mapped for '" + uri + "': {" + m1 + ", " + m2 + "}");//对比完各种请求映射信息，然后抛异常报错该请求有两个控制器方法都能处理，从这儿能看出SpringMVC要求同样的请求路径和请求方式，对应的控制器方法只能有一个
				}
			}
			request.setAttribute(BEST_MATCHING_HANDLER_ATTRIBUTE, bestMatch.handlerMethod);
			handleMatch(bestMatch.mapping, lookupPath, request);
			return bestMatch.handlerMethod;
		}
		else {
			return handleNoMatch(this.mappingRegistry.getMappings().keySet(), lookupPath, request);
		}
	}
    ...
}
```

##### 重点2：getHandlerAdapter(mappedHandler.getHandler())源码解析

1. 对默认的四个处理器适配器进行for增强遍历，使用四个不同适配器的supports方法对handler的类型进行匹配，@RequestMapping注解标注的方法对应Handler是HandlerMethod类型，函数式编程方法对应的是HandlerFunction类型，类型匹配就返回对应的适配器

【***doDispatch方法中的getHandlerAdapter方法解析***】

```java
public class DispatcherServlet extends FrameworkServlet {
    ...
    protected HandlerAdapter getHandlerAdapter(Object handler) throws ServletException {
        if (this.handlerAdapters != null) {
            for (HandlerAdapter adapter : this.handlerAdapters) {//for增强循环遍历4种HandlerAdapter处理器适配器，选出合适的处理器适配器
                if (adapter.supports(handler)) {//判断被循环的处理器适配器是否支持当前的handler，判断代码：return (handler instanceof HandlerMethod && supportsInternal((HandlerMethod) handler));意思是如果handler是HandlerMethod类型就返回，supportsInternal((HandlerMethod) handler))总是返回true，这里判断机制没说清楚，到底是根据不同adapter对应的的supports方法对应的handler的类型判断还是其他的，主要这里不知道其他的Adapter中的supports方法的handler应该是什么类型，看起来就是根据Adapter的不同supports方法支持不同的Handler类型进行判断的：@RequestMapping注解标注的handler都是HandlerMethod类型的，函数式编程的方法对应的handler的适配器支持的handler都是HandlerFunction类型的；其他两个先不管，需要的时候去对应适配器的supports方法里面看
                    return adapter;//找到适配器直接返回，找不到就报错
                }
            }
        }
        throw new ServletException("No adapter for handler [" + handler +
                "]: The DispatcherServlet configuration needs to include a HandlerAdapter that supports this handler");
    }
    ...
}
```

##### 重点3：ha.handle(processedRequest, response, mappedHandler.getHandler())源码解析

> 执行handler目标方法的适配器handle方法源码解析

1. handle方法核心流程总结：

   + 方法1：DispatcherServlet的doDispatch方法的handle方法
     + 调用handleInternal方法获得mav对象【ModelAndView】
   + 方法2：AbstractHandlerMethodAdapter的handle方法的handleInternal方法
     + 调用invokeHandlerMethod方法获取参数解析器解析参数，执行控制器方法将返回值封装到ModelAndView并返回ModelAndView
   + 方法3：RequestMappingHandlerAdapter的handleInternal方法的invokeHandlerMethod方法
     + 构建webRequest，放入doDispatch方法传参的request和response
     + 获取目标方法对象并封装成可执行方法对象，可执行方法对象可以调用invokeAndHandle方法
     + 为可执行方法对象设置所有的参数解析器和返回值处理器
     + 调用invokeAndHandle方法获取参数解析器解析参数，执行控制器方法将返回值【即视图信息】封装到mavContainer中并获取到模型和视图容器对象
     + 调用getModelAndView方法使用mavContainer获取ModelAndView对象并返回给handleInternal方法

   【分支1：invokeAndHandle方法分支】

   + 方法4：RequestMappingHandlerAdapter的invokeHandlerMethod方法的invokeAndHandle方法

     > 无需返回值，直接把操作结果传入参数mavContainer的defalutModel和view属性中

     + 调用invokeForRequest方法去找参数解析器解析参数，把请求域共享数据放进mavContainer的属性defaultModel，并调用目标控制器方法，完事后得到控制器目标方法的返回值存入returnValue
     + 调用handleReturnValue方法处理控制器方法的返回结果，其实是把返回值的字符串或者其他东西处理赋值给mavContainer的view属性

   【小分支1：invokeForRequest方法分支】

   + 方法5：ServletInvocableHandlerMethod的invokeAndHandle方法的invokeForRequest方法
     + 用一个Object数组接受调用getMethodArgumentValues方法获取的所有的形参对应的实参值，根据形参顺序排列
     + 调用doInvoke方法传参Object数组使用反射机制完成目标控制器方法的调用并向invokeAndHandle方法返回控制器方法的返回值
   + 方法6：InvocableHandlerMethod的invokeForRequest方法的getMethodArgumentValues方法
     + 获取目标方法的所有形式参数的详细信息，包括形式参数的注解、索引位置、参数类型等并创建对应参数长度的object数组准备接收解析出来的参数值，最后返回的就是接收完所有实参的Object数组
     + 有参数就对参数进行遍历，遍历过程中
       + 调用参数解析器集合的supportsParameter方法匹配对应参数的参数解析器，并将参数解析器以参数对象为key存入参数解析器缓存
       + 调用参数解析器集合的resolveArgument方法解析参数的值并将值顺序放入Object数组
     + 遍历完事以后返回Object数组

   【小小分支1：参数解析器集合的supportsParameter方法分支】

   + 方法7：InvocableHandlerMethod的getMethodArgumentValues方法的supportsParameter方法

     + 调用getArgumentResolver方法遍历所有参数解析器，并调用各个参数解析器的supportsParameter方法判断是否支持解析当前参数，支持就把对应的参数解析器存入argumentResolverCache即参数解析器缓存对象中，并返回参数解析器给supportsParameter方法，该方法判断参数解析器不为空就返回true，为空就返回false

       [^要点1]: 直到调用各个参数解析器的supportsParameter方法才涉及到不同参数解析器的实现类的同名方法，此前的流程所有的请求都会经历

   【小小分支2：参数解析器集合的resolveArgument方法分支】

   + 方法8：InvocableHandlerMethod的getMethodArgumentValues方法的resolveArgument方法

     > 参数解析器集合调用的resolveArgument方法

     + 尝试从缓存中获取对应的参数解析器，获取不到就去遍历参数解析器集合重新获取参数解析器并存入缓存
     + 调用对应的参数解析器的resolveArgument方法解析出参数值并返回被放入Object数组

   + 方法9：HandlerMethodArgumentResolverComposite中的参数解析器集合的resolveArgument方法的参数解析器的resolveArgument方法

     [^要点2]: 实际上从这里即调用各个参数解析器的resolveArgument方法都对应方法7中的相应参数解析器类，此后的才针对不同的请求进入不同的实现类进行处理

     + 通过参数获取参数的名字

     + 调用resolveName传入参数的名字获取参数值，实际是urlPathHelper提前按正则匹配的方式处理URL解析所有的数据封装成map集合uriTemplateVars，参数解析器获取参数都是拿着参数名直接从Map集合里面取，然后返回取得的参数值，最终封装在Object数组中

       [^注意]: 不是所有的参数都走uriTemplateVars，路径变量是走uriTemplateVars；请求头数据如User-Agent是以数组的形式直接放在请求域中，没有经过Map封装，直接在请求头方法参数解析器中通过请求对象获取相应变量值数组，如果数组长度为1则返回第一个元素，如果数组长度大于1就返回整个数组，如果数组长度为0则返回null

   【小分支2：handleReturnValue方法分支】

   + 方法10：ServletInvocableHandlerMethod的invokeAndHandle方法的handleReturnValue方法

     + 调用selectHandler方法根据返回值类型找到合适的返回值处理器并赋值给handler
     + 处理器handle对象调用对应的handleReturnValue方法处理控制器方法的返回值

   + 方法11：HandlerMethodReturnValueHandlerComposite的handleReturnValue方法的handleReturnValue方法

     + 如果Object类型的返回值是一个字符串，将返回值转为字符串并存入mavContainer对象的view属性，此时mavContainer【模型和视图的容器】对象中既有向请求域中共享的defaultModel中的数据，也有了view数据；
     + 如果viewName是重定向视图名就把mavContainer对象的redirectModelScenario属性设置为true

     [^Ques?]: 不知道这里返回值不是字符串类型如Map集合或者自定义对象的情况怎么处理?

   【分支2：getModelAndView方法分支】

   + 方法12：RequestMappingHandlerAdapter的invokeHandlerMethod方法的getModelAndView方法方法

     + 进来就有一个updateModel方法是设置model中共享数据绑定策略的，没听明白，暂时不管

     + 从mavContainer获取defaultModel，把defaultModel、view属性值还有状态码传入ModelAndView对象的有参构造来创建ModelAndView对象mav

       [^注意]: 这个时候defaultModel中的数据被打包到一个全新的ModelMap对象中，mav中保存的model属性已经不在是最初的defaultModel了

     + 如果Model是重定向携带数据，调用putAll方法把所有数据放到请求的上下文中【?应用上下文吗?,没听明白这里】，最后返回mav给invokeHandlerMethod方法		
       		

【***doDispatch方法中的ha.handle方法解析***】

```java
public abstract class AbstractHandlerMethodAdapter extends WebContentGenerator implements HandlerAdapter, Ordered {
    ...
	@Override
	@Nullable
	public final ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		return handleInternal(request, response, (HandlerMethod) handler);//核心方法，返回的是ModelAndView类型的mav对象(封装了请求域共享数据和视图名称)
	}
    ...
}
```

【***handle方法中的handleInternal方法解析***】

```java
//RequestMappingHandlerAdapter支持控制器方法上标注@RequestMapping注解的Handler的适配器
public class RequestMappingHandlerAdapter extends AbstractHandlerMethodAdapter
		implements BeanFactoryAware, InitializingBean {
    ...
    //实际上是父类AbstractHandlerMethodAdapter中的handleInternal方法被子类RequestMappingHandlerAdapter重写了，调用也是直接通过这个类继承来的的handler调用重写的handleInternal
	@Override
	protected ModelAndView handleInternal(HttpServletRequest request,
			HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {
		ModelAndView mav;
		checkRequest(request);
		// Execute invokeHandlerMethod in synchronized block if required.
		if (this.synchronizeOnSession) {//自己的用例中这儿进不来，应该和线程安全有关，同一个session会用session锁锁住
			HttpSession session = request.getSession(false);
			if (session != null) {
				Object mutex = WebUtils.getSessionMutex(session);
				synchronized (mutex) {
					mav = invokeHandlerMethod(request, response, handlerMethod);//执行handler的目标方法代码
				}
			}
			else {
				// No HttpSession available -> no mutex necessary
				mav = invokeHandlerMethod(request, response, handlerMethod);//执行handler的目标方法代码
			}
		}
		else {
            //自己的用例会直接到这儿
			// No synchronization on session demanded at all...
			mav = invokeHandlerMethod(request, response, handlerMethod);//执行handler的目标方法代码(核心),返回封装好请求域共享数据和视图名称的ModelAndView对象
		}
		if (!response.containsHeader(HEADER_CACHE_CONTROL)) {
			if (getSessionAttributesHandler(handlerMethod).hasSessionAttributes()) {
				applyCacheSeconds(response, this.cacheSecondsForSessionAttributeHandlers);
			}
			else {
				prepareResponse(response);
			}
		}
		return mav;//获取到mav后直接返回ModelAndView给handle方法，再返回给doDispatch方法，并用mv变量接收
	}
    ...
}
```

【**handleInternal方法中的invokeHandlerMethod方法解析**】

1. 对可执行方法对象设置参数解析器和返回值处理器
2. 调用可执行方法对象的invocableMethod.invokeAndHandle(webRequest, mavContainer);方法执行目标方法并返回封装好请求域共享数据和视图名称的ModelAndView对象，注意此时如果不是转发就还没有向请求域共享数据，是转发就已经把共享数据放在上下文对象中了

```java
public class RequestMappingHandlerAdapter extends AbstractHandlerMethodAdapter
		implements BeanFactoryAware, InitializingBean {
    ...
	@Nullable
	protected ModelAndView invokeHandlerMethod(HttpServletRequest request,
			HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {

        //注意，在这里构造的webRequest，构造webRequest的request和response是doDispatch传参传进来的
		ServletWebRequest webRequest = new ServletWebRequest(request, response);
		try {
			WebDataBinderFactory binderFactory = getDataBinderFactory(handlerMethod);
			ModelFactory modelFactory = getModelFactory(handlerMethod, binderFactory);

			ServletInvocableHandlerMethod invocableMethod = createInvocableHandlerMethod(handlerMethod);//在目标方法执行前会为invocableMethod可执行的方法对象中设置参数解析器argumentResolvers，可执行的方法对象就是控制器控制器中的目标方法，invocableMethod只是对handler又封装了一层;随即立即为可执行方法对象设置返回值处理器
			if (this.argumentResolvers != null) {//argumentResolvers参数解析器集合，里面有26个参数解析器，决定了目标方法中能写多少种参数类型
				invocableMethod.setHandlerMethodArgumentResolvers(this.argumentResolvers);//为可执行的方法对象 设置 参数解析器
			}
			if (this.returnValueHandlers != null) {//returnValueHandlers返回值处理器集合，里面有15中返回值处理器
				invocableMethod.setHandlerMethodReturnValueHandlers(this.returnValueHandlers);//为可执行方法对象设置返回值处理器
			}
            
			...非核心代码【注册线程池什么的】

			invocableMethod.invokeAndHandle(webRequest, mavContainer);//核心代码，这一步真正执行目标方法，到这儿可执行方法对象数据封装处理完毕，具体就是将向请求域共享的数据Model或者Map，以及通过控制器方法的返回值获取viewName都封装进mavContainer模型与视图容器对象中
			if (asyncManager.isConcurrentHandlingStarted()) {
				return null;
			}

			return getModelAndView(mavContainer, modelFactory, webRequest);//通过getModelAndView方法使用mavContainer对象获取ModelAndView对象，并将ModelAndView对象返回给handleInternal方法
		}
		finally {
			webRequest.requestCompleted();
		}
	}
    ...
}
```

【***invokeHandlerMethod方法中的invokeAndHandle方法解析***】

------

[^注意]: 这个方法比较核心，大的逻辑是：1.先通过invokeForRequest方法获取形参参数解析器，获取形参参数值数组，带着参数值数组调用doinvoke方法执行控制器方法并返回控制器方法的返回值给returnValue；2.再通过调用this.returnValueHandlers.handleReturnValue方法传参是开始处理控制器方法的返回结果

1. 进入invokeAndHandle方法后立即通过invokeForRequest方法去执行控制器目标方法

```java
public class ServletInvocableHandlerMethod extends InvocableHandlerMethod {
	public void invokeAndHandle(ServletWebRequest webRequest, ModelAndViewContainer mavContainer,
			Object... providedArgs) throws Exception {

		Object returnValue = invokeForRequest(webRequest, mavContainer, providedArgs);//执行处理当前请求，该方法就是去找参数解析器处理参数并调用控制器方法的目标方法，完事后得到控制器目标方法的返回值，这个returnValue就是我们希望控制器返回的值
        //上述方法重要参数值：
        //1.returnValue:控制器方法的返回值
        //2.mavContainer：里面有defaultModel，里面封装着用户想向请求域共享的数据
        //3.webRequest：传参webRequest向控制器方法提供原生的request和response对象
		
        setResponseStatus(webRequest);//设置响应状态，暂时不管

		if (returnValue == null) {//如果控制器方法返回null，则invokeAndHandle方法直接返回
			if (isRequestNotModified(webRequest) || getResponseStatus() != null || mavContainer.isRequestHandled()) {
				disableContentCachingIfNecessary(webRequest);
				mavContainer.setRequestHandled(true);
				return;
			}
		}
		else if (StringUtils.hasText(getResponseStatusReason())) {//这个检测返回值中有没有一些失败原因
			mavContainer.setRequestHandled(true);
			return;
		}
        
		//如果有返回值且返回值不是字符串，就会执行以下代码
		mavContainer.setRequestHandled(false);
		Assert.state(this.returnValueHandlers != null, "No return value handlers");
		try {
			this.returnValueHandlers.handleReturnValue(
					returnValue, getReturnValueType(returnValue), mavContainer, webRequest);//this.returnValueHandlers.handleReturnValue方法是开始处理控制器方法的返回结果，其实是把返回值的字符串或者其他东西处理赋值给mavContainer的view属性，getReturnValueType(returnValue)是获取返回值的类型，实际上获取的是HandlerMethod$ReturnValueMethodParameter类型的对象，其中的returnValue属性保存的是返回值，executable属性指向的对象的name属性为对应控制的方法名，returnType保存的是返回值类型，这一段代码就是处理返回值的核心代码
		}
		catch (Exception ex) {
			if (logger.isTraceEnabled()) {
				logger.trace(formatErrorForReturnValue(returnValue), ex);
			}
			throw ex;
		}
	}
}
```

【***invokeAndHandle方法中的invokeForRequest方法解析***】

------

------

1. 核心1：通过getMethodArgumentValues方法去获取参数解析器，使用参数解析器获取形参参数值打包成Object数组

2. 核心2：带着所有形参参数通过doInvoke(args)方法去执行控制器方法，具体用反射调用目标方法的过程不管，重点是执行完以后怎么办，显然这里return的就是控制器方法的返回值返回给invokeAndHandle方法并赋值给returnValue

```java
public class InvocableHandlerMethod extends HandlerMethod {
	...
	@Nullable
	public Object invokeForRequest(NativeWebRequest request, @Nullable ModelAndViewContainer mavContainer,
			Object... providedArgs) throws Exception {

		Object[] args = getMethodArgumentValues(request, mavContainer, providedArgs);//获取目标方法所有参数的值，封装成Object数组，有且只封装形参中有的参数值，每个元素代表一个对应的参数对象
		if (logger.isTraceEnabled()) {
			logger.trace("Arguments: " + Arrays.toString(args));
		}
		return doInvoke(args);//这一步就是利用反射机制调用目标方法，这个方法里面第一步就是调用的反射工具类CoroutinesUtils，具体执行先不管
	}
	...
}
```

【***invokeForRequest方法的getMethodArgumentValues方法解析***】

1. 以下代码就是如何确定每一个目标参数的值的代码
   + 小重点1：判断和寻找每个参数的参数解析器
   + 小重点2：解析参数的参数值

2. 核心就是获取目标方法的所有参数信息数组，对参数使用增强for循环；对每个参数都使用参数解析器集合的参数支持判断方法对每一个参数解析器循环遍历调用相应的参数支持判断方法根据注解类型和参数类型匹配参数解析器并将参数解析器放在参数解析器缓存中；然后对每个参数使用参数解析器集合中的解析参数方法直接从缓存中获取参数解析器，如果获取不到再去遍历所有的参数解析器[**一层通用行为**]，再通过参数解析器的解析参数方法一般都从请求域中拿数据(可能从请求域中直接拿，也可能从请求域中封装好的Map里面来，具体要看每种参数解析器的实现)[**参数解析器的个体行为**]

```java
public class InvocableHandlerMethod extends HandlerMethod {
	protected Object[] getMethodArgumentValues(NativeWebRequest request, @Nullable ModelAndViewContainer mavContainer,
			Object... providedArgs) throws Exception {

		MethodParameter[] parameters = getMethodParameters();//获取目标方法的所有形式参数的详细信息，包括形式参数的注解[combinedAnnotation]、索引位置[parameterIndex]、参数类型[ParameterType]等
		if (ObjectUtils.isEmpty(parameters)) {//判断目标方法参数列表是否为空
			return EMPTY_ARGS;//如果参数列表为空直接返回
		}

        //如果有参数列表
		Object[] args = new Object[parameters.length];//创建一个长度为参数个数的Object数组args
		for (int i = 0; i < parameters.length; i++) {//遍历参数
			MethodParameter parameter = parameters[i];//拿到第i个参数，即形参列表中的第i+1个参数
			parameter.initParameterNameDiscovery(this.parameterNameDiscoverer);//初始化过程不管，找参数名字的发现器来发现对应参数的名字
			args[i] = findProvidedArgument(parameter, providedArgs);//为args[i]赋值，赋值过程不管
			if (args[i] != null) {
				continue;
			}
            
            //重点1
			if (!this.resolvers.supportsParameter(parameter)) {//解析前先判断当前解析器是否支持当前参数类型，这个resovlers就是argumentResolvers参数解析器集合，内含26个参数参数解析器，这里面找到了参数解析器会把参数解析器存入参数解析器缓存中
				throw new IllegalStateException(formatArgumentError(parameter, "No suitable resolver"));
			}
			try {
                
                //重点2
				args[i] = this.resolvers.resolveArgument(parameter, mavContainer, request, this.dataBinderFactory);//核心:解析当前参数的参数值
			}
			catch (Exception ex) {
				// Leave stack trace for later, exception may actually be resolved and handled...
				if (logger.isDebugEnabled()) {
					String exMsg = ex.getMessage();
					if (exMsg != null && !exMsg.contains(parameter.getExecutable().toGenericString())) {
						logger.debug(formatArgumentError(parameter, exMsg));
					}
				}
				throw ex;
			}
		}
		return args;//返回args
	}
}
```

【**小重点1：以下是判断和寻找每个参数的参数解析器的过程解析**】

------

------

------

【getMethodArgumentValues方法中的resolvers.supportsParameter方法解析】

1. 重点：形参对应的参数解析器缓存机制，第一次请求去挨个遍历26个参数解析器，速度慢；找到以后就以参数作为key，参数解析器作为value存入参数解析器缓存argumentResolverCache，以后请求找参数解析器都走缓存

```java
public class HandlerMethodArgumentResolverComposite implements HandlerMethodArgumentResolver {
    private final List<HandlerMethodArgumentResolver> argumentResolvers = new ArrayList();
    private final Map<MethodParameter, HandlerMethodArgumentResolver> argumentResolverCache = new ConcurrentHashMap(256);
    
    ...
        
    public boolean supportsParameter(MethodParameter parameter) {
        return this.getArgumentResolver(parameter) != null;//核心方法，在supportsParameter方法中调用同一个类的getArgumentResolver方法来获取支持Parameter这个参数解析的参数解析器，如果有就返回true
    }

    @Nullable
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        ...
    }
	
    //【supportsParameter方法中的this.getArgumentResolver方法】
    @Nullable
	private HandlerMethodArgumentResolver getArgumentResolver(MethodParameter parameter) {
        //先从argumentResolverCache参数解析器缓存[map集合]中获取参数，但第一次请求访问缓存中是空的，会直接进入if中执行代码【woc我的argumentResolverCache不为null，没有进入for增强，直接返回result对应的参数解析器，是因为新版本改了吗？卧槽懂了，通透：因为每个参数都要遍历，很耗时间，所以只要访问了一次，就会把结果存入缓存，以后对应参数都直接拿着参数从缓存argumentResolverCache拿】
		HandlerMethodArgumentResolver result = this.argumentResolverCache.get(parameter);//通过key即参数获取对应的参数解析器result
        //那缓存不为空没有判断参数解析器是否支持是个什么情况？第一次对应请求执行以后缓存就有了，以后不判断直接走缓存
		if (result == null) {
			for (HandlerMethodArgumentResolver resolver : this.argumentResolvers) {//增强for循环遍历所有参数解析器，挨个调用参数解析器的supportsParameter方法确定26个参数解析器谁能解析传入的第i个参数，判断原理更参数解析器各不相同，一般都是判断注解类型，有些会判断参数类型并做一些额外动作
				if (resolver.supportsParameter(parameter)) {//核心resolver.supportsParameter(parameter)方法判断参数是否支持，不支持继续判断下一个参数解析器
					result = resolver;//支持就把对应的参数解析器放入result中存入参数解析器缓存中并跳出第一个参数的寻找参数解析器循环
					this.argumentResolverCache.put(parameter, result);
					break;
				}
			}
		}
		return result;//然后返回支持第一个参数解析的参数解析器给supportsParameter方法
	}
}
```

【**getArgumentResolver方法中的resolver.supportsParameter(parameter)方法解析**】

```java
public class RequestParamMethodArgumentResolver extends AbstractNamedValueMethodArgumentResolver
		implements UriComponentsContributor {
	...
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		if (parameter.hasParameterAnnotation(RequestParam.class)) {//判断传入的参数有没有包含@RequestParam注解，没有标注该注解会直接return false，然后回到上一个方法的for增强选择下一个参数解析器RequestParamMapMethodArgumentResolver的supportsParameter方法继续判断，不同参数解析器的判断方法不同，有些都直接在supportsParameter方法就把判断的事情搞定了，一般都是判断参数上是否标注了对应的请求参数基本注解，有些还要判断特定的参数类型并做一些额外动作，其他参数解析器的对应supportsParameter方法就不看了
			if (Map.class.isAssignableFrom(parameter.nestedIfOptional().getNestedParameterType())) {
				RequestParam requestParam = parameter.getParameterAnnotation(RequestParam.class);
				return (requestParam != null && StringUtils.hasText(requestParam.name()));
			}
			else {
				return true;
			}
		}
		else {
			if (parameter.hasParameterAnnotation(RequestPart.class)) {//判断传入的参数有没有包含@RequestPart注解，有就直接返回false
				return false;
			}
			parameter = parameter.nestedIfOptional();
			if (MultipartResolutionDelegate.isMultipartArgument(parameter)) {
				return true;
			}
			else if (this.useDefaultResolution) {
				return BeanUtils.isSimpleProperty(parameter.getNestedParameterType());
			}
			else {
				return false;
			}
		}
	}
	...
}
```

【**小重点2：以下是解析当前参数的参数值的解析过程**】

------

------

------

【***getMethodArgumentValues方法中的resolvers.resolveArgument方法解析***】

```java
public class HandlerMethodArgumentResolverComposite implements HandlerMethodArgumentResolver {
	@Override
	@Nullable
	public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {//注意，该方法中的webRequest中封装了原生的request和response对象
		HandlerMethodArgumentResolver resolver = getArgumentResolver(parameter);//拿到当前参数的参数解析器，先尝试从缓存拿，拿不到就去遍历参数解析器集合再次放缓存并返回参数解析器，正常情况下在判断解析器的步骤中已经遍历了，这里可以直接从缓存拿
		if (resolver == null) {
			throw new IllegalArgumentException("Unsupported parameter type [" +
					parameter.getParameterType().getName() + "]. supportsParameter should be called first.");
		}
        
        //核心
		return resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);//调用参数解析器的resolveArgument方法进行解析
	}
}
```

【resolveArgument方法中的resolver.resolveArgument方法解析】

```java
public abstract class AbstractNamedValueMethodArgumentResolver implements HandlerMethodArgumentResolver {	
	@Override
	@Nullable
	public final Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {

		NamedValueInfo namedValueInfo = getNamedValueInfo(parameter);//获取参数信息，包含参数的名字，此时参数还没有值
		MethodParameter nestedParameter = parameter.nestedIfOptional();

		Object resolvedName = resolveEmbeddedValuesAndExpressions(namedValueInfo.name);//这个方法能解析出括号中参数的名字，该名字为获取参数值做准备
		if (resolvedName == null) {
			throw new IllegalArgumentException(
					"Specified name must not resolve to null: [" + namedValueInfo.name + "]");
		}

        //核心
		Object arg = resolveName(resolvedName.toString(), nestedParameter, webRequest);//通过这个方法解析括号中 参数的值，参数解析器从请求路径按照正则匹配的方式拿到参数的值，实际是urlPathHelper提前解析所有的数据封装成map集合uriTemplateVars后续都是拿着参数名直接从Map集合里面取；路径变量是走uriTemplateVars；某个请求头数据如User-Agent是以数组的形式直接放在请求域中，没有经过Map封装，直接在请求头方法参数解析器中通过请求对象获取相应变量值数组，如果数组长度为1则返回第一个元素，如果数组长度大于1就返回整个数组，如果数组长度为0则返回null
		...额外操作代码暂时不管
		return arg;
	}
}
```

【**resolveArgument方法中的resolveName方法解析**】

```java
public class PathVariableMethodArgumentResolver extends AbstractNamedValueMethodArgumentResolver
		implements UriComponentsContributor {
	@Override
	@SuppressWarnings("unchecked")
	@Nullable
	protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
		Map<String, String> uriTemplateVars = (Map<String, String>) request.getAttribute(
				HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);//uriTemplateVars中的数据id=2，username=zhangsan，这里面的数据是由请求一进来就被UrlPathHelper提前解析出来并将数据封装在uriTemplateVars并提前保存在请求域中
		return (uriTemplateVars != null ? uriTemplateVars.get(name) : null);//直接在uriTemplateVars这个Map集合中通过参数的名字获取对应的值
	}
}
```

【***invokeAndHandle方法中的this.returnValueHandlers.handleReturnValue方法解析***】

------

------


```java
public class HandlerMethodReturnValueHandlerComposite implements HandlerMethodReturnValueHandler {
	@Override
	public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType,ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
		HandlerMethodReturnValueHandler handler = selectHandler(returnValue, returnType);//调用selectHandler方法根据返回值类型找到合适的返回值处理器并赋值给handler
		if (handler == null) {
			throw new IllegalArgumentException("Unknown return value type: " + returnType.getParameterType().getName());
		}
		handler.handleReturnValue(returnValue, returnType, mavContainer, webRequest);//核心：处理器对象调用对应的handleReturnValue方法处理控制器方法的返回值
	}
}
```

【**handleReturnValue方法中的handler.handleReturnValue方法**】

```java
public class ViewNameMethodReturnValueHandler implements HandlerMethodReturnValueHandler {
	@Override
	public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType,
			ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {

		if (returnValue instanceof CharSequence) {//如果返回值是一个字符串，注意此时是一个Object
			String viewName = returnValue.toString();//拿到返回值的字符串并存入viewName
			mavContainer.setViewName(viewName);//将字符串返回值存入mavContainer对象的viewName属性中，mavContainer翻译过来就是模型和视图的容器，此时mavContainer对象中既有向请求域中共享的数据，也有了viewName
			if (isRedirectViewName(viewName)) {//判断view是否是重定向的视图，使用startWith方法判断视图名是否以redirect:开始的；或者调用simpleMatch方法判断视图名是否匹配重定向路径
				mavContainer.setRedirectModelScenario(true);//如果viewName是重定向视图名就把mavContainer对象的redirectModelScenario属性设置为true，这个后面再说
			}
		}
		else if (returnValue != null) {
			// should not happen
			throw new UnsupportedOperationException("Unexpected return type: " +
					returnType.getParameterType().getName() + " in method: " + returnType.getMethod());
		}
	}
}
```

【**invokeHandlerMethod方法中的getModelAndView方法解析**】

------

1. 共享数据和视图名称都放在了ModelAndViewContainer类型的mavContainer对象中，包含要去的页面地址View和请求域共享的Model数据，以下的代码就是对mavContainer对象中的相关数据进行处理

![mavContainer](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\mavContainer.png)

```java
public class RequestMappingHandlerAdapter extends AbstractHandlerMethodAdapter
		implements BeanFactoryAware, InitializingBean {
	@Nullable
	private ModelAndView getModelAndView(ModelAndViewContainer mavContainer,
			ModelFactory modelFactory, NativeWebRequest webRequest) throws Exception {

		modelFactory.updateModel(webRequest, mavContainer);//modelFactory模型工厂，updateModel方法的作用是更新Model
		if (mavContainer.isRequestHandled()) {
			return null;
		}
		ModelMap model = mavContainer.getModel();//还是获取defaultModel，注意getModel这个方法内部是有个判断的，如果是重定向视图的话，就重新new了一个model返回给你，如果是转发的话，返回的model就是mavContainer里的那个defaultModel，也就是含user的；是否使用默认的defaultModel是根据!this.redirectModelScenario || (this.redirectModel == null && !this.ignoreDefaultModelOnRedirect这三个属性值综合判断的
		ModelAndView mav = new ModelAndView(mavContainer.getViewName(), model, mavContainer.getStatus());//将defaultModel(如果是重定向视图是上一步新建的model)、viewName还有状态码传入ModelAndView对象的有参构造创建ModelAndView对象
		if (!mavContainer.isViewReference()) {
			mav.setView((View) mavContainer.getView());
		}
		if (model instanceof RedirectAttributes) {//如果Model是重定向携带数据，使用putAll方法把所有数据放到请求的上下文中，形参不仅能写Model类型；还可以写RedirectAttributes类型，RedirectAttributes类型用于重定向携带数据，这个具体操作后面再说，简言之就是把重定向携带数据想办法搞到重定向请求中
			Map<String, ?> flashAttributes = ((RedirectAttributes) model).getFlashAttributes();
			HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
			if (request != null) {
				RequestContextUtils.getOutputFlashMap(request).putAll(flashAttributes);
			}
		}
		return mav;
	}
}
```

【**getModelAndView方法中的updateModel方法解析**】

```java
public final class ModelFactory {
    ...
	public void updateModel(NativeWebRequest request, ModelAndViewContainer container) throws Exception {
		ModelMap defaultModel = container.getDefaultModel();//从mavContainer中拿到defaultModel
		if (container.getSessionStatus().isComplete()){
			this.sessionAttributesHandler.cleanupAttributes(request);
		}
		else {
			this.sessionAttributesHandler.storeAttributes(request, defaultModel);
		}
		if (!container.isRequestHandled() && container.getModel() == defaultModel) {
			updateBindingResult(request, defaultModel);//调用updateBindingResult更新最终的绑定结果
		}
	}
    //被上述updateModel方法调用
    private void updateBindingResult(NativeWebRequest request, ModelMap model) throws Exception {
		List<String> keyNames = new ArrayList<>(model.keySet());//从defaultModel中拿到所有的键值对的key，封装成List集合
		for (String name : keyNames) {//对所有共享数据的key遍历
			Object value = model.get(name);//通过key拿到defaultModel中对应的value
			if (value != null && isBindingCandidate(name, value)) {//下面好像是设置绑定策略的，暂时不管
				String bindingResultKey = BindingResult.MODEL_KEY_PREFIX + name;
				if (!model.containsAttribute(bindingResultKey)) {
					WebDataBinder dataBinder = this.dataBinderFactory.createBinder(request, value, name);
					model.put(bindingResultKey, dataBinder.getBindingResult());
				}
			}
		}
	}
    ...
}
```

##### 重点4：processDispatchResult(processedRequest, response, mappedHandler, mv, dispatchException)源码解析

> 在该方法中把Model中的数据放到请求域中，核心是在最后渲染那一步才把model中的数据封装到LinkedHashMap类型的mergedMap对象中，在究极嵌套的render方法的view.render方法中的最后一个方法renderMergedOutputModel方法中的exposeModelAsRequestAttributes方法中放入请求域的

【doDispatch方法中的processDispatchResult方法解析】

```java
public class DispatcherServlet extends FrameworkServlet {
    ...
	private void processDispatchResult(HttpServletRequest request, HttpServletResponse response,
			@Nullable HandlerExecutionChain mappedHandler, @Nullable ModelAndView mv,
			@Nullable Exception exception) throws Exception {

		boolean errorView = false;//判断整个处理期间有没有失败，为什么能够判断后面再说

        //这里都是异常相关的不管
		if (exception != null) {
			if (exception instanceof ModelAndViewDefiningException) {
				logger.debug("ModelAndViewDefiningException encountered", exception);
				mv = ((ModelAndViewDefiningException) exception).getModelAndView();
			}
			else {
				Object handler = (mappedHandler != null ? mappedHandler.getHandler() : null);
				mv = processHandlerException(request, response, handler, exception);
				errorView = (mv != null);
			}
		}

		// Did the handler return a view to render?
		if (mv != null && !mv.wasCleared()) {//mv就是ModelAndView，里面的model被处理成mav时重新封装成ModelMap，保存的值还是共享数据键值对；ModelAndView不为空且ModelAndView没有被清理过
            
            //小重点1：使用render方法开始渲染要去哪个页面
            //ModelAndView不为空且没有被清理过就调用render方法进行视图渲染
			render(mv, request, response);
			
            if (errorView) {
				WebUtils.clearErrorRequestAttributes(request);
			}
		}
		else {
			if (logger.isTraceEnabled()) {
				logger.trace("No view rendering, null ModelAndView returned.");
			}
		}
		if (WebAsyncUtils.getAsyncManager(request).isConcurrentHandlingStarted()) {
			// Concurrent handling started during a forward
			return;
		}
		if (mappedHandler != null) {
			// Exception (if any) is already handled..
			mappedHandler.triggerAfterCompletion(request, response, null);
		}
	}
    ...
}
```

【**小重点1：processDispatchResult方法中的render方法**】

```java
public class DispatcherServlet extends FrameworkServlet {
	protected void render(ModelAndView mv, HttpServletRequest request, HttpServletResponse response) throws Exception {
		// Determine locale for request and apply it to the response.进行国际化的暂时不管
		Locale locale =
				(this.localeResolver != null ? this.localeResolver.resolveLocale(request) : request.getLocale());
		response.setLocale(locale);

		View view;
		String viewName = mv.getViewName();//从ModelAndView中获取视图名，就是控制器方法返回的字符串，一字不差
		if (viewName != null) {
			// We need to resolve the view name.
			view = resolveViewName(viewName, mv.getModelInternal(), locale, request);//这一步解析视图，mv.getModelInternal()会把Model中的数据全部拿出来放到一个Map中，但是这个Map在该方法中没用上啊，这一步的目的是获取最佳的视图对象，得到视图对象是为了调用其render方法
			if (view == null) {
				throw new ServletException("Could not resolve view with name '" + mv.getViewName() +
						"' in servlet with name '" + getServletName() + "'");
			}
		}
		else {
			// No need to lookup: the ModelAndView object contains the actual View object.
			view = mv.getView();
			if (view == null) {
				throw new ServletException("ModelAndView [" + mv + "] neither contains a view name nor a " +
						"View object in servlet with name '" + getServletName() + "'");
			}
		}

		// Delegate to the View object for rendering.
		if (logger.isTraceEnabled()) {
			logger.trace("Rendering view [" + view + "] ");
		}
		try {
			if (mv.getStatus() != null) {
				request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, mv.getStatus());
				response.setStatus(mv.getStatus().value());
			}
            //核心代码：render方法中的view.render方法：作用是渲染目标页面，拿到页面数据
			view.render(mv.getModelInternal(), request, response);
		}
		catch (Exception ex) {
			if (logger.isDebugEnabled()) {
				logger.debug("Error rendering view [" + view + "]", ex);
			}
			throw ex;
		}
	}
}
```

【render方法中的resolveViewName方法解析】

------

------

【所有的视图解析器】

![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\视图解析器.png)

```java
public class DispatcherServlet extends FrameworkServlet {
	@Nullable
	protected View resolveViewName(String viewName, @Nullable Map<String, Object> model,
			Locale locale, HttpServletRequest request) throws Exception {

		if (this.viewResolvers != null) {//视图解析器，获取所有的视图解析器
			for (ViewResolver viewResolver : this.viewResolvers) {//遍历视图解析器
				View view = viewResolver.resolveViewName(viewName, locale);//核心方法，获取到最佳匹配的视图并返回给render方法
				if (view != null) {
					return view;
				}
			}
		}
		return null;
	}
}
```

【resolveViewName方法中的viewResolver.resolveViewName(viewName, locale)方法解析】

```java
public class ContentNegotiatingViewResolver extends WebApplicationObjectSupport
		implements ViewResolver, Ordered, InitializingBean {
	@Override
	@Nullable
	public View resolveViewName(String viewName, Locale locale) throws Exception {
		RequestAttributes attrs = RequestContextHolder.getRequestAttributes();//RequestContextHolder.getRequestAttributes()拿到所有请求域中的属性，这个东西暂时和Model没啥关系
		Assert.state(attrs instanceof ServletRequestAttributes, "No current ServletRequestAttributes");
		List<MediaType> requestedMediaTypes = getMediaTypes(((ServletRequestAttributes) attrs).getRequest());//这一步获取客户端能够接收的所有媒体内容类型
		if (requestedMediaTypes != null) {
			List<View> candidateViews = getCandidateViews(viewName, locale, requestedMediaTypes);//获取候选的视图，不知道是啥意思，反正得到了两个RedirectView，感觉都指向main.html，注意：这个getCandidateViews是视图解析器ContentNegotiatingViewResolver中的方法，这个方法中又对其他四种视图解析器进行了遍历
			View bestView = getBestView(candidateViews, requestedMediaTypes, attrs);//获取到最好的视图?按内容协商筛选最佳匹配？这儿讲的好水
			if (bestView != null) {
				return bestView;//返回这个视图
			}
		}
		...无关代码
	}
}
```

【resolveViewName方法中的getCandidateViews方法】

```java
public class ContentNegotiatingViewResolver extends WebApplicationObjectSupport
		implements ViewResolver, Ordered, InitializingBean {
	private List<View> getCandidateViews(String viewName, Locale locale, List<MediaType> requestedMediaTypes)
			throws Exception {

		List<View> candidateViews = new ArrayList<>();
		if (this.viewResolvers != null) {//内容协商视图解析器中有一个视图解析器集合，视图解析器本来有5个 第一个是内容协商视图解析器，这里面没有内容协商视图解析器，所以只有四个
			Assert.state(this.contentNegotiationManager != null, "No ContentNegotiationManager set");
			for (ViewResolver viewResolver : this.viewResolvers) {//第一个是BeanNameViewResolver，对应的resolveViewName的逻辑是拿到IoC容器，判断容器中有没有对应viewName的组件组件名与返回值字符串完全相同，如果有就会用BeanNameViewResolver解析；
                //第二个是ThymeleafViewResolver，这个是Thymeleaf模板引擎的视图解析器，这个视图解析器判断ViewName是不是以redirect：开始的， 如果是就截取redirect：后面的内容，这里是/main.html，通过这个/main.html直接创建RedirectView对象【很诡异，在视图解析器中调用视图解析器创建视图View对象】；否则如果是forward:开始的，会通过控制器方法的返回值除去forward:创建InternalResourceView【相当于thymeleaft拦截了渲染的过程，将返回结果用thymeleaft的语法进行了渲染。】；如果既不是redirect:也不是forward:，就会调用loadView方法来进行页面加载，先拿到IoC工厂，根据ViewName判断有没有对应的组件存在，如果不存在会利用模板引擎自己创建一个ThymeleafView并设置一些属性
                //第三个是
				View view = viewResolver.resolveViewName(viewName, locale);//得到这个view对象是进行内容匹配用的，暂时不管
				if (view != null) {
					candidateViews.add(view);
				}
				for (MediaType requestedMediaType : requestedMediaTypes) {//进行内容类型匹配
					List<String> extensions = this.contentNegotiationManager.resolveFileExtensions(requestedMediaType);
					for (String extension : extensions) {
						String viewNameWithExtension = viewName + '.' + extension;
						view = viewResolver.resolveViewName(viewNameWithExtension, locale);
						if (view != null) {
							candidateViews.add(view);
						}
					}
				}
			}
		}
		if (!CollectionUtils.isEmpty(this.defaultViews)) {
			candidateViews.addAll(this.defaultViews);
		}
		return candidateViews;//直接返回匹配的视图List集合
	}
}
```

【render方法中的view.render方法解析】

------

------

```java
public abstract class AbstractView extends WebApplicationObjectSupport implements View, BeanNameAware {
	@Override
	public void render(@Nullable Map<String, ?> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("View " + formatViewName() +
					", model " + (model != null ? model : Collections.emptyMap()) +
					(this.staticAttributes.isEmpty() ? "" : ", static attributes " + this.staticAttributes));
		}

		Map<String, Object> mergedModel = createMergedOutputModel(model, request, response);//创建一个要合并输出的Model，传参的model还是存的老数据，还是ModelMap ，此外还传参了request和response，这一步的目的就是用把model中的数据再封装一层转移到mergedModel中，不知道这么做的意图是什么
        
		prepareResponse(request, response);//准备响应
		renderMergedOutputModel(mergedModel, getRequestToExpose(request), response);//核心方法：渲染合并输出的模型数据，传参再次封装的mergedModel，这一步就是把Model中的数据放在请求域中，参数中getRequestToExpose(request)获取的是原生的request
	}
    
    //【render方法中的createMergedOutputModel方法】
    protected Map<String, Object> createMergedOutputModel(@Nullable Map<String, ?> model,
			HttpServletRequest request, HttpServletResponse response) {

		@SuppressWarnings("unchecked")
		Map<String, Object> pathVars = (this.exposePathVariables ?
				(Map<String, Object>) request.getAttribute(View.PATH_VARIABLES) : null);

		// Consolidate static and dynamic model attributes.
		int size = this.staticAttributes.size();
		size += (model != null ? model.size() : 0);
		size += (pathVars != null ? pathVars.size() : 0);

		Map<String, Object> mergedModel = CollectionUtils.newLinkedHashMap(size);//新建一个LinkedHashMap类型的mergeModel对象
		mergedModel.putAll(this.staticAttributes);
		if (pathVars != null) {
			mergedModel.putAll(pathVars);
		}
		if (model != null) {
			mergedModel.putAll(model);//如果model不为空，即用户向model或者Map中放了共享数据，就把所有的数据转移放到mergeModel中
		}

		// Expose RequestContext?
		if (this.requestContextAttribute != null) {
			mergedModel.put(this.requestContextAttribute, createRequestContext(request, response, mergedModel));
		}

		return mergedModel;//将封装了共享数据的mergeModel返回给render方法
	}
}
```

【view.render方法中的renderMergedOutputModel方法解析】

```java
public class InternalResourceView extends AbstractUrlBasedView {//InternalResourceView属于视图解析流程
    
    //renderMergedOutputModel这个方法就是视图解析InternalResourceView类中的核心方法
	@Override
	protected void renderMergedOutputModel(
			Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		// Expose the model object as request attributes.
		exposeModelAsRequestAttributes(model, request);//暴露model作为请求域的属性，这个就是把Model中的数据放入请求域的核心方法

		// Expose helpers as request attributes, if any.
		exposeHelpers(request);

		// Determine the path for the request dispatcher.
		String dispatcherPath = prepareForRendering(request, response);

		// Obtain a RequestDispatcher for the target resource (typically a JSP).
		RequestDispatcher rd = getRequestDispatcher(request, dispatcherPath);
		if (rd == null) {
			throw new ServletException("Could not get RequestDispatcher for [" + getUrl() +
					"]: Check that the corresponding file exists within your web application archive!");
		}

		// If already included or response already committed, perform include, else forward.
		if (useInclude(request, response)) {
			response.setContentType(getContentType());
			if (logger.isDebugEnabled()) {
				logger.debug("Including [" + getUrl() + "]");
			}
			rd.include(request, response);
		}

		else {
			// Note: The forwarded resource is supposed to determine the content type itself.
			if (logger.isDebugEnabled()) {
				logger.debug("Forwarding to [" + getUrl() + "]");
			}
			rd.forward(request, response);
		}
	}
}
```

【renderMergedOutputModel方法中的exposeModelAsRequestAttributes方法】

```java
public abstract class AbstractView extends WebApplicationObjectSupport implements View, BeanNameAware {
	protected void exposeModelAsRequestAttributes(Map<String, Object> model,
			HttpServletRequest request) throws Exception {

        //逻辑很简单，就是直接取所有的key和value，然后使用原生request的setAttribute挨个遍历放到请求域中，注意这个model对象是最后封装的LinkedHashMap类型的mergedMap对象
		model.forEach((name, value) -> {//学到了对Map集合的流式编程，直接用key和value
			if (value != null) {
				request.setAttribute(name, value);
			}
			else {
				request.removeAttribute(name);
			}
		});
	}
}
```



#### 02、Web开发核心对象

##### HandlerMapping【**处理器映射**】

![HandlerMappings](C:/Users/Earl/Desktop/SpringBoot2学习笔记/image/20210205005802305.png)

1. 【/user—GET请求】下的HandlerMapping，请求处理规则【即/xxx请求对应处理器】都被保存在HandlerMapping中，默认有五个HandlerMapping

2. WelcomePageHandlerMapping欢迎页的处理器映射，老熟人了

   + WebMvcAutoConfiguration中的welcomePageHandlerMapping通过spring.mvc.static-path-pattern属性创建welcomePageHandlerMapping组件并配置到容器中

   + WelcomePageHandlerMapping中的pathMatcher路径匹配保存的是"/",意思是请求直接访问"/"会被对应到rootHandler中的view="forward:index.html"，这就是HandleMapping中保存的映射规则

3. RequestMappingHandlerMapping【@RequestMapping注解的所有处理器映射】

   + RequestMappingHandlerMapping保存了所有@RequestMapping注解和handler的映射规则，也由配置类WebMvcAutoConfiguration对该组件进行配置

   + 应用一启动，SpringMVC自动扫描所有的控制器组件并解析@RequestMapping注解，把所有注解信息保存在RequestMappingHandlerMapping中

   + RequestMappingHandlerMapping中有一个mappingRegistry属性【映射的注册中心】，其下的mappingLookup属性中用HashMap保存了用户所有自定义路径以及对应的处理器以及系统自带的两个错误处理映射

     ![mappingRegistry属性【映射的注册中心】](C:/Users/Earl/Desktop/SpringBoot2学习笔记/image/20210205005926474.png)

[^备注]: 其他处理器映射后面再说



##### HandlerAdapter【处理器适配器】

> 处理器就是一个大的反射工具，可以使用反射机制执行用户的控制器方法并且调用参数解析器为控制器方法的参数赋值

![在这里插入图片描述](C:/Users/Earl/Desktop/SpringBoot2学习笔记/image/20210205010047654.png)

1. RequestMappingHandlerAdapter：支持控制器方法上标注@RequestMapping注解的Handler的适配器

2. HandlerFunctionAdapter：支持控制器函数式编程的方法【后面了解】

   [^注意]: 适配器默认以上4种，上述两种用的是最多的

   

##### ArgumentResolver【参数解析器】

1. 在处理器适配器RequestMappingHandlerAdapter中有argumentResolvers属性，里面存储了26个参数解析器ArgumentResolver(数量不一定，我这里测试有27个，版本也不同)，每个参数解析器都实现了HandlerMethodArgumentResolver接口

   ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\参数解析器集合.png)

2. 这些参数解析器的作用是自动设置将要执行的目标方法中形参的具体参数值

3. 具体的参数解析器

   + RequestParamMethodArgumentResolver：解析标注@Requestparam注解请求参数的方法参数解析器
   + PathVariableMethodArgumentResolver：解析标注@PathVariable注解的请求参数
   + ...

   > SpringMVC目标方法中能写多少中参数类型取决于参数解析器

4. 参数解析器的源码

   + 由源码可知参数解析器的工作流程
     + 步骤1：判断当前解析器是否支持传入的当前参数
     + 步骤2：如果支持当前参数的解析就调用resolveArgument方法来进行解析

   【参数解析器作为处理器适配器的属性】

   ```java
   public class RequestMappingHandlerAdapter extends AbstractHandlerMethodAdapter
   		implements BeanFactoryAware, InitializingBean {
   	...
   	@Nullable
   	private HandlerMethodArgumentResolverComposite argumentResolvers;
   	...
   }
   ```

   【参数解析器的设计源码】

   有二十几个参数解析器，针对不同的参数注解和类型都有不同的实现，执行流程和代码原理在【重点3：ha.handle方法源码解析】中已经讲的很清楚了，去看就完事了

   ```java
   public class HandlerMethodArgumentResolverComposite implements HandlerMethodArgumentResolver {
       ...
   }
   ```

   【参数解析器实现的接口HandlerMethodArgumentResolver】

   ```java
   public interface HandlerMethodArgumentResolver {
   
   	boolean supportsParameter(MethodParameter parameter);//判断当前参数解析器是否支持这种参数
   
   	@Nullable
   	Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
   			NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception;//如果当前参数解析器支持这种参数就调用resolveArgument方法对该参数进行解析
       
   }
   ```

   

##### ReturnValueHandler【返回值处理器】

1. 在处理器适配器RequestMappingHandlerAdapter中有returnValueHandlers属性，里面存储了15个返回值处理器ReturnValueHandler(我这里测试有15个，版本不同)【在可执行方法中也有returnValueHandlers属性】

   ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\返回值处理器.png)

2. 这些返回值处理器决定了控制器方法的返回值类型种类(注意非形参)

   + 如返回ModelAndView、Model、View、返回值可以使用@ResponseBody注解、HttpEntity等等
   + SpringMVC支持的返回值类型【根据返回值处理器的顺序依次向下】
     + ModelAndView
     + Model
     + View
     + ResponseEntity
     + ResponseBodyEmitter
     + SreamingResponseBody【返回值类型是不是流式数据的，这是一个函数式接口】 
     + HttpEntity【且返回值类型不能是RequestEntity】
     + HttpHeaders
     + Callable【判断是否异步的，将JUC的时候会讲】
     + DeferredResult【支持异步的】、ListenableFuture、CompletionStage【这三个都是被SpringMVC包装了的一些异步返回方式】
     + WebAsnyTask
     + 控制器方法上有@ModelAttribute注解标注的对应返回值【注意使用了这种注解还会判断返回值不是简单类型如字符串，必须是对象】
     + 控制器方法上有@ResponseBody注解标注的对应返回值
       + 【相应返回值处理器：RequestResponseBodyMethodProcessor】，这个返回值处理器就能处理响应json格式的数据

3. 返回值处理器都继承了接口HandlerMethodReturnValueHandler

   ```java
   public interface HandlerMethodReturnValueHandler {
   
       //1.返回值处理器调用supportsReturnType方法判断是否支持当前类型返回值returnType
   	boolean supportsReturnType(MethodParameter returnType);
   
       //2.返回值处理器调用handleReturnValue方法对该返回值进行处理
   	void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType,
   			ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception;
   }
   ```

4. 一些返回值处理器既可以作为返回值处理流程中的返回值处理器，也可以作为参数解析器

   > 以返回值处理器RequestResponseBodyMethodProcessor为例

   ```java
   /**
    * Resolves method arguments annotated with {@code @RequestBody}(解析标注特定注解的方法参数) and handles return
    * values from methods annotated with {@code @ResponseBody}(处理标注特定注解的方法的返回值) by reading and writing
    * to the body of the request(通过读请求体) or response with an {@link HttpMessageConverter}(或者通过HttpMessageConverter来响应).
    *
    * <p>An {@code @RequestBody} method argument is also validated if it is annotated
    * with any
    * {@linkplain org.springframework.validation.annotation.ValidationAnnotationUtils#determineValidationHints
    * annotations that trigger validation}. In case of validation failure,
    * {@link MethodArgumentNotValidException} is raised and results in an HTTP 400
    * response status code if {@link DefaultHandlerExceptionResolver} is configured.
    */
   public class RequestResponseBodyMethodProcessor extends AbstractMessageConverterMethodProcessor {
       ...
   }
   ```

   【继承结构图】

   ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\既是消息转换器也是解析器继承结构图.png)

##### WebDataBinder【Web数据绑定器】

1. 使用绑定工厂创建一个Web数据绑定器WebDataBinder类型的binder对象，控制器方法中的自定义对象参数创建的空pojo对象会被直接封装到binder对象中作为binder的target属性
2. binder中还有一个conversionService属性称为转换服务，conversionService对应的WebConversionService对象中的converters属性里面封装者124个converter
   + Http协议规定传过来的都是字符串，SpringMVC就依靠这些转换器把String类型的数据转成各种类型的数据
   + 转换器不只是String类型转成其他类型的数据，各种数据类型的转换都有



##### MessageConverter【消息转换器】

1. 所有的MessageConverter都实现了HttpMessageConverter接口

   ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\消息转换器的父接口.png)

   + canRead是判断能不能把Class类型的返回值对象以MediaType的格式读入消息转换器

   + canWrite是判断能否把Class类型的返回值对象以MediaType的格式写出到浏览器

     > 例子：Person对象转为JSON，还可以实现请求中的JSON转换成Person对象，是可逆的，把服务器对象转成媒体类型，把请求的媒体类型转成服务器对象，原因是某些返回值处理器如RequestResponseBodyMethodProcessor既可以作为返回值处理流程中的返回值处理器，也可以作为参数解析器，里面都有消息转换器

[^要点1]: 处理器映射器、适配器、参数解析器、返回值处理器、消息转换器都是全部加载再匹配

2. 系统中默认的所有MessageConverter

   ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\MessageConverter.png)

   + ByteArrayHttpMessageConverter-只支持返回值Byte类型

   + StringHttpMessageConverter-只支持返回值String类型

   + StringHttpMessageConverter-只支持返回值String类型

     [^注意1]: 以上两个类型相同，区别是一个默认字符集是UTF-8，一个是ISO-8859-1

   + ResourceHttpMessageConverter-只支持返回值Resource类型【Resource类型是springFramework的io下的InputStream，返回这个类型的也可以写出去，这个是一个接口，继承接口有HttpResource，HttpResource的实现类GzippedResource...是以压缩包的内容类型响应；FileNameVersionedResource是以文件的内容类型响应；AbstractResource是Resource的实现类，里面有N多个继承类，有路径的方式、压缩包的方式、系统文件的方式】【指定返回值类型就会自动调用相关的消息转换器】

   + ResourceRegionHttpMessageConverter-只支持返回值ResourceRegion类型

   + SourceHttpMessageConverter-支持的是一个返回值类型HashSet集合，里面的元素有【添加到set集合中是静态代码块中添加的】

     + DOMSource、SAXSource、StAXSource、StreamSource、Source

   + AllEncompassingFormHttpMessageConverter-只支持返回值MultiValueMap类型

   + MappingJackson2HttpMessageConverter-这个的support方法直接返回true，没有像其他转换器一样进行类型匹配，但是仍然有canWrite方法内部的重载方法canWrite方法的调用判断，两个都同时满足才满足对应返回值类型，一般来说这个消息转换器能处理任何对象将其转成json写出去

   + MappingJackson2HttpMessageConverter-这个和上一个是一样的，区别暂时没讲

   + Jaxb2RootElementHttpMessageConverter-只支持方法标注了@XmlRoctElement注解的返回值

[^注意2]: canWrite方法中的canWrite重载方法一般是对支持的内容类型进行遍历，判断支持的内容类型和不为空媒体类型是否匹配，如果匹配就支持【比如我能产生json，服务器也能接收json就可以匹配】；此外，媒体类型为空有些也能支持



##### HandlerInterceptor【拦截器】

1. 所有的拦截器都继承了接口HandlerInterceptor

   > 包括自定义的拦截器，根据执行时机的需要选择合适的方法

   + 接口HandlerInterceptor中有三个需要被实现的方法
     + preHandle方法：在控制器方法执行前被调用执行
     + postHandle方法：控制器方法执行完但还没到达页面以前执行postHandle方法【即执行完handle方法获取ModelAndView后派发最终结果前】
     + afterCompletion方法：页面渲染完成后还想执行一些操作

   ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\HandlerInterceptor.png)

2. 自定义拦截器需要通过配置类实现WebMvcConfigurer接口的addInterceptors方法添加到IoC容器中

   + 通过registry.addInterceptor方法添加自定义的拦截器组件到IoC容器中
   + 在registry.addInterceptor方法后使用addPathPatterns方法添加拦截器生效的路径
     + /**表示所有请求，拦截所有也会拦截掉静态资源的访问
   + 在addPathPatterns方法后使用excludePathPatterns方法添加排除拦截器生效的路径
     + 正常写，如登录页面"/","/login''，以及静态资源如"/css/**",...
     + 静态资源的放行还可以通过设置静态资源前缀如/static，放行"/static/**"来实现对所有静态资源的放行

3. 拦截器源码分析

   【拦截器原理总览】

   ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\拦截器原理.png)

   + 第一步：根据当前请求，找到HandlerExcutionChain【即doDispatch方法中的mappedHandler】

     > 其中包含处理请求的Handler以及对应请求的所有拦截器

     + 找到适配的Handler，即mappedHandler，其中的两个属性handler指向控制器的main.html映射匹配的控制器方法，mappedHandler实际上是一个HandlerExcutionChain，即处理器执行链

       > 处理器中只有两个属性，处理器和拦截器列表

     + interceptorList为拦截器列表，其中的LoginInterceptor就是自定义的登录验证拦截器
       + 下面两个拦截器任何方法都会执行
         + ConversionServiceExposinginterceptor
           + ResourceUrlProviderExposingInterceptor

     ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\main页面请求拦截器.png)

   + 第二步：先顺序执行所有拦截器的preHandle方法

     + 如果当前拦截器preHandle方法返回true，则执行下一个拦截器的preHandle方法
     + 如果当前拦截器返回false，则直接触发triggerAfterCompletion方法倒序执行所有已经执行了preHandle方法的拦截器的AfterCompletion方法并返回false触发结束doDispatch方法的执行，不再继续执行目标控制器方法

   + 第三步：执行完handle方法并处理默认视图名字【没有视图名就应用默认视图名】后立即去倒序执行所有拦截器的postHandle方法

   + 第四步：执行完postHandle方法后立即去执行派发最终结果方法，在processDispatchResult方法的最后一行即页面成功渲染完成以后也会倒序触发执行已执行拦截器的afterCompletion方法

     > 以上所有过程发生任何异常都会捕捉异常直接去倒序执行所有执行过preHandle方法的拦截器的afterCompletion方法，包括自己写的拦截器方法发生异常

     ```java
     protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
         ...
                 mappedHandler = getHandler(processedRequest);//mappedHandler是处理器执行来年，mappedHandler的handler属性是HelloController#main()方法,interceptorList是拦截器列表
                 ...
     
                 HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());//调用控制器方法大的反射工具类
                 ...
     
                 //分支1
                 if (!mappedHandler.applyPreHandle(processedRequest, response)) {
                     return;
                 }//执行拦截器的preHandle方法,就在执行控制器方法的前一步
     
                 //执行控制器方法
                 mv = ha.handle(processedRequest, response, mappedHandler.getHandler());
     
                 if (asyncManager.isConcurrentHandlingStarted()) {
                     return;
                 }
     
                 applyDefaultViewName(processedRequest, mv);
         		//分支2
                 mappedHandler.applyPostHandle(processedRequest, response, mv);//执行相关拦截器的postHandle方法
             }
             catch (Exception ex) {
                 dispatchException = ex;
             }
             ...
             //重点四：处理派发最终的结果，这一步执行前，整个页面还没有跳转过去
             processDispatchResult(processedRequest, response, mappedHandler, mv, dispatchException);
         }
         catch (Exception ex) {
             triggerAfterCompletion(processedRequest, response, mappedHandler, ex);
         }
         ...
     }
     ```

     【分支1：applypreHandle方法分支】

     【doDispatch方法中的applypreHandle方法】

     ```java
     public class HandlerExecutionChain {
     	boolean applyPreHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
     		for (int i = 0; i < this.interceptorList.size(); i++) {
     			HandlerInterceptor interceptor = this.interceptorList.get(i);//正向一次遍历所有拦截器
     			if (!interceptor.preHandle(request, response, this.handler)) {//执行拦截器的preHandle方法，如果但凡一个拦截器返回false，这里就会先执行拦截器的AfterCompletion然后返回false导致直接跳出doDispatch方法的执行
     				triggerAfterCompletion(request, response, null);
     				return false;
     			}
     			this.interceptorIndex = i;//如果拦截器返回true，就会遍历一个拦截器，执行完对应的preHandle方法就会给interceptorIndex属性赋值i，这个为当前已执行preHandle方法的拦截器在interceptorList中的下标
     		}
     		return true;
     	}
     }
     ```

     【applyPreHandle方法中的triggerAfterCompletion方法】

     ```java
     public class HandlerExecutionChain {
     	void triggerAfterCompletion(HttpServletRequest request, HttpServletResponse response, @Nullable Exception ex) {//但凡有一个拦截器的返回值为false，就会直接进来执行已经执行了preHandle方法的所有拦截器的afterCompletion方法
     		for (int i = this.interceptorIndex; i >= 0; i--) {
     			HandlerInterceptor interceptor = this.interceptorList.get(i);
     			try {
     				interceptor.afterCompletion(request, response, this.handler, ex);//执行afterCompletion方法，正常不正常的afterCompletion方法都是来这里面执行
     			}
     			catch (Throwable ex2) {
     				logger.error("HandlerInterceptor.afterCompletion threw exception", ex2);
     			}
     		}
     	}
     }
     ```

     【分支2：applyPostHandle方法分支】

     【doDispatch方法中的applyPostHandle方法】

     ```java
     public class HandlerExecutionChain {
     	void applyPostHandle(HttpServletRequest request, HttpServletResponse response, @Nullable ModelAndView mv)
     			throws Exception {
     		for (int i = this.interceptorList.size() - 1; i >= 0; i--) {//倒序执行所有拦截器的postHandle方法，注意这里的初始i取得是拦截器列表的最后一位的下标
     			HandlerInterceptor interceptor = this.interceptorList.get(i);
     			interceptor.postHandle(request, response, this.handler, mv);//挨个执行postHandle方法
     		}
     	}
     }
     ```

### 4.4.3、Handler请求参数处理

#### 01、请求参数基本注解

> 在SpringMVC中的控制器方法形式参数面前加上适当的注解，SpringMVC就可以自动为赋值目标请求参数



##### @PathVariable

> 路径变量注解@PathVariable可以从Rest风格请求路径获取请求参数并将特定请求参数与形参对应

1. 在形参前面使用@PathVariable("id")能指定形参对应请求参数的位置并将请求参数赋值给形参

2. 使用key和value都为String类型的Map集合作为形参结合@PathVariable注解能获取请求映射注解匹配路径中所有用大括号进行标识的请求参数

[***使用@PathVariable注解的前提是请求映射@GetMapping("/car/{id}/owner/{username}")的匹配路径对相关请求参数用大括号进行标识，注意请求映射上大括号标注的路径变量可以动态的被替换***]

```java
@GetMapping("/car/{id}/owner/{username}")
public Map<String,Object> getCar(@PathVariable("id") Integer id,
                                 @PathVariable("username") String username,
                                 @PathVariable Map<String,String> pv){
    Map<String,Object> map=new HashMap<>();
    map.put("id",id);
    map.put("username",username);
    map.put("pv",pv);
    return map;//{"pv":{"id":"2","username":"zhangsan"},"id":2,"username":"zhangsan"}
}
```



##### @RequestHeader

> 获取请求头对象，可以通过请求头的Host获取请求发送的ip地址、通过User-Agent获取发送请求的浏览器信息

1. 在形参前面使用@RequestHeader("User-Agent")可以获取请求头中key为User-Agent的单个属性值并赋值给对应字符串形参，这种方式只支持获取请求头中的单个键值对的属性值

2. 使用key和value都为String类型的Map集合或者MultiValueMap类型以及HttpHeaders类型的形参结合@RequestHeader注解可以获取全部的请求头信息

```java
@GetMapping("/car/header")
public Map<String,Object> getCar(@RequestHeader("User-Agent") String userAgent,
                                 @RequestHeader Map<String,String> header){
    Map<String,Object> map=new HashMap<>();
    map.put("userAgent",userAgent);
    map.put("header",header);
    return map;//"userAgent":"Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Mobile Safari/537.36"},header太长了不展示
}
```

[^注意]: 使用Map和不使用Map参数解析器都不一样



##### @RequestParam

> 非Rest风格传递请求参数使用@RequestParam指定形参与请求参数名的对应关系

1. 在形参前面使用@RequestParam("age")注解指定请求参数age与形式参数age的对应关系并自动赋值给对应的形式参数，如果请求参数中含有多个同名参数值，需要使用List集合进行接收
2. 使用key和value都为String类型的Map集合结合@RequestParam注解可以获取所有的请求参数，注意这种使用map集合接收多个同名参数的方法存在参数值丢失的现象

```java
@GetMapping("/car/param")
public Map<String,Object> getCar(@RequestParam("age") Integer age,
                                 @RequestParam("inters") List<String> inters,
                                 //@RequestParam Map<String,String> params){
                                 @RequestParam Map<String,Object> params){//Map接收所有数据请求参数存在多个同名参数值的会丢值
    Map<String,Object> map=new HashMap<>();
    map.put("age",age);
    map.put("inters",inters);
    map.put("params",params);
    return map;//{"inters":["basketball","game"],"params":{"age":"18","inters":" basketball"},"age":18}
}
```



##### @CookieValue

> 使用@CookieValue注解可以获取请求的Cookie值

1. 在字符串形参前面使用@CookieValue("JSESSIONID")注解指定请求头中Cookie项下的key为JSESSIONID的参数的参数值并为形参赋值，这种方式只是拿到了JSESSIONID的cookie值
2. 在Cookie类型的形参前面使用@CookieValue("JSESSIONID")注解可以获取整个"JSESSIONID"的Cookie对象并赋值给形参，可以获取Cookie对象的名字，值等信息

```java
@GetMapping("/car/cookie")
public Map<String,Object> getCar(@CookieValue("JSESSIONID") String jSessionId,
                                 @CookieValue("JSESSIONID") Cookie cookie){
    System.out.println(cookie+" | "+cookie.getName()+":"+cookie.getValue());
    //javax.servlet.http.Cookie@4bbe7695 | JSESSIONID:44613C5A612B109B5DB9E9A6D71C6C3D
    Map<String,Object> map=new HashMap<>();
    map.put("JSESSIONID",jSessionId);
    return map;//{"JSESSIONID":"44613C5A612B109B5DB9E9A6D71C6C3D"}
}
```

【***注意Chrome需要先获取一次session对象才会生成JSESSIONID，否则在请求头是不会显示Cookie的***】



##### @RequestBody

> 使用@RequestBody注解可以获取POST请求的请求体，即表单中的数据

1. 在字符串形参前面使用@RequestBody注解指定form表单中的数据并为形参赋值，浏览器请求体中表单的内容原本就是类似于Get请求URL中问号的部分如：username=zhangsan&email=2625074321%40qq.com，所以这种方式获取的字符串也是同样的组织形式

```java
@PostMapping("/save")
public Map<String,Object> getCar(@RequestBody String requestBodyContent){
    System.out.println(requestBodyContent);//username=zhangsan&email=2625074321%40qq.com
    Map<String,Object> map=new HashMap<>();
    map.put("requestBodyContent",requestBodyContent);
    return map;//{"requestBodyContent":"username=zhangsan&email=2625074321%40qq.com"}
}
```

[^Ques?]: 这种方式获取的请求参数特殊符号比如@会解析成%40，该问题是怎么解决的?

2. 使用RestTemplate的post方式由服务发送的请求被调用方的参数必须有@RequestBody注解参数才能被获取封装，否则获取不到参数，数据库插入null，但是服务器不报错

   > 注意，即便调用方是Get请求只要能封装成Payment对象，也不影响调用方对RestTemplate的Post请求参数的接收，就是完全可以调用方接收get请求的参数，然后封装数据单独发送post请求给被调用方

   【调用方】

   ```java
   @PostMapping("/consumer/payment/create")
   public CommonResponse<Payment> create(Payment payment){
       return restTemplate.postForObject(PAYMENT_URL+"/payment/create",payment, CommonResponse.class);
   }
   ```

   【被调用方】

   ```java
   @PostMapping("/payment/create")
   public CommonResp create(@RequestBody Payment payment){
       int result=paymentService.create(payment);
       log.info("插入结果："+result);
       return result>0?new CommonResp(200,"数据插入成功",result):
               new CommonResp(505,"插入数据库失败");
   }
   ```

   【添加@RequestBody注解前后效果】

   ![](https://vpc-ol-edu.oss-cn-chengdu.aliyuncs.com/2023/10/20/274bf77e3b9e43fb8b17f5805bace53eRestTemplate发起Post请求.png)



##### @RequestAttribute

> 使用@RequestAttribute注解可以在转发页面获取请求域中的共享数据

1. 在转发控制器方法中与请求域参数类型对应的形参前面使用@RequestAttribute("msg")注解指定域中的key为msg的值并自动为对应的形参赋值，要求对应的值转发必须已经存入请求域
2. 从请求域中获取共享数据除了使用@RequestAttribute注解的方式外还可以使用原生的被转发的request请求调用getAttribute获取，你懂的
3. 可以在@RequestAttribute注解中使用required=false指定参数不是必须的，没传参引用数据类型全部默认null

```java
@Controller
public class RequestController {
    @GetMapping("/goto")
    public String goToPage(HttpServletRequest request){
        request.setAttribute("msg","成功了...");
        request.setAttribute("code",200);
        return "forward:/success";//转发到'/success'请求
    }
    @ResponseBody
    @GetMapping("/success")
    public Map<String,Object> success(@RequestAttribute("msg") String msg,
                                      @RequestAttribute("code") Integer code,
                                      HttpServletRequest request){
        Map<String,Object> map=new HashMap<>();
        Object msg1 = request.getAttribute("msg");
        map.put("reqMethod_msg",msg1);
        map.put("annotationMethod_code",code);
        return map;//{"reqMethod_msg":"成功了...","annotationMethod_code":200}
    }
}
```

[^注意]: @RequestAttribute不能使用Map集合一次性接收请求域中的所有值



##### @MatrixVariable

> 使用@MatrixVariable注解可以获取矩阵变量路径中的矩阵变量

1. 矩阵变量

   > 矩阵变量是一种请求数据的组织形式，原来一直使用的是查询字符串

   + 查询字符串【queryString】：/cars/{path}?xxx=xxx&xxxx=xxx
     + 查询字符串的方式使用@RequestParam注解获取请求数据

   + 矩阵变量：/cars/sell;low=34;brand=byd,audi,yd

     + 应用场景：Cookie禁用，浏览器请求头不携带JSESSIONID，解决办法是重写url，把cookie的值使用矩阵变量的方式进行传递：/abc;jsessionid=xxxx

     + 含多个同名参数的矩阵变量写法：

       ```html
       <a href="/cars/sell;low=34;brand=byd,audi,yd,haha"></a>
       <a href="/cars/sell;low=34;brand=byd,audi,yd;brand=haha"></a>
       <!--以上两种写法的效果都是一样的，最终brand都会封装成四个属性值
       	{"path":"sell","low":34,"brand":["byd","audi","yd","niuniu"]}
       -->
       <a href="/boss/1;age=20/2;age=10"></a>
       <!--以上这种写法的同名参数属于不同的路径变量，当指定了pathVar后会分别依据pathVar获取不同路径变量的age-->
       ```

     + 矩阵变量中/xxx;xxx=xxx/的xxx;xxx=xxx是一个基本整体，一个请求路径中可以有多个这样的基础单元；基本单元中分号前面没有等号的部分是访问路径，分号后面的等式是矩阵变量，多个矩阵变量间使用分号进行区分

   + 使用@MatrixVariable注解可以获取矩阵变量路径中的矩阵变量，注意这种获取矩阵变量的方式矩阵变量必须绑定在路径变量中，即请求映射注解中的矩阵变量部分基本单元需要使用{xxx}如{path}代替，多个基本单元中含有同名变量，需要在@MatrixVariable注解中使用pathvar("xxx")如pathVar("path")指定基本单元

2. 手动开启矩阵变量功能

   + 禁用矩阵变量的原理

     > SpringBoot默认是禁用矩阵变量的，需要定制化SpringMVC中的组件手动开启矩阵变量功能，相应的核心属性是WebMvcAutoConfiguration中的configurePathMatch配置路径映射组件的UrlPathHelper对象中的removeSemicolonContent属性；SpringBoot对路径的处理依靠UrlPathHelper对路径进行解析，当removeSemicolonContent属性为true时表示移除路径中分号后面的所有内容，矩阵变量会被自动忽略，因此使用矩阵变量需要自定义removeSemicolonContent属性为false的组件configurePathMatch

     ```java
     public class WebMvcAutoConfiguration {
         ...
         @Configuration(proxyBeanMethods = false)
         @Import(EnableWebMvcConfiguration.class)
         @EnableConfigurationProperties({ WebMvcProperties.class, ResourceProperties.class })
         @Order(0)
         public static class WebMvcAutoConfigurationAdapter implements WebMvcConfigurer {
         	...
             @Override
             @SuppressWarnings("deprecation")
             public void configurePathMatch(PathMatchConfigurer configurer) {
                 configurer.setUseSuffixPatternMatch(this.mvcProperties.getPathmatch().isUseSuffixPattern());
                 configurer.setUseRegisteredSuffixPatternMatch(
                         this.mvcProperties.getPathmatch().isUseRegisteredSuffixPattern());
                 this.dispatcherServletPath.ifAvailable((dispatcherPath) -> {
                     String servletUrlMapping = dispatcherPath.getServletUrlMapping();
                     if (servletUrlMapping.equals("/") && singleDispatcherServlet()) {
                         UrlPathHelper urlPathHelper = new UrlPathHelper();//UrlPathHelper意为路径帮助器，UrlPathHelper中的removeSemicolonContent属性为true，表示会默认移除请求路径URI中分号后面的所有内容，就相当于直接忽略掉所有的矩阵变量
                         urlPathHelper.setAlwaysUseFullPath(true);
                         configurer.setUrlPathHelper(urlPathHelper);
                     }
                 });
             }
         }
     }
     ```

     [^注意]: 配置对应组件configurePathMatch方法是WebMvcConfigurer接口中的方法；注意默认组件configurePathMatch的配置类即WebMvcAutoConfiguration内部的配置类WebMvcAutoConfigurationAdapter实现了WebMvcConfigurer接口，该配置类没有开启单实例，实际添加UrlPathHelper对象不像是添加组件，而是通过方法configurer.setUrlPathHelper(urlPathHelper);把创建好的urlPathHelper对象添加到configurer对象中

   + 自定义configurePathMatch组件

     > SpringBoot为组件自定义提供了三种方案，详情见 [**4.1.1、 SpringMVC自动配置概览**]，这里自定义configurePathMatch组件有两种方式

     + 方式一：在自定义配置类WebConfig中用@Bean注解给容器配置一个WebMvcConfigurer组件
     
       ```java
       @Configuration(proxyBeanMethods = false)
       public class WebConfig{
       	//方式一:使用@Bean配置一个WebMvcConfigurer组价
           @Bean
           public WebMvcConfigurer webMvcConfigurer(){
               return new WebMvcConfigurer() {
                   @Override
                   public void configurePathMatch(PathMatchConfigurer configurer) {
                       UrlPathHelper urlPathHelper=new UrlPathHelper();
                       //设置removeSemicolonContent属性为false就可以保留矩阵变量功能分号后面的内容
                       urlPathHelper.setRemoveSemicolonContent(false);
                       //感觉像把容器中的默认urlPathHelper换成自己创建的
                       configurer.setUrlPathHelper(urlPathHelper);
                   }
               };
           }
       }
       ```

     + 方式二：让自定义配置类WebConfig实现WebMvcConfigurer接口，由于Jdk8有该接口的默认实现，可以只实现该接口的configurePathMatch方法，将自定义的urlPathHelper通过该方法传参configurer的setUrlpathHelper(urlPathHelper)方法配置到容器中即可
     
       ```java
       @Configuration(proxyBeanMethods = false)
       public class WebConfig implements WebMvcConfigurer {
           //方式二:自定义配置类实现WebMvcConfigurer接口并重写configurePathMatch方法
           @Override
           public void configurePathMatch(PathMatchConfigurer configurer) {
               UrlPathHelper urlPathHelper=new UrlPathHelper();
               //设置removeSemicolonContent属性为false就可以保留矩阵变量功能分号后面的内容
               urlPathHelper.setRemoveSemicolonContent(false);
               //感觉像把容器中的默认urlPathHelper换成自己创建的
               configurer.setUrlPathHelper(urlPathHelper);
           }
       }
       ```

3. @MatrixVariable注解的用法

   + 在形参前面使用@MatrixVariable("low")注解可以获取矩阵变量路径中变量名为low的字面值并自动赋值给形参，形参类型不限，含多个同名参数需要使用List集合类型的形参进行接收

     ```java
     //请求路径： /cars/sell;low=34;brand=byd,audi,yd
     //@GetMapping("/cars/sell")
     @GetMapping("/cars/{path}")
     public Map<String,Object> carsSell(@MatrixVariable("low") Integer low,
                                        @MatrixVariable("brand") List<String> brand,
                                        @PathVariable("path") String path){
         System.out.println(path);
         Map<String,Object> map=new HashMap<>();
         map.put("low",low);
         map.put("brand",brand);
         map.put("path",path);//sell
         return map;//{"path":"sell","low":34,"brand":["byd","audi","yd"]}
     }
     ```

   + 配置@MatrixVariable注解的pathVar属性可以获取不同路径变量的同名参数

     ```java
     //请求路径:   /boss/1;age=20/2;age=10
     @GetMapping("/boss/{bossId}/{empId}")
     public Map<String,Object> boss(@MatrixVariable(value = "age",pathVar = "bossId") Integer bossAge,
                                    @MatrixVariable(value = "age",pathVar = "empId") Integer empAge){
         Map<String,Object> map=new HashMap<>();
         map.put("bossAge",bossAge);
         map.put("empAge",empAge);
         return map;//{"bossAge":20,"empAge":10}
     }
     ```

     

##### @ModelAttribute

> 参数解析器ServletModelAttributeMethodArgumentResolver是专门检查是否有@ModelAttribute注解的，不能处理复杂参数Model类型



#### 02、常用Servlet API

> 在控制方法形参列表指定特定参数类型可以自动获取常用的原生Servlet相关对象作为参数，可使用原生Servlet的功能

1. 可以使用的Servlet API：
   + WebRequest、ServletRequest、MultipartRequest、 HttpSession、javax.servlet.http.PushBuilder、Principal、InputStream、Reader、HttpMethod、Locale、TimeZone、ZoneId
   + 上述部分API的参数解析器为ServletRequestMethodArgumentResolver，在supportsParameter方法中规定了具体的相关参数类型，原生request和response都可以通过传递过来封装了原生请求和响应对象的参数webRequest获取，具体原理见源码

2. 以ServletRequest为例分析Servlet API的底层原理

   + 只讲getMethodArgumentValues方法的两个重点判断参数解析器和解析参数值，其他的执行流程见重点3

   【控制器方法准备】

   ```java
   @GetMapping("/goto")
   public String goToPage(HttpServletRequest request){
       request.setAttribute("msg","成功了...");
       request.setAttribute("code",200);
       return "forward:/success";//转发到'/success'请求
   }
   ```

   【步骤1：判断和寻找适合HttpServletRequest类型参数的参数解析器】

   ```java
   public class HandlerMethodArgumentResolverComposite implements HandlerMethodArgumentResolver {
       ... 
       public boolean supportsParameter(MethodParameter parameter) {
           return this.getArgumentResolver(parameter) != null;//第1步
       }
   	
       //第2步
       @Nullable
   	private HandlerMethodArgumentResolver getArgumentResolver(MethodParameter parameter) {
   		HandlerMethodArgumentResolver result = this.argumentResolverCache.get(parameter);
   		if (result == null) {
   			for (HandlerMethodArgumentResolver resolver : this.argumentResolvers) {
   				if (resolver.supportsParameter(parameter)) {//第3步 
   					result = resolver;
   					this.argumentResolverCache.put(parameter, result);//第4步
   					break;
   				}
   			}
   		}
   		return result;
   	}
   }
   ```

   【当循环到resolver为ServletRequestMethodArgumentResolver时的supportsParameter方法解析】

   ```java
   public class ServletRequestMethodArgumentResolver implements HandlerMethodArgumentResolver {
       ...
   	@Override
   	public boolean supportsParameter(MethodParameter parameter) {
   		Class<?> paramType = parameter.getParameterType();//拿到参数类型HttpServletRequest，接下来对参数类型进行一堆判断，HttpServletRequest正好就是ServletRequest,返回true，不止HttpServletRequest，凡是下面出现的Servlet API都可以使用这个解析器进行解析
   		return (/*是否WebRequest*/WebRequest.class.isAssignableFrom(paramType) ||
   				/*是否ServletRequest*/ServletRequest.class.isAssignableFrom(paramType) ||
   				/*是否...*/MultipartRequest.class.isAssignableFrom(paramType) ||
   				/*...*/HttpSession.class.isAssignableFrom(paramType) ||
   				(pushBuilder != null && pushBuilder.isAssignableFrom(paramType)) ||
   				(Principal.class.isAssignableFrom(paramType) && !parameter.hasParameterAnnotations()) ||
   				InputStream.class.isAssignableFrom(paramType) ||
   				Reader.class.isAssignableFrom(paramType) ||
   				HttpMethod.class == paramType ||
   				Locale.class == paramType ||
   				TimeZone.class == paramType ||
   				ZoneId.class == paramType);
   	}
       ...
   }
   ```

   【步骤2：使用解析器将原生的HttpServletRequest传递给形参】

   ```java
   public class ServletRequestMethodArgumentResolver implements HandlerMethodArgumentResolver {
   	@Override
   	public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
   			NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {//注意了webRequest中封装了原生的request和response对象，是由HandlerMethodArgumentResolverComposite类中的resolveArgument方法传进来的
   
   		Class<?> paramType = parameter.getParameterType();
   
   		// WebRequest / NativeWebRequest / ServletWebRequest
   		if (WebRequest.class.isAssignableFrom(paramType)) {//判断对象类型是不是WebRequest ，这里是HttpServletRequest，所以不是【还有其他两个是也行】
   			if (!paramType.isInstance(webRequest)) {
   				throw new IllegalStateException(
   						"Current request is not of type [" + paramType.getName() + "]: " + webRequest);
   			}
   			return webRequest;
   		}
   
   		// ServletRequest / HttpServletRequest / MultipartRequest / MultipartHttpServletRequest
   		if (ServletRequest.class.isAssignableFrom(paramType) || MultipartRequest.class.isAssignableFrom(paramType)) {//判断对象类型是不是ServletRequest ，这里是HttpServletRequest，所以是【还有其他三个是也行】
   			return resolveNativeRequest(webRequest, paramType);//核心，把webRequest形参传给了resolveNativeRequest方法进行解析
   		}
   
   		// HttpServletRequest required for all further argument types
   		return resolveArgument(paramType, resolveNativeRequest(webRequest, HttpServletRequest.class));
   	}
       
       //【resolveArgument方法中的resolveNativeRequest方法解析】
       private <T> T resolveNativeRequest(NativeWebRequest webRequest, Class<T> requiredType) {
   		T nativeRequest = webRequest.getNativeRequest(requiredType);//通过webRequest拿到原生的request请求
   		if (nativeRequest == null) {
   			throw new IllegalStateException(
   					"Current request is not of type [" + requiredType.getName() + "]: " + webRequest);
   		}
   		return nativeRequest;//拿到原生的request请求直接返回给resolveNativeRequest方法，最后返回给存储所有参数的数组
   	}
   }
   ```

   

#### 03、常用复杂参数

> 在形参列表指定特定参数类型可以自动获取一些复杂参数，如Model可用于向请求域共享数据，doDispatch方法源码解析的重点4中介绍了Map和Model数据从封装到ModelAndView到最后放入请求域中的整个流程

1. 常用复杂参数清单

   + **Map**

   + **Model**

     [^要点1]: 存放在map、model的数据会被放在request的请求域中， 相当于给数据调用request.setAttribute方法
     [^注意]: 向请求域中放数据实际上有五种方式，雷神这里讲了三种：原生request、map、model

   - Errors/BindingResult

   - **RedirectAttributes**

     [^要点2]: 重定向携带数据，以前都是要放session会话域的

   - **ServletResponse**

     [^要点3]: 原生的response，这个不是在原生Servlet API中讲过了吗

   - SessionStatus

   - UriComponentsBuilder

   - ServletUriComponentsBuilder

2. 测试map和model对应的参数解析器

   + 原理：还是原来的，在supportsParameter方法的调用方法getArgumentResolver中对26种参数解析器进行遍历，测试请求参数类型按顺序依次为Map、Model、HttpServletRequest、HttpServletResponse ，生效的参数解析器依次为MapMethodProcessor、ModelMethodProcessor、

   + 特别注意：经过测试和源码分析，形参同时有参数类型为Map以及参数类型为Model的情况下，实参Object临时数组中存放的Map和Model的引用地址都指向同一个BindingAwareModelMap对象

     ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\Map和Model的实参都指向同一个对象.png)

     【BindingAwareModelMap的继承结构图】

     ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\BindingAwareModelMap继承关系结构图.png)

   + 当参数类型为Map且参数没有任何注解时的底层原理

     + 核心1：参数解析器MapMethodProcessor的supportsParameter方法检查参数类型是否Map且参数上没有注解
     + 核心2：参数解析器MapMethodProcessor的resolveArgument方法直接调用mavContainer. getModel()方法从mavContainer对象【ModelAndViewContainer】中获取空的defaultModel属性【BindingAwareModelMap】并将其作为实参传入类型为Map的形参，defaultModel是一个空Map ，同时也是一个Model，这个defaultModel可以直接从mavContainer对象中随时拿

     【MapMethodProcessor的相关方法】

     ```java
     public class MapMethodProcessor implements HandlerMethodArgumentResolver, HandlerMethodReturnValueHandler {
     	//匹配参数解析器方法
         @Override
     	public boolean supportsParameter(MethodParameter parameter) {
     		return (Map.class.isAssignableFrom(parameter.getParameterType()) &&
     				parameter.getParameterAnnotations().length == 0);//判断方法参数类型是Map并且该没有注解
     	}
         
         //解析参数方法
         @Override
     	@Nullable
     	public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
     			NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
     
     		Assert.state(mavContainer != null, "ModelAndViewContainer is required for model exposure");
     		return mavContainer.getModel();//mavContainer是ModelAndViewContainer，通过getModel方法可以找到ModelAndViewContainer中有一个defaultModel属性，返回defaultModel，此时这个这个defaultModel会直接返回给形参object数组,以后会把这个defaultModel赋值给Map类型的形参
             
             //defaultModel实际上是一个BindingAwareModelMap,BindingAwareModelMap继承于ExtendedModelMap，ExtendedModelMap继承于ModelMap并实现了Model，ModelMap继承于LinkedHashMap<String,Object>,相当于用于形参列表中的Map类型既是Model也是Map
             
     	}
     }
     ```

     【ModelAndViewContainer中的defaultModel属性】

     ```java
     public class ModelAndViewContainer {
     	...
     	private final ModelMap defaultModel = new BindingAwareModelMap();
     	...
     	public ModelMap getModel() {
     		if (useDefaultModel()) {
     			return this.defaultModel;//返回defaulModel属性
     		}
     		else {
     			if (this.redirectModel == null) {
     				this.redirectModel = new ModelMap();
     			}
     			return this.redirectModel;
     		}
     	}
     	...
     }
     ```

   + 当参数类型为Model

     + 核心1：匹配参数解析器的方法没说，以后研究一下
     + 核心2：参数解析器ModelMethodProcessor的resolveArgument方法还是直接调用mavContainer. getModel()方法，仍然返回ModelAndViewContainer类型的mavContainer对象中BindingAwareModelMap类型的属性defaultModel，仍然是一个空的map，也是一个model

     【ModelMethodProcessor的相关方法】

     ```java
     public class ModelMethodProcessor implements HandlerMethodArgumentResolver, HandlerMethodReturnValueHandler {
     
     	@Override
     	public boolean supportsParameter(MethodParameter parameter) {
     		return Model.class.isAssignableFrom(parameter.getParameterType());//没进去，就说找到了，怎么找的没说
     	}
     
     	@Override
     	@Nullable
     	public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
     			NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
     
     		Assert.state(mavContainer != null, "ModelAndViewContainer is required for model exposure");
     		return mavContainer.getModel();//实际上，这个方法调用和MapMethodProcessor中的一模一样,也是返回ModelAndViewContainer对象mavContainer中BindingAwareModelMap类型的属性defaultModel，仍然是一个空的map，也是一个model
     	}
     	...
     }
     
     ```

3. Modle和Map类型形参向请求域共享数据的原理

   + 执行完形参数据封装成object数组，立即去执行控制器方法，获得控制器方法的返回值，随后将返回值处理封装到ModelAndView中【该过程给mav构造方法传参defaultModel，执行构造方法时defaultModel中的数据被封装成新的ModelMap存入ModelAndView】返回给doDispatch方法
   + 转头立即去执行所有拦截器的postHandle方法，
   + 随后执行processDispatchResult方法处理和派发结果，调用render方法获取视图名以及最佳匹配视图并
   + 在render方法中调用view.render方法把ModelMap中的数据全部转移到LinkedHashMap类型的mergedModel中，然后调用renderMergedOutputModel方法，传参mergedModel和原生request对象
   + 在renderMergedOutputModel方法中调用exposeModelAsRequestAttributes方法使用Map流式编程遍历key和value并全部放入请求域中

[^Ques?]: 我不理解一个几行代码就能解决的事情为什么要绕这么大一圈



#### 04、自定义对象参数

> 与属性同名的请求参数会被自动封装在对象中，场景举例：传递参数的名字和pojo类的属性名相同，或者与属性指向的对象的属性名相同(即级联属性)，可以使用SpringBoot实现前端提交的数据被自动封装成pojo对象

1. 数据绑定：页面提交的请求数据(GET、POST)都可以和对象属性进行绑定

【pojo类】

```java
/**
 *     姓名： <input name="userName"/> <br/>
 *     年龄： <input name="age"/> <br/>
 *     生日： <input name="birth"/> <br/>
 *     宠物姓名：<input name="pet.name"/><br/>
 *     宠物年龄：<input name="pet.age"/>
 */
@Data
public class Person {
    
    private String userName;
    private Integer age;
    private Date birth;
    private Pet pet;
    
}

@Data
public class Pet {

    private String name;
    private String age;

}
```

【前端测试页面】

1. 注意：
   + 涉及级联属性的前端表单页面提交数据的名字也要用pet.name对级联属性进行区分，否则无法为级联属性赋值
   + 且多个相同参数名而属性只能接收一个就只取第一个请求参数的值

```html
<form action="/saveuser" method="post">
    姓名:<input name="userName" value="zhangsan"><br>
    年龄:<input name="age" value="18"><br>
    生日:<input name="birth" value="2019/12/10"><br>
    宠物姓名:<input name="pet.name" value="阿猫"><br>
    宠物年龄:<input name="pet.age" value="5"><br>
    <input TYPE="submit" value="保存">
</form>
```

【控制器方法代码】

```java
@RestController
public class ParameterTestController {
    @PostMapping("/saveuser")
    public Person saveUser(Person person){
        return person;
        //{"userName":"zhangsan","age":18,"birth":"2019-12-09T16:00:00.000+00:00","pet":{"name":"阿猫","age":"5"}}
        //如果没有表单提交数据的参数名没有使用级联属性的命名，就无法为级联属性赋值，且多个相同参数名而属性只能接收一个就只取第一个请求参数
    }
}
```

2. 数据绑定的底层原理

   + POJO类使用的参数解析器是ServletMethodAttributeMethodProcessor【注意有两个参数解析器都叫做ServletMethodAttributeMethodProcessor，第6个和第25个，只有第25个能解析Pojo类自定义参数，这是为什么呢】

     + 这个参数解析器的supportsParameter方法是从父类ModelAttributeMethodProcessor继承来的且没有重写
     + resolveArgument也是从父类ModelAttributeMethodProcessor继承来的且没有重写，打断点会直接进父类执行对应的方法

   + supportsParameter方法

     + 只要有@ModelAttribute注解的参数或者不是必须标注注解且不是简单类型的参数ServletMethodAttributeMethodProcessor参数解析器就可以解析该参数

   + resolveArgument方法

     + 先通过createAttribute方法创建一个对应自定义类型参数的空Pojo实例出来
     + 判断pojo实例没有绑定数据就会通过绑定工厂的binderFactory.createBinder方法创建一个Web数据绑定器WebDataBinder binder对象

   + 太烦了，先记住以后几个重点，自己慢慢理一下

     + WebDataBinder
     + WebDataBinder中有124个converter
     + 可以自定义converter把任意类型转换成我们想要的类型【valueOf方法】

     【源码解析】

     ```java
     public class ModelAttributeMethodProcessor implements HandlerMethodArgumentResolver, HandlerMethodReturnValueHandler {
     	...
     	@Override
     	public boolean supportsParameter(MethodParameter parameter) {
     		return (parameter.hasParameterAnnotation(ModelAttribute.class) ||
     				(this.annotationNotRequired && !BeanUtils.isSimpleProperty(parameter.getParameterType())));//hasParameterAnnotation是判断参数是否标注@ModelAttribute注解，annotationNotRequired判断参数是否不需要注解，如果不需要注解就继续判断参数是否简单类型;只要有@ModelAttribute注解的参数或者不是必须标注注解且不是简单类型的参数ServletMethodAttributeMethodProcessor参数解析器就可以解析该参数
     	}
     
     	@Override
     	@Nullable
     	public final Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
     			NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
     
     		Assert.state(mavContainer != null, "ModelAttributeMethodProcessor requires ModelAndViewContainer");
     		Assert.state(binderFactory != null, "ModelAttributeMethodProcessor requires WebDataBinderFactory");
     
     		String name = ModelFactory.getNameForParameter(parameter);//获取参数的名字
     		ModelAttribute ann = parameter.getParameterAnnotation(ModelAttribute.class);//获取@ModelAttribute注解
     		if (ann != null) {
     			mavContainer.setBinding(name, ann.binding());//如果有注解走这里绑定一堆东西
     		}
     
             //自定义pojo没有@ModelAttribute注解直接到这儿
     		Object attribute = null;
     		BindingResult bindingResult = null;
     
     		if (mavContainer.containsAttribute(name)) {//这一步是判断mavContainer中是否有所需参数，这个也是@ModelAttribute注解的功能，@ModelAttribute注解不常用，暂时不管
     			attribute = mavContainer.getModel().get(name);
     		}
     		else {
     			// Create attribute instance
     			try {
     				attribute = createAttribute(name, parameter, binderFactory, webRequest);//经过这一步会创建一个实例给attribute，创建的实例是一个空Pojo对象【这里是空Person对象】
     			}
     			catch (BindException ex) {
     				if (isBindExceptionRequired(parameter)) {
     					// No BindingResult parameter -> fail with BindException
     					throw ex;
     				}
     				// Otherwise, expose null/empty value and associated BindingResult
     				if (parameter.getParameterType() == Optional.class) {
     					attribute = Optional.empty();
     				}
     				else {
     					attribute = ex.getTarget();
     				}
     				bindingResult = ex.getBindingResult();
     			}
     		}
     
     		if (bindingResult == null) {//空person对象最终需要跟请求数据进行绑定，bindingResult是绑定结果，就是用来进入请求数据赋值给pojo对象的判据，没有绑定结果就进行绑定
     			// Bean property binding and validation;
     			// skipped in case of binding failure on construction.
                 
                 ////以下就是将请求数据赋值给空的pojo对象
     			WebDataBinder binder = binderFactory.createBinder(webRequest, attribute, name);//利用绑定工厂创建一个Web数据绑定器WebDataBinder binder对象，空pojo对象即attribute对象会直接封装到binder中作为binder的target属性；binder中还有一个conversionService属性称为转换服务，conversionService对应的WebConversionService对象中的converters属性里面封装者124个converter，因为Http协议规定传过来的都是字符串，所以SpringMVC就依靠这些转换器把String类型的数据转成各种类型的数据，但是我看到不止String转成其他，还有Number转成Number，各种类型转换都有；WebDataBinder使用转换器把数据转换成对应的数据类型后使用反射机制把数据封装到pojo对象的属性中
     			
                 if (binder.getTarget() != null) {//拿到pojo对象并判断对象不为空
     				if (!mavContainer.isBindingDisabled(name)) {
                         
                         //核心
     					bindRequestParameters(binder, webRequest);//这一步是绑定请求数据，传参有web数据绑定器和原生请求webRequest，绑定器有实例化的空pojo对象【person】，在这一步就会完成空pojo的所有属性传参，这一步就是核心：原理是获取请求中的每个参数值，通过反射机制找到空pojo的属性值并把值封装到属性中，反射过程没咋说，反正javase有，主要还是获取转换器，转换器通过dicode或者valueOf转换参数类型，完事返回目标参数值在某个环节被ph.set绑定在pojo对象对应的属性中
     				}
     				validateIfApplicable(binder, parameter);
     				if (binder.getBindingResult().hasErrors() && isBindExceptionRequired(binder, parameter)) {
     					throw new BindException(binder.getBindingResult());//绑定期间有任何异常都会放到绑定结果中，使用getBindingResult方法就能获取绑定结果，SpringMVC数据校验时校验错误拿校验结果就是在这一块拿的
     				}
     			}
     			// Value type adaptation, also covering java.util.Optional
     			if (!parameter.getParameterType().isInstance(attribute)) {
     				attribute = binder.convertIfNecessary(binder.getTarget(), parameter.getParameterType(), parameter);
     			}
     			bindingResult = binder.getBindingResult();
     		}
     
     		// Add resolved attribute and BindingResult at the end of the model
     		Map<String, Object> bindingResultModel = bindingResult.getModel();
     		mavContainer.removeAttributes(bindingResultModel);
     		mavContainer.addAllAttributes(bindingResultModel);
     
     		return attribute;
     	}
     	...
     }
     
     ```

     3. supportsParameter方法中调用的相关方法源码

     【ModelAttributeMethodProcessor的supportsParameter方法中的BeanUtils.isSimpleProperty方法】

     ```java
     public abstract class BeanUtils {
     	public static boolean isSimpleProperty(Class<?> type) {
             Assert.notNull(type, "'type' must not be null");
             return isSimpleValueType(type) || type.isArray() && isSimpleValueType(type.getComponentType());//isSimpleValueType(type)判断参数是不是简单类型，type.isArray()判断参数是不是数组，isSimpleValueType(type.getComponentType())是判断啥的没说，主要是不知道type.getComponentType()是什么，反正自定义POJO这个方法返回false，"||"和”&&“优先级相同
         }
     }
     ```

     【BeanUtils.isSimpleProperty方法中的isSimpleValueType方法】

     ```java
     //简单类型包含以下几种
     public static boolean isSimpleValueType(Class<?> type) {
             return Void.class != type && Void.TYPE != type &&
             (ClassUtils.isPrimitiveOrWrapper(type) || 
             Enum.class.isAssignableFrom(type) ||
             CharSequence.class.isAssignableFrom(type) || 
             Number.class.isAssignableFrom(type) || 
             Date.class.isAssignableFrom(type) || 
             Temporal.class.isAssignableFrom(type) || 
             URI.class == type || URL.class == type || 
             Locale.class == type || Class.class == type);
         }
     ```

     4. resolveArgument方法中调用的相关方法源码

        > 此例中是String到Number类型的转换

     【ModelAttributeMethodProcessor的resolveArgument方法中的bindRequestParameters方法】

     ```java
     public class ServletModelAttributeMethodProcessor extends ModelAttributeMethodProcessor {
         ...
     	@Override
     	protected void bindRequestParameters(WebDataBinder binder, NativeWebRequest request) {
     		ServletRequest servletRequest = request.getNativeRequest(ServletRequest.class);//先获取原生的request请求
     		Assert.state(servletRequest != null, "No ServletRequest");
     		ServletRequestDataBinder servletBinder = (ServletRequestDataBinder) binder;//将binder的数据类型向下强转为ServletRequestDataBinder类型并赋值给servletBinder
     		servletBinder.bind(servletRequest);//通过servletBinder的bind方法传入原生的request对象进行请求参数绑定，根据原来的请求参数，原参数类型，目标参数类型获取转换器，利用转换器进行类型转换，底层就是dicode方法和valueOf方法，转换完成后ph调用set方法把属性值绑定到目标属性中，一切都在bind方法中进行的 
     	}
         ...
     }
     ```

     【bindRequestParameters方法中的bind方法】

     ```java
     public class ServletRequestDataBinder extends WebDataBinder {
     	public void bind(ServletRequest request) {
     		MutablePropertyValues mpvs = new ServletRequestParameterPropertyValues(request);//获取原生request中所有的k-v对，mpvs中有一个propertyValueList属性，是一个ArrayList集合，每一个元素都是一个PropertyValue,每个PropertyValue中都保存了请求参数的name和value还有一些其他的属性
     		MultipartRequest multipartRequest = WebUtils.getNativeRequest(request, MultipartRequest.class);
     		if (multipartRequest != null) {
     			bindMultipart(multipartRequest.getMultiFileMap(), mpvs);
     		}
     		else if (StringUtils.startsWithIgnoreCase(request.getContentType(), MediaType.MULTIPART_FORM_DATA_VALUE)) {
     			HttpServletRequest httpServletRequest = WebUtils.getNativeRequest(request, HttpServletRequest.class);
     			if (httpServletRequest != null && HttpMethod.POST.matches(httpServletRequest.getMethod())) {
     				StandardServletPartUtils.bindParts(httpServletRequest, mpvs, isBindEmptyMultipartFiles());
     			}
     		}
     		
             addBindValues(mpvs, request);//添加额外要绑定的值到mpvs中，传参包含所有请求参数的mpvs和原生request，从 request请求域中通过属性名"org.springframework.web.servlet.HandlerMapping.uriTemplateVariables"获取请求域中的一个Map集合并赋值给变量uriVars,如果请求域存在该Map集合，就使用Map流式编程遍历并添加到mpvs中
     		
             doBind(mpvs);//执行正式的绑定工作
     	}
     }
     ```

     MutablePropertyValues mpvs对象中请求参数的存在形式

     + 这里age是数组是因为俺在测试级联属性不加前缀导致的age参数名重复

     ![MutablePropertyValues](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\mvps.png)

     【bind方法中的doBind方法】

     ```java
     public class WebDataBinder extends DataBinder {
     	@Override
     	protected void doBind(MutablePropertyValues mpvs) {
     		checkFieldDefaults(mpvs);
     		checkFieldMarkers(mpvs);//检查哪些属性需要被绑定
     		adaptEmptyArrayIndices(mpvs);
     		super.doBind(mpvs);//调用父类的doBind方法进行绑定
     	}
     }
     ```

     【doBind方法中的super.doBind方法】

     ```java
     public class DataBinder implements PropertyEditorRegistry, TypeConverter {
     	protected void doBind(MutablePropertyValues mpvs) {
     		checkAllowedFields(mpvs);
     		checkRequiredFields(mpvs);
     		applyPropertyValues(mpvs);//应用属性值
     	}
     }
     ```

     【super.doBind方法中的applyPropertyValues方法】

     ```java
     public class DataBinder implements PropertyEditorRegistry, TypeConverter {
     	protected void applyPropertyValues(MutablePropertyValues mpvs) {
     		try {
     			// Bind request parameters onto target object.
     			getPropertyAccessor().setPropertyValues(mpvs, isIgnoreUnknownFields(), isIgnoreInvalidFields());//这一步终于开始绑定属性值了
     		}
     		catch (PropertyBatchUpdateException ex) {
     			// Use bind error processor to create FieldErrors.
     			for (PropertyAccessException pae : ex.getPropertyAccessExceptions()) {
     				getBindingErrorProcessor().processPropertyAccessException(pae, getInternalBindingResult());
     			}
     		}
     	}
     }
     ```

     【applyPropertyValues方法中的setPropertyValues方法】

     ```java
     public abstract class AbstractPropertyAccessor extends TypeConverterSupport implements ConfigurablePropertyAccessor {
     	public void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown, boolean ignoreInvalid) throws BeansException {
             List<PropertyAccessException> propertyAccessExceptions = null;
             List<PropertyValue> propertyValues = pvs instanceof MutablePropertyValues ? ((MutablePropertyValues)pvs).getPropertyValueList() : Arrays.asList(pvs.getPropertyValues());
             //这儿反编译好像有点问题，和雷神上课讲的不一样，雷神的课上讲的是获取所有的属性值pvs，对所有属性值pv(PropertyValue类型)进行遍历，调用setPropertyValue方法把属性值设置到pojo类中
             if (ignoreUnknown) {
                 this.suppressNotWritablePropertyException = true;
             }
     
             try {
                 Iterator var6 = propertyValues.iterator();//这一步就是拿到propertyValues集合的迭代器
     
                 while(var6.hasNext()) {//还有属性值就就继续遍历，以前是增强for循环，现在编程迭代器迭代了
                     PropertyValue pv = (PropertyValue)var6.next();//获取单个属性值
     
                     try {
                         this.setPropertyValue(pv);//这一步就是真正为属性绑定属性值
                     } catch (NotWritablePropertyException var14) {
                         if (!ignoreUnknown) {
                             throw var14;
                         }
                     } 
                     ...额外操作不管
         }
     }
     ```

     【setPropertyValues方法中的setPropertyValue方法】

     ```java
     public abstract class AbstractNestablePropertyAccessor extends AbstractPropertyAccessor {
         
     	public void setPropertyValue(PropertyValue pv) throws BeansException {
             AbstractNestablePropertyAccessor.PropertyTokenHolder tokens = (AbstractNestablePropertyAccessor.PropertyTokenHolder)pv.resolvedTokens;
             if (tokens == null) {
                 String propertyName = pv.getName();//从pv中获取对应属性值的属性名
     
                 AbstractNestablePropertyAccessor nestedPa;//创建一个属性访问器
                 try {
                     
                     //PropertyAccessor是一个反射工具类，getPropertyAccessorForPropertyPath方法获取的是一个属性的访问器，传参是属性名；属性访问器就是nestedPa，类型是BeanWrapperImpl，nestedPa对象中把空的Person对象封装在rootObject属性中
                     nestedPa = this.getPropertyAccessorForPropertyPath(propertyName);
                 
                 } catch (NotReadablePropertyException var6) {
                     throw new NotWritablePropertyException(this.getRootClass(), this.nestedPath + propertyName, "Nested property in path '" + propertyName + "' does not exist", var6);
                 }
     
                 tokens = this.getPropertyNameTokens(this.getFinalPath(nestedPa, propertyName));
                 if (nestedPa == this) {
                     pv.getOriginalPropertyValue().resolvedTokens = tokens;
                 }
     
                 nestedPa.setPropertyValue(tokens, pv);//调用BeanWrapperImpl对象的setPropertyValue方法把属性值绑定进去
             } else {
                 this.setPropertyValue(tokens, pv);
             }
     
         }
     }
     ```

     【setPropertyValue方法中的nestedPa.setPropertyValue方法】

     ```java
     public abstract class AbstractNestablePropertyAccessor extends AbstractPropertyAccessor {
     	protected void setPropertyValue(AbstractNestablePropertyAccessor.PropertyTokenHolder tokens, PropertyValue pv) throws BeansException {
             if (tokens.keys != null) {
                 this.processKeyedProperty(tokens, pv);
             } else {
                 this.processLocalProperty(tokens, pv);//在这一步绑定值，属实是麻了
             }
     
         }
     }
     ```

     【nestedPa.setPropertyValue方法中的processLocalProperty方法】

     ```java
     public abstract class AbstractNestablePropertyAccessor extends AbstractPropertyAccessor {
     	
         private void processLocalProperty(AbstractNestablePropertyAccessor.PropertyTokenHolder tokens, PropertyValue pv) {
             AbstractNestablePropertyAccessor.PropertyHandler ph = this.getLocalPropertyHandler(tokens.actualName);//获取属性处理器ph
             if (ph != null && ph.isWritable()) {
                 Object oldValue = null;
     
                 PropertyChangeEvent propertyChangeEvent;
                 try {
                     Object originalValue = pv.getValue();
                     Object valueToApply = originalValue;//拿到真正的属性值
                     if (!Boolean.FALSE.equals(pv.conversionNecessary)) {
                         if (pv.isConverted()) {
                             valueToApply = pv.getConvertedValue();
                         } else {
                             if (this.isExtractOldValueForEditor() && ph.isReadable()) {
                                 try {
                                     oldValue = ph.getValue();
                                 } catch (Exception var8) {
                                     Exception ex = var8;
                                     if (var8 instanceof PrivilegedActionException) {
                                         ex = ((PrivilegedActionException)var8).getException();
                                     }
     
                                     if (logger.isDebugEnabled()) {
                                         logger.debug("Could not read previous value of property '" + this.nestedPath + tokens.canonicalName + "'", ex);
                                     }
                                 }
                             }
     
                             //核心一步，把字符串类型的18转换成了Integer类型的18
                             valueToApply = this.convertForProperty(tokens.canonicalName, oldValue, originalValue, ph.toTypeDescriptor());//对属性值进行转换，convertForProperty方法进去先调用convertIfNecessary方法判断是否需要类型转换 ，先获取pojo类的属性类型，中间封的太深，反正最后调用valueOf方法转换成相应类型以后一路返回到这儿赋值给valueToApply，这一步此时真正的找数据类型转换器并进行数据转换的一步
                         }
     
                         pv.getOriginalPropertyValue().conversionNecessary = valueToApply != originalValue;
                     }
     
                     ph.setValue(valueToApply);//这一步才真正把转换后的值设置到属性值中，ph中保存了Person对象，之前说过，每一个属性值都经历了这个流程
                 } catch (TypeMismatchException var9) {
                     throw var9;
                 } 
                 ...一些额外操作
         }
     ```

     【processLocalProperty方法中的this.convertForProperty方法解析】

     ```java
     public abstract class AbstractNestablePropertyAccessor extends AbstractPropertyAccessor {
     	@Nullable
     	protected Object convertForProperty(
     			String propertyName, @Nullable Object oldValue, @Nullable Object newValue, TypeDescriptor td)
     			throws TypeMismatchException {
     
     		return convertIfNecessary(propertyName, oldValue, newValue, td.getType(), td);//td.getType()是拿到空pojo的属性值类型，这一步判断有必要就进行类型转换
     	}
     }
     ```

     【this.convertForProperty方法中的convertIfNecessary方法】

     ```java
     public abstract class AbstractNestablePropertyAccessor extends AbstractPropertyAccessor {
     	@Nullable
     	private Object convertIfNecessary(@Nullable String propertyName, @Nullable Object oldValue,
     			@Nullable Object newValue, @Nullable Class<?> requiredType, @Nullable TypeDescriptor td)
     			throws TypeMismatchException {
     
     		Assert.state(this.typeConverterDelegate != null, "No TypeConverterDelegate");//typeConverterDelegate中124个转换器还在这里面的conversionService属性中
     		try {
     			return this.typeConverterDelegate.convertIfNecessary(propertyName, oldValue, newValue, requiredType, td);//这一步调用convertIfNecessary方法进行转换
     		}
     		...扔异常的一些操作
     	}
     }
     ```

     【convertIfNecessary方法中的typeConverterDelegate.convertIfNecessary方法解析】

     ```java
     class TypeConverterDelegate {
     	@SuppressWarnings("unchecked")
     	@Nullable
     	public <T> T convertIfNecessary(@Nullable String propertyName, @Nullable Object oldValue, @Nullable Object newValue,
     			@Nullable Class<T> requiredType, @Nullable TypeDescriptor typeDescriptor) throws IllegalArgumentException {
     
     		// Custom editor for this type?
     		PropertyEditor editor = this.propertyEditorRegistry.findCustomEditor(requiredType, propertyName);
     
     		ConversionFailedException conversionAttemptEx = null;
     
     		// No custom editor but custom ConversionService specified?
             //getConversionService方法分支1
     		ConversionService conversionService = this.propertyEditorRegistry.getConversionService();//这一步拿到了conversionService里面有124个转换器
     		if (editor == null && conversionService != null && newValue != null && typeDescriptor != null) {
     			TypeDescriptor sourceTypeDesc = TypeDescriptor.forObject(newValue);
     			if (conversionService.canConvert(sourceTypeDesc, typeDescriptor)) {//conversionService.canConvert方法判断能不能转换，找的到就返回true并把对应的转换器存入缓存converterCache中
     				try {
                         
                         //convert方法分支2
     					return (T) conversionService.convert(newValue, sourceTypeDesc, typeDescriptor);//调用转换器的convert方法，在这个方法中获取对应类型转换的converter转换器，
     				}
     				catch (ConversionFailedException ex) {
     					// fallback to default conversion logic below
     					conversionAttemptEx = ex;
     				}
     			}
     		}
     		...额外操作
         }
     }
     ```

     【getConversionService方法分支1】

     ------

     【convertIfNecessary方法的canConvert方法】

     ```java
     public class GenericConversionService implements ConfigurableConversionService {
     	public boolean canConvert(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
             Assert.notNull(targetType, "Target type to convert to cannot be null");
             if (sourceType == null) {
                 return true;
             } else {
                 GenericConverter converter = this.getConverter(sourceType, targetType);//getConverter方法获取合适的转换器
                 return converter != null;//如果converter不为空则返回true
             }
         }
     }
     ```

     【canConvert方法中的getConverter方法】

     ```java
     public class GenericConversionService implements ConfigurableConversionService {
     	@Nullable
     	protected GenericConverter getConverter(TypeDescriptor sourceType, TypeDescriptor targetType) {
     		ConverterCacheKey key = new ConverterCacheKey(sourceType, targetType);
     		GenericConverter converter = this.converterCache.get(key);//先尝试从缓存中拿转换器
     		if (converter != null) {
     			return (converter != NO_MATCH ? converter : null);
     		}
     
     		converter = this.converters.find(sourceType, targetType);//第一次缓存中没有转换器会进来调用find方法拿对应类型转换的转换器
     		if (converter == null) {
     			converter = getDefaultConverter(sourceType, targetType);
     		}
     
     		if (converter != null) {
     			this.converterCache.put(key, converter);//这里把找到的converter存入转换器缓存converterCache中
     			return converter;//并且继续向上返回converter
     		}
     
     		this.converterCache.put(key, NO_MATCH);
     		return null;
     	}
     }
     ```

     【getConverter方法中的find方法】

     > 这个GenericConversionService貌似编写了转换器转换规则的所有东西，设置每一个参数值的时候都会在所有converter中去找那个可以将前端请求参数的数据类型转换到指定的pojo属性的数据类型，请求参数的类型也有非字符串类型，比如文件上传就是以流的形式上传，转换器就会把流转换成文件类型进行操作

     ```java
     public class GenericConversionService implements ConfigurableConversionService {
     	@Nullable
         public GenericConverter find(TypeDescriptor sourceType, TypeDescriptor targetType) {
             // Search the full type hierarchy
             List<Class<?>> sourceCandidates = getClassHierarchy(sourceType.getType());//获取请求参数的原类型
             List<Class<?>> targetCandidates = getClassHierarchy(targetType.getType());//获取请求参数的目标类型
             for (Class<?> sourceCandidate : sourceCandidates) {
                 for (Class<?> targetCandidate : targetCandidates) {//增强for循环，遍历所有124个转换器
                     ConvertiblePair convertiblePair = new ConvertiblePair(sourceCandidate, targetCandidate);
                     GenericConverter converter = getRegisteredConverter(sourceType, targetType, convertiblePair);//调用getRegisteredConverter方法获取到当前能用的converter
                     if (converter != null) {
                         return converter;//找到了converter就返回converter
                     }
                 }
             }
             return null;
         }
     }
     ```

     【convert方法分支2】

     ------

     【convertIfNecessary方法的conversionService.convert方法】

     ```java
     public class GenericConversionService implements ConfigurableConversionService {
     	@Override
     	@Nullable
         //source是请求携带的原生请求参数
         //sourceType是原来的参数类型
         //targetType是需要转成pojo中对应的参数类型
     	public Object convert(@Nullable Object source, @Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
     		Assert.notNull(targetType, "Target type to convert to cannot be null");
     		if (sourceType == null) {
     			Assert.isTrue(source == null, "Source must be [null] if source type == [null]");
     			return handleResult(null, targetType, convertNullSource(null, targetType));
     		}
     		if (source != null && !sourceType.getObjectType().isInstance(source)) {
     			throw new IllegalArgumentException("Source to convert from must be an instance of [" +
     					sourceType + "]; instead it was a [" + source.getClass().getName() + "]");
     		}
     		GenericConverter converter = getConverter(sourceType, targetType);//调用getConverter拿到相应的转换器，这个getConverter方法之前说过，就是先从缓存拿，拿不到就去遍历所有的转换器，再将转化器存入缓存
     		if (converter != null) {
     			Object result = ConversionUtils.invokeConverter(converter, source, sourceType, targetType);//能拿到converter就调用静态方法ConversionUtils.invokeConverter对原请求数据进行类型转换
     			return handleResult(sourceType, targetType, result);
     		}
     		return handleConverterNotFound(source, sourceType, targetType);
     	}
     }
     ```

     【convert方法中的ConversionUtils.invokeConverter方法解析】

     ```java
     abstract class ConversionUtils {
     	@Nullable
     	public static Object invokeConverter(GenericConverter converter, @Nullable Object source,
     			TypeDescriptor sourceType, TypeDescriptor targetType) {
     		try {
     			return converter.convert(source, sourceType, targetType);//调用converter的convert方法进行类型转换
     		}
     		catch (ConversionFailedException ex) {
     			throw ex;
     		}
     		catch (Throwable ex) {
     			throw new ConversionFailedException(sourceType, targetType, source, ex);
     		}
     	}
     }
     ```

     【invokeConverter方法中的converter.convert方法解析】

     ```java
     public class GenericConversionService implements ConfigurableConversionService {
     	@Override
         @Nullable
         public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
             if (source == null) {
                 return convertNullSource(sourceType, targetType);
             }
             return this.converterFactory.getConverter(targetType.getObjectType()).convert(source);//这一步是通过converterFactory获取converter，获取完converter后立即调用converter的convert方法，convert方法又去调用NumberUtils.parseNumber方法
         }
     }
     ```

     【convert方法中的this.converterFactory.getConverter方法】

     > 最终使用的String转Number用的StringToNumberConverterFactory【String到Number的转换工厂】

     ```java
     final class StringToNumberConverterFactory implements ConverterFactory<String, Number> {
     	@Override
     	public <T extends Number> Converter<String, T> getConverter(Class<T> targetType) {
     		return new StringToNumber<>(targetType);//传入目标属性的类型创建一个StringToNumber对象，该类是一个私有类
     	}
         
         //这是一个私有类【StringToNumber类型：这就是一个String转换到Number类型的转换器】
         private static final class StringToNumber<T extends Number> implements Converter<String, T> {
     	private final Class<T> targetType;
     
         public StringToNumber(Class<T> targetType) {
             this.targetType = targetType;
         }
     
         @Override
         @Nullable
         public T convert(String source) {
             if (source.isEmpty()) {
                 return null;
             }
             return NumberUtils.parseNumber(source, this.targetType);//实际String到Number类型的转换在获取完上诉转换器后马上调用的是这个方法convert方法再调用NumberUtils.parseNumber方法进行类型转换
         }
     }
     }
     ```

     > 由此启发：我们可以依葫芦画瓢自定义一些类型转换器，T表示要转换的类型，由Converter接口继承来，在实现类中指定T要继承的目标类型，【还有这个用法，T还是妙啊】

     【转换器中convert方法中的NumberUtils.parseNumber方法】

     > md封了这么多层最后还不是valueOf，服了

     ```java
     public abstract class NumberUtils {
     	@SuppressWarnings("unchecked")
     	public static <T extends Number> T parseNumber(String text, Class<T> targetClass) {
     		Assert.notNull(text, "Text must not be null");
     		Assert.notNull(targetClass, "Target class must not be null");
     		String trimmed = StringUtils.trimAllWhitespace(text);//这特么是啥，这个好像就是请求数据参数值的字符串
     
             //看pojo类的属性是什么类型，根据类型去执行对应的转换方法
     		if (Byte.class == targetClass) {
     			return (T) (isHexNumber(trimmed) ? Byte.decode(trimmed) : Byte.valueOf(trimmed));
     		}
     		else if (Short.class == targetClass) {
     			return (T) (isHexNumber(trimmed) ? Short.decode(trimmed) : Short.valueOf(trimmed));
     		}
     		else if (Integer.class == targetClass) {
     			return (T) (isHexNumber(trimmed) ? Integer.decode(trimmed) : Integer.valueOf(trimmed));//isHexNumber好像是进行一些特殊符号判断的，判断结果为true就调用decode方法，为false就调用valueOf方法把字符串转换成integer类型，显然这里调用的是valueOf方法，雷神讲错了，然后这个值一路返回上一个方法，最终返回到valueToApply
     		}
     		...相似的类型判断
     	}
     }
     ```

3. WebDataBinder的配置

   + Web数据绑定器是使用ConfigurableWebBindingInitializer自动向容器中配置的数据绑定器，ConfigurableWebBindingInitializer的调用者之一是WebMvcAutoConfiguration的getConfigurableWebBindingInitializer方法通过ConfigurableWebBindingInitializer.class给容器中配置了一个ConfigurableWebBindingInitializer类型的数据绑定器，ConversionService也是从该容器中拿的；

   + ConfigurableWebBindingInitializer中有一个initBinder方法，该方法给WebDataBinder中设置了各种东西，其中之一就是conversionService类型转换器，ConversionService接口的子接口ConfigurableConversionService接口的实现类GenericConversionService中有非常多的converter转换器，这些Converter就是来进行类型转换的，所有converter的总接口就是Converter<S,T>，这是一个函数式接口：S表示SourceType，T表示TargetType

     

4. 自定义Converter

   ```html
   <!--宠物姓名:<input name="pet.name" value="阿猫"><br>-->
   <!--宠物年龄:<input name="pet.age" value="5"><br>-->
   <!--宠物年龄:<input name="age" value="5"><br>-->
   
   <!--公司现在认为宠物的数据分开组织不好，不想使用级联属性命名的方式，想自己组织如下的数据形式，
   后端如何自定义类型转换器来处理这种数据进行对象的封装呢，如果不管直接发送请求，服务器会提示数据绑定错误：错误
   信息是person的pet属性绑定"阿猫,3"发生了类型不匹配问题，String类型转换成Pet属性转换不过来，即springMVC不知道
   怎么将"阿猫,3"按某种规则封装成宠物对象，因为底层转换是converter负责的，我们只需要自定义一个Converter即可-->
   宠物:<input name="pet" value="阿猫,3">
   ```

   + 对SpringMVC的定制都采用给容器中放一个WebMvcConfigurer组件的方式，该组件中扩展的所有功能都能使用，WebMvcConfigurer是一个接口，里面有一个默认实现的方法叫做addFormatters，意为添加一些格式化器

     ```java
     public interface WebMvcConfigurer {
     
     	/**
     	 * Help with configuring {@link HandlerMapping} path matching options such as
     	 * whether to use parsed {@code PathPatterns} or String pattern matching
     	 * with {@code PathMatcher}, whether to match trailing slashes, and more.
     	 * @since 4.0.3
     	 * @see PathMatchConfigurer
     	 * 之前做过开启矩阵变量功能，添加自己配置的urlPathHelper，把removeSemicolonContent属性设置成false
     	 */
     	default void configurePathMatch(PathMatchConfigurer configurer) {
             /*configurer中有一个configurer.setUrlPathHelper(urlPathHelper);方法可以添加urlPathHelper*/
     	}
     
     	...
     	
     	/**
     	 * Add {@link Converter Converters} and {@link Formatter Formatters} in addition to the ones
     	 * registered by default.
     	 * 注释信息是可以给SpringMVC添加一些自定义的类型转换器和格式化器，一起请求数据日期和钱不仅涉及到String类型向其他类型的转换，还涉及到格式的转换如斜杠、横线、逗号等等，此例自定义前端页面数据提交格式只涉及到类型转换
     	 */
     	default void addFormatters(FormatterRegistry registry) {
             /*registry中有一个registry.addConverter(converter);方法可以添加converter*/
     	}
     	...
     }
     ```

   + 具体实现自定义Converter代码

     + 步骤一：在配置类中使用@Bean注解添加WebMvcConfigurer组件，感觉这个写法也是匿名内部类
     + 步骤二：在WebMvcConfigurer的匿名内部类中重写addFormatters方法，传参是registry
     + 步骤三：在addFormatters方法中调用registry.addConverter方法添加以匿名内部类的方式创建的自定义converter对象，并重写convert方法，传入的source就是页面提交会赋值给目标类的请求参数，在convert方法中完成对应的参数解析，并给属性值赋值即可

     > 这样，当SpringMVC拿着"阿猫,3"要去给pet属性赋值时，需要把字符串的"阿猫,3"转换为Pet类型，一查有这个转换器，就用这个转换器的convert方法传入"阿猫,3"并返回Pet类型的对象，此时创建Pet对象和给参数值赋值都是用户自己完成的，不像之前springMVC自己创建空pojo

     ```java
     @Configuration(proxyBeanMethods = false)
     public class WebConfig /*implements WebMvcConfigurer*/ {
         //方式一:使用@Bean配置一个WebMvcConfigurer组件
         @Bean
         public WebMvcConfigurer webMvcConfigurer(){
             return new WebMvcConfigurer() {//woc这是什么写法，没见过，记录一下，感觉像匿名内部类
                 @Override//重写addFormatters方法，添加自己写的匿名内部类converter对象进去
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
     }
     ```

     

## 4.5、响应数据处理

> 即控制器方法的返回值在SpringMVC中的处理过程

![在这里插入图片描述](C:/Users/Earl/Desktop/SpringBoot2学习笔记/image/20210205010403920.jpg)

### 4.5.1、返回值处理器原理

> ReturnValueHandler，这个在核心对象中已经进行了基本的讲解，这里讲一下返回值处理器的两个方法
>
> 返回值处理的核心流程【这个处理过程是在handle方法中执行完执行目标方法获取ModelAndView之前进行的】
>
> + 遍历返回值处理器，调用所有返回值处理器的selectHandler方法获取合适的返回值处理器
> + 通过获取的返回值处理器调用其handleReturnValue方法处理返回值
>   + 在处理响应json的返回值处理器中的handleReturnValue方法中调用writeWithMessageConverters方法通过消息转换器MessageConverter进行处理，实现将返回数据写为json

#### 01、响应json

> jackson.jar+@ResponseBody
>
> 这种方式只要引入web场景，给控制器方法添加@ResponseBody注解就会自动给前端响应json数据格式的字符串

1. 引入starter-web依赖

   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-web</artifactId>
   </dependency>
   ```

   [^注意1]: web场景会自动引入了json场景,starter-web依赖于starter-json，starter-json又依赖于Jackson的相关依赖

   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-json</artifactId>
       <version>2.3.4.RELEASE</version>
       <scope>compile</scope>
   </dependency>
   ```

2. 给控制器方法标注@ResponseBody注解

   > 能够处理标注了@ResponseBody注解的返回值处理器是RequestResponseBodyMethodProcessor

3. 控制器方法返回一个对象会自动返回对应的json格式字符串

4. 源码分析

   > 4.4.2中的重点3中的【***invokeAndHandle方法中的this.returnValueHandlers.handleReturnValue方法解析***】已经对处理返回值的代码进行过简单解析了，这里只讲重点
   >
   > 标注了@ResponseBody注解的控制器方法会利用RequestResponseBodyMethodProcessor返回值处理器中的消息转换器进行处理，把对象自动转成json格式的Byte数组会使用处理器中的消息转换器MappingJackson2HttpMessageConverter将对象转成json

   【***invokeHandlerMethod方法中的invokeAndHandle方法解析***】

   + 重点：调用this.returnValueHandlers.handleReturnValue方法，传参返回值、返回值类型、webRequest、mavContainer；handleReturnValue方法是开始处理控制器方法的返回结果的核心方法
   
   ```java
   public class ServletInvocableHandlerMethod extends InvocableHandlerMethod {
   	public void invokeAndHandle(ServletWebRequest webRequest, ModelAndViewContainer mavContainer,
   			Object... providedArgs) throws Exception {
   
   		Object returnValue = invokeForRequest(webRequest, mavContainer, providedArgs);
           setResponseStatus(webRequest);
   		if (returnValue == null) {//如果控制器方法返回null，则invokeAndHandle方法直接返回
   			if (isRequestNotModified(webRequest) || getResponseStatus() != null || mavContainer.isRequestHandled()) {
   				disableContentCachingIfNecessary(webRequest);
   				mavContainer.setRequestHandled(true);
   				return;
   			}
   		}
   		else if (StringUtils.hasText(getResponseStatusReason())) {//这个检测返回值中有没有一些失败原因
   			mavContainer.setRequestHandled(true);
   			return;
   		}
           
   		//如果有返回值且返回值不是字符串，就会执行以下代码
   		mavContainer.setRequestHandled(false);
   		Assert.state(this.returnValueHandlers != null, "No return value handlers");
   		try {
   			this.returnValueHandlers.handleReturnValue(
   					returnValue, getReturnValueType(returnValue), mavContainer, webRequest);//this.returnValueHandlers.handleReturnValue方法是开始处理控制器方法的返回结果，其实是把返回值的字符串或者其他东西处理赋值给mavContainer的view属性，getReturnValueType(returnValue)是获取返回值的类型，实际上获取的是HandlerMethod$ReturnValueMethodParameter类型的对象，其中的returnValue属性保存的是返回值，executable属性指向的对象的name属性为对应控制的方法名，returnType保存的是返回值类型，这一段代码就是处理返回值的核心代码
   		}
   		catch (Exception ex) {
   			if (logger.isTraceEnabled()) {
   				logger.trace(formatErrorForReturnValue(returnValue), ex);
   			}
   			throw ex;
   		}
   	}
   }
   ```

   【invokeAndHandle方法中的handleReturnValue方法】

   > + 返回值处理器都继承了HandlerMethodReturnValueHandler接口，重写了其中的用于判断是否支持当前类型返回值returnType的supportsReturnType方法和对返回值进行处理的handleReturnValue方法
   
   ```java
   public class HandlerMethodReturnValueHandlerComposite implements HandlerMethodReturnValueHandler {
       @Override
       public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType,
               ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
   
           //分支一:
           HandlerMethodReturnValueHandler handler = selectHandler(returnValue, returnType);//先通过返回值和返回值类型调用selectHandler方法找到能处理对应返回值的返回值处理器
           if (handler == null) {
               throw new IllegalArgumentException("Unknown return value type: " + returnType.getParameterType().getName());
           }
           
           //分支二：
           handler.handleReturnValue(returnValue, returnType, mavContainer, webRequest);//通过返回值处理器调用handleReturnValue方法来处理返回值，和参数解析器的原理是一样的
       }
   }
   ```

   【分支一：selectHandler方法分支】

   ------
   
   【handleReturnValue方法中的selectHandler方法】
   
   ```java
   public class HandlerMethodReturnValueHandlerComposite implements HandlerMethodReturnValueHandler {
   	@Nullable
   	private HandlerMethodReturnValueHandler selectHandler(@Nullable Object value, MethodParameter returnType) {
           //小分支一
   		boolean isAsyncValue = isAsyncReturnValue(value, returnType);//判断返回值类型是否异步返回值，对每个返回值处理器进行遍历，判断每个handler是否AsyncHandlerMethodReturnValueHandler类型，并且调用对应的isAsyncReturnValue传参控制器方法传参和参数类型判断判断是否异步返回值
   		for (HandlerMethodReturnValueHandler handler : this.returnValueHandlers) {
   			if (isAsyncValue && !(handler instanceof AsyncHandlerMethodReturnValueHandler)) {
   				continue; 
   			}
   			if (handler.supportsReturnType(returnType)) {//判断处理器是否支持该类型的返回值
   				return handler;
   			}
   		}
   		return null;
   	}
   }
   ```

   【selectHandler方法中的supportsReturnType(returnType)方法】
   
   > 因为这里遍历会调用所有返回值处理器的supportsReturnType方法，这里只以第一个ModelAndViewMethodReturnValueHandler为例
   
   ```java
   public class ModelAndViewMethodReturnValueHandler implements HandlerMethodReturnValueHandler {
       ...
   	@Override
   	public boolean supportsReturnType(MethodParameter returnType) {
   		return ModelAndView.class.isAssignableFrom(returnType.getParameterType());//判断ModelAndViewMethodReturnValueHandler返回值处理器是否支持当前返回值是判断当前返回值是否ModelAndView类型，所以该处理器不支持当前返回值;一般判断方式就是判断返回值类型是否是特定的某种类型，由这些处理器的判断方法可以知道SpringMVC一共支持哪些返回值类型，如下列表所示
   	}
   
   	@Override
   	public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType,ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
   		...
   	}
       ...
   }
   ```

   【分支二：handleReturnValue方法分支】

   ------
   
   
   > 这里返回person类型的字符串添加了@ResponseBody注解，对应的返回值处理器是RequestResponseBodyMethodProcessor，分析handleReturnValue方法选取相应返回值处理器的
   
   【handleReturnValue方法中的handler.handleReturnValue方法】
   
   ```java
   public class RequestResponseBodyMethodProcessor extends AbstractMessageConverterMethodProcessor {
   	@Override
   	public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType,
   			ModelAndViewContainer mavContainer, NativeWebRequest webRequest)
   			throws IOException, HttpMediaTypeNotAcceptableException, HttpMessageNotWritableException {
   
   		mavContainer.setRequestHandled(true);
           //对webRequest和webrequest进行进一步封装
   		ServletServerHttpRequest inputMessage = createInputMessage(webRequest);
   		ServletServerHttpResponse outputMessage = createOutputMessage(webRequest);
   
   		// Try even with null return value. ResponseBodyAdvice could get involved.
   		writeWithMessageConverters(returnValue, returnType, inputMessage, outputMessage);//将返回值，返回值类型，请求以及响应都传递给writeWithMessageConverters方法，作用是使用消息转换器进行写出操作
   	}
   }
   ```

   【handler.handleReturnValue方法中的writeWithMessageConverters方法】

   + 媒体类型涉及内容协商

     + 在请求头的Accept相关信息中声明了浏览器能接受的响应内容类型

       [^要点1]: 下图表示能接受的类型包括html、xml、图片、`*/*`表示给任意响应内容都能接受，逗号比分号优先级高；q表示权重，没写表示权重为1，浏览器会优先选择接受权重更高的响应类型；服务器也会根据请求头中的内容进行内容协商

       ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\请求头Accept.png)

     + 服务器会根据自己自身的能力，决定服务器能生产出什么样内容类型的数据
   
     + SpringMVC会挨个遍历所有容器底层的HttpMessageConverter【HttpMessageConverter是所有消息转换器都要实现的接口】查找能处理相应响应内容的消息转换器
   
       [^要点1]: canRead和canWrite是判断该消息转换器是否支持把Class对象对应的返回值对象的class对象（这里是person.getClass）以MediaType内容类型(这里是json)的形式读进来以及是否支持讲返回值对象的class对象以MediaType内容类型(这里是json)的形式写出去
   
       ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\消息转换器的父接口.png)
       
       + 响应Json会找到MappingJackson2HttpMessageConverter，该消息转换器可以把对象转成json写出到浏览器，在writeSuffix方法一执行完浏览器就会显示对应的结果，此时还在handle方法中
   
   ```java
   public abstract class AbstractMessageConverterMethodProcessor extends AbstractMessageConverterMethodArgumentResolver
   		implements HandlerMethodReturnValueHandler {
   	@SuppressWarnings({"rawtypes", "unchecked"})
   	protected <T> void writeWithMessageConverters(@Nullable T value, MethodParameter returnType,ServletServerHttpRequest inputMessage, ServletServerHttpResponse outputMessage) throws IOException, HttpMediaTypeNotAcceptableException, HttpMessageNotWritableException {
   
   		Object body;
   		Class<?> valueType;
   		Type targetType;
   
   		if (value instanceof CharSequence) {//判断值是不是字符串类型
   			body = value.toString();
   			valueType = String.class;
   			targetType = String.class;
   		}
   		else {
   			body = value;//把值赋值给body
   			valueType = getReturnValueType(body, returnType);//把返回值类型赋值给valueType
   			targetType = GenericTypeResolver.resolveType(getGenericType(returnType), returnType.getContainingClass());//要转成的类型赋值给targetType，这里两个都是person类型?难不成你这里还可以自动类型转换？
   		}
   
   		if (isResourceType(value, returnType)) {//判断是不是资源类型，这里判断的是返回值类型是不是InputStreamResource类型或者Resource类型，即如果是流数据就调用下方对流数据的相应处理办法
   			outputMessage.getHeaders().set(HttpHeaders.ACCEPT_RANGES, "bytes");
   			if (value != null && inputMessage.getHeaders().getFirst(HttpHeaders.RANGE) != null &&
   					outputMessage.getServletResponse().getStatus() == 200) {
   				Resource resource = (Resource) value;
   				try {
   					List<HttpRange> httpRanges = inputMessage.getHeaders().getRange();
   					outputMessage.getServletResponse().setStatus(HttpStatus.PARTIAL_CONTENT.value());
   					body = HttpRange.toResourceRegions(httpRanges, resource);
   					valueType = body.getClass();
   					targetType = RESOURCE_REGION_LIST_TYPE;
   				}
   				catch (IllegalArgumentException ex) {
   					outputMessage.getHeaders().set(HttpHeaders.CONTENT_RANGE, "bytes */" + resource.contentLength());
   					outputMessage.getServletResponse().setStatus(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.value());
   				}
   			}
   		}
   
           //不是流数据就到这儿
   		MediaType selectedMediaType = null;//MediaType是媒体类型，媒体类型牵扯到内容协商，selectedMediaType意为被选中的媒体类型；内容协商是指：浏览器在发送请求时就要求了响应数据的格式，在请求头的Accept、Accept-Encoding、Accept-Language内容下就对响应内容的规则做了要求；以下代码就是进行内容协商的，这里先不管
   		MediaType contentType = outputMessage.getHeaders().getContentType();//先看响应头有没有内容类型，可能存在前置处理已经设定好响应内容的内容类型了
   		boolean isContentTypePreset = contentType != null && contentType.isConcrete();
   		if (isContentTypePreset) {
   			if (logger.isDebugEnabled()) {
   				logger.debug("Found 'Content-Type:" + contentType + "' in response");
   			}
   			selectedMediaType = contentType;//如果之前响应头已经有了内容类型就直接用相应的内容类型
   		}
   		else {//如果没有前置的响应处理，没有设置响应内容类型，就执行以下代码来决定响应内容类型
   			HttpServletRequest request = inputMessage.getServletRequest();//拿到原生的request对象
   			List<MediaType> acceptableTypes;
   			try {
   				acceptableTypes = getAcceptableMediaTypes(request);//通过原生请求对象获取能接受的内容类型，这里能接受的内容类型有七种，就是对应上述截图请求头中相应的七种内容类型
   			}
   			catch (HttpMediaTypeNotAcceptableException ex) {
   				int series = outputMessage.getServletResponse().getStatus() / 100;
   				if (body == null || series == 4 || series == 5) {
   					if (logger.isDebugEnabled()) {
   						logger.debug("Ignoring error response content (if any). " + ex);
   					}
   					return;
   				}
   				throw ex;
   			}
   			List<MediaType> producibleTypes = getProducibleMediaTypes(request, valueType, targetType);//这个是服务器能响应的内容类型，其中包含四种内容类型，全是json类型的数据，我这儿测试的和雷神演示的是一样的，getProducibleMediaTypes方法中就牵涉到内容协商原理，在该方法中服务器根据自身的的能力，决定服务器能生产处什么内容类型的数据
   
   			if (body != null && producibleTypes.isEmpty()) {
   				throw new HttpMessageNotWritableException(
   						"No converter found for return value of type: " + valueType);
   			}
   			List<MediaType> mediaTypesToUse = new ArrayList<>();
   			for (MediaType requestedType : acceptableTypes) {//对浏览器可接受内容类型遍历
   				for (MediaType producibleType : producibleTypes) {//对服务器可生产内容类型遍历
   					if (requestedType.isCompatibleWith(producibleType)) {//根据生产内容类型对浏览器可接受内容类型进行匹配，服务器能生产且浏览器能接受，符合要求就把相应内容类型放入一个ArrayList数组mediaTypesToUse，因为这里的示例浏览器能接受*/*，所以四种json浏览器都能接受，mediaTypesToUse中最终存放四种json相关的内容类型
   						mediaTypesToUse.add(getMostSpecificMediaType(requestedType, producibleType));
   					}
   				}
   			}
   			if (mediaTypesToUse.isEmpty()) {
   				if (logger.isDebugEnabled()) {
   					logger.debug("No match for " + acceptableTypes + ", supported: " + producibleTypes);
   				}
   				if (body != null) {
   					throw new HttpMediaTypeNotAcceptableException(producibleTypes);
   				}
   				return;
   			}
   
   			MediaType.sortBySpecificityAndQuality(mediaTypesToUse);
   
   			for (MediaType mediaType : mediaTypesToUse) {//这一步讲的不清楚
   				if (mediaType.isConcrete()) {
   					selectedMediaType = mediaType;
   					break;
   				}
   				else if (mediaType.isPresentIn(ALL_APPLICATION_MEDIA_TYPES)) {
   					selectedMediaType = MediaType.APPLICATION_OCTET_STREAM;
   					break;
   				}
   			}
   
   			if (logger.isDebugEnabled()) {
   				logger.debug("Using '" + selectedMediaType + "', given " +
   						acceptableTypes + " and supported " + producibleTypes);
   			}
   		}
   
   		if (selectedMediaType != null) {
   			selectedMediaType = selectedMediaType.removeQualityValue();
   			for (HttpMessageConverter<?> converter : this.messageConverters) {// this.messageConverters是所有的MessageConverter，SpringMVC遍历所有HttpMessageConverter，HttpMessageConverter是所有消息转换器要实现的接口
   				GenericHttpMessageConverter genericConverter = (converter instanceof GenericHttpMessageConverter ?
   						(GenericHttpMessageConverter<?>) converter : null);//判断converter是不是GenericHttpMessageConverter类型的，是的话就强转成该类型，不是就直接赋值null
   				if (genericConverter != null ?
   						((GenericHttpMessageConverter) converter).canWrite(targetType, valueType, selectedMediaType) ://被遍历到的converter调用canWrite方法判断能不能支持对应返回值的写操作，判断方法一般是converter在canWrite方法中调用support方法判断是否支持对应类型的返回值，对应消息转换器支持的返回值见4.2.2中核心对象并且调用重载canWrite方法(mediaType)判断是否支持对应媒体类型的写操作，这个canWrite方法都是各个转化器中自己重写的方法，包括调用的子方法也是；这个方法就是判断canWrite方法能不能适配对应返回值类型，针对是不是GenericHttpMessageConverter进行了强制类型转换，如果有一个转换器支持就执行下述代码
   						converter.canWrite(valueType, selectedMediaType)) {
   					body = getAdvice().beforeBodyWrite(body, returnType, selectedMediaType,
   							(Class<? extends HttpMessageConverter<?>>) converter.getClass(),
   							inputMessage, outputMessage);//获取需要响应的内容，这里用例就是对应的person对象
   					if (body != null) {//这里是把响应内容写出json的格式
   						Object theBody = body;
   						LogFormatUtils.traceDebug(logger, traceOn ->
   								"Writing [" + LogFormatUtils.formatValue(theBody, !traceOn) + "]");
   						addContentDispositionHeader(inputMessage, outputMessage);//这里是加一些头信息
   						if (genericConverter != null) {
   							genericConverter.write(body, targetType, selectedMediaType, outputMessage);//使用消息转换器的write方法
   						}
   						else {
   							((HttpMessageConverter) converter).write(body, selectedMediaType, outputMessage);
   						}
   					}
   					else {
   						if (logger.isDebugEnabled()) {
   							logger.debug("Nothing to write: null body");
   						}
   					}
   					return;
   				}
   			}
   		}
   
   		if (body != null) {
   			Set<MediaType> producibleMediaTypes =
   					(Set<MediaType>) inputMessage.getServletRequest()
   							.getAttribute(HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE);
   
   			if (isContentTypePreset || !CollectionUtils.isEmpty(producibleMediaTypes)) {
   				throw new HttpMessageNotWritableException(
   						"No converter for [" + valueType + "] with preset Content-Type '" + contentType + "'");
   			}
   			throw new HttpMediaTypeNotAcceptableException(getSupportedMediaTypes(body.getClass()));
   		}
   	}
   }
   ```
   
   【writeWithMessageConverters方法中的write方法】
   
   ```java
   public abstract class AbstractGenericHttpMessageConverter<T> extends AbstractHttpMessageConverter<T> implements GenericHttpMessageConverter<T> {
   	@Override
   	public final void write(final T t, @Nullable final Type type, @Nullable MediaType contentType,
   			HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
   
   		final HttpHeaders headers = outputMessage.getHeaders();//拿到响应头，此时响应头中什么都没有，headers是ServletServerHttpResponse$ServletResponseHttpHeader类型
   		addDefaultHeaders(headers, t, contentType);//向headers添加一个默认的响应头，此例添加的是一个Content-Type，是一个LinkedList集合，key是Content-Type，value是application/json
   
   		if (outputMessage instanceof StreamingHttpOutputMessage) {
   			StreamingHttpOutputMessage streamingOutputMessage = (StreamingHttpOutputMessage) outputMessage;
   			streamingOutputMessage.setBody(outputStream -> writeInternal(t, type, new HttpOutputMessage() {
   				@Override
   				public OutputStream getBody() {
   					return outputStream;
   				}
   				@Override
   				public HttpHeaders getHeaders() {
   					return headers;
   				}
   			}));
   		}
   		else {
   			writeInternal(t, type, outputMessage);//把数据（即person）、数据原类型(Person)、outputMessage是ServletServerHttpResponse类型，是响应对象
   			outputMessage.getBody().flush();
   		}
   	}
   }
   ```
   
   【write方法中的writeInternal方法】
   
   > 注意，这个writeInternal方法是消息转换器MappingJackson2HttpMessageConverter继承于AbstractGenericHttpMessageConverter的，在这个方法中实现了把对象转成json格式的Byte数组
   
   ```java
   public abstract class AbstractJackson2HttpMessageConverter extends AbstractGenericHttpMessageConverter<Object> {
   	@Override
   	protected void writeInternal(Object object, @Nullable Type type, HttpOutputMessage outputMessage)
   			throws IOException, HttpMessageNotWritableException {
   
           //这是把对象转json的流程
   		MediaType contentType = outputMessage.getHeaders().getContentType();
   		JsonEncoding encoding = getJsonEncoding(contentType);
   
   		Class<?> clazz = (object instanceof MappingJacksonValue ?
   				((MappingJacksonValue) object).getValue().getClass() : object.getClass());
   		ObjectMapper objectMapper = selectObjectMapper(clazz, contentType);
   		Assert.state(objectMapper != null, () -> "No ObjectMapper for " + clazz.getName());
   
   		OutputStream outputStream = StreamUtils.nonClosing(outputMessage.getBody());
   		try (JsonGenerator generator = objectMapper.getFactory().createGenerator(outputStream, encoding)) {//拿到生成器generator
   			writePrefix(generator, object);
   
   			Object value = object;//把返回值对象赋值给value
   			Class<?> serializationView = null;//创造一些空引用
   			FilterProvider filters = null;
   			JavaType javaType = null;
   
   			if (object instanceof MappingJacksonValue) {
   				MappingJacksonValue container = (MappingJacksonValue) object;
   				value = container.getValue();
   				serializationView = container.getSerializationView();
   				filters = container.getFilters();
   			}
   			if (type != null && TypeUtils.isAssignable(type, value.getClass())) {
   				javaType = getJavaType(type, null);
   			}
   
   			ObjectWriter objectWriter = (serializationView != null ?
   					objectMapper.writerWithView(serializationView) : objectMapper.writer());//拿到objectWriter
   			if (filters != null) {
   				objectWriter = objectWriter.with(filters);
   			}
   			if (javaType != null && javaType.isContainerType()) {
   				objectWriter = objectWriter.forType(javaType);
   			}
   			SerializationConfig config = objectWriter.getConfig();
   			if (contentType != null && contentType.isCompatibleWith(MediaType.TEXT_EVENT_STREAM) &&
   					config.isEnabled(SerializationFeature.INDENT_OUTPUT)) {
   				objectWriter = objectWriter.with(this.ssePrettyPrinter);
   			}
   			objectWriter.writeValue(generator, value);//在这一步将对象以json格式写出去
   
   			writeSuffix(generator, object);//write之前object还是一个person对象，会将json写给outputMessage，该对象的ServletResponse的response的outputBuffer的bb属性中存放了这个json格式的Byte数组，我这上面的版本改了，直接在generator的outputBuffer属性就存放了json格式字符串数组，卧槽执行完这一步就能直接在浏览器看到结果
   			generator.flush();
   		}
   		catch (InvalidDefinitionException ex) {
   			throw new HttpMessageConversionException("Type definition error: " + ex.getType(), ex);
   		}
   		catch (JsonProcessingException ex) {
   			throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getOriginalMessage(), ex);
   		}
   	}
   }
   ```
   

#### 02、内容协商原理

> 根据不同的返回值类型或者控制器方法上标注的注解，SpringMVC底层会选择不同的返回值处理器，返回值处理器会根据转换返回值类型和服务器能提供的内容类型匹配合适的消息转换器，选择消息转换器的一个重点就是内容协商，通过遍历所有的消息转换器，最终找到一个能合适处理对应媒体类型的消息转换器
>
> 内容协商的核心是根据客户端的不同，返回不同媒体类型的数据		

[^注意]: 以下以返回xml为例

1. 操作流程

   + 第一步：引入XML依赖【jackson也支持XML类型的响应内容类型，需要引入对应的依赖，不像默认的json，自动就引入了，这个需要手动引入，不需要管理版本】

     ```xml
     <dependency>
         <groupId>com.fasterxml.jackson.dataformat</groupId>
         <artifactId>jackson-dataformat-xml</artifactId>
     </dependency>
     ```

     [^注意]: 只要引入这个依赖发送响应json的请求会自动响应xml，因为浏览器的Accept的xml接收的优先级高于json，具体要看客户端请求发送情况，SpringMVC会根据客户端接受的内容类型和优先级自动调整发送的内容形式如json或者xml

   + 第二步：使用postman发送一个Accept为xml的请求

     > 只需要更改请求头中的Accept字段，告诉服务器服务器该客户端可以接受的类型是application/json、application/xml

     ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\postMan请求.png)

   + 第三步：内容协商源码跟踪

     > 这一部分的流程在响应json就已经跟过了，只是内容协商部分这里进行了细讲
     
     + 要点一：如果内容协商前拦截器对响应进行了部分处理，可能已经确定了内容响应的内容类型，这时候直接设置内容类型为已经设定的内容类型
     
     + 要点二：之前没有对响应进行处理就调用getAcceptableMediaTypes方法可以获取客户端请求头的Accept字段 【application/xml】，查询出所有客户端能接收的内容类型
     
       + 通过contentNegotiationManager内容协商管理器【里面有一个strategies的ArrayList，保存了HeaderContentNegotiationStrategy，表示内容协商管理器默认使用基于请求头的内容协商策略】
     
       + 下图是内容协商管理器中的所有策略
     
         ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\内容协商管理器的所有策略.png)
     
         + 内容协商策略是一个函数式接口ContentNegotiationStrategy，有非常多的实现类，可以基于请求头，基于路径、扩展变量等等等等，HeaderContentNegotiationStrategy就是其中一个基于请求头的
         + 所以strategy.resolveMediaTypes(request)调用的是HeaderContentNegotiationStrategy基于请求头的resolveMediaTypes方法来确定客户端可以接收的内容类型
     
       + 使用PostMan可以自定义Accept字段，浏览器无法自定义请求头，除非浏览器发送Ajax时指定请求头的Content-Type属性，针对浏览器无法更改请求头Accept字段，SpringMVC底层也实现了针对浏览器内容协商的快速支持
     
     + 要点三：遍历循环所有当前系统的MessageConverter，找到支持操作对应对象的消息转换器，把converter支持的媒体类型统计出来【application/json、application/*+json】(MappinJackson2能支持自定义对象的原因是supports方法直接返回true)，服务端根据返回值类型和消息转换器统计出能支持的若干种可以转换的类型，这里对应Person有json、xml相关的十种处理能力
     
       【result中具体保存的媒体类型】?为什么有不同元素相同对象的
     
       > result中保存了当前系统对当次请求支持返回json和xml类型的数据类型的统计结果
     
       ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\支持的内容类型.png)
     
     + 要点四：进行内容协商的最佳匹配
     
       + 先遍历可以接收的媒体类型，嵌套遍历可以产生的媒体类型，将二者都有的媒体类型添加到mediaTypesToUse中，这里面保存了对应的媒体类型和相应权重
     
       + 然后对mediaTypesToUse简单排序和依次判断，从前到后优先选取第一个有效的媒体类型存入selectedMediaType中，排序会把权重高的放在前面
     
         ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\媒体类型排序后.png)
     
       ```java
       for (MediaType requestedType : acceptableTypes) {//先遍历浏览器能接收的内容类型
           for (MediaType producibleType : producibleTypes) {//在选定可接收内容类型的前提下选定服务器可以产生的内容类型
               if (requestedType.isCompatibleWith(producibleType)) {//服务器接收类型和产生类型能否匹配，如果能匹配就把该内容类型加到mediaTypesToUse中，这个循环的目的就是匹配客户端需要的刚好我又能提供的媒体类型
               mediaTypesToUse.add(getMostSpecificMediaType(requestedType, producibleType));
               }
           }
       }
       ```
     
     + 要点五：选择能处理对应媒体类型的消息转换器
     
       + 根据selectedMediaType中的媒体类型，再次对消息转换器进行遍历，选出能够写出对应媒体类型的消息转换器，调用该消息转换器的write方法进行转换
     
       > MessageConverter在整个内容协商中用了两次，第一次是看当前系统所有的MessageConverter能够支持某个返回值类型能写出的所有媒体类型；第二次是遍历所有MessageConverter获取能够写出特定媒体类型的转换器；
       >
       > 这里可以对第一次进行优化，因为消息转换器总是固定的，可以在第一次请求进来的时候获取producibleTypes时将producibleTypes缓存起来，以后相同请求进来没必要再遍历所有消息转换器，直接从缓存中拿producibleTypes
     
     ```java
     public abstract class AbstractMessageConverterMethodProcessor extends AbstractMessageConverterMethodArgumentResolver
     		implements HandlerMethodReturnValueHandler {
     	@SuppressWarnings({"rawtypes", "unchecked"})
     	protected <T> void writeWithMessageConverters(@Nullable T value, MethodParameter returnType,
     			ServletServerHttpRequest inputMessage, ServletServerHttpResponse outputMessage)
     			throws IOException, HttpMediaTypeNotAcceptableException, HttpMessageNotWritableException {
     
     		Object body;
     		Class<?> valueType;
     		Type targetType;
     
     		if (value instanceof CharSequence) {
     			body = value.toString();
     			valueType = String.class;
     			targetType = String.class;
     		}
     		else {
     			body = value;
     			valueType = getReturnValueType(body, returnType);
     			targetType = GenericTypeResolver.resolveType(getGenericType(returnType), returnType.getContainingClass());
     		}
     
     		if (isResourceType(value, returnType)) {
     			outputMessage.getHeaders().set(HttpHeaders.ACCEPT_RANGES, "bytes");
     			if (value != null && inputMessage.getHeaders().getFirst(HttpHeaders.RANGE) != null &&
     					outputMessage.getServletResponse().getStatus() == 200) {
     				Resource resource = (Resource) value;
     				try {
     					List<HttpRange> httpRanges = inputMessage.getHeaders().getRange();
     					outputMessage.getServletResponse().setStatus(HttpStatus.PARTIAL_CONTENT.value());
     					body = HttpRange.toResourceRegions(httpRanges, resource);
     					valueType = body.getClass();
     					targetType = RESOURCE_REGION_LIST_TYPE;
     				}
     				catch (IllegalArgumentException ex) {
     					outputMessage.getHeaders().set(HttpHeaders.CONTENT_RANGE, "bytes */" + resource.contentLength());
     					outputMessage.getServletResponse().setStatus(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.value());
     				}
     			}
     		}
     
     		MediaType selectedMediaType = null;//声明一个selectedMediaType
     		MediaType contentType = outputMessage.getHeaders().getContentType();//看一下当前的响应有没有被处理过，比如拦截器是否给响应写了一些数据，已经把媒体类型写死了
     		boolean isContentTypePreset = contentType != null && contentType.isConcrete();
     		if (isContentTypePreset) {
     			if (logger.isDebugEnabled()) {
     				logger.debug("Found 'Content-Type:" + contentType + "' in response");
     			}
     			selectedMediaType = contentType;//如果有就用之前的媒体类型
     		}
     		else {
     			HttpServletRequest request = inputMessage.getServletRequest();
     			List<MediaType> acceptableTypes;
     			try {
                     
                     //分支一
     				acceptableTypes = getAcceptableMediaTypes(request);//关键一：getAcceptableMediaTypes
     			}
     			catch (HttpMediaTypeNotAcceptableException ex) {
     				int series = outputMessage.getServletResponse().getStatus() / 100;
     				if (body == null || series == 4 || series == 5) {
     					if (logger.isDebugEnabled()) {
     						logger.debug("Ignoring error response content (if any). " + ex);
     					}
     					return;
     				}
     				throw ex;
     			}
                 
                 //分支二
     			List<MediaType> producibleTypes = getProducibleMediaTypes(request, valueType, targetType);//获取服务器可以产生的内容类型，服务器会根据返回值类型自动判断对应返回值能根据哪种内容类型写出
     
     			if (body != null && producibleTypes.isEmpty()) {
     				throw new HttpMessageNotWritableException(
     						"No converter found for return value of type: " + valueType);
     			}
     			List<MediaType> mediaTypesToUse = new ArrayList<>();
     			for (MediaType requestedType : acceptableTypes) {//先遍历浏览器能接收的内容类型
     				for (MediaType producibleType : producibleTypes) {//在选定可接收内容类型的前提下选定服务器可以产生的内容类型
     					if (requestedType.isCompatibleWith(producibleType)) {//服务器接收类型和产生类型能否匹配，如果能匹配就把该内容类型加到mediaTypesToUse中，这个循环的目的就是匹配客户端需要的刚好我又能提供的媒体类型
     					mediaTypesToUse.add(getMostSpecificMediaType(requestedType, producibleType));
     					}
     				}
     			}
     			if (mediaTypesToUse.isEmpty()) {
     				if (logger.isDebugEnabled()) {
     					logger.debug("No match for " + acceptableTypes + ", supported: " + producibleTypes);
     				}
     				if (body != null) {
     					throw new HttpMediaTypeNotAcceptableException(producibleTypes);
     				}
     				return;
     			}
     
     			MediaType.sortBySpecificityAndQuality(mediaTypesToUse);//在这里对内容类型排个序
     
     			for (MediaType mediaType : mediaTypesToUse) {
     				if (mediaType.isConcrete()) {
     					selectedMediaType = mediaType;//选中的媒体类型永远只有一个
     					break;//这里只要判断第一个成功就直接break了，不成功继续遍历
     				}
     				else if (mediaType.isPresentIn(ALL_APPLICATION_MEDIA_TYPES)) {
     					selectedMediaType = MediaType.APPLICATION_OCTET_STREAM;
     					break;
     				}
     			}
     
     			if (logger.isDebugEnabled()) {
     				logger.debug("Using '" + selectedMediaType + "', given " +
     						acceptableTypes + " and supported " + producibleTypes);
     			}
     		}
     
     		if (selectedMediaType != null) {
     			selectedMediaType = selectedMediaType.removeQualityValue();
     			for (HttpMessageConverter<?> converter : this.messageConverters) {//这里遍历conveter看哪一个支持把对象转为xml,MessageConverter在这里面用了两次，第一次是看当前系统所有的MessageConverter能够支持某个返回值类型能写出的所有媒体类型；第二次是遍历所有MessageConverter获取能够写出特定媒体类型的转换器，通过这一步因为xml的优先级在前面，所以会获取能将Person转xml的消息转换器
     				GenericHttpMessageConverter genericConverter = (converter instanceof GenericHttpMessageConverter ?
     						(GenericHttpMessageConverter<?>) converter : null);//这里就是判断能转换对应xml的消息转换器的代码
     				if (genericConverter != null ?
     						((GenericHttpMessageConverter) converter).canWrite(targetType, valueType, selectedMediaType) :
     						converter.canWrite(valueType, selectedMediaType)) {
     					body = getAdvice().beforeBodyWrite(body, returnType, selectedMediaType,
     							(Class<? extends HttpMessageConverter<?>>) converter.getClass(),
     							inputMessage, outputMessage);//这个body就是最终要转成xml的原始返回值对象person
     					if (body != null) {
     						Object theBody = body;
     						LogFormatUtils.traceDebug(logger, traceOn ->
     								"Writing [" + LogFormatUtils.formatValue(theBody, !traceOn) + "]");
     						addContentDispositionHeader(inputMessage, outputMessage);
                             
                             //分支三
     						if (genericConverter != null) {
     							genericConverter.write(body, targetType, selectedMediaType, outputMessage);//这里是调用write方法把对象写成xml
     						}
     						else {
     							((HttpMessageConverter) converter).write(body, selectedMediaType, outputMessage);
     						}
     					}
     					else {
     						if (logger.isDebugEnabled()) {
     							logger.debug("Nothing to write: null body");
     						}
     					}
     					return;
     				}
     			}
     		}
     
     		if (body != null) {
     			Set<MediaType> producibleMediaTypes =
     					(Set<MediaType>) inputMessage.getServletRequest()
     							.getAttribute(HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE);
     
     			if (isContentTypePreset || !CollectionUtils.isEmpty(producibleMediaTypes)) {
     				throw new HttpMessageNotWritableException(
     						"No converter for [" + valueType + "] with preset Content-Type '" + contentType + "'");
     			}
     			throw new HttpMediaTypeNotAcceptableException(getSupportedMediaTypes(body.getClass()));
     		}
     	}
     }
     ```
     
     【分支一：getAcceptableMediaTypes方法分支】
     
     【writeWithMessageConverters方法中的getAcceptableMediaTypes方法】
     
     ```java
     public abstract class AbstractMessageConverterMethodProcessor extends AbstractMessageConverterMethodArgumentResolver
     		implements HandlerMethodReturnValueHandler {
     	private List<MediaType> getAcceptableMediaTypes(HttpServletRequest request)
     			throws HttpMediaTypeNotAcceptableException {
     		return this.contentNegotiationManager.resolveMediaTypes(new ServletWebRequest(request));//通过调用contentNegotiationManager内容协商管理器的resolveMediaTypes方法解析媒体类型，new ServletWebRequest(request)是包装请求对象
     	}
     }
     ```
     
     【getAcceptableMediaTypes方法中的resolveMediaTypes方法】
     
     ```java
     public class ContentNegotiationManager implements ContentNegotiationStrategy, MediaTypeFileExtensionResolver {
     	@Override
     	public List<MediaType> resolveMediaTypes(NativeWebRequest request) throws HttpMediaTypeNotAcceptableException {
     		for (ContentNegotiationStrategy strategy : this.strategies) {//默认遍历内容协商管理器中的所有策略，strategies是一个ArrayList集合，里面有一个HeaderContentNegotiationStrategy请求头内容策略用于解析媒体类型
     			List<MediaType> mediaTypes = strategy.resolveMediaTypes(request);//这是进行解析媒体类型的核心方法
     			if (mediaTypes.equals(MEDIA_TYPE_ALL_LIST)) {
     				continue;
     			}
     			return mediaTypes;
     		}
     		return MEDIA_TYPE_ALL_LIST;
     	}
     }
     ```
     
     【resolveMediaTypes方法中的resolveMediaTypes方法】
     
     ```java
     public class HeaderContentNegotiationStrategy implements ContentNegotiationStrategy {
     	@Override
     	public List<MediaType> resolveMediaTypes(NativeWebRequest request)
     			throws HttpMediaTypeNotAcceptableException {
     
     		String[] headerValueArray = request.getHeaderValues(HttpHeaders.ACCEPT);//通过原生请求获取请求头的Accept字段，headerValueArray中会以字符串数组的形式保存所有的Accept字段中的值，这个方法的调用基于默认内容协商策略是基于请求头的内容协商策略，拿请求头中Accept字段中的内容类型
     		if (headerValueArray == null) {
     			return MEDIA_TYPE_ALL_LIST;
     		}
     
     		List<String> headerValues = Arrays.asList(headerValueArray);//将字符串数组转成List数组
     		try {
     			List<MediaType> mediaTypes = MediaType.parseMediaTypes(headerValues);
     			MediaType.sortBySpecificityAndQuality(mediaTypes);
     			return !CollectionUtils.isEmpty(mediaTypes) ? mediaTypes : MEDIA_TYPE_ALL_LIST;//这里讲的不清楚，相当于返回从Accept解析出来能被处理的内容类型，封装成List集合一路返回
     		}
     		catch (InvalidMediaTypeException ex) {
     			throw new HttpMediaTypeNotAcceptableException(
     					"Could not parse 'Accept' header " + headerValues + ": " + ex.getMessage());
     		}
     	}
     }
     ```
     
     【分支二：getProducibleMediaTypes分支】
     
     【writeWithMessageConverters方法中的getProducibleMediaTypes方法】
     
     ```java
     public abstract class AbstractMessageConverterMethodProcessor extends AbstractMessageConverterMethodArgumentResolver
     		implements HandlerMethodReturnValueHandler {
     	@SuppressWarnings("unchecked")
     	protected List<MediaType> getProducibleMediaTypes(
     			HttpServletRequest request, Class<?> valueClass, @Nullable Type targetType) {
     
     		Set<MediaType> mediaTypes =
     				(Set<MediaType>) request.getAttribute(HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE);//先从请求域中获取一个默认的媒体类型
     		if (!CollectionUtils.isEmpty(mediaTypes)) {
     			return new ArrayList<>(mediaTypes);
     		}
     		List<MediaType> result = new ArrayList<>();
     		for (HttpMessageConverter<?> converter : this.messageConverters) {//会拿到所有的消息转换器，导入jackson的json相关依赖就是10个，导入jackson的xml相关依赖就有11个
     			if (converter instanceof GenericHttpMessageConverter && targetType != null) {
     				if (((GenericHttpMessageConverter<?>) converter).canWrite(targetType, valueClass, null)) {//如果某个消息转换器调用canWrite判断该消息转换器支持对valueClass类型向目标类型的转换
     					result.addAll(converter.getSupportedMediaTypes(valueClass));//就把支持的媒体加入到媒体类型集合中，实际上除了MappingJackson2HttpMessageConverter和MappingJackson2XmlHttpMessageConverter对应的四个消息转换器(两个同名,woc连引用地址都一样，原理是什么，为什么要放两个一样的东西)外其他的消息转换器均不支持处理自定义对象，result中存放的是可用的消息转换器总共能支持哪些媒体类型的能力，getSupportedMediaTypes(valueClass)的结果是，这个result内部东西的原理不懂，一共有和json和xml相关的十种内容类型
     				}
     			}
     			else if (converter.canWrite(valueClass, null)) {
     				result.addAll(converter.getSupportedMediaTypes(valueClass));
     			}
     		}
     		return (result.isEmpty() ? Collections.singletonList(MediaType.ALL) : result);
     	}
     }
     ```
     
     【分支三：write方法分支】
     
     【writeWithMessageConverters方法中的write方法】
     
     ```java
     public abstract class AbstractGenericHttpMessageConverter<T> extends AbstractHttpMessageConverter<T>
     		implements GenericHttpMessageConverter<T> {
     	@Override
     	public final void write(final T t, @Nullable final Type type, @Nullable MediaType contentType,
     			HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
     
     		final HttpHeaders headers = outputMessage.getHeaders();
     		addDefaultHeaders(headers, t, contentType);//添加默认响应头，这时候响应头的headers的Content-Type已经和上一个响应json不同，这里是application/xml，charset=UTF-8；
     
     		if (outputMessage instanceof StreamingHttpOutputMessage) {
     			StreamingHttpOutputMessage streamingOutputMessage = (StreamingHttpOutputMessage) outputMessage;
     			streamingOutputMessage.setBody(outputStream -> writeInternal(t, type, new HttpOutputMessage() {
     				@Override
     				public OutputStream getBody() {
     					return outputStream;
     				}
     				@Override
     				public HttpHeaders getHeaders() {
     					return headers;
     				}
     			}));
     		}
     		else {
     			writeInternal(t, type, outputMessage);//调用writeInternal方法在底层调用objectMapper把person对象转成xml
     			outputMessage.getBody().flush();//outputMessage中的ServletResponse的response的outputBuffer的bb属性中存放了这个xml格式的Byte数组，view as String可以看到具体的xml内容
     		}
     	}
     }
     ```
     
     【write方法中的writeInternal方法】
     
     ```java
     public abstract class AbstractJackson2HttpMessageConverter extends AbstractGenericHttpMessageConverter<Object> {
     	@Override
     	protected void writeInternal(Object object, @Nullable Type type, HttpOutputMessage outputMessage)
     			throws IOException, HttpMessageNotWritableException {
     
     		MediaType contentType = outputMessage.getHeaders().getContentType();
     		JsonEncoding encoding = getJsonEncoding(contentType);
     
     		Class<?> clazz = (object instanceof MappingJacksonValue ?
     				((MappingJacksonValue) object).getValue().getClass() : object.getClass());
     		ObjectMapper objectMapper = selectObjectMapper(clazz, contentType);
     		Assert.state(objectMapper != null, () -> "No ObjectMapper for " + clazz.getName());
     
     		OutputStream outputStream = StreamUtils.nonClosing(outputMessage.getBody());
     		try (JsonGenerator generator = objectMapper.getFactory().createGenerator(outputStream, encoding)) {
     			writePrefix(generator, object);
     
     			Object value = object;
     			Class<?> serializationView = null;
     			FilterProvider filters = null;
     			JavaType javaType = null;
     
     			if (object instanceof MappingJacksonValue) {
     				MappingJacksonValue container = (MappingJacksonValue) object;
     				value = container.getValue();
     				serializationView = container.getSerializationView();
     				filters = container.getFilters();
     			}
     			if (type != null && TypeUtils.isAssignable(type, value.getClass())) {
     				javaType = getJavaType(type, null);
     			}
     
     			ObjectWriter objectWriter = (serializationView != null ?
     					objectMapper.writerWithView(serializationView) : objectMapper.writer());//通过objectMapper获取objectWriter
     			if (filters != null) {
     				objectWriter = objectWriter.with(filters);
     			}
     			if (javaType != null && javaType.isContainerType()) {
     				objectWriter = objectWriter.forType(javaType);
     			}
     			SerializationConfig config = objectWriter.getConfig();
     			if (contentType != null && contentType.isCompatibleWith(MediaType.TEXT_EVENT_STREAM) &&
     					config.isEnabled(SerializationFeature.INDENT_OUTPUT)) {
     				objectWriter = objectWriter.with(this.ssePrettyPrinter);
     			}
     			objectWriter.writeValue(generator, value);//用objectWriter的writeValue方法把person对象写成xml写出去到浏览器
     
     			writeSuffix(generator, object);
     			generator.flush();
     		}
     		catch (InvalidDefinitionException ex) {
     			throw new HttpMessageConversionException("Type definition error: " + ex.getType(), ex);
     		}
     		catch (JsonProcessingException ex) {
     			throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getOriginalMessage(), ex);
     		}
     	}
     }
     ```
     
     【writeInternal方法中的writeValue方法】
     
     ```java
     public class ObjectWriter implements Versioned,java.io.Serializable{
     	public void writeValue(JsonGenerator g, Object value) throws IOException
         {
             _assertNotNull("g", g);
             _configureGenerator(g);//此时这个g即JsonGenerator是ToXmlGenerator，已验证；上一个响应ToJsonGenerator，自然而然数据就会写出xml格式
             if (_config.isEnabled(SerializationFeature.CLOSE_CLOSEABLE)
                     && (value instanceof Closeable)) {
     
                 Closeable toClose = (Closeable) value;
                 try {
                     _prefetch.serialize(g, value, _serializerProvider());
                     if (_config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
                         g.flush();
                     }
                 } catch (Exception e) {
                     ClassUtil.closeOnFailAndThrowAsIOE(null, toClose, e);
                     return;
                 }
                 toClose.close();
             } else {
                 _prefetch.serialize(g, value, _serializerProvider());
                 if (_config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
                     g.flush();
                 }
             }
         }
     }
     ```

#### 03、开启基于请求参数的内容协商功能

为了方便浏览器内容协商，SpringMVC提供基于请求参数的内容协商功能【不改请求头，实现响应xml和json的随心所欲切换】

1. 步骤：

   + 第一步：在全局配置文件中配置spring.mvc.contentnegotiation.favor-parameter=true【默认是false】

     > 表示开启基于参数方式的内容协商，该属性规定可以在请求参数中带名为format的请求参数，通过format指定需要响应的内容类型

     ```properties
     spring:
       mvc:
         contentnegotiation:
           favor-parameter: true  #开启请求参数内容协商
     ```

   + 第二步：发送请求时请求字段带上format请求参数指定响应内容类型

     + http://localhost:8080/test/person?format=xml
     + http://localhost:8080/test/person?format=json

2. 源码跟踪

   > 边缘的不讲了，这里只讲内容协商的getAcceptableMediaType方法确定浏览器可以接受的内容类型
   >
   > + 核心就是开启请求参数的内容协商功能后会自动把参数内容协商策略放在默认的请求头内容协商策略前面，优先获取请求参数format的参数值对应的内容类型，如果确定了内容类型就跳出策略遍历；如果获取的内容类型是`*/*`,就继续遍历下一个内容协商策略

   【writeWithMessageConverters方法中的getAcceptableMediaTypes方法】
   
   ```java
   public abstract class AbstractMessageConverterMethodProcessor extends AbstractMessageConverterMethodArgumentResolver
   		implements HandlerMethodReturnValueHandler {
   	private List<MediaType> getAcceptableMediaTypes(HttpServletRequest request)
   			throws HttpMediaTypeNotAcceptableException {
   		return this.contentNegotiationManager.resolveMediaTypes(new ServletWebRequest(request));//通过调用contentNegotiationManager内容协商管理器的resolveMediaTypes方法解析媒体类型，new ServletWebRequest(request)是包装请求对象
   	}
   }
   ```

   > 以前contentNegotiationManager中的strategies只有基于请求头的内容协商策略HeaderContentNegotiationStrategy，现在开启了基于请求参数的内容协商功能后，多了一个基于参数的内容协商策略ParameterContentNegotiationStrategy，里面参数的名字就叫做format

   ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\内容协商策略.png)

   [^注意1]: 上图中MediaType属性中定死了能写的内容类型只有json和xml两种

   【getAcceptableMediaTypes方法中的resolveMediaTypes方法】
   
   ```java
   public class ContentNegotiationManager implements ContentNegotiationStrategy, MediaTypeFileExtensionResolver {
   	@Override
   	public List<MediaType> resolveMediaTypes(NativeWebRequest request) throws HttpMediaTypeNotAcceptableException {
   		for (ContentNegotiationStrategy strategy : this.strategies) {//默认遍历内容协商管理器中的所有策略，优先遍历第一个基于参数的内容协商策略，只要解析出了媒体类型就会直接返回，不会在遍历第二个内容协商策略
   			List<MediaType> mediaTypes = strategy.resolveMediaTypes(request);//这是进行解析媒体类型的核心方法
   			if (mediaTypes.equals(MEDIA_TYPE_ALL_LIST)) {
   				continue;//除非解析出的还是*/*才会继续遍历下一个内容协商策略
   			}
   			return mediaTypes;
   		}
   		return MEDIA_TYPE_ALL_LIST;
   	}
   }
   ```

   【resolveMediaTypes方法中的resolveMediaTypes方法】

   > 注意啊这里第一个是基于参数的resolveMediaTypes方法了，不在是基于请求头的内容协商策略中的方法了
   
   ```java
   public abstract class AbstractMappingContentNegotiationStrategy extends MappingMediaTypeFileExtensionResolver
   		implements ContentNegotiationStrategy {
   	@Override
   	public List<MediaType> resolveMediaTypes(NativeWebRequest webRequest)
   			throws HttpMediaTypeNotAcceptableException {
   
   		return resolveMediaTypeKey(webRequest, getMediaTypeKey(webRequest));//getMediaTypeKey是获取媒体类型参数的key,这个方法中会去调用request.getParameter(getParameterName())来获取请求参数的值即json或者xml，而getParameterName()就是获取常量值format，
   	}
   }
   ```

   	【resolveMediaTypes方法中的resolveMediaTypeKey方法】

```java
public abstract class AbstractMappingContentNegotiationStrategy extends MappingMediaTypeFileExtensionResolver
		implements ContentNegotiationStrategy {
	public List<MediaType> resolveMediaTypeKey(NativeWebRequest webRequest, @Nullable String key)
			throws HttpMediaTypeNotAcceptableException {

		if (StringUtils.hasText(key)) {//key就是json或者xml
			MediaType mediaType = lookupMediaType(key);//json对应的是application/json;xml对应的是application/xml
			if (mediaType != null) {
				handleMatch(key, mediaType);
				return Collections.singletonList(mediaType);//这儿返回的十个啥，是mediaType吗?最外层接收的是一个媒体类型List集合，所以这里能返回几个媒体类型?
			}
			mediaType = handleNoMatch(webRequest, key);
			if (mediaType != null) {
				addMapping(key, mediaType);
				return Collections.singletonList(mediaType);
			}
		}
		return MEDIA_TYPE_ALL_LIST;
	}
}
```

[^注意]: 基于参数的内容协商策略支持json和xml，如果想要基于参数的响应内容类型支持pdf、excel等等可以通过自定义内容协商管理器实现对应的功能



​	



#### 04、自定义MessageConverter

> 使用@ResponseBody注解标注控制器方法就意味着响应数据，会调用返回值处理器RequestResponseBodyMethodProcessor处理返回值，使用消息转换器进行处理，所有MessageConverter一起可以支持各种媒体类型数据的读和写操作，读对应canRead方法和read方法，写对应canWrite和write方法；通过内容协商就能找到最终的MessageConverter，调用消息转换器对应的write方法来实现内容类型的转换和写出
>
> 自定义消息转换器场景:
>
> 浏览器发请求希望返回xml数据，如果是ajax请求希望返回json数据，如果是其他的app请求，则返回自定义协议数据【以前的解决办法是写三个方法，每种场景发送不同路径的请求】；现在可以直接使用内容协商一个方法解决，分别规定请求发送时的需要的内容类型，服务器通过内容协商找到对应的消息转换器分别处理即可

1. 需求：

   + 扩展场景

        + 如果是浏览器发请求直接返回xml数据    [application/xml]    jacksonXmlConverter

        + 如果是ajax请求，返回json数据    [application/json]    jacksonJsonConverter

        + 如果是客户端如硅谷app发请求，返回自定义协议数据    [application/x-guigu]    xxxConverter

             + 属性值1；属性值2；...[这种方式只要值，省了很多数据，传输更快]

             即适配三种不同的场景

   + 解决流程:

        + 第一步：添加自定义的MessageConverter进系统底层

        + 第二步：系统底层会自动统计处所有MessageConverter针对返回值类型能操作的内容类型

        + 第三步：服务器根据客户端需要的内容类型以及自己能提供的内容类型进行内容协商，自动调用对应的自定义消息转换器处理返回值

             > 如需要x-guigu，服务器一看刚好有x-guigu对应的转换器，就自动使用自定义消息转换器来处理相应的返回值

2. 消息转换器的默认配置

   + 在配置类WebMvcAutoConfiguration中的子配置类WebMvcAutoConfigurationAdapter中配置了一个configureMessageConverters组件，该组件在系统加载时会依靠这个组件来配置默认的所有MessageConverter

     ```java
     @Configuration(proxyBeanMethods = false)
     @ConditionalOnWebApplication(type = Type.SERVLET)
     @ConditionalOnClass({ Servlet.class, DispatcherServlet.class, WebMvcConfigurer.class })
     @ConditionalOnMissingBean(WebMvcConfigurationSupport.class)
     @AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 10)
     @AutoConfigureAfter({ DispatcherServletAutoConfiguration.class, TaskExecutionAutoConfiguration.class,
     		ValidationAutoConfiguration.class })
     public class WebMvcAutoConfiguration {
         ...
     	@Configuration(proxyBeanMethods = false)
     	@Import(EnableWebMvcConfiguration.class)
     	@EnableConfigurationProperties({ WebMvcProperties.class, ResourceProperties.class })
     	@Order(0)
     	public static class WebMvcAutoConfigurationAdapter implements WebMvcConfigurer {
     		...
     		@Override
     		public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
     			this.messageConvertersProvider
     					.ifAvailable((customConverters) -> converters.addAll(customConverters.getConverters()));//将从customConverters.getConverters()获取到的所有converter直接加载到converters中
     		}
             ...
         }
     }
     ```

     【WebMvcAutoConfiguration中的configureMessageConverters方法中的getConverters()方法】

     ```java
     public class HttpMessageConverters implements Iterable<HttpMessageConverter<?>> {
     	...
         private final List<HttpMessageConverter<?>> converters;//这个converters是在创建HttpMessageConverters对象时会自动添加默认的converter
         ...
         public HttpMessageConverters(boolean addDefaultConverters, Collection<HttpMessageConverter<?>> converters) {
     		List<HttpMessageConverter<?>> combined = getCombinedConverters(converters,
     				addDefaultConverters ? getDefaultConverters() : Collections.emptyList());//getDefaultConverters()就是获取默认的converters
     		combined = postProcessConverters(combined);
     		this.converters = Collections.unmodifiableList(combined);//把这些默认的converter赋值给属性converters
     	}
         
         ...
     	public List<HttpMessageConverter<?>> getConverters() {
     		return this.converters;
     	}
         ...
     }
     ```

     【HttpMessageConverters构造方法中的getDefaultConverters()方法】

     ```java
     public class HttpMessageConverters implements Iterable<HttpMessageConverter<?>> {
     	...
         private List<HttpMessageConverter<?>> getDefaultConverters() {
     		List<HttpMessageConverter<?>> converters = new ArrayList<>();
     		if (ClassUtils.isPresent("org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport",
     				null)) {
     			converters.addAll(new WebMvcConfigurationSupport() {
     
     				public List<HttpMessageConverter<?>> defaultMessageConverters() {
     					return super.getMessageConverters();
     				}
     			}.defaultMessageConverters());//默认的converter是从defaultMessageConverters()方法获取的
     		}
     		else {
     			converters.addAll(new RestTemplate().getMessageConverters());
     		}
     		reorderXmlConvertersToEnd(converters);
     		return converters;
     	}
         ...
     }
     ```

     【getDefaultConverters()方法中的super.getMessageConverters()方法】

     ```java
     public class WebMvcConfigurationSupport implements ApplicationContextAware, ServletContextAware {
     	protected final List<HttpMessageConverter<?>> getMessageConverters() {
     		if (this.messageConverters == null) {
     			this.messageConverters = new ArrayList<>();
     			configureMessageConverters(this.messageConverters);
     			if (this.messageConverters.isEmpty()) {
     				addDefaultHttpMessageConverters(this.messageConverters);//默认的converter是从addDefaultHttpMessageConverters方法获取的
     			}
     			extendMessageConverters(this.messageConverters);
     		}
     		return this.messageConverters;
     	}
     }
     ```

     【getMessageConverters()方法中的addDefaultHttpMessageConverters方法】

     > 解释了只要导入了jackson处理xml的相关类，xml的MessageConverters就会自动添加到converters集合中

     ```java
     public class WebMvcConfigurationSupport implements ApplicationContextAware, ServletContextAware {
     	protected final void addDefaultHttpMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
     		messageConverters.add(new ByteArrayHttpMessageConverter());//这个就是加载时添加各个MessageConverter的代码
     		messageConverters.add(new StringHttpMessageConverter());
     		messageConverters.add(new ResourceHttpMessageConverter());
     		messageConverters.add(new ResourceRegionHttpMessageConverter());
     		try {
     			messageConverters.add(new SourceHttpMessageConverter<>());
     		}
     		catch (Throwable ex) {
     			// Ignore when no TransformerFactory implementation is available...
     		}
     		messageConverters.add(new AllEncompassingFormHttpMessageConverter());
     
     		if (romePresent) {
     			messageConverters.add(new AtomFeedHttpMessageConverter());
     			messageConverters.add(new RssChannelHttpMessageConverter());
     		}
     
     		if (jackson2XmlPresent) {//这是一个boolean类型的变量，当静态代码块使用类工具ClassUtils的isPresent方法判断系统类是否导了对应变量的类，导了就是true，就会执行添加对应消息转化器的代码
     			Jackson2ObjectMapperBuilder builder = Jackson2ObjectMapperBuilder.xml();
     			if (this.applicationContext != null) {
     				builder.applicationContext(this.applicationContext);
     			}
     			messageConverters.add(new MappingJackson2XmlHttpMessageConverter(builder.build()));//这里如果导入jacksonXML的依赖这里就会自动去添加消息转换器MappingJackson2XmlHttpMessageConverter
     		}
     		else if (jaxb2Present) {
     			messageConverters.add(new Jaxb2RootElementHttpMessageConverter());
     		}
     
     		if (jackson2Present) {
     			Jackson2ObjectMapperBuilder builder = Jackson2ObjectMapperBuilder.json();
     			if (this.applicationContext != null) {
     				builder.applicationContext(this.applicationContext);
     			}
     			messageConverters.add(new MappingJackson2HttpMessageConverter(builder.build()));
     		}
     		else if (gsonPresent) {
     			messageConverters.add(new GsonHttpMessageConverter());
     		}
     		else if (jsonbPresent) {
     			messageConverters.add(new JsonbHttpMessageConverter());
     		}
     
     		if (jackson2SmilePresent) {
     			Jackson2ObjectMapperBuilder builder = Jackson2ObjectMapperBuilder.smile();
     			if (this.applicationContext != null) {
     				builder.applicationContext(this.applicationContext);
     			}
     			messageConverters.add(new MappingJackson2SmileHttpMessageConverter(builder.build()));
     		}
     		if (jackson2CborPresent) {
     			Jackson2ObjectMapperBuilder builder = Jackson2ObjectMapperBuilder.cbor();
     			if (this.applicationContext != null) {
     				builder.applicationContext(this.applicationContext);
     			}
     			messageConverters.add(new MappingJackson2CborHttpMessageConverter(builder.build()));
     		}
     	}
     }
     ```

3. 自定义消息转换器的流程【使用设置Accept为自定义内容类型的方式】

   + 步骤一：WebMvcConfigurer接口中有一个configureMessageConverters方法【配置消息转换器，这个会覆盖掉所有默认的消息转换器】，还有一个extendMessageConverters方法【扩展消息转换器，在默认的基础上额外追加】，所以添加自定义消息转换器只需要添加WebMvcConfigurer组件的匿名内部类并重写该组件中的extendMessageConverters方法

     [^注意]: SpringMVC当前添加自定义功能一般都是通过向容器添加一个WebMvcConfigurer组件的匿名内部类，通过实现特定的方法添加相应的组件

     ```java
     @Configuration(proxyBeanMethods = false)
     public class WebConfig /*implements WebMvcConfigurer*/ {
         @Bean
         public WebMvcConfigurer webMvcConfigurer(){
             return new WebMvcConfigurer() {
                 @Override
                 public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
                     converters.add(new GuiGuMessageConverter());//这是创建自定义消息转换器并通过add方法把消息转换器加入converters集合中
                 }
             };
         }
     }
     ```

   + 第二步：自己在converter包下写一个自定义的消息转换器GuiGuConverter实现消息转换器的总接口HttpMessageConverter<>,括号中为该消息转换器支持的数据类型，这里只设定为Person

     ```java
     /**
      * 自定义的Converter，这个转换器统一不支持读，canRead直接返回false
      * 支持读的意思是形参使用@RequestBody注解进行标注，传过来的请求数据是json或者xml或者自定义类型，
      *      该消息转换器可以把请求数据读成对应的类型如Person
      * */
     public class GuiGuMessageConverter implements HttpMessageConverter<Person> {
     
         @Override
         public boolean canRead(Class<?> clazz, MediaType mediaType) {
             return false;
         }
     
         @Override
         public boolean canWrite(Class<?> clazz, MediaType mediaType) {//这个mediaType在后续执行会传参最佳匹配的内容类型作为判断依据，有需要可以使用
             //支持写只要返回值类型是Person类型就支持,isAssignableFrom是判断传参类型的方法
             return clazz.isAssignableFrom(Person.class);
         }
     
         /**
          * @return {@link List }<{@link MediaType } 返回支持类型的集合>
          * @描述 服务器统计所有的MessageConverter都能支持写哪些内容就是调用的这个方法，这是告诉SpringMVC这个转换器
          * 能够将特定返回值类型转换成哪些内容类型
          * @author Earl
          * @version 1.0.0
          * @创建日期 2023/05/29
          * @since 1.0.0
          */
         @Override
         public List<MediaType> getSupportedMediaTypes() {
             /**自定义转换器用MediaType.parseMediaTypes方法，可以把字符串解析成媒体类型集合,这里面的字符串只是一个标识，
             只要能用来和请求中的内容类型匹配就行*/
             return MediaType.parseMediaTypes("application/x-guigu");
         }
     
         @Override
         public Person read(Class<? extends Person> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
             return null;
         }
     
         /**
          * @param person        人，这个在括号中写了会自动出现
          * @param contentType   内容类型
          * @param outputMessage 输出消息
          * @描述 自定义协议数据的写出，这玩意儿直接把对应对象要写成的格式弄成字符串直接通过输出流写出去即可
          * @author Earl
          * @version 1.0.0
          * @创建日期 2023/05/29
          * @since 1.0.0
          */
         @Override
         public void write(Person person, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
             //自定义协议数据的写出
             String data=person.getUserName()+";"+person.getAge()+";"+person.getBirth()+";"+person.getPet();
             //写出数据
             OutputStream body=outputMessage.getBody();//这个outputMessage.getBody方法可以获取输出流
             body.write(data.getBytes());//输出流的write方法可以直接写出字符串的byte数组,有点像原生响应的out.print方法
         }
     }
     ```

   + 第三步：通过postMan发送Accept为x-guigu的请求

     > 注意，这里还是使用的请求头内容协商策略，参数内容协商策略执行了但是没有得到结果
     >
     > 在获取所有服务器支持的内容类型中this.messageConverters多出一个自定义的消息转换器，result中保存的是application/x-guigu，此外加上可以转json和xml的，最终producibleTypes可以产生11种数据，但是浏览器只需要一种application/x-guigu，最佳匹配最终确定application/x-guigu；接着遍历消息转换器，查看哪种消息转换器可以处理，找到调用对应的write方法写出去

     [^扩展]: 如何使用浏览器基于请求参数方式的内容协商

4. 自定义消息转换器的流程【基于请求参数的内容协商方式】

   > 基于参数的内容协商默认只支持xml和json，需要自己自定义内容协商管理器来支持第三方的内容协商策略，WebMvcConfigurer中的configureContentNegotiation方法传参configurer对象的.strategies方法可以自定义内容协商策略，这个内容协商策略会覆盖掉默认的相同类型的内容协商策略【还会取代内容协商管理器中的所有内容协商策略，如基于请求头的内容协商策略】，所以需要补充json和xml，注意：当前策略没法解析出结果，比如基于请求头内容解析策略但是没有请求头内容解析策略对象就会默认媒体类型为`*/*`,即服务器认为浏览器什么类型都能接受，会直接匹配服务器能提供的第一个媒体类型进行返回，也就导致Accept随便怎么写都返回application/json类型的数据

   ```java
   public interface WebMvcConfigurer {
   	default void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
   	}
   }
   ```
   
   + 自定义基于参数内容协商策略的代码
   
     ```java
     @Configuration(proxyBeanMethods = false)
     public class WebConfig /*implements WebMvcConfigurer*/ {
         //方式一:使用@Bean配置一个WebMvcConfigurer组件
         @Bean
         public WebMvcConfigurer webMvcConfigurer(){
             return new WebMvcConfigurer() {
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
             }
         }
     }
     ```
   
     【自定义基于参数的内容协商管理器后的内容协商管理器】
   
     > 只有一个自定义的策略了，自定义策略支持三种媒体类型，注意还要和自定义的对应消息转换器的媒体类型联动，否则没法获取相应的消息转换器处理对应返回对象
   
     ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\自定义参数内容协商策略.png)
   
     [^注意]: 自定义功能如内容协商策略可能会覆盖掉很多默认功能，如基于请求头的内容协商功能，需要debug看缺失了什么在自定义中补充，如这个例子中添加基于请求头的内容协商策略
   
     

## 4.6、视图解析

### 4.6.1、视图解析

1. 视图解析

   + 视图解析是服务器处理完请求后跳转某个页面的过程

   + 视图解析的方式包括转发、重定向、自定义视图

     ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\视图解析方式.png)

   + 一般来说处理完请求可以通过以上三种方式跳转JSP页面，但是由于SpringBoot打的是小胖jar，而JSP不支持在压缩包内编译的方式，所有SpringBoot不支持JSP，想要实现页面渲染需要引入第三方模板引擎技术【JSP也是一种模板引擎】

   + SpringBoot支持的第三方模板引擎

     > 在Using SpringBoot的Starter中有以下场景是springBoot支持整合的模板引擎

     + spring-boot-starter-freemarker
     + spring-boot-starter-groovy-templates
     + spring-boot-starter-thymeleaf

2. Thymeleaf简介

   + **Thymeleaf** is a modern server-side Java template；Thymeleaf是一个服务端的java模板引擎，后台开发人员经常会使用Thymeleaf
   + 优点：
     + 语法简单，贴近JSP的方式
   + 缺点：
     + 性能不是很好，高并发场景以及后台管理系统一般都不使用Thymeleaf采用前后端分离的方式，Thymeleaf一般适用于简单的单体应用

3. Thymeleaf的使用

   + Thymeleaf的语法可以参考官方文档的第四小节Standard Expression Syntax

   + 使用流程

     + 第一步：引入Thymeleaf的相关依赖spring-boot-starter-thymeleaf

       ```xml
       <dependency>
           <groupId>org.springframework.boot</groupId>
           <artifactId>spring-boot-starter-thymeleaf</artifactId>
       </dependency>
       ```

     + 第二步：引入了相关依赖Thymeleaf会使用ThymeleafAutoConfiguration自动配置Thymeleaf

       > 自动配置的策略：
       >
       > + 所有Thymeleaf的配置值都在ThymeleafProperties类中
       >
       >   ```java
       >   @ConfigurationProperties(prefix = "spring.thymeleaf")
       >   public class ThymeleafProperties {
       >                   
       >   	private static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;
       >                   
       >   	public static final String DEFAULT_PREFIX = "classpath:/templates/";//默认类路径下templates目录放页面
       >                   
       >   	public static final String DEFAULT_SUFFIX = ".html";//所有的页面都默认以xxx.html命名
       >   	...
       >   }
       >   ```
       >
       >   
       >
       > + 配置好了SpringTemplateEngine【但是在更新的版本没看到SpringTemplateEngine】
       >
       > + 配置好了ThymeleafViewResolver

       ```java
       @AutoConfiguration(after = { WebMvcAutoConfiguration.class, WebFluxAutoConfiguration.class })
       @EnableConfigurationProperties(ThymeleafProperties.class)
       @ConditionalOnClass({ TemplateMode.class, SpringTemplateEngine.class })
       @Import({ TemplateEngineConfigurations.ReactiveTemplateEngineConfiguration.class,
       		TemplateEngineConfigurations.DefaultTemplateEngineConfiguration.class })//？没有SpringTemplateEngine是不是因为被Import自动已经配置好了？老版本注解上是没有关于模板引擎的
       public class ThymeleafAutoConfiguration {
       
       	@Configuration(proxyBeanMethods = false)
       	@ConditionalOnMissingBean(name = "defaultTemplateResolver")
       	static class DefaultTemplateResolverConfiguration {
       
       		...其他代码
       
               //给IoC容器中放一个模板引擎解析器
       		@Bean
       		SpringResourceTemplateResolver defaultTemplateResolver() {
       			SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
       			resolver.setApplicationContext(this.applicationContext);
       			resolver.setPrefix(this.properties.getPrefix());//指定thymeleaf的前缀
       			resolver.setSuffix(this.properties.getSuffix());//指定后缀，这个前缀后缀是从这个类中的properties属性从配置文件绑定的
       			resolver.setTemplateMode(this.properties.getMode());
       			if (this.properties.getEncoding() != null) {
       				resolver.setCharacterEncoding(this.properties.getEncoding().name());
       			}
       			resolver.setCacheable(this.properties.isCache());
       			Integer order = this.properties.getTemplateResolverOrder();
       			if (order != null) {
       				resolver.setOrder(order);
       			}
       			resolver.setCheckExistence(this.properties.isCheckTemplate());
       			return resolver;
       		}
       
       	}
       
       	@Configuration(proxyBeanMethods = false)
       	@ConditionalOnWebApplication(type = Type.SERVLET)
       	@ConditionalOnProperty(name = "spring.thymeleaf.enabled", matchIfMissing = true)
       	static class ThymeleafWebMvcConfiguration {
       
       		...其他代码
       
       		@Configuration(proxyBeanMethods = false)
       		static class ThymeleafViewResolverConfiguration {
       
                   //Thymeleaf的视图解析器
       			@Bean
       			@ConditionalOnMissingBean(name = "thymeleafViewResolver")
       			ThymeleafViewResolver thymeleafViewResolver(ThymeleafProperties properties,
       					SpringTemplateEngine templateEngine) {
       				ThymeleafViewResolver resolver = new ThymeleafViewResolver();
       				resolver.setTemplateEngine(templateEngine);
       				resolver.setCharacterEncoding(properties.getEncoding().name());
       				resolver.setContentType(
       						appendCharset(properties.getServlet().getContentType(), resolver.getCharacterEncoding()));
       				resolver.setProducePartialOutputWhileProcessing(
       						properties.getServlet().isProducePartialOutputWhileProcessing());//视图解析器的所有配置也从properties属性来
       				resolver.setExcludedViewNames(properties.getExcludedViewNames());
       				resolver.setViewNames(properties.getViewNames());
       				// This resolver acts as a fallback resolver (e.g. like a
       				// InternalResourceViewResolver) so it needs to have low precedence
       				resolver.setOrder(Ordered.LOWEST_PRECEDENCE - 5);
       				resolver.setCache(properties.isCache());
       				return resolver;
       			}
       
       			...其他代码
       		}
       	}
       
       	@Configuration(proxyBeanMethods = false)
       	@ConditionalOnWebApplication(type = Type.REACTIVE)
       	@ConditionalOnProperty(name = "spring.thymeleaf.enabled", matchIfMissing = true)
       	static class ThymeleafWebFluxConfiguration {
       		...
       	}
       	@Configuration(proxyBeanMethods = false)
       	@ConditionalOnClass(LayoutDialect.class)
       	static class ThymeleafWebLayoutConfiguration {
       		...
       	}
       	@Configuration(proxyBeanMethods = false)
       	@ConditionalOnClass(DataAttributeDialect.class)
       	static class DataAttributeDialectConfiguration {
       		...
       	}
       	@Configuration(proxyBeanMethods = false)
       	@ConditionalOnClass({ SpringSecurityDialect.class, CsrfToken.class })
       	static class ThymeleafSecurityDialectConfiguration {
       		...
       	}
       	@Configuration(proxyBeanMethods = false)
       	@ConditionalOnClass(Java8TimeDialect.class)
       	static class ThymeleafJava8TimeDialect {
       		...
       	}
       }
       ```

     + 第三步：创建html页面，引入Thymeleaf命名空间，使用Thymeleaf就会自动提示，然后编写前端代码

       ```html
       <!DOCTYPE html>
       <html lang="en" xmlns:th="http://www.thymeleaf.org">
       <head>
           <meta charset="UTF-8">
           <title>success</title>
       </head>
       <body>
       <!--th:text=""是修改该标签的文本值，${}可以获取所有域中的属性值}-->
       <h1 th:text="${msg}">哈哈</h1>
       <h2>
           <!--th:href表示修改a标签href属性值，注意所有的th: 冒号后面不能空一格，空了会报错-->
           <a href="www.atguigu.com" th:href="${link}">去百度</a>
           <!--@{/link}会把/link这个字符串直接拼接到域名+前置路径后面,而不是去域中取值-->
           <!--在属性配置文件中设置属性server.servlet.context-path为自定义上下文路径如/world即可【以后所有的请求都以/world开始】-->
           <a href="www.atguigu.com" th:href="@{/link}">去百度2</a>
       </h2>
       </body>
       </html>
       ```

     + 第四步：编写后端代码

       ```java
       @Controller
       public class ViewTestController {
           @GetMapping("/atguigu")
           public String atguigu(Model model){
               model.addAttribute("msg","你好，guigu");
               model.addAttribute("link","http://www.baidu.com");
               return "success";
           }
       }
       ```

       

### 4.6.2、Thymeleaf的语法

#### 表达式

| 表达式名字 | 语法   |                        用途                         |
| ---------- | ------ | :-------------------------------------------------: |
| 变量取值   | ${...} |           获取请求域、session域、对象的值           |
| 选择变量   | *{...} |                  获取上下文对象值                   |
| 消息       | #{...} |                   获取国际化等值                    |
| 链接       | @{...} | 生成链接，使用@{}指定的超链接可以自动拼上上下文路径 |
| 片段表达式 | ~{...} |     相当于JSP的include的作用，引入公共页面片段      |

#### 字面量

- 文本值: **'one text'** **,** **'Another one!'** **,…**
- 数字: **0** **,** **34** **,** **3.0** **,** **12.3** **,…**
- 布尔值: **true** **,** **false**
- 空值: **null**
- 变量： one，two，.... 变量不能有空格，变量一般用在表达式中

#### 文本操作

- 字符串拼接: **+**
- 变量替换: **|The name is ${name}|** 

#### 数学运算

- 运算符: + , - , * , / , %

#### 布尔运算

- 运算符:  **and** **,** **or**
- 一元运算: **!** **,** **not** 

#### 比较运算

- 比较: **>** **,** **<** **,** **>=** **,** **<=** **(** **gt** **,** **lt** **,** **ge** **,** **le** **)**
- 等式: **==** **,** **!=** **(** **eq** **,** **ne** **)** 

#### 条件运算

- If-then: **(if) ? (then)**
- If-then-else: **(if) ? (then) : (else)**【三元运算】
- Default: (value) **?: (defaultvalue)** 【如果：前面是真，直接给：后面括号中默认值】

#### 特殊操作

- 无操作： _

#### 设置属性值-th:attr

- 给单个value属性设置单个值

```html
<form action="subscribe.html" th:attr="action=@{/subscribe}">
  <fieldset>
    <input type="text" name="email" />
    <input type="submit" value="Subscribe!" th:attr="value=#{subscribe.submit}"/>
  </fieldset>
</form>
```

- 给多个变量设置单个值

```html
<img src="../../images/gtvglogo.png"  
     th:attr="src=@{/images/gtvglogo.png},title=#{logo},alt=#{logo}" />
```

+ 以上两个的代替写法 th:xxxx

  > 直接用th:value代替th:attr="value=#{subscribe.submit}"，th:value取值解析以后会直接覆盖value原来的值，即 th:xxxx相当于改xxxx属性的值，注意没有单独写xxxx属性直接写th:xxxx也是一样效果

```html
<input type="submit" value="Subscribe!" th:value="#{subscribe.submit}"/>
<form action="subscribe.html" th:action="@{/subscribe}">
```

+ 所有h5兼容的标签写法

https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html#setting-value-to-specific-attributes

#### 标签迭代

> 使用th:each属性对list集合进行遍历，和JSP差不多，复习复习JSP

```html
<tr th:each="prod : ${prods}">
    <td th:text="${prod.name}">Onions</td>
    <td th:text="${prod.price}">2.41</td>
    <td th:text="${prod.inStock}? #{true} : #{false}">yes</td>
</tr>
```

```html
<tr th:each="prod,iterStat : ${prods}" th:class="${iterStat.odd}? 'odd'">
    <td th:text="${prod.name}">Onions</td>
    <td th:text="${prod.price}">2.41</td>
    <td th:text="${prod.inStock}? #{true} : #{false}">yes</td>
</tr>
```

【实际应用】

```html
<table  class="display table table-bordered table-striped" id="dynamic-table">
    <thead>
        <tr>
            <th>#id</th>
            <th>用户名</th>
            <th>密码</th>
        </tr>
    </thead>
    <tbody>
        <tr class="gradeX" th:each="user,status:${users}">
            <!--status是遍历的状态属性和JSP类似，里面的count是下标从1开始，index是从0开始-->
            <td th:text="${status.count}">null</td>
            <td th:text="${user.username}">null</td>
            <td>[[${user.password}]]</td>
        </tr>
    </tbody>
</table>
```

#### 条件运算

```html
<a href="comments.html"
	th:href="@{/product/comments(prodId=${prod.id})}"
	th:if="${not #lists.isEmpty(prod.comments)}">view</a>
```

```html
<div th:switch="${user.role}">
      <p th:case="'admin'">User is an administrator</p>
      <p th:case="#{roles.manager}">User is a manager</p>
      <p th:case="*">User is some other thing</p>
</div>
```

#### 属性优先级

| Order | Feature                         | Attributes                                 |
| :---- | :------------------------------ | :----------------------------------------- |
| 1     | Fragment inclusion              | `th:insert` `th:replace`                   |
| 2     | Fragment iteration              | `th:each`                                  |
| 3     | Conditional evaluation          | `th:if` `th:unless` `th:switch` `th:case`  |
| 4     | Local variable definition       | `th:object` `th:with`                      |
| 5     | General attribute modification  | `th:attr` `th:attrprepend` `th:attrappend` |
| 6     | Specific attribute modification | `th:value` `th:href` `th:src` `...`        |
| 7     | Text (tag body modification)    | `th:text` `th:utext`                       |
| 8     | Fragment specification          | `th:fragment`                              |
| 9     | Fragment removal                | `th:remove`                                |

[官方文档 - 10 Attribute Precedence](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html#attribute-precedence)



### 4.6.3、构建后台管理系统

#### 1. 项目创建

+ 使用IDEA的Spring Initializr构建项目

+ 引入的相关依赖

  - thymeleaf

  - web-starter

  - devtools

  - lombok

#### 2. 添加登陆页面

- `/static` 放置 css，js等静态资源

- `/templates/login.html` 登录页

  > 注意通过label标签设置提示信息

  ```html
  <html lang="en" xmlns:th="http://www.thymeleaf.org"><!-- 要加这玩意thymeleaf才能用 -->
  <form class="form-signin" action="index.html" method="post" th:action="@{/login}">
      ...
      <!-- 消息提醒 -->
      <label style="color: red" th:text="${msg}"></label>
      
      <input type="text" name="userName" class="form-control" placeholder="User ID" autofocus>
      <input type="password" name="password" class="form-control" placeholder="Password">
      <button class="btn btn-lg btn-login btn-block" type="submit">
          <i class="fa fa-check"></i>
      </button>
      ...
  </form>
  ```

- `/templates/main.html` 主页

  + thymeleaf内联写法【也叫行内写法，在文本中使用域中的参数，使用双中括号即可】：

    ```html
    <p>Hello, [[${session.user.name}]]!</p>
    ```

#### 3. 登录功能控制层代码

> 转发存在表单重复提交，一般都使用重定向到目标页面

```java
@Controller
public class IndexController {
    /**
     * @return {@link String }
     * @描述 登录页
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/05/30
     * @since 1.0.0
     */
    @GetMapping({"/","/login"})
    public String IndexController(){
        return "login";
    }

    /**
     * @param user    用户
     * @param session 会话
     * @param model   模型
     * @return {@link String }
     * @描述 登录成功跳转主页面,转发存在表单重复提交问题，一般适用重定向到目标页面
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/05/30
     * @since 1.0.0
     */
    @PostMapping("/login")
    //public String main(String username,String password){//直接用User对象封装表单提交的值
    public String main(User user, HttpSession session, Model model){
        //如果会话域中账户和密码不为空，则判定已经登录过,此时把username和password放入会话域供控制器方法mainPage做登录判断
        //不为空的判断可以使用Spring下的StringUtils的isEmpty、hasText(有内容)、hasLength(有长度)方法进行判断

        //if (StringUtils.isEmpty(user.getUsername())&&StringUtils.hasLength(user.getPassword())) {
        if (!StringUtils.isEmpty(user.getUsername())&&"123".equals(user.getPassword())) {
            session.setAttribute("loginUser",user.getUsername());
            //不要放密码，这样不安全
            //session.setAttribute("password",user.getPassword());
            return "redirect:/main.html";
        }
        //return "main";
        //return "redirect:/main.html";
        //登录不成功就回到登录页面,此时可以给登录页面传递信息，给请求域中放提示错误信息
        model.addAttribute("msg","账号密码错误");
        return "login";
    }

    /**
     * @return {@link String }
     * @描述 主页面，这样也存在问题，只要前端一敲这个地址就可以不登录直接来到主页面，需要在这个控制器方法中加一个登录状态判断
     * 解决办法：创建一个User的bean类
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/05/30
     * @since 1.0.0
     */
    @GetMapping("/main.html")
    public String mainPage(HttpSession session,Model model){
        //直接通过路径访问主页面需要判断是否经过登录，合理的做法是通过拦截器或者过滤器判断登录状态，这里直接为了遍历写在方法里面
        if (session.getAttribute("loginUser") != null) {
            //如果会话域有用户名，跳转主页面【这还是有bug，一个登录处处登录，可以改善，暂时不管】
            return "main";
        }else {
            //登录不成功就回到登录页面,此时可以给登录页面传递信息，给请求域中放提示错误信息
            model.addAttribute("msg","请重新登录");
            return "login";
        }
    }
}
```

#### 4. 导入菜单模板页面

+ 导入table相关模板

  + 将4个table相关页面复制到templates/table/目录下【table目录存放与表格相关的目录】，代表了Data Table下的所有页面

+ 编写控制器方法实现页面跳转

  + 控制器方法代码

    ```java
    /**
     * 负责处理table相关的路径跳转
     */
    @Controller
    public class TableController {
        @GetMapping("/basic_table.html")
        public String basic_table(){
            return "table/basic_table";
        }
    
        @GetMapping("/dynamic_table.html")
        public String dynamic_table(){
            return "table/dynamic_table";
        }
    
        @GetMapping("/responsive_table.html")
        public String responsive_table(){
            return "table/responsive_table";
        }
    
        @GetMapping("/editable_table.html")
        public String editable_table(){
            return "table/editable_table";
        }
    }
    ```

    [^注意]: 由于前端页面的超链接写死了，且每个页面都有相同的部分，控制器方法的超链接一修改所有页面相关的超链接都得改，很不方便，需要抽取公共页面进行统一处理

+ 抽取公共页面

  + 公共部分的Html代码

    ```html
    <!DOCTYPE html>
    <html lang="en" xmlns:th="http://www.thymeleaf.org">
    <head>
      ...
        
      <!--公共部分1-->
      <!--以下4个css、js文件在每个table相关文件中都有，所有包含侧边和顶部的页面这个部分都是一样的-->
      <link href="css/style.css" rel="stylesheet">
      <link href="css/style-responsive.css" rel="stylesheet">
    
      <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
      <!--[if lt IE 9]>
      <script src="js/html5shiv.js"></script>
      <script src="js/respond.min.js"></script>
      <![endif]-->
        
    </head>
    <body class="sticky-header">
    <section>
        
        <!--公共部分2-->
        <!--这个导航栏在各个页面也是一样的-->
        <!-- left side start:左侧导航开始-->
        <div class="left-side sticky-left-side">
    		...
        </div>
        <!-- left side end:左侧导航结束-->
        
        ...
        
        <!--公共部分3-->
        <!-- header section start-->
        <div class="header-section">
           ...
        </div>
        <!-- header section end-->
            
    	...
    </section>
    
    <!--公共部分4-->
    <!--这6个js的引用在每个table中也是公共的，所有包含侧边和顶部的页面这个部分都是一样的-->
    <!-- Placed js at the end of the document so the pages load faster -->
    <script src="js/jquery-1.10.2.min.js"></script>
    <script src="js/jquery-ui-1.9.2.custom.min.js"></script>
    <script src="js/jquery-migrate-1.2.1.min.js"></script>
    <script src="js/bootstrap.min.js"></script>
    <script src="js/modernizr.min.js"></script>
    <script src="js/jquery.nicescroll.js"></script>
    ...
    </body>
    </html>
    ```

  + 抽取公共信息

    > 新建一个html，把所有公共信息写在这个html中，这个页面专门给第三方引用，抽取公共信息的文档参考官方文档第八章Template Layout

    + 抽取步骤：

      + 第一步：声明公共信息

        + 方式一：公共的信息使用th:fragment="xxxx"属性进行声明

          > 公共信息的html文件假如为templates/footer.html
          >
          > 在别处对公共信息进行引用，对应的标签类型要相同，footer是公共信息所在html文件的文件名，xxxx是公共信息的标识名，标识名可以是id属性也可以是fragment属性
          >
          > 对公共信息的引用可以使用三种方式：th：insert、th：replace、th：include，官方文档不推荐3.0以后得Thymeleaf使用th：include
        
          + 在footer.html中使用fragment属性对标签进行标注
        
            ```html
            <!DOCTYPE html SYSTEM "http://www.thymeleaf.org/dtd/xhtml1-strict-thymeleaf-4.dtd">
            <html xmlns="http://www.w3.org/1999/xhtml"
                  xmlns:th="http://www.thymeleaf.org">
              <body>
                <div th:fragment="copy">
                  &copy; 2011 The Good Thymes Virtual Grocery
                </div>
              </body>
            </html>
            ```
        
          + 在目标页面的对应标签中使用th:include通过fragment属性对对应公共信息进行引用
        
            ```html
            <body>
              ...
              <div th:include="footer :: copy"></div>
            </body>
            ```
        
          + 在目标页面的对应标签中使用th:insert通过fragment属性对对应公共信息进行引用
        
            ```html
            <body>
              ...
              <div th:insert="~{footer :: copy}"></div>
            </body>
            ```
        
          + 在目标页面的对应标签中使用th:replace通过fragment属性对对应公共信息进行引用
        
            ```html
            <body>
              ...
              <div th:replace="footer :: copy"></div>
            </body>
            ```
        
            
        
        + 方式二：公共信息使用选择器进行声明
        
          + 在footer.html中使用id正常对标签进行标注
        
            ```html
            ...
            <div id="copy-section">
              &copy; 2011 The Good Thymes Virtual Grocery
            </div>
            ...
            ```
        
          + 在目标页面的对应标签中使用th:include通过id属性对对应公共信息进行引用
        
            ```html
            <body>
              ...
              <div th:include="footer :: #copy-section"></div>
            </body>
            ```
        
          + 在目标页面的对应标签中使用th:insert通过id属性对对应公共信息进行引用
        
            ```html
            <body>
              ...
              <div th:insert="~{footer :: #copy-section}"></div>
            </body>
            ```
        
          + 在目标页面的对应标签中使用th:replace通过fragment属性对对应公共信息进行引用
        
            ```html
            <body>
              ...
              <div th:replace="footer :: #copy-section"></div>
            </body>
            ```
        
          [^注意]: 三种引入方式的区别，这三种方式的区别在官方文档3.0中有介绍
        
          【公共信息部分】
        
          ```html
          <footer th:fragment="copy">
            &copy; 2011 The Good Thymes Virtual Grocery
          </footer>
          ```
        
          【三种引入方式】
        
          ```html
          <body>
            ...
            <div th:insert="footer :: copy"></div>
          
            <div th:replace="footer :: copy"></div>
          
            <div th:include="footer :: copy"></div>
          </body>
          ```
        
          【三种引入方式的效果】
        
          ```html
          <body>
            ...
            <!--th:insert的效果：在div中用insert就嵌入div-->
            <div>
              <footer>
                &copy; 2011 The Good Thymes Virtual Grocery
              </footer>
            </div>
          
            <!--th:replace的效果：会把replace所在的标签div丢掉-->
            <footer>
              &copy; 2011 The Good Thymes Virtual Grocery
            </footer>
          
            <!--th:include的效果：会直接把公共信息标签中的内容放在include所在的标签div中-->
            <div>
              &copy; 2011 The Good Thymes Virtual Grocery
            </div>
          </body>
          ```
        
          【前后端代码修改较多，看视频比较方便，这里不再列举】
  
  + 使用Thymeleaf对后端数据进行循环遍历
  
    【后端数据准备】
  
    ```java
    @GetMapping("/dynamic_table")
    public String dynamic_table(Model model){
        //表格内容的遍历，准备表格
        List<User> users= Arrays.asList(new User("zhangsan","123"),
                new User("lisi","1234"),
                new User("wangwu","12345"),
                new User("zhaoliu","123456"));
        model.addAttribute("users",users);
        return "table/dynamic_table";
    }
    ```
  
    【前端数据展示处理】
  
    ```html
    <table  class="display table table-bordered table-striped" id="dynamic-table">
        <thead>
            <tr>
                <th>#id</th>
                <th>用户名</th>
                <th>密码</th>
            </tr>
        </thead>
        <tbody>
            <tr class="gradeX" th:each="user,status:${users}">
                <!--status是遍历的状态属性和JSP类似，里面的count是下标从1开始，index是从0开始-->
                <td th:text="${status.count}">null</td>
                <td th:text="${user.username}">null</td>
                <td>[[${user.password}]]</td>
            </tr>
        </tbody>
    </table>
    ```
  
    【页面效果】
  
    ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\thymeleaf遍历效果.png)

### 4.6.4、视图解析器与视图原理

> 处理redirect:XXXX的返回值处理器是ViewNameMethodReturnHandler，返回不是null且为字符串就会用这个返回值处理器进行处理，该返回值处理器会把返回的字符串放入mavContainer的view属性中，之前讲过了

1. mavContainer中存放的信息

   + ignoreDefaultModelOnRedirect	后面再说

   + view    【控制器方法字符串返回值对应的字符串一字不差】

   + defaultModel    【这个就是mavContainer中最开始的那个Model】

     [^注意]: 方法形参如果是一个自定义类型对象，对象的属性值需要从请求参数中确定和获取，SpringMVC会自动把该对象放入model即请求域中【必须是自定义对象】?但是示例中的user虽然出现在了defaultModel中，但是并没有出现在ModelAndView中

   + redirectModel     【重定向新建的Model？】

2. SpringMVC会根据mavContainer创建ModelAndView

   + 任何控制器方法都会得到一个ModelAndView，ModelAndView中保存了域数据和视图地址【ModelAndView中的Model都是新建的，在利用mavContainer创建ModelAndView时创建的ModelMap；只是重定向视图还会提前创建一个ModelMap，可暂时理解为转发和重定向创建ModelAndView时传入的model不是同一种，一个传参defaultModel(转发)，一个传参ModelMap（重定向）】

3. doDispatch方法的processDispatchResult方法处理派发结果

   > 这个方法决定了页面该如何响应

   + 调用render方法对页面进行渲染

     + 调用resolveViewName方法根据控制器方法的String类型返回值得到View对象【View对象中定义了页面的渲染逻辑】

       + resolveViewName方法的逻辑是遍历所有的视图解析器尝试是否有能根据当前返回值得到View对象，最终得到了RedirectView；RedirectView是View的一个实现类，View的实现类非常非常多，实际上View这个系统很复杂，AbstractUrlBasedView的同级View有非常非常多

         ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\RedirectView.png)

         [^注意]: 这里的操作很诡异，在ContentNegotiationViewResolver中的resolveViewName方法中调用getCandidateViews方法来对其他四种视图解析器进行遍历能不能解析出对应的视图View对象

         ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\内容协商视图解析器.png)
       
       + 得到视图对象是为了调用自定义的render方法来进行页面渲染工作
       
         > RedirectView通过重定向到一个页面进行渲染
         >
         > + 获取目标url地址
         > + 使用原生Servlet的response的sendRedirect方法进行重定向
       
         ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\View.png)
   

​							【render方法中的renderMergedOutputModel方法】

> 为什么这里render方法还在AbstractView中，renderMergedOutputModel方法就直接跳去RedirectView了，这里的renderMergedOutputModel方法和重点4中不一样，单独说一下，以下就是RedirectView的渲染方法

```java
public class RedirectView extends AbstractUrlBasedView implements SmartView {
	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

        //分支1
		String targetUrl = createTargetUrl(model, request);//获取要重定向的目标url
		targetUrl = updateTargetUrl(targetUrl, model, request, response);

		// Save flash attributes
		RequestContextUtils.saveOutputFlashMap(targetUrl, request, response);

        //分支2
		// Redirect
		sendRedirect(request, response, targetUrl, this.http10Compatible);//执行重定向
	}
}
```

【分支1：createTargetUrl方法分支】

【renderMergedOutputModel方法中的createTargetUrl方法】

```java
public class RedirectView extends AbstractUrlBasedView implements SmartView {
	protected final String createTargetUrl(Map<String, Object> model, HttpServletRequest request) throws UnsupportedEncodingException {
		// Prepare target URL.
		StringBuilder targetUrl = new StringBuilder();//创建一个可变长度字符串对象
		String url = getUrl();//这个url就是/main.html
		Assert.state(url != null, "'url' not set");

		if (this.contextRelative && getUrl().startsWith("/")) {
			// Do not apply context path to relative URLs.
			targetUrl.append(getContextPath(request));//向可变长度字符串添加获取的url，即/main.html
		}
		targetUrl.append(getUrl());

		String enc = this.encodingScheme;
		if (enc == null) {
			enc = request.getCharacterEncoding();//准备编码格式
		}
		if (enc == null) {
			enc = WebUtils.DEFAULT_CHARACTER_ENCODING;//设置编码UTF-8
		}

		if (this.expandUriTemplateVariables && StringUtils.hasText(targetUrl)) {
			Map<String, String> variables = getCurrentRequestUriVariables(request);
			targetUrl = replaceUriTemplateVariables(targetUrl.toString(), model, variables, enc);//这里还是/main.html
		}
		if (isPropagateQueryProperties()) {
			appendCurrentQueryParams(targetUrl, request);
		}
		if (this.exposeModelAttributes) {//如果有一些属性如exposeModelAttributes要重定向，会将这些重定向属性以请求参数的形式拼接到url后边
			appendQueryProperties(targetUrl, model, enc);
		}

		return targetUrl.toString();
	}
}
```

【分支2：sendRedirect方法分支】

【renderMergedOutputModel方法中的sendRedirect方法】

```java
public class RedirectView extends AbstractUrlBasedView implements SmartView {
	protected void sendRedirect(HttpServletRequest request, HttpServletResponse response,
			String targetUrl, boolean http10Compatible) throws IOException {

		String encodedURL = (isRemoteHost(targetUrl) ? targetUrl : response.encodeRedirectURL(targetUrl));//拿到url，如果url中有中文进行编码
		if (http10Compatible) {
			HttpStatus attributeStatusCode = (HttpStatus) request.getAttribute(View.RESPONSE_STATUS_ATTRIBUTE);
			if (this.statusCode != null) {
				response.setStatus(this.statusCode.value());
				response.setHeader("Location", encodedURL);
			}
			else if (attributeStatusCode != null) {
				response.setStatus(attributeStatusCode.value());
				response.setHeader("Location", encodedURL);
			}
			else {
				// Send status code 302 by default.
				response.sendRedirect(encodedURL);//调用原生servlet的response的sendRedirect方法来重定向，执行到这儿没有直接跳出页面，感觉像循环执行了多次请求后才逐渐出现页面的
			}
		}
		else {
			HttpStatus statusCode = getHttp11StatusCode(request, response, targetUrl);
			response.setStatus(statusCode.value());
			response.setHeader("Location", encodedURL);
		}
	}
}
```

4. 不同场景下的视图解析

   + 返回值以forward:开始：创建InternalResourceView(forwardUrl)-->render策略是request.getRequestDispatcher（request，diapatcherPath）拿到转发器rd[RequestDispatcher],通过rd的forward(request,response)方法进行转发;本质上就是原生Servlet的转发request.getRequestDispatcher(path).forward(request,response)

   + 返回值以redirect:开始：创建RedirectView(redirectUrl)-->render就是调用原生的重定向response.sendRedirect(encodedURL);

   + 返回值是普通字符串，会创建ThymeleafView

     【ThymeleafView的render方法】

     ```java
     public class ThymeleafView extends AbstractThymeleafView {
     	public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
             this.renderFragment(this.markupSelectors, model, request, response);//调用renderFragment进行渲染
         }
         
         //ThymeleafView中的render方法中的renderFragment方法
         protected void renderFragment(Set<String> markupSelectorsToRender, Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
             ServletContext servletContext = this.getServletContext();
             String viewTemplateName = this.getTemplateName();
             ISpringTemplateEngine viewTemplateEngine = this.getTemplateEngine();//拿到Thymeleaf模板引擎
             if (viewTemplateName == null) {
                 ...
             } else {
                 Map<String, Object> mergedModel = new HashMap(30);//拿到要给页面渲染的数据
                 Map<String, Object> templateStaticVariables = this.getStaticVariables();
                 ...
                 if (model != null) {
                     mergedModel.putAll(model);//这个mergeModel中就有给请求域放入的数据如users的list集合，还有其他东西，暂时看不懂
                 }
                 ...
                 WebExpressionContext context = new WebExpressionContext(configuration, request, response, servletContext, this.getLocale(), mergedModel);//拿到Thymeleaf模板引擎的一些配置
                 String templateName;
                 Set markupSelectors;
                 if (!viewTemplateName.contains("::")) {//判断页面地址是不是有::，公共信息引用中有的【这个地址是页面中的地址解析】
                     templateName = viewTemplateName;
                     markupSelectors = null;
                 } else {
                     ...
                 }
     
                 String templateContentType = this.getContentType();//拿到页面要响应的内容类型text/html;charset=utf-8
                 Locale templateLocale = this.getLocale();
                 String templateCharacterEncoding = this.getCharacterEncoding();//拿到字符编码
                 Set processMarkupSelectors;
                 ...
                 boolean producePartialOutputWhileProcessing = this.getProducePartialOutputWhileProcessing();
                 Writer templateWriter = producePartialOutputWhileProcessing ? response.getWriter() : new FastStringWriter(1024);
                 viewTemplateEngine.process(templateName, processMarkupSelectors, context, (Writer)templateWriter);//模板引擎调用process方法，这里面拿到输出数据流writer，把所有的页面内容刷到writer中，刷出的页面内容在其中的writer中的out属性的bb属性的hb属性View as String就能看到；所以还是找出资源搞成字符串然后用输出流返回字符串给浏览器，而不是直接将静态页面直接返回
                 ...
             }
         }
     }
     ```

     > 通过以上内容可知我们可以自定义视图解析器+自定义视图来完成更复杂的功能，比如把页面渲染的数据直接包装成excel表格，在自定义视图时在render方法中创建所有的excel表格，使用response将所有的表格响应出去【这些东西在尚硅谷大厂学院里面讲】

### 4.6.5、拦截器实现登录检查

所有拦截器都继承了接口HandlerInterceptor，拦截器相关的知识见web开发核心对象

1. 自定义登录检查拦截器代码

   ```java
   @Slf4j
   public class LoginInterceptor implements HandlerInterceptor {
       /**
        * @return boolean
        * @描述 控制器方法执行前处理
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
   ```

2. 自定义拦截器添加到组件【注册时通过实现WebMvcConfiguration的addInterceptor方法】

   > 在该步骤中指定拦截规则，拦截所有也包括静态资源在内，排除拦截的路径包括/和/login以及所有的静态资源访问；如果只是精确拦截则不需要管静态资源的事情

   ```java
   @Configuration
   public class AdminWebConfig implements WebMvcConfigurer {
       @Override
       public void addInterceptors(InterceptorRegistry registry) {
           registry.addInterceptor(new LoginInterceptor())
                   .addPathPatterns("/**")//所有请求都会被拦截包括静态资源
                   .excludePathPatterns("/","/login","/css/**","/fonts/**","/images/**","/js/**");
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
   }
   ```



# 备注

> 为避免文件过大造成数据丢失，后面的笔记记在Note2中



# 附录

### 1、其他事项

------

1. IDEA快捷键：

   - ` ctrl + shift + alt + U`：在pom.xml文件中使用以分析依赖树的方式显示项目中依赖之间的关系，也可以在pom.xml中右键Diagrams的Show Dependencies显示分析依赖树。
   - `Ctrl + Alt + B`:查看类的具体实现代码
   - `Ctrl+n`和`双击shift`的效果相同
   - Ctrl + H : 以树形方式展现类继承结构图
   - Ctrl + Alt + U : 鼠标放在类上以弹窗的形式用UML类图展现当前类的父类以及实现哪些接口
   - Crtl + Alt + Shift + U : 鼠标放在类上以新页面的形式用UML类图展现当前类的父类以及实现哪些接口
   - shift+insert：可以直接把复制的类加入到包中，感觉作用像粘贴，笑了就是粘贴，只是为了方便左撇子
   - Alt+f7：选中类使用该快捷键或者选中find Usages可以列出一个类的调用者
   - 右键方法-->goto-->implement：可以查看对应哪些子类实现了了该方法，按住ctrl点击方法名可能会跳去接口默认实现的方法，以上方法能跳转去子类实现的对应方法，即debug中点击进入方法的效果，不过需要明晰到底去哪个子类中执行了对应的方法
   - shift+f6：给文件重命名
   - ctrl+n：效果和双击shift是一样的
   - alt+鼠标：与鼠标中键的效果相同，块编辑

2. springBoot应用run方法会返回IoC容器，IoC容器中包含当前应用的所有组件

   ```java
   public static void main(String[] args) {
       //SpringApplication.run(MainApplication.class, args)会返回IoC容器
       ConfigurableApplicationContext run = SpringApplication.run(MainApplication.class, args);
       //通过IoC容器的getBeanDefinitionNames()方法可以获取所有组件的名字，返回字符串数组
       String[] names = run.getBeanDefinitionNames();
       //Stream流式编程
       Arrays.stream(names).forEach(name->{
           System.out.println(name);
       });
   }
   ```

3. 从IoC容器中获取组件对象的方法

   + User user01 = run.getBean("user01", User.class);

     通过id和组件的class对象获取组件对象

   + MyConfig bean = run.getBean(MyConfig.class);

     仅通过组件的class对象获取单个组件对象

   + String[] users = run.getBeanNamesForType(User.class);

     从容器中根据类型获取该类型对应的所有组件名字

   + boolean tom = run.containsBean("tom");

     根据名字(id)判断容器中是否存在对应组件，true表示存在，false表示不存在

4. debug模式下运行至断点位置选中当前行的某段代码右键Evaluate Expression或者alt+F8可以唤出结算界面获取断点行某个代码片段的运算值

5. SpringBoot的设计思想:

   + SpringBoot默认在底层配置好所有的组件，但是如果用户配置了组件就以用户的优先，比如CharacterEncodingFilter，实现方式是条件装配没有当前类型的组件就生效创建组件，有就自动配置组件失效

6. 浏览器快捷键:

   + 浏览器使用ctrl+f5是不走缓存发送请求

7. UrlpathHelper【**URL路径帮助器**】常用方法介绍

   + decodeMatrixVariables（）
     + 解码路径中的矩阵变量方法
   + decodePathVariables（）
     + 解码路径变量
   + decodeRequestString（）
     + 解码请求字符串
   + ...

8. 浏览器相关操作
   + Application中可以查看cookies，自己发给浏览器的cookie浏览器会在原来发回来的cookie后面追加

9. Map集合的流式编程

   > 可以直接用变量表示key和value然后直接用

   ```java
   model.forEach((name, value) -> {//学到了对Map集合的流式编程，直接用key和value
           if (value != null) {
               request.setAttribute(name, value);
           }
           else {
               request.removeAttribute(name);
           }
       });
   ```

10. HttpServletResponse接口中有所有的Http响应状态码以及相应的状态信息，从345行开始

11. debug查看对象属性时Byte数组可以View as String

12. Arrays.asList(headerValueArray);//将字符串数组转成List集合

    ```java
    List<User> users= Arrays.asList(new User("zhangsan","123"),
                    new User("lisi","1234"),
                    new User("wangwu","12345"),
                    new User("zhaoliu","123456"));
    ```

13. 设置服务器的前置路径:

    + 在属性配置文件中设置属性server.servlet.context-path为自定义上下文路径如/world即可【以后所有的请求都以/world开始】

14. 浏览器中li标签的class="active"属性可以设置选中状态高亮,实现原理是什么，怎么实现公共页面抽取后仍然能实现对应选项高亮？

    ```html
    <ul class="sub-menu-list">
        <li class="active"><a href="basic_table.html"> Basic Table</a></li>
        <li><a href="dynamic_table.html"> Advanced Table</a></li>
        <li><a href="responsive_table.html"> Responsive Table</a></li>
        <li><a href="editable_table.html"> Edit Table</a></li>
    </ul>
    ```

    

   

​		







