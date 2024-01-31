package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.NotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
@Primary
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User create(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        Map<String, String> params = Map.of("name", user.getName(),
                "login", user.getLogin(),
                "email", user.getEmail(),
                "birthday", String.valueOf((user.getBirthday())));
        Number id = simpleJdbcInsert.executeAndReturnKey(params);
        user.setId(id.intValue());
        System.out.println("Создан пользователь с id " + id);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return jdbcTemplate.query("SELECT * FROM users", (rs, rowNum) -> new User(
                rs.getInt("id"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getDate("birthday").toLocalDate()
        ));
    }

    @Override
    public User getUserById(int id) throws NotFoundException {
        try {
            User user = jdbcTemplate.queryForObject("SELECT * FROM users WHERE id = ?", userRowMapper(), id);
            user.setFriends(new HashSet<>(getFriendsForUser(user.getId())));
            user.setRequestedFriends(new HashSet<>(getRequestedFriendsForUser(user.getId())));
            return user;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
    }

    @Override
    public List<Integer> getFriendsForUser(int userId) {
        String sql = "SELECT id FROM users WHERE id IN (" +
                "  SELECT friend_id FROM friends WHERE user_id = ?" +
                "  UNION" +
                "  SELECT user_id FROM friends WHERE friend_id = ? AND status = '1')";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("id"), userId, userId);
    }

    @Override
    public List<Integer> getRequestedFriendsForUser(int userId) {
        return jdbcTemplate.query("SELECT id FROM users WHERE id IN (SELECT user_id FROM friends WHERE friend_id = ? AND status = 0)",
                (rs, rowNum) -> rs.getInt("id"), userId);
    }

    @Override
    public User update(User user) throws NotFoundException {
        jdbcTemplate.update("UPDATE users SET name = ?, login = ?, email = ?, birthday = ? WHERE id = ?",
                user.getName(), user.getLogin(), user.getEmail(), user.getBirthday(), user.getId());
        return getUserById(user.getId());
    }

    @Override
    public void addToFriends(int userId, int friendId) {
        jdbcTemplate.update("INSERT INTO friends(user_id, friend_id, status) VALUES (?, ?, 0)",
                userId, friendId);
    }

    @Override
    public void approveFriend(int userId, int friendId) {
        jdbcTemplate.update("UPDATE friends SET status = 1 WHERE user_id = ? AND friend_id = ?",
                friendId, userId);
    }

    @Override
    public User deleteFromFriends(int userId, int friendId) throws NotFoundException {
        jdbcTemplate.update("DELETE FROM friends WHERE user_id in (?, ?) AND friend_id in (?, ?)",
                userId, friendId, userId, friendId);
        return getUserById(userId);
    }

    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> new User(rs.getInt("id"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getDate("birthday").toLocalDate());
    }
}
