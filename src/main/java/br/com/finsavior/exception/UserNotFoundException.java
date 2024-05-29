package br.com.finsavior.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message){ super(message); }
}
