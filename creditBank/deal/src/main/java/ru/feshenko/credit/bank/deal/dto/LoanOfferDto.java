package ru.feshenko.credit.bank.deal.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Loan Offer Information")
public class LoanOfferDto {

        @Schema(description = "Request ID", example = "123e4567-e89b-12d3-a456-426614174000")
        private UUID statementId;

        @Schema(description = "Requested loan amount", example = "1000000")
        private BigDecimal requestedAmount;

        @Schema(description = "The total amount of the loan (including insurance and additional services)", example = "1500000")
        private BigDecimal totalAmount;

        @Schema(description = "Loan term in months", example = "12")
        private Integer term;

        @Schema(description = "Monthly payment", example = "50000")
        private BigDecimal monthlyPayment;

        @Schema(description = "The interest rate", example = "14")
        private BigDecimal rate;

        @Schema(description = "is Insurance Enabled", example = "true")
        private Boolean isInsuranceEnabled;

        @Schema(description = "is Salary Client", example = "true")
        private Boolean isSalaryClient;
}
