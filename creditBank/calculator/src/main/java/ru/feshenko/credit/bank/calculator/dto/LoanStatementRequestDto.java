package ru.feshenko.credit.bank.calculator.dto;

import jakarta.validation.constraints.*;
import ru.feshenko.credit.bank.calculator.validation.Adult;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LoanStatementRequestDto(
        @NotNull(message = "amount must be specified")
        @DecimalMin(value = "20000", message = "amount should be greater than 20000")
        BigDecimal amount,

        @NotNull(message = "term must be specified")
        @Min(value = 6, message = "term should be greater than 6")
        Integer term,

        @NotEmpty(message = "firstName should not be empty")
        @Pattern(regexp = "[a-zA-Z]{2,30}", message = "firstName must consist of Latin letters and from 2 to 30 characters.")
        String firstName,

        @NotEmpty(message = "lastName should not be empty")
        @Pattern(regexp = "[a-zA-Z]{2,30}", message = "lastName must consist of Latin letters and from 2 to 30 characters.")
        String lastName,

        @Pattern(regexp = "[a-zA-Z]{2,30}", message = "middleName must consist of Latin letters and from 2 to 30 characters.")
        String middleName,

        @NotEmpty(message = "email should not be empty")
        @Pattern(regexp = "^[a-z0-9A-Z_!#$%&'*+/=?`{|}~^.-]+@[a-z0-9A-Z.-]+$", message = "incorrect email format")
        String email,

        @NotNull(message = "birthdate must be specified")
        @Adult
        LocalDate birthdate,

        @Pattern(regexp = "\\d{4}", message = "the passport series must consist of 4 digits")
        String passportSeries,

        @Pattern(regexp = "\\d{6}", message = "the passport number must consist of 6 digits")
        String passportNumber
) {
}
