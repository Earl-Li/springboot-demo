# 4、SpringBoot-Web开发

## 4.7、文件上传

### 4.7.1、文件上传保存实例

1. 前端页面文件上传表单准备

   > 重点是表单的单文件和多文件上传
   >
   > 多文件上传input标签的type属性为file类型，且需要在input标签中添加multiple字段，没有multiple字段就表示单文件上传

   ```html
   <div class="panel-body">
       <!--enctype就是encodetype是编码类型的意思。
   	multipart/form-data是指表单数据有多部分构成，既有文本数据，又有文件等二进制数据的意思。
   	默认情况下，enctype的值是application/x-www-form-urlencoded，不能用于文件上传，只有使用了multipart/form-data，才能完整的传递文件数据。-->
       <form role="form" th:action="@{/upload}" method="post" enctype="multipart/form-data">
           <div class="form-group">
               <label for="exampleInputEmail1">邮箱</label>
               <input type="email" name="email" class="form-control" id="exampleInputEmail1"
                      placeholder="Enter email">
           </div>
           <div class="form-group">
               <label for="exampleInputPassword1">名字</label>
               <input type="password" name="username" class="form-control" id="exampleInputPassword1" placeholder="Password">
           </div>
           <div class="form-group">
               <label for="exampleInputFile">头像</label>
               <input type="file" name="headImg" id="exampleInputFile">
           </div>
           <div class="form-group">
               <label for="exampleInputFile">生活照</label>
               <!--multiple用在input的file中就是表示多文件上传，没有multiple就表示单文件上传-->
               <input type="file" name="photos" multiple>
           </div>
           <div class="checkbox">
               <label>
                   <input type="checkbox"> Check me out
               </label>
           </div>
           <button type="submit" class="btn btn-primary">提交</button>
       </form>
   </div>
   ```

   【前端页面效果】

   ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\文件提交表单.png)

2. 控制器方法

   > 重点是如何接收前端上传的文件以及文件的保存
   >
   > + 单个文件接收
   >   + 使用@RequestPart("headImg")注解可以为MultipartFile类型形参自动封装名为headImg的单个文件为单个对象
   > + 多个文件接收
   >   + 使用@RequestPart("photos")注解可以为MultipartFile[]类型形参自动封装名为photos的多个文件为数组、
   > + 文件保存
   >   + MultipartFile对象的isEmpty方法可以判断文件有内容
   >   + MultipartFile对象的getOriginalFilename方法可以获取原文件的名字，并不是表单中提交的名字而是文件原始名字
   >   + MultipartFile对象的getName方法可以获取表单中文件的名字，不是原始文件的名字
   >   + MultipartFile对象的getInputStream方法可以获取原始输入流可以对流进行操作
   >   + MultipartFile对象的transferTo方法是对getInputStream方法获取的流对象的保存进行了封装，需要传参目标文件File对象【利用Spring自家的fileCopyUtils文件复制工具类的copy方法，传参操作文件对象，目标文件对象实现文件的复制转移】
   >   + new File("F:\\\cache\\\\"+originalFilename)
   >     + 一定要注意，java中new File时文件名不存在没关系，没有会自己新建，但是目录名必须有，目录名不会自己新建，实在没有使用mkdir进行创建，
   >     + 此外注意同名同内容的文件会直接进行覆盖而不会追加，同名文件不同内容也会直接覆盖掉原内容一般适用uuid解决这个问题
   
   ```java
   @Controller
   @Slf4j
   public class FormTestController {
       /**
        * @描述 
        *使用@RequestPart("headImg")注解可以为MultipartFile类型形参自动封装名为headImg的单个文件为单个对象
        *使用@RequestPart("photos")注解可以为MultipartFile[]类型形参自动封装名为photos的多个文件为数组
        * @author Earl
        * @version 1.0.0
        */
       @PostMapping("/upload")
       public String upload(@RequestParam("email") String email,
                            @RequestParam("username") String username,
                            @RequestPart("headImg") MultipartFile headImg,
                            @RequestPart("photos") MultipartFile[] photos
                            ) throws IOException {
           log.info("上传的信息:email={},username={},headImg的大小={},photos的个数={}",
                   email,username,headImg.getSize(),photos.length);
           //上传的信息:email=2625074321@qq.com,username=zhangsan,headImg的大小=1347131,photos的个数=3
           /**
            * 对文件进行保存操作
            *      SpringBoot的MultipartFile对象提供isEmpty实例方法判断文件对象是否为空
            *      业务逻辑是不为空则保存到文件服务器或者阿里云的对象存储服务器OSS服务器
            *      这里直接存入本地磁盘
            * */
           if (!headImg.isEmpty()) {
               /**可以通过MultipartFile对象的实例方法getOriginalFilename拿到原始文件的文件名，这个文件名可以作为保存文件的文件名*/
               String originalFilename = headImg.getOriginalFilename();
               /**通过MultipartFile对象的实例方法getInputStream()拿到原始输入流后想怎么操作就怎么操作，SpringBoot为了方便还专门封装成
               一个transferTo方法直接将上传文件保存到对应的磁盘地址*/
               //headImg.getInputStream()
               headImg.transferTo(new File("F:\\cache\\"+originalFilename));
           }
           if (photos.length>0){
               for (MultipartFile photo: photos) {
                   if (!photo.isEmpty()) {
                       String originalFilename = photo.getOriginalFilename();
                       /**一定要注意，java中new File时文件名不存在没关系，没有会自己新建，但是目录名必须有，目录名不会自己新建，
                        * 实在没有使用mkdir进行创建，此外注意同名同内容的文件会直接进行覆盖而不会追加，同名文件不同内容也会直接覆盖掉原内容
                        * 一般适用uuid解决这个问题*/
                       photo.transferTo(new File("F:\\cache\\"+originalFilename));
                   }
               }
           }
           return "main";
       }
   }
   ```
   
   [^注意]: 上传文件过大需要在全局配置文件中设置以下属性值`spring.servlet.multipart.max-file-size: 20MB`,因为SpringMVC默认只支持1MB，超过会直接报错
   
   【文件上传解析器】
   
   > 这个也是Springboot自动配置的
   
   ```java
   @Configuration(proxyBeanMethods = false)
   @ConditionalOnClass({ Servlet.class, StandardServletMultipartResolver.class, MultipartConfigElement.class })
   @ConditionalOnProperty(prefix = "spring.servlet.multipart", name = "enabled", matchIfMissing = true)//跟文件上传的相关属性都以spring.servlet.multipart为前缀，所有的属性最终都被封装到MultipartProperties中
   @ConditionalOnWebApplication(type = Type.SERVLET)
   @EnableConfigurationProperties(MultipartProperties.class)
   public class MultipartAutoConfiguration {
   	...
   }
   ```
   
   【MultipartProperties封装文件上传属性值的文件】
   
   ```java
   @ConfigurationProperties(prefix = "spring.servlet.multipart", ignoreUnknownFields = false)
   public class MultipartProperties {
   	...
   	/**
   	 * Max file size.
   	 */
   	private DataSize maxFileSize = DataSize.ofMegabytes(1);//maxFileSize这个就是上传文件最大大小的属性，默认的最大文件限制是1MB
   
   	/**
   	 * Max request size.
   	 */
   	private DataSize maxRequestSize = DataSize.ofMegabytes(10);//最大的请求大小限制默认是10MB【所有的总请求最大的总上传文件大小不超过10MB】
   	...
   }
   ```

### 4.7.2、文件上传原理

1. 文件上传解析器的自动配置原理

   + 文件上传自动配置类-MultipartAutoConfiguration-MultipartProperties【相应属性配置文件】
   + 自动配置了StandardServletMultipartResolver【文件上传解析器】
     + 这个文件上传解析器只能处理遵守Servlet协议上传过来的文件，如果是自定义以流的形式上传的文件需要自定义文件上传解析器
   + 所有文件上传的有关配置属性全部封装在MultipartProperties中

   ```java
   @Configuration(proxyBeanMethods = false)
   @ConditionalOnClass({ Servlet.class, StandardServletMultipartResolver.class, MultipartConfigElement.class })
   @ConditionalOnProperty(prefix = "spring.servlet.multipart", name = "enabled", matchIfMissing = true)//跟文件上传的相关属性都以spring.servlet.multipart为前缀，所有的属性最终都被封装到MultipartProperties中
   @ConditionalOnWebApplication(type = Type.SERVLET)
   @EnableConfigurationProperties(MultipartProperties.class)
   public class MultipartAutoConfiguration {
   	
   	private final MultipartProperties multipartProperties;
   
   	public MultipartAutoConfiguration(MultipartProperties multipartProperties) {
   		this.multipartProperties = multipartProperties;
   	}
   
   	@Bean//文件上传的一些配置信息MultipartConfigElement
   	@ConditionalOnMissingBean({ MultipartConfigElement.class, CommonsMultipartResolver.class })
   	public MultipartConfigElement multipartConfigElement() {
   		return this.multipartProperties.createMultipartConfig();
   	}
   
   	@Bean(name = DispatcherServlet.MULTIPART_RESOLVER_BEAN_NAME)//文件上传解析器StandardServletMultipartResolver，放入的名字也叫MULTIPART_RESOLVER_BEAN_NAME即multipartResolver
   	@ConditionalOnMissingBean(MultipartResolver.class)//当容器中没有文件上传解析器的时候再装配这个默认的文件上传解析器
   	public StandardServletMultipartResolver multipartResolver() {
   		StandardServletMultipartResolver multipartResolver = new StandardServletMultipartResolver();
   		multipartResolver.setResolveLazily(this.multipartProperties.isResolveLazily());
   		return multipartResolver;
   	}
   }
   ```

2. 文件上传实现源码追踪

   + 第一步：判断是否文件上传请求

     + 请求进入doDispatch方法的checkMultipart(request)方法，使用文件上传解析器的isMultipart方法判断是否文件上传请求，是文件上传请求使用文件上传解析器的resolveMultipart方法对原生请求封装成MultipartHttpServletRequest类型的StandardMultipartHttpServletRequest对象并赋值给processedRequest

       > 在这一步就将请求所有的文件信息全部封装到一个MultiValueMap<String,MultipartFile>Map集合中了，之后直接通过注解的value属性从Map中拿文件作为参数值，这里雷神没有细讲怎么封装的Map【很诡异，value确实是MultiValueMap，但是实际上value是一个StrandradMultipartFile的ArrayList集合，而且也没有看到MultipartFile的继承结构里面有ArrayList】

     + 如果不是文件上传请求则直接把原生请求赋值给processedRequest

   + 第二步：如果是文件上传请求将multipartRequestParsed属性改为true

   + 第三步：handle方法中对自动接收文件的形参的处理

   ```java
   protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
       HttpServletRequest processedRequest=request;//这个request对象的request属性的coyoteRequest属性能看到请求是否是静态资源的请求
       ...
       boolean multipartRequestParsed = false;//文件上传请求解析默认是false，即以multipartRequestParsed为true标志这是一个文件上传请求，默认不是文件上传请求；判断为true的依据是原生请求因为内容类型以multipart/开始就对原生请求进行包装，只要包装了就是文件上传请求
       ...
       try {
           ...
           try {
               //分支一
               processedRequest = checkMultipart(request);//检查是否文件上传请求，如果是文件上传请求把原生请求request赋值给processedRequest，即如果是文件上传请求，就把原生请求包装成文件上传请求processedRequest；checkMultipart方法中使用的是MultipartResolver的isMultipart方法对是否文件上传请求进行判断的，判断依据是请求的内容类型是否叫“multipart/”,所有这里决定了form表单需要写enctype="multipart/form-data";如果是文件上传请求使用MultipartResolver文件上传解析器的resolverMultipart(request)方法把文件上传请求进行解析包装成StandardMultipartHttpServletRequest这个类型并返回；这里如果是文件上传请求会进行包装，返回MultipartHttpServletRequest包装请求给processedRequest，如果不是文件上传请求，会直接把原生request直接赋值给processedRequest
               multipartRequestParsed = (processedRequest != request);//如果是文件上传请求根据检查结果重新设定文件上传请求解析，进行了请求包装就把multipartRequestParsed属性值改成true
   
               mappedHandler = getHandler(processedRequest);
               if (mappedHandler == null) {
                   noHandlerFound(processedRequest, response);
                   return;
               }
   
               HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());
               ...
   
               if (!mappedHandler.applyPreHandle(processedRequest, response)) {
                   return;
               }//执行拦截器的preHandle方法
   
               //分支二
               //处理文件上传请求，核心是形参处理，即对文件的自动封装原理
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
       ...
   }
   ```

   【分支一：checkMultipart方法分支】

   ------

   【doDispatch方法中的checkMultipart方法】

   ```java
   @SuppressWarnings("serial")
   public class DispatcherServlet extends FrameworkServlet {
   	protected HttpServletRequest checkMultipart(HttpServletRequest request) throws MultipartException {
           //小分支1
   		if (this.multipartResolver != null && this.multipartResolver.isMultipart(request)) {//调用文件上传解析器的isMultipart方法判断是否文件上传请求
   			if (WebUtils.getNativeRequest(request, MultipartHttpServletRequest.class) != null) {
   				if (DispatcherType.REQUEST.equals(request.getDispatcherType())) {
   					logger.trace("Request already resolved to MultipartHttpServletRequest, e.g. by MultipartFilter");
   				}
   			}
   			else if (hasMultipartException(request)) {
   				logger.debug("Multipart resolution previously failed for current request - " +
   						"skipping re-resolution for undisturbed error rendering");
   			}
   			else {
   				try {
                       //小分支2
   					return this.multipartResolver.resolveMultipart(request);//调用文件上传解析器的resolveMultipart(request)方法对原生请求进行封装
   				}
   				catch (MultipartException ex) {
   					if (request.getAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE) != null) {
   						logger.debug("Multipart resolution failed for error dispatch", ex);
   						// Keep processing error dispatch with regular request handle below
   					}
   					else {
   						throw ex;
   					}
   				}
   			}
   		}
   		// If not returned before: return original request.
   		return request;
   	}
   }
   ```

   【小分支1：isMultipart方法分支】

   【checkMultipart方法中的isMultipart方法】

   ```java
   public class StandardServletMultipartResolver implements MultipartResolver {
   	@Override
   	public boolean isMultipart(HttpServletRequest request) {
   		return StringUtils.startsWithIgnoreCase(request.getContentType(),
   				(this.strictServletCompliance ? MediaType.MULTIPART_FORM_DATA_VALUE : "multipart/"));//判断请求的内容类型是不是以multipart/开始的，如果是则是文件上传请求，如果不是，则不是文件上传请求
   	}
   }
   ```

   【小分支2：resolveMultipart方法分支】

   【checkMultipart方法中的resolveMultipart方法】

   ```java
   public class StandardServletMultipartResolver implements MultipartResolver {
   	@Override
   	public MultipartHttpServletRequest resolveMultipart(HttpServletRequest request) throws MultipartException {
   		return new StandardMultipartHttpServletRequest(request, this.resolveLazily);//通过原生request创建StandardMultipartHttpServletRequest对象以MultipartHttpServletRequest的类型返回
   	}
   }
   ```

   【文件上传请求的参数处理过程】

   > 文件上传请求的参数解析器是RequestPartMethodArgumentResolver，该参数解析器解析请求中的文件内容并封装成MultipartFile对象或者MultipartFile数组

   【】

   ```java
   public class InvocableHandlerMethod extends HandlerMethod {
   	@Nullable
   	public Object invokeForRequest(NativeWebRequest request, @Nullable ModelAndViewContainer mavContainer,
   			Object... providedArgs) throws Exception {
   
   		Object[] args = getMethodArgumentValues(request, mavContainer, providedArgs);//获取所有形参的值
   		if (logger.isTraceEnabled()) {
   			logger.trace("Arguments: " + Arrays.toString(args));
   		}
   		return doInvoke(args);
   	}
   
   	//invokeForRequest方法中的getMethodArgumentValues方法
   	protected Object[] getMethodArgumentValues(NativeWebRequest request, @Nullable ModelAndViewContainer mavContainer,
   			Object... providedArgs) throws Exception {
   
   		MethodParameter[] parameters = getMethodParameters();
   		if (ObjectUtils.isEmpty(parameters)) {
   			return EMPTY_ARGS;
   		}
   
   		Object[] args = new Object[parameters.length];
   		for (int i = 0; i < parameters.length; i++) {//对所有参数进行遍历，找到MultipartFile类型的参数
   			MethodParameter parameter = parameters[i];
   			parameter.initParameterNameDiscovery(this.parameterNameDiscoverer);
   			args[i] = findProvidedArgument(parameter, providedArgs);
   			if (args[i] != null) {
   				continue;
   			}
   			if (!this.resolvers.supportsParameter(parameter)) {//在这里面对所有的参数解析器遍历，找出支持MultipartFile类型的参数解析的参数解析器，最后结果就是RequestPartMethodArgumentReolver
   				throw new IllegalStateException(formatArgumentError(parameter, "No suitable resolver"));
   			}
   			try {
   				args[i] = this.resolvers.resolveArgument(parameter, mavContainer, request, this.dataBinderFactory);//使用参数解析器的resolveArgument方法对参数进行解析，从缓存中拿参数解析器，拿不到就循环遍历
   			}
   			...
   		}
   		return args;
   	}
   }
   ```

   【文件上传参数的类型】

   ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\参数类型.png)

   【RequestPartMethodArgumentReolver参数解析器中的resolveArgument方法】

   ```java
   public class RequestPartMethodArgumentResolver extends AbstractMessageConverterMethodArgumentResolver {
   	@Override
   	public boolean supportsParameter(MethodParameter parameter) {
   		if (parameter.hasParameterAnnotation(RequestPart.class)) {
   			return true;
   		}
   		else {
   			if (parameter.hasParameterAnnotation(RequestParam.class)) {
   				return false;
   			}
   			return MultipartResolutionDelegate.isMultipartArgument(parameter.nestedIfOptional());
   		}
   	}
   
   	@Override
   	@Nullable
   	public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
   			NativeWebRequest request, @Nullable WebDataBinderFactory binderFactory) throws Exception {
   
   		HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
   		Assert.state(servletRequest != null, "No HttpServletRequest");
   
   		RequestPart requestPart = parameter.getParameterAnnotation(RequestPart.class);//拿到requestPart注解，这个注解中有很多信息
   		boolean isRequired = ((requestPart == null || requestPart.required()) && !parameter.isOptional());
   
   		String name = getPartName(parameter, requestPart);//通过注解获取相应提交文件的form表单名字
   		parameter = parameter.nestedIfOptional();
   		Object arg = null;
   
   		Object mpArg = MultipartResolutionDelegate.resolveMultipartArgument(name, parameter, servletRequest);//使用文件上传解析的代理来解析文件上传请求的参数
   		if (mpArg != MultipartResolutionDelegate.UNRESOLVABLE) {
   			arg = mpArg;
   		}
   		...
   	}
   }
   ```

   【resolveArgument方法的resolveMultipartArgument方法】

   ```java
   public final class MultipartResolutionDelegate {
   	@Nullable
   	public static Object resolveMultipartArgument(String name, MethodParameter parameter, HttpServletRequest request)
   			throws Exception {
   
   		MultipartHttpServletRequest multipartRequest =
   				WebUtils.getNativeRequest(request, MultipartHttpServletRequest.class);//拿到文件上传请求
   		boolean isMultipart = (multipartRequest != null || isMultipartContent(request));
   		...
   		else if (isMultipartFileArray(parameter)) {//判断参数是不是MultipartFile数组
   			if (!isMultipart) {
   				return null;
   			}
   			if (multipartRequest == null) {
   				multipartRequest = new StandardMultipartHttpServletRequest(request);
   			}
   			List<MultipartFile> files = multipartRequest.getFiles(name);//如果是MultipartFile数组通过注解的名字把请求中的文件获取并封装成MultipartFile集合，这个getFiles方法其实也是去提前封装好的multipartFiles集合中依靠名字直接拿的，在该方法执行以前所有的文件就已经被封装到multipartFiles属性中，该属性是一个LinkedMultiValueMap，key为form表单的对应name，value为一个StandardMultipartFile类型的ArrayList集合，这个multipartFiles是在在dodispatch()方法中的checkMultipart(request)方法中直接通过request生成的
   			return (!files.isEmpty() ? files.toArray(new MultipartFile[0]) : null);
   		}
   		...
   	}
   }
   ```


