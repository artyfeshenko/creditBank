package ru.feshenko.credit.bank.calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ru.feshenko.credit.bank.calculator.service.CalculatorService;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class CalculatorServiceTest {
    private CalculatorService calculatorService;

    @BeforeEach
    void setUp() {
        calculatorService = new CalculatorService();
        ReflectionTestUtils.setField(calculatorService, "baseRate", new BigDecimal("15.0"));
        ReflectionTestUtils.setField(calculatorService, "insurancePrice", new BigDecimal("50000"));
        ReflectionTestUtils.setField(calculatorService, "insuranceDiscount", new BigDecimal("3.0"));
        ReflectionTestUtils.setField(calculatorService, "salaryClientDiscount", new BigDecimal("1"));
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
