package br.com.finsavior.controller.advice;

import br.com.finsavior.exception.DeleteUserException;
import br.com.finsavior.exception.GenericException;
import br.com.finsavior.exception.AuthTokenException;
import io.grpc.StatusRuntimeException;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(annotations = RestController.class)
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiErrorHandlerResponse> handlerNotFound(UsernameNotFoundException e){
        log.error("ApiExceptionHandler, message={}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorHandlerResponse(
                        HttpStatus.NOT_FOUND.value(),
                        e.getMessage()
                ));
    }

    @ExceptionHandler(AuthTokenException.class)
    public ResponseEntity<Boolean> handleValidateTokenErrorException(AuthTokenException e){
        log.error("ApiExceptionHandler, message={}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(false);
    }

    @ExceptionHandler(GenericException.class)
    public ResponseEntity<ApiErrorHandlerResponse> handlerGenericException(GenericException e){
        log.error("ApiExceptionHandler, message={}", e.getMessage(), e);
        return ResponseEntity
                .status(e.getStatus())
                .body(new ApiErrorHandlerResponse(
                        e.getStatus().value(),
                        e.getMessage()
                ));
    }
    
    @ExceptionHandler(value = {
            Exception.class,
            RuntimeException.class,
            StatusRuntimeException.class,
            DeleteUserException.class,
            NullPointerException.class,
            AccessDeniedException.class,
    })
    public ResponseEntity<ApiErrorHandlerResponse> handler(Exception e){
        log.error("ApiExceptionHandler, message={}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiErrorHandlerResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        e.getMessage()
                ));
    }
}
