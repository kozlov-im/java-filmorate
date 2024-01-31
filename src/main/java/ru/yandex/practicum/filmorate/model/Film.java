package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.validation.FilmRelease;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Film.
 */
@Data
public class Film {
    private int id;
    private Like like;
    private Set<Genre> genres = new HashSet<>();
    private Mpa mpa;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания может быть не более 200 символов")
    private String description;

    @FilmRelease(message = "Некорректная дата релиза")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность должна быть положительной")
    private int duration;

    public Film() {
    }

    public Film(int id, String name, String description, LocalDate releaseDate, int duration, Mpa mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }

    public void addGenre(Genre genre) {
        genres.add(genre);
    }

    public List<Genre> getGenres() {
        return genres.stream().sorted(Comparator.comparingInt(Genre::getId)).collect(Collectors.toList());
    }
}
