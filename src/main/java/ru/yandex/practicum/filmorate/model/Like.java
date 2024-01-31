package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class Like {
    private int filmId;
    Set<Integer> usersLikes = new HashSet<>();

    public void addLike(int id) {
        usersLikes.add(id);
    }
}
