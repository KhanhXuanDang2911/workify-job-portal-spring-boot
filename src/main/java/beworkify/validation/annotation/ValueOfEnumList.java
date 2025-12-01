package beworkify.validation.annotation;

import beworkify.validation.validator.ValueOfEnumListValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Custom annotation for validating that a list of values exists in a specified Enum.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValueOfEnumListValidator.class)
@Documented
public @interface ValueOfEnumList {

  Class<? extends Enum<?>> enumClass();

  String message() default "Contains invalid value. Must be one of the enum constants";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
