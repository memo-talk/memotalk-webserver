package com.memotalk.exception;

import com.memotalk.exception.enumeration.ErrorCode;

public class NoAuthException extends RuntimeException {
    public NoAuthException(ErrorCode errorCode) {
        super(errorCode.getMessage());
    }
}