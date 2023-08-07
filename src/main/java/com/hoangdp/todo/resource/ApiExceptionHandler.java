package com.hoangdp.todo.resource;

import org.springdoc.api.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler({ java.sql.SQLIntegrityConstraintViolationException.class, org.hibernate.PropertyValueException.class })
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage badRequestException(Exception ex, WebRequest request) {
        return new ErrorMessage("Vui lòng kiểm tra lại yêu cầu. Tất cả có tại: /swagger-ui/index.html?urls.primaryName=user");
    }

    @ExceptionHandler({IndexOutOfBoundsException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage notFoundException(Exception ex, WebRequest request) {
        return new ErrorMessage("Không tìm thấy đối tượng phù hợp");
    }
}
