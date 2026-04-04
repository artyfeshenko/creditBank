package ru.feshenko.credit.bank.deal.dto;

import ru.feshenko.credit.bank.deal.enums.Gender;
import ru.feshenko.credit.bank.deal.enums.MaritalStatus;

import java.time.LocalDate;

public record FinishRegistrationRequestDto(
        Gender gender,
        MaritalStatus maritalStatus,
        Integer dependentAmount,
        LocalDate passportIssueDate,
        String passportIssueBranch,
        EmploymentDto employment,
        String accountNumber
) {
}
