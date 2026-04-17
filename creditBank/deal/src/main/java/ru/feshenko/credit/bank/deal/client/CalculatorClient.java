package ru.feshenko.credit.bank.deal.client;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.feshenko.credit.bank.deal.dto.CreditDto;
import ru.feshenko.credit.bank.deal.dto.LoanOfferDto;
import ru.feshenko.credit.bank.deal.dto.LoanStatementRequestDto;
import ru.feshenko.credit.bank.deal.dto.ScoringDataDto;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CalculatorClient {
    private final RestClient restClient;
    public List<LoanOfferDto> offers(LoanStatementRequestDto request) {
        return restClient.post()
                .uri("/offers")
                .body(request)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<LoanOfferDto>>() {
                }).getBody();
    }

    public CreditDto calculateCredit (ScoringDataDto scoringDataDto) {
        return restClient.post()
                .uri("/calc")
                .body(scoringDataDto)
                .retrieve()
                .toEntity(CreditDto.class)
                .getBody();
    }
}
