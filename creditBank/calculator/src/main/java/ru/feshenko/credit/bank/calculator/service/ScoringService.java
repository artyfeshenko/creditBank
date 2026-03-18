package ru.feshenko.credit.bank.calculator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.feshenko.credit.bank.calculator.dto.ScoringDataDto;
import ru.feshenko.credit.bank.calculator.enums.EmploymentStatusEnum;
import ru.feshenko.credit.bank.calculator.enums.GenderEnum;
import ru.feshenko.credit.bank.calculator.exception.ScoringDataException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScoringService {

    private final CalculatorService calculatorService;

    public BigDecimal calculateScoringRate(ScoringDataDto dto) {
        validateScoringData(dto);
        BigDecimal rate = calculatorService.calculateRate(dto.isInsuranceEnabled(), dto.isSalaryClient());
        rate = calculateEmploymentScoring(dto, rate);
        rate = calculatePositionScoring(dto, rate);
        rate = calculateMaritalStatusScoring(dto, rate);
        rate = calculateGenderScoring(dto, rate);
        log.debug("итоговая ставка по кредиту: {}", rate);
        return rate;
    }

    public void validateScoringData(ScoringDataDto dto) {
        if (dto.employment().employmentStatus() == EmploymentStatusEnum.UNEMPLOYED) {
            log.debug("отказ-клиент безработный");
            throw new ScoringDataException("you are unemployed");
        }

        if (dto.amount().compareTo(dto.employment().salary().multiply(BigDecimal.valueOf(24))) > 0) {
            log.debug("отказ-зарплата клиента меньше суммы всех зарплат за 24 месяца");
            throw new ScoringDataException("the loan amount exceeds 24 salaries");
        }

        int age = Period.between(dto.birthdate(), LocalDate.now()).getYears();
        if (age < 20 || age > 65) {
            log.debug("отказ-возраст клиента меньше 20 или больше 65");
            throw new ScoringDataException("Your age is not suitable");
        }

        if (dto.employment().workExperienceTotal() < 18) {
            log.debug("отказ-общий стаж клиента меньше 18 месяцев");
            throw new ScoringDataException("your work experience is too short");
        }

        if (dto.employment().workExperienceCurrent() < 3) {
            log.debug("отказ-текущий стаж клиента меньше 3 месяцев");
            throw new ScoringDataException("your work experience is too short");
        }
    }

    public BigDecimal calculateEmploymentScoring(ScoringDataDto dto, BigDecimal rate) {
        BigDecimal finalRate = switch (dto.employment().employmentStatus()) {
            case SELF_EMPLOYED -> rate.add(BigDecimal.TWO);
            case BUSINESS_OWNER -> rate.add(BigDecimal.ONE);
            default -> rate;
        };
        log.debug("итоговый процент: {}", finalRate);
        return finalRate;
    }

    public BigDecimal calculatePositionScoring(ScoringDataDto dto, BigDecimal rate) {
        BigDecimal finalRate = switch (dto.employment().position()) {
            case MIDDLE_MANAGER -> rate.add(BigDecimal.TWO);
            case TOP_MANAGER -> rate.add(BigDecimal.valueOf(3));
        };
        log.debug("итоговый процент: {}", finalRate);
        return finalRate;
    }

    public BigDecimal calculateMaritalStatusScoring(ScoringDataDto dto, BigDecimal rate) {
        BigDecimal finalRate = switch (dto.maritalStatus()) {
            case MARRIED -> rate.subtract(BigDecimal.valueOf(3));
            case DIVORCED -> rate.add(BigDecimal.ONE);
            default -> rate;
        };
        log.debug("итоговый процент: {}", finalRate);
        return finalRate;
    }

    public BigDecimal calculateGenderScoring(ScoringDataDto dto, BigDecimal rate) {
        int age = Period.between(dto.birthdate(), LocalDate.now()).getYears();
        if (dto.gender() == GenderEnum.WOMAN && age >= 32 && age <= 60) {
            BigDecimal finalRate = rate.subtract(BigDecimal.valueOf(3));
            log.debug("итоговый процент: {}", finalRate);
            return finalRate;
        }

        if (dto.gender() == GenderEnum.MAN && age >= 30 && age <= 55) {
            BigDecimal finalRate = rate.subtract(BigDecimal.valueOf(3));
            log.debug("итоговый процент: {}", finalRate);
            return finalRate;
        }

        if (dto.gender() == GenderEnum.NOT_BINARY) {
            BigDecimal finalRate = rate.add(BigDecimal.valueOf(7));
            log.debug("итоговый процент: {}", finalRate);
            return finalRate;
        }
        log.debug("итоговый процент: {}", rate);
        return rate;
    }
}
