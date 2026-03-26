package ru.feshenko.credit.bank.calculator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.feshenko.credit.bank.calculator.config.LoanProperties;
import ru.feshenko.credit.bank.calculator.util.CreditCalculationConstants;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static ru.feshenko.credit.bank.calculator.util.CreditCalculationConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalculatorService {


    private final LoanProperties loanProperties;

    public BigDecimal calculateMonthlyRate(BigDecimal rate) {
        return rate.divide(BigDecimal.valueOf(PERCENT_DIVISOR), DECIMAL_SCALE, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(MONTHS_IN_YEAR), DECIMAL_SCALE, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateMonthlyPayment(BigDecimal amount, BigDecimal rate, Integer term) {

        BigDecimal monthlyRate = calculateMonthlyRate(rate);
        log.debug("Месячная ставка: {}", monthlyRate);

        BigDecimal onePlusMonthlyRate = monthlyRate.add(BigDecimal.ONE);
        BigDecimal powerFactor = onePlusMonthlyRate.pow(term);

        BigDecimal numerator = amount
                .multiply(monthlyRate)
                .multiply(powerFactor);

        BigDecimal denominator = powerFactor.subtract(BigDecimal.ONE);

        BigDecimal monthlyPayment = numerator.divide(denominator, ROUNDING_SCALE, RoundingMode.HALF_UP);
        log.debug("месячный платеж: {}", monthlyPayment);
        return monthlyPayment;
    }

    public BigDecimal calculateRate(Boolean isInsuranceEnabled, Boolean isSalaryClient) {
        BigDecimal rate = loanProperties.getBaseRate();
        if (isInsuranceEnabled) {
            rate = rate.subtract(loanProperties.getInsurance().getDiscount());
        }
        if (isSalaryClient) {
            rate = rate.subtract(loanProperties.getSalaryClient().getDiscount());
        }
        log.debug("процент после пресскоринга: {}", rate);
        return rate;
    }

    public BigDecimal calculateAmount(Boolean isInsuranceEnabled, BigDecimal amount) {
        BigDecimal resultAmount;

        if (isInsuranceEnabled) {
            resultAmount = amount.add(loanProperties.getInsurance().getPrice());
        } else {
            resultAmount = amount;
        }
        log.debug("сумма кредита после пресскоринга: {}", resultAmount);
        return resultAmount;
    }

    public BigDecimal calculatePsk(BigDecimal monthlyPayment, Integer term, BigDecimal amount) {
        BigDecimal totalPayment = monthlyPayment.multiply(BigDecimal.valueOf(term));
        BigDecimal psk = totalPayment
                .divide(amount, DECIMAL_SCALE, RoundingMode.HALF_UP)
                .subtract(BigDecimal.ONE)
                .multiply(BigDecimal.valueOf(PERCENT_DIVISOR))
                .setScale(ROUNDING_SCALE, RoundingMode.HALF_UP);
        log.debug("полная стоимость кредита: {}", psk);
        return psk;
    }
}
