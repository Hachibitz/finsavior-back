package br.com.finsavior.controller.advice;

import java.time.LocalDateTime;

public record ApiErrorHandlerResponse(
        Integer status,
        String message
) {
}
