package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private Film create(@RequestBody @Valid Film film) {
        film.setId(generatedId++);
        films.put(film.getId(), film);
        log.info("Фильм с id = " + film.getId() + " успешно добавлен");
        return film;
    }

    @GetMapping
    private List<Film> returnFilms() {
        return new ArrayList<>(films.values());
    }

    @PutMapping
    private ResponseEntity<Film> update(@RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Фильм с id = " + film.getId() + " успешно обновлен");
            return ResponseEntity.ok(film);
        } else {
            log.info("Фильм с id = " + film.getId() + " не найден");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(film);
        }
    }
}
