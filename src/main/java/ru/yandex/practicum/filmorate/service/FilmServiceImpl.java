package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.LikeDbStorage;
import ru.yandex.practicum.filmorate.validation.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validation.ValidationException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final LikeDbStorage likeDbStorage;
    private final GenreDbStorage genreDbStorage;

    public FilmServiceImpl(FilmStorage filmStorage, UserService userService, LikeDbStorage likeDbStorage, GenreDbStorage genreDbStorage) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.likeDbStorage = likeDbStorage;
        this.genreDbStorage = genreDbStorage;
    }

    @Override
    public Film create(Film film) throws NotFoundException {
        Film createdFilm = filmStorage.create(film);
        log.info("Фильм с id = " + film.getId() + " успешно добавлен");
        return createdFilm;
    }

    @Override
    public List<Film> getAllFilms() throws NotFoundException {
        List<Film> filmList = filmStorage.getAllFilms();
        filmList.stream().forEach(film -> film.setGenres(new HashSet<>(genreDbStorage.getGenresForFilm(film.getId()))));
        List<Integer> filmsIdList = new ArrayList<>();
        filmList.stream().forEach(film -> filmsIdList.add(film.getId()));
        log.info("Список фильмов с id = " + filmsIdList + " успешно получен");
        return filmList;
    }

    @Override
    public Film getFilmById(int id) throws NotFoundException {
        Film film = filmStorage.getFilmById(id);
        film.setLike(likeDbStorage.getLikes(film.getId()));
        log.info("Фильм с id = " + film.getId() + " успешно получен");
        return film;
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
    public Film setLike(int filmId, int userId) throws NotFoundException, ValidationException {
        Film film = getFilmById(filmId);
        userService.getUserById(userId);
        if (film.getLike() == null) {
            film.setLike(likeDbStorage.setLike(filmId, userId));
            return film;
        } else if (!film.getLike().getUsersLikes().contains(userId)) {
            film.setLike(likeDbStorage.setLike(filmId, userId));
            log.info("Пользователь id = " + userId + " установил лайк фильму id = " + filmId);
            return film;

        } else {
            throw new ValidationException("Пользователь id = " + userId + " уже установил лайк фильму id = " + filmId);
        }
    }

    @Override
    public Film removeLike(int filmId, int userId) throws NotFoundException {
        getFilmById(filmId);
        userService.getUserById(userId);
        likeDbStorage.removeLike(filmId, userId);
        log.info("Пользователь id = " + userId + " удалил лайк фильму id = " + filmId);
        return getFilmById(filmId);
    }

    @Override
    public List<Film> getPopularFilms(String count) throws NotFoundException {
        List<Film> filmList = filmStorage.getPopularFilms(count);
        List<Integer> filmsIdList = new ArrayList<>();
        filmList.stream().forEach(film -> filmsIdList.add(film.getId()));
        log.info("Список популярных фильмов с id = " + filmsIdList + " успешно получен");
        return filmList;
    }
}
