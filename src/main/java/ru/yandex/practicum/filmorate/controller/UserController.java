package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validation.NotFoundException;
import ru.yandex.practicum.filmorate.validation.ValidationException;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
public class UserController {
    private UserService userService;

    @PostMapping("/users")
    public User create(@RequestBody @Valid User user) throws NotFoundException {
        return userService.create(user);
    }

    @GetMapping("/users")
    public List<User> returnUsers() {
        return userService.returnUsers();
    }

    @GetMapping("/users/{userId}")
    public User getUserById(@PathVariable int userId) throws NotFoundException {
        return userService.getUserById(userId);
    }

    @GetMapping("/users/{userId}/friends")
    public List<User> getFriendsForUser(@PathVariable int userId) throws NotFoundException {
        return userService.getFriendsForUser(userId);
    }

    @GetMapping("/users/{userId}/friends/common/{otherId}")
    public List<User> getCommonFriendsForUser(@PathVariable int userId, @PathVariable int otherId) throws NotFoundException {
        return userService.getCommonFriendsForUser(userId, otherId);
    }

    @PutMapping("/users")
    public User update(@RequestBody @Valid User user) throws NotFoundException {
        return userService.update(user);
    }

    @PutMapping("/users/{userId}/friends/{friendId}")
    public User addToFriends(@PathVariable int userId, @PathVariable int friendId) throws NotFoundException, ValidationException {
        return userService.addToFriends(userId, friendId);
    }

    @DeleteMapping("/users/{userId}/friends/{friendId}")
    public User deleteFromFriends(@PathVariable int userId, @PathVariable int friendId) throws NotFoundException {
        return userService.deleteFromFriends(userId, friendId);
    }
}
