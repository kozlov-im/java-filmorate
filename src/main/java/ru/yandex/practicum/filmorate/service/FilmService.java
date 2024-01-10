package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.validation.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    public Film create(Film film) throws NotFoundException;

    public List<Film> returnFilms();

    public Film getFilmById(int id) throws NotFoundException;

    public Film update(Film film) throws NotFoundException;

    public Film setLike(int filmId, int userId) throws NotFoundException;

    public Film removeLike(int filmId, int userId) throws NotFoundException;

    public List<Film> getPopularFilms(String count);


}
