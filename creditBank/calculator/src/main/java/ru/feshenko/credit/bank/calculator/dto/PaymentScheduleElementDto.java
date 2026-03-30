package ru.feshenko.credit.bank.calculator.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Payment Schedule Element Information")
public record PaymentScheduleElementDto(

        @Schema(
                description = "Payment period number",
                example = "1"
        )
        Integer number,

        @Schema(
                description = "Payment date",
                example = "2024-02-15"
        )
        LocalDate date,

        @Schema(
                description = "Total payment amount",
                example = "50000"
        )
        BigDecimal totalPayment,

        @Schema(
                description = "Interest portion of the payment",
                example = "10000"
        )
        BigDecimal interestPayment,

        @Schema(
                description = "Principal debt portion of the payment",
                example = "40000"
        )
        BigDecimal debtPayment,

        @Schema(
                description = "Remaining debt after payment",
                example = "960000"
        )
        BigDecimal remainingDebt
) {
}
