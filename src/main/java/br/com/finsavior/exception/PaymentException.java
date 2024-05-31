package br.com.finsavior.exception;


public class PaymentException extends RuntimeException {

    public PaymentException(String message) {
        super(message);
    }
}
