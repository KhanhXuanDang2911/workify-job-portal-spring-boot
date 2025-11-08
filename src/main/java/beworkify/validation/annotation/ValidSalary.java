
package beworkify.validation.annotation;

import beworkify.validation.validator.SalaryValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SalaryValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSalary {
	String message() default "Invalid salary configuration for the selected salary type";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
