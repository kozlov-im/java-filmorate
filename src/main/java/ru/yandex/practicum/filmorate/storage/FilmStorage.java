package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.validation.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    public Film create(Film film);

    public List<Film> returnFilms();

    public Film getFilmById(int id) throws NotFoundException;

    public Film update(Film film);

    public List<Film> getPopularFilms(String count);
}
