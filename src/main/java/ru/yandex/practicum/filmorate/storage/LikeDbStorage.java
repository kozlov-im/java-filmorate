package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.HashSet;
import java.util.Set;

@Component
@AllArgsConstructor
public class LikeDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public Like getLikes(int filmId) {
        try {
            return jdbcTemplate.queryForObject("SELECT user_id FROM likes WHERE film_id = ?", (rs, rowNum) -> {
                Set<Integer> usersLikeId = new HashSet<>();
                do {
                    usersLikeId.add(rs.getInt("user_id"));
                } while (rs.next());
                return new Like(filmId, usersLikeId);
            }, filmId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Like setLike(int filmId, int userId) {
        jdbcTemplate.update("INSERT INTO likes(film_id, user_id) VALUES(?, ?)", filmId, userId);
        return getLikes(filmId);
    }

    public Like removeLike(int filmId, int userId) {
        jdbcTemplate.update("DELETE FROM likes WHERE film_id = ? AND user_id = ?", filmId, userId);
        return getLikes(filmId);
    }
}
