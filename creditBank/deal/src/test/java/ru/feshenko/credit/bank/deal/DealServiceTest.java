package ru.feshenko.credit.bank.deal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.feshenko.credit.bank.deal.client.CalculatorClient;
import ru.feshenko.credit.bank.deal.dto.*;
import ru.feshenko.credit.bank.deal.entity.Client;
import ru.feshenko.credit.bank.deal.entity.Credit;
import ru.feshenko.credit.bank.deal.entity.Passport;
import ru.feshenko.credit.bank.deal.entity.Statement;
import ru.feshenko.credit.bank.deal.enums.*;
import ru.feshenko.credit.bank.deal.exception.StatementNotFoundException;
import ru.feshenko.credit.bank.deal.mapper.CreditMapper;
import ru.feshenko.credit.bank.deal.mapper.ScoringMapper;
import ru.feshenko.credit.bank.deal.repository.CreditRepository;
import ru.feshenko.credit.bank.deal.service.ClientService;
import ru.feshenko.credit.bank.deal.service.DealService;
import ru.feshenko.credit.bank.deal.service.StatementService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DealServiceTest {
    private DealService dealService;
    private StatementService statementService;
    private CreditRepository creditRepository;
    private ClientService clientService;
    private CalculatorClient calculatorClient;
    private ScoringMapper scoringMapper;
    private CreditMapper creditMapper;

    private LoanStatementRequestDto buildLoanStatementRequestDto() {
        return new LoanStatementRequestDto(
                BigDecimal.valueOf(500000),
                6,
                "sasha",
                "mironov",
                "mickhailovich",
                "sano@yandex.ru",
                LocalDate.of(2003, 11, 1),
                "4202",
                "168032"
        );
    }

    private FinishRegistrationRequestDto buildFinishRegistrationRequestDto() {
        return new FinishRegistrationRequestDto(
                Gender.MALE,
                MaritalStatus.MARRIED,
                2,
                LocalDate.of(2003, 11, 1),
                "mvd russia",
                new EmploymentDto(EmploymentStatus.SELF_EMPLOYED, "13457836423",
                        BigDecimal.valueOf(50000), EmployementPosition.WORKER, 10, 4 ),
                "40817810200000000001"
        );
    }

    private Client buildClient() {
        Client client = new Client();
        client.setFirstName("sasha");
        client.setLastName("mironov");
        client.setMiddleName("mickhailovich");
        client.setEmail("sano@yandex.ru");
        client.setBirthDate(LocalDate.of(2003, 11, 1));
        client.setPassport(new Passport(null, "2418", "159580", null, null));
        return client;
    }

    private Statement buildStatement() {
        Client client = buildClient();
        Statement statement = new Statement();
        statement.setStatementId(UUID.randomUUID());
        statement.setClient(client);
        statement.setStatus(ApplicationStatus.PREAPPROVAL);
        return statement;
    }

    private LoanOfferDto buildLoanOfferDto(UUID statementId) {
        return new LoanOfferDto(
                statementId, BigDecimal.valueOf(5000000),
                BigDecimal.valueOf(1000000),
                17,
                BigDecimal.valueOf(87000),
                BigDecimal.valueOf(1044000),
                false,
                false
        );
    }

    private List<LoanOfferDto> buildLoanOffersList(UUID statementId) {
        return List.of(
                buildLoanOfferDto(statementId),
                buildLoanOfferDto(statementId),
                buildLoanOfferDto(statementId),
                buildLoanOfferDto(statementId)
        );
    }

    private CreditDto buildCreditDto() {
        return new CreditDto(
                BigDecimal.valueOf(500000),
                6,
                BigDecimal.valueOf(90269.66),
                BigDecimal.valueOf(28),
                BigDecimal.valueOf(8.32),
                false,
                false,
                null
        );
    }

    private Credit buildCredit() {
        Credit credit = new Credit();
        credit.setCreditId(UUID.randomUUID());
        credit.setAmount(BigDecimal.valueOf(500000));
        credit.setTerm(6);
        return credit;
    }

    private ScoringDataDto buildScoringDataDto() {
        return new ScoringDataDto(
                BigDecimal.valueOf(500000),
                6,
                "sasha",
                "mironov",
                "mickhailovich",
                null,
                LocalDate.of(2003, 11, 1),
                "4202",
                "168138",
                LocalDate.of(2017, 2, 2),
                "ROMADIN MVD",
                null,
                5,
                new EmploymentDto(EmploymentStatus.SELF_EMPLOYED, "13457836423",
                        BigDecimal.valueOf(50000), EmployementPosition.WORKER, 10, 4 ),
                "123421432",
                false,
                false
        );
    }

    @BeforeEach
    void setUp() {
        statementService = mock(StatementService.class);
        creditRepository = mock(CreditRepository.class);
        clientService = mock(ClientService.class);
        calculatorClient = mock(CalculatorClient.class);
        scoringMapper = mock(ScoringMapper.class);
        creditMapper = mock(CreditMapper.class);

        dealService = new DealService(
                statementService,
                creditRepository,
                clientService,
                calculatorClient,
                scoringMapper,
                creditMapper
        );
    }

    @Test
    void testGenerateLoanOffers() {
        LoanStatementRequestDto requestDto = buildLoanStatementRequestDto();
        Client client = buildClient();
        Statement statement = buildStatement();
        UUID statementId = statement.getStatementId();
        List<LoanOfferDto> expectedOffers = buildLoanOffersList(statementId);

        when(clientService.createClient(requestDto)).thenReturn(client);
        when(clientService.saveClient(client)).thenReturn(client);
        when(statementService.createStatement(client)).thenReturn(statement);
        when(statementService.saveStatement(statement)).thenReturn(statement);
        when(calculatorClient.offers(requestDto)).thenReturn(expectedOffers);

        List<LoanOfferDto> actualOffers = dealService.generateLoanOffers(requestDto);

        assertNotNull(actualOffers);
        assertEquals(4, actualOffers.size());
        for (LoanOfferDto offer : actualOffers) {
            assertEquals(statementId, offer.getStatementId());
        }

        verify(clientService, times(1)).createClient(requestDto);
        verify(clientService, times(1)).saveClient(client);
        verify(statementService, times(1)).createStatement(client);
        verify(statementService, times(1)).saveStatement(statement);
        verify(calculatorClient, times(1)).offers(requestDto);
    }

    @Test
    void testSelectLoanOffer() {
        UUID statementId = UUID.randomUUID();
        LoanOfferDto loanOfferDto = buildLoanOfferDto(statementId);
        Statement statement = buildStatement();

        when(statementService.findStatement(statementId)).thenReturn(statement);
        when(statementService.updateStatus(statement, ApplicationStatus.APPROVED, ChangeType.AUTOMATIC))
                .thenReturn(statement);
        when(statementService.saveStatement(statement)).thenReturn(statement);

        dealService.selectLoanOffer(loanOfferDto);

        assertEquals(loanOfferDto, statement.getAppliedOffer());
        verify(statementService, times(1)).findStatement(statementId);
        verify(statementService, times(1)).updateStatus(statement, ApplicationStatus.APPROVED, ChangeType.AUTOMATIC);
        verify(statementService, times(1)).saveStatement(statement);
    }

    @Test
    void testCompleteRegistrationAndCalculateCredit() {
        UUID statementId = UUID.randomUUID();
        FinishRegistrationRequestDto requestDto = buildFinishRegistrationRequestDto();
        Statement statement = buildStatement();
        statement.setStatementId(statementId);
        Client client = statement.getClient();
        LoanOfferDto appliedOffer = buildLoanOfferDto(statementId);
        statement.setAppliedOffer(appliedOffer);

        ScoringDataDto scoringDataDto = buildScoringDataDto();
        CreditDto creditDto = buildCreditDto();
        Credit credit = buildCredit();

        when(statementService.findStatement(statementId)).thenReturn(statement);
        when(clientService.updateClient(client, requestDto)).thenReturn(client);
        when(clientService.saveClient(client)).thenReturn(client);
        when(scoringMapper.toScoringDataDto(requestDto, client, appliedOffer)).thenReturn(scoringDataDto);
        when(calculatorClient.calculateCredit(scoringDataDto)).thenReturn(creditDto);
        when(creditMapper.toCredit(creditDto)).thenReturn(credit);
        when(creditRepository.save(credit)).thenReturn(credit);
        when(statementService.updateStatus(statement, ApplicationStatus.CC_APPROVED, ChangeType.AUTOMATIC))
                .thenReturn(statement);
        when(statementService.saveStatement(statement)).thenReturn(statement);

        dealService.completeRegistrationAndCalculateCredit(requestDto, statementId);

        assertEquals(credit, statement.getCredit());
        verify(statementService, times(1)).findStatement(statementId);
        verify(clientService, times(1)).updateClient(client, requestDto);
        verify(clientService, times(1)).saveClient(client);
        verify(scoringMapper, times(1)).toScoringDataDto(requestDto, client, appliedOffer);
        verify(calculatorClient, times(1)).calculateCredit(scoringDataDto);
        verify(creditMapper, times(1)).toCredit(creditDto);
        verify(creditRepository, times(1)).save(credit);
        verify(statementService, times(1)).updateStatus(statement, ApplicationStatus.CC_APPROVED, ChangeType.AUTOMATIC);
        verify(statementService, times(1)).saveStatement(statement);
    }

    @Test
    void testGenerateLoanOffersWhenCalculatorReturnsEmptyList() {
        LoanStatementRequestDto requestDto = buildLoanStatementRequestDto();
        Client client = buildClient();
        Statement statement = buildStatement();

        when(clientService.createClient(requestDto)).thenReturn(client);
        when(clientService.saveClient(client)).thenReturn(client);
        when(statementService.createStatement(client)).thenReturn(statement);
        when(statementService.saveStatement(statement)).thenReturn(statement);
        when(calculatorClient.offers(requestDto)).thenReturn(List.of());

        List<LoanOfferDto> actualOffers = dealService.generateLoanOffers(requestDto);

        assertNotNull(actualOffers);
        assertEquals(0, actualOffers.size());
    }

    @Test
    void testSelectLoanOfferWhenStatementNotFound() {
        UUID statementId = UUID.randomUUID();
        LoanOfferDto loanOfferDto = buildLoanOfferDto(statementId);

        when(statementService.findStatement(statementId)).thenThrow(new StatementNotFoundException("Statement not found"));

        assertThrows(StatementNotFoundException.class, () -> {
            dealService.selectLoanOffer(loanOfferDto);
        });
    }
}
