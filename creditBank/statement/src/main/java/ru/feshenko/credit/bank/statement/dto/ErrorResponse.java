package ru.feshenko.credit.bank.statement.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Error Response Information")
public record ErrorResponse(

        @Schema(description = "Error message", example = "The client was not found with the specified ID")
        String message,

        @Schema(description = "Timestamp of the error occurrence", example = "2024-01-15T10:30:45")
        LocalDateTime timestamp
) {
}
