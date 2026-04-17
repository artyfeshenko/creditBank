package ru.feshenko.credit.bank.deal.dto;


import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Loan Statement Request Information")
public record LoanStatementRequestDto(

        @Schema(description = "Requested loan amount", example = "1000000")
        BigDecimal amount,

        @Schema(description = "Loan term in months", example = "12")
        Integer term,

        @Schema(description = "First name (Latin letters only)", example = "Ivan")
        String firstName,

        @Schema(description = "Last name (Latin letters only)", example = "Petrov")
        String lastName,

        @Schema(description = "Middle name (Latin letters only, optional)", example = "Ivanovich")
        String middleName,

        @Schema(description = "Email address", example = "ivan.petrov@example.com")
        String email,

        @Schema(description = "Date of birth", example = "1990-01-01")
        LocalDate birthdate,

        @Schema(description = "Passport series (4 digits)", example = "1234")
        String passportSeries,

        @Schema(description = "Passport number (6 digits)", example = "567890")
        String passportNumber
) {
}
