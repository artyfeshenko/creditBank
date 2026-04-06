package ru.feshenko.credit.bank.deal.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.feshenko.credit.bank.deal.dto.FinishRegistrationRequestDto;
import ru.feshenko.credit.bank.deal.dto.LoanOfferDto;
import ru.feshenko.credit.bank.deal.dto.LoanStatementRequestDto;
import ru.feshenko.credit.bank.deal.service.DealService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/deal")
@RequiredArgsConstructor
public class DealController {

    private final DealService dealService;

    @PostMapping("/statement")
    public List<LoanOfferDto> calculateLoanTerms(@RequestBody LoanStatementRequestDto loanStatementRequestDto) {
        return dealService.generateLoanOffers(loanStatementRequestDto);
    }

    @PostMapping("/offer/select")
    public void selectOffer(@RequestBody  LoanOfferDto loanOfferDto) {
        dealService.selectLoanOffer(loanOfferDto);
    }

    @PostMapping("/calculate/{statementId}")
    public void fullLoanCalculation(@RequestBody FinishRegistrationRequestDto finishRegistrationRequestDto, @PathVariable String statementId) {
        dealService.completeRegistrationAndCalculateCredit(finishRegistrationRequestDto, UUID.fromString(statementId));
    }
}
