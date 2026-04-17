package ru.feshenko.credit.bank.deal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.feshenko.credit.bank.deal.dto.CreditDto;
import ru.feshenko.credit.bank.deal.entity.Credit;

@Mapper(componentModel = "spring")
public interface CreditMapper {
    @Mapping(target = "creditStatus", constant = "CALCULATED")
    Credit toCredit(CreditDto dto);
}
