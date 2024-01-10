package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.validation.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    public User create(User user);

    public List<User> returnUsers();

    public User getUserById(int id) throws NotFoundException;

    public User update(User user);
}
