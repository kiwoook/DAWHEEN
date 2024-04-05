package com.study.dawheen.common.exception;

import org.springframework.security.core.AuthenticationException;

public class AuthorizationFailedException extends AuthenticationException {

    public AuthorizationFailedException(String msg) {
        super(msg);
    }
}
