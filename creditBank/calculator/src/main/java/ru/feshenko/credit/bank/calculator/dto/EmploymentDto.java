package ru.feshenko.credit.bank.calculator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.feshenko.credit.bank.calculator.enums.EmploymentStatusEnum;
import ru.feshenko.credit.bank.calculator.enums.PositionEnum;

import java.math.BigDecimal;

@Schema(description = "Employment Information")
public record EmploymentDto(

        @Schema(
                description = "Employment status",
                example = "SELF_EMPLOYED"
        )
        EmploymentStatusEnum employmentStatus,

        @Schema(
                description = "Employer's INN",
                example = "7707083893"
        )
        String employerINN,

        @Schema(
                description = "Wages",
                example = "100000"
        )
        BigDecimal salary,

        @Schema(
                description = "position",
                example = "TOP_MANAGER"
        )
        PositionEnum position,

        @Schema(
                description = "Total work experience in months",
                example = "20"
        )
        Integer workExperienceTotal,

        @Schema(
                description = "Length of service at the current workplace in months",
                example = "5"
        )
        Integer workExperienceCurrent
) {
}
