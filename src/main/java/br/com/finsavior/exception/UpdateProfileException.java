package br.com.finsavior.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UpdateProfileException extends RuntimeException {

    private HttpStatus status;

    public UpdateProfileException(String message) { super(message); }

    public UpdateProfileException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}
