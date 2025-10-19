package beworkify.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;

public class ValueOfEnumListValidator implements ConstraintValidator<ValueOfEnumList, List<String>> {

    private List<String> acceptedValues;

    @Override
    public void initialize(ValueOfEnumList annotation) {
        acceptedValues = Arrays.stream(annotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .toList();
    }

    @Override
    public boolean isValid(List<String> values, ConstraintValidatorContext context) {
        if (values == null || values.isEmpty())
            return true;

        boolean allValid = values.stream().allMatch(acceptedValues::contains);

        if (!allValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Invalid value(s): " + values +
                            ". Allowed values: " + acceptedValues)
                    .addConstraintViolation();
        }

        return allValid;
    }
}