## 4.8、异常处理机制

> SpringBoot异常处理机制参考官方文档Spring Boot Features-->developing-web-applications-->Error Handling

### 4.8.1、异常机制总览

1. 默认情况下，SpringBoot会启动一个/error的映射来处理所有的错误，发生错误会自动转发到/error，

   + 如果是机器客户端，会产生一个JSON格式的响应数据【包含错误的时间戳、HTTP状态错误码和错误原因信息、异常信息、那个路径发生了错误】

     【Postman响应的错误信息】

     ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\错误json.png)

   + 如果是浏览器客户端，会产生一个白页，白页渲染为一个HTML页面，展示相同的错误信息

     【浏览器白页效果】

     ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\白页效果.png)

2. 自定义错误页面

   + 可以在静态资源目录如static目录下，以及templates目录下设置error/目录，该目录下的HTTP状态码对应的4xx.html，5xx.html会在发生对应错误时会被自动调用【比较常用就是404和服务器内部异常500】

     [^注意]: 可以起名404.html，这样只有发生404错误会跳转该页面；也可以起名4xx.html，这样所有的以4开头的错误都会跳转4xx.html

     【实际设置实例】

     ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\错误页设置.png)

   + 一般后台管理系统自己就写好了一些错误页面

     【模板404页面】

     ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\404html.png)

     【模板500页面】

     ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\500html.png)

   + 自定义错误信息展示

     【前端页面代码】

     ```html
     <section class="error-wrapper text-center">
         <h1><img alt="" src="images/500-error.png"></h1>
         <h2>OOOPS!!!</h2>
         <!--message是Spring响应错误默认传递的错误信息，把他当做放在请求域中来处理，比较方便-->
         <h3 th:text="${message}">Something went wrong.</h3>
         <!--trace是错误相应的堆栈信息，都可以理解为直接从请求域中拿-->
         <p class="nrml-txt" th:text="${trace}">Why not try refreshing you page? Or you can <a href="#">contact our support</a> if the problem persists.</p>
         <a class="back-btn" href="index.html"> Back To Home</a>
     </section>
     ```

     【实际效果展示】

     ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\自定义错误展示.png)

### 4.8.2、异常自动配置原理

1. 异常处理自动配置原理

   + springframework-boot-autoconfigure-web下的servlet下的error包下专门是针对错误处理的类

     【web错误相关的错误自动配置类】

     ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\error相关包.png)

   + ErrorMvcAutoConfiguration【自动配置异常处理规则】

     【ErrorMvcAutoConfiguration源码】

     ```java
     // Load before the main WebMvcAutoConfiguration so that the error View is available
     @AutoConfiguration(before = WebMvcAutoConfiguration.class)
     @ConditionalOnWebApplication(type = Type.SERVLET)
     @ConditionalOnClass({ Servlet.class, DispatcherServlet.class })
     @EnableConfigurationProperties({ ServerProperties.class, WebMvcProperties.class })//异常自动类中绑定了ServerProperties和WebMvcProperties中的一些属性，对应属性配置文件的前缀分别为server和spring.mvc
     public class ErrorMvcAutoConfiguration {
     	...
     	@Bean
     	@ConditionalOnMissingBean(value = ErrorAttributes.class, search = SearchStrategy.CURRENT)//当容器中没有ErrorAttributes就会给容器注册组件DefaultErrorAttributes，有就使用自己的
     	public DefaultErrorAttributes errorAttributes() {
     		return new DefaultErrorAttributes();
     	}
     
     	@Bean
     	@ConditionalOnMissingBean(value = ErrorController.class, search = SearchStrategy.CURRENT)//这是配置了一个Controller，当容器中没有ErrorController组件是配置默认的BasicErrorController，id为basicErrorController
     	public BasicErrorController basicErrorController(ErrorAttributes errorAttributes,
     			ObjectProvider<ErrorViewResolver> errorViewResolvers) {
     		return new BasicErrorController(errorAttributes, this.serverProperties.getError(),
     				errorViewResolvers.orderedStream().collect(Collectors.toList()));
     	}
     
     	@Bean//给容器配置了一个错误页定制化器，暂时不管
     	public ErrorPageCustomizer errorPageCustomizer(DispatcherServletPath dispatcherServletPath) {
     		return new ErrorPageCustomizer(this.serverProperties, dispatcherServletPath);
     	}
     	...
     	@Configuration(proxyBeanMethods = false)
     	@EnableConfigurationProperties({ WebProperties.class, WebMvcProperties.class })
     	static class DefaultErrorViewResolverConfiguration {
     		...
     		@Bean//这里还配置了一个视图解析器，叫做错误视图解析器，id为conventionErrorViewResolver
     		@ConditionalOnBean(DispatcherServlet.class)
     		@ConditionalOnMissingBean(ErrorViewResolver.class)
     		DefaultErrorViewResolver conventionErrorViewResolver() {
     			return new DefaultErrorViewResolver(this.applicationContext, this.resources);
     		}
     
     	}
     
     	@Configuration(proxyBeanMethods = false)
     	@ConditionalOnProperty(prefix = "server.error.whitelabel", name = "enabled", matchIfMissing = true)
     	@Conditional(ErrorTemplateMissingCondition.class)
     	protected static class WhitelabelErrorViewConfiguration {
     
     		private final StaticView defaultErrorView = new StaticView();
     
     		@Bean(name = "error")//给容器中添加了一个视图组件，该视图组件的名字就叫error
     		@ConditionalOnMissingBean(name = "error")//注意如果自己自定义了一个名为error的视图，那么默认事务错误页就不会生效
     		public View defaultErrorView() {
     			return this.defaultErrorView;
     		}
     
     		// If the user adds @EnableWebMvc then the bean name view resolver from
     		// WebMvcAutoConfiguration disappears, so add it back in to avoid disappointment.
     		@Bean
     		@ConditionalOnMissingBean//以组件名作为视图名的视图解析器，猜想是有这个才能通过视图名去容器中找对应名字的视图组件，讲的不清楚
     		public BeanNameViewResolver beanNameViewResolver() {
     			BeanNameViewResolver resolver = new BeanNameViewResolver();
     			resolver.setOrder(Ordered.LOWEST_PRECEDENCE - 10);
     			return resolver;
     		}
     
     	}
     
     	
     	private static class ErrorTemplateMissingCondition extends SpringBootCondition {
     		...
     	}
     
     	//error视图组件的具体类型，白页html代码就写死在这里面
     	private static class StaticView implements View {
     		...
     	}
     
     	static class ErrorPageCustomizer implements ErrorPageRegistrar, Ordered {
     		...
     	}
     
     	static class PreserveErrorControllerTargetClassPostProcessor implements BeanFactoryPostProcessor {
     		...
     	}
     
     }
     
     ```

#### 01、自动配置的容器组件

> 根据自动配置原理可以得出不同错误页需求下对应自定义的组件
>
> + 扩展错误信息-->自定义DefaultErrorAttribute
> + 不使用默认白页使用自定义白页-->自定义BasicErrorController
> + 不想把错误页放在error文件夹下-->自定义BeanNameViewResolver

##### 1. DefaultErrorAttribute

+ DefaultErrorAttribute-->id：errorAttributes

  > DefaultErrorAttribute定义错误页面可以包含哪些属性，如果觉得默认的错误页面中的信息不够，就要自定义DefaultErrorAttribute

  ```java
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public class DefaultErrorAttributes implements ErrorAttributes, HandlerExceptionResolver, Ordered {//DefaultErrorAttributes中规定着服务器返回错误的哪些属性，在getErrorAttributes方法中存放着
  
  	private static final String ERROR_INTERNAL_ATTRIBUTE = DefaultErrorAttributes.class.getName() + ".ERROR";
  
  	@Override
  	public int getOrder() {
  		return Ordered.HIGHEST_PRECEDENCE;
  	}
  
  	@Override//这个方法能返回一个ModelAndView
  	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
  			Exception ex) {
  		storeErrorAttributes(request, ex);//保存错误属性，错误属性指
  		return null;
  	}
  
  	private void storeErrorAttributes(HttpServletRequest request, Exception ex) {
  		request.setAttribute(ERROR_INTERNAL_ATTRIBUTE, ex);
  	}
  
  	@Override//这里面规定着返回错误信息的具体项目
  	public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
  		Map<String, Object> errorAttributes = getErrorAttributes(webRequest, options.isIncluded(Include.STACK_TRACE));//这个getErrorAttributes可以获取时间戳、响应状态码、错误细节和路径等信息，这里面就是完整的信息了
  		if (!options.isIncluded(Include.EXCEPTION)) {//这里是判断options中不包含exception就从错误信息集合中把exception移除
  			errorAttributes.remove("exception");
  		}
  		if (!options.isIncluded(Include.STACK_TRACE)) {
  			errorAttributes.remove("trace");
  		}
  		if (!options.isIncluded(Include.MESSAGE) && errorAttributes.get("message") != null) {
  			errorAttributes.remove("message");
  		}
  		if (!options.isIncluded(Include.BINDING_ERRORS)) {
  			errorAttributes.remove("errors");
  		}
  		return errorAttributes;
  	}
  
      //这个就是上面getErrorAttributes方法中调用的getErrorAttributes重载方法
  	private Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
          //这些方法都写在这个类中的
  		Map<String, Object> errorAttributes = new LinkedHashMap<>();
  		errorAttributes.put("timestamp", new Date());//时间戳
  		addStatus(errorAttributes, webRequest);//状态码
  		addErrorDetails(errorAttributes, webRequest, includeStackTrace);//错误细节
  		addPath(errorAttributes, webRequest);//错误对应的请求路径
  		return errorAttributes;
  	}
  
      //添加状态码的方法
  	private void addStatus(Map<String, Object> errorAttributes, RequestAttributes requestAttributes) {
  		...
  	}
  
      //添加错误细节的方法
  	private void addErrorDetails(Map<String, Object> errorAttributes, WebRequest webRequest,boolean includeStackTrace) {
  		...
  	}
  	...
  }
  ```

##### 2. BasicErrorController

+ BasicErrorController-->id：basicErrorController

  > 如果不想跳转的白页不想是默认的错误页，而是自定义的白页或者json，就需要自定义BasicErrorController

  + 控制器对应的请求映射${server.error.path:${error.path:/error}}是一个动态取的值，如果配置了server.error.path，路径就是动态取的值；如果没有配置，就取出error.path的配置值；如果error.path也没配置，就使用默认值/error作为请求映射路径

    [^注意1]: 即没有在属性配置文件配置server.error.path或者error.path的前提下默认处理/error路径的请求

  + 由控制器方法可知，spring既可以响应错误页也可以响应json，错误页是去容器中找名为error组件，由error组件的条件配置可以看出自定义了同名error错误页，SpringBoot就不会配置默认的error错误页了，即平常看到的白页

  ```java
  @Controller
  @RequestMapping("${server.error.path:${error.path:/error}}")
  public class BasicErrorController extends AbstractErrorController {
      private final ErrorProperties errorProperties;
  	...
      //以下两个请求映射默认都匹配/error
  	@RequestMapping(produces = MediaType.TEXT_HTML_VALUE)//这就是响应html的,produces = MediaType.TEXT_HTML_VALUE表示匹需要产生HTML页面的，即对应浏览器，至于produces属性没有讲
  	public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
  		HttpStatus status = getStatus(request);//得到响应的状态码，这里数学运算错误是500，这是从请求域中的异常中拿到的
  		Map<String, Object> model = Collections
  			.unmodifiableMap(getErrorAttributes(request, getErrorAttributeOptions(request, MediaType.TEXT_HTML)));//这个model里面的信息已经是完整异常信息
  		response.setStatus(status.value());
  		ModelAndView modelAndView = resolveErrorView(request, response, status, model);//调用解析错误视图方法resolveErrorView获取ModelAndView
  		return (modelAndView != null) ? modelAndView : new ModelAndView("error", model);//响应Html会响应一个ModelAndView，其中的ViewName属性为error页面
  	}
  
  	@RequestMapping//ResponseEntity<Map<String, Object>>表示匹配需要产生json数据的，即对应客户端
  	public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
  		HttpStatus status = getStatus(request);
  		if (status == HttpStatus.NO_CONTENT) {
  			return new ResponseEntity<>(status);
  		}
  		Map<String, Object> body = getErrorAttributes(request, getErrorAttributeOptions(request, MediaType.ALL));
  		return new ResponseEntity<>(body, status);//响应json通过ResponseEntity<>把map里面的数据都响应出去
  	}
  	...
  }
  ```

  【控制器方法中model异常信息总览】

  + 自上而下依次是：时间戳、状态码、错误信息、堆栈信息、错误原因（除0错误）、错误路径

  ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\控制器方法中model异常信息总览.png)

##### 3. StaticView

+ View-->id：error

  + 上一个控制器组件响应浏览器是error页面，响应的就是这个error视图，因为之前涉及过，先根据名字去容器中找对应视图组件，找不到才调用Thymeleaf视图解析器去找资源最后响应字符串

  + 这个StaticView就配置在ErrorMvcAutoConfiguration中

    ```java
    private static class StaticView implements View {
    
        private static final MediaType TEXT_HTML_UTF8 = new MediaType("text", "html", StandardCharsets.UTF_8);//响应一个html
    
        private static final Log logger = LogFactory.getLog(StaticView.class);
    
        //render方法中拼接的白页信息
        @Override
        public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response)
                throws Exception {
            if (response.isCommitted()) {
                String message = getMessage(model);
                logger.error(message);
                return;
            }
            response.setContentType(TEXT_HTML_UTF8.toString());
            StringBuilder builder = new StringBuilder();
            Object timestamp = model.get("timestamp");
            Object message = model.get("message");
            Object trace = model.get("trace");
            if (response.getContentType() == null) {
                response.setContentType(getContentType());
            }
            //在这里直接拼接出默认的错误白页对应的html页面
            builder.append("<html><body><h1>Whitelabel Error Page</h1>")
                .append("<p>This application has no explicit mapping for /error, so you are seeing this as a fallback.</p>")
                .append("<div id='created'>")
                .append(timestamp)
                .append("</div>")
                .append("<div>There was an unexpected error (type=")
                .append(htmlEscape(model.get("error")))
                .append(", status=")
                .append(htmlEscape(model.get("status")))
                .append(").</div>");
            if (message != null) {
                builder.append("<div>").append(htmlEscape(message)).append("</div>");
            }
            if (trace != null) {
                builder.append("<div style='white-space:pre-wrap;'>").append(htmlEscape(trace)).append("</div>");
            }
            builder.append("</body></html>");
            response.getWriter().append(builder.toString());
        }
    	...其他方法
    }
    ```

##### 4. BeanNameViewResolver

+ BeanNameViewResolver-->id：beanNameViewResolver

  > 如果不想把错误页放在error文件夹下就要自定义BeanNameViewResolver

  + 这是一个视图解析器，作用是按照返回的视图名作为组件的id去容器中找View对象

  > 整个的逻辑是，/error请求匹配basicErrorController，如果是浏览器请求控制器方法返回一个ModelAndView，其中的ViewName是error，然后利用视图解析器beanNameViewResolver去容器中找到名为error的视图组件View对象，这么找的没说，以及有了error/4xx.html就不去找error视图的原理也没说

##### 5. DefaultErrorViewResolver

