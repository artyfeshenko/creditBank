package ru.feshenko.credit.bank.calculator.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AdultValidation.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Adult {
    String message() default "you have not reached the age of majority";
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
