package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.validation.NotFoundException;
import ru.yandex.practicum.filmorate.validation.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    public User create(User user) throws NotFoundException;

    public List<User> returnUsers();

    public User getUserById(int id) throws NotFoundException;

    public List<User> getFriendsForUser(int userId) throws NotFoundException;

    public List<User> getCommonFriendsForUser(int userId, int otherId) throws NotFoundException;

    public User update(User user) throws NotFoundException;

    public User addToFriends(int userId, int friendId) throws NotFoundException, ValidationException;

    public User deleteFromFriends(int userId, int friendId) throws NotFoundException;
}
