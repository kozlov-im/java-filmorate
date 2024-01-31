package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.validation.NotFoundException;

import java.sql.PreparedStatement;
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
            jdbcTemplate.batchUpdate("INSERT INTO genre_link (film_id, genre_id) VALUES (?, ?)",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setInt(1, film.getId());
                            ps.setInt(2, genresList.get(i).getId());
                        }

                        @Override
                        public int getBatchSize() {
                            return genresList.size();
                        }
                    });
        }
        return getFilmById(film.getId());
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT f.id id, " +
                "f.name name, " +
                "f.description description, " +
                "f.release_date release_date, " +
                "f.duration duration, " +
                "f.mpa_id mpa_id, " +
                "m.mpa mpa " +
                "FROM films f " +
                "LEFT OUTER JOIN mpa m on f.mpa_id = m.id " +
                "ORDER BY f.id";
        return jdbcTemplate.query(sql, filmRowMapper());
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

            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                Film film = new Film(rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDate("release_date").toLocalDate(),
                        rs.getInt("duration"),
                        new Mpa(rs.getInt("mpa_id"), rs.getString("mpa")));
                do { //прохожусь по всем записям и добавляю все жанры в результирующий набор
                    if (rs.getInt("genre_id") != 0) {
                        film.addGenre(new Genre(rs.getInt("genre_id"), rs.getString("genre_name")));
                    }
                } while (rs.next());
                return film;
            }, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
    }

    @Override
    public Film update(Film film) throws NotFoundException {
        jdbcTemplate.update("UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?",
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        List<Genre> genresList = film.getGenres();
        jdbcTemplate.update("DELETE FROM genre_link WHERE film_id = ?", film.getId());
        jdbcTemplate.batchUpdate("INSERT INTO genre_link(film_id, genre_id) VALUES(?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, film.getId());
                        ps.setInt(2, genresList.get(i).getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return genresList.size();
                    }
                });
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

    private RowMapper<Film> filmRowMapper() {
        return (rs, rowNum) ->
                new Film(rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDate("release_date").toLocalDate(),
                        rs.getInt("duration"),
                        new Mpa(rs.getInt("mpa_id"), rs.getString("mpa")));
    }
}
