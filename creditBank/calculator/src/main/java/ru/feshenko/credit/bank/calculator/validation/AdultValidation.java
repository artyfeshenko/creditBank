package ru.feshenko.credit.bank.calculator.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;

public class AdultValidation implements ConstraintValidator<Adult, LocalDate> {
    @Override
    public boolean isValid(LocalDate birthdate, ConstraintValidatorContext constraintValidatorContext) {
        return Period.between(birthdate, LocalDate.now()).getYears() >= 18;
    }
}
