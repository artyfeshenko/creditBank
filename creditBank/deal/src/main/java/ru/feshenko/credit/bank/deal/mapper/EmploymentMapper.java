package ru.feshenko.credit.bank.deal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.feshenko.credit.bank.deal.dto.EmploymentDto;
import ru.feshenko.credit.bank.deal.entity.Employment;

@Mapper(componentModel = "spring")
public interface EmploymentMapper {

    @Mapping(target = "status", source = "employmentStatus")
    @Mapping(target = "employerInn", source = "employerINN")
    Employment toEmployment(EmploymentDto dto);
}
