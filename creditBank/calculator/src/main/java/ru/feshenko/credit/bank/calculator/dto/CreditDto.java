package ru.feshenko.credit.bank.calculator.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Loan Information")
public record CreditDto(

        @Schema(
            description = "Loan amount",
            example = "100000.00"
        )
        BigDecimal amount,

        @Schema(
                description = "Loan term in months",
                example = "12"
        )
        Integer term,

        @Schema(
                description = "Monthly payment",
                example = "5000.00"
        )
        BigDecimal monthlyPayment,

        @Schema(
                description = "The interest rate",
                example = "12.5"
        )
        BigDecimal rate,

        @Schema(
                description = "The full cost of the loan",
                example = "8.32"
        )
        BigDecimal psk,

        @Schema(
                description = "availability of insurance",
                example = "true"
        )
        Boolean isInsuranceEnabled,

        @Schema(
                description = "does the bank's client receive a salary",
                example = "true"
        )
        Boolean isSalaryClient,

        @Schema(
                description = "Loan payment schedule"
        )
        List<PaymentScheduleElementDto> paymentSchedule
) {
}
