package beworkify.validation.validator;

import beworkify.dto.request.JobRequest;
import beworkify.enumeration.SalaryType;
import beworkify.validation.annotation.ValidSalary;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SalaryValidator implements ConstraintValidator<ValidSalary, JobRequest> {

    @Override
    public boolean isValid(JobRequest job, ConstraintValidatorContext context) {
        if (job == null || job.getSalaryType() == null) {
            return true;
        }

        SalaryType type = SalaryType.fromValue(job.getSalaryType());

        Double minSalary = job.getMinSalary();
        Double maxSalary = job.getMaxSalary();
        var unit = job.getSalaryUnit();

        context.disableDefaultConstraintViolation();

        boolean valid = true;

        switch (type) {
            case RANGE -> {
                if (minSalary == null) {
                    context.buildConstraintViolationWithTemplate("{validation.salary.range.minRequired}")
                            .addPropertyNode("minSalary").addConstraintViolation();
                    valid = false;
                }
                if (maxSalary == null) {
                    context.buildConstraintViolationWithTemplate("{validation.salary.range.maxRequired}")
                            .addPropertyNode("maxSalary").addConstraintViolation();
                    valid = false;
                }
                if (unit == null) {
                    context.buildConstraintViolationWithTemplate("{validation.salary.range.unitRequired}")
                            .addPropertyNode("salaryUnit").addConstraintViolation();
                    valid = false;
                }
            }
            case GREATER_THAN -> {
                if (minSalary == null) {
                    context.buildConstraintViolationWithTemplate("{validation.salary.greater.minRequired}")
                            .addPropertyNode("minSalary").addConstraintViolation();
                    valid = false;
                }
                if (unit == null) {
                    context.buildConstraintViolationWithTemplate("{validation.salary.greater.unitRequired}")
                            .addPropertyNode("salaryUnit").addConstraintViolation();
                    valid = false;
                }
            }
        }

        return valid;
    }
}
