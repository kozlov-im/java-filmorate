package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class FilmControllerTest {
    private Validator validator;

    @BeforeEach
    public void beforeEach() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldCheckFilmNameIsExist() {
        Film film = new Film();
        film.setDescription("filmDescription");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(60);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        List<String> validateMessage = violations.stream().map(v -> v.getMessage()).collect(Collectors.toList());
        Assertions.assertTrue(validateMessage.contains("Название фильма не может быть пустым"));
    }

    @Test
    public void shouldCheckFilmNameIsBlank() {
        Film film = new Film();
        film.setName("");
        film.setDescription("filmDescription");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(60);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        List<String> validateMessage = violations.stream().map(v -> v.getMessage()).collect(Collectors.toList());
        Assertions.assertTrue(validateMessage.contains("Название фильма не может быть пустым"));
    }

    @Test
    public void shouldCheckFilmDescriptionLessThan200() {
        Film film = new Film();
        film.setName("filmName");
        StringBuffer filmDescription = new StringBuffer();
        for (int i = 0; i < 201; i++) {
            filmDescription.append("d");
        }
        film.setDescription(filmDescription.toString());
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(60);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        List<String> validateMessage = violations.stream().map(v -> v.getMessage()).collect(Collectors.toList());
        Assertions.assertTrue(validateMessage.contains("Максимальная длина описания может быть не более 200 символов"));
    }

    @Test
    public void shouldCheckFilmRelease() {
        Film film = new Film();
        film.setName("filmName");
        film.setDescription("filmDescription");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(60);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        List<String> validateMessage = violations.stream().map(v -> v.getMessage()).collect(Collectors.toList());
        Assertions.assertTrue(validateMessage.contains("Некорректная дата релиза"));
    }

    @Test
    public void shouldCheckFilmDuration() {
        Film film = new Film();
        film.setName("filmName");
        film.setDescription("filmDescription");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(0);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        List<String> validateMessage = violations.stream().map(v -> v.getMessage()).collect(Collectors.toList());
        Assertions.assertTrue(validateMessage.contains("Продолжительность должна быть положительной"));
    }

}