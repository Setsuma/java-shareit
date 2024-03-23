package ru.practicum.shareit.booking.validator;

import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class CorrectDateValidator implements ConstraintValidator<CorrectDate, Object> {
    private String start;
    private String end;

    public void initialize(CorrectDate constraintAnnotation) {
        this.start = constraintAnnotation.startDate();
        this.end = constraintAnnotation.endDate();
    }

    public boolean isValid(Object value, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("Начало не может быть позже конца").addConstraintViolation();
        LocalDateTime startDate = (LocalDateTime) new BeanWrapperImpl(value)
                .getPropertyValue(start);
        LocalDateTime endDate = (LocalDateTime) new BeanWrapperImpl(value)
                .getPropertyValue(end);

        if (startDate == null || endDate == null || startDate.isEqual(endDate) || startDate.isAfter(endDate))
            return false;

        return true;
    }
}