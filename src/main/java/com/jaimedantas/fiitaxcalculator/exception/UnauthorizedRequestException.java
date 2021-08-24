package com.jaimedantas.fiitaxcalculator.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter @Setter
public class UnauthorizedRequestException extends Exception {
    final HttpStatus code = HttpStatus.UNAUTHORIZED;
    String message;

    public UnauthorizedRequestException() {
        super("Request blocked");
        this.message = message;
    }
}
