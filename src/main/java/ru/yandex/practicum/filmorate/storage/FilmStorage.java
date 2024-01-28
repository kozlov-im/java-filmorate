package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.validation.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film create(Film film) throws NotFoundException;

    List<Film> returnFilms() throws NotFoundException;

    Film getFilmById(int id) throws NotFoundException;

    Film update(Film film) throws NotFoundException;

    List<Film> getPopularFilms(String count) throws NotFoundException;

    Film setLike(int filmId, int userId) throws NotFoundException;

    Film removeLike(int filmId, int userId) throws NotFoundException;
}
