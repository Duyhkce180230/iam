package com.example.identity.validator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;

import com.nimbusds.jose.Payload;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = {DobValidator.class})
public @interface DobContraint {
    String message() default "Invalid Dob";

    int min();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
