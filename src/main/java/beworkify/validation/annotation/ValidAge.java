package beworkify.validation.annotation;

import beworkify.validation.validator.AgeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AgeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAge {
  String message() default "Invalid age configuration for the selected age type";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
