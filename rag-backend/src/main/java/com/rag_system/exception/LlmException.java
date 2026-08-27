package com.rag_system.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class LlmException extends RuntimeException {

    private final HttpStatus httpStatus;

    public LlmException(String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = HttpStatus.BAD_GATEWAY;
    }

    public LlmException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }
}
