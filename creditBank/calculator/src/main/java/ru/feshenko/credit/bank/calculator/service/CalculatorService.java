package ru.feshenko.credit.bank.calculator.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
        return amount.multiply(monthlyRate).multiply(monthlyRate.add(BigDecimal.ONE).pow(term)).divide(monthlyRate.add(BigDecimal.ONE).pow(term).subtract(BigDecimal.ONE),2, RoundingMode.HALF_UP);
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

    public BigDecimal calculatePsk(BigDecimal monthlyPayment, Integer term, BigDecimal amount) {
        BigDecimal sp = monthlyPayment.multiply(BigDecimal.valueOf(term));
        return sp.divide(amount,10,RoundingMode.HALF_UP).subtract(BigDecimal.ONE).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
    }
}
