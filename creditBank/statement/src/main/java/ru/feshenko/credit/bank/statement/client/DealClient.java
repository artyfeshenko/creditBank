package ru.feshenko.credit.bank.statement.client;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.feshenko.credit.bank.statement.dto.LoanOfferDto;
import ru.feshenko.credit.bank.statement.dto.LoanStatementRequestDto;
import ru.feshenko.credit.bank.statement.exception.StatementNotFoundException;

import java.util.List;

@RequiredArgsConstructor
@Component
public class DealClient {
    private final RestClient restClient;
    public List<LoanOfferDto> getLoanOffers(LoanStatementRequestDto request) {
        return restClient.post()
                .uri("/statement")
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ((req, res) -> {
                    throw new StatementNotFoundException(res.getBody().toString());
                }))
                .toEntity(new ParameterizedTypeReference<List<LoanOfferDto>>() {
                }).getBody();
    }

    public void selectOffer(LoanOfferDto offer) {
        restClient.post()
                .uri("/offer/select")
                .body(offer)
                .retrieve()
                .toBodilessEntity();
    }
}
