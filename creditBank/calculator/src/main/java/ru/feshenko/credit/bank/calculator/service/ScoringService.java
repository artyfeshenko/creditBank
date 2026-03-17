package ru.feshenko.credit.bank.calculator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.feshenko.credit.bank.calculator.dto.ScoringDataDto;
import ru.feshenko.credit.bank.calculator.enums.EmploymentStatusEnum;
import ru.feshenko.credit.bank.calculator.enums.GenderEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
public class ScoringService {

    private final CalculatorService calculatorService;

    public BigDecimal calculateScoringRate(ScoringDataDto dto) {
        validateScoringDate(dto);
        BigDecimal rate = calculatorService.calculateRate(dto.isInsuranceEnabled(), dto.isSalaryClient());
        rate = calculateEmploymentScoring(dto, rate);
        rate = calculatePositionScoring(dto, rate);
        rate = calculateMaritalStatusScoring(dto, rate);
        rate = calculateGenderScoring(dto, rate);
        return rate;
    }

    public void validateScoringDate(ScoringDataDto dto) {
        if (dto.employment().employmentStatus() == EmploymentStatusEnum.UNEMPLOYED) {
            throw new RuntimeException("you are unemployed");
        }

        if (dto.amount().compareTo(dto.employment().salary().multiply(BigDecimal.valueOf(24))) > 0) {
            throw new RuntimeException("the loan amount exceeds 24 salaries");
        }

        int age = Period.between(dto.birthdate(), LocalDate.now()).getYears();
        if (age < 20 || age > 65) {
            throw new RuntimeException("Your age is not suitable");
        }

        if (dto.employment().workExperienceTotal() < 18) {
            throw new RuntimeException("your work experience is too short");
        }

        if (dto.employment().workExperienceCurrent() < 3) {
            throw new RuntimeException("your work experience is too short");
        }
    }

    public BigDecimal calculateEmploymentScoring(ScoringDataDto dto, BigDecimal rate) {
        return switch (dto.employment().employmentStatus()) {
            case SELF_EMPLOYED -> rate.add(BigDecimal.TWO);
            case BUSINESS_OWNER -> rate.add(BigDecimal.ONE);
            default -> rate;
        };
    }

    public BigDecimal calculatePositionScoring(ScoringDataDto dto, BigDecimal rate) {
        return switch (dto.employment().position()) {
            case MIDDLE_MANAGER -> rate.add(BigDecimal.TWO);
            case TOP_MANAGER -> rate.add(BigDecimal.valueOf(3));
        };
    }

    public BigDecimal calculateMaritalStatusScoring(ScoringDataDto dto, BigDecimal rate) {
        return switch (dto.maritalStatus()) {
            case MARRIED -> rate.subtract(BigDecimal.valueOf(3));
            case DIVORCED -> rate.add(BigDecimal.ONE);
            default -> rate;
        };
    }

    public BigDecimal calculateGenderScoring(ScoringDataDto dto, BigDecimal rate) {
        int age = Period.between(dto.birthdate(), LocalDate.now()).getYears();
        if (dto.gender() == GenderEnum.WOMAN && age >= 32 && age <= 60) {
            return rate.subtract(BigDecimal.valueOf(3));
        }

        if (dto.gender() == GenderEnum.MAN && age >= 30 && age <= 55) {
            return rate.subtract(BigDecimal.valueOf(3));
        }

        if (dto.gender() == GenderEnum.NOT_BINARY) {
            return rate.add(BigDecimal.valueOf(7));
        }
        return rate;
    }
}
