package ru.feshenko.credit.bank.deal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import ru.feshenko.credit.bank.deal.dto.FinishRegistrationRequestDto;
import ru.feshenko.credit.bank.deal.dto.LoanStatementRequestDto;
import ru.feshenko.credit.bank.deal.entity.Client;
import ru.feshenko.credit.bank.deal.entity.Passport;

@Mapper(componentModel = "spring", uses = {EmploymentMapper.class})
public interface ClientMapper {

    @Mapping(target = "birthDate", source = "birthdate")
    @Mapping(target = "passport", source = ".", qualifiedByName = "toPassport")
    Client toClient(LoanStatementRequestDto dto);

    @Mapping(target = "passport.issueDate", source = "passportIssueDate")
    @Mapping(target = "passport.issueBranch", source = "passportIssueBranch")
    void updateClient(@MappingTarget Client client, FinishRegistrationRequestDto requestDto);

    @Mapping(target = "series", source = "passportSeries")
    @Mapping(target = "number", source = "passportNumber")
    @Named("toPassport")
    Passport toPassport(LoanStatementRequestDto dto);
}
