package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validation.NotFoundException;
import ru.yandex.practicum.filmorate.validation.ValidationException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
@Primary
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public User create(User user) throws NotFoundException {
        if (user.getName() == null || user.getName().trim().equals("")) {
            user.setName(user.getLogin());
        }
        userStorage.create(user);
        log.info("Пользователь c id = " + user.getId() + " успешно добавлен");
        return user;
    }

    @Override
    public List<User> getAllUsers() throws NotFoundException {
        List<User> userList = userStorage.getAllUsers();
        userList.stream().forEach(user -> {
            user.setFriends(new HashSet<>(userStorage.getFriendsForUser(user.getId())));
            user.setRequestedFriends(new HashSet<>(userStorage.getRequestedFriendsForUser(user.getId())));
        });
        List<Integer> usersIdList = new ArrayList<>();
        userList.stream().forEach(user -> usersIdList.add(user.getId()));
        log.info("Список пользователей с id = " + usersIdList + " успешно получен");
        return userList;
    }

    @Override
    public User getUserById(int id) throws NotFoundException {
        User user = userStorage.getUserById(id);
        log.info("Пользователь c id = " + user.getId() + " успешно получен");
        return user;
    }

    @Override
    public List<User> getFriendsForUser(int userId) throws NotFoundException {
        User user = userStorage.getUserById(userId);
        List<User> usersList = new ArrayList<>();
        for (Integer friendId : user.getFriends()) {
            usersList.add(userStorage.getUserById(friendId));
        }
        List<Integer> friendsIdList = new ArrayList<>();
        usersList.stream().forEach(user1 -> friendsIdList.add(user1.getId()));
        log.info("Для пользователя c id = " + user.getId() + " успешно получен список друзей с id " + friendsIdList);
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
        List<Integer> commonFriendsIdList = new ArrayList<>();
        commonFriends.stream().forEach(user1 -> commonFriendsIdList.add(user1.getId()));
        log.info("Для пользователя c id = " + user.getId() + " и пользователя с id = " + otherUser.getId() + " успешно получен список общих друзей с id " + commonFriendsIdList);
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
        if (user.getFriends().contains(friendId)) {
            throw new ValidationException("Пользователь c id " + userId + " уже имеет друга с id " + friendId);
        } else {
            if (user.getRequestedFriends().contains(friendId)) {
                user.removeFriendRequest(friendId);
                user.addFriend(friendId);
                userStorage.approveFriend(userId, friendId);
                log.info("Пользователь id = " + userId + " подтвердил дружбу с пользователем id = " + friendId);

            } else {
                user.addFriend(friendId);
                friend.addFriendRequest(userId);
                userStorage.addToFriends(userId, friendId);
                log.info("Пользователь id = " + userId + " добавил в друзья пользователя id = " + friendId + " и запросил у него дружбу");
            }
        }
        return user;
    }

    @Override
    public User deleteFromFriends(int userId, int friendId) throws NotFoundException {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (user.getFriends().contains(friendId) && friend.getFriends().contains(userId)) {
            user.removeFriend(friendId);
            friend.removeFriend(userId);
            userStorage.deleteFromFriends(userId, friendId);
            log.info("Дружба между пользователем id = " + userId + " и пользователем id = " + friendId + " разорвана");
            return userStorage.getUserById(userId);
        } else if (user.getRequestedFriends().contains(friendId) && friend.getFriends().contains(userId)) {
            user.removeFriendRequest(friendId);
            friend.removeFriend(userId);
            userStorage.deleteFromFriends(userId, friendId);
            log.info("Пользователь id = " + userId + " не подтвердил дружбу пользователю id = " + friendId);
            return userStorage.getUserById(userId);
        } else if (user.getFriends().contains(friendId) && friend.getRequestedFriends().contains(userId)) {
            user.removeFriend(friendId);
            friend.removeFriendRequest(userId);
            userStorage.deleteFromFriends(userId, friendId);
            log.info("Пользователь id = " + userId + " отозвал запрос на дружбу с пользователем id = " + friendId);
            return userStorage.getUserById(userId);

        } else {
            throw new NotFoundException("Пользователь id = " + userId + " и пользователя id = " + friendId + " не являются друзьями");
        }
    }
}
