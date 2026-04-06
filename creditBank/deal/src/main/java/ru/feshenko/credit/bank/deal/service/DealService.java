package ru.feshenko.credit.bank.deal.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.feshenko.credit.bank.deal.client.CalculatorClient;
import ru.feshenko.credit.bank.deal.dto.CreditDto;
import ru.feshenko.credit.bank.deal.dto.FinishRegistrationRequestDto;
import ru.feshenko.credit.bank.deal.dto.LoanOfferDto;
import ru.feshenko.credit.bank.deal.dto.LoanStatementRequestDto;
import ru.feshenko.credit.bank.deal.dto.ScoringDataDto;
import ru.feshenko.credit.bank.deal.entity.Client;
import ru.feshenko.credit.bank.deal.entity.Credit;
import ru.feshenko.credit.bank.deal.entity.Statement;
import ru.feshenko.credit.bank.deal.enums.ApplicationStatus;
import ru.feshenko.credit.bank.deal.enums.ChangeType;
import ru.feshenko.credit.bank.deal.mapper.CreditMapper;
import ru.feshenko.credit.bank.deal.mapper.ScoringMapper;
import ru.feshenko.credit.bank.deal.repository.CreditRepository;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class DealService {
    private final StatementService statementService;
    private final CreditRepository creditRepository;
    private final ClientService clientService;
    private final CalculatorClient calculatorClient;
    private final ScoringMapper scoringMapper;
    private final CreditMapper creditMapper;

    public List<LoanOfferDto> generateLoanOffers(LoanStatementRequestDto loanStatementRequestDto) {
        Client client = clientService.createClient(loanStatementRequestDto);
        client = clientService.saveClient(client);
        Statement statement = statementService.createStatement(client);
        statement = statementService.saveStatement(statement);

        List<LoanOfferDto> offers = calculatorClient.offers(loanStatementRequestDto);
        for (LoanOfferDto offer : offers) {
            offer.setStatementId(statement.getStatementId());
        }
        return offers;
    }


    public void selectLoanOffer(LoanOfferDto loanOfferDto) {
        Statement statement = statementService.findStatement(loanOfferDto.getStatementId());
        statement = statementService.updateStatus(statement, ApplicationStatus.APPROVED, ChangeType.AUTOMATIC);
        statement.setAppliedOffer(loanOfferDto);
        statementService.saveStatement(statement);
    }

    public void completeRegistrationAndCalculateCredit(FinishRegistrationRequestDto requestDto, UUID statementId) {
        Statement statement = statementService.findStatement(statementId);
        LoanOfferDto appliedOffer = statement.getAppliedOffer();
        Client client = statement.getClient();
        client = clientService.updateClient(client, requestDto);
        client = clientService.saveClient(client);

        ScoringDataDto scoringDataDto = scoringMapper.toScoringDataDto(requestDto, client, appliedOffer);

        CreditDto creditDto = calculatorClient.calculateCredit(scoringDataDto);

        Credit credit = creditMapper.toCredit(creditDto);
        creditRepository.save(credit);

        statement.setCredit(credit);
        statement = statementService.updateStatus(statement, ApplicationStatus.CC_APPROVED, ChangeType.AUTOMATIC);
        statementService.saveStatement(statement);
    }
}
