package ru.feshenko.credit.bank.calculator.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.feshenko.credit.bank.calculator.dto.*;
import ru.feshenko.credit.bank.calculator.enums.EmploymentStatusEnum;
import ru.feshenko.credit.bank.calculator.enums.GenderEnum;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class CalculatorService {
    @Value("${loan.base.rate}")
    private BigDecimal baseRate;

    @Value("${loan.insurance.price}")
    private BigDecimal insurancePrice;

    @Value("${loan.insurance.discount}")
    private BigDecimal insuranceDiscount;

    @Value("${loan.salary-client.discount}")
    private BigDecimal salaryClientDiscount;

    public List<LoanOfferDto> generatedOffers(LoanStatementRequestDto request) {
        List<LoanOfferDto> offers = new ArrayList<>();
        offers.add(calculateLoanOffer(request, true, true));
        offers.add(calculateLoanOffer(request, true, false));
        offers.add(calculateLoanOffer(request, false, true));
        offers.add(calculateLoanOffer(request, false, false));
        return offers.stream().sorted(Comparator.comparing(LoanOfferDto::rate).reversed()).toList();
    }

    public BigDecimal calculateMonthlyPayment(BigDecimal amount, BigDecimal rate, Integer term) {
        BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP).divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        return amount.multiply(monthlyRate).multiply(monthlyRate.add(BigDecimal.ONE).pow(term)).divide(monthlyRate.add(BigDecimal.ONE).pow(term).subtract(BigDecimal.ONE),2, RoundingMode.HALF_UP);
    }

    public LoanOfferDto calculateLoanOffer(LoanStatementRequestDto request, Boolean isInsuranceEnabled, Boolean isSalaryClient) {
        BigDecimal totalAmount = calculateAmount(isInsuranceEnabled, request.amount());
        BigDecimal rate = calculateRate(isInsuranceEnabled, isSalaryClient);

        return new LoanOfferDto(UUID.randomUUID(), request.amount(), totalAmount, request.term(), calculateMonthlyPayment(totalAmount, rate, request.term()), rate, isInsuranceEnabled, isSalaryClient);
    }

    public BigDecimal calculateRate(Boolean isInsuranceEnabled, Boolean isSalaryClient) {
        BigDecimal rate = baseRate;
        if (isInsuranceEnabled == true) {
            rate = rate.subtract(insuranceDiscount);
        }
        if (isSalaryClient == true) {
            rate = rate.subtract(salaryClientDiscount);
        }
        return rate;
    }

    public BigDecimal calculateAmount(Boolean isInsuranceEnabled, BigDecimal amount) {
        if (isInsuranceEnabled == true) {
            return amount.add(insurancePrice);
        }
        return amount;
    }


    public CreditDto scoreAndCalculateCredit(ScoringDataDto dto) {
        BigDecimal totalAmount = calculateAmount(dto.isInsuranceEnabled(), dto.amount());
        BigDecimal rate = calculateScoringRate(dto);
        BigDecimal monthlyPayment = calculateMonthlyPayment(totalAmount, rate, dto.term());
        BigDecimal psk = calculatePsk(monthlyPayment, dto.term(), totalAmount);
        return new CreditDto(totalAmount, dto.term(), monthlyPayment, rate, psk, dto.isInsuranceEnabled(), dto.isSalaryClient(), calculatePaymentScheduleDto(totalAmount, dto.term(), rate, monthlyPayment));
    }

    public BigDecimal calculateScoringRate(ScoringDataDto dto) {
        validateScoringDate(dto);
        BigDecimal rate = calculateRate(dto.isInsuranceEnabled(), dto.isSalaryClient());
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

    public BigDecimal calculatePsk(BigDecimal monthlyPayment, Integer term, BigDecimal amount) {
        BigDecimal sp = monthlyPayment.multiply(BigDecimal.valueOf(term));
        return sp.divide(amount,10,RoundingMode.HALF_UP).subtract(BigDecimal.ONE).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
    }

    public List<PaymentScheduleElementDto> calculatePaymentScheduleDto(BigDecimal amount, Integer term, BigDecimal rate, BigDecimal monthlyPayment) {
        BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP).divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        List<PaymentScheduleElementDto> paymentSchedule = new ArrayList<>();
        BigDecimal remainingDebt = amount;
        for (int i = 0; i < term; i++) {
            BigDecimal interestPayment = remainingDebt.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal debtPayment = monthlyPayment.subtract(interestPayment);
            remainingDebt = remainingDebt.subtract(debtPayment);
            if (remainingDebt.compareTo(BigDecimal.ZERO) < 0) {
                remainingDebt = BigDecimal.ZERO;
            }
            paymentSchedule.add(new PaymentScheduleElementDto(i + 1, LocalDate.now().plusMonths(i + 1), monthlyPayment, interestPayment, debtPayment, remainingDebt));
        }
        return paymentSchedule;
    }

}
