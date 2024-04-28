package br.com.finsavior.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GenericException extends RuntimeException {

    private HttpStatus status;

    public GenericException(String message) { super(message); }

    public GenericException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}
