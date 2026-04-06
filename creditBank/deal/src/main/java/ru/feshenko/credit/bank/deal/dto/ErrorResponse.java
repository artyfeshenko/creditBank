package ru.feshenko.credit.bank.deal.dto;

import java.time.LocalDateTime;

public record ErrorResponse(


        String message,

        LocalDateTime timestamp
) {
}
