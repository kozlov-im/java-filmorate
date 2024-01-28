package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.validation.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private Map<Integer, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> returnUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(int id) throws NotFoundException {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
    }

    @Override
    public User update(User user) {
        if (users.containsKey(user.getId())) {
            if (user.getName() == null || user.getName().trim().equals("")) {
                user.setName(user.getLogin());
            }
            users.put(user.getId(), user);
            return user;
        } else {
            return null;
        }
    }

    @Override
    public void addToFriends(int user, int friend) {
    }

    @Override
    public void approveFriend(int friendshipInit, int user) {
    }

    @Override
    public User deleteFromFriends(int userId, int friendId) {
        return null;
    }
}