+ DefaultErrorViewResolver-->id：conventionErrorViewResolver

  > 这里面的resolve方法解释了为什么SpringMVC可以根据状态码自动找到error目录下对应的以状态码命名的html页面，即发生错误，会通过DefaultErrorViewResolver以error/HTTP状态码作为视图ViewName去寻找前端页面

  ```java
  public class DefaultErrorViewResolver implements ErrorViewResolver, Ordered {
  
  	private static final Map<Series, String> SERIES_VIEWS;
  
  	static {//给Map中放入一些信息，客户端错误是4xx,服务端错误是5xx
  		Map<Series, String> views = new EnumMap<>(Series.class);
  		views.put(Series.CLIENT_ERROR, "4xx");
  		views.put(Series.SERVER_ERROR, "5xx");
  		SERIES_VIEWS = Collections.unmodifiableMap(views);
  	}
  
  	...其他代码
  	@Override//解析得到视图对象
  	public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status, Map<String, Object> model) {
  		ModelAndView modelAndView = resolve(String.valueOf(status.value()), model);//String.valueOf(status.value())是拿到异常的状态码的精确值，调用resolve方法来解析异常生成ModelAndView，对就是下面那两个方法，woc串起来了，所以白页怎么回事
  		if (modelAndView == null && SERIES_VIEWS.containsKey(status.series())) {//如果精确值找不到对应的视图且SERIES_VIEWS中包含状态码的系列
  			modelAndView = resolve(SERIES_VIEWS.get(status.series()), model);//调用resolve方法得到modelAndView，其中就有要跳转的页面,这里面传参给viewName的SERIES_VIEWS.get(status.series())是HTTP状态码,注意：在SERIES_VIEWS.get(status.series())中貌似就解决了500到5xx的过程，这里的过程不清楚，弹幕说的；已讲：status.series()获取的是HtppStatus的series属性，是Series枚举类型，值有1,2,3,4,5；就是对应的4xx,5xx;即有精确的页面就找精确页面，没有就找模糊的页面
  		}
  		return modelAndView;
  	}
  
      //这个就是resolveErrorView方法中调用的resolve方法
  	private ModelAndView resolve(String viewName, Map<String, Object> model) {
  		String errorViewName = "error/" + viewName;//拼接成error/HTTP状态码，从这里就能看出，SpringMVC底层支持error目录下直接用状态码给对应对应的html页面取名，因为视图名就是error/HTTP状态码，此例中是error/500
  		TemplateAvailabilityProvider provider = this.templateAvailabilityProviders.getProvider(errorViewName,
  				this.applicationContext);//通过模板引擎判断有没有这个error/500页面，有provider就是null
  		if (provider != null) {
  			return new ModelAndView(errorViewName, model);//如果没有就创建ModelAndView以error/500作为视图名并返回，以错误信息封装的model作为model放到请求域中
  		}
  		return resolveResource(errorViewName, model);//如果有就调用resolveResource方法获取ModelAndView
  	}
      
      //这个就是resolveResource方法
      private ModelAndView resolveResource(String viewName, Map<String, Object> model) {
  		for (String location : this.resources.getStaticLocations()) {
  			try {
  				Resource resource = this.applicationContext.getResource(location);
  				resource = resource.createRelative(viewName + ".html");//视图名称会带上html
  				if (resource.exists()) {//这儿是判断对应资源存在的
  					return new ModelAndView(new HtmlResourceView(resource), model);//最终返回带error/500.html页面创建的ModelAndView
  				}
  			}
  			catch (Exception ex) {
  			}
  		}
  		return null;
  	}
  	...其他代码
  }
  ```

  

### 4.8.3、异常处理步骤流程

> 注意：如果控制器方法中的参数无法通过请求参数赋值会报400错误，会提示需要的参数不存在

1. 完整流程

   + 第一步：执行目标方法handle

     + 目标方法执行期间有任何异常都会被catch到并在RequestMappingHandlerAdapter的invokeHandlerMethod方法的finally语句块中使用webRequest.requestComplete方法将AbstractRequestAttributes属性设置为false，标志当前请求结束，

     + 并且使用dispatchException进行封装所有的异常

       [^注意]: 控制器方法发生异常无法返回ModelAndView

   + 第二步：执行视图解析流程【页面渲染】processDispatchResult

     + 在该方法中调用processHandlerException处理控制器方法执行过程中发生的异常，并返回ModelAndView赋值给原本执行完handle方法返回的mv

       + 遍历所有的handlerExceptionResolver，看谁能处理当前异常，所有的处理器异常解析器都保存在handlerExceptionResolvers这个List集合中

         > 所有的处理器异常解析器都实现了接口HandleExceptionResolver，这个接口中只有一个resolveException方法，拿到HttpServletRequest、HttpServletResponse对象、控制器方法处理器、以及控制器方法发生的异常对象；自定义处理器异常解析器也要返回ModelAndView，要决定跳转到那个页面，页面中要放哪些数据

         【HandleExceptionResolver接口】

         ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\HandlerExceptionResolver.png)

         + 系统默认的处理器异常解析器包含：

           ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\处理器异常解析器.png)

           + DefaultErrorAttributes

             [^注意]: 这个就是之前异常自动配置给容器中自动配置的defaultErrorAttributes组件，里面规定和放置了需要放入的异常信息，可以看出DefaultErrorAttributes实现了接口HandlerExceptionResolver，专门用于处理控制器方法执行中发生的异常

           + HandlerExceptionResolverComposite【注意这是一个处理器异常解析器的组合，里面还有三个处理器异常解析器】

             + ExceptionHandlerExceptionResolver
             + ResponseStatusExceptionResolver
             + DefaultHandlerExceptionResolver

         + 首先遍历第一个处理器异常解析器DefaultErrorAttributes

           > 调用该解析器的resolveException方法作用是把异常信息放入请求域，这个解析器不会返回ModelAndView，只会返回null，必然会继续遍历后续处理器异常解析器

         + 再遍历其他三个处理器异常解析器

           > 结果很尴尬，三个里面有两个都是处理特定注解的异常的，剩下一个没讲干啥的;总之没有一个能处理，直接把异常抛出到doDispatch方法，一抛出去有被catch捕获到

     + 第四步：第二步无法解析的异常继续手动抛出，被捕获后执行triggerAfterCompletion方法

       > 这个方法就是拦截器的倒序最后一步，对异常处理没啥意义，这个执行完，当前请求就执行完了，也就是当前请求压根没有执行异常的处理逻辑，但是下一次请求进来请求路径直接变成了/error;
       >
       > 即当前请求发生异常，但是没有人能够处理，那么SpringMVC又会liji 再发一次请求，请求的路径的URI直接变成/error，即没人能够处理的异常会把异常信息放在请求域转发到/error【原因是异常没处理最终会交给tomcat处理，Tomcat底层支持异常请求转发，springboot把转发路径默认设置成了/error】

     + 第五步：没人处理的错误会带着异常或者错误信息转发到/error，会被BasicErrorController即SpringMVC中专门处理/error请求的控制器匹配直接再次通过handle方法去执行对应/error的控制器方法，从请求域中拿到错误信息封装到model中，然后调用resolveErrorView方法遍历错误视图解析器集合errorViewResolvers里面默认的由异常机制自动配置的DefaultErrorViewResolver的resolveErrorView方法把响应状态码作为错误页的地址拼接成error/HTTP状态码.html来获取ModelAndView，即模板引擎最终响应error/5xx.html

       [^注意]: 由此看出可以自定义errorViewResolvers自己指定异常处理规则

       ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\控制器方法中model异常信息总览.png)

     ```java
     protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
         ...
                 //第一步：反射调用处理器方法
                 // Actually invoke the handler.通过处理器适配器真正执行handler目标方法，传入请求、响应对象，以及匹配的handler；在handle()方法中调用了handleInternal()方法
                 mv = ha.handle(processedRequest, response, mappedHandler.getHandler());
                 ...
             }
     		//注意：这两中被捕获的异常，即所有的异常都会被封装到dispatchException中
             catch (Exception ex) {//Exception可以捕获
                 dispatchException = ex;
             }
             catch (Throwable err) {//最大类型的Throwable也可以捕获
                 dispatchException = new NestedServletException("Handler dispatch failed", err);
             }
             //第二步：处理派发最终的结果，这一步执行前，整个页面还没有跳转过去；注意没有发生异常，这个派发最终结果的方法会执行，发生了异常，这个派发最终结果的方法还是会执行；这里面的mv因为handle方法没有正确执行，这个mv当前是空的，已确定；此时此例中的dispatchException是数学运算异常
             processDispatchResult(processedRequest, response, mappedHandler, mv, dispatchException);
         }
         catch (Exception ex) {//异常上面三个处理器异常解析器搞不定数学运算异常就会手动抛出来，被这儿或者下面捕捉到
             
             //第四步
             triggerAfterCompletion(processedRequest, response, mappedHandler, ex);//抛出的异常被捕捉到触发triggerAfterCompletion方法
         }
         catch (Throwable err) {
             ...
         }
     }
     ```
     
     【doDispatch方法中的processDispatchResult方法】
     
     ```java
     @SuppressWarnings("serial")
     public class DispatcherServlet extends FrameworkServlet {
     	private void processDispatchResult(HttpServletRequest request, HttpServletResponse response,@Nullable HandlerExecutionChain mappedHandler, @Nullable ModelAndView mv,@Nullable Exception exception) throws Exception {//这最后一个参数exceptionption接收的就是dispatchException，这里是数学运算异常
     
     		boolean errorView = false;//先搞一个errorView为false
     
     		if (exception != null) {//异常不为空就会进入这个流程
     			if (exception instanceof ModelAndViewDefiningException) {//判断异常是否ModelAndView定义异常
     				logger.debug("ModelAndViewDefiningException encountered", exception);
     				mv = ((ModelAndViewDefiningException) exception).getModelAndView();
     			}
     			else {
     				Object handler = (mappedHandler != null ? mappedHandler.getHandler() : null);//不是ModelAndView定义异常就会跳转到这里，拿到原生的handler
                     //第三步：调用processHandlerException方法处理控制器发生的异常并返回ModelAndView，解析不了也不返回了，直接抛异常终止后续代码执行
     				mv = processHandlerException(request, response, handler, exception);//调用处理控制器异常processHandlerException方法来处理控制器方法发生的异常，并把处理的结果保存成ModelAndView，这个ModelAndView就是传参进来的mv，也就是原来用来接收handle方法返回的ModelAndView
     				errorView = (mv != null);
     			}
     		}
     
     		...
     	}
     }
     ```
     
     【processDispatchResult方法中的processHandlerException方法】
     
     ```java
     @SuppressWarnings("serial")
     public class DispatcherServlet extends FrameworkServlet {
     	@Nullable
     	protected ModelAndView processHandlerException(HttpServletRequest request, HttpServletResponse response,@Nullable Object handler, Exception ex) throws Exception {
     
     		// Success and error responses may use different content types
     		request.removeAttribute(HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE);//移除request中的一些属性，暂时不管
     
     		// Check registered HandlerExceptionResolvers...
     		ModelAndView exMv = null;//准备了一个ModelAndView，该方法结束后会返回该ModelAndView
     		if (this.handlerExceptionResolvers != null) {//如果处理器异常解析器不为空，示例中只有两个，分别是DefaultErrorAttributes和HandlerExceptionResolverComposite
     			for (HandlerExceptionResolver resolver : this.handlerExceptionResolvers) {//挨个遍历异常解析器
                     //异常解析器挨个对异常进行处理，处理得到的结果赋值给ModelAndView
     				exMv = resolver.resolveException(request, response, handler, ex);
     				if (exMv != null) {//如果不为空就跳出循环
     					break;//除了第一个处理器异常解析器把异常放到请求域中，没有一个解析器能解析该异常
     				}
     			}
     		}
     		if (exMv != null) {//这里没执行，因为没有一个处理器异常解析器能解析该异常的
     			if (exMv.isEmpty()) {
     				request.setAttribute(EXCEPTION_ATTRIBUTE, ex);
     				return null;
     			}
     			// We might still need view name translation for a plain error model...
     			if (!exMv.hasView()) {
     				String defaultViewName = getDefaultViewName(request);
     				if (defaultViewName != null) {
     					exMv.setViewName(defaultViewName);
     				}
     			}
     			if (logger.isTraceEnabled()) {
     				logger.trace("Using resolved error view: " + exMv, ex);
     			}
     			else if (logger.isDebugEnabled()) {
     				logger.debug("Using resolved error view: " + exMv);
     			}
     			WebUtils.exposeErrorRequestAttributes(request, ex, getServletName());
     			return exMv;//如果不为空进行一些日志操作并判断ModelAndView中是否是有视图，都没问题就返回该ModelAndView
     		}
     		throw ex;//处理期间有任何异常都会抛出去*****？这里是不是讲的有问题？是如果处理不了该异常就会把这个异常抛出去，就是处理不了，所有的异常解析器都处理不了，这个异常直接被抛出去了
     	}
     }
     ```
     
     ------
     
     ------
     
     【第三步的所有异常解析器的解析过程】
     
     【以下均为processHandlerException方法中遍历处理器异常解析器的resolveException方法】
     
     > DefaultErrorAttributes即第一个异常解析器先来处理异常，把异常信息保存到request域中，并且返回null，这个异常信息貌似就是最初的doDispatch方法中那个直接放在请求域中，返回null意味着还会继续遍历处理器异常解析器，这里也说明异常解析器必须解析出结果，不解析出结果不算完事
     
     【DefaultErrorAttributes中的resolveException方法】
     
     ```java
     @Order(Ordered.HIGHEST_PRECEDENCE)
     public class DefaultErrorAttributes implements ErrorAttributes, HandlerExceptionResolver, Ordered {
     	@Override
     	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,Exception ex) {
     		storeErrorAttributes(request, ex);//保存错误的属性信息
     		return null;
     	}
         private void storeErrorAttributes(HttpServletRequest request, Exception ex) {
     		request.setAttribute(ERROR_INTERNAL_ATTRIBUTE, ex);//给请求域中放入ex，这个ex其实就是最初的dispatchException，key是一个很长的字符串就是DefaultErrorAttributes的全限定类名.ERROR
     	}
     }
     ```
     
     > 执行处理器异常解析器的组合的resolveException方法会跳去执行HandlerExceptionResolverComposite中的resolveException方法，在该方法中对三个处理器异常解析器进行处理
     
     ------
     
     【HandlerExceptionResolverComposite中的resolveException方法】
     
     ```java
     public class HandlerExceptionResolverComposite implements HandlerExceptionResolver, Ordered {
     	@Override
     	@Nullable
     	public ModelAndView resolveException(
     			HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex) {
     
     		if (this.resolvers != null) {
     			for (HandlerExceptionResolver handlerExceptionResolver : this.resolvers) {
     				ModelAndView mav = handlerExceptionResolver.resolveException(request, response, handler, ex);
     				if (mav != null) {
     					return mav;
     				}
     			}
     		}
     		return null;
     	}
     }
     ```
     
     ------
     
     【ExceptionHandlerExceptionResolver中的resolveException方法】
     
     > 只要控制器方法上没有标注ExceptionHandler注解就会返回null，即ExceptionHandlerExceptionResolver异常解析器只能解析控制器方法上标注了ExceptionHandler注解的异常
     
     ```java
     public abstract class AbstractHandlerExceptionResolver implements HandlerExceptionResolver, Ordered {
     	@Override
     	@Nullable
     	public ModelAndView resolveException(
     			HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex) {
     
     		if (shouldApplyTo(request, handler)) {//先判断能不能解析该异常，不能解析直接返回null，判断原理是拿到handler，传入父类的shouldApplyTo方法判断异常映射里面有没有，有啥？雷神说后面再说，无语；说这里要结合@ExceptionHandler注解来分析？这里的handler不是发生异常的Handler吗，为什么雷神说要找到标注了@ExceptionHandler的控制器方法，执行找@ExceptionHandler的控制器方法的代码在哪里
     			prepareResponse(ex, response);
     			ModelAndView result = doResolveException(request, response, handler, ex);//这一步是解析异常，
     			if (result != null) {
     				// Print debug message when warn logger is not enabled.
     				if (logger.isDebugEnabled() && (this.warnLogger == null || !this.warnLogger.isWarnEnabled())) {
     					logger.debug(buildLogMessage(ex, request) + (result.isEmpty() ? "" : " to " + result));
     				}
     				// Explicitly configured warn logger in logException method.
     				logException(ex, request);
     			}
     			return result;
     		}
     		else {
     			return null;
     		}
     	}
     }
     ```
     
     【resolveException方法中的doResolveException方法】
     
     ```java
     public abstract class AbstractHandlerMethodExceptionResolver extends AbstractHandlerExceptionResolver {
     	@Override
     	@Nullable
     	protected final ModelAndView doResolveException(
     			HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex) {
     
     		HandlerMethod handlerMethod = (handler instanceof HandlerMethod ? (HandlerMethod) handler : null);//拿到目标方法
     		return doResolveHandlerMethodException(request, response, handlerMethod, ex);//调用doResolveHandlerMethodException方法进行解析
     	}
     }
     ```
     
     【doResolveException方法的doResolveHandlerMethodException方法】
     
     ```java
     public class ExceptionHandlerExceptionResolver extends AbstractHandlerMethodExceptionResolver
     		implements ApplicationContextAware, InitializingBean {
     	@Override
     	@Nullable
     	protected ModelAndView doResolveHandlerMethodException(HttpServletRequest request,HttpServletResponse response, @Nullable HandlerMethod handlerMethod, Exception exception) {
     
     		ServletInvocableHandlerMethod exceptionHandlerMethod = getExceptionHandlerMethod(handlerMethod, exception);//先来看目标方法有没有ExceptionHandler注解，这个后面再说，
     		if (exceptionHandlerMethod == null) {
     			return null;//只要没有使用相应注解，doResolveHandlerMethodException就会返回null,后面都不执行
     		}
     		...
         }
     }
     ```
     
     ------
     
     【ResponseStatusExceptionResolver的resolveException方法，直接继承的父类的】
     
     > 这个异常解析器的作用是如果控制器方法上标注了@ResponseStatus注解，出现错误以后直接给一个响应状态码，这个也不能解析，但是具体原理没讲，包括能解析怎么创建ModelAndView，这里肯定是没有这个注解直接判断返回null了；对自定义异常标注了@ResponseStatus注解的同时会给对应异常一个状态码
     
     ```java
     public abstract class AbstractHandlerExceptionResolver implements HandlerExceptionResolver, Ordered {
     	@Override
     	@Nullable
     	public ModelAndView resolveException(
     			HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex) {
     
     		if (shouldApplyTo(request, handler)) {
     			prepareResponse(ex, response);
     			ModelAndView result = doResolveException(request, response, handler, ex);//调用doResolveException方法执行解析流程
     			if (result != null) {
     				// Print debug message when warn logger is not enabled.
     				if (logger.isDebugEnabled() && (this.warnLogger == null || !this.warnLogger.isWarnEnabled())) {
     					logger.debug(buildLogMessage(ex, request) + (result.isEmpty() ? "" : " to " + result));
     				}
     				// Explicitly configured warn logger in logException method.
     				logException(ex, request);
     			}
     			return result;
     		}
     		else {
     			return null;
     		}
     	}
     }
     ```
     
     【resolveException方法中的doResolveException方法】
     
     ```java
     public class ResponseStatusExceptionResolver extends AbstractHandlerExceptionResolver implements MessageSourceAware {
     	@Override
     	@Nullable
     	protected ModelAndView doResolveException(
     			HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex) {
     
     		try {
     			if (ex instanceof ResponseStatusException) {//判断异常是不是ResponseStatusException异常，这是具体的异常类，即自定义异常可以使用继承ResponseStatusException的方式，这里标注了ResponseStatus注解的异常并不是ResponseStatusException异常
     				return resolveResponseStatusException((ResponseStatusException) ex, request, response, handler);
     			}
     
     			ResponseStatus status = AnnotatedElementUtils.findMergedAnnotation(ex.getClass(), ResponseStatus.class);//判断异常上是否标注了ResponseStatus注解，有就返回status，即status不为null
     			if (status != null) {
     				return resolveResponseStatus(status, request, response, handler, ex);//把注解的信息解析出来返回一个ModelAndView,调用的resolveResponseStatus方法会拿到注解的错误状态码和错误原因，利用statusCode，reason,response创建ModelAndView进行返回，resolveResponseStatus方法还会直接调用response.sendError直接跳去对应状态码的错误页；执行response.sendError相当于直接给Tomcat发送/error请求【和没人能处理的/error是一样的】，当前请求立即结束，里面后续创建的ModelAndView实际上创建了对象，但是View和model全是null，作用是结束循环让这个请求结束
     			}
     
     			if (ex.getCause() instanceof Exception) {
     				return doResolveException(request, response, handler, (Exception) ex.getCause());
     			}
     		}
     		catch (Exception resolveEx) {
     			if (logger.isWarnEnabled()) {
     				logger.warn("Failure while trying to resolve exception [" + ex.getClass().getName() + "]", resolveEx);
     			}
     		}
     		return null;
     	}
     }
     ```
     
     
     
     ------
     
     【DefaultHandlerExceptionResolver的resolveException方法】
     
     > woc只说了这个也不能解析，多一嘴都没提，后来补上了
     >
     > + 这个控制器异常解析器是专门来处理SpringMVC自己的底层异常的
     
     ```java
     public abstract class AbstractHandlerExceptionResolver implements HandlerExceptionResolver, Ordered {//这是从父类直接继承过来的，没啥好说的，几个处理器异常解析器都是这个方法
     	@Override
     	@Nullable
     	public ModelAndView resolveException(
     			HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex) {
     
     		if (shouldApplyTo(request, handler)) {
     			prepareResponse(ex, response);
     			ModelAndView result = doResolveException(request, response, handler, ex);//进来就直奔各个子类的doResolveException方法
     			if (result != null) {
     				// Print debug message when warn logger is not enabled.
     				if (logger.isDebugEnabled() && (this.warnLogger == null || !this.warnLogger.isWarnEnabled())) {
     					logger.debug(buildLogMessage(ex, request) + (result.isEmpty() ? "" : " to " + result));
     				}
     				// Explicitly configured warn logger in logException method.
     				logException(ex, request);
     			}
     			return result;
     		}
     		else {
     			return null;
     		}
     	}
     }
     ```
     
     【resolveException方法中的doResolveException方法】
     
     ```java
     public class DefaultHandlerExceptionResolver extends AbstractHandlerExceptionResolver {//这个处理器就是专门处理SpringMVC底层的异常
     	@Override
     	@Nullable
     	protected ModelAndView doResolveException(
     			HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex) {
     
     		try {
     			if (ex instanceof HttpRequestMethodNotSupportedException) {//判断是否请求方式不支持的异常
     				return handleHttpRequestMethodNotSupported(
     						(HttpRequestMethodNotSupportedException) ex, request, response, handler);
     			}
     			else if (ex instanceof HttpMediaTypeNotSupportedException) {//是不是媒体类型不支持的异常
     				return handleHttpMediaTypeNotSupported(
     						(HttpMediaTypeNotSupportedException) ex, request, response, handler);
     			}
     			else if (ex instanceof HttpMediaTypeNotAcceptableException) {
     				return handleHttpMediaTypeNotAcceptable(
     						(HttpMediaTypeNotAcceptableException) ex, request, response, handler);
     			}
     			else if (ex instanceof MissingPathVariableException) {
     				return handleMissingPathVariable(
     						(MissingPathVariableException) ex, request, response, handler);
     			}
     			else if (ex instanceof MissingServletRequestParameterException) {//缺少形参对应的请求参数异常
     				return handleMissingServletRequestParameter(
     						(MissingServletRequestParameterException) ex, request, response, handler);
     			}
     	...
     }
     ```
     
     【doResolveException方法中的handleMissingServletRequestParameter方法】
     
     ```java
     public class DefaultHandlerExceptionResolver extends AbstractHandlerExceptionResolver {
     protected ModelAndView handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
     			HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) throws IOException {
     
     		response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());//还是一样的逻辑，response.sendError就是tomcat发送/error请求给SpringMVC，SpringMVC再次调用组件BasicErrorController使用DefaultErrorViewReslover进行处理，处理不了响应SpringMVC默认的白页，正常情况下没人能够处理这个/error请求，tomcat会响应自己的丑陋错误页
     		return new ModelAndView();
     	}
     }
     ```
     
     【tomcat的丑陋错误页】
     
     ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\tomcat原生错误页.png)
     
     ------
     
     ------
     
     > 好家伙，除了第一个处理器异常解析器把异常放到请求域中，没有一个解析器能解析该异常
     
     【第四步】
     
     【doDispatch方法中的triggerAfterCompletion方法】
     
     > 这特么就是拦截器最后一步所有已经执行了的拦截器执行AfterCompletion方法，对异常处理没啥意义
     
     ```java
     @SuppressWarnings("serial")
     public class DispatcherServlet extends FrameworkServlet {
     	private void triggerAfterCompletion(HttpServletRequest request, HttpServletResponse response,
     			@Nullable HandlerExecutionChain mappedHandler, Exception ex) throws Exception {
     
     		if (mappedHandler != null) {
     			mappedHandler.triggerAfterCompletion(request, response, ex);
     		}
     		throw ex;
     	}
     }
     ```
     
     ------
     
     【第五步】
     
     【BasicErrorController中的errorHtml方法中的resolveErrorView方法】
     
     ```java
     public abstract class AbstractErrorController implements ErrorController {
     	protected ModelAndView resolveErrorView(HttpServletRequest request, HttpServletResponse response, HttpStatus status,Map<String, Object> model) {
     		for (ErrorViewResolver resolver : this.errorViewResolvers) {//遍历所有的ErrorViewResolver
     			ModelAndView modelAndView = resolver.resolveErrorView(request, status, model);//还是看谁能产生ModelAndView，这里面只有一个DefaultErrorViewResolver，具体的处理过程看组件那部分
     			if (modelAndView != null) {
     				return modelAndView;
     			}
     		}
     		return null;
     	}
     }
     ```
     
     【errorViewResolvers中的错误视图解析器】
     
     > DefaultErrorViewResolver这玩意就是自动配置类给底层配置的
     
     ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\错误视图解析器.png)

