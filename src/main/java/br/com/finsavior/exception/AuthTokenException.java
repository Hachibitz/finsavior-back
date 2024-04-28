package br.com.finsavior.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthTokenException extends RuntimeException{

    private HttpStatus status;

    public AuthTokenException(String message){ super(message); }

    public AuthTokenException(String message, HttpStatus status){
        super(message);
        this.status = status;
    }
}
