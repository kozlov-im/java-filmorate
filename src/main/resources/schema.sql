DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS films CASCADE;
DROP TABLE IF EXISTS friends;
DROP TABLE IF EXISTS genre_link;
DROP TABLE IF EXISTS genres;
DROP TABLE IF EXISTS likes;
DROP TABLE IF EXISTS mpa;

CREATE TABLE IF NOT EXISTS users (
  id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name varchar(255),
  login varchar(30) UNIQUE,
  email varchar(30) UNIQUE,
  birthday date
);

CREATE TABLE IF NOT EXISTS friends (
  user_id integer REFERENCES users (id) ON DELETE CASCADE,
  friend_id integer REFERENCES users (id) ON DELETE CASCADE,
  status integer
);

CREATE TABLE IF NOT EXISTS mpa (
  id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  mpa varchar(255)
);

CREATE TABLE IF NOT EXISTS films (
  id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name varchar(255),
  description varchar(2048),
  release_date date,
  duration integer,
  mpa_id integer  REFERENCES mpa (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS likes (
  film_id integer REFERENCES films (id) ON DELETE CASCADE,
  user_id integer REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS genres (
  id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  genre varchar(255)
);

CREATE TABLE IF NOT EXISTS genre_link (
  film_id integer REFERENCES films (id) ON DELETE CASCADE,
  genre_id integer REFERENCES genres (id) ON DELETE CASCADE
);