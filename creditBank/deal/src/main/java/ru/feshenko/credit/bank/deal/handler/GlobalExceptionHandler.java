package ru.feshenko.credit.bank.deal.handler;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.feshenko.credit.bank.deal.dto.ErrorResponse;
import ru.feshenko.credit.bank.deal.exception.StatementNotFoundException;

import java.time.LocalDateTime;

@RestControllerAdvice
@Hidden
public class GlobalExceptionHandler {
    @ExceptionHandler(StatementNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleStatementNotFoundException(StatementNotFoundException e) {
        String message = e.getMessage();
        ErrorResponse errorResponse = new ErrorResponse(message, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}
