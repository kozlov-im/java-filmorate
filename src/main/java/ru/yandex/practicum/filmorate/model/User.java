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
    private Set<Integer> requestedFriends = new HashSet<>();

    public User() {
    }

    public User(int id, String login, String name, String email, LocalDate birthday) {
        this.id = id;
        this.login = login;
        this.name = name;
        this.email = email;
        this.birthday = birthday;
    }

    private void setFriends(Set<Integer> id) {
    }

    private void setRequestedFriends(Set<Integer> id) {

    }

    public void addFriend(int id) {
        friends.add(id);
    }

    public void addFriendRequest(int id) {
        requestedFriends.add(id);
    }

    public Integer removeFriend(int id) {
        if (friends.contains(id)) {
            friends.remove(id);
            return id;
        } else {
            return null;
        }
    }

    public Integer removeFriendRequest(int id) {
        if (requestedFriends.contains(id)) {
            requestedFriends.remove(id);
            return id;
        } else {
            return null;
        }
    }

    @NonWhitespace(message = "Логин не может быть пустым или содержать пробелы")
    private String login;

    private String name;

    @NotBlank(message = "Почта не может быть пустой")
    @Email(message = "Почта введена не корректно")
    private String email;

    @Past(message = "Дата не может быть в будущем")
    private LocalDate birthday;
}
