package ru.feshenko.credit.bank.deal.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.feshenko.credit.bank.deal.dto.*;
import ru.feshenko.credit.bank.deal.entity.*;
import ru.feshenko.credit.bank.deal.enums.ApplicationStatus;
import ru.feshenko.credit.bank.deal.enums.ChangeType;
import ru.feshenko.credit.bank.deal.enums.CreditStatus;
import ru.feshenko.credit.bank.deal.repository.ClientRepository;
import ru.feshenko.credit.bank.deal.repository.CreditRepository;
import ru.feshenko.credit.bank.deal.repository.StatementRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class DealService {
    private final StatementRepository statementRepository;
    private final ClientRepository clientRepository;
    private final CreditRepository creditRepository;
    RestClient restClient = RestClient.create();

    public Client createClient(LoanStatementRequestDto loanStatementRequestDto) {
        Client client = new Client();
        client.setFirstName(loanStatementRequestDto.firstName());
        client.setLastName(loanStatementRequestDto.lastName());
        client.setMiddleName(loanStatementRequestDto.middleName());
        client.setEmail(loanStatementRequestDto.email());
        client.setBirthDate(loanStatementRequestDto.birthdate());
        Passport pasport = new Passport();
        pasport.setNumber(loanStatementRequestDto.passportNumber());
        pasport.setSeries(loanStatementRequestDto.passportSeries());
        client.setPassport(pasport);
        return client;
    }

    public Statement createStatement(Client client) {
        Statement statement = new Statement();
        statement.setClient(client);
        statement.setStatus(ApplicationStatus.PREAPPROVAL);
        List <StatusHistory> statusHistory = new ArrayList<>();
        statusHistory.add(new StatusHistory(ApplicationStatus.PREAPPROVAL, LocalDateTime.now(), ChangeType.AUTOMATIC));
        statement.setStatusHistory(statusHistory);
        LocalDateTime creationDate = LocalDateTime.now();
        statement.setCreationDate(creationDate);
        return statement;
    }



    public List<LoanOfferDto> generateLoanOffers(LoanStatementRequestDto loanStatementRequestDto) {
        Client client = createClient(loanStatementRequestDto);
        clientRepository.save(client);
        Statement statement = createStatement(client);
        statementRepository.save(statement);
        List <LoanOfferDto> offers = restClient.post()
                .uri("http://localhost:8080/calculator/offers")
                .body(loanStatementRequestDto)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<LoanOfferDto>>() {
                }).getBody();
        for (LoanOfferDto offer : offers) {
            offer.setStatementId(statement.getStatementId());
        }
        return offers;
    }


    public void selectLoanOffer(LoanOfferDto loanOfferDto) {
        Statement statement = statementRepository.findById(loanOfferDto.getStatementId()).orElseThrow();
        statement.setStatus(ApplicationStatus.APPROVED);
        StatusHistory statusHistory = new StatusHistory(ApplicationStatus.APPROVED, LocalDateTime.now(), ChangeType.AUTOMATIC);
        statement.getStatusHistory().add(statusHistory);
        statement.setAppliedOffer(loanOfferDto);
        statementRepository.save(statement);
    }

    public void completeRegistrationAndCalculateCredit(FinishRegistrationRequestDto finishRegistrationRequestDto, String statementId) {
        Statement statement = statementRepository.findById(UUID.fromString(statementId)).orElseThrow();
        ScoringDataDto scoringDataDto = new ScoringDataDto(statement.getAppliedOffer().getRequestedAmount(),
                statement.getAppliedOffer().getTerm(), statement.getClient().getFirstName(),
                statement.getClient().getLastName(), statement.getClient().getMiddleName(),
                finishRegistrationRequestDto.gender(),  statement.getClient().getBirthDate(),
                statement.getClient().getPassport().getSeries(), statement.getClient().getPassport().getNumber(),
                finishRegistrationRequestDto.passportIssueDate(), finishRegistrationRequestDto.passportIssueBranch(),
                finishRegistrationRequestDto.maritalStatus(), finishRegistrationRequestDto.dependentAmount(),
                finishRegistrationRequestDto.employment(), finishRegistrationRequestDto.accountNumber(),
                statement.getAppliedOffer().getIsInsuranceEnabled(), statement.getAppliedOffer().getIsSalaryClient());

        CreditDto creditDto   = restClient.post()
                .uri("http://localhost:8080/calculator/calc")
                .body(scoringDataDto)
                .retrieve()
                .toEntity(CreditDto.class)
                .getBody();

        Credit credit = new Credit();
        credit.setAmount(creditDto.amount());
        credit.setTerm(creditDto.term());
        credit.setMonthlyPayment(creditDto.monthlyPayment());
        credit.setRate(creditDto.rate());
        credit.setPsk(creditDto.psk());
        credit.setPaymentSchedule(creditDto.paymentSchedule());
        credit.setIsInsuranceEnabled(creditDto.isInsuranceEnabled());
        credit.setIsSalaryClient(creditDto.isSalaryClient());
        credit.setCreditStatus(CreditStatus.CALCULATED);
        creditRepository.save(credit);

        statement.setCredit(credit);
        statement.setStatus(ApplicationStatus.CALCULATED);

        StatusHistory statusHistory = new StatusHistory(ApplicationStatus.CALCULATED, LocalDateTime.now(), ChangeType.AUTOMATIC);
        if (statement.getStatusHistory() == null) {
            statement.setStatusHistory(new ArrayList<>());
        }
        statement.getStatusHistory().add(statusHistory);

        statementRepository.save(statement);
    }
}
