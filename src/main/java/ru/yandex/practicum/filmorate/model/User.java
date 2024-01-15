package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.validation.NonWhitespace;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Data
public class User {
    private int id;
    private Set<Integer> friends = new HashSet<>();

    public void addFriend(int id) {
        friends.add(id);
    }

    public Integer removeFriend(int id) {
        if (friends.contains(id)) {
            friends.remove(id);
            return id;
        } else {
            return null;
        }
    }

    @NotBlank(message = "Почта не может быть пустой")
    @Email(message = "Почта введена не корректно")
    private String email;

    @NonWhitespace(message = "Логин не может быть пустым или содержать пробелы")
    private String login;

    private String name;

    @Past(message = "Дата не может быть в будущем")
    private LocalDate birthday;
}
