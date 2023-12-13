package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    static final Logger log = LoggerFactory.getLogger(UserController.class);
    private Map<Integer, User> users = new HashMap<>();
    private int generatedId = 1;

    @PostMapping
    public User create(@RequestBody @Valid User user) {
        user.setId(generatedId++);
        if (user.getName() == null || user.getName().trim().equals("")) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Пользователь c id = " + user.getId() + " успешно добавлен");
        return user;
    }

    @GetMapping
    public List<User> returnUsers() {
        return new ArrayList<>(users.values());
    }

    @PutMapping
    public User update(@RequestBody @Valid User user) throws ValidationException {
        if (users.containsKey(user.getId())) {
            if (user.getName() == null || user.getName().trim().equals("")) {
                user.setName(user.getLogin());
            }
            users.put(user.getId(), user);
            log.info("Пользователь с id = " + user.getId() + " успешно обновлен");
            return user;
        } else {
            log.info("Пользователь с id = " + user.getId() + " не найден");
            throw new ValidationException("Пользователь с id = " + user.getId() + " не найден");
        }
    }
}
