package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.NotFoundException;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private FilmStorage filmStorage;
    private UserStorage userStorage;

    @BeforeEach
    public void beforeEach() {
        filmStorage = new FilmDbStorage(jdbcTemplate);
        userStorage = new UserDbStorage(jdbcTemplate);
    }

    @Test
    void testCreate() throws NotFoundException {
        Film film = new Film(1, "film 1", "film 1 description", LocalDate.of(1990, 1, 1), 90, new Mpa(1, "G"));
        filmStorage.create(film);

        Film savedFilm = filmStorage.getFilmById(film.getId());
        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film);
    }

    @Test
    void testReturnFilms() throws NotFoundException {
        Film film1 = new Film(1, "film 1", "film 1 description", LocalDate.of(1991, 1, 1), 90, new Mpa(1, "G"));
        Film film2 = new Film(2, "film 2", "film 2 description", LocalDate.of(1992, 1, 1), 90, new Mpa(1, "G"));
        filmStorage.create(film1);
        filmStorage.create(film2);
        List<Film> savedFilms = filmStorage.returnFilms();
        savedFilms.sort(Comparator.comparingInt(Film::getId));
        assertThat(savedFilms.get(0)).isNotNull().usingRecursiveComparison().isEqualTo(film1);
        assertThat(savedFilms.get(1)).isNotNull().usingRecursiveComparison().isEqualTo(film2);
    }

    @Test
    void getFilmById() throws NotFoundException {
        Film film = new Film(1, "film 1", "film 1 description", LocalDate.of(1990, 1, 1), 90, new Mpa(1, "G"));
        filmStorage.create(film);

        Film savedFilm = filmStorage.getFilmById(film.getId());
        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film);
    }

    @Test
    void update() throws NotFoundException {
        Film film1 = new Film(1, "film 1", "film 1 description", LocalDate.of(1991, 1, 1), 90, new Mpa(1, "G"));
        filmStorage.create(film1);
        Film expectedFilm = new Film(1, "film 2", "film 2 description", LocalDate.of(1992, 1, 1), 90, new Mpa(1, "G"));
        film1.setName(expectedFilm.getName());
        film1.setDescription(expectedFilm.getDescription());
        film1.setReleaseDate(expectedFilm.getReleaseDate());
        film1.setDuration(expectedFilm.getDuration());
        film1.setMpa(expectedFilm.getMpa());
        filmStorage.update(film1);

        Film savedFilm = filmStorage.getFilmById(film1.getId());
        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(expectedFilm);
    }

    @Test
    void getPopularFilms() throws NotFoundException {
        Film film1 = new Film(1, "film1", "film1 description", LocalDate.of(1991, 1, 1), 90, new Mpa(1, "G"));
        Film film2 = new Film(2, "film2", "film2 description", LocalDate.of(1992, 1, 1), 90, new Mpa(1, "G"));
        Film film3 = new Film(3, "film3", "film3 description", LocalDate.of(1993, 1, 1), 90, new Mpa(1, "G"));

        User user1 = new User(1, "Ivan Petrov", "vanya123", "user@email.ru", LocalDate.of(1990, 1, 1));
        User user2 = new User(2, "Vasiliy Ivanov", "vasya123", "vasya@email.ru", LocalDate.of(1991, 1, 1));
        User user3 = new User(2, "Petr Sidorov", "petr123", "petr@email.ru", LocalDate.of(1992, 1, 1));

        filmStorage.create(film1);
        filmStorage.create(film2);
        filmStorage.create(film3);

        userStorage.create(user1);
        userStorage.create(user2);
        userStorage.create(user3);

        filmStorage.setLike(film1.getId(), user1.getId());

        filmStorage.setLike(film2.getId(), user1.getId());
        filmStorage.setLike(film2.getId(), user2.getId());

        filmStorage.setLike(film3.getId(), user1.getId());
        filmStorage.setLike(film3.getId(), user2.getId());
        filmStorage.setLike(film3.getId(), user3.getId());

        film1.addLike(1);
        film2.addLike(1);
        film2.addLike(2);
        film3.addLike(1);
        film3.addLike(2);
        film3.addLike(3);
        List<Film> expectedList = List.of(film3, film2, film1);

        List<Film> popularFilms = new LinkedList<>(filmStorage.getPopularFilms("3"));
        System.out.println(popularFilms);

        assertThat(popularFilms)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(expectedList);
    }

    @Test
    void setLike() throws NotFoundException {
        Film film = new Film(1, "film", "film description", LocalDate.of(1991, 1, 1), 90, new Mpa(1, "G"));
        filmStorage.create(film);
        User user = new User(1, "Ivan Petrov", "vanya123", "user@email.ru", LocalDate.of(1990, 1, 1));
        userStorage.create(user);

        filmStorage.setLike(film.getId(), user.getId());

        Film expectedFilm = new Film(1, "film", "film description", LocalDate.of(1991, 1, 1), 90, new Mpa(1, "G"));
        expectedFilm.addLike(1);

        Film savedFilm = filmStorage.getFilmById(film.getId());
        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(expectedFilm);
    }

    @Test
    void removeLike() throws NotFoundException {
        Film film = new Film(1, "film", "film description", LocalDate.of(1991, 1, 1), 90, new Mpa(1, "G"));
        filmStorage.create(film);
        User user = new User(1, "Ivan Petrov", "vanya123", "user@email.ru", LocalDate.of(1990, 1, 1));
        userStorage.create(user);

        filmStorage.setLike(film.getId(), user.getId());
        filmStorage.removeLike(film.getId(), user.getId());

        Film savedFilm = filmStorage.getFilmById(film.getId());
        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film);
    }
}