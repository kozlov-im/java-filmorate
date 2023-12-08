package ru.yandex.practicum.filmorate.controller;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class FilmReleaseValidator implements ConstraintValidator<FilmRelease, LocalDate> {
    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        if (localDate.isAfter(LocalDate.of(1895, 12, 27))) {
            return true;
        } else {
            return false;
        }
    }
}
