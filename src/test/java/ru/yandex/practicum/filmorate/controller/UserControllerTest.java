package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


class UserControllerTest {
    private Validator validator;

    @BeforeEach
    public void beforeEach(){
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldCheckEmailExist(){
        User user = new User();
        user.setLogin("userLogin");
        user.setName("userName");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        List<String> validateMessage = violations.stream().map(v -> v.getMessage()).collect(Collectors.toList());
        Assertions.assertTrue(validateMessage.contains("Почта не может быть пустой"));
    }

    @Test
    public void shouldCheckEmailIsBlank(){
        User user = new User();
        user.setEmail("");
        user.setLogin("userLogin");
        user.setName("userName");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        List<String> validateMessage = violations.stream().map(v -> v.getMessage()).collect(Collectors.toList());
        Assertions.assertTrue(validateMessage.contains("Почта не может быть пустой"));
    }
    @Test
    public void shouldCheckEmailIsCorrect(){
        User user = new User();
        user.setEmail("userNamemail.ru");
        user.setLogin("userLogin");
        user.setName("userName");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        List<String> validateMessage = violations.stream().map(v -> v.getMessage()).collect(Collectors.toList());
        Assertions.assertTrue(validateMessage.contains("Почта введена не корректно"));
    }
    @Test
    public void shouldCheckLoginIsExist(){
        User user = new User();
        user.setEmail("userName@mail.ru");
        user.setName("userName");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        List<String> validateMessage = violations.stream().map(v -> v.getMessage()).collect(Collectors.toList());
        Assertions.assertTrue(validateMessage.contains("Логин не может быть пустым или содержать пробелы"));
    }

    @Test
    public void shouldCheckLoginIsBlank(){
        User user = new User();
        user.setEmail("userName@mail.ru");
        user.setLogin("");
        user.setName("userName");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        List<String> validateMessage = violations.stream().map(v -> v.getMessage()).collect(Collectors.toList());
        Assertions.assertTrue(validateMessage.contains("Логин не может быть пустым или содержать пробелы"));
    }
    @Test
    public void shouldCheckLoginContainsWhitespaces(){
        User user = new User();
        user.setEmail("userName@mail.ru");
        user.setLogin("log in");
        user.setName("userName");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        List<String> validateMessage = violations.stream().map(v -> v.getMessage()).collect(Collectors.toList());
        Assertions.assertTrue(validateMessage.contains("Логин не может быть пустым или содержать пробелы"));
    }
    @Test
    public void shouldCheckBirthday(){
        User user = new User();
        user.setEmail("userName@mail.ru");
        user.setLogin("login");
        user.setName("userName");
        user.setBirthday(LocalDate.now().plusDays(1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        List<String> validateMessage = violations.stream().map(v -> v.getMessage()).collect(Collectors.toList());
        Assertions.assertTrue(validateMessage.contains("Дата не может быть в будущем"));
    }

}