package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.validation.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User create(User user);

    List<User> returnUsers();

    User getUserById(int id) throws NotFoundException;

    User update(User user);
}
