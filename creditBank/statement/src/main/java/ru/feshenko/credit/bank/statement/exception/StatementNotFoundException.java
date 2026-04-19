package ru.feshenko.credit.bank.statement.exception;

public class StatementNotFoundException extends RuntimeException {
    public StatementNotFoundException(String message) {
        super(message);
    }
}
