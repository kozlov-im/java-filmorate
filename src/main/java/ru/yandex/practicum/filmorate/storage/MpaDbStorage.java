package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.validation.NotFoundException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@AllArgsConstructor
public class MpaDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public List<Mpa> getAllMpa() {
        return jdbcTemplate.query("SELECT * FROM mpa", new RowMapper<Mpa>() {
            @Override
            public Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Mpa(rs.getInt("id"), rs.getString("mpa"));
            }
        });
    }

    public Mpa getMpaById(int id) throws NotFoundException {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM mpa WHERE id = ?", new RowMapper<Mpa>() {
                @Override
                public Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new Mpa(rs.getInt("id"), rs.getString("mpa"));
                }
            }, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("MPA с id = " + id + " не найден");
        }
    }
}
