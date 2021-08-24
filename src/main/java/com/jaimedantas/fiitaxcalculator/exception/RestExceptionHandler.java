package com.jaimedantas.fiitaxcalculator.exception;

import com.jaimedantas.fiitaxcalculator.context.MdcInterceptor;
import com.jaimedantas.fiitaxcalculator.controller.RestController;
import org.apache.tomcat.util.descriptor.web.ContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.jaimedantas.fiitaxcalculator.model.Error;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RestController.class);

    @ExceptionHandler(ArithmeticException.class)
    protected ResponseEntity<Object> handleArithmeticException(ArithmeticException ex){
        logger.error("Divisão por zero");
        Error response = new Error(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());

        return new ResponseEntity<>(response, response.getCode());
    }

    @ExceptionHandler(IOException.class)
    protected ResponseEntity<Object> handleIOException(IOException ex){
        logger.error("Falha ao converter PDF");
        Error response = new Error(HttpStatus.INTERNAL_SERVER_ERROR, ex.getCause().getMessage());

        return new ResponseEntity<>(response, response.getCode());
    }

    @ExceptionHandler(FileTooBigException.class)
    protected ResponseEntity<Object> handleArithmeticException(FileTooBigException ex){
        logger.error(ex.getMessage());
        Error response = new Error(ex.getCode(), ex.getMessage());

        return new ResponseEntity<>(response, response.getCode());
    }

    @ExceptionHandler(UnauthorizedRequestException.class)
    protected ResponseEntity<Object> handleArithmeticException(UnauthorizedRequestException ex){
        logger.error("Tentativa de hack");
        Error response = new Error(HttpStatus.UNAUTHORIZED, ex.getCause().getMessage());

        return new ResponseEntity<>(response, response.getCode());
    }

    @ExceptionHandler(Throwable.class)
    protected ResponseEntity<Object> handleArithmeticException(Throwable ex){
        logger.error("Não conseguimos processar sua requisição. Tente novamente mais tarde.");
        Error response = new Error(HttpStatus.INTERNAL_SERVER_ERROR, ex.getCause().getMessage());

        return new ResponseEntity<>(response, response.getCode());
    }


}