### 4.8.4、定制错误处理逻辑

1. 自定义错误页

   + error/404.html、error/5xx.html、有精确错误状态码页面就精确匹配，没有就找4xx.html，如果都没有就触发白页

2. @ControllerAdvice+@ExceptionHandler处理全局异常

   【异常处理器的代码实例】

   ```java
   /**
    * @描述 处理整个web controller的异常
    */
   @Slf4j
   @ControllerAdvice//注解@ControllerAdvice也是一个组件，该注解上标注了@Component注解
   public class GlobalExceptionHandler {
       //标注了@ExceptionHandler表示当前是一个异常处理器,可以指定该处理器处理哪些异常
       @ExceptionHandler({ArithmeticException.class,NullPointerException.class})
       public String handleArithException(Exception e){//注意异常处理器中Exception类型的参数会自动封装对应异常的异常信息
           //使用日志打印记录异常
           log.error("异常是:{}",e);
           return "login";//这里和普通控制器是一样的，既可以返回视图名称，也可以返回ModelAndView
       }
   }
   ```

   [^注意]: 使用ExceptionHandler指定了处理对应异常的异常处理器后，直接就能在遍历异常处理器环节直接进入ExceptionHandlerExceptionResolver的resolveException方法进行处理，相当于就是自动根据异常类型找到对应的异常处理器然后视图和请求域数据完全由自己控制

3. @ResponseStatus+自定义异常

   > @ResponseStatus标注在异常类上，可以用value属性执行异常对应状态码，用reason属性指定异常原因
   >
   > 底层发生当次异常请求后在遍历控制器异常解析器的时候会直接调用ResponseStatusExceptionResolver 的resolveException方法，把responseStatus注解的信息解析出来直接调用response.sendError（statusCode,resolvedReason）通知tomcat拿着错误状态码和错误信息直接发送/error请求，spring接收/error请求直接像无法处理的请求再次发送/error一样进行处理最终匹配到4xx，5xx，没有就匹配白页,response.sendError方法返回一个view和model均为空的ModelAndView，结束对处理器异常解析器的循环结束当前请求

   【自定义@ResponseStatus标注异常实例】

   ```java
   @ResponseStatus(value = HttpStatus.FORBIDDEN,reason = "用户数量太多!!!")//HttpStatus是枚举，里面有超多值，分别对应不同的状态码，点进去仅能看见，FORBIDDEN是403
   public class UserTooManyException extends RuntimeException{
       public UserTooManyException() {
       }
   
       public UserTooManyException(String msg) {
           super(msg);
       }
   }
   ```

4. Spring底层的异常，如参数类型转换异常

   + 控制器方法需要的形参请求参数不存在框架会自动抛异常，然后进入异常处理流程，遍历所有处理器异常解析器，第一个DefaultErrorAttributes把异常放在请求域继续执行，接下来遍历其他三个异常解析器，前两个一个是处理加了ExceptionHandler注解的，一个是处理异常加了ResponseStatus注解的，两个都不行
   + 来到第三个异常解析器DefaultHandlerExceptionResolver，这个异常就是专门处理SpringMVC框架底层的异常，这里以MissingServletRequestParameterException异常讲解SpringMVC底层对框架自身异常的处理逻辑
   + 实际上就是再调用response.sendError方法让tomcat发送/error请求被BasicErrorController匹配到然后调用DefaultErrorViewResolver来处理解析错误视图，解析不了响应SpringMVC的白页，没有SpringMVC会响应tomcat自己的丑陋白页

5. 所有的异常解析器都实现了HandlerExceptionResolver

   【接口HandlerExceptionResolver】

   ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\HandlerExceptionResolver.png)

   > 我们可以通过实现这个接口自定义控制器异常解析器，自定义异常解析器实现该接口然后使用@Component注解将其放入IoC容器中
   >
   > + 要点1：自定义异常解析器会直接放在处理器异常解析器组合后面，即处理器异常解析器集合由原来的两个变成三个，由于自定义异常解析器被放在了最后，很多异常压根轮不到自定义异常解析器进行处理，此时给自定义异常解析器使用order注解通过设定value属性值为Ordered接口的HIGHEST_PRECEDENCE属性可以设定自定义异常解析器的优先级为最高优先级（数字越小优先级越高）
   >
   >   【修改了优先级后的处理器异常解析器集合】
   >
   >   ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\修改优先级后的自定义处理器异常解析器.png)
   >
   > + 自定义处理器异常解析器可以自定义返回ModelAndView，设置跳转视图和请求域数据，不设置也要创建ModelAndView，目的是为了让其他处理器异常解析器不再进行遍历，注意如果自定义异常解析器不进行筛选拦截所有异常，SpringMVC所带的所有异常处理机制都会失效
   >
   > + 所以只要把自定义处理器异常解析器的优先级调高，就会作为默认的全局异常处理规则，很灵活，可以拦截所有异常按自己的逻辑进行处理，也可以拦部分异常，放行其他异常给Spring来处理

   【处理器异常解析器集合】

   ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\自定义处理器异常解析器.png)

   【自定义处理器异常解析器实例】

   ```java
   @Order(Ordered.HIGHEST_PRECEDENCE)//优先级，数字越小，优先级越高
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
           return new ModelAndView();//可以自定义返回ModelAndView，设置跳转视图和请求域数据，不设置也要创建ModelAndView，目的是为了让其他处理器异常解析器不再进行遍历，注意如果自定义异常解析器不进行筛选拦截所有异常，SpringMVC所带的所有异常处理机制都会失效
       }
   }
   ```

6. ErrorViewResolver

   + 这个一般不会自定义，因为Spring底层的异常处理逻辑大都是让tomcat自己发一个/error请求，该请求就会被BasicErrorController匹配调用这个错误页视图解析器解析状态码跳转对应的错误页【两种方式都会让tomcat自己发送/error请求：一种自己直接response.sendError;第二种没有任何人能处理的异常即所有异常解析器都返回null】

   

## 4.9、Web原生组件注入

> Servlet、Filter、Listener
>
> 具体内容参考官方文档spring-boot-features7.4，使用@WebServlet、@WebFilter、@WebListener注解标注的类可以使用@ServletComponentScan注解自动注册【前面三个注解都是在Servlet规范中的】

### 4.9.1、方式一：两个注解组合

> @ServletComponentScan+原生注解【@WebServlet | @WebFilter | @WebListener】的方式

1. 使用原生继承了HttpServlet的Servlet

   > 作用是移植工程追求迅速上线可以直接把原生servlet全copy过来先不改代码扫一下就能用

   【原生Servlet实例】

```java
@WebServlet(urlPatterns = "/my")//@WebServlet注解是Servlet3.0规范提供的注解
public class MyServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().write("6666");
    }
}
```

> 注意1：此时是不能直接访问/my请求的，会报错404；需要在主配置类上添加@ServletComponentScan注解指定basePackages属性具体要扫描的包和子包，自动对目标包下写好的servlet进行扫描【只有标注了对应原生注解且实现了对应接口的类才能被扫描】
>
> ```java
> @ServletComponentScan(basePackages = "com.atlisheng.admin")
> @SpringBootApplication
> public class Boot05WebAdminApplication {
>     public static void main(String[] args) {
>         SpringApplication.run(Boot05WebAdminApplication.class, args);
>     }
> }
> ```
>
> 注意2：这种原生Servlet的方式会直接响应，不会被Spring的拦截器进行拦截

2. 使用原生实现了Filter接口的Filter

   【原生Filter实例】

   ```java
   @Slf4j
   @WebFilter("/css/*")//可以设置Filter拦截访问静态资源的所有请求,注意拦截所有单*是Servlet的写法，
   // 双*是Spring家的写法，使用原生Servlet需要使用单*表示所有路径
   public class MyFilter implements Filter {
       @Override
       public void init(FilterConfig filterConfig) throws ServletException {
           //这个是用于过滤器初始化的
           log.info("MyFilter正在初始化");
       }
       @Override
       public void destroy() {
           //这个方法用于过滤器销毁
           log.info("MyFilter正在销毁");
       }
       @Override
       public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
           //这里面写业务逻辑
           log.info("MyFilter正在工作");
           //Filter工作需要filter链的doFilter方法进行放行
           chain.doFilter(request,response);
       }
   }
   ```

3. 使用原生实现了监听器接口的Listener

   【原生Listener实例】

   ```java
   @Slf4j
   @WebListener//？这个不写默认是啥路径，监听器是否只针对时刻
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
   ```

   

### 4.9.2、方式二：使用RegistrationBean

> ServletRegistrationBean、FilterRegistrayionBean、ServletListenerRegistrationBean
>
> 以ServletRegistrationBean为例，这种用法是直接在配置类中向容器注册一个ServletRegistrationBean组件，而没有添加原生@WebServlet注解的Servlet可以直接创建一个servlet对象，直接通过构造方法向ServletRegistrationBean传入对应servlet和映射路径，映射路径是可变数量字符串参数
>
> Filter和Listener的注入方式是一样的

【使用RegistrationBean注入Web原生组件的实例】

```java
//(proxyBeanMethods = true)是保证依赖的组件始终是单实例的
@Configuration//注意这个配置类的@Configuration注解不要设置(proxyBeanMethods = false),因为有了这个设置有组件依赖，
// 一个组件被多次依赖就会创建多个对象，功能上不会有大问题，但是会导致容器中有很多冗余的对象
public class MyRegistConfig {
    @Bean
    public ServletRegistrationBean myServlet(){
        MyServlet myServlet=new MyServlet();
        return new ServletRegistrationBean(myServlet,"/my","/my02");
    }
    @Bean
    public FilterRegistrationBean myFilter(){
        MyFilter myFilter=new MyFilter();

        //方式一
        //return new FilterRegistrationBean(myFilter,myServlet());//注意FilterRegistrationBean的构造方法可以传参ServletRegistrationBean，该组件对应的Servlet对应的映射路径都会对过滤器起作用，

        //方式二
        //FilterRegistrationBean的构造方法不能直接写映射路径，只能通过setUrlPatterns方法传参字符串的list集合来设置映射路径，原因不明，记住就行
        //Arrays.asList("/my","/css/*")就可以把一串可变数量的字符串参数转换成list集合
        FilterRegistrationBean filterRegistrationBean=new FilterRegistrationBean(myFilter);
        //这个路径设置可以实现以不同的请求路径访问servlet可以控制哪些路径需要经过filter
        filterRegistrationBean.setUrlPatterns(Arrays.asList("/my","/css/*"));
        return filterRegistrationBean;
    }
    @Bean//这个是监听时间点的，有些监听器不需要设置请求路径
    public ServletListenerRegistrationBean myListener(){
        MyServletContextListener myServletContextListener=new MyServletContextListener();
        return new ServletListenerRegistrationBean(myServletContextListener);
    }
}
```

