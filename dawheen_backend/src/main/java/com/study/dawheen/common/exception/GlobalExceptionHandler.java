package com.study.dawheen.common.exception;

import com.study.dawheen.common.dto.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;


/**
 * 모든 예외 처리 핸들러
 */

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /*
      1. @Valid, @Validated 으로 binding error
      2. HttpMessageConverter binder error 시
     */

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException : {}", e.getMessage());
        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST, e.getBindingResult());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /*
        enum type 불일치로 인해 binding error
     */

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("handleMethodArgumentTypeMismatchException : {}", e.getMessage());

        final ErrorResponse response = ErrorResponse.of(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /*
        지원하지 않는 Method
     */

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("handleHttpRequestMethodNotSupportedException", e);
        final ErrorResponse response = ErrorResponse.of(HttpStatus.METHOD_NOT_ALLOWED);
        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    /*
        파라미터로 인해 엔티티 존재하지 않는 에러
     */
    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e) {
        log.error("EntityNotFoundExceptionError : {}", e.getMessage());
        return ResponseEntity.notFound().build();
    }

    /*
        파라미터의 값이 잘못될 시 error
     */
    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgumentExceptionError : {}", e.getMessage());
        return ResponseEntity.badRequest().build();
    }

    /*
        비정상적인 접근으로 인해 발생하는 Error
     */

    @ExceptionHandler(IllegalAccessException.class)
    protected ResponseEntity<ErrorResponse> handleIllegalAccessException(IllegalArgumentException e) {
        log.error("IllegalAccessException : {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /*
        서버 입출력 에러 시
     */

    @ExceptionHandler(IOException.class)
    protected ResponseEntity<ErrorResponse> handleIOException(IOException e) {
        log.error("IOExceptionExceptionError : {}", e.getMessage());
        return ResponseEntity.internalServerError().build();
    }

    /*
        동시성 처리에러로 시간 초과 발생
     */

    @ExceptionHandler(InterruptedException.class)
    protected ResponseEntity<ErrorResponse> handleInterruptException(InterruptedException e) {
        log.error("InterruptedExceptionError : {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

}
