package br.com.finsavior.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BillRegisterException extends RuntimeException {

    private HttpStatus status;

    public BillRegisterException(String message) { super(message); }

    public BillRegisterException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}
