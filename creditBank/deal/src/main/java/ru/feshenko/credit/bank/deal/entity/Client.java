package ru.feshenko.credit.bank.deal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.feshenko.credit.bank.deal.enums.Gender;
import ru.feshenko.credit.bank.deal.enums.MaritalStatus;


import java.time.LocalDate;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "client")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID clientId;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "email")
    private String email;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus;

    @Column(name = "dependent_amount")
    private Integer dependentAmount;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "passport_id")
    private Passport passport;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "employment_id")
    private Employment employment;

    @Column(name = "account_number")
    private String accountNumber;
}
