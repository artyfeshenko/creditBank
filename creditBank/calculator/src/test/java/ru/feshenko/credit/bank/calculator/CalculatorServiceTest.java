package ru.feshenko.credit.bank.calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.feshenko.credit.bank.calculator.config.LoanProperties;
import ru.feshenko.credit.bank.calculator.service.CalculatorService;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class CalculatorServiceTest {
    private CalculatorService calculatorService;
    private LoanProperties loanProperties;

    @BeforeEach
    void setUp() {
        loanProperties = new LoanProperties();
        loanProperties.setBaseRate(new BigDecimal("15.0"));

        LoanProperties.Insurance insurance = new LoanProperties.Insurance();
        insurance.setPrice(new BigDecimal("50000"));
        insurance.setDiscount(new BigDecimal("3.0"));
        loanProperties.setInsurance(insurance);

        LoanProperties.SalaryClient salaryClient = new LoanProperties.SalaryClient();
        salaryClient.setDiscount(new BigDecimal("1"));
        loanProperties.setSalaryClient(salaryClient);

        calculatorService = new CalculatorService(loanProperties);
    }

    @Test
    void testCalculateMonthlyPayment() {
        BigDecimal expected = BigDecimal.valueOf(34673.36);
        BigDecimal actual = calculatorService.calculateMonthlyPayment(BigDecimal.valueOf(500000), BigDecimal.valueOf(15), 16);
        assertEquals(expected, actual);
    }

    @Test
    void testCalculateRateIfInsuranceEnabled() {
        BigDecimal expected = BigDecimal.valueOf(12.0);
        BigDecimal actual = calculatorService.calculateRate(true, false);
        assertEquals(expected, actual);
    }

    @Test
    void testCalculateRateIfNotInsuranceEnabled() {
        BigDecimal expected = BigDecimal.valueOf(15.0);
        BigDecimal actual = calculatorService.calculateRate(false, false);
        assertEquals(expected, actual);
    }

    @Test
    void testCalculateRateIfSalaryClientDiscount() {
        BigDecimal expected = BigDecimal.valueOf(14.0);
        BigDecimal actual = calculatorService.calculateRate(false, true);
        assertEquals(expected, actual);
    }

    @Test
    void testCalculateRateIfNotSalaryClientDiscount() {
        BigDecimal expected = BigDecimal.valueOf(15.0);
        BigDecimal actual = calculatorService.calculateRate(false, false);
        assertEquals(expected, actual);
    }

    @Test
    void testCalculateAmountIfInsuranceEnabled() {
        BigDecimal expected = BigDecimal.valueOf(550000);
        BigDecimal actual = calculatorService.calculateAmount(true, BigDecimal.valueOf(500000));
        assertEquals(expected, actual);
    }

    @Test
    void testCalculateAmountIfNotInsuranceEnabled() {
        BigDecimal expected = BigDecimal.valueOf(500000);
        BigDecimal actual = calculatorService.calculateAmount(false, BigDecimal.valueOf(500000));
        assertEquals(expected, actual);
    }

    @Test
    void testCalculatePsk() {
        BigDecimal expected = BigDecimal.valueOf(10.95);
        BigDecimal actual = calculatorService.calculatePsk(BigDecimal.valueOf(34673.36), 16, BigDecimal.valueOf(500000));
        assertEquals(expected, actual);
    }
}
