package ru.feshenko.credit.bank.deal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.feshenko.credit.bank.deal.dto.FinishRegistrationRequestDto;
import ru.feshenko.credit.bank.deal.dto.LoanOfferDto;
import ru.feshenko.credit.bank.deal.dto.ScoringDataDto;
import ru.feshenko.credit.bank.deal.entity.Client;

@Mapper(componentModel = "spring")
public interface ScoringMapper {
    @Mapping(target = "amount", source = "loanOfferDto.requestedAmount")
    @Mapping(target = "birthdate", source = "client.birthDate")
    @Mapping(target = "passportSeries", source = "client.passport.series")
    @Mapping(target = "passportNumber", source = "client.passport.number")
    @Mapping(target = "gender", source = "dto.gender")
    @Mapping(target = "maritalStatus", source = "dto.maritalStatus")
    @Mapping(target = "dependentAmount", source = "dto.dependentAmount")
    @Mapping(target = "employment", source = "dto.employment")
    @Mapping(target = "accountNumber", source = "dto.accountNumber")
    ScoringDataDto toScoringDataDto(FinishRegistrationRequestDto dto, Client client, LoanOfferDto loanOfferDto);
}
