package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.NotFoundException;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private UserStorage userStorage;

    @BeforeEach
    public void beforeEach() {
        userStorage = new UserDbStorage(jdbcTemplate);
    }

    @Test
    void testCreate() throws NotFoundException {
        User newUser = new User(1, "Ivan Petrov", "vanya123", "user@email.ru", LocalDate.of(1990, 1, 1));
        userStorage.create(newUser);

        User savedUser = userStorage.getUserById(1);
        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser);
    }

    @Test
    void testGetAllUsers() throws NotFoundException {
        User user1 = new User(1, "Ivan Petrov", "vanya123", "user@email.ru", LocalDate.of(1990, 1, 1));
        User user2 = new User(2, "Vasiliy Ivanov", "vasya123", "vasya@email.ru", LocalDate.of(1991, 1, 1));
        userStorage.create(user1);
        userStorage.create(user2);
        List<User> savedUser = userStorage.getAllUsers();
        savedUser.sort(Comparator.comparingInt(User::getId));
        assertThat(savedUser.get(0)).isNotNull().usingRecursiveComparison().isEqualTo(user1);
        assertThat(savedUser.get(1)).isNotNull().usingRecursiveComparison().isEqualTo(user2);
    }

    @Test
    void testGetUserById() throws NotFoundException {
        User newUser = new User(1, "Ivan Petrov", "vanya123", "user@email.ru", LocalDate.of(1990, 1, 1));
        userStorage.create(newUser);
        User savedUser = userStorage.getUserById(1);
        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser);
    }

    @Test
    void testUpdate() throws NotFoundException {
        User user1 = new User(1, "Ivan Petrov", "vanya123", "user@email.ru", LocalDate.of(1990, 1, 1));
        userStorage.create(user1);
        User expectedUser = new User(1, "Vasiliy Ivanov", "vasya123", "vasya@email.ru", LocalDate.of(1991, 1, 1));
        user1.setLogin(expectedUser.getLogin());
        user1.setName(expectedUser.getName());
        user1.setEmail(expectedUser.getEmail());
        user1.setBirthday(expectedUser.getBirthday());
        userStorage.update(user1);

        User savedUser = userStorage.getUserById(1);
        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(expectedUser);
    }

    @Test
    void testAddToFriends() throws NotFoundException {
        User user1 = new User(1, "Ivan Petrov", "vanya123", "user@email.ru", LocalDate.of(1990, 1, 1));
        User user2 = new User(2, "Vasiliy Ivanov", "vasya123", "vasya@email.ru", LocalDate.of(1991, 1, 1));
        userStorage.create(user1);
        userStorage.create(user2);
        User expectedUser = new User(1, "Ivan Petrov", "vanya123", "user@email.ru", LocalDate.of(1990, 1, 1));
        expectedUser.addFriend(2);
        userStorage.addToFriends(user1.getId(), user2.getId());

        User savedUser = userStorage.getUserById(user1.getId());
        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(expectedUser);
    }

    @Test
    void testApproveFriend() throws NotFoundException {
        User user1 = new User(1, "Ivan Petrov", "vanya123", "user@email.ru", LocalDate.of(1990, 1, 1));
        User user2 = new User(2, "Vasiliy Ivanov", "vasya123", "vasya@email.ru", LocalDate.of(1991, 1, 1));
        userStorage.create(user1);
        userStorage.create(user2);

        User expectedUser = new User(2, "Vasiliy Ivanov", "vasya123", "vasya@email.ru", LocalDate.of(1991, 1, 1));
        expectedUser.addFriendRequest(1);
        userStorage.addToFriends(user1.getId(), user2.getId());

        User savedUser = userStorage.getUserById(user2.getId());
        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(expectedUser);
    }

    @Test
    void testDeleteFromFriends() throws NotFoundException {
        User user1 = new User(1, "Ivan Petrov", "vanya123", "user@email.ru", LocalDate.of(1990, 1, 1));
        User user2 = new User(2, "Vasiliy Ivanov", "vasya123", "vasya@email.ru", LocalDate.of(1991, 1, 1));
        userStorage.create(user1);
        userStorage.create(user2);

        userStorage.addToFriends(user1.getId(), user2.getId());
        userStorage.addToFriends(user2.getId(), user1.getId());

        userStorage.deleteFromFriends(user1.getId(), user2.getId());

        User expectedUser = new User(1, "Ivan Petrov", "vanya123", "user@email.ru", LocalDate.of(1990, 1, 1));

        User savedUser = userStorage.getUserById(user1.getId());
        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(expectedUser);
    }
}