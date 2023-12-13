package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private Map<Integer, Film> films = new HashMap<>();
    private int generatedId = 1;

    @PostMapping
    public Film create(@RequestBody @Valid Film film) {
        film.setId(generatedId++);
        films.put(film.getId(), film);
        log.info("Фильм с id = " + film.getId() + " успешно добавлен");
        return film;
    }

    @GetMapping
    public List<Film> returnFilms() {
        return new ArrayList<>(films.values());
    }

    @PutMapping
    public Film update(@RequestBody @Valid Film film) throws ValidationException {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Фильм с id = " + film.getId() + " успешно обновлен");
            return film;
        } else {
            log.info("Фильм с id = " + film.getId() + " не найден");
            throw new ValidationException("Фильм с id = " + film.getId() + " не найден");
        }
    }
}
