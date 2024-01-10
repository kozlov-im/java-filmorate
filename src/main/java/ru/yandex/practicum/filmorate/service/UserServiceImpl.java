package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.validation.NotFoundException;
import ru.yandex.practicum.filmorate.validation.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private UserStorage userStorage;
    private int generatedId = 1;

    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public User create(User user) throws NotFoundException {
        if (user.getName() == null || user.getName().trim().equals("")) {
            user.setName(user.getLogin());
        }
        Set<Integer> userFriends = user.getFriends();
        if (userFriends != null) {
            for (Integer friendId : userFriends) {
                userStorage.getUserById(friendId);
            }
        }
        user.setId(generatedId++);
        userStorage.create(user);
        for (Integer friendId : userFriends) {
            userStorage.getUserById(friendId).addFriend(user.getId());
        }
        log.info("Пользователь c id = " + user.getId() + " успешно добавлен");
        return user;
    }

    @Override
    public List<User> returnUsers() {
        return userStorage.returnUsers();
    }

    @Override
    public User getUserById(int id) throws NotFoundException {
        return userStorage.getUserById(id);
    }

    @Override
    public List<User> getFriendsForUser(int userId) throws NotFoundException {
        User user = userStorage.getUserById(userId);
        List<User> usersList = new ArrayList<>();
        for (Integer friendId : user.getFriends()) {
            usersList.add(userStorage.getUserById(friendId));
        }
        return usersList;
    }

    @Override
    public List<User> getCommonFriendsForUser(int userId, int otherId) throws NotFoundException {
        User user = userStorage.getUserById(userId);
        User otherUser = userStorage.getUserById(otherId);
        List<User> commonFriends = new ArrayList<>();
        for (Integer friendId : user.getFriends()) {
            for (Integer otherFriendId : otherUser.getFriends()) {
                if (friendId == otherFriendId) {
                    commonFriends.add(userStorage.getUserById(friendId));
                }
            }
        }
        return commonFriends;
    }

    @Override
    public User update(User user) throws NotFoundException {
        User returnedUser = userStorage.update(user);
        if (returnedUser != null) {
            log.info("Пользователь с id = " + user.getId() + " успешно обновлен");
            return returnedUser;
        } else {
            log.info("Пользователь с id = " + user.getId() + " не найден");
            throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
        }
    }

    @Override
    public User addToFriends(int userId, int friendId) throws NotFoundException, ValidationException {
        if (userId == friendId) {
            throw new ValidationException("Нельзя добавить в друзья самого себя");
        }
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.addFriend(friendId);
        log.info("Пользователь id = " + userId + " добавил в друзья пользователя id = " + friendId);
        friend.addFriend(userId);
        log.info("Пользователь id = " + friendId + " добавил в друзья пользователя id = " + userId);
        return user;
    }

    @Override
    public User deleteFromFriends(int userId, int friendId) throws NotFoundException {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        Integer removedFriendId = user.removeFriend(friendId);
        if (removedFriendId != null) {
            log.info("Пользователь id = " + userId + " удалил из друзей пользователя id = " + friendId);
            friend.removeFriend(userId);
            log.info("Пользователь id = " + userId + " удален из друзей пользователя id = " + friendId);
            return user;
        } else {
            throw new NotFoundException("Пользователь id = " + friendId + " не найден в друзьях пользователя id = " + userId);
        }
    }
}
