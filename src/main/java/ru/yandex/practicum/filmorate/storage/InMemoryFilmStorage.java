package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.validation.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Integer, Film> films = new HashMap<>();

    @Override
    public Film create(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Film> returnFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(int id) throws NotFoundException {
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
    }

    public Film update(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return film;
        } else {
            return null;
        }
    }

    @Override
    public List<Film> getPopularFilms(String count) {
        List<Film> filmsList = new ArrayList<>(films.values());
        Collections.sort(filmsList, new Comparator<Film>() {
            @Override
            public int compare(Film o1, Film o2) {
                return o2.getLikes().size() - o1.getLikes().size();
            }
        });
        return filmsList.stream().limit(Integer.parseInt(count)).collect(Collectors.toList());
    }
}
