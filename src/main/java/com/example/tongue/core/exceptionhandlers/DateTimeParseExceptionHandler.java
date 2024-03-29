package com.example.tongue.core.exceptionhandlers;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.format.DateTimeParseException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class DateTimeParseExceptionHandler {
    @ResponseStatus(value = BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(DateTimeParseException.class)
    public Error DateTimeParseException(DateTimeParseException ex) {
        Error error = new Error();
        error.setError(BAD_REQUEST);
        error.setReason("Dates must follow ISO8601 standard");
        error.setValue(ex.getParsedString());
        error.setErrorAtIndex(ex.getErrorIndex());
        return error;
    }
    static class Error{
        private HttpStatus error;
        private String reason;
        private String value;
        private int errorAtIndex;

        public HttpStatus getError() {
            return error;
        }

        public void setError(HttpStatus error) {
            this.error = error;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getErrorAtIndex() {
            return errorAtIndex;
        }

        public void setErrorAtIndex(int errorAtIndex) {
            this.errorAtIndex = errorAtIndex;
        }
    }
}
