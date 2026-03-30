package ru.feshenko.credit.bank.calculator.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Loan Offer Information")
public record LoanOfferDto(

        @Schema(
                description = "Request ID",
                example = "123e4567-e89b-12d3-a456-426614174000"
        )
        UUID statementId,

        @Schema(
                description = "Requested loan amount",
                example = "1000000"
        )
        BigDecimal requestedAmount,

        @Schema(
                description = "The total amount of the loan (including insurance and additional services)",
                example = "1500000"
        )
        BigDecimal totalAmount,

        @Schema(
                description = "Loan term in months",
                example = "12"
        )
        Integer term,

        @Schema(
                description = "Monthly payment",
                example = "50000"
        )
        BigDecimal monthlyPayment,

        @Schema(
                description = "The interest rate",
                example = "14"
        )
        BigDecimal rate,

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
