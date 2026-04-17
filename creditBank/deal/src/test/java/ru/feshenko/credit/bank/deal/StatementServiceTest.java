package ru.feshenko.credit.bank.deal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.feshenko.credit.bank.deal.entity.Client;
import ru.feshenko.credit.bank.deal.entity.Statement;
import ru.feshenko.credit.bank.deal.entity.StatusHistory;
import ru.feshenko.credit.bank.deal.enums.ApplicationStatus;
import ru.feshenko.credit.bank.deal.enums.ChangeType;
import ru.feshenko.credit.bank.deal.exception.StatementNotFoundException;
import ru.feshenko.credit.bank.deal.repository.StatementRepository;
import ru.feshenko.credit.bank.deal.service.StatementService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StatementServiceTest {
    private StatementService statementService;
    private StatementRepository statementRepository;

    private Client buildClient() {
        Client client = new Client();
        client.setFirstName("sasha");
        client.setLastName("mironov");
        return client;
    }

    private Statement buildStatement() {
        Client client = buildClient();
        Statement statement = new Statement();
        statement.setStatementId(UUID.randomUUID());
        statement.setClient(client);
        statement.setStatus(ApplicationStatus.PREAPPROVAL);
        statement.setCreationDate(LocalDateTime.now());
        statement.setStatusHistory(new ArrayList<>());
        return statement;
    }

    @BeforeEach
    void setUp() {
        statementRepository = mock(StatementRepository.class);
        statementService = new StatementService(statementRepository);
    }

    @Test
    void testCreateStatement() {
        Client client = buildClient();

        Statement statement = statementService.createStatement(client);

        assertNotNull(statement);
        assertEquals(client, statement.getClient());
        assertEquals(ApplicationStatus.PREAPPROVAL, statement.getStatus());
        assertNotNull(statement.getCreationDate());
        assertNotNull(statement.getStatusHistory());
        assertEquals(1, statement.getStatusHistory().size());

        StatusHistory history = statement.getStatusHistory().get(0);
        assertEquals(ApplicationStatus.PREAPPROVAL, history.getStatus());
        assertEquals(ChangeType.AUTOMATIC, history.getChangeType());
    }

    @Test
    void testSaveStatement() {
        Statement statementToSave = buildStatement();
        Statement savedStatement = buildStatement();

        when(statementRepository.save(statementToSave)).thenReturn(savedStatement);

        Statement result = statementService.saveStatement(statementToSave);

        assertNotNull(result);
        assertEquals(savedStatement.getStatementId(), result.getStatementId());
        verify(statementRepository, times(1)).save(statementToSave);
    }

    @Test
    void testFindStatementWhenExists() {
        Statement expectedStatement = buildStatement();
        UUID statementId = expectedStatement.getStatementId();

        when(statementRepository.findById(statementId)).thenReturn(Optional.of(expectedStatement));

        Statement actualStatement = statementService.findStatement(statementId);

        assertNotNull(actualStatement);
        assertEquals(expectedStatement.getStatementId(), actualStatement.getStatementId());
        verify(statementRepository, times(1)).findById(statementId);
    }

    @Test
    void testFindStatementWhenNotFound() {
        UUID nonExistentId = UUID.randomUUID();

        when(statementRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(StatementNotFoundException.class, () -> {
            statementService.findStatement(nonExistentId);
        });
        verify(statementRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void testUpdateStatus() {
        Statement statement = buildStatement();
        int initialHistorySize = statement.getStatusHistory().size();

        Statement updatedStatement = statementService.updateStatus(
                statement,
                ApplicationStatus.APPROVED,
                ChangeType.MANUAL
        );

        assertEquals(ApplicationStatus.APPROVED, updatedStatement.getStatus());
        assertEquals(initialHistorySize + 1, updatedStatement.getStatusHistory().size());

        StatusHistory lastHistory = updatedStatement.getStatusHistory().get(updatedStatement.getStatusHistory().size() - 1);
        assertEquals(ApplicationStatus.APPROVED, lastHistory.getStatus());
        assertEquals(ChangeType.MANUAL, lastHistory.getChangeType());
    }

}
