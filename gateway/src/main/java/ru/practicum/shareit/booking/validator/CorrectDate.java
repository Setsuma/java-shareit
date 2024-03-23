package ru.practicum.shareit.booking.validator;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = CorrectDateValidator.class)
@Target(ElementType.TYPE)
@Retention(RUNTIME)
public @interface CorrectDate {

    String message() default "date error";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};

    String startDate();

    String endDate();
}