package ru.feshenko.credit.bank.calculator.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.feshenko.credit.bank.calculator.dto.CreditDto;
import ru.feshenko.credit.bank.calculator.dto.LoanOfferDto;
import ru.feshenko.credit.bank.calculator.dto.LoanStatementRequestDto;
import ru.feshenko.credit.bank.calculator.dto.ScoringDataDto;
import ru.feshenko.credit.bank.calculator.service.CalculatorService;

import java.util.List;

@RestController
@RequestMapping("/calculator")
@RequiredArgsConstructor
public class CalculatorController {

    private final CalculatorService calculatorService;

    @PostMapping("/offers")
    public ResponseEntity<List<LoanOfferDto>> getLoanOffers(@Valid @RequestBody LoanStatementRequestDto loanStatementRequestDto) {
        return ResponseEntity.ok(calculatorService.generatedOffers(loanStatementRequestDto));
    }

    @PostMapping("/calc")
    public CreditDto scoreAndCalculate(@RequestBody ScoringDataDto scoringDataDto) {
        return null;
    }
}
