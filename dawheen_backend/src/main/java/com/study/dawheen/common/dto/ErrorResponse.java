package com.study.dawheen.common.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

    private String message;
    private int status;
    private List<FieldError> errors;
    private HttpStatus code;

    private ErrorResponse(final HttpStatus code) {
        this.message = code.getReasonPhrase();
        this.status = code.value();
    }

    private ErrorResponse(final HttpStatus code, final List<FieldError> errors) {
        this.message = code.getReasonPhrase();
        this.status = code.value();
        this.errors = errors;
    }

    public static ErrorResponse of(final HttpStatus code) {
        return new ErrorResponse(code);
    }

    public static ErrorResponse of(final HttpStatus code, final BindingResult bindingResult) {
        return new ErrorResponse(code, FieldError.of(bindingResult));
    }

    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class FieldError {
        private String field;
        private String value;

        private FieldError(final String field, final String value) {
            this.field = field;
            this.value = value;
        }

        private static List<FieldError> of(final BindingResult bindingResult) {
            final List<org.springframework.validation.FieldError> fieldErrors = bindingResult.getFieldErrors();
            return fieldErrors.stream()
                    .map(error -> new FieldError(
                            error.getField(),
                            error.getRejectedValue() == null ? "" : error.getRejectedValue().toString()))
                    .toList();
        }
    }
}
