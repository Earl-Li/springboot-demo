package com.atlisheng.admin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN,reason = "用户数量太多!!!")//HttpStatus是枚举，里面有超多值，分别对应不同的状态码，点进去仅能看见，FORBIDDEN是403
public class UserTooManyException extends RuntimeException{
    public UserTooManyException() {
    }

    public UserTooManyException(String msg) {
        super(msg);
    }
}
