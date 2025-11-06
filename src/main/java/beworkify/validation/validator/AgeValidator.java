package beworkify.validation.validator;

import beworkify.dto.request.JobRequest;
import beworkify.enumeration.AgeType;
import beworkify.validation.annotation.ValidAge;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AgeValidator implements ConstraintValidator<ValidAge, JobRequest> {

    @Override
    public boolean isValid(JobRequest job, ConstraintValidatorContext context) {
        if (job == null || job.getAgeType() == null) {
            return true;
        }

        String typeString = job.getAgeType();
        AgeType type = AgeType.fromName(typeString);
        Long min = job.getMinAge();
        Long max = job.getMaxAge();

        context.disableDefaultConstraintViolation();
        boolean valid = true;

        switch (type) {
            case NONE -> {
            }
            case ABOVE -> {
                if (min == null) {
                    context.buildConstraintViolationWithTemplate("{validation.age.above.minRequired}")
                            .addPropertyNode("minAge").addConstraintViolation();
                    valid = false;
                }
            }
            case BELOW -> {
                if (max == null) {
                    context.buildConstraintViolationWithTemplate("{validation.age.below.maxRequired}")
                            .addPropertyNode("maxAge").addConstraintViolation();
                    valid = false;
                }
            }
            case INPUT -> {
                if (min == null) {
                    context.buildConstraintViolationWithTemplate("{validation.age.input.minRequired}")
                            .addPropertyNode("minAge").addConstraintViolation();
                    valid = false;
                }
                if (max == null) {
                    context.buildConstraintViolationWithTemplate("{validation.age.input.maxRequired}")
                            .addPropertyNode("maxAge").addConstraintViolation();
                    valid = false;
                }
                if (min != null && max != null && min > max) {
                    context.buildConstraintViolationWithTemplate("{validation.age.input.rangeInvalid}")
                            .addPropertyNode("maxAge").addConstraintViolation();
                    valid = false;
                }
            }
        }

        return valid;
    }
}
