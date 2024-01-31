package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.validation.NotFoundException;

import java.util.List;

@Component
@AllArgsConstructor
public class GenreDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public List<Genre> getAllGenres() {
        return jdbcTemplate.query("SELECT * FROM genres", genreRowMapper());
    }

    public Genre getGenreById(int id) throws NotFoundException {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM genres WHERE id = ?", genreRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Жанр с id = " + id + " не найден");
        }
    }

    public List<Genre> getGenresForFilm(int filmId) {
        return jdbcTemplate.query("SELECT genre_id id, genre FROM genre_link gl JOIN genres g on gl.genre_id = g.id WHERE film_id = ?",
                genreRowMapper(), filmId);
    }

    private RowMapper<Genre> genreRowMapper() {
        return (rs, rowNum) -> new Genre(rs.getInt("id"), rs.getString("genre"));
    }


}