[^要点1]: filter组件通过在构造方法中指定servlet或者通过来setUrlPatterns方法来设置匹配映射路径，servlet组件可以直接在构造方法中指定可变数量字符串的方式指定匹配映射路径，监听器大部分监听具体对象创建的时间点，一般不用指定路径

### 4.9.3、DispatchServlet注入原理

> 原生Servlet匹配的映射请求不会被Spring拦截的原理

1. dsadj

   + 目前SpringBoot中有两个Servlet，一个是注入的Web原生servlet即myServlet，另一个是最大的派发处理器DispatcherServlet【前端控制器】

     > MyServlet-->处理/my请求
     >
     > DispatcherServlet-->处理/请求

   + DispatcherServlet对应的自动配置类DispatcherServletAutoConfiguration

     > 在自动配置包下的web模块下的servlet包下

     + DispatcherServlet注册步骤

       + 第一步：SpringBoot会给容器中放一个DispatcherServlet，对应的名字为dispatcherServlet

         + DispatcherServlet属性绑定的是WebMvcProperties，对应的配置项前缀是spring.mvc

         [^注意]: 此时的dispatcherServlet只是一个普通的Bean，并不是以Web原生Servlet的形式注入的

       + 第二步：给容器配置文件上传解析器

       + 第三步：以RegistrationBean的方式将从容器中拿到dispatcherServlet将DispatcherServlet组件以Web原生Servlet的形式注册到IoC容器中，这里面确定了DispatcherServlet的请求匹配映射规则为"/"，通过修改属性spring.mvc.servlet.path可以更改DispatcherServlet的对应匹配路径前缀

       [^注意]: Tomcat的Servlet有一个规定，如果多个Servlet都能处理到同一层路径，根据精确优选原则，即DispatcherServlet默认处理的是/，而我们自己的servlet处理的是/my路径，按照精确匹配优先的原则会匹配/my对应的servlet，这里/是同一层目录【精确优选原则计算机网络中有】，拦截器都是DispatcherServlet中执行的，如果请求不匹配DispatcherServlet，自然请求无法被拦截器拦截

     ```java
     @AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
     @AutoConfiguration(after = ServletWebServerFactoryAutoConfiguration.class)
     @ConditionalOnWebApplication(type = Type.SERVLET)
     @ConditionalOnClass(DispatcherServlet.class)
     public class DispatcherServletAutoConfiguration {
     
     	/**
     	 * The bean name for a DispatcherServlet that will be mapped to the root URL "/".
     	 */
     	public static final String DEFAULT_DISPATCHER_SERVLET_BEAN_NAME = "dispatcherServlet";
     
     	/**
     	 * The bean name for a ServletRegistrationBean for the DispatcherServlet "/".
     	 */
     	public static final String DEFAULT_DISPATCHER_SERVLET_REGISTRATION_BEAN_NAME = "dispatcherServletRegistration";
     
     	@Configuration(proxyBeanMethods = false)//配置类DispatcherServletConfiguration
     	@Conditional(DefaultDispatcherServletCondition.class)
     	@ConditionalOnClass(ServletRegistration.class)
     	@EnableConfigurationProperties(WebMvcProperties.class)
     	protected static class DispatcherServletConfiguration {
     
     		@Bean(name = DEFAULT_DISPATCHER_SERVLET_BEAN_NAME)//给容器中放DispatcherServlet，这里的name常量就是dispatcherServlet，注意啊，这里只有@Bean注解，没有@WebServlet注解，这里注册的dispatcherServlet就是一个普通的Bean，此时还不是一个合格的Servlet
     		public DispatcherServlet dispatcherServlet(WebMvcProperties webMvcProperties) {//DispatcherSertvlet中的属性都是绑定在WebMvcProperties中，前缀是SpringMVC
     			DispatcherServlet dispatcherServlet = new DispatcherServlet();
     			dispatcherServlet.setDispatchOptionsRequest(webMvcProperties.isDispatchOptionsRequest());
     			dispatcherServlet.setDispatchTraceRequest(webMvcProperties.isDispatchTraceRequest());
     			dispatcherServlet.setThrowExceptionIfNoHandlerFound(webMvcProperties.isThrowExceptionIfNoHandlerFound());//比如webMvcProperties中的isThrowExceptionIfNoHandlerFound() ，DispatcherServlet中如果没有异常处理器能处理错误是否要抛出异常
     			dispatcherServlet.setPublishEvents(webMvcProperties.isPublishRequestHandledEvents());
     			dispatcherServlet.setEnableLoggingRequestDetails(webMvcProperties.isLogRequestDetails());
     			return dispatcherServlet;
     		}
     
     		@Bean//给容器配置文件上传解析器
     		@ConditionalOnBean(MultipartResolver.class)
     		@ConditionalOnMissingBean(name = DispatcherServlet.MULTIPART_RESOLVER_BEAN_NAME)
     		public MultipartResolver multipartResolver(MultipartResolver resolver) {
     			// Detect if the user has created a MultipartResolver but named it incorrectly
     			return resolver;
     		}
     
     	}
     
     	@Configuration(proxyBeanMethods = false)//配置类DispatcherServletRegistrationConfiguration
     	@Conditional(DispatcherServletRegistrationCondition.class)
     	@ConditionalOnClass(ServletRegistration.class)
     	@EnableConfigurationProperties(WebMvcProperties.class)
     	@Import(DispatcherServletConfiguration.class)
     	protected static class DispatcherServletRegistrationConfiguration {
     
     		@Bean(name = DEFAULT_DISPATCHER_SERVLET_REGISTRATION_BEAN_NAME)
     		@ConditionalOnBean(value = DispatcherServlet.class, name = DEFAULT_DISPATCHER_SERVLET_BEAN_NAME)//配置了DispatcherServletRegistrationBean，这个类继承于ServletRegistrationBean，这里面直接从容器中拿的dispatcherServlet组件，这才是通过RegistrationBean的方式将DispatcherServlet以Web原生Servlet的形式注册到IoC容器中
     		public DispatcherServletRegistrationBean dispatcherServletRegistration(DispatcherServlet dispatcherServlet,
     				WebMvcProperties webMvcProperties, ObjectProvider<MultipartConfigElement> multipartConfig) {
     			DispatcherServletRegistrationBean registration = new DispatcherServletRegistrationBean(dispatcherServlet,
     					webMvcProperties.getServlet().getPath());//通过webMvcProperties.getServlet().getPath()拿到的DispatcherServlet的匹配映射路径，这个拿到的东西就是字符串"/",可以看出对应绑定的是webMvcProperties中的servlet属性的path属性，即可以通过spring.mvc.servlet.path来修改DispatcherServlet的匹配映射路径，如果修改spring.mvc.servlet.path=/mvc/,那么DispatcherServlet只能处理以/mvc/起头的请求路径
     			registration.setName(DEFAULT_DISPATCHER_SERVLET_BEAN_NAME);
     			registration.setLoadOnStartup(webMvcProperties.getServlet().getLoadOnStartup());
     			multipartConfig.ifAvailable(registration::setMultipartConfig);
     			return registration;
     		}
     
     	}
     
     	@Order(Ordered.LOWEST_PRECEDENCE - 10)
     	private static class DefaultDispatcherServletCondition extends SpringBootCondition {
     		...
     	}
     
     	@Order(Ordered.LOWEST_PRECEDENCE - 10)
     	private static class DispatcherServletRegistrationCondition extends SpringBootCondition {
     		...
     	}
     }
     ```

     

## 4.10、嵌入式Servlet容器

### 4.10.1、Web容器自动配置原理

> 对应官方文档的7.4.3章节
>
> 嵌入式Servlet容器的作用即创建SpringBoot应用时，无需外置部署服务器，应用中自带服务器，应用一启动就能够直接使用，默认使用的就是tomcat；原理SpringBoot说是使用了一种特殊的IoC容器ServletWebServerApplicationContext：IoC容器是ApplicationContext类，如果SpringBoot发现当前是一个web应用，IoC容器就会变成ServletWebServerApplicationContext【ServletWeb服务器的IoC容器】，该IoC容器继承了ApplicationContext接口，即IoC容器接口；
>
> ServletWebServerApplicationContext的作用是当SpringBoot启动引导的过程中会搜索一个ServletWebServerFactory【ServletWeb服务器工厂】，这个工厂的作用就是生产ServletWeb服务器
>
> SpringBoot底层一般有很多的WebServer工厂，TomcatServletWebServerFactory、JettyServletWebServerFactory、UndertowServletWebServerFactory
>
> 【SpringBoot底层支持的Web服务器】
>
> + 注意UndertowWebServer不支持JSP
>
> ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\web服务器.png)

1. 原理

   + 关于对Web服务器的自动配置

     > + 在自动配置包下的web包下的servlet下的有一个ServletWebFactoryAutoConfiguration即Web服务器自动配置类
     >
     > + ServletWebFactoryAutoConfiguration通过Import注解导入ServletWebServerFactoryConfiguration配置类，在这个配置类中根据系统中到底导入哪个Web服务器的包动态判断需要配置哪种Web服务器工厂【web-starter场景默认导入的是tomcat的包，所以系统底层默认配置的是Tomcat的web服务器工厂TomcatServletWebServerFactory】
     >
     > + 最后由TomcatServletWebServerFactory创建Tomcat服务器TomcatWebServer并启动，由于TomcatWebServer的构造器会执行初始化方法initialize....，在执行该方法过程中直接调用TomcatWebServer的start方法，执行完该方法tomcat就启动了，即创建服务器对象的时候tomcat服务器也一并启动了
     > + 所谓的内嵌服务器就是Spring来调用tomcat启动服务器的代码而已

     【ServletWebFactoryAutoConfiguration】

     ```java
     @Configuration(proxyBeanMethods = false)
     @AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
     @ConditionalOnClass(ServletRequest.class)
     @ConditionalOnWebApplication(type = Type.SERVLET)
     @EnableConfigurationProperties(ServerProperties.class)
     @Import({ ServletWebServerFactoryAutoConfiguration.BeanPostProcessorsRegistrar.class,
     		ServletWebServerFactoryConfiguration.EmbeddedTomcat.class,//通过ServletWebServerFactoryAutoConfiguration导入了ServletWebServerFactoryConfigurationweb服务器工厂的配置类
     		ServletWebServerFactoryConfiguration.EmbeddedJetty.class,
     		ServletWebServerFactoryConfiguration.EmbeddedUndertow.class })
     public class ServletWebServerFactoryAutoConfiguration {
     
     	@Bean//配置ServletWeb服务器工厂的定制化器ServletWebServerFactoryCustomizer，这个定制化器的作用就是web服务器配好了以后，可以通过定制化器在后续修改工厂的相关信息，在其中的customize方法中把配置文件的信息拿到修改对应旧工厂中的属性信息
     	public ServletWebServerFactoryCustomizer servletWebServerFactoryCustomizer(ServerProperties serverProperties) {
     		return new ServletWebServerFactoryCustomizer(serverProperties);
     	}
     	...
     }
     ```
     
     【ServletWebServerFactoryConfiguration】
     
     ```java
     @Configuration(proxyBeanMethods = false)
     class ServletWebServerFactoryConfiguration {//SpringBoot就在这个类中给底层配置了三个常用工厂
     	@Configuration(proxyBeanMethods = false)
     	@ConditionalOnClass({ Servlet.class, Tomcat.class, UpgradeProtocol.class })//条件配置，如果系统中有Servlet类，有tomcat类；即如果应用中导了tomcat的依赖，服务器中就会配置一个Tomcat的工厂
     	@ConditionalOnMissingBean(value = ServletWebServerFactory.class, search = SearchStrategy.CURRENT)
     	static class EmbeddedTomcat {
     
     		@Bean//给容器放了一个TomcatServletWebServerFactory
     		TomcatServletWebServerFactory tomcatServletWebServerFactory(
     				ObjectProvider<TomcatConnectorCustomizer> connectorCustomizers,
     				ObjectProvider<TomcatContextCustomizer> contextCustomizers,
     				ObjectProvider<TomcatProtocolHandlerCustomizer<?>> protocolHandlerCustomizers) {
     			TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
     			factory.getTomcatConnectorCustomizers()
     					.addAll(connectorCustomizers.orderedStream().collect(Collectors.toList()));
     			factory.getTomcatContextCustomizers()
     					.addAll(contextCustomizers.orderedStream().collect(Collectors.toList()));
     			factory.getTomcatProtocolHandlerCustomizers()
     					.addAll(protocolHandlerCustomizers.orderedStream().collect(Collectors.toList()));
     			return factory;
     		}
     
     	}
     
     	/**
     	 * Nested configuration if Jetty is being used.
     	 */
     	@Configuration(proxyBeanMethods = false)
     	@ConditionalOnClass({ Servlet.class, Server.class, Loader.class, WebAppContext.class })//条件配置，如果系统中导入的是Jetty中的Server类，就配置一个JettyServletWebServerFactory
     	@ConditionalOnMissingBean(value = ServletWebServerFactory.class, search = SearchStrategy.CURRENT)
     	static class EmbeddedJetty {
     
     		@Bean//给容器放了一个JettyServletWebServerFactory
     		JettyServletWebServerFactory JettyServletWebServerFactory(
     				ObjectProvider<JettyServerCustomizer> serverCustomizers) {
     			JettyServletWebServerFactory factory = new JettyServletWebServerFactory();
     			factory.getServerCustomizers().addAll(serverCustomizers.orderedStream().collect(Collectors.toList()));
     			return factory;
     		}
     
     	}
     
     	/**
     	 * Nested configuration if Undertow is being used.
     	 */
     	@Configuration(proxyBeanMethods = false)
     	@ConditionalOnClass({ Servlet.class, Undertow.class, SslClientAuthMode.class })//如果导入的是Undertow的包，就会放undertow的服务器工厂UndertowServletWebServerFactory
     	@ConditionalOnMissingBean(value = ServletWebServerFactory.class, search = SearchStrategy.CURRENT)
     	static class EmbeddedUndertow {
     
     		@Bean//给容器中放了一个UndertowServletWebServerFactory
     		UndertowServletWebServerFactory undertowServletWebServerFactory(
     				ObjectProvider<UndertowDeploymentInfoCustomizer> deploymentInfoCustomizers,
     				ObjectProvider<UndertowBuilderCustomizer> builderCustomizers) {
     			UndertowServletWebServerFactory factory = new UndertowServletWebServerFactory();
     			factory.getDeploymentInfoCustomizers()
     					.addAll(deploymentInfoCustomizers.orderedStream().collect(Collectors.toList()));
     			factory.getBuilderCustomizers().addAll(builderCustomizers.orderedStream().collect(Collectors.toList()));
     			return factory;
     		}
     	}
     }
     ```
     
     【ServletWebServerApplicationContext】
     
     > ServletWeb服务器的IoC容器
     
     ```java
     public class ServletWebServerApplicationContext extends GenericWebApplicationContext
     		implements ConfigurableWebServerApplicationContext {
     
     	private static final Log logger = LogFactory.getLog(ServletWebServerApplicationContext.class);
     	...
     	@Override
     	public final void refresh() throws BeansException, IllegalStateException {
     		try {
     			super.refresh();
     		}
     		catch (RuntimeException ex) {//如果有异常
     			WebServer webServer = this.webServer;
     			if (webServer != null) {
     				webServer.stop();//如果有异常异常一被捕获到Web服务器就停了
     			}
     			throw ex;
     		}
     	}
     
     	@Override
     	protected void onRefresh() {//容器一启动就会调用这个方法，作用是创建web服务器
     		super.onRefresh();
     		try {
     			createWebServer();//创建web服务器
     		}
     		catch (Throwable ex) {
     			throw new ApplicationContextException("Unable to start web server", ex);
     		}
     	}
     
     	@Override
     	protected void doClose() {
     		if (isActive()) {
     			AvailabilityChangeEvent.publish(this, ReadinessState.REFUSING_TRAFFIC);
     		}
     		super.doClose();
     	}
     
         //这个就是onRefresh方法调用的创建web服务器方法
     	private void createWebServer() {
     		WebServer webServer = this.webServer;//开始web服务器默认是空
     		ServletContext servletContext = getServletContext();
     		if (webServer == null && servletContext == null) {
     			ServletWebServerFactory factory = getWebServerFactory();//获取web服务器工厂，在刚好值引入一个web服务器依赖的情况下肯定能找到，即web服务器工厂恰好自动配置了一个
     			this.webServer = factory.getWebServer(getSelfInitializer());//调用web服务器工厂的getWebServer方法拿到web服务器，这一步执行完web服务器就有了，这里返回的WebServer对象，WebServer中的start方法就定义了服务器如何启动
     			getBeanFactory().registerSingleton("webServerGracefulShutdown",
     					new WebServerGracefulShutdownLifecycle(this.webServer));
     			getBeanFactory().registerSingleton("webServerStartStop",
     					new WebServerStartStopLifecycle(this, this.webServer));
     		}
     		else if (servletContext != null) {
     			try {
     				getSelfInitializer().onStartup(servletContext);
     			}
     			catch (ServletException ex) {
     				throw new ApplicationContextException("Cannot initialize servlet context", ex);
     			}
     		}
     		initPropertySources();
     	}
     
     	//这个就是上面方法调用的获取web服务器工厂方法
     	protected ServletWebServerFactory getWebServerFactory() {
     		// Use bean names so that we don't consider the hierarchy
     		String[] beanNames = getBeanFactory().getBeanNamesForType(ServletWebServerFactory.class);//去IoC容器中找ServletWebServerFactory组件，而且可能会找到多个
     		if (beanNames.length == 0) {//如果没有找到会抛异常，提示当前web项目中没有web服务器工厂
     			throw new ApplicationContextException("Unable to start ServletWebServerApplicationContext due to missing "
     					+ "ServletWebServerFactory bean.");
     		}
     		if (beanNames.length > 1) {//如果大于一个，也会抛异常，说当前web项目的服务器工厂太多
     			throw new ApplicationContextException("Unable to start ServletWebServerApplicationContext due to multiple "
     					+ "ServletWebServerFactory beans : " + StringUtils.arrayToCommaDelimitedString(beanNames));
     		}
     		return getBeanFactory().getBean(beanNames[0], ServletWebServerFactory.class);//如果刚好只有一个就去IoC容器中找那个web服务器工厂
     	}
     	...
     }
     ```
     
     【createWebServer方法中的factory.getWebServer方法】
     
     ```java
     public class TomcatServletWebServerFactory extends AbstractServletWebServerFactory
     		implements ConfigurableTomcatWebServerFactory, ResourceLoaderAware {
     	@Override
     	public WebServer getWebServer(ServletContextInitializer... initializers) {
     		if (this.disableMBeanRegistry) {
     			Registry.disableRegistry();
     		}
             //以前外置服务器是用户自己来启动tomcat服务器，而SpringBoot内嵌tomcat是通过创建Tomcat对象直接通过程序实现了
     		Tomcat tomcat = new Tomcat();
     		File baseDir = (this.baseDirectory != null) ? this.baseDirectory : createTempDir("tomcat");
     		tomcat.setBaseDir(baseDir.getAbsolutePath());
     		for (LifecycleListener listener : this.serverLifecycleListeners) {
     			tomcat.getServer().addLifecycleListener(listener);
     		}
     		Connector connector = new Connector(this.protocol);
     		connector.setThrowOnFailure(true);
     		tomcat.getService().addConnector(connector);//配置好连接器
     		customizeConnector(connector);
     		tomcat.setConnector(connector);
     		tomcat.getHost().setAutoDeploy(false);//配置好端口
     		configureEngine(tomcat.getEngine());//配置Tomcat引擎
     		for (Connector additionalConnector : this.additionalTomcatConnectors) {
     			tomcat.getService().addConnector(additionalConnector);
     		}
     		prepareContext(tomcat.getHost(), initializers);
     		return getTomcatWebServer(tomcat);//返回tomcatweb服务器
     	}
     }
     ```

