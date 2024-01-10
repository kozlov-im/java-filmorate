package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.validation.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class FilmServiceImpl implements FilmService {
    private FilmStorage filmStorage;
    private UserService userService;
    private int generatedId = 1;

    public FilmServiceImpl(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    @Override
    public Film create(Film film) throws NotFoundException {
        Set<Integer> usersLikes = film.getLikes();
        if (usersLikes != null) {
            for (Integer userId : usersLikes) {
                userService.getUserById(userId);
            }
        }
        film.setId(generatedId++);
        filmStorage.create(film);
        log.info("Фильм с id = " + film.getId() + " успешно добавлен");
        return film;
    }

    @Override
    public List<Film> returnFilms() {
        return filmStorage.returnFilms();
    }

    @Override
    public Film getFilmById(int id) throws NotFoundException {
        return filmStorage.getFilmById(id);
    }

    @Override
    public Film update(Film film) throws NotFoundException {
        Film returnedFilm = filmStorage.update(film);
        if (returnedFilm != null) {
            log.info("Фильм с id = " + film.getId() + " успешно обновлен");
            return returnedFilm;
        } else {
            log.info("Фильм с id = " + film.getId() + " не найден");
            throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
        }
    }

    @Override
    public Film setLike(int filmId, int userId) throws NotFoundException {
        Film film = getFilmById(filmId);
        userService.getUserById(userId);
        film.addLike(userId);
        log.info("Пользователь id = " + userId + " установил лайк фильму id = " + filmId);
        return film;
    }

    @Override
    public Film removeLike(int filmId, int userId) throws NotFoundException {
        Film film = getFilmById(filmId);
        userService.getUserById(userId);
        film.removeLike(userId);
        log.info("Пользователь id = " + userId + " удалил лайк фильму id = " + filmId);
        return film;
    }

    @Override
    public List<Film> getPopularFilms(String count) {
        return filmStorage.getPopularFilms(count);
    }
}
