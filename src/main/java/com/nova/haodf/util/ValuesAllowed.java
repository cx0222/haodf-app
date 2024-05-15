package com.nova.haodf.util;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Set;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValuesAllowed.Validator.class)
public @interface ValuesAllowed {
    String message() default "Field value should be from list of ";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String propertyName();

    String[] values();

    class Validator implements ConstraintValidator<ValuesAllowed, Object> {
        private String propertyName;
        private String message;
        private Set<Object> allowableValues;

        @Override
        public void initialize(ValuesAllowed constraintAnnotation) {
            this.propertyName = constraintAnnotation.propertyName();
            this.message = constraintAnnotation.message();
            this.allowableValues = Set.of(constraintAnnotation.values());
        }

        @Override
        public boolean isValid(Object value, ConstraintValidatorContext context) {
            boolean valid = value == null || this.allowableValues.contains(value.toString());
            if (!valid) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(message + this.allowableValues)
                        .addPropertyNode(this.propertyName)
                        .addConstraintViolation();
            }
            return valid;
        }
    }
}
