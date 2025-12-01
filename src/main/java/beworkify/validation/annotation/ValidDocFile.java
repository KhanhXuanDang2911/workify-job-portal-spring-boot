package beworkify.validation.annotation;

import beworkify.validation.validator.DocFileValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Custom annotation for validating document files. Ensures that the uploaded file is a valid
 * document (e.g., .doc, .docx).
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Documented
@Constraint(validatedBy = DocFileValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDocFile {

  String message() default "Invalid file. Only .doc or .docx files are allowed.";

  boolean required() default true;

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
