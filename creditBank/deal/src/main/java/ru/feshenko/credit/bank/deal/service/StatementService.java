package ru.feshenko.credit.bank.deal.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.feshenko.credit.bank.deal.entity.Client;
import ru.feshenko.credit.bank.deal.entity.Statement;
import ru.feshenko.credit.bank.deal.entity.StatusHistory;
import ru.feshenko.credit.bank.deal.enums.ApplicationStatus;
import ru.feshenko.credit.bank.deal.enums.ChangeType;
import ru.feshenko.credit.bank.deal.exception.StatementNotFoundException;
import ru.feshenko.credit.bank.deal.repository.StatementRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StatementService {
    private final StatementRepository statementRepository;

    public Statement createStatement(Client client) {
        Statement statement = new Statement();
        statement.setClient(client);
        statement.setStatus(ApplicationStatus.PREAPPROVAL);
        List<StatusHistory> statusHistory = new ArrayList<>();
        statusHistory.add(new StatusHistory(ApplicationStatus.PREAPPROVAL, LocalDateTime.now(), ChangeType.AUTOMATIC));
        statement.setStatusHistory(statusHistory);
        LocalDateTime creationDate = LocalDateTime.now();
        statement.setCreationDate(creationDate);
        return statement;
    }

    public Statement saveStatement(Statement statement) {
        return statementRepository.save(statement);
    }

    public Statement findStatement(UUID id) {
        return statementRepository.findById(id).orElseThrow(() -> new StatementNotFoundException("statement with id %s not found".formatted(id)));
    }

    public Statement setStatus(Statement statement, ApplicationStatus status, ChangeType changeType) {
        statement.setStatus(status);
        StatusHistory statusHistory = new StatusHistory(status, LocalDateTime.now(), changeType);
        statement.getStatusHistory().add(statusHistory);
        return statement;
    }

    public Statement createAndSaveStatement(Client client) {
        return saveStatement(createStatement(client));
    }
}
