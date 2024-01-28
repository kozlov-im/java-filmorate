package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.validation.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User create(User user);

    List<User> returnUsers() throws NotFoundException;

    User getUserById(int id) throws NotFoundException;

    User update(User user) throws NotFoundException;

    void addToFriends(int user, int friend);

    void approveFriend(int friendshipInit, int user);

    public User deleteFromFriends(int userId, int friendId) throws NotFoundException;
}
