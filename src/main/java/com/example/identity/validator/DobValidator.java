package com.example.identity.validator;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DobValidator implements ConstraintValidator<DobContraint, LocalDate> {

    private int min;

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {

        if (Objects.isNull(localDate)) {
            return true;
        }
        long year = ChronoUnit.YEARS.between(localDate, LocalDate.now());
        return year >= this.min;
    }

    @Override
    public void initialize(DobContraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        min = constraintAnnotation.min();
    }
}
