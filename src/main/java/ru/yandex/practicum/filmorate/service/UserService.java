package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.validation.NotFoundException;
import ru.yandex.practicum.filmorate.validation.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    User create(User user) throws NotFoundException;

    List<User> returnUsers() throws NotFoundException;

    User getUserById(int id) throws NotFoundException;

    List<User> getFriendsForUser(int userId) throws NotFoundException;

    List<User> getCommonFriendsForUser(int userId, int otherId) throws NotFoundException;

    User update(User user) throws NotFoundException;

    User addToFriends(int userId, int friendId) throws NotFoundException, ValidationException;

    User deleteFromFriends(int userId, int friendId) throws NotFoundException;
}
