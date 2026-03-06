package ru.feshenko.credit.bank.calculator.dto;

import java.math.BigDecimal;

public record EmploymentDto(
        Enum employmentStatus,
        String employerINN,
        BigDecimal salary,
        Enum position,
        Integer workExperienceTotal,
        Integer workExperienceCurrent
) {
}
