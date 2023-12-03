package com.atlisheng.admin.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author Earl
 * @version 1.0.0
 * @描述 处理整个web controller的异常
 * @创建日期 2023/06/04
 * @since 1.0.0
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
