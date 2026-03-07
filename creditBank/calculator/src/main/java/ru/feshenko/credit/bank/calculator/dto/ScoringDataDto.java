package ru.feshenko.credit.bank.calculator.dto;

import ru.feshenko.credit.bank.calculator.enums.GenderEnum;
import ru.feshenko.credit.bank.calculator.enums.MaritalStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ScoringDataDto(
        BigDecimal amount,
        Integer term,
        String firstName,
        String lastName,
        String middleName,
        GenderEnum gender,
        LocalDate birthdate,
        String passportSeries,
        String passportNumber,
        LocalDate passportIssueDate,
        String passportIssueBranch,
        MaritalStatusEnum maritalStatus,
        Integer dependentAmount,
        EmploymentDto employment,
        String accountNumber,
        Boolean isInsuranceEnabled,
        Boolean isSalaryClient
) {
}
