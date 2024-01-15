package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.validation.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film create(Film film);

    List<Film> returnFilms();

    Film getFilmById(int id) throws NotFoundException;

    Film update(Film film);

    List<Film> getPopularFilms(String count);
}
