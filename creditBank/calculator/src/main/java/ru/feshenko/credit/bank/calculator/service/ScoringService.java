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
    private static final int WOMAN_MIN_AGE = 32;
    private static final int WOMAN_MAX_AGE = 60;
    private static final int MAN_MIN_AGE = 30;
    private static final int MAN_MAX_AGE = 55;
    private static final int SALARY_MONTHS = 24;
    private static final int MIN_AGE = 20;
    private static final int MAX_AGE = 65;
    private static final int MIN_TOTAL_WORK_EXPERIENCE_MONTHS = 18;
    private static final int MIN_CURRENT_WORK_EXPERIENCE_MONTHS = 3;
    private static final BigDecimal WOMAN_MAN_RATE_REDUCTION = BigDecimal.valueOf(3);
    private static final BigDecimal NON_BINARY_RATE_INCREASE = BigDecimal.valueOf(7);

    public BigDecimal calculateScoringRate(ScoringDataDto dto) {
        BigDecimal rate = calculatorService.calculateRate(dto.isInsuranceEnabled(), dto.isSalaryClient());
        rate = applyEmploymentScoring(dto, rate);
        rate = applyPositionScoring(dto, rate);
        rate = applyMaritalStatusScoring(dto, rate);
        rate = applyGenderScoring(dto, rate);
        log.debug("итоговая ставка по кредиту: {}", rate);
        return rate;
    }

    public void validateScoringData(ScoringDataDto dto) {
        if (dto.employment().employmentStatus() == EmploymentStatusEnum.UNEMPLOYED) {
            log.debug("отказ-клиент безработный");
            throw new ScoringDataException("you are unemployed");
        }

        if (dto.amount().compareTo(dto.employment().salary().multiply(BigDecimal.valueOf(SALARY_MONTHS))) > 0) {
            log.debug("отказ-зарплата клиента меньше суммы всех зарплат за {} месяца", SALARY_MONTHS);
            throw new ScoringDataException("the loan amount exceeds" + SALARY_MONTHS + "salaries");
        }

        int age = calculateAge(dto);
        if (age < MIN_AGE || age > MAX_AGE) {
            log.debug("отказ-возраст клиента меньше {} или больше {}", MIN_AGE, MAX_AGE);
            throw new ScoringDataException("Your age is not suitable");
        }

        if (dto.employment().workExperienceTotal() < MIN_TOTAL_WORK_EXPERIENCE_MONTHS) {
            log.debug("отказ-общий стаж клиента меньше {} месяцев", MIN_TOTAL_WORK_EXPERIENCE_MONTHS);
            throw new ScoringDataException("your work experience is too short");
        }

        if (dto.employment().workExperienceCurrent() < MIN_CURRENT_WORK_EXPERIENCE_MONTHS) {
            log.debug("отказ-текущий стаж клиента меньше {} месяцев", MIN_CURRENT_WORK_EXPERIENCE_MONTHS);
            throw new ScoringDataException("your work experience is too short");
        }
    }

    public BigDecimal applyEmploymentScoring(ScoringDataDto dto, BigDecimal rate) {
        BigDecimal finalRate = switch (dto.employment().employmentStatus()) {
            case SELF_EMPLOYED -> rate.add(BigDecimal.TWO);
            case BUSINESS_OWNER -> rate.add(BigDecimal.ONE);
            default -> rate;
        };
        log.debug("ставка после учёта занятости: {}", finalRate);
        return finalRate;
    }

    public BigDecimal applyPositionScoring(ScoringDataDto dto, BigDecimal rate) {
        BigDecimal finalRate = switch (dto.employment().position()) {
            case MIDDLE_MANAGER -> rate.add(BigDecimal.TWO);
            case TOP_MANAGER -> rate.add(BigDecimal.valueOf(3));
            default -> rate;
        };
        log.debug("ставка после учеба позиции на работе: {}", finalRate);
        return finalRate;
    }

    public BigDecimal applyMaritalStatusScoring(ScoringDataDto dto, BigDecimal rate) {
        BigDecimal finalRate = switch (dto.maritalStatus()) {
            case MARRIED -> rate.subtract(BigDecimal.valueOf(3));
            case DIVORCED -> rate.add(BigDecimal.ONE);
            default -> rate;
        };
        log.debug("ставка после учета семейного положения: {}", finalRate);
        return finalRate;
    }

    public int calculateAge(ScoringDataDto dto) {
        return Period.between(dto.birthdate(), LocalDate.now()).getYears();
    }


    public BigDecimal applyGenderScoring(ScoringDataDto dto, BigDecimal rate) {
        int age = calculateAge(dto);
        BigDecimal finalRate;
        if (dto.gender() == GenderEnum.FEMALE && age >= WOMAN_MIN_AGE && age <= WOMAN_MAX_AGE) {
            finalRate = rate.subtract(WOMAN_MAN_RATE_REDUCTION);
            log.debug("ставка после учёта пола и возраста (женщина {}-{}): {}", WOMAN_MIN_AGE, WOMAN_MAX_AGE, finalRate);
        } else if (dto.gender() == GenderEnum.MALE && age >= MAN_MIN_AGE && age <= MAN_MAX_AGE) {
            finalRate = rate.subtract(WOMAN_MAN_RATE_REDUCTION);
            log.debug("ставка после учёта пола и возраста (мужчина {}-{}): {}", MAN_MIN_AGE, MAN_MAX_AGE, finalRate);
        } else if (dto.gender() == GenderEnum.NON_BINARY) {
            finalRate = rate.add(NON_BINARY_RATE_INCREASE);
            log.debug("ставка после учёта пола (небинарный): {}", finalRate);
        } else {
            finalRate = rate;
            log.debug("ставка после учёта пола и возраста: {}", finalRate);
        }
        return finalRate;
    }
}
