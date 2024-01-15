package ru.yandex.practicum.filmorate.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NonWhitespaceValidator implements ConstraintValidator<NonWhitespace, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s != null) {
            if (s.contains(" ") || s.equals("")) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }
}
