package ru.feshenko.credit.bank.deal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.feshenko.credit.bank.deal.enums.Gender;
import ru.feshenko.credit.bank.deal.enums.MaritalStatus;

import java.time.LocalDate;

@Schema(description = "DTO for completing registration and full credit calculation")
public record FinishRegistrationRequestDto(

        Gender gender,

        MaritalStatus maritalStatus,

        Integer dependentAmount,

        @Schema(description = "Passport issue date", example = "2010-05-15")
        LocalDate passportIssueDate,


        String passportIssueBranch,

        @Schema(description = "Employment details of the client")
        EmploymentDto employment,

        @Schema(description = "Bank account number", example = "40817810099910004312")
        String accountNumber
) {
}
