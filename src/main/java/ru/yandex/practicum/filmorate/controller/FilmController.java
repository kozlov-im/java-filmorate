package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validation.NotFoundException;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
public class FilmController {

    private FilmService filmService;
    private static final String DEFAULT_POPULAR_FILMS_AMOUNT = "10";

    @PostMapping("/films")
    public Film create(@RequestBody @Valid Film film) throws NotFoundException {
        return filmService.create(film);
    }

    @GetMapping("/films")
    public List<Film> returnFilms() {
        return filmService.returnFilms();
    }

    @GetMapping("/films/{filmId}")
    public Film getFilmById(@PathVariable int filmId) throws NotFoundException {
        return filmService.getFilmById(filmId);
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = DEFAULT_POPULAR_FILMS_AMOUNT) String count) {
        return filmService.getPopularFilms(count);
    }

    @PutMapping("/films")
    public Film update(@RequestBody @Valid Film film) throws NotFoundException {
        return filmService.update(film);
    }

    @PutMapping("/films/{filmId}/like/{userId}")
    public Film setLike(@PathVariable int filmId, @PathVariable int userId) throws NotFoundException {
        return filmService.setLike(filmId, userId);
    }

    @DeleteMapping("/films/{filmId}/like/{userId}")
    public Film removeLike(@PathVariable int filmId, @PathVariable int userId) throws NotFoundException {
        return filmService.removeLike(filmId, userId);
    }
}
