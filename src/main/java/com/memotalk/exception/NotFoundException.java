package com.memotalk.exception;

import com.memotalk.exception.enumeration.ErrorCode;

public class NotFoundException extends RuntimeException {
    public NotFoundException(ErrorCode errorCodeMessage) {
        super(errorCodeMessage.getMessage());
    }
}
