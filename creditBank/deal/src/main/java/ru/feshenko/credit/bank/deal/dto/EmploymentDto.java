package ru.feshenko.credit.bank.deal.dto;


import ru.feshenko.credit.bank.deal.enums.EmploymentStatus;
import ru.feshenko.credit.bank.deal.enums.EmployementPosition;

import java.math.BigDecimal;

public record EmploymentDto(


        EmploymentStatus employmentStatus,


        String employerINN,

        BigDecimal salary,


        EmployementPosition position,


        Integer workExperienceTotal,


        Integer workExperienceCurrent
) {
}
