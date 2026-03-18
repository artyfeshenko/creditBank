package ru.feshenko.credit.bank.calculator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.feshenko.credit.bank.calculator.dto.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreditService {

    private final CalculatorService calculatorService;

    private final ScoringService scoringService;

    public List<LoanOfferDto> generatedOffers(LoanStatementRequestDto request) {
        List<LoanOfferDto> offers = new ArrayList<>();
        offers.add(calculateLoanOffer(request, true, true));
        offers.add(calculateLoanOffer(request, true, false));
        offers.add(calculateLoanOffer(request, false, true));
        offers.add(calculateLoanOffer(request, false, false));
        log.debug("количество кредитных предложений: {}", offers.size());
        return offers.stream().sorted(Comparator.comparing(LoanOfferDto::rate).reversed()).toList();
    }

    public LoanOfferDto calculateLoanOffer(LoanStatementRequestDto request, Boolean isInsuranceEnabled, Boolean isSalaryClient) {
        BigDecimal totalAmount = calculatorService.calculateAmount(isInsuranceEnabled, request.amount());
        BigDecimal rate = calculatorService.calculateRate(isInsuranceEnabled, isSalaryClient);
        LoanOfferDto loanOffer = new LoanOfferDto(UUID.randomUUID(), request.amount(), totalAmount, request.term(), calculatorService.calculateMonthlyPayment(totalAmount, rate, request.term()), rate, isInsuranceEnabled, isSalaryClient);
        log.debug("кредитное предложение: {}", loanOffer);
        return loanOffer;
    }

    public CreditDto scoreAndCalculateCredit(ScoringDataDto dto) {
        BigDecimal totalAmount = calculatorService.calculateAmount(dto.isInsuranceEnabled(), dto.amount());
        BigDecimal rate = scoringService.calculateScoringRate(dto);
        BigDecimal monthlyPayment = calculatorService.calculateMonthlyPayment(totalAmount, rate, dto.term());
        BigDecimal psk = calculatorService.calculatePsk(monthlyPayment, dto.term(), totalAmount);
        CreditDto creditOffer = new CreditDto(totalAmount, dto.term(), monthlyPayment, rate, psk, dto.isInsuranceEnabled(), dto.isSalaryClient(), calculatePaymentScheduleDto(totalAmount, dto.term(), rate, monthlyPayment));
        log.debug("полное кредитное предложение после скоринга:{}", creditOffer);
        return creditOffer;
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
        log.debug("размер графика платежей:{}, срок кредита:{}, ежемесячный платеж:{}, оставшая сумма кредита:{}", paymentSchedule.size(), term, monthlyPayment, remainingDebt);
        return paymentSchedule;
    }

}
