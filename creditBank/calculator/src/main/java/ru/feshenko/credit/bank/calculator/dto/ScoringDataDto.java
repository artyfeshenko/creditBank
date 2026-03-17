package ru.feshenko.credit.bank.calculator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.feshenko.credit.bank.calculator.enums.GenderEnum;
import ru.feshenko.credit.bank.calculator.enums.MaritalStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Scoring Data Information")
public record ScoringDataDto(

        @Schema(
                description = "Requested loan amount",
                example = "1000000"
        )
        BigDecimal amount,

        @Schema(
                description = "Loan term in months",
                example = "12"
        )
        Integer term,

        @Schema(
                description = "First name (Latin letters only)",
                example = "Ivan"
        )
        String firstName,

        @Schema(
                description = "Last name (Latin letters only)",
                example = "Petrov"
        )
        String lastName,

        @Schema(
                description = "Middle name (Latin letters only, optional)",
                example = "Ivanovich"
        )
        String middleName,

        @Schema(
                description = "Gender",
                example = "MALE"
        )
        GenderEnum gender,

        @Schema(
                description = "Date of birth",
                example = "1990-01-01"
        )
        LocalDate birthdate,

        @Schema(
                description = "Passport series (4 digits)",
                example = "1234"
        )
        String passportSeries,

        @Schema(
                description = "Passport number (6 digits)",
                example = "567890"
        )
        String passportNumber,

        @Schema(
                description = "Passport issue date",
                example = "2010-05-15"
        )
        LocalDate passportIssueDate,

        @Schema(
                description = "Passport issue department",
                example = "MVD RUSSIA"
        )
        String passportIssueBranch,

        @Schema(
                description = "Marital status",
                example = "MARRIED"
        )
        MaritalStatusEnum maritalStatus,

        @Schema(
                description = "?",
                example = "2"
        )
        Integer dependentAmount,

        @Schema(
                description = "Employment information"
        )
        EmploymentDto employment,

        @Schema(
                description = "Bank account number",
                example = "40817810099910004321"
        )
        String accountNumber,

        @Schema(
                description = "is Insurance Enabled",
                example = "true"
        )
        Boolean isInsuranceEnabled,

        @Schema(
                description = "is Salary Client",
                example = "true"
        )
        Boolean isSalaryClient
) {
}
