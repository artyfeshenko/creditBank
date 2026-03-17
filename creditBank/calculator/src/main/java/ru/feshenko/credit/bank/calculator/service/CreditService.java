package ru.feshenko.credit.bank.calculator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.feshenko.credit.bank.calculator.dto.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreditService {
    @Value("${loan.base.rate}")
    private BigDecimal baseRate;

    @Value("${loan.insurance.price}")
    private BigDecimal insurancePrice;

    @Value("${loan.insurance.discount}")
    private BigDecimal insuranceDiscount;

    @Value("${loan.salary-client.discount}")
    private BigDecimal salaryClientDiscount;

    private final CalculatorService calculatorService;

    private final ScoringService scoringService;

    public List<LoanOfferDto> generatedOffers(LoanStatementRequestDto request) {
        List<LoanOfferDto> offers = new ArrayList<>();
        offers.add(calculateLoanOffer(request, true, true));
        offers.add(calculateLoanOffer(request, true, false));
        offers.add(calculateLoanOffer(request, false, true));
        offers.add(calculateLoanOffer(request, false, false));
        return offers.stream().sorted(Comparator.comparing(LoanOfferDto::rate).reversed()).toList();
    }

    public LoanOfferDto calculateLoanOffer(LoanStatementRequestDto request, Boolean isInsuranceEnabled, Boolean isSalaryClient) {
        BigDecimal totalAmount = calculatorService.calculateAmount(isInsuranceEnabled, request.amount());
        BigDecimal rate = calculatorService.calculateRate(isInsuranceEnabled, isSalaryClient);

        return new LoanOfferDto(UUID.randomUUID(), request.amount(), totalAmount, request.term(), calculatorService.calculateMonthlyPayment(totalAmount, rate, request.term()), rate, isInsuranceEnabled, isSalaryClient);
    }

    public CreditDto scoreAndCalculateCredit(ScoringDataDto dto) {
        BigDecimal totalAmount = calculatorService.calculateAmount(dto.isInsuranceEnabled(), dto.amount());
        BigDecimal rate = scoringService.calculateScoringRate(dto);
        BigDecimal monthlyPayment = calculatorService.calculateMonthlyPayment(totalAmount, rate, dto.term());
        BigDecimal psk = calculatorService.calculatePsk(monthlyPayment, dto.term(), totalAmount);
        return new CreditDto(totalAmount, dto.term(), monthlyPayment, rate, psk, dto.isInsuranceEnabled(), dto.isSalaryClient(), calculatePaymentScheduleDto(totalAmount, dto.term(), rate, monthlyPayment));
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
