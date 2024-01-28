package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.validation.NotFoundException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@AllArgsConstructor
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film create(Film film) throws NotFoundException {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("films")
                .usingGeneratedKeyColumns("id");
        Map<String, String> params = Map.of(
                "name", film.getName(),
                "description", film.getDescription(),
                "release_date", String.valueOf(film.getReleaseDate()),
                "duration", String.valueOf(film.getDuration()),
                "mpa_id", String.valueOf(film.getMpa().getId()));
        Number id = simpleJdbcInsert.executeAndReturnKey(params);
        film.setId(id.intValue());
        List<Genre> genresList = film.getGenres();
        if (genresList.size() > 0) {
            for (Genre genre : genresList) {
                jdbcTemplate.update("INSERT INTO genre_link (film_id, genre_id) VALUES (?, ?)", film.getId(), genre.getId());
            }
        }
        //return getFilmById(film.getId());
        return film;
    }

    @Override
    public List<Film> returnFilms() throws NotFoundException {
        List<Integer> filmsIdSet = jdbcTemplate.query("SELECT id FROM films ORDER BY id", (rs, rowNum) -> rs.getInt("id"));
        List<Film> filmsList = new ArrayList<>();
        for (Integer filmId : filmsIdSet) {
            filmsList.add(getFilmById(filmId));
        }
        return filmsList;
    }

    @Override
    public Film getFilmById(int id) throws NotFoundException {
        try {
            String sql = "SELECT f.id id, " +
                    "f.name name, " +
                    "f.description description, " +
                    "f.release_date release_date, " +
                    "f.duration duration, " +
                    "p.id mpa_id, " +
                    "p.mpa mpa, " +
                    "g.id genre_id, " +
                    "g.genre genre_name " +
                    "FROM films f " +
                    "LEFT OUTER JOIN genre_link gl on f.id = gl.film_id " +
                    "LEFT OUTER JOIN genres g on gl.genre_id = g.id " +
                    "LEFT OUTER JOIN mpa p on f.mpa_id = p.id " +
                    "WHERE f.id = ? ORDER BY f.id, g.id";

            Film film = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                Film film1 = new Film(rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDate("release_date").toLocalDate(),
                        rs.getInt("duration"),
                        //rs.getInt("mpa_id"));
                        new Mpa(rs.getInt("mpa_id"), rs.getString("mpa")));
                do {
                    if (rs.getInt("genre_id") != 0) {
                        //film1.addGenre((rs.getInt("genre_id")));
                        film1.addGenre(new Genre(rs.getInt("genre_id"), rs.getString("genre_name")));
                    }
                } while (rs.next());
                return film1;
            }, id);
            jdbcTemplate.query("SELECT user_id FROM likes WHERE film_id = ?", (RowMapper<Object>) (rs, rowNum) -> {
                film.addLike(rs.getInt("user_id"));
                return film;
            }, id);
            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
    }

    @Override
    public Film update(Film film) throws NotFoundException {
        jdbcTemplate.update("UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?",
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        List<Genre> genresList = film.getGenres();
        //if (genresSet.size() > 0) {
        jdbcTemplate.update("DELETE FROM genre_link WHERE film_id = ?", film.getId());
        for (Genre genre : genresList) {
            jdbcTemplate.update("INSERT INTO genre_link(film_id, genre_id) VALUES(?, ?)", film.getId(), genre.getId());
        }
        // }
        return getFilmById(film.getId());
    }

    @Override
    public List<Film> getPopularFilms(String count) throws NotFoundException {
        List<Film> popularFilmsSet = new LinkedList<>();
        String sql = "SELECT f.id film_id FROM films f " +
                "LEFT OUTER JOIN " +
                "(SELECT film_id, COUNT(film_id) likes_amount " +
                "FROM likes " +
                "GROUP BY film_id) l ON f.id = l.film_id " +
                "ORDER BY likes_amount DESC NULLS LAST LIMIT ?";
        List<Integer> popularFilmsId = new LinkedList<>(jdbcTemplate.query(sql, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt("film_id");
            }
        }, Integer.parseInt(count)));
        for (Integer filmId : popularFilmsId) {
            popularFilmsSet.add(getFilmById(filmId));
        }
        return popularFilmsSet;
    }

    public Film setLike(int filmId, int userId) throws NotFoundException {
        jdbcTemplate.update("INSERT INTO likes(film_id, user_id) VALUES(?, ?)", filmId, userId);
        return getFilmById(filmId);
    }

    @Override
    public Film removeLike(int filmId, int userId) throws NotFoundException {
        jdbcTemplate.update("DELETE FROM likes WHERE film_id = ? AND user_id = ?", filmId, userId);
        return getFilmById(filmId);
    }


}
