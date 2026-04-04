package ru.feshenko.credit.bank.deal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.feshenko.credit.bank.deal.enums.ApplicationStatus;
import ru.feshenko.credit.bank.deal.enums.ChangeType;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusHistory {
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    private LocalDateTime time;

    @Enumerated(EnumType.STRING)
    private ChangeType changeType;
}
