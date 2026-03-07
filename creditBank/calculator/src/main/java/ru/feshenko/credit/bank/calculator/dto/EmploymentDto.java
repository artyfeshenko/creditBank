package ru.feshenko.credit.bank.calculator.dto;

import ru.feshenko.credit.bank.calculator.enums.EmploymentStatusEnum;
import ru.feshenko.credit.bank.calculator.enums.PositionEnum;

import java.math.BigDecimal;

public record EmploymentDto(
        EmploymentStatusEnum employmentStatus,
        String employerINN,
        BigDecimal salary,
        PositionEnum position,
        Integer workExperienceTotal,
        Integer workExperienceCurrent
) {
}
