package beworkify.validation.validator;

import beworkify.validation.annotation.ValidImageFile;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Set;
import org.springframework.web.multipart.MultipartFile;

public class ImageFileValidator implements ConstraintValidator<ValidImageFile, MultipartFile> {

  private boolean required;

  private static final Set<String> ALLOWED_TYPES =
      Set.of(
          "image/jpeg",
          "image/png",
          "image/gif",
          "image/webp",
          "image/bmp",
          "image/svg+xml",
          "image/x-icon",
          "image/vnd.microsoft.icon",
          "image/tiff",
          "image/heif",
          "image/heic",
          "image/avif",
          "image/jp2",
          "image/jpx",
          "image/jpm",
          "image/mj2",
          "image/vnd.adobe.photoshop",
          "image/x-portable-bitmap",
          "image/x-portable-graymap",
          "image/x-portable-pixmap",
          "image/x-xbitmap",
          "image/x-xpixmap",
          "image/x-targa",
          "image/x-tga",
          "image/x-pcx",
          "image/x-canon-cr2",
          "image/x-canon-cr3",
          "image/x-nikon-nef",
          "image/x-sony-arw",
          "image/x-olympus-orf",
          "image/x-panasonic-rw2",
          "image/x-fuji-raf");

  @Override
  public void initialize(ValidImageFile constraintAnnotation) {
    this.required = constraintAnnotation.required();
  }

  @Override
  public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
    if (file == null || file.isEmpty()) {
      return !required;
    }

    String contentType = file.getContentType();
    if (contentType == null) {
      return false;
    }

    return ALLOWED_TYPES.contains(contentType.toLowerCase());
  }
}
