package beworkify.validation.validator;

import beworkify.validation.annotation.ValidDocFile;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * Validator for document files. Checks if the uploaded file has a valid extension (.doc, .docx,
 * .pdf).
 *
 * @author KhanhDX
 * @since 1.0.0
 */
public class DocFileValidator implements ConstraintValidator<ValidDocFile, MultipartFile> {

  private boolean required;
  private final List<String> allowedExtensions = Arrays.asList(".doc", ".docx", ".pdf");

  @Override
  public void initialize(ValidDocFile constraintAnnotation) {
    this.required = constraintAnnotation.required();
  }

  @Override
  public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
    if (!required && (file == null || file.isEmpty())) {
      return true;
    }

    if (file == null || file.isEmpty()) {
      return false;
    }

    String fileName = file.getOriginalFilename();
    if (fileName == null) {
      return false;
    }

    return allowedExtensions.stream().anyMatch(ext -> fileName.toLowerCase().endsWith(ext));
  }
}
