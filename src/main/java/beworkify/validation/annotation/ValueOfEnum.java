package beworkify.validation.annotation;

import beworkify.validation.validator.ValueOfEnumValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValueOfEnumValidator.class)
@Documented
public @interface ValueOfEnum {
    Class<? extends Enum<?>> enumClass();

    boolean required() default true;

    String message() default "Invalid data enum";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
