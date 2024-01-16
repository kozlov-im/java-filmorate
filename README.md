# java-filmorate
Template repository for Filmorate project.
## Диаграмма сущность-связь
<https://dbdiagram.io/d/filmorate-65a52674ac844320aef134cc>

## Cоздание БД и наполнение ее тестовыми данными
### Создание пользователя и базы данных
```
CREATE  user filmorate_user superuser
ALTER  user filmorate_user password '123'
CREATE database javaFilmorate with owner filmorate_use
```
### Создание таблиц БД
```
CREATE TABLE "users" (
  "id" integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, 
  "name" varchar(255),
  "login" varchar(30) UNIQUE,
  "email" varchar(30) UNIQUE,
  "bithdate" date
);

CREATE TYPE "status" AS ENUM ('0', '1', '2');

CREATE TABLE "friends" (
  "friend_id_one" integer,
  "friend_id_two" integer,
  "status" status default '0'
);

CREATE TABLE "films" (
  "id" integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  "name" varchar(255),
  "description" varchar(2048),
  "release_date" date,
  "duration" integer,
  "genre" integer,
  "rating_PMA_id" integer
);

CREATE TABLE "likes" (
  "film_id" integer,
  "user_id" integer
);

CREATE TABLE "genres" (
  "id" integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  "genre" varchar(255)
);

CREATE TABLE "genre_link" (
  "film_id" integer,
  "genre_id" integer
);

CREATE TABLE "rating_PMA" (
  "id" integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  "rating_PMA" varchar(255)
);

ALTER TABLE "friends" ADD FOREIGN KEY ("friend_id_one") REFERENCES "users" ("id");

ALTER TABLE "friends" ADD FOREIGN KEY ("friend_id_two") REFERENCES "users" ("id");

ALTER TABLE "likes" ADD FOREIGN KEY ("film_id") REFERENCES "films" ("id");

ALTER TABLE "likes" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id");

ALTER TABLE "genre_link" ADD FOREIGN KEY ("film_id") REFERENCES "films" ("id");

ALTER TABLE "genre_link" ADD FOREIGN KEY ("genre_id") REFERENCES "genres" ("id");

ALTER TABLE "films" ADD FOREIGN KEY ("rating_PMA_id") REFERENCES "rating_PMA" ("id");
```

### Наполнение БД тестовыми данными
```
INSERT INTO public.users(name, login, email, bithdate)
VALUES 
('Ivan', 'Ivan_login', 'ivan@mail.ru', '01.01.2001'),
('Vasiliy', 'Vasiliy_login', 'vasiliy@mail.ru', '01.01.2002'),
('Petr', 'Petr_login', 'petr@mail.ru', '01.01.2003'),
('Victor', 'Victor_login', 'victor@mail.ru', '01.01.2004'),
('Denis', 'Denis_login', 'denis@mail.ru', '01.01.2005');

INSERT INTO public.friends(friend_id_one, friend_id_two, status)
VALUES 
(1, 2, '2'),
(1, 3, '2'),
(2, 3, '2'),
(2, 4, '2');
	
INSERT INTO public.genres(genre)
VALUES
('Комедия'),
('Драма'),
('Мультфильм'),
('Триллер'),
('Документальный'),
('Боевик');


INSERT INTO public."rating_PMA"("rating_PMA")
VALUES 
('G'),
('PG'),
('PG-13'),
('R'),
('NC-17');


INSERT INTO public.films(name, description, release_date, duration, "rating_PMA_id")
VALUES 
('фильм 1', 'описание фильма 1', '01.01.1991', '65', 2),
('фильм 2', 'описание фильма 2', '01.01.1992', '70', 1),
('фильм 3', 'описание фильма 3', '01.01.1993', '75', 3),
('фильм 4', 'описание фильма 4', '01.01.1994', '80', 5),
('фильм 5', 'описание фильма 5', '01.01.1995', '90', 4);


INSERT INTO public.genre_link(film_id, genre_id)
VALUES 
(1, 4),
(1, 5),
(2, 1),
(3, 5),
(4, 2),
(1, 3);

INSERT INTO public.likes(film_id, user_id)
VALUES 
(1, 3),
(1, 4),
(2, 3),
(2, 1),
(2, 2);
```
## Проверка основных операции бизнес-логики
### Возвращение списка друзей пользователя
```
SELECT *
FROM users
WHERE id IN (
  SELECT friend_id_two
  FROM friends
  WHERE friend_id_one = 1 AND status = '2'
  UNION
  SELECT friend_id_one
  FROM friends
  WHERE friend_id_two = 1 AND status = '2'
	       )
```
### Возврашение списка общих друзей двух пользователей
```
SELECT *
FROM users
WHERE id IN (
            SELECT first_user_friends.friend_id_two common_friends
            FROM (
                  SELECT friend_id_two
                  FROM friends
                  WHERE friend_id_one = 2 AND status = '2'
                  UNION
                  SELECT friend_id_one
                  FROM friends
                  WHERE friend_id_two = 2 AND status = '2' 
                ) first_user_friends
          JOIN
               (
                 SELECT friend_id_one
                 FROM friends
                 WHERE friend_id_two = 3 AND status = '2'
                 UNION
                 SELECT friend_id_one
                 FROM friends
                 WHERE friend_id_two = 3 AND status = '2' 
               ) second_user_friends 
        ON first_user_friends.friend_id_two = second_user_friends.friend_id_one
           )
```
### Возвращает список из первых фильмов по количеству лайков
~~~
SELECT *
FROM films f
JOIN (
      SELECT film_id, count(film_id) likes_amount
      FROM likes
      GROUP BY film_id
     ) a
ON f.id = a.film_id
ORDER BY a.likes_amount DESC
LIMIT 10
~~~
