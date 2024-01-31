package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.validation.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User create(User user);

    List<User> getAllUsers() throws NotFoundException;

    List<Integer> getFriendsForUser(int userId);

    List<Integer> getRequestedFriendsForUser(int userId);

    User getUserById(int id) throws NotFoundException;

    User update(User user) throws NotFoundException;

    void addToFriends(int user, int friend);

    void approveFriend(int friendshipInit, int user);

    User deleteFromFriends(int userId, int friendId) throws NotFoundException;
}
