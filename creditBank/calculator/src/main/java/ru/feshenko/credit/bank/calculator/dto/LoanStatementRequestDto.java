package ru.feshenko.credit.bank.calculator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import ru.feshenko.credit.bank.calculator.validation.Adult;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Loan Statement Request Information")
public record LoanStatementRequestDto(

        @Schema(
                description = "Requested loan amount",
                example = "1000000"
        )
        @NotNull(message = "amount must be specified")
        @DecimalMin(value = "20000", message = "amount should be greater than 20000")
        BigDecimal amount,

        @Schema(
                description = "Loan term in months",
                example = "12"
        )
        @NotNull(message = "term must be specified")
        @Min(value = 6, message = "term should be greater than 6")
        Integer term,

        @Schema(
                description = "First name (Latin letters only)",
                example = "Ivan"
        )
        @NotEmpty(message = "firstName should not be empty")
        @Pattern(regexp = "[a-zA-Z]{2,30}", message = "firstName must consist of Latin letters and from 2 to 30 characters.")
        String firstName,

        @Schema(
                description = "Last name (Latin letters only)",
                example = "Petrov"
        )
        @NotEmpty(message = "lastName should not be empty")
        @Pattern(regexp = "[a-zA-Z]{2,30}", message = "lastName must consist of Latin letters and from 2 to 30 characters.")
        String lastName,

        @Schema(
                description = "Middle name (Latin letters only, optional)",
                example = "Ivanovich"
        )
        @Pattern(regexp = "[a-zA-Z]{2,30}", message = "middleName must consist of Latin letters and from 2 to 30 characters.")
        String middleName,

        @Schema(
                description = "Email address",
                example = "ivan.petrov@example.com"
        )
        @NotEmpty(message = "email should not be empty")
        @Pattern(regexp = "^[a-z0-9A-Z_!#$%&'*+/=?`{|}~^.-]+@[a-z0-9A-Z.-]+$", message = "incorrect email format")
        String email,

        @Schema(
                description = "Date of birth",
                example = "1990-01-01"
        )
        @NotNull(message = "birthdate must be specified")
        @Adult
        LocalDate birthdate,

        @Schema(
                description = "Passport series (4 digits)",
                example = "1234"
        )
        @Pattern(regexp = "\\d{4}", message = "the passport series must consist of 4 digits")
        String passportSeries,

        @Schema(
                description = "Passport number (6 digits)",
                example = "567890"
        )
        @Pattern(regexp = "\\d{6}", message = "the passport number must consist of 6 digits")
        String passportNumber
) {
}
