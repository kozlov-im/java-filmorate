package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.validation.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    Film create(Film film) throws NotFoundException;

    List<Film> returnFilms();

    Film getFilmById(int id) throws NotFoundException;

    Film update(Film film) throws NotFoundException;

    Film setLike(int filmId, int userId) throws NotFoundException;

    Film removeLike(int filmId, int userId) throws NotFoundException;

    List<Film> getPopularFilms(String count);


}
