package ru.feshenko.credit.bank.calculator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
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


    public BigDecimal calculateMonthlyPayment(BigDecimal amount, BigDecimal rate, Integer term) {
        BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP).divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        log.debug("Месячная ставка: {}", monthlyRate);
        BigDecimal monthlyPayment = amount.multiply(monthlyRate).multiply(monthlyRate.add(BigDecimal.ONE).pow(term)).divide(monthlyRate.add(BigDecimal.ONE).pow(term).subtract(BigDecimal.ONE),2, RoundingMode.HALF_UP);
        log.debug("месячный платеж: {}", monthlyPayment);
        return monthlyPayment;
    }

    public BigDecimal calculateRate(Boolean isInsuranceEnabled, Boolean isSalaryClient) {
        BigDecimal rate = baseRate;
        if (isInsuranceEnabled == true) {
            rate = rate.subtract(insuranceDiscount);
        }
        if (isSalaryClient == true) {
            rate = rate.subtract(salaryClientDiscount);
        }
        log.debug("процент после пресскоринга: {}", rate);
        return rate;
    }

    public BigDecimal calculateAmount(Boolean isInsuranceEnabled, BigDecimal amount) {
        if (isInsuranceEnabled == true) {
            return amount.add(insurancePrice);
        }
        log.debug("сумма кредита после пресскоринга: {}", amount);
        return amount;
    }

    public BigDecimal calculatePsk(BigDecimal monthlyPayment, Integer term, BigDecimal amount) {
        BigDecimal sp = monthlyPayment.multiply(BigDecimal.valueOf(term));
        BigDecimal psk = sp.divide(amount,10,RoundingMode.HALF_UP).subtract(BigDecimal.ONE).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
        log.debug("полная стоимость кредита: {}", psk);
        return psk;
    }
}
