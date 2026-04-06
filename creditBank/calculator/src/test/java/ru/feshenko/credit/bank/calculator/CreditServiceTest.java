package ru.feshenko.credit.bank.calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.feshenko.credit.bank.calculator.dto.CreditDto;
import ru.feshenko.credit.bank.calculator.dto.EmploymentDto;
import ru.feshenko.credit.bank.calculator.dto.LoanOfferDto;
import ru.feshenko.credit.bank.calculator.dto.LoanStatementRequestDto;
import ru.feshenko.credit.bank.calculator.dto.PaymentScheduleElementDto;
import ru.feshenko.credit.bank.calculator.dto.ScoringDataDto;
import ru.feshenko.credit.bank.calculator.enums.EmploymentStatusEnum;
import ru.feshenko.credit.bank.calculator.enums.GenderEnum;
import ru.feshenko.credit.bank.calculator.enums.MaritalStatusEnum;
import ru.feshenko.credit.bank.calculator.enums.PositionEnum;
import ru.feshenko.credit.bank.calculator.service.CalculatorService;
import ru.feshenko.credit.bank.calculator.service.CreditService;
import ru.feshenko.credit.bank.calculator.service.ScoringService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreditServiceTest {
    private CreditService creditService;
    private ScoringService scoringService;
    private CalculatorService calculatorService;

    private LoanStatementRequestDto buildRequestDto() {
        return new LoanStatementRequestDto(BigDecimal.valueOf(500000), 6, "sasha", "mironov", "mickhailovich", "sano@yandex.ru", LocalDate.of(2003,11,1), "4202", "168032");
    }

    private List<PaymentScheduleElementDto> buildPaymentSchedule() {
        return List.of(
                new PaymentScheduleElementDto(1, LocalDate.now().plusMonths(1), BigDecimal.valueOf(90269.66), BigDecimal.valueOf(11666.67), BigDecimal.valueOf(78602.99), BigDecimal.valueOf(421397.01)),
                new PaymentScheduleElementDto(2, LocalDate.now().plusMonths(2), BigDecimal.valueOf(90269.66), BigDecimal.valueOf(9832.601).setScale(2, RoundingMode.HALF_UP), BigDecimal.valueOf(80437.06), BigDecimal.valueOf(340959.95)),
                new PaymentScheduleElementDto(3, LocalDate.now().plusMonths(3), BigDecimal.valueOf(90269.66), BigDecimal.valueOf(7955.73), BigDecimal.valueOf(82313.93), BigDecimal.valueOf(258646.02)),
                new PaymentScheduleElementDto(4, LocalDate.now().plusMonths(4), BigDecimal.valueOf(90269.66), BigDecimal.valueOf(6035.07), BigDecimal.valueOf(84234.59), BigDecimal.valueOf(174411.43)),
                new PaymentScheduleElementDto(5, LocalDate.now().plusMonths(5), BigDecimal.valueOf(90269.66), BigDecimal.valueOf(4069.601).setScale(2, RoundingMode.HALF_UP), BigDecimal.valueOf(86200.06), BigDecimal.valueOf(88211.37)),
                new PaymentScheduleElementDto(6, LocalDate.now().plusMonths(6), BigDecimal.valueOf(90269.66), BigDecimal.valueOf(2058.27), BigDecimal.valueOf(88211.39), BigDecimal.valueOf(0))
        );
    }

    private ScoringDataDto buildScoringDataDto() {
        return new ScoringDataDto(BigDecimal.valueOf(500000), 6, "sasha", "mironov", "mickhailovich", GenderEnum.NON_BINARY, LocalDate.of(2003,11,1),
                "4202", "168138", LocalDate.of(2017,2,2), "ROMADIN MVD", MaritalStatusEnum.DIVORCED, 5,
                new EmploymentDto(EmploymentStatusEnum.SELF_EMPLOYED, "12341345", BigDecimal.valueOf(60000), PositionEnum.TOP_MANAGER, 20, 5), "sdfsdg", false, false);
    }

    @BeforeEach
    void setUp() {
        scoringService = mock(ScoringService.class);
        calculatorService = mock(CalculatorService.class);
        creditService =  new CreditService(calculatorService, scoringService);
        when(calculatorService.calculateAmount(true, BigDecimal.valueOf(500000))).thenReturn(BigDecimal.valueOf(550000));
        when(calculatorService.calculateRate(true, true)).thenReturn(BigDecimal.valueOf(11));
        when(calculatorService.calculateMonthlyPayment(BigDecimal.valueOf(550000), BigDecimal.valueOf(11), 6)).thenReturn(BigDecimal.valueOf(94630));
        when(calculatorService.calculateAmount(true, BigDecimal.valueOf(500000))).thenReturn(BigDecimal.valueOf(550000));
        when(calculatorService.calculateRate(true, false)).thenReturn(BigDecimal.valueOf(12));
        when(calculatorService.calculateMonthlyPayment(BigDecimal.valueOf(550000), BigDecimal.valueOf(12), 6)).thenReturn(BigDecimal.valueOf(94902));
        when(calculatorService.calculateAmount(false, BigDecimal.valueOf(500000))).thenReturn(BigDecimal.valueOf(500000));
        when(calculatorService.calculateRate(false, true)).thenReturn(BigDecimal.valueOf(14));
        when(calculatorService.calculateMonthlyPayment(BigDecimal.valueOf(500000), BigDecimal.valueOf(14), 6)).thenReturn(BigDecimal.valueOf(86769));
        when(calculatorService.calculateAmount(false, BigDecimal.valueOf(500000))).thenReturn(BigDecimal.valueOf(500000));
        when(calculatorService.calculateRate(false, false)).thenReturn(BigDecimal.valueOf(15));
        when(calculatorService.calculateMonthlyPayment(BigDecimal.valueOf(500000), BigDecimal.valueOf(15), 6)).thenReturn(BigDecimal.valueOf(87017));
        when(calculatorService.calculatePsk(BigDecimal.valueOf(90269.66), 6, BigDecimal.valueOf(500000))).thenReturn(BigDecimal.valueOf(8.32));
        when(calculatorService.calculateMonthlyPayment(BigDecimal.valueOf(500000), BigDecimal.valueOf(28), 6)).thenReturn(BigDecimal.valueOf(90269.66));
    }

    @Test
    void testCalculateLoanOfferInsuranceEnabledSalaryClient() {
        BigDecimal expectedTotalAmount = BigDecimal.valueOf(550000);
        BigDecimal expectedMonthlyPayment = BigDecimal.valueOf(94630);
        BigDecimal expectedRate = BigDecimal.valueOf(11);
        LoanOfferDto actual = creditService.calculateLoanOffer(buildRequestDto(), true, true);
        assertEquals(expectedTotalAmount, actual.totalAmount());
        assertEquals(expectedMonthlyPayment, actual.monthlyPayment());
        assertEquals(expectedRate, actual.rate());
    }

    @Test
    void testCalculateLoanOfferInsuranceEnabledNotSalaryClient() {
        BigDecimal expectedTotalAmount = BigDecimal.valueOf(550000);
        BigDecimal expectedMonthlyPayment = BigDecimal.valueOf(94902);
        BigDecimal expectedRate = BigDecimal.valueOf(12);
        LoanOfferDto actual = creditService.calculateLoanOffer(buildRequestDto(), true, false);
        assertEquals(expectedTotalAmount, actual.totalAmount());
        assertEquals(expectedMonthlyPayment, actual.monthlyPayment());
        assertEquals(expectedRate, actual.rate());
    }

    @Test
    void testCalculateLoanOfferNotInsuranceEnabledSalaryClient() {
        BigDecimal expectedTotalAmount = BigDecimal.valueOf(500000);
        BigDecimal expectedMonthlyPayment = BigDecimal.valueOf(86769);
        BigDecimal expectedRate = BigDecimal.valueOf(14);
        LoanOfferDto actual = creditService.calculateLoanOffer(buildRequestDto(), false, true);
        assertEquals(expectedTotalAmount, actual.totalAmount());
        assertEquals(expectedMonthlyPayment, actual.monthlyPayment());
        assertEquals(expectedRate, actual.rate());
    }

    @Test
    void testCalculateLoanOfferNotInsuranceEnabledNotSalaryClient() {
        BigDecimal expectedTotalAmount = BigDecimal.valueOf(500000);
        BigDecimal expectedMonthlyPayment = BigDecimal.valueOf(87017);
        BigDecimal expectedRate = BigDecimal.valueOf(15);
        LoanOfferDto actual = creditService.calculateLoanOffer(buildRequestDto(), false, false);
        assertEquals(expectedTotalAmount, actual.totalAmount());
        assertEquals(expectedMonthlyPayment, actual.monthlyPayment());
        assertEquals(expectedRate, actual.rate());
    }

    @Test
    void testGenerateOffers() {
        List<LoanOfferDto> actual = creditService.generateOffers(buildRequestDto());
        assertEquals(4, actual.size());
    }

    @Test
    void testCalculatePaymentScheduleDto() {
        List<PaymentScheduleElementDto> expected = buildPaymentSchedule();
        List<PaymentScheduleElementDto> actual = creditService.calculatePaymentScheduleDto(BigDecimal.valueOf(500000), 6, BigDecimal.valueOf(28), BigDecimal.valueOf(90269.66));
        assertEquals(expected, actual);
    }

    @Test
    void testScoreAndCalculateCredit() {
        ScoringDataDto request = buildScoringDataDto();
        when(scoringService.calculateScoringRate(request)).thenReturn(BigDecimal.valueOf(28));
        CreditDto expected = new CreditDto(BigDecimal.valueOf(500000), 6, BigDecimal.valueOf(90269.66), BigDecimal.valueOf(28), BigDecimal.valueOf(8.32), false, false, buildPaymentSchedule());
        CreditDto actual = creditService.scoreAndCalculateCredit(request);
        assertEquals(expected, actual);
    }

}