### 4.10.2、切换其他的Web服务器

> 在官方文档Using SpringBoot中的1.5starters中有starter-undertow、starter-tomcat、starter-jetty
>
> 服务器是由web场景starter-web导入的，默认就是导入的starter-tomcat，此时直接在pom.xml的starter-web中用exclusions标签排除starter-tomcat依赖，然后单独引入目标服务器依赖

1. 引入其他web服务器pom.xml设置

   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-web</artifactId>
       <exclusions>
           <exclusion>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-starter-tomcat</artifactId>
           </exclusion>
       </exclusions>
   </dependency>
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-undertow</artifactId>
   </dependency>
   ```

### 4.10.3、更改服务器的一些默认配置

> 定制化web服务器，官方文档spring-boot-features7.4.4
>
> 第一种定制方式是修改配置文件
>
> + 所有关于服务器的配置属性前缀都是server，原因是服务器自动配置类ServletWebServerFactoryAutoConfiguration绑定的文件是Serverproperties,服务器的取值来源都来自Serverproperties，web服务器工厂创建web服务器时会根据工厂定制化器中的serverproperties来确定服务器的属性值；而Serverproperties绑定的配置属性都以server为前缀，Serverproperties类中的属性包括端口号port、地址address【这是什么地址?】、错误页error、安全连接相关信息ssl、Servlet有关的同样信息servlet
>   + server.undertow.accesslog.dir=/tmp表示指定访问undertow日志的临时目录
>   + server.servlet.session.timeout=60m表示设置session的超时时间为60分钟
>
> 第二种方式是直接自定义ServletWebServerFactory并将该组件添加到容器中，推荐自定义ConfigurableServletWebServerFactory，这两都是接口，后者继承了前者
>
> + 创建以后SpringBoot底层会自动调用该工厂根据相应属性创建web服务器
>
>   【自定义ConfigurableServletWebServerFactory示例】
>
>   ```java
>   @Bean
>   public ConfigurableServletWebServerFactory webServerFactory() {
>       TomCatServletWebServerFactory factory =new TomCatServletWebServerFactory();
>       factory.setPort(9000);
>       factory.setSessionTimeout(10,TimeUnit.MINUTES);
>       factory.setErrorPage(new ErrorPage(HttpStatus.NOT_FOUND,"/notfound.html"));
>       return factory;
>   }
>   ```
>
> 第三种是自定义WebServerFactoryCustomizer<ConfigurableServletWebServerFactory>
>
> + 通过自定义的工厂化定制器将配置文件的值与ServletWebServerFactory进行绑定
>
>   > Spring底层经常出现这种定制化器，这更像Spring的一种设计规则，只要给底层配置了自定义定制化器，就可以改变某些东西的默认行为
>
>   【原始的工厂定制化器】
>
>   ```java
>   public class ServletWebServerFactoryCustomizer implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory>, Ordered {
>   	...
>   	@Override
>   	public void customize(ConfigurableServletWebServerFactory factory) {
>   		PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
>   		map.from(this.serverProperties::getPort).to(factory::setPort);
>   		map.from(this.serverProperties::getAddress).to(factory::setAddress);
>   		map.from(this.serverProperties.getServlet()::getContextPath).to(factory::setContextPath);
>   		map.from(this.serverProperties.getServlet()::getApplicationDisplayName).to(factory::setDisplayName);
>   		map.from(this.serverProperties.getServlet()::isRegisterDefaultServlet).to(factory::setRegisterDefaultServlet);
>   		map.from(this.serverProperties.getServlet()::getSession).to(factory::setSession);
>   		map.from(this.serverProperties::getSsl).to(factory::setSsl);
>   		map.from(this.serverProperties.getServlet()::getJsp).to(factory::setJsp);
>   		map.from(this.serverProperties::getCompression).to(factory::setCompression);
>   		map.from(this.serverProperties::getHttp2).to(factory::setHttp2);
>   		map.from(this.serverProperties::getServerHeader).to(factory::setServerHeader);
>   		map.from(this.serverProperties.getServlet()::getContextParameters).to(factory::setInitParameters);
>   		map.from(this.serverProperties.getShutdown()).to(factory::setShutdown);
>   	}
>   }
>   ```
>
>   【定制工厂定制化器实例】
>
>   ```java
>   @Component
>   public class MyWebServerFactoryCustomizer implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
>       @Override
>       public void customize(ConfigurableServletWebServerFactory server) {
>           server.setPort(9000);
>       }
>   }
>   ```
>
> [^注意]: 最终还是推荐直接改全局配置文件的相关属性



## 4.11、定制化原理

1. 自动配置原理套路分析
   + 引入starter-XXX场景-->xxxxAutoConfiguration-->导入xxx组件-->绑定xxxProperties-->绑定配置文件项

2. 常见定制化方式

   + 第一种：编写自定义配置类xxxConfiguation，使用@Bean注解或者@Component注解替换或增加容器中的默认组件，如视图解析器、参数解析器，处理器异常解析器等

   + 第二种：修改配置文件中的配置属性，这种是比较常用的

   + 第三种：自定义xxxxCustomizer即xxxx定制化器，相当于选择性修改工厂创建对象时的部分默认值

   + 第四种：编写一个配置类实现WebMvcConfigurer接口，重写接口中的各种方法来实现定制

   + 第五种：如果想修改SpringMVC非常底层的组件，如RequestMappingHandlerMapping、RequestMappingHandlerAdapter、ExceptionHandlerExceptionResolver可以给容器中添加一个WebMvcRegistrations组件

     【添加WebMvcRegistrations组件示例】

     ```java
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
     ```

   + 第六种：如果用户想完全控制SpringMVC，可以添加一个用@Configuration注解标注的配置类，该配置类由@EnableWebMvc注解标注

     【@EnableWebMvc注解实例】

     ```java
     @EnableWebMvc//这个注解表示用户全面接管SpringMVC，所有的静态资源、视图解析器、欢迎页等等所有的Spring官方的自动配置全部失效，必须自己来定义所有的事情比如静态资源的访问等全部底层行为
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
         @Override
         public void addResourceHandlers(ResourceHandlerRegistry registry) {
             /**意思是访问"/aa/**" 的所有请求都去 "classpath:/static/ 下面对**内容"进行匹配*/
             registry.addResourceHandler("/aa/**")//添加静态资源处理，需要传参静态资源的请求路径，
                     .addResourceLocations("classpath:/static/");//传参对应static目录下的所有资源路径
         }
     
         @Override
         public void addInterceptors(InterceptorRegistry registry) {
             registry.addInterceptor(new LoginInterceptor())
                     .addPathPatterns("/**")//所有请求都会被拦截包括静态资源
                     .excludePathPatterns("/","/login","/css/**","/fonts/**","/images/**","/js/**","/aa/**");
         }
     }
     ```

     [^注意]: @EnableWebMvc注解表示用户全面接管SpringMVC，所有的静态资源、视图解析器、欢迎页等等所有的Spring官方的自动配置全部失效，必须自己来定义所有的事情比如静态资源的访问等全部底层行为，这个注解需要慎用

     + 全面接管SpringMVC的原理

       + SpringMVC的所有自动配置功能都在WebMvcAutoConfiguration中，这个类中有对静态资源、欢迎页、视图解析器等等一堆配置

       + 一旦使用@enableWebMvc注解，会用@Import注解导入DelegatingWebMvcConfiguration

       + DelegatingWebMvcConfiguration的作用

         + 作用1：把系统中的所有WebMvcConfigurer拿到，把这些WebMvcConfigurer的所有功能定制合起来一起生效

         + 作用2：这个类继承了WebMvcConfigurationSupport，根据父类中的行为会自动配置了一些非常底层的组件，比如HandlerMapping、内容协商管理器；这些组件依赖的组件都是从容器中获取

           > 这个类只保证SpringMVC最基本的使用，只有核心组件，如RequestMappingHandlerMapping、Adapter等

       + ​	核心：WebMvcAutoConfiguration中的配置能生效的必要条件是

         + 条件配置@ConditionalOnMissingBean(WebMvcConfigurationSupporties.class),即没有WebMvcConfigurationSupporties才生效，但是@enableWebMvc注解导入了DelegatingWebMvcConfiguration就相当于有了其父类WebMvcConfigurationSupport，即@enableWebMvc注解会导致SpringMVC配置类WebMvcAutoConfiguration没生效

           > 这时候只能由WebMvcAutoConfiguration配置的支持Rest风格的过滤器、各种消息转换器等等，都需要自己来进行配置

         ```java
         @Retention(RetentionPolicy.RUNTIME)
         @Target(ElementType.TYPE)
         @Documented
         @Import(DelegatingWebMvcConfiguration.class)//导入了DelegatingWebMvcConfiguration
         public @interface EnableWebMvc {
         }
         ```

         【DelegatingWebMvcConfiguration】

         ```java
         @Configuration(proxyBeanMethods = false)
         public class DelegatingWebMvcConfiguration extends WebMvcConfigurationSupport {
         	private final WebMvcConfigurerComposite configurers = new WebMvcConfigurerComposite();
         
         	@Autowired(required = false)
         	public void setConfigurers(List<WebMvcConfigurer> configurers) {//把系统中的所有WebMvcConfigurer拿到，把这些WebMvcConfigurer的所有功能定制一起生效
         		if (!CollectionUtils.isEmpty(configurers)) {
         			this.configurers.addWebMvcConfigurers(configurers);
         		}
         	}
         	...
         }
         ```

         

# 5、数据访问

## 5.1.1、导入JDBC场景

1. SQL部分

   + 导入数据开发场景-->引入自动配置类-->导入数据源相关组件-->数据源配置项和属性配置文件绑定

     > 在Using SpringBoot中的starter中有很多以data开始的都是整个数据库访问相关的场景，如jdbc、jpa、redis

   + 用户在上述流程中的角色是导入相关场景，配置属性配置文件

2. 数据源的自动配置

   + 导入JDBC场景

     ```xml
     <dependency>
         <groupId>org.springframework.boot</groupId>
         <artifactId>spring-boot-starter-data-jdbc</artifactId>
     </dependency>
     ```

     【场景导入的依赖】

     ![](C:\Users\Earl\Desktop\SpringBoot2学习笔记\image\JDBC场景.png)

     > 这里面缺少JDBC驱动，官方因为不知道用户使用的是那个版本和哪种数据库，所以数据库驱动由用户自己引入，但是SpringBoot对mysql驱动由默认版本仲裁，一般都自己指定自己的版本
     >
     > 【修改版本的两种方式】
     >
     > 【 方式一】
     >
     > ```xml
     > <!--SpringBoot不会在JDBC场景提供数据库驱动，因为Spring不知道用户用什么数据库和哪个版本的数据库-->
     > <!-- https://mvnrepository.com/artifact/mysql/mysql-connector-java -->
     > <dependency>
     >     <groupId>mysql</groupId>
     >     <artifactId>mysql-connector-java</artifactId>
     >     <version>8.0.27</version>
     > </dependency>
     > ```
     >
     > 【方式二】
     >
     > ```xml
     > <properties>
     >     <java.version>1.8</java.version>
     >     <!-- 重新声明版本(maven的属性就近优先原则)子工程有就以子工程为主 -->
     >     <mysql.version>8.0.27</mysql.version>
     > </properties>
     > <dependencies>
     >     <!--SpringBoot不会在JDBC场景提供数据库驱动，因为Spring不知道用户用什么数据库和哪个版本的数据库-->
     >     <!-- https://mvnrepository.com/artifact/mysql/mysql-connector-java -->
     >     <dependency>
     >         <groupId>mysql</groupId>
     >         <artifactId>mysql-connector-java</artifactId>
     >         <!--<version>8.0.27</version>-->
     >         <version>${mysql.version}</version>
     >     </dependency>
     > </dependencies>
     > ```
     >
     > 【当前本机SpringBoot的mysql驱动默认版本】
     >
     > ```xml
     > <mysql.version>8.0.33</mysql.version>
     > ```

   + 分析自动配置原理

     > 数据相关的jdbc自动配置在AutoConfigure中的data包下的jdbc包下，这里面是用来找JDBC的接口类，还有一个jdbc大包，这个包下是对整个数据库相关的配置，该包下的有关配置类见5.1.2

## 5.1.2、自动配置的类

### 【DataSourceAutoConfiguration】

> 数据源的自动配置类
>
> + 全局配置文件修改数据源相关配置属性的前缀：spring.datasource
>
> + 数据库连接池的配置，容器中没有DataSource才自动配置
>
> + 底层配置好的连接池是：HikariDataSource，这个DataSourceConfiguration.Hikari.class类中已经默认自动配置了spring.datasource.type=com.zaxxer.hikari.HikariDataSource
>
>   关于数据源自动配置的原理讲的不清楚，以后再自己研究，先记结论

```java
@AutoConfiguration(before = SqlInitializationAutoConfiguration.class)
@ConditionalOnClass({ DataSource.class, EmbeddedDatabaseType.class })
@ConditionalOnMissingBean(type = "io.r2dbc.spi.ConnectionFactory")//没有基于响应式编程的数据库连接池的相关类ConnectionFactory才会自动配置下列组件，即不用响应式技术栈才会配置原生的数据源
@EnableConfigurationProperties(DataSourceProperties.class)//跟数据源有关的所有配置都在DataSourceProperties中与全局配置文件中前缀为spring.datasourcr的配置属性绑定
@Import(DataSourcePoolMetadataProvidersConfiguration.class)
public class DataSourceAutoConfiguration {

	@Configuration(proxyBeanMethods = false)
	@Conditional(EmbeddedDatabaseCondition.class)
	@ConditionalOnMissingBean({ DataSource.class, XADataSource.class })
	@Import(EmbeddedDataSourceConfiguration.class)//导入嵌入式数据源的配置，没有做什么
	protected static class EmbeddedDatabaseConfiguration {

	}

	@Configuration(proxyBeanMethods = false)
	@Conditional(PooledDataSourceCondition.class)
	@ConditionalOnMissingBean({ DataSource.class, XADataSource.class })//当容器中没有配数据源DataSource的时候下面内容才生效
	@Import({ DataSourceConfiguration.Hikari.class, DataSourceConfiguration.Tomcat.class,
			DataSourceConfiguration.Dbcp2.class, DataSourceConfiguration.OracleUcp.class,
			DataSourceConfiguration.Generic.class, DataSourceJmxConfiguration.class //这个DataSourceConfiguration.Hikari.class类中已经默认自动配置了spring.datasource.type=com.zaxxer.hikari.HikariDataSource
                })
	protected static class PooledDataSourceConfiguration {//池化数据源的配置

	}
	...
}
```

【DataSourceTransactionManagerAutoConfiguration】

> 事务管理器的自动配置类

【JdbcTemplateAutoConfiguration】

> JdbcTemplate的自动配置类，JdbcTemplate可以用来对数据库进行CRUD的小组件，这里面跟JDBC相关的配置都在JdbcProperties中，前缀是spring.jdbc
>
> 给容器中导入组件JdbcTemplate，通过修改前缀是spring.jdbc的配置属性可以修改jdbcTemplate
>
> 只有当容器中没有数据源DataSource这个类才会自动配置数据源

【JndiDataSourceAutoConfiguration】

> Jndi的自动配置类

【XADataSourceAutoConfiguration】

> 分布式事务相关的配置

## 5.5.3、数据源的配置属性

> 数据源是用来执行数据库CRUD操作的，数据源就是提供连接对象的

1. 【配置属性实例】

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/db_account
    username: root
    password: Haworthia0715
    driver-class-name: com.mysql.jdbc.Driver #这个是数据库驱动
    #type: com.zaxxer.hikari.HikariDataSource #这个已经被默认配置好了
```

> 配置了数据库相关场景，但是没有配置数据源配置属性SpringBoot项目启动会报错，成功的标志就是应用正常启动且没有报错

2. JdbcTemplate配置实例

   ```java
   @Slf4j
   @SpringBootTest
   class Boot05WebAdminApplicationTests {
   
       //这个用@Autowired注解要报红
       @Resource
       JdbcTemplate jdbcTemplate;
   
       @Test
       void contextLoads() {
           Long aLong = jdbcTemplate.queryForObject("select count(*) from emp", Long.class);
           log.info("emp表中记录总数:{}",aLong);
       }
   
   }
   ```

