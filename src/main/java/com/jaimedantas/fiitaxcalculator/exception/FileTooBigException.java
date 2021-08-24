package com.jaimedantas.fiitaxcalculator.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter @Setter
public class FileTooBigException extends Exception {
    final HttpStatus code = HttpStatus.UNPROCESSABLE_ENTITY;
    String message;

    public FileTooBigException(String message) {
        super(message);
        this.message = message;
    }
}
