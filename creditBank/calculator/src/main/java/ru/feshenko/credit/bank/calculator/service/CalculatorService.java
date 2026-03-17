package ru.feshenko.credit.bank.calculator.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.feshenko.credit.bank.calculator.dto.LoanOfferDto;
import ru.feshenko.credit.bank.calculator.dto.LoanStatementRequestDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        offers.add(calculateLoneOffer(request, true, true));
        offers.add(calculateLoneOffer(request, true, false));
        offers.add(calculateLoneOffer(request, false, true));
        offers.add(calculateLoneOffer(request, false, false));
        return offers.stream().sorted(Comparator.comparing(LoanOfferDto::rate).reversed()).toList();
    }

    public BigDecimal calculateMonthlyPayment(BigDecimal amount, BigDecimal rate, Integer term) {
        BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP).divide(BigDecimal.valueOf(12), 8, RoundingMode.HALF_UP);
        return amount.multiply(monthlyRate).multiply(monthlyRate.add(BigDecimal.ONE).pow(term)).divide(monthlyRate.add(BigDecimal.ONE).pow(term).subtract(BigDecimal.ONE),2, RoundingMode.HALF_UP);
    }

    public LoanOfferDto calculateLoneOffer(LoanStatementRequestDto request, Boolean isInsuranceEnabled, Boolean isSalaryClient) {
        BigDecimal totalAmount = request.amount();
        BigDecimal rate = baseRate;
        if (isInsuranceEnabled == true) {
            totalAmount = totalAmount.add(insurancePrice);
            rate = rate.subtract(insuranceDiscount);
        }
        if (isSalaryClient == true) {
            rate = rate.subtract(salaryClientDiscount);
        }
        return new LoanOfferDto(UUID.randomUUID(), request.amount(), totalAmount, request.term(), calculateMonthlyPayment(totalAmount, rate, request.term()), rate, isInsuranceEnabled, isSalaryClient);
    }
}