3. 配置德鲁伊druid数据源

   > HikariDataSource数据源是目前世面上性能最好的数据源产品，实际开发中也比较喜欢使用阿里的druid德鲁伊数据源，该数据源有针对数据源的完善解决方案，包括：数据源的全方位监控、防止SQL的注入攻击等等

   SpringBoot整合第三方的技术有两种方式

#### 方式一

+ 方式1：自己引入一大堆东西

  > 根据Druid官方文档引入相关依赖

  + 第一步 创建数据源

    ```xml
     <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>druid</artifactId>
        <version>1.1.17</version>
    </dependency>
    
    <!--以前Spring的配置方法-->
    <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource"
        destroy-method="close">
        <property name="url" value="${jdbc.url}" />
        <property name="username" value="${jdbc.username}" />
        <property name="password" value="${jdbc.password}" />
        <property name="maxActive" value="20" />
        <property name="initialSize" value="1" />
        <property name="maxWait" value="60000" />
        <property name="minIdle" value="1" />
        <property name="timeBetweenEvictionRunsMillis" value="60000" />
        <property name="minEvictableIdleTimeMillis" value="300000" />
        <property name="testWhileIdle" value="true" />
        <property name="testOnBorrow" value="false" />
        <property name="testOnReturn" value="false" />
        <property name="poolPreparedStatements" value="true" />
        <property name="maxOpenPreparedStatements" value="20" />
    </bean>
    ```

  + 配置Druid的监控页

    + 以前的项目需要在web.xml中配一个监控页StatViewServlet，对应的拦截路径是/druid/*，访问http://110.76.43.235:9000/druid/index.html就会来到内置监控页面首页，监控页里面有数据源的很多详细信息，SQL监控【只要对数据库有操作，SQL监控中就会有内容】

    + 在SpringBoot中开启监控页还是一样的逻辑

      [^注意]: 监控页的八位区间分布每一位的具体意思可以参考官方文档

  + 打开druid的监控统计功能

    > 需要使用Druid内置的StatFilter，用于统计监控信息，需要配置StatFilter，有以下三种配置方式

    + 方式一：别名配置

    StatFilter的别名是stat，这个别名映射配置信息保存在druid-xxx.jar!/META-INF/druid-filter.properties。

    在spring中使用别名配置方式如下：

    > 相当于通过数据源的filters属性设置属性值stat来开启的

    ```
      <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource" init-method="init" destroy-method="close">
      	... ...
      	<property name="filters" value="stat" />
      </bean>
    ```

    【别名配置实例】

    ```java
    @ConfigurationProperties("spring.datasource")//这个注解的作用是指定组件的属性和属性配置文件中对应前缀的同名属性进行绑定
    @Bean
    public DataSource dataSource() throws SQLException {
        DruidDataSource druidDataSource=new DruidDataSource();
        //druidDataSource.setUrl();//也可以手动设置数据源的属性值
        //开启德鲁伊数据源监控统计功能
        druidDataSource.setFilters("stat");
        return druidDataSource;
    }
    ```

    ------

    【以下两种方式暂时不讲】

    + 方式二：组合配置

    StatFilter可以和其他的Filter配置使用，比如：

    ```
      <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource" init-method="init" destroy-method="close">
      	... ...
      	<property name="filters" value="stat,log4j" />
      </bean>
    ```

    在上面的配置中，StatFilter和Log4jFilter组合使用。

    + 方式三：通过proxyFilters属性配置

    别名配置是通过filters属性配置的，filters属性的类型是String。如果需要通过bean的方式配置，使用proxyFilters属性。

    ```
    <bean id="stat-filter" class="com.alibaba.druid.filter.stat.StatFilter">
    	<property name="slowSqlMillis" value="10000" />
    	<property name="logSlowSql" value="true" />
    </bean>
    <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource"
    	init-method="init" destroy-method="close">
    	... ...
    	<property name="filters" value="log4j" />
    	<property name="proxyFilters">
    		<list>
    			<ref bean="stat-filter" />
    		</list>
    	</property>
    </bean>
    ```

    其中filters和proxyFilters属性是组合关系的，不是替换的，在上面的配置中，dataSource有了两个Filter，StatFilter和Log4jFilter。

  + 内置监控中的Web关联监控配置WebStatFilter

    > WebStatFilter用于采集Jdbc相关的数据，即展示在web应用和URI监控下的相关数据；可以监控到具体的Web请求的一些信息和请求的请求次数等等

    # 配置_配置WebStatFilter

    # web.xml配置

    > 在web.xml中放一个拦截所有请求的WebStatFilter，SpringBoot仍然可以采用一样的逻辑
    >
    > 注意所有静态页的请求、监控页的请求都不要拦截，这些请求全部排除掉，而且注意exclusions是初始化参数，需要在使用来进行设置

    ```xml
    <filter>
    <filter-name>DruidWebStatFilter</filter-name>
    <filter-class>com.alibaba.druid.support.http.WebStatFilter</filter-class>
    <init-param>
        <!--以下静态资源请求和监控页的请求需要排除掉-->
        <param-name>exclusions</param-name>
        <param-value>*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*</param-value>
    </init-param>
    </filter>
    <filter-mapping>
    <filter-name>DruidWebStatFilter</filter-name>
    <url-pattern>/*</url-pattern>
    </filter-mapping>
    ```

    【WebStatFilter配置实例】

    ```java
    @Bean
    public FilterRegistrationBean webStatFilter(){
        WebStatFilter webStatFilter=new WebStatFilter();
        FilterRegistrationBean<WebStatFilter> filterRegistrationBean=new FilterRegistrationBean<>(webStatFilter);
        filterRegistrationBean.setUrlPatterns(Arrays.asList("/*"));
        filterRegistrationBean.addInitParameter("exclusions","*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }
    ```

    【后面的方法没讲，暂时不管】

    # exclusions配置

    经常需要排除一些不必要的url，比如*.js,/jslib/*等等。配置在init-param中。比如：

    ```
     	<init-param>
      		<param-name>exclusions</param-name>
      		<param-value>*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*</param-value>
      	</init-param>
    ```

    # sessionStatMaxCount配置

    缺省sessionStatMaxCount是1000个。你可以按需要进行配置，比如：

    ```
     	<init-param>
      		<param-name>sessionStatMaxCount</param-name>
      		<param-value>1000</param-value>
      	</init-param>
    ```

    # sessionStatEnable配置

    你可以关闭session统计功能，比如：

    ```
     	<init-param>
      		<param-name>sessionStatEnable</param-name>
      		<param-value>false</param-value>
      	</init-param>
    ```

    # principalSessionName配置

    你可以配置principalSessionName，使得druid能够知道当前的session的用户是谁。比如：

    ```
     	<init-param>
      		<param-name>principalSessionName</param-name>
      		<param-value>xxx.user</param-value>
      	</init-param>
    ```

    根据需要，把其中的xxx.user修改为你user信息保存在session中的sessionName。

    注意：如果你session中保存的是非string类型的对象，需要重载toString方法。

    # principalCookieName

    如果你的user信息保存在cookie中，你可以配置principalCookieName，使得druid知道当前的user是谁

    ```
     	<init-param>
      		<param-name>principalCookieName</param-name>
      		<param-value>xxx.user</param-value>
      	</init-param>
    ```

    根据需要，把其中的xxx.user修改为你user信息保存在cookie中的cookieName

    # profileEnable

    druid 0.2.7版本开始支持profile，配置profileEnable能够监控单个url调用的sql列表。

    ```
    <init-param>
        <param-name>profileEnable</param-name>
        <param-value>true</param-value>
    </init-param>
    ```

  + 配置防御SQL注入攻击的防火墙过滤器

    # 配置 wallfilter

    # 使用缺省配置的WallFilter

    > 单独配置一个filter在数据源的filters属性中

    ```
      <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource" init-method="init" destroy-method="close">
          ...
          <property name="filters" value="wall"/>
      </bean>
    ```

    # 结合其他Filter一起使用

    > 多个filter一切使用，直接在filters属性值中用逗号分隔关键字即可

    WallFilter可以结合其他Filter一起使用，例如：

    ```
      <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource" init-method="init" destroy-method="close">
          ...
          <property name="filters" value="wall,stat"/>
      </bean>
    ```

    这样，拦截检测的时间不在StatFilter统计的SQL执行时间内。

    【WallFilter配置实例】

    ```java
    @ConfigurationProperties("spring.datasource")//这个注解的作用是指定组件的属性和属性配置文件中对应前缀的同名属性进行绑定
    @Bean
    public DataSource dataSource() throws SQLException {
        DruidDataSource druidDataSource=new DruidDataSource();
        //druidDataSource.setUrl();//也可以手动设置数据源的属性值
        //开启德鲁伊数据源监控统计功能
        druidDataSource.setFilters("stat,wall");
        return druidDataSource;
    }
    ```

  + 配置Spring关联监控

    > 这里没做演示，直接跳过了，只讲了第一个和第二个方法，也只是浅谈了一下

    # 配置_Druid和Spring关联监控配置

    Druid提供了Spring和Jdbc的关联监控。

    # 配置spring

    com.alibaba.druid.support.spring.stat.DruidStatInterceptor是一个标准的Spring MethodInterceptor。可以灵活进行AOP配置。

    Spring AOP的配置文档： http://static.springsource.org/spring/docs/current/spring-framework-reference/html/aop-api.html

    ## 按类型拦截配置

    ```xml
    <bean id="druid-stat-interceptor"
    class="com.alibaba.druid.support.spring.stat.DruidStatInterceptor">
    </bean>
    
    <bean id="druid-type-proxyCreator" class="com.alibaba.druid.support.spring.stat.BeanTypeAutoProxyCreator">
    <!-- 所有ABCInterface的派生类被拦截监控  -->
    <property name="targetBeanType" value="xxxx.ABCInterface" />
    <property name="interceptorNames">
        <list>
            <!--这个组件会用到上面的druid-stat-interceptor组件-->
            <value>druid-stat-interceptor</value>
        </list>
    </property>
    </bean>
    ```

    ## 方法名正则匹配拦截配置

    ```xml
    <bean id="druid-stat-interceptor"
    	class="com.alibaba.druid.support.spring.stat.DruidStatInterceptor">
    </bean>
    
    <bean id="druid-stat-pointcut" 		class="org.springframework.aop.support.JdkRegexpMethodPointcut"
    scope="prototype">
    <property name="patterns">
    	<!--这是常用来指定包名的东西-->
        <list>
            <value>com.mycompany.service.*</value>
            <value>com.mycompany.dao.*</value>
        </list>
    </property>
    </bean>
    
    <!--这是aop的功能-->
    <aop:config>
    <aop:advisor advice-ref="druid-stat-interceptor"
        pointcut-ref="druid-stat-pointcut" />
    </aop:config>
    ```

    有些情况下，可能你需要配置proxy-target-class，例如：

    ```
    <aop:config proxy-target-class="true">
    	<aop:advisor advice-ref="druid-stat-interceptor"
    		pointcut-ref="druid-stat-pointcut" />
    </aop:config>
    ```

    \##按照BeanId来拦截配置

    ```
      <bean id="druid-stat-interceptor"
      	class="com.alibaba.druid.support.spring.stat.DruidStatInterceptor">
      </bean>
    
    <bean
    	class="org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator">
    	<property name="proxyTargetClass" value="true" />
    	<property name="beanNames">
    		<list>
    			<!-- 这里配置需要拦截的bean id列表 -->
    			<value>xxx-dao</value>
    			<value>xxx-service</value>
    		</list>
    	</property>
    	<property name="interceptorNames">
    		<list>
    			<value>druid-stat-interceptor</value>
    		</list>
    	</property>
    </bean>
    ```

  + 开启监控页的登录功能

    ## 1.2 配置监控页面访问密码

    需要配置Servlet的 `loginUsername` 和 `loginPassword`这两个初始参数。

    具体可以参考: [为Druid监控配置访问权限(配置访问监控信息的用户与密码)](http://blog.csdn.net/renfufei/article/details/39553639)

    示例如下:

    ```
    <!-- 配置 Druid 监控信息显示页面 -->  
    <servlet>  
        <servlet-name>DruidStatView</servlet-name>  
        <servlet-class>com.alibaba.druid.support.http.StatViewServlet</servlet-class>  
        <init-param>  
    	<!-- 允许清空统计数据 -->  
    	<param-name>resetEnable</param-name>  
    	<param-value>true</param-value>  
        </init-param>  
        <init-param>  
    	<!-- 用户名 -->  
    	<param-name>loginUsername</param-name>  
    	<param-value>druid</param-value>  
        </init-param>  
        <init-param>  
    	<!-- 密码 -->  
    	<param-name>loginPassword</param-name>  
    	<param-value>druid</param-value>  
        </init-param>  
    </servlet>  
    <servlet-mapping>  
        <servlet-name>DruidStatView</servlet-name>  
        <url-pattern>/druid/*</url-pattern>  
    </servlet-mapping>  
    ```

    【监控页登录配置实例】

    ```java
    @Bean
    public ServletRegistrationBean statViewServlet(){
        StatViewServlet statViewServlet=new StatViewServlet();
        ServletRegistrationBean<StatViewServlet> registrationBean=new ServletRegistrationBean<>(statViewServlet,"/druid/*");
        registrationBean.addInitParameter("loginUsername","admin");
        registrationBean.addInitParameter("loginPassword","123456");
        return registrationBean;
    }
    ```

  + 配置黑白名单

    > 没讲，只提了一嘴

    # 2. 配置allow和deny

    StatViewSerlvet展示出来的监控信息比较敏感，是系统运行的内部情况，如果你需要做访问控制，可以配置allow和deny这两个参数。比如：

    ```
      <servlet>
          <servlet-name>DruidStatView</servlet-name>
          <servlet-class>com.alibaba.druid.support.http.StatViewServlet</servlet-class>
      	<init-param>
      		<param-name>allow</param-name>
      		<param-value>128.242.127.1/24,128.242.128.1</param-value>
      	</init-param>
      	<init-param>
      		<param-name>deny</param-name>
      		<param-value>128.242.127.4</param-value>
      	</init-param>
      </servlet>
    ```

    ## 判断规则

    + deny优先于allow，如果在deny列表中，就算在allow列表中，也会被拒绝。
    + 如果allow没有配置或者为空，则允许所有访问

    ## ip配置规则

    配置的格式

    ```
      <IP>
      或者
      <IP>/<SUB_NET_MASK_size>
    ```

    其中

    ```
      128.242.127.1/24
    ```

    24表示，前面24位是子网掩码，比对的时候，前面24位相同就匹配。

    ## 不支持IPV6

    由于匹配规则不支持IPV6，配置了allow或者deny之后，会导致IPV6无法访问。

  + 凡是用set方法设置对象属性值的都可以去全局配置文件直接指定配置属性值来达到相同的效果

#### 方式二

+ 方式2：找对应的官方的starter，根据starter中的自动配置类自动配置场景

  + 第一步：引入官方的starter
  
    > 官方starter会引入所有的组件
  
    ```xml
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>druid-spring-boot-starter</artifactId>
        <version>1.1.17</version>
    </dependency>
    ```
  
    【druid的自动配置类】
  
    ```java
    @Configuration
    @ConditionalOnClass({DruidDataSource.class})//系统里面有DruidDataSource这个类即可配置
    @AutoConfigureBefore({DataSourceAutoConfiguration.class})//在Spring的数据源自动配置之前先进行配置的，因为spring有默认的数据源HikariDataSource，如果Spring默认的数据源先配置druid就不会生效，先配置druid那么官方的HikariDataSource就不会生效
    @EnableConfigurationProperties({DruidStatProperties.class, DataSourceProperties.class})//所有的属性都绑定在DruidStatProperties和DataSourceProperties；前缀分别为spring.datasource.druid和spring.datasource
    @Import({DruidSpringAopConfiguration.class, DruidStatViewServletConfiguration.class, DruidWebStatFilterConfiguration.class, DruidFilterConfiguration.class})//DruidSpringAopConfiguration这个是配置监控页spring监控的，配置项是spring.datasource.druid.aop-patterns;
    public class DruidDataSourceAutoConfigure {
        private static final Logger LOGGER = LoggerFactory.getLogger(DruidDataSourceAutoConfigure.class);
    
        public DruidDataSourceAutoConfigure() {
        }
    
        @Bean(initMethod = "init")//放了数据源，放的是DruidDataSourceWrapper装饰器
        @ConditionalOnMissingBean
        public DataSource dataSource() {
            LOGGER.info("Init DruidDataSource");
            return new DruidDataSourceWrapper();
        }
    }
    ```
  
    + 导入了组件数据源 DruidDataSourceWrapper
  
    + DruidSpringAopConfiguration这个是配置监控页spring监控的，配置项是spring.datasource.druid. aop-patterns;
  
    + DruidStatViewServletConfiguration是配置监控页的，配置项spring.datasource.druid.stat-view-servlet；【其中spring.datasource.druid.stat-view-servlet.enabled默认为true，表示监控页的功能是默认开启的】，这里面的配置属性值都来源于DruidStatProperties.class
  
    + DruidWebStatFilterConfiguration是配置WebStatFilter【采集web-jdbc关联监控的数据】，即web监控配置，配置项spring.datasource.druid.web-stat-filter；【其中spring.datasource.druid.web-stat-filter.enabled默认就是true，表示web监控的功能默认是开启的】
  
    + DruidFilterConfiguration是配置底层需要的所有Druid的过滤器，默认开启都是false需要在配置文件进行开启
  
      ```java
      private static final String FILTER_STAT_PREFIX = "spring.datasource.druid.filter.stat";
          private static final String FILTER_CONFIG_PREFIX = "spring.datasource.druid.filter.config";
          private static final String FILTER_ENCODING_PREFIX = "spring.datasource.druid.filter.encoding";
          private static final String FILTER_SLF4J_PREFIX = "spring.datasource.druid.filter.slf4j";
          private static final String FILTER_LOG4J_PREFIX = "spring.datasource.druid.filter.log4j";
          private static final String FILTER_LOG4J2_PREFIX = "spring.datasource.druid.filter.log4j2";
          private static final String FILTER_COMMONS_LOG_PREFIX = "spring.datasource.druid.filter.commons-log";
          private static final String FILTER_WALL_PREFIX = "spring.datasource.druid.filter.wall";
          private static final String FILTER_WALL_CONFIG_PREFIX = "spring.datasource.druid.filter.wall.config";
      ```
  
  + 第二步：配置全局配置文件
  
    > 上面只是自动配置了组件，实际上很多功能需要使用全局配置文件进行开启，注意不要又配置了这个还整了方式一的配置，这样两种配置都不会起作用
    >
    > 讲的太少了，还是要去GitHub上面扣文档
  
    ```yml
    druid:
      stat-view-servlet: #这个下面都是stat-view-servlet监控页的设置
        enabled: true #监控页的功能默认也是开启的，这些属性配置项可以在DruidStatProperties（前缀spring.datasource.druid）的内部类StatViewServlet中查看到
        login-username: admin
        login-password: 123456
        reset-enable: false #禁用重置按钮，重置按钮就是监控页顶上蓝色的重置按钮
        #allow:允许哪里的用户进行访问
        #urlPattern:请求路径
    
      #Druid的过滤器相关功能都是false
      web-stat-filter:
        enabled: true #这些功能默认都是关闭的
        url-pattern: /* #指定匹配的请求路径
        exclusions: '*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*'   #指定要排除的请求路径，这个也有默认配置，不为空就用用户的，为空就用默认配置，默认配置就是排除静态资源和druid的请求,这是一个字符串，需要使用单引号括起来，注意双引号不会转义
    
      #DruidDataSourceWrapper中的autoAddFilters属性，addAll（filters）方法可以添加filter组件，对应的是开启监控页和防火墙功能
      #filters属性是开启一系列功能组件，这里讲的太烂了，这个到底是开启组件功能，还是添加组件；经过测试，只这么写是可以使用SQL监控和SQL防火墙功能，不是必须在filter中指定enabled为true
      filters:
        #stat,wall #stat是开启sql监控的，wall是开启防火墙的，经测试，filters和filter都不写两个功能用不了
    
      #filter属性是配置单个组件属性的
      filter:
        stat:
          slow-sql-millis: 1000 #设置慢查询时间，单位是毫秒；作用是Druid会统计，所有超过1s的sql都是慢查询，StatFilter中有相关的属性列表，默认慢查询是3秒
          log-slow-sql: true #是否用日志记录慢查询，默认是false
          enabled: true #开启StatFilter，只配置filters也可以开启这个功能，单独配置这个也可以开启Sql监控功能，所以这个和filters都可以开启防火墙功能和sql监控功能
    
        wall:
          config:
            update-allow: false #这个设置为false相当于所有的更新操作都会被防火墙拦截
            drop-table-allow: false #这个设置为false表示不允许删表，所有的删表操作都会被拦截
          enabled: true #开启WebStatFilter
    
      #这个是配置spring监控的
      aop-patterns: com.atlisheng.admin.*
    ```
  
    + 系统中所有filter：
  
    | 别名          | Filter类名                                              |
    | ------------- | ------------------------------------------------------- |
    | default       | com.alibaba.druid.filter.stat.StatFilter                |
    | stat          | com.alibaba.druid.filter.stat.StatFilter                |
    | mergeStat     | com.alibaba.druid.filter.stat.MergeStatFilter           |
    | encoding      | com.alibaba.druid.filter.encoding.EncodingConvertFilter |
    | log4j         | com.alibaba.druid.filter.logging.Log4jFilter            |
    | log4j2        | com.alibaba.druid.filter.logging.Log4j2Filter           |
    | slf4j         | com.alibaba.druid.filter.logging.Slf4jLogFilter         |
    | commonlogging | com.alibaba.druid.filter.logging.CommonsLogFilter       |
  
    > slf4j 是记录日志的过滤器，比如防火墙执行拦截，监控到异常slf4j都会记录下来
  

## 5.5.4、整合MyBatis操作

### 01、整合MyBatis的流程

1. 第一步：引入MyBatis的场景启动器

> 引入场景启动器在GitHub上点入对应starter下的pom.xml【注意版本要选择稳定版，不要选择快照版，从master分支中的标签Tags来切换版本】

+ 该场景启动器引入的依赖包括
  + 数据库开发的Jdbc场景
  + mybatis的自动配置包
  + 引入了mybatis框架
  + mybatis和spring的整合

```xml
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>2.1.4</version>
</dependency>
```

+ 以前手动配置MyBatis的步骤：编写全局配置文件-->编写Mapper接口-->编写Mapper映射文件【指定namespace为对应mapper接口的全限定类名】-->获取SqlSessionFactory工厂-->通过工厂创建SqlSession-->通过SqlSession找到接口操作数据库
+ 添加了Mybatis场景启动器后的自动配置内容
  + 配置了SqlSessionFactory
  + 配置了SQLSession：自动配置了SqlSessiontemplate,在属性中组合了SqlSession
  + 通过@import注解条件配置了AutoConfiguredMapperScannerRegistrar.class，在这里面有个registerBeanDefinitions方法，在该方法中找到所有标注了@Mapper注解的接口，这里规定了只要用户自己写的操作Mybatis接口标注了@Mapper注解就会被自动扫描进来

【MybatisAutoConfiguration】

```java
@Configuration
@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class})//导入了Mybatis的jar包自然就有这两个类才能配置
@ConditionalOnSingleCandidate(DataSource.class)//整个容器中有且只有一个数据源的时候才能配置
@EnableConfigurationProperties({MybatisProperties.class})//所有对于Mybatis的配置都从配置类MybatisProperties中获取，对应的前缀是mybatis
@AutoConfigureAfter({DataSourceAutoConfiguration.class, MybatisLanguageDriverAutoConfiguration.class})
public class MybatisAutoConfiguration implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(MybatisAutoConfiguration.class);
    private final MybatisProperties properties;
    private final Interceptor[] interceptors;
    private final TypeHandler[] typeHandlers;
    private final LanguageDriver[] languageDrivers;
    private final ResourceLoader resourceLoader;
    private final DatabaseIdProvider databaseIdProvider;
    private final List<ConfigurationCustomizer> configurationCustomizers;

    //构造这个自动配置类的时候就会把MybatisProperties中的配置信息拿到
    public MybatisAutoConfiguration(MybatisProperties properties, ObjectProvider<Interceptor[]> interceptorsProvider, ObjectProvider<TypeHandler[]> typeHandlersProvider, ObjectProvider<LanguageDriver[]> languageDriversProvider, ResourceLoader resourceLoader, ObjectProvider<DatabaseIdProvider> databaseIdProvider, ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider) {
        this.properties = properties;
        this.interceptors = (Interceptor[])interceptorsProvider.getIfAvailable();
        this.typeHandlers = (TypeHandler[])typeHandlersProvider.getIfAvailable();
        this.languageDrivers = (LanguageDriver[])languageDriversProvider.getIfAvailable();
        this.resourceLoader = resourceLoader;
        this.databaseIdProvider = (DatabaseIdProvider)databaseIdProvider.getIfAvailable();
        this.configurationCustomizers = (List)configurationCustomizersProvider.getIfAvailable();
    }

    ...

    @Bean//给容器中放了一个SqlSessionFactory
    @ConditionalOnMissingBean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);//把数据源放到Mybatissql会话工厂中
        factory.setVfs(SpringBootVFS.class);
        if (StringUtils.hasText(this.properties.getConfigLocation())) {//这个Properties就是MybatisProperties
            factory.setConfigLocation(this.resourceLoader.getResource(this.properties.getConfigLocation()));
        }
        ...
    }

    private void applyConfiguration(SqlSessionFactoryBean factory) {
        ...
    }

    @Bean//给容器中配置了一个SqlSessionTemplate，这里面有一个SqlSession属性，这个份SqlSession用来操作数据库的
    @ConditionalOnMissingBean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        ExecutorType executorType = this.properties.getExecutorType();
        return executorType != null ? new SqlSessionTemplate(sqlSessionFactory, executorType) : new SqlSessionTemplate(sqlSessionFactory);
    }

    @Configuration
    @Import({MybatisAutoConfiguration.AutoConfiguredMapperScannerRegistrar.class})//导入了一个AutoConfiguredMapperScannerRegistrar.class，这个也要满足条件配置才能生效；在这里面有个registerBeanDefinitions方法，在该方法中找到所有标注了@Mapper注解的接口，这里规定了只要用户自己写的操作Mybatis接口标注了@Mapper注解就会被自动扫描进来
    @ConditionalOnMissingBean({MapperFactoryBean.class, MapperScannerConfigurer.class})
    public static class MapperScannerRegistrarNotFoundConfiguration implements InitializingBean {
        public MapperScannerRegistrarNotFoundConfiguration() {
        }

        public void afterPropertiesSet() {
            MybatisAutoConfiguration.logger.debug("Not found configuration for registering mapper bean using @MapperScan, MapperFactoryBean and MapperScannerConfigurer.");
        }
    }

    public static class AutoConfiguredMapperScannerRegistrar implements BeanFactoryAware, ImportBeanDefinitionRegistrar {
        ...
        }
        public void setBeanFactory(BeanFactory beanFactory) {
            this.beanFactory = beanFactory;
        }
    }
}

```

【MyBatisProperties】

```java
@ConfigurationProperties(
    prefix = "mybatis"
)
public class MybatisProperties {
    public static final String MYBATIS_PREFIX = "mybatis";
    private static final ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
    private String configLocation;//配置的是mybatis全局配置文件的路径
    private String[] mapperLocations;//配置的是mybatis的mapper文件的路径
    private String typeAliasesPackage;
    private Class<?> typeAliasesSuperType;
    private String typeHandlersPackage;
    private boolean checkConfigLocation = false;
    private ExecutorType executorType;
    private Class<? extends LanguageDriver> defaultScriptingLanguageDriver;
    private Properties configurationProperties;
    @NestedConfigurationProperty
    private Configuration configuration;
    ...
}
```

【SqlSessionTemplate】

```java
public class SqlSessionTemplate implements SqlSession, DisposableBean {
    private final SqlSessionFactory sqlSessionFactory;
    private final ExecutorType executorType;
    private final SqlSession sqlSessionProxy;//这个就是整合的SqlSession
    private final PersistenceExceptionTranslator exceptionTranslator;
    ...
}
```

2. 第二步：准备mybatis组件

> 这里参考Mybatis官方文档

+ 配置Mybatis全局配置文件

  在yml文件中使用mybatis.config-location.classpath属性指定mybatis全局配置文件的位置，全局配置文件的Configuration标签里面啥都不用写，但是Configuration标签得在

  ```xml
  <?xml version="1.0" encoding="UTF-8" ?>
  <!DOCTYPE configuration
          PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
          "http://mybatis.org/dtd/mybatis-3-config.dtd">
  <configuration>
      
  </configuration>
  ```

+ 准备javaBean类

+ 准备AccountMapper接口

  > mybatisx插件比较好用，mybatisx是mybatis-plus开发的插件，可以实现mapper和SQL映射文件的跳转，可以联系数据库实现逆向工程生成代码，还有一些语法提示，mybatis-plus官方文档介绍比较详尽
  >
  > 注意Mapper接口一定要标注@Mapper注解

  ```java
  @Mapper
  public interface AccountMapper {
      public Account getActById(Long id);
  }
  ```

+ 准备mapper映射文件，该映射文件需要和Mapper接口同名

  ```xml
  <?xml version="1.0" encoding="UTF-8" ?>
  <!DOCTYPE mapper
          PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
          "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
  
  <mapper namespace="com.atlisheng.admin.mapper.AccountMapper">
      <select id="getActById" resultType="com.atlisheng.admin.bean.Account">
          select * from account_tbl where id=#{id}
      </select>
  </mapper>
  ```

+ 在spring全局配置文件中指定Mybatis全局配置文件和Mapper映射文件的类路径下的目录

  ```yml
  mybatis:
    config-location: classpath:mybatis/mybatis-config.xml #全局配置文件位置
    mapper-locations: classpath:mybatis/mapper/*.xml #Mapper映射文件位置
  ```

+ 编写Service类

  ```java
  @Service
  public class AccountService {
      @Resource
      AccountMapper accountMapper;
      public Account getActById(Long id){
          return accountMapper.getActById(id);
      }
  }
  ```

+ 编写控制器方法

  ```java
  @ResponseBody
  @GetMapping("/acct")
  public Account getById(@RequestParam("id") Long id){
      return accountService.getActById(id);
  }
  ```

3. 配置属性配置项

   1. 指定配置文件位置

      > 在spring全局配置文件中指定Mybatis全局配置文件和Mapper映射文件的类路径下的目录

      ```yml
      mybatis:
        config-location: classpath:mybatis/mybatis-config.xml #全局配置文件位置
        mapper-locations: classpath:mybatis/mapper/*.xml #Mapper映射文件位置
      ```

   2. 指定全局配置文件的信息

      > 建议不要写mybatis全局配置文件，直接配置在mybatis.configuration属性下即可

​			注意：驼峰命名规则默认是false，需要在mybatis全局配置文件的Configuration标签中打开

```xml
<settings>
    <setting name="mapUnderscoreToCamelCase" value="true"/>
</settings>
```

> 在MybatisProperties中的private Configuration configuration属性中对Mybatis的设置项进行了绑定，同样在全局配置文件中指定对应的属性配置值就相当于在mybatis全局配置文件中进行对应的配置，具体要配的值可以看IDEA的提示，注意一旦使用这种方式就不能使用config-location: classpath:mybatis/mybatis-config.xml来指定全局配置文件，即要么只能在mybatiys全局配置文件中配，要么只能在yml中配置【意味着可以不写mybatis全局配置文件，所有的mybatis的全局配置文件都可以单独放在Configuration配置项中即可】

```yml
mybatis:
  #config-location: classpath:mybatis/mybatis-config.xml；config-location和configuration不可以共存，mybatis不知道以哪个为准
  mapper-locations: classpath:mybatis/mapper/*.xml
  configuration: #这个属性下可以指定mybatis全局配置文件中的相关配置
    map-underscore-to-camel-case: true
```

### 02、Mybatis纯注解整合

> 参考Mybatis的GitHub上的starter的Wiki
>
> 除了引入Mybatis的starter，还可以在SpringBoot项目初始化的时候选中Mybatis框架，效果是一样的
>
> 纯注解写Mapper【如@select注解】可以不用写Mapper映射文件，但是这种方式只适用于简单SQL；好就好在Mapper映射文件和纯注解可以混用，特别复杂的SQL就可以用Mapper映射文件
>
> 用@Options注解可以给SQL标签设置属性值，相当于扩展SQL语句的功能
>
> 【纯注解的Mapper接口实例】
>
> ```java
> @Mapper
> public interface CityMapper {
>     @Select("select * from city where id=#{id}")
>     public City getCityById(Long id);
> 
>     @Insert("insert into city(name,state,country) values (#{name},#{state},#{country});")
>     @Options(useGeneratedKeys = true,keyProperty = "id")
>     public int insertCity(City city);
> }
> ```
>
> 【service】
>
> ```java
> @Service
> public class CityService {
>     @Autowired
>     CityMapper cityMapper;
> 
>     public City getCityById(Long id){
>         return cityMapper.getCityById(id);
>     }
> 
> 
>     public City saveCity(City city){
>         cityMapper.insertCity(city);
>         return city;
>     }
> }
> ```
>
> 【控制层】
>
> ```java
> @ResponseBody
> @PostMapping("/city")
> public City saveCity(City city){
>     System.out.println(city);
>     return cityService.saveCity(city);
> }
> 
> @GetMapping("/city")
> @ResponseBody
> public City getCityById(@RequestParam Long id){
>     return cityService.getCityById(id);
> }
> ```

### 03、混合模式

简单SQL用注解

复杂SQL还是用SQL映射文件

> ```
> useGeneratedKeys属性为true，keyProperty为id表示查询返回自增主键，然后赋值给city的id属性
> ```

【Mapper接口】

```java
@Mapper
public interface CityMapper {
    @Select("select * from city where id=#{id}")
    public City getCityById(Long id);

    public int insertCity(City city);
}
```

【SQL映射文件】

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.atlisheng.admin.mapper.CityMapper">
    <!--useGeneratedKeys属性为true，keyProperty为id表示查询返回自增主键，然后赋值给city的id属性-->
    <insert id="insertCity" useGeneratedKeys="true" keyProperty="id">
        insert into city(name,state,country) values (#{name},#{state},#{country});
    </insert>
</mapper>
```

【service】

```java
@Service
public class CityService {
    @Autowired
    CityMapper cityMapper;

    public City getCityById(Long id){
        return cityMapper.getCityById(id);
    }


    public City saveCity(City city){
        cityMapper.insertCity(city);
        return city;
    }
}
```

【控制层】

```java
@ResponseBody
@PostMapping("/city")
public City saveCity(City city){
    System.out.println(city);
    return cityService.saveCity(city);
}

@GetMapping("/city")
@ResponseBody
public City getCityById(@RequestParam Long id){
    return cityService.getCityById(id);
}
```

### 04、最佳实践

1. 引入mybatis-starter

2. 配置application.yaml,指定mapper-location位置

3. 编写Mapper接口并标注@Mapper注解

4. 简单方法直接使用注解写SQL

5. 复杂方法编写mapper.xml进行绑定映射

   > 每个Mapper上都写@Mapper注解太麻烦，可以直接在主应用类上用@MapperScan注解指定Mapper所在的包，这样每个Mapper接口上就不用标注@Mapper注解了
   >
   > ```java
   > @MapperScan("com.atlisheng.admin.mapper")
   > @ServletComponentScan(basePackages = "com.atlisheng.admin")
   > @SpringBootApplication
   > public class Boot05WebAdminApplication {
   > 
   >     public static void main(String[] args) {
   >         SpringApplication.run(Boot05WebAdminApplication.class, args);
   >     }
   > 
   > }
   > ```

## 5.5.6、整合Mybatis-Plus完成CRUD











