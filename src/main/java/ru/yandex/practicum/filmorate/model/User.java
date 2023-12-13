package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.controller.NonWhitespace;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;


@Data
public class User {

    private int id;

    @NotBlank(message = "Почта не может быть пустой")
    @Email(message = "Почта введена не корректно")
    private String email;

    @NonWhitespace(message = "Логин не может быть пустым или содержать пробелы")
    private String login;

    private String name;

    @Past(message = "Дата не может быть в будущем")
    private LocalDate birthday;
}
