package ru.feshenko.credit.bank.calculator.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ScoringDataDto(
        BigDecimal amount,
        Integer term,
        String firstName,
        String lastName,
        String middleName,
        Enum gender,
        LocalDate birthdate,
        String passportSeries,
        String passportNumber,
        LocalDate passportIssueDate,
        String passportIssueBranch,
        Enum maritalStatus,
        Integer dependentAmount,
        EmploymentDto employment,
        String accountNumber,
        Boolean isInsuranceEnabled,
        Boolean isSalaryClient
) {
}
