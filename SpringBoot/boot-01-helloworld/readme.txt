P5 需求:
浏览发送/hello请求，服务器响应Hello,SpringBoot2

使用原生spring的方式,需要导入spring和springMVC依赖，编写配置文件，开发代码，将tomcat引入idea，将应用部署在tomcat上启动运行

用springBoot开发web场景的流程
1.在pom.xml中引入父工程spring-boot-starter-parent
2.在pom.xml中引入web的场景启动器依赖spring-boot-starter-web
3.编写主程序类如MainApplication，并且必须用@SpringBootApplication注解告诉springBoot这是一个springboot应用，并且在主程序类的主方法中编写
    代码SpringApplication.run(MainApplication.class,args)传入主程序类的class对象和主方法的args，该方法的作用相当于让主程序类对应的springboot
    代码应用跑起来，还可以直接点击debug也可以
4.在主程序类所在的包下创建控制器，编写对应/hello请求路径的对应控制器方法，并响应浏览器一段字符串，注意这里的/hello就是webapp的路径，即http://localhost:8080/hello
5.直接运行主程序的主方法即可(对比以前还需要整Tomcat和很多配置文件)
经过测试，确实Ok

springBoot最强大的功能是简化配置
(比如改tomcat端口号，以前需要打开tomcat的配置文件改端口号，springboot可以直接在类路径下的一个属性配置文件中修改所有的配置信息，该文件有固定名字application.properties)
springboot本身有默认配置，在application.properties文件中可以进行修改的配置可以参考官方文档的Application Properties，使用ctrl+f能在文档中进行搜索
比如服务器的端口名固定为server.port;除此外还有配置的默认值信息;woc,IDEA对属性名还有提示功能


可以使用springboot的spring-boot-maven-plugin插件把springboot应用打成一个可执行的jar包，这个jar包称为小胖jar(fat.jars)，这个小胖jar包含整个运行环境，
可以直接通过DOS窗口运行，以前需要打成war包部署到服务器上.
pom.xml插件配置代码
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

打好的小胖jar可以直接在DOS命令窗口的当前目录使用命令java -jar boot-01-helloworld-1.0-SNAPSHOT.jar直接运行，经验证OK
实际生产环境部署也是直接引入打包插件打成小胖jar，直接在目标服务器执行即可，注意:要关闭DOS命令窗口的快速编辑模式，否则鼠标只要一点
DOS命令窗口，springboot应用的启动进程就会卡住

小胖jar中BOOT-INF下的lib下是第三方的所有jar包
BOOT-INF下的classes下是我们自己写的代码和配置文件

P6
了解自动配置原理

SpringBoot的两大优秀特性
1.依赖管理
1.1


2.自动配置
