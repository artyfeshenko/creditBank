package ru.feshenko.credit.bank.deal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.feshenko.credit.bank.deal.enums.EmployementPosition;
import ru.feshenko.credit.bank.deal.enums.EmploymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "employment")
public class Employment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID employmentId;

    @Enumerated(EnumType.STRING)
    private EmploymentStatus status;

    @Column(name = "employer_inn")
    private String employerInn;

    @Column(name = "salary")
    private BigDecimal salary;

    @Enumerated(EnumType.STRING)
    private EmployementPosition position;

    @Column(name = "work_experience_total")
    private int workExperienceTotal;

    @Column(name = "work_experience_current")
    private int workExperienceCurrent;

}
