package ru.feshenko.credit.bank.statement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.feshenko.credit.bank.statement.client.DealClient;
import ru.feshenko.credit.bank.statement.dto.LoanOfferDto;
import ru.feshenko.credit.bank.statement.dto.LoanStatementRequestDto;


import java.util.List;

@Service
@RequiredArgsConstructor
public class StatementService {
    private final DealClient dealClient;
    public List<LoanOfferDto> generateOffers(LoanStatementRequestDto loanStatementRequestDto) {
        return dealClient.getLoanOffers(loanStatementRequestDto);
    }

    public void selectOffer(LoanOfferDto loanOfferDto) {
        dealClient.selectOffer(loanOfferDto);
    }
}
