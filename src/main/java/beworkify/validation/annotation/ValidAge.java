package beworkify.validation.annotation;

import beworkify.validation.validator.AgeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Custom annotation for validating age configuration. Ensures that the age range is valid based on
 * the selected age type.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Documented
@Constraint(validatedBy = AgeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAge {
  String message() default "Invalid age configuration for the selected age type";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
